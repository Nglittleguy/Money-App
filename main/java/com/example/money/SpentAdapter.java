package com.example.money;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.money.ui.main.RecordFragment;
import com.example.money.ui.main.SpenditureFragment;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SpentAdapter extends RecyclerView.Adapter<SpentAdapter.ViewHolder> {

    Context c;
    List<Spending> spentList;
    RecyclerView recyclerView;
    Fragment f;
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE - h aa", Locale.getDefault());
    RecordSpendingDialog dialog;

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
        }

    }

    /*
    Constructor with dialog
     */
    public SpentAdapter(Context c, List<Spending> spentList, RecyclerView recyclerView, Fragment f, RecordSpendingDialog dialog) {
        this.c = c;
        this.spentList = spentList;
        this.recyclerView = recyclerView;
        this.f = f;
        this.dialog = dialog;
    }

    /*
    Constructor without dialog
     */
    public SpentAdapter(Context c, List<Spending> spentList, RecyclerView recyclerView, Fragment f) {
        this.c = c;
        this.spentList = spentList;
        this.recyclerView = recyclerView;
        this.f = f;
        this.dialog = null;
    }

    @NonNull
    @Override
    public SpentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        //Inflate recycler with single_income layout
        View v = inflater.inflate(R.layout.single_income, parent, false);
        ViewHolder vH = new ViewHolder(v);
        return vH;
    }

    @Override
    public void onBindViewHolder(@NonNull SpentAdapter.ViewHolder holder, int position) {
        Spending i = spentList.get(position);
        //if parent fragment is RecordFragment, no remove button (past record, don't change)
        if(f instanceof RecordFragment)
            holder.rowButton.setVisibility(View.GONE);
        //if spent is from saving, change colour to orange - indicate not from weekly allowance
        if(i.getFromSaving()) {
            holder.rowDesc.setTextColor(f.getActivity().getResources().getColor(R.color.myOrange));
            holder.rowAmount.setTextColor(f.getActivity().getResources().getColor(R.color.myOrange));
        }
        //if is necessity, boldface description
        if(i.getNecessity())
            holder.rowAmount.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        //if amount is less than 0, adding money, change to green
        if(i.getAmount()<0) {
            holder.rowDesc.setTextColor(f.getActivity().getResources().getColor(R.color.myGreen));
            holder.rowAmount.setTextColor(f.getActivity().getResources().getColor(R.color.myGreen));
            holder.rowAmount.setText(""+Databases.centsToDollar(-i.getAmount()));
        }
        else
            holder.rowAmount.setText(""+Databases.centsToDollar(i.getAmount()));
        holder.rowDesc.setText(""+i.getDesc().split(":")[0]+": "+dateFormat.format(i.getDateTime()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if spenditure fragment, show snackbar here, else show snackbar from dialog
                if(f instanceof SpenditureFragment)
                    ((SpenditureFragment)f).showSnackbar(i);
                else if(f instanceof RecordFragment && dialog!=null)
                    dialog.showSnackbar(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return spentList.size();
    }

    /*
    Remove spenditure when button selected
     */
    public void removeAt(int pos) {
        Spending i = spentList.get(pos);
        if(!i.getFromSaving()) {
            //remove from list, database, notify removed, update total
            spentList.remove(pos);
            Databases.getDBHelper().removeOneSpend(i);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, spentList.size());
            ((SpenditureFragment)f).updateTotal();
        }
        else
            Toast.makeText(f.getContext(), "Sorry. Cannot remove spenditure from saving.", Toast.LENGTH_LONG).show();

    }
}
