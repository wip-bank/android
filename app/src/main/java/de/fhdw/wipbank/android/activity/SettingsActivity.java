package de.fhdw.wipbank.android.activity;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import de.fhdw.wipbank.android.R;
import de.fhdw.wipbank.android.rest.AccountAsyncTask;
import de.fhdw.wipbank.android.util.Validation;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.fragment_container_settings, new SettingsFragment()).commit();

    }

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_accountNumber_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_server_ip_key)));
        }

        /**
         * Attaches a listener so the summary is always updated with the preference value.
         * Also fires the listener once, to initialize the summary (so it shows up before the value
         * is changed.)
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(this);

            // Trigger the listener immediately with the preference's
            // current value.
            onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();


            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list (since they have separate labels/values).
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);

                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }

            } else {
                // For other preferences, set the summary to the value's simple string representation.


                String key = preference.getKey();
                if (key.equals(getString(R.string.pref_accountNumber_key))) {
                    boolean accountNumberValid = new AccountAsyncTask.OnAccountUpdateListener() {

                        /**
                         * Versuch /rest/account aufzurufen mit neuer AccountNumber
                         * Es wird auf das Ergebnis des AccountAsyncTasks mittels accountAsyncTask.execute.get() gewartet
                         * @param accountNumber
                         * @return valid / invalid
                         */
                        public boolean isAccountNumberValid(String accountNumber) {
                            AccountAsyncTask accountAsyncTask = new AccountAsyncTask(this, getContext());
                            accountAsyncTask.setAccountNumber(accountNumber);
                            try {
                                Pair<String, String> responsePair = accountAsyncTask.execute().get();

                                if (responsePair == null)
                                    return false; // Keine Antwort vom Server
                                else
                                    // Wenn ein JSON zurück kommt, dann existiert der Account
                                    return responsePair.first != null;
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                                return false;
                            }

                        }

                        @Override
                        public void onAccountUpdateSuccess() {

                        }

                        @Override
                        public void onAccountUpdateError(String errorMsg) {
                            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }.isAccountNumberValid(stringValue);

                    if (!accountNumberValid) {
                        return false;
                    }

                } else if (key.equals(getString(R.string.pref_server_ip_key))) {
                    // Validierung der IP (optional mit Port)
                    if (!Validation.isIPValid(stringValue)) {
                        Toast.makeText(getContext(), "IP ungültig", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    boolean server_ipValid = new AccountAsyncTask.OnAccountUpdateListener() {

                        /**
                         * Versuch /rest/account aufzurufen mit neuer Server-IP
                         * Es wird auf das Ergebnis des AccountAsyncTasks mittels accountAsyncTask.execute.get() gewartet
                         * @param server_ip
                         * @return valid / invalid
                         */
                        public boolean isServer_ipValid(String server_ip) {
                            AccountAsyncTask accountAsyncTask = new AccountAsyncTask(this, getContext());
                            accountAsyncTask.setServer_ip(server_ip);
                            try {
                                Pair<String, String> responsePair = accountAsyncTask.execute().get();
                                // Wenn eine Response zurück kommt, dann ist die Server-IP valide
                                return responsePair != null;
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                                return false;
                            }

                        }

                        @Override
                        public void onAccountUpdateSuccess() {

                        }

                        @Override
                        public void onAccountUpdateError(String errorMsg) {
                            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }.isServer_ipValid(stringValue);

                    if (!server_ipValid) {
                        return false;
                    }

                }

                preference.setSummary(stringValue);
            }

            return true;
        }
    }

    /**
     * react to the user tapping the back/up icon in the action bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
//                onBackPressed();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
