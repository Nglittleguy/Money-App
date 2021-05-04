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

import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolder>{

    Context c;
    List<Income> incomeList;
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
            //Databases.getIncomeHelper().removeOne(incomeList.get(iPos));
            Log.d("Success", iPos+"");
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
        View v = inflater.inflate(R.layout.single_income, parent, false);
        ViewHolder vH = new ViewHolder(v);
        return vH;
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeAdapter.ViewHolder holder, int position) {
        Income i = incomeList.get(position);
        holder.rowDesc.setText(""+i.getDesc());
        holder.rowAmount.setText(""+i.getAmountPerWeek());
    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    public void removeAt(int pos) {
        incomeList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, incomeList.size());
    }
}
