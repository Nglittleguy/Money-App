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

public class MainAddExpense extends AppCompatActivity {

    private ExpenseDBHelper dbHelper;
    private RecyclerView rvExpense;
    private RecyclerView.LayoutManager rvExpenseManger;
    private ExpenseAdapter ExpenseAdapter;
    private TextView totalExpense;
    List<Expense> ExpenseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_add_expense);

        dbHelper = new ExpenseDBHelper(this);
        Databases.setExpenseHelper(dbHelper);

        ExpenseList = dbHelper.getAll();




        rvExpense = findViewById(R.id.recyclerView);
        rvExpense.setHasFixedSize(true);
        rvExpenseManger = new LinearLayoutManager(this);
        ExpenseAdapter = new ExpenseAdapter(this, ExpenseList, rvExpense);
        rvExpense.setLayoutManager(rvExpenseManger);
        rvExpense.setAdapter(ExpenseAdapter);

        totalExpense = findViewById(R.id.totalExpense);
        updateTotal();


    }

    public void updateTotal() {
        int total = 0;
        for(Expense i:ExpenseList)
            total+=i.getAmountPerWeek();

        totalExpense.setText("Weekly Total Expenses: " + Databases.centsToDollar(total));
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

    }
}