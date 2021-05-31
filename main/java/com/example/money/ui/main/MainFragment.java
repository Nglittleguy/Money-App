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

    TextView remainingText, allowanceText;
    ProgressBar spentBar, overBar;
    ImageButton addSpending;

    DatabaseHelper db;

    int totalWeekly, spent;
    List<Spending> spendingList;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
            dayFormat = new SimpleDateFormat("yyyyMMdd");


    //Default method
    public static MainFragment newInstance(int index) {
        MainFragment fragment = new MainFragment();
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

        View root = inflater.inflate(R.layout.main_fragment_tab, container, false);
        remainingText = root.findViewById(R.id.remainingFrag);
        allowanceText = root.findViewById(R.id.allowanceFrag);
        spentBar = root.findViewById(R.id.spentBarFrag);
        overBar = root.findViewById(R.id.overBarFrag);
        addSpending = root.findViewById(R.id.addSpendButton);

        db = Databases.getDBHelper();

        return root;
    }

    //Reload Fragment
    @Override
    public void onResume() {
        super.onResume();

        Date start = db.getLastDate();

        //if new day, reload
        if(!(dayFormat.format(start)).equals(dayFormat.format(new Date()))) {
            ((MainTab)getContext()).forceLoading();
            return;
        }

        //update allowance and spent
        totalWeekly = db.getWeeklyAllowance();
        spent = updateTotal();
    }

    //Update the total spent - and reload record
    public int updateTotal() {
        int total=0, ratio = 0;

        //get amount spent since start of week
        spendingList = db.getAllSpendFromWA(true, ((MainTab)(getContext())).getStartOfWeek());
        for(Spending s: spendingList)
            total+=s.getAmount();

        //if spent is less than weekly allowance, green progress bar - else, red progress bar
        if(total<totalWeekly) {
            spentBar.setVisibility(View.VISIBLE);
            overBar.setVisibility(View.INVISIBLE);

            //if non-zero allowance, find the ratio to set the progress bar
            if(totalWeekly!=0) {
                ratio = 100 * (totalWeekly - total) / totalWeekly;
                spentBar.setProgress(ratio);

                //if more money than allowance, set green text
                if(ratio>100)
                    remainingText.setTextColor(getActivity().getResources().getColor(R.color.myGreen));
            }
            else
                spentBar.setProgress(0);

        }
        else {
            spentBar.setVisibility(View.INVISIBLE);
            overBar.setVisibility(View.VISIBLE);

            //if non-zero allowance and total spent exceeds the total allowance twice over, set red text
            if(total>2*totalWeekly && totalWeekly!=0) {
                ratio = -100* (total - totalWeekly) / totalWeekly;
                overBar.setProgress(100);
                remainingText.setTextColor(getActivity().getResources().getColor(R.color.myRed));
            }
            else {
                //if non-zero allowance (so spent does not exceed twice over), set progress and ratio
                if(totalWeekly!=0) {
                    ratio = -100 * (total - totalWeekly) / totalWeekly;
                    overBar.setProgress(-ratio);
                }
                else
                    overBar.setProgress(0);
            }
        }
        //change the ratio that in the record to a new ratio
        SpentRecord s = new SpentRecord(-1, ratio);
        db.editCurrentRecord(s);

        remainingText.setText(Databases.centsToDollar(totalWeekly-total));
        allowanceText.setText("Allowance per week: "+Databases.centsToDollar(totalWeekly));

        return total;
    }


}