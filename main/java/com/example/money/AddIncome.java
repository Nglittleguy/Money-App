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
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class AddIncome extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText incomeInput, periodInput, descriptionInput;
    private TextView showWI;
    private Button button;
    private Spinner incomePeriod;
    private Switch switchTaxes;

    private ConstraintLayout manualPeriodInput;
    private IncomeDBHelper dbHelper;

    private int weeklyIncome, oldID;
    private Boolean edit, taxes;
    private double periodOfWeeks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        //Read Information of Previous Income for Editing
        Intent intent = getIntent();
        edit = intent.getBooleanExtra("Edit", false);
        weeklyIncome = intent.getIntExtra("WeeklyIncome", 0);
        oldID = intent.getIntExtra("OldID", 0);



        //Select Income Period
        incomePeriod = findViewById(R.id.selectIncomePeriod);
        ArrayAdapter<CharSequence> incomePeriodAdapter = ArrayAdapter.createFromResource(
                this, R.array.incomePeriodValues, android.R.layout.simple_spinner_item);
        incomePeriodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incomePeriod.setAdapter(incomePeriodAdapter);
        incomePeriod.setOnItemSelectedListener(this);

        //Switch
        switchTaxes = findViewById(R.id.switchTax);
        switchTaxes.setChecked(false);
        taxes = false;
        switchTaxes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    taxes = true;
                else
                    taxes = false;
                updateWeeklyIncome(periodOfWeeks, getIncomeAmount());
            }
        });

        //Income Input
        incomeInput = findViewById(R.id.incomeAmount);
        incomeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!incomeInput.getText().toString().startsWith("$")) {
                    incomeInput.setText("$" + incomeInput.getText().toString().replace("$", ""));
                    Selection.setSelection(incomeInput.getText(), incomeInput.getText().length());
                }
                try {
                    if(!(periodInput.equals(null) && incomePeriod.getSelectedItemPosition()==4)) {
                        int amountInteger = getIncomeAmount();
                        updateWeeklyIncome(periodOfWeeks, amountInteger);
                    }
                }
                catch (NumberFormatException e) {
                    Log.d("Exception", e.toString());
                    Toast.makeText(AddIncome.this, "Failed to parse income.", Toast.LENGTH_LONG).show();
                    updateWeeklyIncome(periodOfWeeks, 0);
                }
            }
        });

        //Period Input
        periodInput = findViewById(R.id.setIncomePeriod);
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
                    Toast.makeText(AddIncome.this, "Failed to parse period", Toast.LENGTH_LONG).show();
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
                        updateWeeklyIncome(periodOfWeeks, getIncomeAmount());
                    }
                }
                catch (NumberFormatException e) {
                    Log.d("Exception", e.toString());
                    Toast.makeText(AddIncome.this, "Failed to parse period", Toast.LENGTH_LONG).show();
                    updateWeeklyIncome(periodOfWeeks, 0);
                }
            }
        });

        //Manual Income Period Input
        manualPeriodInput = findViewById(R.id.manualIncomePeriod);

        //Showing Weekly Income
        showWI = findViewById(R.id.showIncomeText);
        if(edit) {
            updateWeeklyIncomeView();
            incomeInput.setText(Databases.centsToDollar(weeklyIncome));
        }

        //Button Press
        button = findViewById(R.id.addIncomeButton);
        if(edit)
            button.setText("Update Income");

        //Description Text
        descriptionInput = findViewById(R.id.incomeDesc);
        if(edit)
            descriptionInput.setText(intent.getStringExtra("Description"));

        //Database Helper
        dbHelper = Databases.getIncomeHelper();

    }

    //incomePeriod Spinner Methods
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 4)
            manualPeriodInput.setVisibility(View.VISIBLE);
        else
            manualPeriodInput.setVisibility(View.INVISIBLE);

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
            if(!(periodInput.equals(null) && incomePeriod.getSelectedItemPosition()==4)) {
                if (incomeInput.getText().toString().length() > 1) {
                    double amountDouble = Double.parseDouble(incomeInput.getText().toString().substring(1));
                    if (amountDouble / 100 > Integer.MAX_VALUE)
                        Toast.makeText(AddIncome.this, "Error, too large a weekly amount", Toast.LENGTH_LONG).show();

                    int amountInteger = (int) amountDouble * 100;
                    updateWeeklyIncome(periodOfWeeks, amountInteger);
                }
                else {
                    updateWeeklyIncome(periodOfWeeks, 0);
                }
            }
        }
        catch (NumberFormatException e) {
            Toast.makeText(AddIncome.this, "Failed to parse income.", Toast.LENGTH_LONG).show();
            updateWeeklyIncome(periodOfWeeks, 0);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}


    /*
    Reads income amount from input
     */
    public int getIncomeAmount() {
        int amountInteger = 0;
        try{
            if(incomeInput.getText().toString().length()>0) {
                double amountDouble = Double.parseDouble(incomeInput.getText().toString().substring(1));
                if(amountDouble/100 > Integer.MAX_VALUE)
                    Toast.makeText(AddIncome.this, "Error, too large a weekly amount", Toast.LENGTH_LONG).show();

                amountInteger = (int) amountDouble*100;
                if(taxes) {
                    amountInteger -= (int) Databases.taxAmount(amountInteger);
                }
            }
            else
                amountInteger = 0;
        }
        catch (NumberFormatException e) {
            Toast.makeText(AddIncome.this, "Failed to parse amount", Toast.LENGTH_LONG).show();
            amountInteger = 0;
        }
        return amountInteger;
    }

    /*
    Updates the weekly income
     */
    public void updateWeeklyIncome(double period, int amount) {
        weeklyIncome = (int) (amount/period);
        updateWeeklyIncomeView();
    }

    /*
    Updates the view of the weekly income
    https://www.geeksforgeeks.org/insert-a-string-into-another-string-in-java/
     */
    public void updateWeeklyIncomeView() {
        showWI.setText("Weekly income: " + Databases.centsToDollar(weeklyIncome));
    }

    /*
    Adds the income of the current values to a list
     */
    public void addIncomeToList(View v) {
        if(weeklyIncome==0 || descriptionInput.getText().toString().length()==0)
            return;

        Income i;
        try {
            i = new Income(-1, descriptionInput.getText().toString(), weeklyIncome, 1);
            Boolean success;
            if(edit) {
                success = dbHelper.editOne(i, oldID);
            }
            else
                success = dbHelper.addOne(i);
            Log.d("Success", "Add it "+success);
            Intent leaveActivity = new Intent(this, MainTab.class);
            startActivity(leaveActivity);
        }
        catch (Exception e) {
            Toast.makeText(this, "Failed to add income", Toast.LENGTH_LONG).show();
        }
    }
}