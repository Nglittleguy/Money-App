package com.example.money;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.List;

public class MoveSavingsDialog extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener{

    private DatabaseHelper db;

    private MoveSavingListener mySavingListener;
    private Spinner sources;

    private Saving chosenDest, from;
    private List<Saving> canTakeFrom;

    /*
    Construct dialog to move remaining savings to another location
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        //Take dialog layout from dialog_move_remaining
        View v = inflater.inflate(R.layout.dialog_move_remaining, null);
        builder.setView(v)
                .setTitle("Remaining Funds Management")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Saving destination = chosenDest;
                        mySavingListener.applySaving(chosenDest, from);
                    }
                });

        //Get database
        db = Databases.getDBHelper();

        //Generate list of savings to add to - including weekly allowance
        Saving defaultSaving = new Saving (-1, "Weekly Allowance", 0, 0, 0, 1);
        canTakeFrom = db.getAllLongTermSave();
        canTakeFrom.add(0, defaultSaving);

        //Source spinner
        sources = v.findViewById(R.id.moveFundsSpinner);
        ArrayAdapter<Saving> sourceAdapter = new ArrayAdapter<Saving>(
                getContext(), android.R.layout.simple_spinner_item, canTakeFrom);
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sources.setAdapter(sourceAdapter);
        sources.setOnItemSelectedListener(this);

        return builder.create();
    }

    /*
    Change destination of remaining funds
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenDest = canTakeFrom.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    /*
    Attaches listener to MainTab
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mySavingListener = (MoveSavingListener) context;
        }
        catch (Exception e) {
            throw new ClassCastException(context.toString() + " must implement MoveSavingListener");
        }

    }

    /*
    Set original saving, maintains remaining amount
     */
    public void setFrom(Saving from) {
        this.from = from;
    }

    public interface MoveSavingListener {
        void applySaving(Saving s, Saving take);
    }
}
