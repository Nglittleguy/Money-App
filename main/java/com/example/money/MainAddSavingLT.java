package com.example.money;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainAddSavingLT extends AppCompatActivity {

    private SavingDBHelper dbHelper;
    private RecyclerView rvSaving;
    private RecyclerView.LayoutManager rvSavingManger;
    private SavingAdapter rvSavingAdapter;
    private TextView totalSaving;
    List<Saving> rvSavingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_add_saving_l_t);

        dbHelper = new SavingDBHelper(this);
        Databases.setSavingHelper(dbHelper);
        rvSavingList = dbHelper.getAllNonFinished();



        rvSaving = findViewById(R.id.recyclerView);
        rvSaving.setHasFixedSize(true);
        rvSavingManger = new LinearLayoutManager(this);
        rvSavingAdapter = new SavingAdapter(this, rvSavingList, rvSaving);
        rvSaving.setLayoutManager(rvSavingManger);
        rvSaving.setAdapter(rvSavingAdapter);

        totalSaving = findViewById(R.id.totalSaving);
        updateTotal();

    }

    public void updateTotal() {
        int total = 0;
        for(Saving i:rvSavingList)
            total += i.getAmountPerWeek();

        totalSaving.setText("Weekly Total Savings: " + Databases.centsToDollar(total));
    }

    public void addNewSavingPressed(View v) {
        Intent leaveActivity = new Intent(this, AddSavingLongTerm.class);
        leaveActivity.putExtra("Edit", false);
        startActivity(leaveActivity);
    }

    public void previousPressed(View v) {
        Intent leaveActivity = new Intent(this, MainAddExpense.class);
        startActivity(leaveActivity);
    }

//    public void nextPressed(View v) {
//        Log.d("Success", "here");
//        Intent leaveActivity = new Intent(this, AddSavingLongTerm.class);
//        startActivity(leaveActivity);
//    }

}