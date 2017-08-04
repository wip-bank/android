package de.fhdw.wipbank.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.fhdw.wipbank.android.model.Account;
import de.fhdw.wipbank.android.model.Transaction;


public class TransactionAsyncTask {

    private static OnTransactionExecuteListener listener;

    /**
     * This interface must be implemented by classes that use the TransactionAsyncTask
     */
    public interface OnTransactionExecuteListener {
        void onTransactionSuccess();
        void onTransactionError(String errorMsg);
    }

    /**
     * @param transaction Zu tätigende Transaktion
     *
     * @param caller Caller wird benutzt um zu verifizieren, ob die aufrufende Klasse
     *               das Interface OnAccountUpdatedListener implementiert hat.
     *
     * @param context Bei einer Activity = caller. Context wird für weitere Methoden verwendet.
     */
    public static void executeTransaction(final Transaction transaction, Object caller, Context context) {

        if (caller instanceof OnTransactionExecuteListener) {
            listener = (OnTransactionExecuteListener) caller;
        } else {
            throw new RuntimeException(caller.toString()
                    + " must implement OnTransactionExecuteListener");
        }


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String accountNumber = prefs.getString(context.getString(R.string.pref_accountNumber_key), "");

        new AsyncTask<String, Void, String>(){

            @Override
            protected String doInBackground(String... params) {
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(params[0]);
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("senderNumber", transaction.getSender().getNumber()));
                    nameValuePairs.add(new BasicNameValuePair("receiverNumber", transaction.getReceiver().getNumber()));
                    nameValuePairs.add(new BasicNameValuePair("amount", String.valueOf(transaction.getAmount())));
                    nameValuePairs.add(new BasicNameValuePair("reference", transaction.getReference()));
                    UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
                    httppost.setEntity( encodedFormEntity );

                    HttpResponse response = httpclient.execute(httppost);

                    //Prüfung, ob der Response null ist. Falls ja (z.B. falls keine Verbindung zum Server besteht)
                    //soll die Methode direkt verlassen und null zurückgegeben werden
                    if(response == null) return null;


                    String op = EntityUtils.toString(response.getEntity(), "UTF-8");//The response you get from your script
                    return op;



                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String response) {
                if(response != null && response != ""){
                    listener.onTransactionSuccess();
                }else{
                    listener.onTransactionError("Fehler");
                }

            }
            //Einbindung der Parameter über Platzhalter in den URL-String
            //}.execute("http://10.0.2.2:9998/rest/transaction");  // Localhost
        }.execute("http://192.168.43.182:9998/rest/transaction"); // Daniels Laptop



    }


}
