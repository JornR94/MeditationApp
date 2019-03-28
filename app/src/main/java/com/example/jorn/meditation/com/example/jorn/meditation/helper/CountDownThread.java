package com.example.jorn.meditation.com.example.jorn.meditation.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jorn.meditation.AfterMeditationPopup;
import com.example.jorn.meditation.MainActivity;
import com.example.jorn.meditation.R;

public class CountDownThread extends Thread {

    // Variables
    private long startTime;
    private long timeToMed;
    private long totalSecs;
    TextView timeMeditating;
    Context context;
    ProgressBar progressBar;
    DatabaseHelper db;
    Button startBtn;
    private boolean running;
    private long endCountDown;

    /**
     * Constructor method
     * @param timeToMed
     * @param timeMed
     * @param context
     */
    public CountDownThread(long timeToMed, TextView timeMed, Context context, ProgressBar pgb, DatabaseHelper db, Button start) {

        // Initiate variables
        this.timeToMed = timeToMed;
        this.context = context;
        this.timeMeditating = timeMed;
        this.progressBar = pgb;
        this.db = db;
        this.startBtn = start;
        this.startTime = System.currentTimeMillis();
        // Meditate until the time:
        this.endCountDown = startTime + timeToMed;

        this.totalSecs = (endCountDown - startTime) / 1000;
    }

    /**
     * Terminating this thread
     */
    public void terminate() {
        this.running = false;
    }

    /**
     * Run method of this thread class
     */
    @Override
    public void run() {
        running = true;
        try {
            while (running) {
                long currentTime = System.currentTimeMillis();

                // While the countdown is not there yet;
                if (currentTime < endCountDown) {
                    Log.d("Time millis", Long.toString(currentTime - startTime));
                    timeMeditating.setText(HelperMethods.toTime(currentTime - startTime));

                    long current = (currentTime - startTime) / 10;
                    int progress = (int) current / (int) totalSecs;
                    progressBar.setProgress(progress);
                    sleep(1000);
                }
                // Else the countdown is finished:
                else {
                    terminate();
                    startBtn.setText("Start"); // Set text back to 'start
                    // Gong sound in the MainActivity context;
                    MediaPlayer mp = MediaPlayer.create(context, R.raw.gong_sound);
                    mp.start();

                    meditationEnded();
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method which is needed to store the meditation after giving a rating
     */
    private void meditationEnded() {
        // The handler here posts the method from Helpermethods.meditationStopped in the MainActivity:
        // https://stackoverflow.com/questions/3875184/cant-create-handler-inside-thread-that-has-not-called-looper-prepare
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                HelperMethods.meditationStopped(context, startTime, db, timeMeditating);
            }
        });
    }


}
