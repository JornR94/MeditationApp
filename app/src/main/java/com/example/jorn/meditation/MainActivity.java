package com.example.jorn.meditation;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Icon;
import android.media.MediaPlayer;
import android.support.constraint.solver.widgets.Helper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.PointerIcon;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jorn.meditation.activity.LoginActivity;
import com.example.jorn.meditation.com.example.jorn.meditation.helper.HelperMethods;
import com.example.jorn.meditation.com.example.jorn.meditation.helper.CountDownThread;
import com.example.jorn.meditation.com.example.jorn.meditation.helper.DatabaseHelper;
import com.example.jorn.meditation.com.example.jorn.meditation.helper.SQLiteHandler;
import com.example.jorn.meditation.com.example.jorn.meditation.helper.SessionManager;
import com.example.jorn.meditation.com.example.jorn.meditation.helper.TimerThread;

public class MainActivity extends AppCompatActivity {

    // variables
    boolean runningCountDown;
    boolean runningNormal;
    long startTime = 0;

    // UI elements
    CheckBox cb;
    Button start;
    ImageButton addMeditation;
    EditText timeToMed;
    TextView timeMeditating;
    Toolbar toolbar;
    ProgressBar progressBar;

    // media
    MediaPlayer gongSound;

    // Helper elements
    DatabaseHelper db;
    TimerThread setTime;
    CountDownThread countDownThread;

    private SQLiteHandler dbApi;
//    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the activity so that nothing is focused yet
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // initialize media, helper elements and UI elements
        initialiseComponents();

//        if (!session.isLoggedIn()) {
//            logoutUser();
//        }

        // Setup the toolbar as the action bar and setup the progressBar:
        setupActionBar(toolbar);
        setupProgressBar();

        // onClickListener for the start button
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb.isChecked()) {
                    // If we have a countdown going
                    if (runningCountDown) {
                        stopCountDown();
                    }
                    // If a meditation without countdown is going
                    else if (runningNormal) {
                        stopTimer();
                    }
                    // Else no meditation started yet
                    else {
                        // If a set meditation time is given
                        Log.d("Time to med string", timeToMed.getText().toString());
                        if (!timeToMed.getText().toString().equals("") && !timeToMed.getText().toString().equals("Time to meditate")) {
                            startCountDownThread();
                        }
                        // Else we start a normal thread
                        else {
                            startTimerThread();
                        }
                    }
                }
                else {
                    // TODO implement this bit
                }
            }
        });

        // OnClickListener for the toolbar options
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.stats)
                    statsScreen(MainActivity.super.getCurrentFocus());
                else if (item.getItemId() == R.id.settings)
                    settingsScreen(MainActivity.super.getCurrentFocus());
//                else if (item.getItemId() == R.id.logout)
//                    logoutUser();
                return false;
            }
        });

        // OnClickListener for the time to meditate EditText
        timeToMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeToMed.setText("");
            }
        });
    }

    private void initialiseComponents() {
        toolbar = findViewById(R.id.toolbar);
        db = new DatabaseHelper(getApplicationContext());
        dbApi = new SQLiteHandler(getApplicationContext());
//        session = new SessionManager(getApplicationContext());
        cb = findViewById(R.id.checkbox); cb.setChecked(true);
        start = findViewById(R.id.startBtn);
        addMeditation = findViewById(R.id.addMeditation);
        timeToMed = findViewById(R.id.meditationTime);
        timeMeditating = findViewById(R.id.timeMeditating);
        progressBar = findViewById(R.id.progressBar);
        gongSound = MediaPlayer.create(this, R.raw.gong_sound);
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

    /**
     * Setup the action bar of the activity
     * @param toolbar
     */
    private void setupActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        toolbar.inflateMenu(R.menu.main_menu);
    }

    /**
     * Setting up the progress bar; requires min level 21 API
     */
    @TargetApi(21)
    private void setupProgressBar() {
        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.smoothYellow));
        progressBar.setProgressTintList(colorStateList);
        progressBar.setIndeterminateTintList(colorStateList);
        progressBar.setProgress(0);
    }

    /**
     * Start the normal thread
     */
    private void startTimerThread() {
        // Play the gong sound
        gongSound.start();

        start.setText("Stop");

        progressBar.setIndeterminate(true);

        runningNormal = true;
        startTime = System.currentTimeMillis();
        // Start a timer thread
        setTime = new TimerThread(timeMeditating, startTime);
        setTime.start();
    }

    /**
     * Start the countdown
     */
    private void startCountDownThread() {
        // First check for valid input:
        int timeInMins = 0;
        try {
            timeInMins = Integer.parseInt(timeToMed.getText().toString());
        } catch (NumberFormatException e) {
            // Give the user a warning if input is not valid
            HelperMethods.makeToast(this, "Please fill in a number");
        }
        long timeMillis = timeInMins * 60 * 1000;

        // Start a countdown thread if we have valid input
        if (timeInMins != 0) {
            // Play the gong sound
            gongSound.start();
            start.setText("Stop");
            runningCountDown = true;
            startTime = System.currentTimeMillis();
            countDownThread = new CountDownThread(timeMillis, timeMeditating, this, progressBar, db, start);
            countDownThread.start();
        }
    }

    /**
     * Stop the timer thread
     */
    private void stopTimer() {
        progressBar.setIndeterminate(false);
        runningNormal = false;
        start.setText("Start");
        setTime.terminate();

        HelperMethods.meditationStopped(this, startTime, db, timeMeditating);
    }

    /**
     * Stop the countdown
     */
    private void stopCountDown() {
        runningCountDown = false;
        start.setText("Start");
        countDownThread.terminate();

        HelperMethods.meditationStopped(this, startTime, db, timeMeditating);
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
     * Opening the settings screen activity
     * @param view
     */
    private void settingsScreen(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    /**
     * Method used by the addMeditation imageButton
     * @param view
     */
    public void addMeditationClicked(View view) {
        startActivity(new Intent(this, AddMeditation.class));
    }

//    /**
//     * Logs out the current user.
//     */
//    private void logoutUser() {
//        session.setLogin(false);
//
//        dbApi.deleteUsers();
//
//        // Launching the login activity
//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }
}
