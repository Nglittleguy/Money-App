package com.example.money;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainAddSavingGoal extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvSaving;
    private RecyclerView.LayoutManager rvSavingManger;
    private SavingAdapter rvSavingAdapter;
    private TextView totalSaving;
    private ProgressBar incomeExpenseSavingProgress;
    List<Saving> rvSavingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_add_saving_goal);

        dbHelper = Databases.getDBHelper();
        rvSavingList = dbHelper.getAllShortTermSave();

        incomeExpenseSavingProgress = findViewById(R.id.savingGoalProgress);

        rvSaving = findViewById(R.id.recyclerView);
        rvSaving.setHasFixedSize(true);
        rvSavingManger = new LinearLayoutManager(this);
        rvSavingAdapter = new SavingAdapter(this, rvSavingList, rvSaving);
        rvSaving.setLayoutManager(rvSavingManger);
        rvSaving.setAdapter(rvSavingAdapter);

        totalSaving = findViewById(R.id.totalSavingGoal);
        updateTotal();
    }

    public void updateTotal() {
        int total = 0;
        for(Saving i:rvSavingList)
            total += i.getAmountPerWeek();

        int incomeSubExpense =
                (int)(100*(1- (double) Databases.getWeeklyExpenses()/Databases.getWeeklyIncome()));
        if(incomeSubExpense<=0)
            incomeExpenseSavingProgress.setSecondaryProgress(0);
        else {
            incomeExpenseSavingProgress.setSecondaryProgress(incomeSubExpense);
            incomeSubExpense =
                    (int)(100*(1- (double) (Databases.getWeeklyExpenses()+Databases.getWeeklySaving())/Databases.getWeeklyIncome()));
            if(incomeSubExpense<=0)
                incomeExpenseSavingProgress.setProgress(0);
            else
                incomeExpenseSavingProgress.setProgress(incomeSubExpense);
        }
        totalSaving.setText("Weekly Goal Savings: " + Databases.centsToDollar(total));
    }

    public void nextPressed(View v) {
//        Intent leaveActivity = new Intent(this, AddSavingLongTerm.class);
//        leaveActivity.putExtra("Edit", false);
//        startActivity(leaveActivity);
    }

    public void previousPressed(View v) {
        Intent leaveActivity = new Intent(this, MainAddSavingLT.class);
        startActivity(leaveActivity);
    }

    public void addNewSavingPressed(View v) {
        Intent leaveActivity = new Intent(this, AddSavingGoal.class);
        startActivity(leaveActivity);
    }
}