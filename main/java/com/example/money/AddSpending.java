package com.example.money;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddSpending extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView title;
    private Switch plusOrMinus, necOrDesire;
    private Spinner descSpinner, sourceSpinner;
    private EditText amountInput, descInput;

    private DatabaseHelper db;

    private int savingChosenPosition, amount;
    private String descMain;
    private boolean subMoney, necessity;
    private List<Saving> canTakeFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spending);

        db = Databases.getDBHelper();

        //Title and Description
        title = findViewById(R.id.spendingTitle);
        descMain = "";

        //Description Spinner
        descSpinner = findViewById(R.id.selectSpendingDesc);
        ArrayAdapter<CharSequence> descSpinAdapter = ArrayAdapter.createFromResource(
                this, R.array.spendingTypeValues, android.R.layout.simple_spinner_item);
        descSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        descSpinner.setAdapter(descSpinAdapter);
        descSpinner.setOnItemSelectedListener(this);

        //Spend or Income Switch
        plusOrMinus = findViewById(R.id.spendOrIncome);
        plusOrMinus.setChecked(true);
        subMoney = true;
        plusOrMinus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    subMoney = true;
                else {
                    descSpinner.setSelection(9);
                    subMoney = false;
                }
                updateText();
            }
        });

        //Necessity or Desire Switch
        necOrDesire = findViewById(R.id.necessityOrDesire);
        necOrDesire.setChecked(true);
        necessity = true;
        necOrDesire.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    necessity = true;
                else
                    necessity = false;
                updateText();
            }
        });

        //Amount Input
        amountInput = findViewById(R.id.spendingAmount);
        amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!amountInput.getText().toString().startsWith("$")) {
                    amountInput.setText("$" + amountInput.getText().toString().replace("$", ""));
                    Selection.setSelection(amountInput.getText(), amountInput.getText().length());
                }
                try {
                    amount = getSpendingAmount();
                }
                catch (NumberFormatException e) {
                    Log.d("Exception", e.toString());
                    Toast.makeText(AddSpending.this, "Failed to parse input", Toast.LENGTH_LONG).show();
                    amount = 0;
                }
            }
        });



        //Source Spinner
        sourceSpinner = findViewById(R.id.selectSource);
        Saving defaultSaving = new Saving (-1, "Weekly Allowance", 0, 0, 0, 1);

            //List of Savings that can be taken from, + weekly allowance
        canTakeFrom = db.getAllRemoveableSave();
        canTakeFrom.add(0, defaultSaving);

        ArrayAdapter<Saving> sourceAdapter = new ArrayAdapter<Saving>(
                this, android.R.layout.simple_spinner_item, canTakeFrom);
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceSpinner.setAdapter(sourceAdapter);
        sourceSpinner.setOnItemSelectedListener(this);

        //Description Text Input
        descInput = findViewById(R.id.spendingDesc);
    }

    /*
    Spinner selection - both source and description
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //if description
        if(parent.equals(descSpinner)) {
            switch(position) {
                case 0:                             //Groceries
                    necOrDesire.setChecked(true);
                    break;
                case 1:                             //Dining
                    necOrDesire.setChecked(false);
                    break;
                case 2:                             //Entertainment
                    necOrDesire.setChecked(false);
                    break;
                case 3:                             //Gift
                    necOrDesire.setChecked(false);
                    break;
                case 4:                             //Home
                    necOrDesire.setChecked(true);
                    break;
                case 5:                             //Self
                    necOrDesire.setChecked(true);
                    break;
                case 6:                             //Hobby
                    necOrDesire.setChecked(false);
                    break;
                case 7:                             //Repair
                    necOrDesire.setChecked(true);
                    break;
                case 8:                             //Utility
                    necOrDesire.setChecked(true);
                    break;
                default:                            //Other
                    necOrDesire.setChecked(false);
            }
            //set Necessity and Description
            descMain = getResources().getStringArray(R.array.spendingTypeValues)[position]+": ";
        }
        //if source, set source
        if(parent.equals(sourceSpinner)) {
            savingChosenPosition = position;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    /*
   Reads income amount from input
    */
    public int getSpendingAmount() {
        int amountInteger = 0;
        try{
            if(amountInput.getText().toString().length()>0) {
                double amountDouble = Double.parseDouble(amountInput.getText().toString().substring(1));
                if(amountDouble/100 > Integer.MAX_VALUE)
                    Toast.makeText(this, "Error, too large a weekly amount", Toast.LENGTH_LONG).show();

                amountInteger = (int) amountDouble*100;
            }
            else
                amountInteger = 0;
        }
        catch (NumberFormatException e) {
            Log.e("Exception", e.toString());
            Toast.makeText(this, "Failed to parse amount", Toast.LENGTH_LONG).show();
            amountInteger = 0;
        }
        return amountInteger;
    }

    //Changes the text to show update or subtract
    public void updateText() {
        if(subMoney) {
            title.setText(R.string.spendingTitleValue);
            plusOrMinus.setText(R.string.spendValue);
        }
        else {
            title.setText(R.string.incomeTitleValue);
            plusOrMinus.setText(R.string.notSpendValue);
        }

        if(necessity)
            necOrDesire.setText(R.string.necessityValue);
        else
            necOrDesire.setText(R.string.notNecessityValue);
    }

    /*
    Adds the spending of the current values to a list
     */
    public void addSpendingToList(View v) {
        if(amount==0)
            return;

        Spending i;
        String descNote;
        if(!subMoney)
            amount*=-1;
        try {
            if(descInput.getText().toString().length()==0)
                descNote = "";
            else
                descNote = descInput.getText().toString();

            if(savingChosenPosition==0)
                i = new Spending(-1, descMain + descNote, amount, necessity);

            else {
                i = new Spending(-1,
                        canTakeFrom.get(savingChosenPosition).getDesc() + ": " +
                                descNote.split(" -", 2)[0], amount, necessity, true);
                db.takeFromSavings(canTakeFrom.get(savingChosenPosition), amount);
            }
            db.addOneSpend(i);

            Intent leaveActivity = new Intent(this, MainLoading.class);
            startActivity(leaveActivity);
        }
        catch (Exception e) {
            Toast.makeText(this, "Failed to add spending", Toast.LENGTH_LONG).show();
        }
    }
}