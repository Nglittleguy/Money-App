package com.example.money;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolder> {

    Context c;
    List<Income> incomeList;
    RecyclerView recyclerView;

    /*
    Single Row View
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView rowDesc;
        TextView rowAmount;
        Button rowButton;

        /*
        https://stackoverflow.com/questions/26076965/android-recyclerview-addition-removal-of-items
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowDesc = itemView.findViewById(R.id.income_desc);
            rowAmount = itemView.findViewById(R.id.income_amount);
            rowButton = itemView.findViewById(R.id.income_remove_button);
            rowButton.setOnClickListener(this);
        }

        //Click on button to remove item
        @Override
        public void onClick(View v) {
            int iPos = getAdapterPosition();
            removeAt(iPos);
        }
    }

    public IncomeAdapter(Context c, List<Income> incomeList, RecyclerView recyclerView) {
        this.c = c;
        this.incomeList = incomeList;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public IncomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        //Inflate the recycler view with single_income layout
        View v = inflater.inflate(R.layout.single_income, parent, false);
        ViewHolder vH = new ViewHolder(v);
        return vH;
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeAdapter.ViewHolder holder, int position) {
        //Get the income, and set the text to reflect its values
        Income i = incomeList.get(position);
        holder.rowDesc.setText(""+i.getDesc());
        holder.rowAmount.setText(""+Databases.centsToDollar(i.getAmountPerWeek()));

        //If clicked on
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIncome;
                if(c instanceof MainAddIncome)                          //Editing Income
                    editIncome = new Intent(c, AddIncome.class);
                else                                                    //Editing Expense
                    editIncome = new Intent(c, AddExpense.class);
                editIncome.putExtra("Edit", true);
                editIncome.putExtra("WeeklyIncome", i.getAmountPerWeek());
                editIncome.putExtra("OldID", i.getId());
                editIncome.putExtra("Description", i.getDesc());
                c.startActivity(editIncome);
            }
        });
    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    /*
    Remove item at this position in list
     */
    public void removeAt(int pos) {
        Income i = incomeList.get(pos);
        incomeList.remove(pos);
        Databases.getDBHelper().removeOneIncome(i);
        //notify removal to update
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, incomeList.size());
        //update the total (dependant on which context
        if(c instanceof MainAddIncome)
            ((MainAddIncome) c).updateTotal();
        else if(c instanceof MainAddExpense)
            ((MainAddExpense) c).updateTotal();
    }
}
