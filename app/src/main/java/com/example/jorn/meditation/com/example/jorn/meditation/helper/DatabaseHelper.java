package com.example.jorn.meditation.com.example.jorn.meditation.helper;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jorn.meditation.R;
import com.example.jorn.meditation.model.Meditation;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "meditationDB";

    // Table Name
    private static final String TABLE_MEDITATION = "meditation";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String DATE = "date";
    private static final String TIME_MEDITATED = "timeMeditated";
    private static final String COMMENT = "comment";
    private static final String RATING = "rating";

    // Parent context
    Context context;

    // Table creation statement
    private static final String CREATE_TABLE_MEDITATION =
            "CREATE TABLE "
            + TABLE_MEDITATION + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + TIME_MEDITATED + " INTEGER,"
            + DATE + " DATETIME,"
            + COMMENT + " TEXT,"
            + RATING + " INTEGER" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MEDITATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDITATION);

        // create new table
        onCreate(db);
    }

    // CRUD operation functions

    /**
     * Insert a meditation
     * @param med
     */
    public void insertMeditation(Meditation med) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, med.getId());
        values.put(TIME_MEDITATED, med.getTimeMeditated());
        values.put(DATE, med.getDateString());
        values.put(COMMENT, med.getComment());
        values.put(RATING, med.getRating());

        // insert row
        db.insert(TABLE_MEDITATION, null, values);
    }

    /**
     * Retrieve a single meditation
     * @param key
     * @return the meditation belonging to the key parameter
     */
    public Meditation getMeditation(int key) throws IllegalArgumentException {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_MEDITATION + " WHERE "
                + KEY_ID + " = " + key;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            long time = cursor.getLong(cursor.getColumnIndex(TIME_MEDITATED));
            String date = cursor.getString(cursor.getColumnIndex(DATE));
            String comment = cursor.getString(cursor.getColumnIndex(COMMENT));
            int rating = cursor.getInt(cursor.getColumnIndex(RATING));

            Meditation reqMeditation = new Meditation(id, time, date, comment, rating);
            return reqMeditation;

        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Get all of the meditations
     * @return a list with all the meditations in the database
     */
    public List<Meditation> getAllMeditations() {
        List<Meditation> meditations = new ArrayList<Meditation>();
        String selectQuery = "SELECT * FROM " + TABLE_MEDITATION;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // We go through the resulting rows and add the meditations to the list
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                long time = cursor.getLong(cursor.getColumnIndex(TIME_MEDITATED));
                String date = cursor.getString(cursor.getColumnIndex(DATE));
                String comment = cursor.getString(cursor.getColumnIndex(COMMENT));
                int rating = cursor.getInt(cursor.getColumnIndex(RATING));

                Meditation newMed = new Meditation(id, time, date, comment, rating);

                meditations.add(newMed);
            } while (cursor.moveToNext());
        }

        return meditations;
    }

    /**
     * Close the database
     */
    public void closeDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * Removes all entries from the database, after checking with user if that is really what (s)he wants...
     */
    public void removeAllEntries() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);

        builder.setTitle("Deleting database entries");

        builder.setMessage("Are you sure? This will remove all of the stored entries in the database...")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String deleteQuery = "DELETE FROM " + TABLE_MEDITATION;
                        SQLiteDatabase db = getWritableDatabase();
                        db.execSQL(deleteQuery);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
