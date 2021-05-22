package com.example.money;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.money.ui.main.SpenditureFragment;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SpentAdapter extends RecyclerView.Adapter<SpentAdapter.ViewHolder> {

    Context c;
    List<Spending> spentList;
    RecyclerView recyclerView;
    SpenditureFragment f;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy - h aa", Locale.getDefault());
    //final View.OnClickListener buttonClickListener = new MyButtonClickListener();

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

    public SpentAdapter(Context c, List<Spending> spentList, RecyclerView recyclerView, SpenditureFragment f) {
        this.c = c;
        this.spentList = spentList;
        this.recyclerView = recyclerView;
        this.f = f;
    }

    @NonNull
    @Override
    public SpentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View v = inflater.inflate(R.layout.single_income, parent, false);
        ViewHolder vH = new ViewHolder(v);
        return vH;
    }

    @Override
    public void onBindViewHolder(@NonNull SpentAdapter.ViewHolder holder, int position) {
        Spending i = spentList.get(position);
        holder.rowAmount.setText(""+Databases.centsToDollar(i.getAmount()));
        holder.rowDesc.setText(""+i.getDesc()+": "+dateFormat.format(i.getDateTime()));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent editSaving = new Intent();
//                editSaving.putExtra("Edit", true);
//                editSaving.putExtra("WeeklySaving", i.getAmountPerWeek());
//                editSaving.putExtra("Percent", i.getPercent());
//                editSaving.putExtra("OldID", i.getId());
//                editSaving.putExtra("Description", i.getDesc());
//                if(c instanceof MainAddSavingLT) {
//                    editSaving.setClass(c, AddSavingLongTerm.class);
//
//                }
//                else {
//                    editSaving.putExtra("SavingTotal", i.getLimitStored());
//                    editSaving.putExtra("Stored", i.getAmountStored());
//                    editSaving.setClass(c, AddSavingGoal.class);
//                    Log.d("Success", "limit is "+i.getLimitStored());
//                }
//                c.startActivity(editSaving);
            }
        });
    }

    @Override
    public int getItemCount() {
        return spentList.size();
    }

    public void removeAt(int pos) {
        Spending i = spentList.get(pos);
        spentList.remove(pos);
        Databases.getDBHelper().removeOneSpend(i);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, spentList.size());
        f.updateTotal();
    }
}
