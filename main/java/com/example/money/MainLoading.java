package com.example.money;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class MainLoading extends AppCompatActivity {

    private DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_loading);

        db = new DatabaseHelper(this);
        Databases.setDBHelper(db);

        Date d = db.getLastDate();

        if(d==null) {
            setUp();
            return;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(d);

        if(getStartOfWeek().before(c)) {
            Databases.setWeeklyAllowance(this, true);
        }
        else {
            Databases.setWeeklyAllowance(this, false);
        }
        db.setLastDate();
        goToMainScreen();
    }

    public void setUp() {
        Intent leaveActivity = new Intent(this, MainImport.class);
        startActivity(leaveActivity);
    }

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
