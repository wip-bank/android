package de.fhdw.wipbank.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TransactionFragment.OnFragmentInteractionListener, NewTransactionFragment.OnFragmentInteractionListener{


    String accountNumber;
    Account account;


    TransactionFragment transactionFragment;
    NewTransactionFragment newTransactionFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        accountNumber = prefs.getString(getString(R.string.pref_accountNumber_key), "");
        //getAccount();

        Intent intent = getIntent();
        account = AccountService.getAccount();

        System.out.println("Main: " + account);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Fragments initialisieren
        transactionFragment = TransactionFragment.newInstance();
        newTransactionFragment = NewTransactionFragment.newInstance();


        // Startfragment -> Transactions
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,  transactionFragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_settings:
                return true;

            case R.id.menu_refresh:
                transactionFragment.swipeRefreshLayout.setRefreshing(true);
                transactionFragment.update();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_transactions) {
            fragmentTransaction.replace(R.id.fragment_container,  transactionFragment);
        } else if (id == R.id.nav_new_transaction) {
            fragmentTransaction.replace(R.id.fragment_container,  newTransactionFragment);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void getAccount() {
        new AsyncTask<String, Void, Pair<String, Integer>>(){

            @Override
            protected Pair<String, Integer> doInBackground(String... params) {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(params[0]);
                    HttpResponse response = httpClient.execute(httpGet);

                    //Prüfung, ob der Response null ist. Falls ja (z.B. falls keine Verbindung zum Server besteht)
                    //soll die Methode direkt verlassen und null zurückgegeben werden
                    if(response == null) return null;
                    int responseCode = response.getStatusLine().getStatusCode();
                    //Prüfung, ob der ResponseCode OK ist, damit ein JSON-String erwartet und verarbeitet werden kann
                    if(responseCode == HttpStatus.SC_OK){
                        String json = EntityUtils.toString(response.getEntity());
                        return Pair.create(json, responseCode);
                    }
                    //Falls der ResponseCode nicht OK ist, wird nur der ResponseCode zurückgegeben
                    else {
                        return Pair.create(null, responseCode);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Pair<String, Integer> responsePair) {
                //Falls das Pair nicht null (und damit der Response auch nicht null war) sowie
                //der JSON-String im Pair nicht null ist, kann weitergearbeitet werden
                if(responsePair != null && responsePair.first != null) {
                    Gson gson = new GsonBuilder().create();
                    //JSON in Java-Objekt konvertieren
                    account = gson.fromJson(responsePair.first, Account.class);
                }
                //Falls kein JSON-String geliefert wird, wird dem Benutzer hier eine Fehlermeldung ausgegeben
                else {
                    Toast.makeText(
                            MainActivity.this, "Response: " + (responsePair != null ?
                                    String.valueOf(responsePair.second) : "null"), Toast.LENGTH_SHORT).show();
                }
            }
            //Einbindung der Parameter über Platzhalter in den URL-String
        }.execute(String.format("http://10.0.2.2:9998/rest/account/%s/", accountNumber));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}

