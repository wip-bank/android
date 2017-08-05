package de.fhdw.wipbank.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import de.fhdw.wipbank.android.R;
import de.fhdw.wipbank.android.account.AccountAsyncTask;

public class SplashActivity extends AppCompatActivity implements AccountAsyncTask.OnAccountUpdateListener {

    private String accountNumber;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        accountNumber = prefs.getString(getString(R.string.pref_accountNumber_key), "");


        if(accountNumber != ""){
            // Account über REST Service laden
            new AccountAsyncTask(this, this).execute();
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
        String toastMsg;
        if (errorMsg.equals("null")){
            toastMsg = "Keine Verbindung zum Server";
        }else{
            toastMsg = "Response: " + errorMsg;
        }
        Toast.makeText(SplashActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
        // Normaler Start der App
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }


}