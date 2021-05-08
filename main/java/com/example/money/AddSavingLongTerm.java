package com.example.money;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.Slider;

public class AddSavingLongTerm extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Switch switchAP;
    private Boolean aP, edit;
    private ConstraintLayout amountLayout;
    private ConstraintLayout percentLayout;
    private ConstraintLayout manualPeriodInput;
    private Spinner savingPeriod;
    private double periodOfWeeks;
    private EditText savingInput;
    private EditText percentInput;
    private EditText periodInput;
    private EditText descriptionInput;
    private TextView showWS;
    private Boolean suffixAdded1;
    private double percent;
    private Button button;
    private int oldID;
    private SavingDBHelper dbHelper;
    private int weeklySaving;
    private SeekBar percentSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_saving_long_term);

        Log.d("Success", "Opening add saving");
        //Constraints
        manualPeriodInput = findViewById(R.id.manualSavingLTPeriod);
        amountLayout = findViewById(R.id.amountLayout);
        percentLayout = findViewById(R.id.percentLayout);


        //Select Saving Period
        savingPeriod = findViewById(R.id.selectSavingLTPeriod);
        ArrayAdapter<CharSequence> savingPeriodAdapter = ArrayAdapter.createFromResource(
                this, R.array.incomePeriodValues, android.R.layout.simple_spinner_item);
        savingPeriodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        savingPeriod.setAdapter(savingPeriodAdapter);
        savingPeriod.setOnItemSelectedListener(this);

        periodOfWeeks = 1;


        //Database Helper
        dbHelper = Databases.getSavingHelper();


        //Switch
        switchAP = findViewById(R.id.amountVPercent);
        switchAP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    aP = true;
                    amountLayout.setVisibility(View.INVISIBLE);
                    manualPeriodInput.setVisibility(View.INVISIBLE);
                    percentLayout.setVisibility(View.VISIBLE);

                }
                else {
                    aP = false;
                    amountLayout.setVisibility(View.VISIBLE);
                    percentLayout.setVisibility(View.INVISIBLE);
                    if(savingPeriod.getSelectedItemPosition()==4)
                        manualPeriodInput.setVisibility(View.VISIBLE);

                }
            }
        });

        //Setting Edit Values
        Intent intent = getIntent();
        edit = intent.getBooleanExtra("Edit", false);
        weeklySaving = intent.getIntExtra("WeeklySaving", 0);
        oldID = intent.getIntExtra("OldID", 0);
        percent = intent.getDoubleExtra("Percent", 0);

        Log.d("Success", "Percent is "+percent);

        if(percent!=0) {
            switchAP.setChecked(true);
            aP = true;
            amountLayout.setVisibility(View.INVISIBLE);
            manualPeriodInput.setVisibility(View.INVISIBLE);
            percentLayout.setVisibility(View.VISIBLE);
        }
        else {
            switchAP.setChecked(false);
            aP = false;
            amountLayout.setVisibility(View.VISIBLE);
            percentLayout.setVisibility(View.INVISIBLE);
            if(savingPeriod.getSelectedItemPosition()==4)
                manualPeriodInput.setVisibility(View.VISIBLE);
        }


        //Saving Amount Input
        savingInput = findViewById(R.id.savingLTAmount);


        //Showing Weekly Expense
        showWS = findViewById(R.id.showSavingLTText);
        if(edit) {
            if(aP) {
                Log.d("Success", "updating percent");
                updateWeeklySaving(percent);
            }
            else {
                updateWeeklySavingView();
                savingInput.setText(Databases.centsToDollar(weeklySaving));
            }
        }


        //Saving Amount Input
        savingInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!savingInput.getText().toString().startsWith("$")) {
                    savingInput.setText("$" + savingInput.getText().toString().replace("$", ""));
                    Selection.setSelection(savingInput.getText(), savingInput.getText().length());
                }
                try {
                    if(!(periodInput.equals(null) && savingPeriod.getSelectedItemPosition()==4)) {
                        int amountInteger = getSavingAmount();
                        updateWeeklySaving(periodOfWeeks, amountInteger);
                    }
                }
                catch (NumberFormatException e) {
                    Log.d("Exception", e.toString());
                    Toast.makeText(AddSavingLongTerm.this, "Failed to parse saving.", Toast.LENGTH_LONG).show();
                    updateWeeklySaving(periodOfWeeks, 0);
                }
            }
        });



        //Saving Percent Input
        percentInput = findViewById(R.id.percentOfRemaining);
        percentInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("%")) {
                    percentInput.setText("");
                    return;
                }
                try{
                    if(s.length()>0 && !s.toString().contains("%") && !s.toString().equals("%")) {

                        String text = s.toString().concat("%");
                        percentInput.setText(text);
                        percentInput.setSelection(text.length() - 1);
                        suffixAdded1 = true;
                    }
                }
                catch (NumberFormatException e) {
                    Log.d("Exception", e.toString());
                    Toast.makeText(AddSavingLongTerm.this, "Failed to parse percent", Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0)
                    suffixAdded1 = false;
                try {
                    if(percentInput.getText().toString().length()>1) {
                        percent = ((double) Integer.parseInt(percentInput.getText().toString()
                                .substring(0, percentInput.getText().toString().length() - 1)));
                        percentSlider.setProgress((int)(percent/5));
                    }

                }
                catch (NumberFormatException e) {
                    Log.d("Exception", e.toString());
                    Toast.makeText(AddSavingLongTerm.this, "Failed to parse percent", Toast.LENGTH_LONG).show();
                }
                if(aP)
                    updateWeeklySaving(percent);
            }
        });


        //Saving Percent Slider
        percentSlider = findViewById(R.id.percentRemainingSlider);
        percentSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                percentInput.setText(String.valueOf(progress*5));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        if(edit)
            percentSlider.setProgress((int)(percent/5));



        //Period Input
        periodInput = findViewById(R.id.setSavingLTPeriod);
        periodInput.addTextChangedListener(new TextWatcher() {
            /*
                https://stackoverflow.com/questions/34596536/android-edittext-with-suffix -
                by kandroidj, Jan 4, 2016
             */

            private boolean suffixAdded = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals(" days")) {
                    periodInput.setText("");
                    return;
                }
                try{
                    if(s.length()>0 && !s.toString().contains(" days") && !s.toString().equals(" days")) {

                        String text = s.toString().concat(" days");
                        periodInput.setText(text);
                        periodInput.setSelection(text.length() - 5);
                        suffixAdded = true;
                    }
                }
                catch (NumberFormatException e) {
                    Log.d("Exception", e.toString());
                    Toast.makeText(AddSavingLongTerm.this, "Failed to parse period", Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0)
                    suffixAdded = false;
                try {
                    if(periodInput.getText().toString().length()>5) {
                        periodOfWeeks = ((double) Integer.parseInt(periodInput.getText().toString()
                                .substring(0, periodInput.getText().toString().length() - 5))) / 7;
                    }

                }
                catch (NumberFormatException e) {
                    Log.d("Exception", e.toString());
                    Toast.makeText(AddSavingLongTerm.this, "Failed to parse period", Toast.LENGTH_LONG).show();
                }
                updateWeeklySaving(periodOfWeeks, getSavingAmount());
            }
        });


        //Button Press
        button = findViewById(R.id.addSavingLTButton);
        if(edit)
            button.setText("Update Saving");


        //Description Text
        descriptionInput = findViewById(R.id.savingLTDesc);
        if(edit)
            descriptionInput.setText(intent.getStringExtra("Description"));

    }

    //savingPeriod Spinner Methods
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        if(position == 4) {
            manualPeriodInput.setVisibility(View.VISIBLE);
        }
        else {
            manualPeriodInput.setVisibility(View.INVISIBLE);
        }

        switch(position) {
            case 0:
                periodOfWeeks = 1;
                break;
            case 1:
                periodOfWeeks = 2;
                break;
            case 2:
                periodOfWeeks = 4;
                break;
            case 3:
                periodOfWeeks = 52;
                break;
        }

        try {
            if(!(periodInput.equals(null) && savingPeriod.getSelectedItemPosition()==4)) {
                if (savingInput.getText().toString().length() > 1) {
                    double amountDouble = Double.parseDouble(savingInput.getText().toString().substring(1));
                    if (amountDouble / 100 > Integer.MAX_VALUE)
                        Toast.makeText(AddSavingLongTerm.this, "Error, too large a weekly amount", Toast.LENGTH_LONG).show();

                    int amountInteger = (int) amountDouble * 100;
                    updateWeeklySaving(periodOfWeeks, amountInteger);
                }
                else {
                    if(!edit || percent==0 || weeklySaving==0)
                        updateWeeklySaving(periodOfWeeks, 0);
                }
            }
        }
        catch (NumberFormatException e) {
            Toast.makeText(AddSavingLongTerm.this, "Failed to parse saving.", Toast.LENGTH_LONG).show();
            updateWeeklySaving(periodOfWeeks, 0);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    public int getSavingAmount() {
        int amountInteger = 0;
        try{
            if(savingInput.getText().toString().length()>0) {
                double amountDouble = Double.parseDouble(savingInput.getText().toString().substring(1));
                if(amountDouble/100 > Integer.MAX_VALUE)
                    Toast.makeText(AddSavingLongTerm.this, "Error, too large a weekly amount", Toast.LENGTH_LONG).show();

                amountInteger = (int) amountDouble*100;
            }
            else
                amountInteger = 0;

        }
        catch (NumberFormatException e) {
            Toast.makeText(AddSavingLongTerm.this, "Failed to parse amount", Toast.LENGTH_LONG).show();
        }
        return amountInteger;
    }

    /*
    Updates the weekly savings
     */
    public void updateWeeklySaving(double period, int amount) {
        weeklySaving = (int) (amount/period);
        updateWeeklySavingView();
    }

    public void updateWeeklySaving(double percent) {
        weeklySaving = (int) percent*Databases.getWeeklyAfterExpenses()/100;
        updateWeeklySavingView();
    }

    /*
    Updates the view of the weekly expense
    https://www.geeksforgeeks.org/insert-a-string-into-another-string-in-java/
     */
    public void updateWeeklySavingView() {
        showWS.setText("Weekly Savings: " + Databases.centsToDollar(weeklySaving));
    }

    /*
    Adds the saving of the current values to a list
     */
    public void addSavingLTToList(View v) {
        if(weeklySaving==0 || descriptionInput.getText().toString().length()==0 || (aP&&percent==0))
            return;

        Saving s;
        try {
            if(!aP)
                s = new Saving (-1, descriptionInput.getText().toString(), Long.MAX_VALUE, 0, weeklySaving, 0, 1);
            else
                s = new Saving (-1, descriptionInput.getText().toString(), Long.MAX_VALUE, 0, weeklySaving, percent, 1);

            Log.d("Success", s.toString());
            Boolean success;
            if(edit) {
                success = dbHelper.editOne(s, oldID);
            }
            else
                success = dbHelper.addOne(s);
            Intent leaveActivity = new Intent(this, MainAddSavingLT.class);
            startActivity(leaveActivity);
        }
        catch (Exception e) {
            Toast.makeText(this, "Failed to add expense", Toast.LENGTH_LONG).show();
            Log.d("Success", e.toString());
        }
    }

}