package com.example.jorn.meditation.com.example.jorn.meditation.helper;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jorn.meditation.AfterMeditationPopup;

public class WaitForRatingThread extends Thread {

    // Variables
    private boolean running;
    private long startTime;
    Context context;
    DatabaseHelper dbHelper;
    AfterMeditationPopup popup;
    TextView timeMeditating;
    int counter = 0;

    // Constructor
    public WaitForRatingThread(Context context, long startTime, DatabaseHelper db, AfterMeditationPopup popup, TextView timeMeditating) {
        this.startTime = startTime;
        this.context = context;
        this.dbHelper = db;
        this.popup = popup;
        this.timeMeditating = timeMeditating;
    }

    /**
     * Run method overriden from superclass Thread
     */
    @Override
    public void run() {

        running = true;

        while (running) {
            // If we don't have a rating yet (rating is initially 0); try for half a minute (30 secs)
            if (popup.getRating() == 0 && counter < 30) {
                // Pause for 1 second, then try again
                try {
                    counter++;
                    sleep(1000);
                    Log.d("Counter", Integer.toString(counter));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Else we have a rating, we can store it;
            else {
                terminate(); // Terminate the thread
                int rating = 7; // default rating
                if (counter != 30) // we have a rating given
                    rating = popup.getRating();
                else
                    popup.close();
                HelperMethods.storeMeditation(context, startTime, dbHelper, rating);
                timeMeditating.setText("00:00:00");
            }
        }
    }

    /**
     * Stop this thread
     */
    private void terminate() {
        this.running = false;
    }

}
