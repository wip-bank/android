package de.fhdw.wipbank.android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import de.fhdw.wipbank.android.R;
import de.fhdw.wipbank.android.fragment.NewTransactionFragment;
import de.fhdw.wipbank.android.fragment.TransactionFragment;


/**
 * MainActivity: Haupt-Activity der App, von hier aus werden alle weiteren Programmteile angesteuert.
 *
 * @author Daniel Sawenko
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TransactionFragment.OnFragmentInteractionListener, NewTransactionFragment.OnFragmentInteractionListener {

    private TransactionFragment transactionFragment;

    private NavigationView navigationView;

    /**
     * OnCreate-Methode
     * @param savedInstanceState SavedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_transactions);

        // Fragment initialisieren
        transactionFragment = TransactionFragment.newInstance();

        // Startfragment -> Transactions
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, transactionFragment);

        fragmentTransaction.commit();
    }

    /*@Override
    protected void onRestart() {
        super.onRestart();
        // Update der Transaktions
        transactionFragment.loadTransactions();
    }*/

    /**
     * onBackPressed-Methode
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            FragmentManager manager = getSupportFragmentManager();
            if(manager.getBackStackEntryCount() > 0) {
                super.onBackPressed();
                Fragment currentFragment = manager.findFragmentById(R.id.fragment_container);
                if(currentFragment instanceof TransactionFragment){
                    navigationView.setCheckedItem(R.id.nav_transactions);
                }
                else if(currentFragment instanceof NewTransactionFragment){
                    navigationView.setCheckedItem(R.id.nav_new_transaction);
                }
            }
        }
    }

    /**
     * onCreateOptionsMenu-Methode
     * @param menu Menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /** onOptionsItemSelected-Methode
     * @param item MenuItem
     * @return true / false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.menu_refresh:
                transactionFragment.getSwipeRefreshLayout().setRefreshing(true);
                transactionFragment.update();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /** onNavigationItemSelected-Methode
     * @param item MenuItem
     * @return true / false
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (item.getItemId()) {

            case R.id.nav_transactions:
                fragmentTransaction.replace(R.id.fragment_container, transactionFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.nav_new_transaction:
                NewTransactionFragment newTransactionFragment = NewTransactionFragment.newInstance();
                fragmentTransaction.replace(R.id.fragment_container, newTransactionFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.nav_settings:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.nav_about:
                Intent aboutUsIntent = new Intent(getApplicationContext(), AboutUsActivity.class);
                startActivity(aboutUsIntent);
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /** onFragmentInteraction-Methode (wird nicht benötigt; aber die Nutzung der Fragments erfordert diese Methode)
     * @param uri Uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    /**
     * Wenn eine Transaktion erfolgreich ausgeführt wurde, wird das aktive Fragment ersetzt (NewTransactionFragment -> TransactionFragment).
     */
    @Override
    public void onTransactionExecute() {
        // NewTransactionFragment durch TransactionFragment ersetzen
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, transactionFragment);
        fragmentTransaction.commit();

        Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.transaction_successful, Snackbar.LENGTH_LONG).show();
        transactionFragment.update();
        navigationView.setCheckedItem(R.id.nav_transactions);
    }

}

