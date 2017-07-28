package de.fhdw.wipbank.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String accountNumber = prefs.getString(getString(R.string.pref_accountNumber_key), "");

        if(accountNumber != ""){

            // Normaler Start der App
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else{
            // Neue Installation -> Eingabe einer Konto-Nummer
            Intent intent = new Intent(this, AccountNumberActivity.class);
            startActivity(intent);
        }
        finish();
    }
}