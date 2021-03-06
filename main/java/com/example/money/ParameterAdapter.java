package com.example.money;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.money.Parameter;
import com.example.money.ui.main.ParameterFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParameterAdapter extends RecyclerView.Adapter<ParameterAdapter.ViewHolder> {
    Context c;
    ParameterFragment f;
    RecyclerView recyclerView;
    List<String> list = Arrays.asList("Weekly Income: ", "Weekly Expense: ", "Weekly Savings: ", "Weekly Goals: ");
    List<Income> income, expense;
    List<Saving> savingLT, savingGoal;

    /*
    Single Parameter Type View
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView parameterTitle;
        TextView parameterValue;
        RecyclerView childView;
        ImageButton parameterAddButton;

        /*
        https://stackoverflow.com/questions/26076965/android-recyclerview-addition-removal-of-items
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parameterTitle = itemView.findViewById(R.id.parameterTitle);
            parameterAddButton = itemView.findViewById(R.id.addParameterButton);
            childView = itemView.findViewById(R.id.childRecycler);
            parameterValue = itemView.findViewById(R.id.parameterValue);
            parameterAddButton.setOnClickListener(this);
        }

        //Add parameter if button is clicked
        @Override
        public void onClick(View v) {
            int iPos = getAdapterPosition();
            addParam(iPos);
        }

        public void setParameterTitle(int position) {
            parameterTitle.setText(list.get(position));
        }

        public void setParameterValue(String amount) {
            parameterValue.setText(amount);
        }
    }

    /*
    Constructor for ParameterFragment
     */
    public ParameterAdapter(Context c, RecyclerView recyclerView, List<Income> income,
                            List<Income> expense, List<Saving> savingLT, List<Saving> savingGoal,
                            ParameterFragment f) {
        this.c = c;
        this.recyclerView = recyclerView;
        this.income = income;
        this.expense = expense;
        this.savingLT = savingLT;
        this.savingGoal = savingGoal;
        this.f = f;
    }

    /*
   Constructor without ParameterFragment
    */
    public ParameterAdapter(Context c, RecyclerView recyclerView, List<Income> income,
                            List<Income> expense, List<Saving> savingLT, List<Saving> savingGoal) {
        this.c = c;
        this.recyclerView = recyclerView;
        this.income = income;
        this.expense = expense;
        this.savingLT = savingLT;
        this.savingGoal = savingGoal;
        this.f = null;
    }

    @NonNull
    @Override
    public ParameterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        //Inflate the recycler view with parameter_section layout
        View v = inflater.inflate(R.layout.parameter_section, parent, false);
        ParameterAdapter.ViewHolder vH = new ParameterAdapter.ViewHolder(v);
        return vH;
    }

    @Override
    public void onBindViewHolder(@NonNull ParameterAdapter.ViewHolder holder, int position) {
        String title = list.get(position);
        int total = 0;

        //set values depending on the type of parameter (position), new recycler view per paramter type
        List<Parameter> send = new ArrayList<>();
        ParameterSectionAdapter sectionAdapter;
        switch(position) {
            case 0:
                //Income
                for(int i = 0; i<income.size(); i++) {
                    send.add(income.get(i));
                }
                total = Databases.getWeeklyIncome();
                sectionAdapter = new ParameterSectionAdapter(c, send, holder.childView, MainAddIncome.class, f);
                holder.parameterAddButton.setImageDrawable(ContextCompat.getDrawable(c, R.drawable.add_income));
                holder.parameterValue.setTextColor(ContextCompat.getColor(c, R.color.myGreen));
                break;
            case 1:
                //Expense
                for(int i = 0; i<expense.size(); i++) {
                    send.add(expense.get(i));
                }
                total = Databases.getWeeklyExpenses();
                sectionAdapter = new ParameterSectionAdapter(c, send, holder.childView, MainAddExpense.class, f);
                holder.parameterAddButton.setImageDrawable(ContextCompat.getDrawable(c, R.drawable.add_expense));
                holder.parameterValue.setTextColor(ContextCompat.getColor(c, R.color.design_default_color_error));
                break;
            case 2:
                //Saving LT
                for(int i = 0; i<savingLT.size(); i++) {
                    send.add(savingLT.get(i));
                }
                total = Databases.getWeeklySaving(true);
                sectionAdapter = new ParameterSectionAdapter(c, send, holder.childView, MainAddSavingLT.class, f);
                break;
            default:
                //Saving Goal
                for(int i = 0; i<savingGoal.size(); i++) {
                    send.add(savingGoal.get(i));
                }
                total = Databases.getWeeklySaving(false);
                sectionAdapter = new ParameterSectionAdapter(c, send, holder.childView, MainAddSavingGoal.class, f);
        }
        holder.setParameterTitle(position);
        holder.setParameterValue(Databases.centsToDollar(total));
        holder.childView.setAdapter(sectionAdapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /*
    Add parameter depending on type of parameter
     */
    public void addParam(int pos) {
        Intent i = new Intent();
        switch(pos) {
            case 0: //Income
                i = new Intent(c, AddIncome.class);
                break;
            case 1: //Expense
                i = new Intent(c, AddExpense.class);
                break;
            case 2: //Saving LT
                i = new Intent(c, AddSavingLongTerm.class);
                break;
            case 3: //Saving Goal
                i = new Intent(c, AddSavingGoal.class);
                break;
        }
        if(f!=null)
            i.putExtra("Update", true);
        c.startActivity(i);
    }

}

