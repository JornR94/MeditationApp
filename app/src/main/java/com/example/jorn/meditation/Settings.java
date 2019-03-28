package com.example.jorn.meditation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import com.example.jorn.meditation.com.example.jorn.meditation.helper.DatabaseHelper;
import com.example.jorn.meditation.com.example.jorn.meditation.helper.HelperMethods;

public class Settings extends AppCompatActivity {

    // Variables
    DatabaseHelper db;
    Toolbar toolbar;
    Spinner soundsChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Setup action bar
        soundsChoice = findViewById(R.id.spinner);
        toolbar = findViewById(R.id.toolbarSettings);
        setupActionBar(toolbar);
        db = new DatabaseHelper(this);

        // OnClickListener for the toolbar options
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.stats)
                    statsScreen(Settings.super.getCurrentFocus());
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Setup the action bar of the activity
     * @param toolbar
     */
    private void setupActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Method for opening the Stats.java activity
     * @param view
     */
    public void statsScreen(View view) {
        Intent intent = new Intent(this, Stats.class);
        startActivity(intent);
    }

    /**
     * On click method for the erase meditations button
     * @param view
     */
    public void eraseActionPerformed(View view) {
        db.removeAllEntries();
    }

    /**
     * Used to go to the home screen
     * @param view
     */
    public void goHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * On click method for the add Meditation button
     * @param view
     */
    public void addMeditationClicked(View view) {
        startActivity(new Intent(this, AddMeditation.class));
    }
}
