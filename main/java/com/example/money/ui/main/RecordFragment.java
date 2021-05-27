package com.example.money.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class RecordFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;


    List<SpentRecord> spentList;
    DatabaseHelper db;
    RecyclerView weeklySpend;

    public static RecordFragment newInstance(int index) {
        RecordFragment fragment = new RecordFragment();
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
            View root = inflater.inflate(R.layout.record_fragment_tab, container, false);

        weeklySpend = root.findViewById(R.id.totalSpentListFrag);
        return root;
    }

    @Override
    public void onResume() {

        db = Databases.getDBHelper();
        spentList = db.getAllRecord();
        RecordAdapter recAdapter = new RecordAdapter(getContext(), spentList, weeklySpend);
        weeklySpend.setAdapter(recAdapter);
        weeklySpend.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        super.onResume();
    }

//
//    public void showSnackbar(Spending i) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy - h:mm aa", Locale.getDefault());
//        if(getActivity()!=null) {
//            CoordinatorLayout tabLayout = getActivity().findViewById(R.id.tabCoordinatorLayout);
//            String[] descNotes = i.getDesc().split(": ", 2);
//            String note = "";
//            if(descNotes[1].isEmpty())
//                note = "No further description.";
//            else
//                note = descNotes[1];
//
//            note += (" - "+dateFormat.format(i.getDateTime()));
//            Snackbar s = Snackbar.make(tabLayout, note, Snackbar.LENGTH_SHORT);
//            s.show();
//        }
//    }
}