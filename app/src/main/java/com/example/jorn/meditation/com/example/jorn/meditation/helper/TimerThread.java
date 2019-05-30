package com.example.jorn.meditation.com.example.jorn.meditation.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import com.example.jorn.meditation.R;

public class TimerThread extends Thread {

    // Variables declaration
    private long startTime;
    // we have three sessions of 5 mins each, going on indefinitely
    private long sessionTime;
    private TextView timeMeditating;
    private boolean threadRunning = false;
    private MediaPlayer focusSound;
    private MediaPlayer openAwarenessSound;
    private MediaPlayer emotionsSound;
    private Context mainActivity;

    private boolean playFocus;
    private boolean playOpenAwareness;
    private boolean playEmotion;

    /**
     * Constructor
     * @param tv
     * @param st
     */
    public TimerThread(TextView tv, long st, Context mainActivity) {
        super();
        timeMeditating = tv;
        startTime = st;
        this.sessionTime = 5000;
        this.mainActivity = mainActivity;

        focusSound = MediaPlayer.create(mainActivity, R.raw.gong_sound);
        openAwarenessSound = MediaPlayer.create(mainActivity, R.raw.gong_sound);
        emotionsSound = MediaPlayer.create(mainActivity, R.raw.gong_sound);

        this.playFocus = true;
    }

    /**
     * Terminate this thread
     */
    public void terminate() {
        this.threadRunning = false;
    }

    @Override
    public void run() {
        threadRunning = true;
        try {
            while (threadRunning) {
                sleep(1000);
                long currentTime = System.currentTimeMillis() - startTime;
                if (currentTime % sessionTime < 100)
                    playSound();

                String time = HelperMethods.toTime(currentTime);
                Log.d("Time", Long.toString(startTime) + " " + Long.toString(currentTime));
                timeMeditating.setText(time);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void playSound() {
        if (playFocus) {
            playFocus();
        } else if (playOpenAwareness) {
            playOpenAwareness();
        } else {
            playEmotions();
        }
    }

    private void playFocus() {
        focusSound.start();
        playFocus = false;
        playOpenAwareness = true;
    }

    private void playOpenAwareness() {
        focusSound.start();
        playOpenAwareness = false;
        playEmotion = true;
    }

    private void playEmotions() {
        emotionsSound.start();
        playEmotion = false;
        playFocus = true;
    }
}
