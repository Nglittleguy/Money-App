package com.example.money;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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

        iDBHelper = Databases.getIncomeHelper();
        iList = iDBHelper.getAll(true);
        eList = iDBHelper.getAll(false);

        sDBHelper = Databases.getSavingHelper();
        ltList = sDBHelper.getAllLongTerm();
        goalList = sDBHelper.getAllShortTerm();

        parameterList= findViewById(R.id.allParameterList);
        ParameterAdapter paramAdapter = new ParameterAdapter(this, parameterList, iList, eList, ltList, goalList);
        parameterList.setAdapter(paramAdapter);
        parameterList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));

        updateTotal();
    }

    @Override
    public void onBackPressed() {

    }

    public void updateTotal() {
        Databases.getSavingHelper().updatePercentAmounts(Databases.getWeeklyAfterExpenses());
        resetParameterValues();
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

    public void resetParameterValues() {
        for(int i = 0; i<4; i++) {
            if(parameterList.findViewHolderForAdapterPosition(i)!=null) {
                if(Objects.requireNonNull(parameterList.findViewHolderForAdapterPosition(i)).itemView!=null) {
                    int val;

                    switch(i) {
                        case 0:
                            val = Databases.getWeeklyIncome();
                            break;
                        case 1:
                            val = Databases.getWeeklyExpenses();
                            break;
                        case 2:
                            val = Databases.getWeeklySaving(true);
                            break;
                        default:
                            val = Databases.getWeeklySaving(false);
                            break;
                    }
                    (((ParameterAdapter.ViewHolder) Objects.requireNonNull(parameterList.findViewHolderForAdapterPosition(i)))).setParameterValue(Databases.centsToDollar(val));
                }
            }
        }
    }

    public void nextPressed(View v) {
        Databases.setWeeklyAllowance(this, false);
        Intent leaveActivity = new Intent(this, MainLoading.class);
        startActivity(leaveActivity);
    }
}