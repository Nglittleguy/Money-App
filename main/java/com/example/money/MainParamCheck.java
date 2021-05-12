package com.example.money;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

public class MainParamCheck extends AppCompatActivity {

    private ProgressBar progress;
    private TextView remaining;
    private RecyclerView parameterList;
    private List<Income> iList, eList;
    private List<Saving> ltList, goalList;
    private IncomeDBHelper iDBHelper;
    private SavingDBHelper sDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_param_check);

        progress = findViewById(R.id.progress);
        remaining = findViewById(R.id.totalRemaining);

        iDBHelper = new IncomeDBHelper(this);
        Databases.setIncomeHelper(iDBHelper);
        iList = iDBHelper.getAll(true);
        eList = iDBHelper.getAll(false);

        sDBHelper = new SavingDBHelper(this);
        Databases.setSavingHelper(sDBHelper);
        ltList = sDBHelper.getAllLongTerm();
        goalList = sDBHelper.getAllShortTerm();

        parameterList= findViewById(R.id.allParameterList);
        ParameterAdapter paramAdapter = new ParameterAdapter(this, parameterList, iList, eList, ltList, goalList);
        parameterList.setAdapter(paramAdapter);
        parameterList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));

        updateTotal();
    }

    public void updateTotal() {
        Databases.getSavingHelper().updatePercentAmounts(Databases.getWeeklyAfterExpenses());
//        for(int i = 0; i<4; i++) {
//            if(parameterList.findViewHolderForAdapterPosition(i)!=null) {
//                int rem = 0;
//
//                switch(i) {
//                    case 0:
//                        for (int j = 0; j < iList.size(); j++)
//                            rem += iList.get(j).getAmountPerWeek();
//                        break;
//                    case 1:
//                        for (int j = 0; j < eList.size(); j++)
//                            rem -= eList.get(j).getAmountPerWeek();
//                        break;
//                    case 2:
//                        for (int j = 0; j < ltList.size(); j++)
//                            rem += ltList.get(j).getAmountPerWeek();
//                        break;
//                    case 3:
//                        for (int j = 0; j < goalList.size(); j++)
//                            rem += goalList.get(j).getAmountPerWeek();
//                        break;
//
//                }
//                //Toast.makeText(this, "Non "+ i, Toast.LENGTH_SHORT).show();
//                //(((ParameterAdapter.ViewHolder) Objects.requireNonNull(parameterList.findViewHolderForAdapterPosition(i)))).setParameterTitle(i, Databases.centsToDollar(rem));
//            }
//            else {
//                Toast.makeText(this, "Null object "+ i, Toast.LENGTH_SHORT).show();
//            }
//        }

        int incomeSubExpense =
                (int)(100*(1- (double) Databases.getWeeklyExpenses()/Databases.getWeeklyIncome()));
        if(incomeSubExpense<=0)
            progress.setSecondaryProgress(0);
        else {
            progress.setSecondaryProgress(incomeSubExpense);
            incomeSubExpense =
                    (int)(100*(1- (double) (Databases.getWeeklyExpenses()+Databases.getWeeklySaving())/Databases.getWeeklyIncome()));
            if(incomeSubExpense<=0)
                progress.setProgress(0);
            else
                progress.setProgress(incomeSubExpense);
        }
        remaining.setText("Weekly Allowance: " + Databases.centsToDollar(Databases.getRemaining()));
    }

    public void nextPressed(View v) {

    }
}