package com.example.money.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.money.DatabaseHelper;
import com.example.money.Databases;
import com.example.money.MainTab;
import com.example.money.R;
import com.example.money.Spending;
import com.example.money.SpendingDBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    List<Spending> spendingList;
    DatabaseHelper db;
    TextView remainingText, allowanceText;
    int totalWeekly, spent;
    ProgressBar spentBar, overBar;
    ImageButton addSpending;


    public static MainFragment newInstance(int index) {
        MainFragment fragment = new MainFragment();
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
        View root = inflater.inflate(R.layout.main_fragment_tab, container, false);
//        final TextView textView = root.findViewById(R.id.section_label);
//        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        remainingText = root.findViewById(R.id.remainingFrag);
        allowanceText = root.findViewById(R.id.allowanceFrag);

        spentBar = root.findViewById(R.id.spentBarFrag);
        overBar = root.findViewById(R.id.overBarFrag);
        addSpending = root.findViewById(R.id.addSpendButton);
        db = Databases.getDBHelper();
        Calendar c = Calendar.getInstance();
        totalWeekly = db.getWeeklyAllowance();
        spent = updateTotal();

        return root;
    }

    @Override
    public void onResume() {
        db = Databases.getDBHelper();
        Calendar c = Calendar.getInstance();
        totalWeekly = db.getWeeklyAllowance();
        spent = updateTotal();
        super.onResume();
    }

    public int updateTotal() {
        int total=0;
        spendingList = db.getAllSpend(true, ((MainTab)(getContext())).getStartOfWeek());
        for(Spending s: spendingList) {
            total+=s.getAmount();
        }
        if(spent<totalWeekly) {
            spentBar.setVisibility(View.VISIBLE);
            overBar.setVisibility(View.INVISIBLE);
            if(totalWeekly!=0)
                spentBar.setProgress(100*(totalWeekly-spent)/totalWeekly);
            else
                spentBar.setProgress(0);

        }
        else {
            spentBar.setVisibility(View.INVISIBLE);
            overBar.setVisibility(View.VISIBLE);
            if(spent>2*totalWeekly)
                overBar.setProgress(100);
            else {
                if(totalWeekly!=0)
                    overBar.setProgress(100 * (spent - totalWeekly) / totalWeekly);
                else
                    overBar.setProgress(0);
            }
        }
        remainingText.setText(Databases.centsToDollar(totalWeekly-spent));
        allowanceText.setText("Allowance per week: "+Databases.centsToDollar(totalWeekly));

        return total;
    }


}