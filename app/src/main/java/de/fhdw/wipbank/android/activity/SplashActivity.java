package de.fhdw.wipbank.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import de.fhdw.wipbank.android.R;
import de.fhdw.wipbank.android.rest.AccountAsyncTask;

/**
 * SplashActivity: Wird beim Start der App angezeigt. Vollbild Splash-Screen mit App-Logo in der Mitte.
 * Steuert, ob OnFirstStartActivity oder MainActivity aufgerufen wird.
 *
 * OnFirstStartActivity: Beim aller ersten Start (AccountNumber leer)
 * MainActivity: Sonst
 *
 */
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
            Intent intent = new Intent(this, OnFirstStartActivity.class);
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
        // Normaler Start der App
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }
}