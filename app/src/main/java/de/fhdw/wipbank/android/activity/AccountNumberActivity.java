package de.fhdw.wipbank.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.fhdw.wipbank.android.R;
import de.fhdw.wipbank.android.account.AccountAsyncTask;
import de.fhdw.wipbank.android.util.Validation;

public class AccountNumberActivity extends AppCompatActivity implements AccountAsyncTask.OnAccountUpdateListener {

    EditText edtAccountNumber;
    EditText edtServerIP;
    Button btnSave;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_number);

        edtAccountNumber = (EditText) findViewById(R.id.edtAccountNumber);
        edtServerIP = (EditText) findViewById(R.id.edtServerIP);
        btnSave = (Button) findViewById(R.id.btnSave);
    }

    public void btnSaveOnClick(View view) {


        // Validierung der IP (optional mit Port)
        String ip = edtServerIP.getText().toString();
        if (!Validation.isIPValid(ip)){
            Toast.makeText(AccountNumberActivity.this, "IP ungültig", Toast.LENGTH_SHORT).show();
            return;
        }





        // Account-Number speichern
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
        editor.putString(getString(R.string.pref_accountNumber_key), edtAccountNumber.getText().toString());
        editor.putString(getString(R.string.pref_server_ip_key), ip);
        editor.apply();
        // Account über REST Service laden
        new AccountAsyncTask(this, this).execute();
    }

    @Override
    public void onAccountUpdateSuccess() {
        // Start der MainActivity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAccountUpdateError(String errorMsg) {
        editor.clear().apply();
        String toastMsg;
        if (errorMsg.equals("null")){
            toastMsg = "Keine Verbindung zum Server";
        }else{
            toastMsg = errorMsg;
        }
        Toast.makeText(AccountNumberActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
    }
}
