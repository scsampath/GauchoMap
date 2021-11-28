package com.example.gauchomap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private boolean settingsChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Listener to check if settings changed
        settingsChanged = false;
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                settingsChanged = true;
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        // Set up toolbar
        mToolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(mToolbar);

        // Set up fragment for preferences
        getFragmentManager()
                .beginTransaction()
                .add(R.id.settingsContainer, new SettingsFragment())
                .commit();
    }

    // Toolbar button setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.appToolbar);
        mToolbar.inflateMenu(R.menu.settings_activity_menu);
        mToolbar.setOnMenuItemClickListener(item -> onOptionsItemSelected(item));
        return true;
    }

    // Toolbar button logic
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(SettingsActivity.this);
        AlertDialog alertDialog;
        switch (item.getItemId()) {
            case R.id.resetButton:
                builder.setTitle("Reset Settings?");
                builder
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface
                                        .OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                builder
                        .setNegativeButton(
                                "No",
                                new DialogInterface
                                        .OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.backButton:
                    // If settings changed restart MainActivity
                    // If settings have not changed then resume MainActivity
                    if(settingsChanged){
                        Intent myIntent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(myIntent);
                    } else {
                        finish();
                    }

                break;
            default:
                break;
        }
        return true;
    }
}




