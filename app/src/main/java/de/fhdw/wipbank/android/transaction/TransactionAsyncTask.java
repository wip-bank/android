package de.fhdw.wipbank.android.transaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Pair;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.fhdw.wipbank.android.R;
import de.fhdw.wipbank.android.model.Transaction;


public class TransactionAsyncTask extends AsyncTask<Void, Void, HttpResponse> {


    private String url;
    private OnTransactionExecuteListener listener;
    private Context context;
    private SharedPreferences sharedPreferences;
    private String accountNumber;
    private Transaction transaction;

    /**
     * This interface must be implemented by classes that use the TransactionAsyncTask
     */
    public interface OnTransactionExecuteListener {
        void onTransactionSuccess();
        void onTransactionError(String response);
    }

    public TransactionAsyncTask(Transaction transaction, Object caller, Context context){
        if (caller instanceof OnTransactionExecuteListener) {
            listener = (OnTransactionExecuteListener) caller;
        } else {
            throw new RuntimeException(caller.toString()
                    + " must implement OnTransactionExecuteListener");
        }

        this.context = context;
        this.transaction = transaction;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        accountNumber = sharedPreferences.getString(context.getString(R.string.pref_accountNumber_key), "");

        url = "http://192.168.178.38:9998/rest/transaction"; //  "http://10.0.2.2:9998/rest/transaction" für Localhost / Daniels Laptop: 192.168.43.182:9998
    }

    @Override
    protected HttpResponse doInBackground(Void... params) {
        try {
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.
            // The default value is zero, that means the timeout is not used.
            int timeoutConnection = 3000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 5000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("senderNumber", transaction.getSender().getNumber()));
            nameValuePairs.add(new BasicNameValuePair("receiverNumber", transaction.getReceiver().getNumber()));
            nameValuePairs.add(new BasicNameValuePair("amount", String.valueOf(transaction.getAmount())));
            nameValuePairs.add(new BasicNameValuePair("reference", transaction.getReference()));
            UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
            httppost.setEntity(encodedFormEntity);

            HttpResponse response = httpClient.execute(httppost);

            //Prüfung, ob der Response null ist. Falls ja (z.B. falls keine Verbindung zum Server besteht)
            //soll die Methode direkt verlassen und null zurückgegeben werden
            if (response == null) return null;


            return response;


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(HttpResponse response) {
        if (listener == null)
            return;

        if (response == null){
            listener.onTransactionError("Keine Verbindung zum Server");
            return;
        }
        int responseCode = response.getStatusLine().getStatusCode();



        if (responseCode == HttpStatus.SC_OK) {
            listener.onTransactionSuccess();
        }
        else {
            try {
                String responseMsg = EntityUtils.toString(response.getEntity(), "UTF-8");
                listener.onTransactionError(responseMsg);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

}