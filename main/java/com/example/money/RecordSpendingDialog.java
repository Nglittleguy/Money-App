package com.example.money;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.money.ui.main.RecordFragment;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordSpendingDialog extends AppCompatDialogFragment {

    private RecyclerView pastSpent;
    private DatabaseHelper db;
    private Date startEnd = new Date();
    private List<Spending> pastSpentList;
    private RecordFragment parentFrag;
    private String startDate = "";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        db = Databases.getDBHelper();

        Calendar c = Calendar.getInstance();
        c.setTime(startEnd);
        c.add(Calendar.DAY_OF_MONTH, 7);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        pastSpentList = db.getAllSpend(df.format(startEnd), df.format(c.getTime()));
        Log.d("Success", "Start at "+startEnd.toString());
        Log.d("Success", "End at "+c.getTime().toString());
        Log.d("Success", "Num "+pastSpentList.size());

        View v = inflater.inflate(R.layout.dialog_record_spent, null);
        pastSpent = v.findViewById(R.id.pastSpentListDialog);


        builder.setView(v)
                .setTitle("Week of "+startDate)
                .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        SpentAdapter spentAdapter = new SpentAdapter(getContext(), pastSpentList, pastSpent, parentFrag, this);
        pastSpent.setAdapter(spentAdapter);
        pastSpent.setLayoutManager(new LinearLayoutManager(getContext()));
        pastSpent.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));

        return builder.create();
    }


    public void setFrom(Date from, RecordFragment recFrag) {
        startEnd = from;
        SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        startDate += df.format(from);
        parentFrag = recFrag;
    }

    public void showSnackbar(Spending i) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy - h:mm aa", Locale.getDefault());
        if(getActivity()!=null) {
            CoordinatorLayout tabLayout = getActivity().findViewById(R.id.tabCoordinatorLayout);
            String[] descNotes = i.getDesc().split(": ", 2);
            String note = "";
            if(descNotes[1].isEmpty())
                note = "No further description.";
            else
                note = descNotes[1];

            note += (" - "+dateFormat.format(i.getDateTime()));
            Snackbar s = Snackbar.make(getContext(), pastSpent, note, Snackbar.LENGTH_SHORT);
            s.show();
        }
    }
}
