package com.example.money.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.money.SpentRecord;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
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
        remainingText = root.findViewById(R.id.remainingFrag);
        allowanceText = root.findViewById(R.id.allowanceFrag);

        spentBar = root.findViewById(R.id.spentBarFrag);
        overBar = root.findViewById(R.id.overBarFrag);
        addSpending = root.findViewById(R.id.addSpendButton);
        db = Databases.getDBHelper();
        totalWeekly = db.getWeeklyAllowance();
        spent = updateTotal();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Date start = db.getLastDate();
        if(!(dayFormat.format(start)).equals(dayFormat.format(new Date()))) {
            Log.d("Success", "Last start was "+dayFormat.format(start));
            Log.d("Success", "Now is "+dayFormat.format(new Date()));
            ((MainTab)getContext()).forceLoading();
            return;
        }
        totalWeekly = db.getWeeklyAllowance();
        spent = updateTotal();
    }

    public int updateTotal() {
        int total=0, ratio = 0;

        spendingList = db.getAllSpendFromWA(true, ((MainTab)(getContext())).getStartOfWeek());
        for(Spending s: spendingList) {
            total+=s.getAmount();
        }
        if(spent<totalWeekly) {
            spentBar.setVisibility(View.VISIBLE);
            overBar.setVisibility(View.INVISIBLE);
            if(totalWeekly!=0) {
                ratio = 100 * (totalWeekly - spent) / totalWeekly;
                spentBar.setProgress(ratio);
                if(ratio>100)
                    remainingText.setTextColor(getActivity().getResources().getColor(R.color.myGreen));
            }
            else
                spentBar.setProgress(0);

        }
        else {
            spentBar.setVisibility(View.INVISIBLE);
            overBar.setVisibility(View.VISIBLE);
            if(spent>2*totalWeekly) {
                ratio = -100* (spent - totalWeekly) / totalWeekly;
                overBar.setProgress(100);
                remainingText.setTextColor(getActivity().getResources().getColor(R.color.myRed));
            }
            else {
                if(totalWeekly!=0) {
                    ratio = -100 * (spent - totalWeekly) / totalWeekly;
                    overBar.setProgress(-ratio);
                }
                else
                    overBar.setProgress(0);
            }
        }
        SpentRecord s = new SpentRecord(-1, ratio);
        db.editCurrentRecord(s);
        remainingText.setText(Databases.centsToDollar(totalWeekly-spent));
        allowanceText.setText("Allowance per week: "+Databases.centsToDollar(totalWeekly));

        return total;
    }


}