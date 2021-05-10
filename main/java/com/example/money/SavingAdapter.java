package com.example.money;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class SavingAdapter extends RecyclerView.Adapter<SavingAdapter.ViewHolder> {

    Context c;
    List<Saving> savingList;
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

    public SavingAdapter(Context c, List<Saving> savingList, RecyclerView recyclerView) {
        this.c = c;
        this.savingList = savingList;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public SavingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View v = inflater.inflate(R.layout.single_income, parent, false);
        ViewHolder vH = new ViewHolder(v);
        return vH;
    }

    @Override
    public void onBindViewHolder(@NonNull SavingAdapter.ViewHolder holder, int position) {
        Saving i = savingList.get(position);
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
                if(c instanceof MainAddSavingLT) {
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

    @Override
    public int getItemCount() {
        return savingList.size();
    }

    public void removeAt(int pos) {
        Saving i = savingList.get(pos);
        savingList.remove(pos);
        Databases.getSavingHelper().removeOne(i);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, savingList.size());
        if(c instanceof MainAddSavingLT)
            ((MainAddSavingLT) c).updateTotal();
    }
}
