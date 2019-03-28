package com.example.jorn.meditation.com.example.jorn.meditation.helper;

import android.util.Log;
import android.widget.TextView;

public class TimerThread extends Thread {

    // Variables declaration
    long startTime;
    TextView timeMeditating;
    boolean threadRunning = false;

    /**
     * Constructor
     * @param tv
     * @param st
     */
    public TimerThread(TextView tv, long st) {
        super();
        timeMeditating = tv;
        startTime = st;
    }

    /**
     * Terminate this thread
     */
    public void terminate() {
        threadRunning = false;
    }

    @Override
    public void run() {
        threadRunning = true;
        try {
            while (threadRunning) {
                sleep(1000);
                long currentTime = System.currentTimeMillis() - startTime;

                String time = HelperMethods.toTime(currentTime);
                Log.d("Time", Long.toString(startTime) + " " + Long.toString(currentTime));
                timeMeditating.setText(time);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}
