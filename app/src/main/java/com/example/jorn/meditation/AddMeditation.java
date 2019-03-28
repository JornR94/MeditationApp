package com.example.jorn.meditation;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.jorn.meditation.com.example.jorn.meditation.helper.DatabaseHelper;
import com.example.jorn.meditation.com.example.jorn.meditation.helper.HelperMethods;

import java.util.Date;

public class AddMeditation extends AppCompatActivity {

    Toolbar toolbar;
    CalendarView calendarView;
    EditText meditationTime;
    RatingBar ratingBar;
    Button addBtn;
    DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meditation);

        // Set the activity so that nothing is focused yet
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initialise components
        initComponents();
        // Setup components
        setupActionBar(toolbar);
        setupRatingBar(ratingBar);
        setupCalendar(calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc.
                String msg = "Selected date is " + dayOfMonth + "/" + (month + 1) + "/" + year;
                HelperMethods.makeToast(getBaseContext(), msg);
            }
        });

        // OnClickListener for the toolbar options
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.stats)
                    statsScreen(AddMeditation.super.getCurrentFocus());
                else if (item.getItemId() == R.id.settings)
                    settingsScreen(AddMeditation.super.getCurrentFocus());
                return false;
            }
        });

    }

    /**
     * Override the onCreateOptionsMenu method to create the menu inflater
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @TargetApi(21)
    private void setupCalendar(CalendarView calendarView) {
        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.smoothYellow));
        calendarView.setForegroundTintList(colorStateList);
    }

    @TargetApi(23)
    private void setupRatingBar(RatingBar ratingBar) {
        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.smoothYellow));
        ratingBar.setForegroundTintList(colorStateList);
        ratingBar.setProgressTintList(colorStateList);
    }

    private void setupActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initComponents() {
        toolbar = findViewById(R.id.toolbarAddMeditation);
        calendarView = findViewById(R.id.calendarView);
        meditationTime = findViewById(R.id.timeMeditated);
        ratingBar = findViewById(R.id.rating);
        addBtn = findViewById(R.id.addBtn);
        db = new DatabaseHelper(getApplicationContext());
    }

    /**
     * On click event for the add meditation button
     * @param view
     */
    public void meditationAdded(View view) {
        String time = meditationTime.getText().toString();
        int rating = Math.round(ratingBar.getRating() * 2);
        long dateTime = calendarView.getDate();

        long timeMeditated = parseTime(time);
        Date date = new Date(dateTime);
        Log.d("Test", Float.toString(ratingBar.getRating()));
        // If the input is valid: (default of ratingBar.getRating() is 0.0)
        if (timeMeditated != -1 && ratingBar.getRating() != 0.0) {
            HelperMethods.storeMeditation(timeMeditated, rating, date, db);
            HelperMethods.makeToast(this, "Meditation added!");
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    /**
     * Parses the time given by the user and converts it to milliseconds
     * @param time
     * @return
     */
    private long parseTime(String time) {
        long res = -1;
        try {
            res = Integer.parseInt(time) * 60 * 1000;
        } catch (NumberFormatException ex) {
            HelperMethods.makeToast(this, "Please provide a valid number");
        }
        return res;
    }

    /**
     * Go to main screen
     * @param view
     */
    public void goHome(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Method for opening the Stats.java activity
     * @param view
     */
    public void statsScreen(View view) {
        startActivity(new Intent(this, Stats.class));
    }

    /**
     * Opening the settings screen activity
     * @param view
     */
    private void settingsScreen(View view) {
        startActivity(new Intent(this, Settings.class));
    }


}
