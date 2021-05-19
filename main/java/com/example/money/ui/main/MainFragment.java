package com.example.money.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.money.Databases;
import com.example.money.R;
import com.example.money.Spending;
import com.example.money.SpendingDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    List<Spending> spendingList;
    SpendingDBHelper db;
    TextView remainingText, allowanceText;
    int totalWeekly, spent;
    ProgressBar spentBar, overBar;


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


        return root;
    }

    @Override
    public void onResume() {
        db = Databases.getSpendingHelper();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 2);
        spendingList = db.getAll(true, getStartOfWeek());

        totalWeekly = db.getWeeklyAllowance();
        spent = initList();
        if(spent<totalWeekly) {
            spentBar.setVisibility(View.VISIBLE);
            overBar.setVisibility(View.INVISIBLE);
            spentBar.setProgress(100*(totalWeekly-spent)/totalWeekly);

        }
        else {
            spentBar.setVisibility(View.INVISIBLE);
            overBar.setVisibility(View.VISIBLE);
            if(spent>2*totalWeekly)
                overBar.setProgress(100);
            else
                overBar.setProgress(100*(spent-totalWeekly)/totalWeekly);
        }
        remainingText.setText(Databases.centsToDollar(totalWeekly-spent));
        allowanceText.setText("Allowance per week: "+Databases.centsToDollar(totalWeekly));
        super.onResume();
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
        return total;
    }
}