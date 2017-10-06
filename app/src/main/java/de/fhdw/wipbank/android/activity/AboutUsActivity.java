package de.fhdw.wipbank.android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.fhdw.wipbank.android.R;

/**
 * AboutUsActivity: Wird über den NavigationDrawer in der {@link MainActivity} aufgerufen. Zeigt Über-Uns Informationen an.
 * @author Alexander Sawenko
 */
public class AboutUsActivity extends AppCompatActivity {
    /**
     * OnCreate-Methode
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }
}
