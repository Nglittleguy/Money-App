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
import androidx.viewpager.widget.ViewPager;

import com.example.money.DatabaseHelper;
import com.example.money.Databases;
import com.example.money.MainTab;
import com.example.money.R;
import com.example.money.Spending;
import com.example.money.SpendingDBHelper;
import com.example.money.SpentAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SpenditureFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    RecyclerView weeklySpend;
    TextView spentAmount;

    DatabaseHelper db;

    List<Spending> spendingList;

    //Default method
    public static SpenditureFragment newInstance(int index) {
        SpenditureFragment fragment = new SpenditureFragment();
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

        View root = inflater.inflate(R.layout.spenditure_fragment_tab, container, false);

        weeklySpend = root.findViewById(R.id.weekSpentListFrag);
        spentAmount = root.findViewById(R.id.totalSpentFrag);

        return root;
    }

    //Reload fragment
    @Override
    public void onResume() {
        db = Databases.getDBHelper();

        //set recycler view for spenditure
        spendingList = db.getAllSpend(true, ((MainTab)(getContext())).getStartOfWeek());
        SpentAdapter spentAdapter = new SpentAdapter(getContext(), spendingList, weeklySpend, this);
        weeklySpend.setAdapter(spentAdapter);
        weeklySpend.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));

        updateTotal();

        super.onResume();
    }


    //Update the total spent during this week
    public void updateTotal() {
        int total=0;
        for(Spending s: spendingList)
            total+=s.getAmount();

        //set the text
        spentAmount.setText("Since "+((MainTab)(getContext())).getStartOfWeek(2)+": "+Databases.centsToDollar(total));
    }

    //Show description note of spenditure when pressed
    public void showSnackbar(Spending i) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy - h:mm aa", Locale.getDefault());

        if(getActivity()!=null) {
            CoordinatorLayout tabLayout = getActivity().findViewById(R.id.tabCoordinatorLayout);

            //split description to just have note
            String[] descNotes = i.getDesc().split(": ", 2);
            String note = "";
            if(descNotes[1].isEmpty())
                note = "No further description.";
            else
                note = descNotes[1];

            //display note of the description and the date time when purchased
            note += (" - "+dateFormat.format(i.getDateTime()));
            Snackbar s = Snackbar.make(tabLayout, note, Snackbar.LENGTH_SHORT);
            s.show();
        }
    }
}