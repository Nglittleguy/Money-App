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

public class MainAddIncome extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvIncome;
    private RecyclerView.LayoutManager rvIncomeManger;
    private IncomeAdapter incomeAdapter;
    private TextView totalIncome;
    private ProgressBar totalIncomeProgress;
    private int incomeTotal;
    List<Income> incomeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_add_income);

        dbHelper = new DatabaseHelper(this);
        Databases.setDBHelper(dbHelper);

        incomeList = dbHelper.getAllIncome(true);



        rvIncome = findViewById(R.id.recyclerView);
        rvIncome.setHasFixedSize(true);
        rvIncomeManger = new LinearLayoutManager(this);
        incomeAdapter = new IncomeAdapter(this, incomeList, rvIncome);
        rvIncome.setLayoutManager(rvIncomeManger);
        rvIncome.setAdapter(incomeAdapter);

        totalIncome = findViewById(R.id.totalIncome);
        totalIncomeProgress = findViewById(R.id.addSavingProgress);
        totalIncomeProgress.setVisibility(View.INVISIBLE);
        updateTotal();


    }

    public void updateTotal() {
        int total = 0;
        for(Income i:incomeList)
            total += i.getInc() * i.getAmountPerWeek();

        if(total!=0)
            totalIncomeProgress.setVisibility(View.VISIBLE);
        else
            totalIncomeProgress.setVisibility(View.INVISIBLE);

        incomeTotal = total;
        totalIncome.setText("Weekly Income: " + Databases.centsToDollar(total));
    }

    public void addNewIncomePressed(View v) {
        Intent leaveActivity = new Intent(this, AddIncome.class);
        leaveActivity.putExtra("Edit", false);
        startActivity(leaveActivity);
    }

    public void nextPressed(View v) {
        Intent leaveActivity = new Intent(this, MainAddExpense.class);
        startActivity(leaveActivity);
    }
}