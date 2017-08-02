package de.fhdw.wipbank.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import de.fhdw.wipbank.android.model.Account;

public class SplashActivity extends AppCompatActivity {

    private String accountNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        accountNumber = prefs.getString(getString(R.string.pref_accountNumber_key), "");


        if(accountNumber != ""){
            getAccount();

        }else{

            // Neue Installation -> Eingabe einer Konto-Nummer
            Intent intent = new Intent(this, AccountNumberActivity.class);
            startActivity(intent);

            finish();
        }
    }

    public void getAccount() {
        new AsyncTask<String, Void, Pair<String, Integer>>(){

            @Override
            protected Pair<String, Integer> doInBackground(String... params) {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(params[0]);
                    HttpResponse response = httpClient.execute(httpGet);

                    //Prüfung, ob der Response null ist. Falls ja (z.B. falls keine Verbindung zum Server besteht)
                    //soll die Methode direkt verlassen und null zurückgegeben werden
                    if(response == null) return null;
                    int responseCode = response.getStatusLine().getStatusCode();
                    //Prüfung, ob der ResponseCode OK ist, damit ein JSON-String erwartet und verarbeitet werden kann
                    if(responseCode == HttpStatus.SC_OK){
                        String json = EntityUtils.toString(response.getEntity());
                        return Pair.create(json, responseCode);
                    }
                    //Falls der ResponseCode nicht OK ist, wird nur der ResponseCode zurückgegeben
                    else {
                        return Pair.create(null, responseCode);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Pair<String, Integer> responsePair) {
                Account account;
                //Falls das Pair nicht null (und damit der Response auch nicht null war) sowie
                //der JSON-String im Pair nicht null ist, kann weitergearbeitet werden
                if(responsePair != null && responsePair.first != null) {
                    Gson gson = new GsonBuilder().create();
                    //JSON in Java-Objekt konvertieren
                    account = gson.fromJson(responsePair.first, Account.class);
                    AccountService.setAccount(account);
                    // Normaler Start der App
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();


                }
                //Falls kein JSON-String geliefert wird, wird dem Benutzer hier eine Fehlermeldung ausgegeben
                else {
                    Toast.makeText(
                            SplashActivity.this, "Response: " + (responsePair != null ?
                                    String.valueOf(responsePair.second) : "null"), Toast.LENGTH_SHORT).show();
                }
            }
            //Einbindung der Parameter über Platzhalter in den URL-String
        }.execute(String.format("http://10.0.2.2:9998/rest/account/%s/", accountNumber));
    }
}