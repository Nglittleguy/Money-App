package com.example.money;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.money.ui.main.ParameterFragment;

import java.util.List;

public class ParameterSectionAdapter extends RecyclerView.Adapter<ParameterSectionAdapter.ViewHolder> {
    Context c;
    ParameterFragment f;
    RecyclerView recyclerView;
    List<Parameter> paramList;
    Class typeOfParameter;

    /*
    Single Parameter View
     */
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

        //Remove parameter if button is clicked
        @Override
        public void onClick(View v) {
            int iPos = getAdapterPosition();
            removeAt(iPos);
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
        /*
    Single Parameter Type View
     */
        View v = inflater.inflate(R.layout.single_income, parent, false);
        ParameterSectionAdapter.ViewHolder vH = new ParameterSectionAdapter.ViewHolder(v);
        return vH;
    }

    @Override
    public void onBindViewHolder(@NonNull ParameterSectionAdapter.ViewHolder holder, int position) {
        Parameter j = paramList.get(position);
        //Set values based on parameter type
        if(j instanceof Income){
            //if income or expense, parse into Income
            Income i = (Income)j;

            holder.rowDesc.setText(""+i.getDesc());
            holder.rowAmount.setText(""+Databases.centsToDollar(i.getAmountPerWeek()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editIncome;
                    if(typeOfParameter.equals(MainAddIncome.class))         //Editing Income
                        editIncome = new Intent(c, AddIncome.class);
                    else                                                    //Editing Expense
                        editIncome = new Intent(c, AddExpense.class);
                    editIncome.putExtra("Edit", true);
                    editIncome.putExtra("WeeklyIncome", i.getAmountPerWeek());
                    editIncome.putExtra("OldID", i.getId());
                    editIncome.putExtra("Description", i.getDesc());
                    if(f!=null)
                        editIncome.putExtra("Update", true);
                    c.startActivity(editIncome);
                }
            });
        }
        else if(j instanceof Saving) {
            //if saving, parse into Saving
            Saving i = (Saving)j;

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(typeOfParameter.equals(MainAddSavingGoal.class) && c instanceof MainTab) {
                        f.showDescription(i);
                    }
                    else if(i.getAmountPerWeek()!=0) {
                        Intent editSaving = new Intent();
                        editSaving.putExtra("Edit", true);
                        editSaving.putExtra("WeeklySaving", i.getAmountPerWeek());
                        editSaving.putExtra("Percent", i.getPercent());
                        editSaving.putExtra("OldID", i.getId());
                        editSaving.putExtra("Description", i.getDesc());
                        if(f!=null)
                            editSaving.putExtra("Update", true);
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
                }
            });
            //set text if percent based, or not
            if(i.getPercent()!=0)
                holder.rowAmount.setText(i.getPercent()+"% of Remaining");
            else {
                //if done saving (no more amount) - orange text, "Completed", non-clickable; else get amount
                if(i.getAmountPerWeek()==0) {
                    holder.rowAmount.setTextColor(f.getActivity().getResources().getColor(R.color.myOrange));
                    holder.rowAmount.setText("Completed");
                    holder.itemView.setOnClickListener(null);
                }
                else
                    holder.rowAmount.setText("" + Databases.centsToDollar(i.getAmountPerWeek()));
            }
            holder.rowDesc.setText(""+i.getDesc().split(" - ")[0]+"\t "+Databases.centsToDollar(i.getAmountStored()));
        }

    }

    @Override
    public int getItemCount() {
        return paramList.size();
    }

    /*
    Remove parameter
     */
    public void removeAt(int pos) {
        Parameter i = paramList.get(pos);
        paramList.remove(pos);
        //if removing saving, open dialog to handle remaining funds
        if(i instanceof Saving) {
            Databases.getDBHelper().removeOneSave((Saving) i);
            if(c instanceof MainTab) {
                ((MainTab)f.getActivity()).openDialog((Saving)i);
            }
        }
        if(i instanceof Income)
            Databases.getDBHelper().removeOneIncome((Income)i);
        //notify recycler view that item is moved/changed
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, paramList.size());
        //update total
        if(f!=null)
            f.updateTotal();
        else
            ((MainParamCheck)c).updateTotal();
    }

}
