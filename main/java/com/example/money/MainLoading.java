package com.example.money;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class MainLoading extends AppCompatActivity {

    private DatabaseHelper db;

    /*
    Initial Activity - starts everything up
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_loading);

        //Generate database, and set it
        db = new DatabaseHelper(this);
        Databases.setDBHelper(db);

        Date d = db.getLastDate();

        if(d==null) {
            //no prior usage, so set up parameters
            setUp();
            return;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(d);

        //Set weekly allowance if same or different week as last used
        if(getStartOfWeek().before(c))
            Databases.setWeeklyAllowance(this, true);
        else
            Databases.setWeeklyAllowance(this, false);

        db.setLastDate();
        goToMainScreen();
    }

    /*
    Sets up parameters, or allows for import
     */
    public void setUp() {
        Intent leaveActivity = new Intent(this, MainImport.class);
        startActivity(leaveActivity);
    }

    /*
    Goes to main tab
     */
    public void goToMainScreen() {
        Intent leaveActivity = new Intent(this, MainTab.class);
        startActivity(leaveActivity);
    }

    public Calendar getStartOfWeek() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c;
    }
}
