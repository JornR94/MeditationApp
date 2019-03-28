package com.example.jorn.meditation;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.example.jorn.meditation.com.example.jorn.meditation.helper.HelperMethods;

public class AfterMeditationPopup {

    // Variables
    public EditText ratingEditText;
    private int rating = 0;
    public Context context;
    AlertDialog dialog;

    // Constructor
    public AfterMeditationPopup(Context context) {
        this.context = context;
    }

    /**
     * Returns the rating given by the user, or 0 if no rating has been given yet
     * @return rating
     */
    public int getRating() {
        return this.rating;
    }

    /**
     * Makes a popup window to store the rating for this meditation
     */
    public void popupRating() {
        // Create an alertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("Rating");
        builder.setMessage("How would you rate this meditation?");

        // Add the rating editText to the builder
        ratingEditText = makeRatingEditText();
        builder.setView(ratingEditText);

        // Add the Confirm button and onClick event
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        try {
                            int ratingGiven = Integer.parseInt(ratingEditText.getText().toString());
                            if (ratingGiven < 1 || ratingGiven > 10) {
                                HelperMethods.makeToast(context, "The rating must be between 1 and 10");
                                popupRating();
                            }
                            else
                                rating = ratingGiven;

                        } catch (NumberFormatException e) {
                            // Give the user a warning if input is not valid
                            HelperMethods.makeToast(context, "Please fill in a number");
                            popupRating();
                        }
                    }
                });

        // Add the Cancel button
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing; return to base context
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    /**
     * Creates and returns a new rating editText
     * @return rating editText
     */
    @TargetApi(21)
    private EditText makeRatingEditText() {
        EditText rating = new EditText(context);
//        rating.getBackground().setColorFilter(getResources().getColor(R.color.smoothYellow), PorterDuff.Mode.SRC_IN);
        return rating;
    }

    public void close() {
        dialog.dismiss();
    }
}
