package com.example.money;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainScreen extends AppCompatActivity {

    ListView spendView;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    List<Spending> spendingList;
    SpendingDBHelper db;
    TextView sampleText;
    int totalWeekly, spent;
    ProgressBar spentBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        spendView = findViewById(R.id.spendView);

        sampleText = findViewById(R.id.explainText);

        spentBar = findViewById(R.id.spentBar);
        db = Databases.getSpendingHelper();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 2);
        spendingList = db.getAll(true, getStartOfWeek());

        totalWeekly = db.getWeeklyAllowance();
        spent = initList();
        spentBar.setProgress(10);
    }

    public String getStartOfWeek() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(c.getTime());
    }

    public int initList() {
        List<String> spendString = new ArrayList<String>();
        int total=0;
        for(Spending s: spendingList) {
            spendString.add(s.toString());
            total+=s.getAmount();
        }


        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spendString);
        spendView.setAdapter(adapter);
        return total;
    }
}