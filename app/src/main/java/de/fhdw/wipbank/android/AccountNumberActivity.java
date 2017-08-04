package de.fhdw.wipbank.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AccountNumberActivity extends AppCompatActivity implements AccountAsyncTask.OnAccountUpdatedListener {

    EditText edtAccountNumber;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_number);

        edtAccountNumber = (EditText) findViewById(R.id.edtAccountNumber);
        btnSave = (Button) findViewById(R.id.btnSave);
    }

    public void btnSaveOnClick(View view) {

        // Account-Number speichern
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_accountNumber_key), edtAccountNumber.getText().toString());
        editor.apply();

        // Account über REST Service laden
        AccountAsyncTask.updateAccount(this, this);
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
        Toast.makeText(AccountNumberActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }
}
