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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class AddExpense extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText expenseInput, periodInput, descriptionInput;
    private TextView showWI;
    private Button button;
    private Spinner expensePeriod;
    private ProgressBar incomeExpenseProgress;

    private ConstraintLayout manualPeriodInput;
    private DecimalFormat ExpenseToText;
    private DatabaseHelper dbHelper;

    private int weeklyExpense, oldID, oldWeeklyExpense;
    private double periodOfWeeks;
    private Boolean edit, update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        //Read Information of Previous Expense for Editing
        Intent intent = getIntent();
        edit = intent.getBooleanExtra("Edit", false);
        weeklyExpense = intent.getIntExtra("WeeklyIncome", 0);
        oldWeeklyExpense = weeklyExpense;
        oldID = intent.getIntExtra("OldID", 0);
        update = intent.getBooleanExtra("Update", false);



        //Progress Bar
        incomeExpenseProgress = findViewById(R.id.addSavingProgress);

        //Select Expense Period
        expensePeriod = findViewById(R.id.selectExpensePeriod);
        ArrayAdapter<CharSequence> expensePeriodAdapter = ArrayAdapter.createFromResource(
                this, R.array.incomePeriodValues, android.R.layout.simple_spinner_item);
        expensePeriodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expensePeriod.setAdapter(expensePeriodAdapter);
        expensePeriod.setOnItemSelectedListener(this);

        //Expense Input
        expenseInput = findViewById(R.id.expenseAmount);
        expenseInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!expenseInput.getText().toString().startsWith("$")) {
                    expenseInput.setText("$" + expenseInput.getText().toString().replace("$", ""));
                    Selection.setSelection(expenseInput.getText(), expenseInput.getText().length());
                }
                try {
                    if(!(periodInput.equals(null) && expensePeriod.getSelectedItemPosition()==4)) {
                        int amountInteger = getExpenseAmount();
                        updateWeeklyExpense(periodOfWeeks, amountInteger);
                    }
                }
                catch (NumberFormatException e) {
                    Log.d("Exception", e.toString());
                    Toast.makeText(AddExpense.this, "Failed to parse expense.", Toast.LENGTH_LONG).show();
                    updateWeeklyExpense(periodOfWeeks, 0);
                }
            }
        });

        //Period Input
        periodInput = findViewById(R.id.setExpensePeriod);
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
                    Toast.makeText(AddExpense.this, "Failed to parse period", Toast.LENGTH_LONG).show();
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
                        updateWeeklyExpense(periodOfWeeks, getExpenseAmount());
                    }
                }
                catch (NumberFormatException e) {
                    Log.d("Exception", e.toString());
                    Toast.makeText(AddExpense.this, "Failed to parse period", Toast.LENGTH_LONG).show();
                    updateWeeklyExpense(periodOfWeeks, 0);
                }

            }
        });


        //Manual Expense Period Input
        manualPeriodInput = findViewById(R.id.manualExpensePeriod);

        //Showing Weekly Expense
        showWI = findViewById(R.id.showExpenseText);
        if(edit) {
            updateWeeklyExpenseView();
            expenseInput.setText(Databases.centsToDollar(weeklyExpense));
        }

        //Button Press
        button = findViewById(R.id.addExpenseButton);
        if(edit)
            button.setText("Update Expense");

        //Description Text
        descriptionInput = findViewById(R.id.expenseDesc);
        if(edit)
            descriptionInput.setText(intent.getStringExtra("Description"));

        //Database Helper
        dbHelper = Databases.getDBHelper();
    }

    //expensePeriod Spinner Methods
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
            if(!(periodInput.equals(null) && expensePeriod.getSelectedItemPosition()==4)) {
                if (expenseInput.getText().toString().length() > 1) {
                    //Find the period from the text
                    double amountDouble = Double.parseDouble(expenseInput.getText().toString().substring(1));
                    if (amountDouble / 100 > Integer.MAX_VALUE)
                        Toast.makeText(AddExpense.this, "Error, too large a weekly amount", Toast.LENGTH_LONG).show();

                    int amountInteger = (int) amountDouble * 100;
                    updateWeeklyExpense(periodOfWeeks, amountInteger);
                }
                else {
                    updateWeeklyExpense(periodOfWeeks, 0);
                }
            }
        }
        catch (NumberFormatException e) {
            Toast.makeText(AddExpense.this, "Failed to parse expense.", Toast.LENGTH_LONG).show();
            updateWeeklyExpense(periodOfWeeks, 0);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    /*
    Reads the expense input
     */
    public int getExpenseAmount() {
        int amountInteger = 0;
        try{
            if(expenseInput.getText().toString().length()>0) {
                double amountDouble = Double.parseDouble(expenseInput.getText().toString().substring(1));
                if(amountDouble/100 > Integer.MAX_VALUE)
                    Toast.makeText(AddExpense.this, "Error, too large a weekly amount", Toast.LENGTH_LONG).show();

                amountInteger = (int) amountDouble*100;
            }
            else
                amountInteger = 0;
        }
        catch (NumberFormatException e) {
            Toast.makeText(AddExpense.this, "Failed to parse amount", Toast.LENGTH_LONG).show();
            amountInteger = 0;
        }
        return amountInteger;
    }

    /*
    Updates the weekly expense
     */
    public void updateWeeklyExpense(double period, int amount) {
        weeklyExpense = (int) (amount/period);
        updateWeeklyExpenseView();
    }

    /*
    Updates the view of the weekly expense
    https://www.geeksforgeeks.org/insert-a-string-into-another-string-in-java/
     */
    public void updateWeeklyExpenseView() {
        updateProgress();
        showWI.setText("Weekly expenses: " + Databases.centsToDollar(weeklyExpense));
    }

    /*
    Adds the expense of the current values to a list
     */
    public void addExpenseToList(View v) {
        if(weeklyExpense==0 || descriptionInput.getText().toString().length()==0)
            return;

        Income i;
        try {
            i = new Income(-1, descriptionInput.getText().toString(), weeklyExpense, -1);
            Log.d("Success", i.toString());
            Boolean success;
            if(edit) {
                success = dbHelper.editOneIncome(i, oldID);
            }
            else
                success = dbHelper.addOneIncome(i);
            Intent leaveActivity;
            if(update)
                leaveActivity = new Intent(this, MainLoading.class);
            else
                leaveActivity = new Intent(this, MainParamCheck.class);
            startActivity(leaveActivity);
        }
        catch (Exception e) {
            Toast.makeText(this, "Failed to add expense", Toast.LENGTH_LONG).show();
            Log.d("Success", e.toString());
        }
    }

    /*
    Updates the Progress Bar with Expenses
     */
    public void updateProgress() {
        int incomeSubExpense =
                (int)(100*(1- (double)
                        (Databases.getWeeklyExpenses()+weeklyExpense-oldWeeklyExpense)
                        /Databases.getWeeklyIncome()));
        if(incomeSubExpense<=0)
            incomeExpenseProgress.setProgress(0);
        else
            incomeExpenseProgress.setProgress(incomeSubExpense);

        if(incomeSubExpense<=0)
            incomeExpenseProgress.setSecondaryProgress(0);
        else {
            incomeExpenseProgress.setSecondaryProgress(incomeSubExpense);
            incomeSubExpense = (int)(100*(1- (double) (Databases.getWeeklyExpenses()+weeklyExpense-oldWeeklyExpense+Databases.getWeeklySaving())/Databases.getWeeklyIncome()));
            if(incomeSubExpense<=0)
                incomeExpenseProgress.setProgress(0);
            else
                incomeExpenseProgress.setProgress(incomeSubExpense);
        }

    }


}
