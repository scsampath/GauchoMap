package com.example.gauchomap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class StatisticsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Set up toolbar
        mToolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(mToolbar);
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
                .Builder(StatisticsActivity.this);
        AlertDialog alertDialog;
        switch (item.getItemId()) {
            case R.id.resetButton:
                builder.setTitle("Reset Statistics?");
                builder.setMessage("WARNING, Statistics data will be lost!");
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
                    finish();
                break;
            default:
                break;
        }
        return true;
    }
}
