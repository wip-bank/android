package de.fhdw.wipbank.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity implements AccountAsyncTask.OnAccountUpdatedListener {

    private String accountNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        accountNumber = prefs.getString(getString(R.string.pref_accountNumber_key), "");


        if(accountNumber != ""){
            // Account über REST Service laden
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