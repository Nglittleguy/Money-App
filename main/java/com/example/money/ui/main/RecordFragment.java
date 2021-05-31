package com.example.money.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.money.DatabaseHelper;
import com.example.money.Databases;
import com.example.money.MainTab;
import com.example.money.R;
import com.example.money.RecordAdapter;
import com.example.money.Spending;
import com.example.money.SpentAdapter;
import com.example.money.SpentRecord;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RecordFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    private RecyclerView weeklySpend;
    private ImageButton exportButton;

    private DatabaseHelper db;

    private List<SpentRecord> spentList;


    //Default method
    public static RecordFragment newInstance(int index) {
        RecordFragment fragment = new RecordFragment();
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

    //First Usage
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.record_fragment_tab, container, false);

        weeklySpend = root.findViewById(R.id.totalSpentListFrag);
        exportButton = root.findViewById(R.id.exportButton);
        exportButton.setOnClickListener(this);
        return root;
    }

    //Reload Fragment
    @Override
    public void onResume() {
        db = Databases.getDBHelper();
        spentList = db.getAllRecord();

        //set recycler view for records, scroll to bottom
        RecordAdapter recAdapter = new RecordAdapter(getContext(), spentList, weeklySpend, this);
        weeklySpend.setAdapter(recAdapter);
        weeklySpend.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        weeklySpend.scrollToPosition(spentList.size()-1);

        super.onResume();
    }

    //Export data if clicked export button
    @Override
    public void onClick(View v) {
        ((MainTab)getContext()).exportData();
    }

}