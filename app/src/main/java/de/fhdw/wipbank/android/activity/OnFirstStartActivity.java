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
import de.fhdw.wipbank.android.rest.AccountAsyncTask;
import de.fhdw.wipbank.android.util.Validation;

/**
 * OnFirstStartActivity: Diese Activity wird beim aller ersten Start der App aufgerufen. Hier werden AccountNumber und IP (opt. mit Port) vom Benutzer eingegeben).
 */
public class OnFirstStartActivity extends AppCompatActivity implements AccountAsyncTask.OnAccountUpdateListener {

    EditText edtAccountNumber;
    EditText edtServerIP;
    Button btnSave;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    /** onCreate-Methode
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_first_start);

        edtAccountNumber = (EditText) findViewById(R.id.edtAccountNumber);
        edtServerIP = (EditText) findViewById(R.id.edtServerIP);
        btnSave = (Button) findViewById(R.id.btnSave);
    }

    /** btnSaveOnClick: Diese Methode wird aufgerufen, wenn der Benutzer auf den Speichern-Button klickt.
     * Es wird zunächst geprüft, ob das Format der IP valide ist. Dann werden AccountNumber und IP in den
     * SharedPreferences gespeichert. Abschließend wird der REST-Service /account/ aufgerufen.
     * @param view
     */
    public void btnSaveOnClick(View view) {

        // Validierung der IP (optional mit Port)
        String ip = edtServerIP.getText().toString();
        if (!Validation.isIPValid(ip)) {
            Toast.makeText(OnFirstStartActivity.this, "IP ungültig", Toast.LENGTH_SHORT).show();
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

    /** onAccountUpdateSuccess: Wurde der REST-Service /account/ mit der IP und dem Account erfolgreich aufgerufen
     * , so wird nun die MainActivity gestartet.
     */
    @Override
    public void onAccountUpdateSuccess() {
        // Start der MainActivity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    /** onAccountUpdateError: Wurde der REST-Service /account/ mit der IP und dem Account fehlerhaft aufgerufen
     * , so wird eine Fehlermeldung angezeigt.
     * @param errorMsg anzuzeigende Fehlermeldung
     */
    @Override
    public void onAccountUpdateError(String errorMsg) {
        editor.clear().apply();
        Toast.makeText(OnFirstStartActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }
}
