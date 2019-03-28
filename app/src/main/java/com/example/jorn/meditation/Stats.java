package com.example.jorn.meditation;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.constraint.solver.widgets.Helper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.jorn.meditation.com.example.jorn.meditation.helper.HelperMethods;
import com.example.jorn.meditation.com.example.jorn.meditation.helper.DatabaseHelper;
import com.example.jorn.meditation.model.Meditation;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Stats extends AppCompatActivity {

    // Variables
    DatabaseHelper db;
    TextView totalTimeMeditated;
    TextView avgTimeMeditated;
    TextView avgRating;
    TextView amountOfMeditations;
    Toolbar toolbar;
    GraphView graph;
    TableLayout medTable;
//    LinearLayout medLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // The database containing the meditation statistics
        db = new DatabaseHelper(getApplicationContext());

        // Variables initialization
        initialiseComponents();

        setupActionBar(toolbar);

        // Fill in the stats
        fillStats(totalTimeMeditated, avgTimeMeditated, avgRating, amountOfMeditations);

        // OnClickListener for the toolbar options
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.settings)
                    settingsScreen(Stats.super.getCurrentFocus());
                return false;
            }
        });
    }

    private void initialiseComponents() {
        toolbar = findViewById(R.id.toolbarStats);
        totalTimeMeditated = findViewById(R.id.totalTimeMeditated);
        avgTimeMeditated = findViewById(R.id.averageLengthMeditation);
        avgRating = findViewById(R.id.averageRating);
        amountOfMeditations = findViewById(R.id.totalMeditations);
        graph = findViewById(R.id.graph);
        medTable = findViewById(R.id.medTable);
    }

    /**
     * Fill the stats graph with the data from the meditations
     * @param totalTimeMeditated
     * @param avgTimeMeditated
     * @param avgRating
     * @param amountOfMeditations
     */
    private void fillStats(TextView totalTimeMeditated, TextView avgTimeMeditated, TextView avgRating, TextView amountOfMeditations) {
        // Get the meditations
        ArrayList<Meditation> meditations = (ArrayList) db.getAllMeditations();
        long totalTime = 0;
        int totalMeditations = 0;
        int totalRating = 0;

        // Parse the meditations to obtain the statistics
        for (Meditation med : meditations) {
            totalTime += med.getTimeMeditated();
            totalMeditations++;
            totalRating += med.getRating();
            Log.d("LOG stats", Integer.toString(totalMeditations) + " " + Integer.toString(totalRating) + " "
                    + Integer.toString((int) med.getTimeMeditated() / 1000));
            addToLayout(med);
        }

        double averageRating = 0;

        // Fill the statistics with the calculated stats
        totalTimeMeditated.setText(HelperMethods.toTime(totalTime));
        if (totalMeditations != 0) {
            avgTimeMeditated.setText(HelperMethods.toTime(totalTime / totalMeditations));
            averageRating = (double)totalRating / totalMeditations;
        }
        avgRating.setText(Double.toString(Math.round(averageRating * 100D) / 100D));
        amountOfMeditations.setText(Integer.toString(totalMeditations));

        setupGraph(meditations);
    }

    private void addToLayout(Meditation med) {
        TableRow tr = tableRowFromMed(med);
        medTable.addView(tr);
    }

    /**
     * Returns a TableRow and adds the data of the meditation to this row, which can then be added to the TableLayout
     * @param med
     * @return
     */
    private TableRow tableRowFromMed(Meditation med) {
        // Create new TableRow entry
        TableRow tr = new TableRow(this);

        TableRow.LayoutParams trp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        tr.setLayoutParams(trp);

        // Textviews for all of the meditation data
        TextView medId = new TextView(this);
        TextView date = new TextView(this);
        TextView length = new TextView(this);
        TextView rating = new TextView(this);

        // Set up the views, filling them with the data + setting the layout
        setupTextViews(med, medId, date, length, rating);

        // Add textViews to the TableRow
        addViewsToTableRow(tr, medId, date, length, rating);

        // Return the TableRow
        return tr;
    }

    /**
     * Sets up the textViews before inserting them in the TableRow entry
     * @param med
     * @param medId
     * @param date
     * @param length
     * @param rating
     */
    private void setupTextViews(Meditation med, TextView medId, TextView date, TextView length, TextView rating) {
        // Setting the text color and the text alignment;
        medId.setTextColor(getResources().getColor(R.color.darkGrey)); medId.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        date.setTextColor(getResources().getColor(R.color.darkGrey)); date.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        length.setTextColor(getResources().getColor(R.color.darkGrey)); length.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        rating.setTextColor(getResources().getColor(R.color.darkGrey)); rating.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        // Drawable resource for setting the white lining behind the textviews;
        Drawable bg = getResources().getDrawable(R.drawable.cell_shape);

        // Setting the background drawable as background for each of the text view
        medId.setBackground(bg);
        date.setBackground(bg);
        length.setBackground(bg);
        rating.setBackground(bg);

        // Filling in the correct data per meditation
        medId.setText(Integer.toString(med.getId() + 1));
        date.setText(HelperMethods.toDDMMMYYYY(med.getDate()));
        length.setText(HelperMethods.toTime(med.getTimeMeditated()));
        rating.setText(Integer.toString(med.getRating()));
    }

    /**
     * Adding the medId, date, length and rating TextViews to the TableRow entry 'res'
     * @param res TableRow
     * @param medId toAdd
     * @param date toAdd
     * @param length toAdd
     * @param rating toAdd
     */
    private void addViewsToTableRow(TableRow res, TextView medId, TextView date, TextView length, TextView rating) {
        res.addView(medId);
        res.addView(date);
        res.addView(length);
        res.addView(rating);
    }

    /**
     * Setup the graph
     * @param meditations
     */
    private void setupGraph(ArrayList<Meditation> meditations) {
        DataPoint[] dataPoints = new DataPoint[meditations.size()];
        int counter = 0;

        // Go over the meditations and store them in the graph
        for (Meditation med : meditations) {
//            String strDate = dateFormat.format(med.getDate());
            Log.d("Graph", "Added " + med.getTimeMeditated()/1000 + " at data point " + counter);
            dataPoints[counter] = new DataPoint(counter, med.getTimeMeditated() / (1000 * 60));
            counter++;
        }

        // Some settings
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        setSeriesSettings(series);

        graph.addSeries(series);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.setTitle("amount of minutes (y-axis) meditated per day (x-axis)");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
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
    }

    /**
     * Setting the settings of the series of the graph
     * @param series
     */
    private void setSeriesSettings(LineGraphSeries<DataPoint> series) {
        series.setDrawBackground(false);
        series.setBackgroundColor(R.color.smoothYellow);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
    }

    /**
     * Method for opening the Stats activity
     * @param view
     */
    public void settingsScreen(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    /**
     * Used to go to the home screen
     * @param view
     */
    public void goHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
