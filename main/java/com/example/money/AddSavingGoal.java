package com.example.money;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddSavingGoal extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private TextView dateSelector, showWS;
    private EditText descriptionInput, savingInput;
    private Button button;
    private ProgressBar incomeExpenseSavingProgress;
    private SavingDBHelper dbHelper;
    private Calendar currentDay, selectedDay;
    private int weeksDiff, weeklySaving, oldSaving, oldID;
    private String oldDesc;
    private int[] oldDay;
    private boolean edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_saving_goal);




        //Read Information of Previous Income for Editing
        Intent intent = getIntent();
        edit = intent.getBooleanExtra("Edit", false);
        weeklySaving = intent.getIntExtra("WeeklySaving", 0);
        oldSaving = weeklySaving;
        oldID = intent.getIntExtra("OldID", 0);
        oldDesc = intent.getStringExtra("Description");
        if(edit && oldDesc.length()>12) {
            int end = oldDesc.length();
            oldDay = new int[] {Integer.parseInt(oldDesc.substring(end-10, end-6)),
                    Integer.parseInt(oldDesc.substring(end-5, end-3)),
                    Integer.parseInt(oldDesc.substring(end-2))};
        }
        currentDay = Calendar.getInstance();
        selectedDay = Calendar.getInstance();
        if(edit && oldDay.length>3) {
            selectedDay.set(Calendar.YEAR, oldDay[0]);
            selectedDay.set(Calendar.MONTH, oldDay[1]);
            selectedDay.set(Calendar.DAY_OF_MONTH, oldDay[2]);
        }
        weeksDiff = 0;

        dateSelector = findViewById(R.id.dateSelector);
        dateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        //Progress Bar
        incomeExpenseSavingProgress = findViewById(R.id.addSavingGoalProgress);

        //Database Helper
        dbHelper = Databases.getSavingHelper();


        //Saving Amount Input
        savingInput = findViewById(R.id.savingGoalAmount);

        //Showing Weekly Saving
        showWS = findViewById(R.id.showSavingGoalText);
        if(edit) {
            updateWeeklySavingView();
            savingInput.setText(Databases.centsToDollar(weeklySaving));
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
                    updateWeeklySaving();
                }
                catch (NumberFormatException e) {
                    Log.d("Exception", e.toString());
                    Toast.makeText(AddSavingGoal.this, "Failed to parse saving.", Toast.LENGTH_LONG).show();
                    updateWeeklySaving(0);
                }
            }
        });

        descriptionInput = findViewById(R.id.savingGoalDesc);
        if(edit) {
            if(oldDesc!=null && oldDesc.length()>12)
                descriptionInput.setText(oldDesc.substring(0, oldDesc.length()-12));
        }

        //Button Press
        button = findViewById(R.id.addSavingGoalButton);
        if(edit)
            button.setText("Update Saving");


    }

    private void showDateDialog() {
        int year, month, day;
        year = selectedDay.get(Calendar.YEAR);
        month = selectedDay.get(Calendar.MONTH);
        day = selectedDay.get(Calendar.DAY_OF_MONTH);



        DatePickerDialog dialog = new DatePickerDialog(
                this, this, year, month, day);
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        if((currentDay.get(Calendar.WEEK_OF_YEAR)<c.get(Calendar.WEEK_OF_YEAR)
                && currentDay.get(Calendar.YEAR)==year)
                || currentDay.get(Calendar.YEAR)<year) {
            weeksDiff = differenceInWeeks(currentDay, c);
            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
            dateSelector.setText("By "+format.format(c.getTime()));
            selectedDay.setTime(c.getTime());
        }
        updateWeeklySaving();
    }

    public void updateWeeklySaving() {
        if(weeksDiff != 0)
            weeklySaving = (int) Math.ceil((double)getSavingAmount()/weeksDiff);
        else
            weeklySaving = 0;
        updateWeeklySavingView();
    }

    public void updateWeeklySaving(int wS) {
        weeklySaving = wS;
        updateWeeklySavingView();
    }

    /*
    Updates the view of the weekly expense
    https://www.geeksforgeeks.org/insert-a-string-into-another-string-in-java/
     */
    public void updateWeeklySavingView() {
        updateProgress();
        showWS.setText("Weekly Savings: " + Databases.centsToDollar(weeklySaving));
    }

    /*
    Read saving amount from input
     */
    public int getSavingAmount() {
        int amountInteger = 0;
        try{
            if(savingInput.getText().toString().length()>0) {
                double amountDouble = Double.parseDouble(savingInput.getText().toString().substring(1));
                if(amountDouble/100 > Integer.MAX_VALUE)
                    Toast.makeText(AddSavingGoal.this, "Error, too large a weekly amount", Toast.LENGTH_LONG).show();

                amountInteger = (int) amountDouble*100;
            }
            else
                amountInteger = 0;
        }
        catch (NumberFormatException e) {
            Toast.makeText(AddSavingGoal.this, "Failed to parse amount", Toast.LENGTH_LONG).show();
        }
        return amountInteger;
    }

    /*
    Update progress bar with saving
     */
    public void updateProgress() {
        int incomeSubExpense =
                (int)(100*(1- (double) (Databases.getWeeklyExpenses())/Databases.getWeeklyIncome()));
        if(incomeSubExpense<=0)
            incomeExpenseSavingProgress.setSecondaryProgress(0);
        else {
            incomeExpenseSavingProgress.setSecondaryProgress(incomeSubExpense);
            incomeSubExpense =
                    (int)(100*(1- (double) (Databases.getWeeklyExpenses()+Databases.getWeeklySaving()+weeklySaving-oldSaving)/Databases.getWeeklyIncome()));
            if(incomeSubExpense<=0)
                incomeExpenseSavingProgress.setProgress(0);
            else
                incomeExpenseSavingProgress.setProgress(incomeSubExpense);
        }
    }

    public int differenceInWeeks(Calendar a, Calendar b) {
        int weeks = 0;
        Calendar c = Calendar.getInstance();
        c.setTime(a.getTime());
        c.add(Calendar.DAY_OF_WEEK, 7-c.get(Calendar.DAY_OF_WEEK));
        while(c.getTime().before(b.getTime())) {
            c.add(Calendar.WEEK_OF_YEAR, 1);
            weeks++;
        }
        return weeks;
    }


    public void addSavingGoalToList(View v){
        if(weeklySaving==0 || descriptionInput.getText().toString().length()==0 || selectedDay==null)
            return;

        Saving s;
        try {
            s = new Saving (-1,
                    descriptionInput.getText().toString() + "- " +
                            selectedDay.get(Calendar.YEAR) + "/" + selectedDay.get(Calendar.MONTH)
                            + "/" + selectedDay.get(Calendar.DAY_OF_MONTH),
                    getSavingAmount(), 0, weeklySaving, 0, 1);

            Boolean success;
            if(edit) {
                success = dbHelper.editOne(s, oldID);
            }
            else
                success = dbHelper.addOne(s);
                Log.d("Success", "Did it work? "+success+", "+s.toString());
            Intent leaveActivity = new Intent(this, MainAddSavingLT.class);
            startActivity(leaveActivity);
        }
        catch (Exception e) {
            Toast.makeText(this, "Failed to add saving", Toast.LENGTH_LONG).show();
            Log.d("Success", e.toString());
        }
    }
}