package com.example.money;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainAddExpense extends AppCompatActivity {

    private IncomeDBHelper dbHelper;
    private RecyclerView rvExpense;
    private RecyclerView.LayoutManager rvExpenseManger;
    private IncomeAdapter expenseAdapter;
    private TextView totalExpense;
    private ProgressBar incomeExpenseProgress;
    List<Income> expenseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_add_expense);

        Intent i = getIntent();
        dbHelper = Databases.getIncomeHelper();

        expenseList = dbHelper.getAll(false);

        incomeExpenseProgress = findViewById(R.id.totalIncomeExpenseProgress);

        rvExpense = findViewById(R.id.recyclerView);
        rvExpense.setHasFixedSize(true);
        rvExpenseManger = new LinearLayoutManager(this);
        expenseAdapter = new IncomeAdapter(this, expenseList, rvExpense);
        rvExpense.setLayoutManager(rvExpenseManger);
        rvExpense.setAdapter(expenseAdapter);

        totalExpense = findViewById(R.id.totalExpense);
        updateTotal();

    }

    public void updateTotal() {
        int total = 0;
        for(Income i:expenseList)
            total += i.getInc() * i.getAmountPerWeek();

        int incomeSubExpense =
                (int)(100*(1- (double) Databases.getWeeklyExpenses()/Databases.getWeeklyIncome()));
        if(incomeSubExpense<=0)
            incomeExpenseProgress.setProgress(0);
        else
            incomeExpenseProgress.setProgress(incomeSubExpense);
        totalExpense.setText("Weekly Expenses: " + Databases.centsToDollar(total));
    }

    public void addNewExpensePressed(View v) {
        Intent leaveActivity = new Intent(this, AddExpense.class);
        leaveActivity.putExtra("Edit", false);
        startActivity(leaveActivity);
    }

    public void previousPressed(View v) {
        Intent leaveActivity = new Intent(this, MainAddIncome.class);
        startActivity(leaveActivity);
    }

    public void nextPressed(View v) {
        Log.d("Success", "here");
        Intent leaveActivity = new Intent(this, MainAddSavingLT.class);
        startActivity(leaveActivity);
    }

}