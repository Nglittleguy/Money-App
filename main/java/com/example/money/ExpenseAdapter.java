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

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    Context c;
    List<Expense> expenseList;
    RecyclerView recyclerView;
    //final View.OnClickListener buttonClickListener = new MyButtonClickListener();

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView rowDesc;
        TextView rowAmount;
        Button rowButton;

        /*
        https://stackoverflow.com/questions/26076965/android-recyclerview-addition-removal-of-items
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowDesc = itemView.findViewById(R.id.expense_desc);
            rowAmount = itemView.findViewById(R.id.expense_amount);
            rowButton = itemView.findViewById(R.id.expense_remove_button);

            rowButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int iPos = getAdapterPosition();
            removeAt(iPos);
            //Databases.getExpenseHelper().removeOne(expenseList.get(iPos));
            Log.d("Success", iPos+"");
        }

    }

    public ExpenseAdapter(Context c, List<Expense> expenseList, RecyclerView recyclerView) {
        this.c = c;
        this.expenseList = expenseList;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ExpenseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View v = inflater.inflate(R.layout.single_expense, parent, false);
        ExpenseAdapter.ViewHolder vH = new ExpenseAdapter.ViewHolder(v);
        return vH;
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ViewHolder holder, int position) {
        Expense i = expenseList.get(position);
        holder.rowDesc.setText(""+i.getDesc());
        holder.rowAmount.setText(""+Databases.centsToDollar(i.getAmountPerWeek()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editExpense = new Intent(c, AddExpense.class);
                editExpense.putExtra("Edit", true);
                editExpense.putExtra("WeeklyExpense", i.getAmountPerWeek());
                editExpense.putExtra("OldID", i.getId());
                editExpense.putExtra("Description", i.getDesc());
                c.startActivity(editExpense);
                //Toast.makeText(c, "Here at number "+position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public void removeAt(int pos) {
        Expense i = expenseList.get(pos);
        expenseList.remove(pos);
        Databases.getExpenseHelper().removeOne(i);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, expenseList.size());
        if(c instanceof MainAddExpense)
            ((MainAddExpense) c).updateTotal();
    }

}
