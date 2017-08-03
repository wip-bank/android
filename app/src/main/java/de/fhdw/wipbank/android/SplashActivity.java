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
import de.fhdw.wipbank.android.model.AccountAsyncTask;

public class SplashActivity extends AppCompatActivity implements AccountAsyncTask.OnAccountUpdatedListener {

    private String accountNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        accountNumber = prefs.getString(getString(R.string.pref_accountNumber_key), "");


        if(accountNumber != ""){
            AccountAsyncTask.updateAccount(this, this);
        }else{

            // Neue Installation -> Eingabe einer Konto-Nummer
            Intent intent = new Intent(this, AccountNumberActivity.class);
            startActivity(intent);

            finish();
        }
    }


    @Override
    public void onAccountUpdateSuccess() {
        // Normaler Start der App
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }

    @Override
    public void onAccountUpdateError(String errorMsg) {
        Toast.makeText(SplashActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }
}