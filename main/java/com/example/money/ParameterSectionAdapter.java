package com.example.money;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.money.Parameter;
import com.example.money.ui.main.ParameterFragment;

import java.util.List;

public class ParameterSectionAdapter extends RecyclerView.Adapter<ParameterSectionAdapter.ViewHolder> {
    Context c;
    ParameterFragment f;
    RecyclerView recyclerView;
    List<Parameter> paramList;
    Class typeOfParameter;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView rowDesc;
        TextView rowAmount;
        ImageButton rowButton;

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

        @Override
        public void onClick(View v) {
            int iPos = getAdapterPosition();
            removeAt(iPos);
            //Databases.getSavingHelper().removeOne(savingList.get(iPos));
            Log.d("Success", iPos+"");
        }

    }

    public ParameterSectionAdapter(Context c, List<Parameter> paramList, RecyclerView recyclerView, Class typeOfParameter, ParameterFragment f) {
        this.c = c;
        this.paramList = paramList;
        this.recyclerView = recyclerView;
        this.typeOfParameter = typeOfParameter;
        this.f = f;
    }

    @NonNull
    @Override
    public ParameterSectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View v = inflater.inflate(R.layout.single_income, parent, false);
        ParameterSectionAdapter.ViewHolder vH = new ParameterSectionAdapter.ViewHolder(v);
        return vH;
    }

    @Override
    public void onBindViewHolder(@NonNull ParameterSectionAdapter.ViewHolder holder, int position) {
        Parameter j = paramList.get(position);
        if(j instanceof Income){
            Income i = (Income)j;

            holder.rowDesc.setText(""+i.getDesc());
            holder.rowAmount.setText(""+Databases.centsToDollar(i.getAmountPerWeek()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editIncome;
                    if(typeOfParameter.equals(MainAddIncome.class))                          //Editing Income
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
        else if(j instanceof Saving) {
            Saving i = (Saving)j;
            if(i.getPercent()!=0)
                holder.rowAmount.setText(i.getPercent()+"% of Remaining");
            else
                holder.rowAmount.setText(""+Databases.centsToDollar(i.getAmountPerWeek()));
            holder.rowDesc.setText(""+i.getDesc()+"\t "+Databases.centsToDollar(i.getAmountStored()));


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editSaving = new Intent();
                    editSaving.putExtra("Edit", true);
                    editSaving.putExtra("WeeklySaving", i.getAmountPerWeek());
                    editSaving.putExtra("Percent", i.getPercent());
                    editSaving.putExtra("OldID", i.getId());
                    editSaving.putExtra("Description", i.getDesc());
                    if(typeOfParameter.equals(MainAddSavingLT.class)) {
                        editSaving.setClass(c, AddSavingLongTerm.class);
                    }
                    else {
                        editSaving.putExtra("SavingTotal", i.getLimitStored());
                        editSaving.putExtra("Stored", i.getAmountStored());
                        editSaving.setClass(c, AddSavingGoal.class);
                        Log.d("Success", "limit is "+i.getLimitStored());
                    }
                    c.startActivity(editSaving);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return paramList.size();
    }

    public void removeAt(int pos) {
        Parameter i = paramList.get(pos);
        paramList.remove(pos);
        if(i instanceof Saving)
            Databases.getSavingHelper().removeOne((Saving)i);
        if(i instanceof Income)
            Databases.getIncomeHelper().removeOne((Income)i);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, paramList.size());

        if(f!=null)
            f.updateTotal();
        else
            ((MainParamCheck)c).updateTotal();
    }

}
