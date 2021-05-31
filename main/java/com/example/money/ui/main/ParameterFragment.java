package com.example.money.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.money.DatabaseHelper;
import com.example.money.Databases;
import com.example.money.Income;
import com.example.money.ParameterAdapter;
import com.example.money.R;
import com.example.money.Saving;
import com.example.money.Spending;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ParameterFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    private ProgressBar progress;
    private TextView remaining;
    private RecyclerView parameterList;

    private DatabaseHelper dBHelper;
    private List<Income> iList, eList;
    private List<Saving> ltList, goalList;


    //Default method
    public static ParameterFragment newInstance(int index) {
        ParameterFragment fragment = new ParameterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    //Default method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    //First usage
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.parameter_fragment_tab, container, false);


        progress = root.findViewById(R.id.progressFrag);
        remaining = root.findViewById(R.id.totalRemainingFrag);

        //Get parameters
        dBHelper = Databases.getDBHelper();
        iList = dBHelper.getAllIncome(true);
        eList = dBHelper.getAllIncome(false);
        ltList = dBHelper.getAllLongTermSave();
        goalList = dBHelper.getAllShortTermSave();

        //Create parameter recycler view
        parameterList= root.findViewById(R.id.parameterListFrag);
        ParameterAdapter paramAdapter = new ParameterAdapter(getContext(), parameterList, iList, eList, ltList, goalList, this);
        parameterList.setAdapter(paramAdapter);
        parameterList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));

        //update amounts
        updateTotal();

        dBHelper.closeDatabase();
        return root;
    }

    //Updates amounts given parameters
    public void updateTotal() {
        //reset amounts on percentages, and change text
        Databases.getDBHelper().updatePercentAmountsSave(Databases.getWeeklyAfterExpenses());
        resetParameterValues();

        //find remaining income, and set progress bar
        int incomeSubExpense =
                (int)(100*(1- (double) Databases.getWeeklyExpenses()/Databases.getWeeklyIncome()));
        if(incomeSubExpense<=0)
            progress.setSecondaryProgress(0);
        else {
            //set expenses (orange)
            progress.setSecondaryProgress(incomeSubExpense);
            incomeSubExpense =
                    (int)(100*(1- (double) (Databases.getWeeklyExpenses()+Databases.getWeeklySaving())/Databases.getWeeklyIncome()));
            //set savings (green)
            if(incomeSubExpense<=0)
                progress.setProgress(0);
            else
                progress.setProgress(incomeSubExpense);
        }
        remaining.setText("Weekly Allowance: " + Databases.centsToDollar(Databases.getRemaining()));
    }

    //Reset the parameter text
    public void resetParameterValues() {
        for(int i = 0; i<4; i++) {
            if(parameterList.findViewHolderForAdapterPosition(i)!=null) {
                if(Objects.requireNonNull(parameterList.findViewHolderForAdapterPosition(i)).itemView!=null) {
                    int val;

                    //get amount for parameter from Databases
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

                    //set the text
                    (((ParameterAdapter.ViewHolder)
                            Objects.requireNonNull(parameterList.findViewHolderForAdapterPosition(i))))
                            .setParameterValue(Databases.centsToDollar(val));
                }
            }
        }
    }

    //Display snackbar for Saving Goal click (don't edit - too difficult to manage) show goal and date
    public void showDescription(Saving i) {
        if(getActivity()!=null) {
            CoordinatorLayout tabLayout = getActivity().findViewById(R.id.tabCoordinatorLayout);
            String note;
                note = i.getDesc() + " Goal: " + Databases.centsToDollar(i.getLimitStored());

            Snackbar s = Snackbar.make(tabLayout, note, Snackbar.LENGTH_SHORT);
            s.show();
        }
    }


}