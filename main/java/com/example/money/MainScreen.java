package com.example.money;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainScreen extends AppCompatActivity {

    //Deprecated Activity

    ListView spendView;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    List<Spending> spendingList;
    DatabaseHelper db;
    TextView sampleText;
    int totalWeekly, spent;
    ProgressBar spentBar, overBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        spendView = findViewById(R.id.spendView);

        sampleText = findViewById(R.id.explainText);

        spentBar = findViewById(R.id.spentBar);
        overBar = findViewById(R.id.overBar);
        db = Databases.getDBHelper();
        Calendar c = Calendar.getInstance();
        spendingList = db.getAllSpend(true, getStartOfWeek());

        totalWeekly = db.getWeeklyAllowance();
        spent = initList();
        if(spent<totalWeekly) {
            spentBar.setVisibility(View.VISIBLE);
            overBar.setVisibility(View.INVISIBLE);
            spentBar.setProgress(100*(totalWeekly-spent)/totalWeekly);
            sampleText.setText(Databases.centsToDollar(totalWeekly-spent)+"/ "+Databases.centsToDollar(totalWeekly));
        }
        else {
            spentBar.setVisibility(View.INVISIBLE);
            overBar.setVisibility(View.VISIBLE);
            if(spent>2*totalWeekly)
                overBar.setProgress(100);
            else
                overBar.setProgress(100*(spent-totalWeekly)/totalWeekly);
            sampleText.setText(Databases.centsToDollar(totalWeekly-spent)+"/ "+Databases.centsToDollar(totalWeekly));
        }

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