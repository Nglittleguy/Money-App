package com.example.money;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainAddIncome extends AppCompatActivity {

    private IncomeDBHelper dbHelper;
    private RecyclerView rvIncome;
    private RecyclerView.LayoutManager rvIncomeManger;
    private IncomeAdapter incomeAdapter;
    private TextView totalIncome;
    List<Income> incomeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_add_income);

        dbHelper = new IncomeDBHelper(this);
        Databases.setIncomeHelper(dbHelper);

        incomeList = dbHelper.getAll();



        rvIncome = findViewById(R.id.recyclerView);
        rvIncome.setHasFixedSize(true);
        rvIncomeManger = new LinearLayoutManager(this);
        incomeAdapter = new IncomeAdapter(this, incomeList, rvIncome);
        rvIncome.setLayoutManager(rvIncomeManger);
        rvIncome.setAdapter(incomeAdapter);

        totalIncome = findViewById(R.id.totalIncome);
        updateTotal();


    }

    public void updateTotal() {
        int total = 0;
        for(Income i:incomeList)
            total+=i.getAmountPerWeek();

        totalIncome.setText("Weekly Total: " + Databases.centsToDollar(total));
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