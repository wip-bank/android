package de.fhdw.wipbank.android.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import de.fhdw.wipbank.android.R;
import de.fhdw.wipbank.android.model.Account;
import de.fhdw.wipbank.android.service.AccountService;

public class AccountAsyncTask extends AsyncTask<Void, Void, Pair<String, String>> {

    private String url;
    private OnAccountUpdateListener listener;
    private Context context;
    private SharedPreferences sharedPreferences;
    private String accountNumber;
    private final String RESTSTANDARDPORT = "9998";
    private final String URL_TEMPLATE = "http://%s/rest/account/%s/";

    /**
     * This interface must be implemented by classes that use the AccountAsyncTask
     */
    public interface OnAccountUpdateListener {
        void onAccountUpdateSuccess();

        void onAccountUpdateError(String errorMsg);
    }

    public AccountAsyncTask(Object caller, Context context) {
        if (caller instanceof AccountAsyncTask.OnAccountUpdateListener) {
            listener = (AccountAsyncTask.OnAccountUpdateListener) caller;
        } else {
            throw new RuntimeException(caller.toString()
                    + " must implement OnAccountUpdateListener");
        }

        this.context = context;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        accountNumber = sharedPreferences.getString(context.getString(R.string.pref_accountNumber_key), "");

        setUrl(sharedPreferences.getString(context.getString(R.string.pref_server_ip_key), ""));
    }

    @Override
    protected Pair<String, String> doInBackground(Void... params) {
        try {
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.
            // The default value is zero, that means the timeout is not used.
            int timeoutConnection = 1500;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 3000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);

            //Prüfung, ob der ErrorResponse null ist. Falls ja (z.B. falls keine Verbindung zum Server besteht)
            //soll die Methode direkt verlassen und null zurückgegeben werden
            if (response == null) return null;
            int responseCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            //Prüfung, ob der ResponseCode OK ist, damit ein JSON-String erwartet und verarbeitet werden kann
            if (responseCode == HttpStatus.SC_OK) {

                return Pair.create(responseString, null);
            }
            //Falls der ResponseCode nicht OK ist, wird nur der ResponseCode zurückgegeben
            else {
                return Pair.create(null, responseString);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onPostExecute(Pair<String, String> responsePair) {
        Account account;
        //Falls das Pair nicht null (und damit der ErrorResponse auch nicht null war) sowie
        //der JSON-String im Pair nicht null ist, kann weitergearbeitet werden
        if (responsePair != null && responsePair.first != null) {
            Gson gson = new GsonBuilder().create();
            //JSON in Java-Objekt konvertieren
            account = gson.fromJson(responsePair.first, Account.class);
            AccountService.setAccount(account);

            // Notify everybody that may be interested.
            if (listener != null) {
                listener.onAccountUpdateSuccess();
            }

            // Backup vom Account (JSON) erstellen. Das Backup wird bei Betrieb ohne Verbindung zum Server als Datengrundlage verwendet
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(context.getString(R.string.pref_backup_account_key), responsePair.first);
            editor.apply();

        }
        //Falls kein JSON-String geliefert wird, wird dem Benutzer hier eine Fehlermeldung ausgegeben
        else {

            Gson gson = new GsonBuilder().create();
            //Backup aus SharedPreferences laden (JSON in Java-Objekt konvertieren)
            String backupAccount = sharedPreferences.getString(context.getString(R.string.pref_backup_account_key), "");
            if (!backupAccount.equals("")) {
                account = gson.fromJson(backupAccount, Account.class);
                AccountService.setAccount(account);
            }


            String errorMsg = null;
            if (responsePair != null) {
                errorMsg = responsePair.second;
            }

            if (errorMsg == null)
                errorMsg = "Keine Verbindung zum Server";

            // Notify everybody that may be interested.
            if (listener != null) {
                listener.onAccountUpdateError(errorMsg);
            }
        }
    }

    public void setUrl(String ip) {
        if (!ip.contains(":")){
            ip = ip + ":" + RESTSTANDARDPORT;
        }
        url = String.format(URL_TEMPLATE, ip, accountNumber);
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
