package com.example.jorn.meditation.com.example.jorn.meditation.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jorn.meditation.AfterMeditationPopup;
import com.example.jorn.meditation.MainActivity;
import com.example.jorn.meditation.R;
import com.example.jorn.meditation.Settings;
import com.example.jorn.meditation.model.Meditation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HelperMethods {

    /**
     * Convert a time in milliseconds to a MM:HH:SS format
     * @param time
     * @return
     */
    public static String toTime(long time) {

        String res = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(time),
                TimeUnit.MILLISECONDS.toMinutes(time) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));

        return res;
    }

    // TODO implement method
    public static String toTimeWithDays(long time) {
        throw new IllegalArgumentException("Not implemented yet!");
    }

    /**
     * Return a String representation of the date parameter in the format DD-MMM-YYYY
     * @param date
     * @return
     */
    public static String toDDMMMYYYY(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Log.d("Date", date.toString() + " ------ " + dateFormat.format(date));
        return dateFormat.format(date);
    }

    /**
     * Storing a new meditation in the database
     * @param context
     * @param startTime
     * @param db
     */
    public static void storeMeditation(Context context, long startTime, DatabaseHelper db, int rating) {
        long totalTime = System.currentTimeMillis() - startTime;
        int medId = db.getAllMeditations().size();
        Meditation newMeditation = new Meditation(medId, totalTime, "Test", rating);
        Log.d("store", "Storing meditation----- id:" + Integer.toString(medId) + " time:" + Long.toString(totalTime / 60) + " rating:" + rating);
        db.insertMeditation(newMeditation);
    }

    public static void storeMeditation(long totalTime, int rating, Date date, DatabaseHelper db) {
        int medId = db.getAllMeditations().size();
        Meditation newMeditation = new Meditation(medId, totalTime, "Test", rating, date);
        Log.d("store", "Storing meditation----- id:" + Integer.toString(medId) + " time:" + Long.toString(totalTime / 60) + " rating:" + rating);
        db.insertMeditation(newMeditation);
    }

    /**
     * Check the meditation time
     * @param endTimeMillis
     * @param startTimeMillis
     * @return true if meditation lasted longer than one minute, false otherwise
     */
    private static boolean validTime(long endTimeMillis, long startTimeMillis) {
        long totalTimeSecs = (endTimeMillis - startTimeMillis) / 1000;
        Log.d("Total time", Long.toString(totalTimeSecs));
        return totalTimeSecs >= 60;
    }

    /**
     * Make a warning toast for not giving the right parameter
     * @param context
     */
    public static void makeToast(Context context, String text) {
        Toast warningNumber = Toast.makeText(context, text, Toast.LENGTH_LONG);
        warningNumber.setGravity(Gravity.CENTER, 0, 0);
        warningNumber.show();
    }

    /**
     * Method to store the meditation once the mediatation has stopped
     * @param context
     * @param startTime
     * @param db
     */
    public static void meditationStopped(Context context, long startTime, DatabaseHelper db, TextView timeMeditating) {
        // If meditation lasted shorter than a minute, don't save:
        if (!validTime(System.currentTimeMillis(), startTime)) {
            Toast toast = Toast.makeText(context, "The meditation lasted shorter than one minute, " +
                    "so it has not been saved", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } // Else we have a valid meditation:
        else {
            // Let the user give a rating
            AfterMeditationPopup popup = new AfterMeditationPopup(context);
            popup.popupRating();

            // Wait for the rating to be given and store the mediation afterwards:
            WaitForRatingThread wfrt = new WaitForRatingThread(context, startTime, db, popup, timeMeditating);
            wfrt.start();
        }

    }

}

