package com.example.money;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainLoading extends AppCompatActivity {

    private SpendingDBHelper spendingDB;
    private SavingDBHelper savingDB;
    private IncomeDBHelper incomeDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_loading);

        spendingDB = new SpendingDBHelper(this);
        Databases.setSpendingHelper(spendingDB);

        savingDB = new SavingDBHelper(this);
        Databases.setSavingHelper(savingDB);

        incomeDB = new IncomeDBHelper(this);
        Databases.setIncomeHelper(incomeDB);

        Date d = spendingDB.getLastDate();

        if(d==null) {
            setUp();
            return;
        }
        Log.d("Success", "Last is "+d.toString());
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        Calendar now = Calendar.getInstance();


        if(c.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)
                && c.get(Calendar.YEAR) == now.get(Calendar.YEAR))
            Databases.setWeeklyAllowance(this, true);
        else
            Databases.setWeeklyAllowance(this, false);

        spendingDB.setLastDate();
        goToMainScreen();
    }

    public void setUp() {
        Spending initialDate = new Spending(-1, "Last Date & Time", 0, true);
        Log.d("Success", "Date is "+initialDate.getDateTime().toString());
        spendingDB.addOne(initialDate);
        Intent leaveActivity = new Intent(this, MainParamCheck.class);
        startActivity(leaveActivity);
    }

    public void goToMainScreen() {
        Intent leaveActivity = new Intent(this, MainTab.class);
        startActivity(leaveActivity);
    }
}