package com.example.money;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner incomePeriod;
    private ConstraintLayout manualPeriodInput;
    private EditText incomeInput;
    private EditText periodInput;
    private TextView showWI;
    private int weeklyIncome;       //in cents
    private int periodOfWeeks;
    private DecimalFormat incomeToText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        incomeToText = new DecimalFormat("0.00");
        weeklyIncome = 0;

        //Select Income Period
        incomePeriod = findViewById(R.id.selectIncomePeriod);
        ArrayAdapter<CharSequence> incomePeriodAdapter = ArrayAdapter.createFromResource(
                this, R.array.incomePeriodValues, android.R.layout.simple_spinner_item);
        incomePeriodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incomePeriod.setAdapter(incomePeriodAdapter);
        incomePeriod.setOnItemSelectedListener(this);


        //Income Input
        incomeInput = findViewById(R.id.incomeAmount);
        incomeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(incomeInput.getText()==null) {
                    updateWeeklyIncome(periodOfWeeks, 0);
                    updateWeeklyIncomeView();
                    return;
                }
                if(!incomeInput.getText().toString().startsWith("$")) {
                    incomeInput.setText("$" + incomeInput.getText().toString().replace("$", ""));
                    Selection.setSelection(incomeInput.getText(), incomeInput.getText().length());
                }
                if(!(periodInput.equals(null) && incomePeriod.getSelectedItemPosition()==4)) {

                    double amountDouble = Double.parseDouble(incomeInput.getText().toString().substring(1));
                    if(amountDouble/100 > Integer.MAX_VALUE)
                        Toast.makeText(MainActivity.this, "Error, too large a weekly amount", Toast.LENGTH_LONG).show();

                    int amountInteger = (int) amountDouble*100;
                    updateWeeklyIncome(periodOfWeeks, amountInteger);
                    updateWeeklyIncomeView();

                }
            }
        });

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

                if(s.length()>0 && !s.toString().contains(" days") && !s.toString().equals(" days")) {
                    String text = s.toString().concat(" days");
                    periodInput.setText(text);
                    periodInput.setSelection(text.length() - 5);
                    suffixAdded = true;

                    periodOfWeeks = Integer.parseInt(periodInput.getText().toString()
                            .substring(0, periodInput.getText().toString().length()-5));
                    updateWeeklyIncome(periodOfWeeks, weeklyIncome);
                    updateWeeklyIncomeView();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0)
                    suffixAdded = false;
            }
        });


        //Manual Income Period Input
        manualPeriodInput = findViewById(R.id.manualIncomePeriod);

        //Showing Weekly Income
        showWI = findViewById(R.id.showIncomeText);



    }

    //incomePeriod Spinner Methods
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
                periodOfWeeks = 52;
                break;
            case 1:
                periodOfWeeks = 4;
                break;
            case 2:
                periodOfWeeks = 2;
                break;
            case 3:
                periodOfWeeks = 1;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}


    /*
    Updates the weekly income
     */
    public void updateWeeklyIncome(int period, int amount) {
        weeklyIncome = amount/period;
    }

    /*
    Updates the view of the weekly income
    https://www.geeksforgeeks.org/insert-a-string-into-another-string-in-java/
     */
    public void updateWeeklyIncomeView() {
        String text = "Weekly income: $";
        double amount = ((double)weeklyIncome)/100;
        String num = incomeToText.format(amount);
        showWI.setText(text+num);
        Toast.makeText(this, ""+weeklyIncome, Toast.LENGTH_LONG).show();
    }
}