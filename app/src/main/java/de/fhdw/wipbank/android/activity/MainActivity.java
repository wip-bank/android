package de.fhdw.wipbank.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import de.fhdw.wipbank.android.R;
import de.fhdw.wipbank.android.account.AccountService;
import de.fhdw.wipbank.android.model.Account;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TransactionFragment.OnFragmentInteractionListener, NewTransactionFragment.OnFragmentInteractionListener{


    String accountNumber;
    Account account;


    TransactionFragment transactionFragment;
    NewTransactionFragment newTransactionFragment;

    NavigationView navigationView;

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_transactions);

        // Fragments initialisieren
        transactionFragment = TransactionFragment.newInstance();
        newTransactionFragment = NewTransactionFragment.newInstance();


        // Startfragment -> Transactions
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,  transactionFragment);
        //fragmentTransaction.addToBackStack(null);

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


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public void onTransactionExecute() {
        Snackbar.make(findViewById(R.id.coordinatorLayout), "Transaktion erfolgreich", Snackbar.LENGTH_LONG).show();
        transactionFragment.update();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,  transactionFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        navigationView.setCheckedItem(R.id.nav_transactions);



    }
}

