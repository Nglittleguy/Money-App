package com.example.money;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainAdd extends AppCompatActivity {

    private IncomeDBHelper dbHelper;
    private RecyclerView rvIncome;
    private RecyclerView.LayoutManager rvIncomeManger;
    private IncomeAdapter incomeAdapter;
    List<Income> incomeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_add);

        dbHelper = new IncomeDBHelper(this);
        Databases.setIncomeHelper(dbHelper);

        incomeList = dbHelper.getAll();

        //listOfIncome = findViewById(R.id.listOfIncome);



        rvIncome = findViewById(R.id.recyclerView);
        rvIncome.setHasFixedSize(true);
        rvIncomeManger = new LinearLayoutManager(this);
        incomeAdapter = new IncomeAdapter(this, incomeList, rvIncome);
        rvIncome.setLayoutManager(rvIncomeManger);
        rvIncome.setAdapter(incomeAdapter);




    }


    public void addNewIncomePressed(View v) {
        Intent leaveActivity = new Intent(this, AddIncome.class);
        leaveActivity.putExtra("Edit", false);
        startActivity(leaveActivity);
    }

    public void nextPressed(View v) {

    }
}