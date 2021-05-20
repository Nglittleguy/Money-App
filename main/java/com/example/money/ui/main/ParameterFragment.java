package com.example.money.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.money.Databases;
import com.example.money.Income;
import com.example.money.IncomeDBHelper;
import com.example.money.ParameterAdapter;
import com.example.money.R;
import com.example.money.Saving;
import com.example.money.SavingDBHelper;

import java.util.List;
import java.util.Objects;

public class ParameterFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;


    private ProgressBar progress;
    private TextView remaining;
    private RecyclerView parameterList;
    private List<Income> iList, eList;
    private List<Saving> ltList, goalList;
    private IncomeDBHelper iDBHelper;
    private SavingDBHelper sDBHelper;


    public static ParameterFragment newInstance(int index) {
        ParameterFragment fragment = new ParameterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

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

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.parameter_fragment_tab, container, false);


        progress = root.findViewById(R.id.progressFrag);
        remaining = root.findViewById(R.id.totalRemainingFrag);

        iDBHelper = Databases.getIncomeHelper();
        iList = iDBHelper.getAll(true);
        eList = iDBHelper.getAll(false);

        sDBHelper = Databases.getSavingHelper();
        ltList = sDBHelper.getAllLongTerm();
        goalList = sDBHelper.getAllShortTerm();

        parameterList= root.findViewById(R.id.parameterListFrag);
        ParameterAdapter paramAdapter = new ParameterAdapter(getContext(), parameterList, iList, eList, ltList, goalList, this);
        parameterList.setAdapter(paramAdapter);
        parameterList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));

        updateTotal();


//        final TextView textView = root.findViewById(R.id.section_label);
//        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
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
}