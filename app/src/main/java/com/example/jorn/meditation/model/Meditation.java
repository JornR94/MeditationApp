package com.example.jorn.meditation.model;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Meditation {

    int id;
    long timeMeditated; // in milliseconds
    Date date;
    String comment;
    int rating;

    // constructors
    public Meditation(int id, long timeMeditated, String date, String comment, int rating) {
        this.id = id;
        this.timeMeditated = timeMeditated;
        this.date = stringToDate(date);
        this.comment = comment;
        this.rating = rating;
    }

    public Meditation(int id, long timeMeditated, String comment, int rating) {
        this.id = id;
        this.timeMeditated = timeMeditated;
        this.date = new Date();
        this.comment = comment;
        this.rating = rating;
    }

    public Meditation(int id, long timeMeditated, String comment, int rating, Date date) {
        this.id = id;
        this.timeMeditated = timeMeditated;
        this.date = new Date();
        this.comment = comment;
        this.rating = rating;
        this.date = date;
    }

    // Getters
    public int getId() {
        return this.id;
    }

    public String getDateString() {
        return dateToString(this.date);
    }

    public Date getDate() {
        return this.date;
    }

    public String getComment() {
        return this.comment;
    }

    public int getRating() {
        return this.rating;
    }

    public long getTimeMeditated () {
        return this.timeMeditated;
    }


    // Helper methods
    public Date stringToDate(String date) {
        Log.d("String to date", date);
        SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        Date res = null;
        try {
            res = ft.parse(date);
        } catch (ParseException e) {
            Log.d("Date format", "Date formatting exception:");
            e.printStackTrace();
        }
        return res;
    }

    public String dateToString(Date date) {
        SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        String res = ft.format(date);
        Log.d("Date to string", res);
        return res;
    }
}
