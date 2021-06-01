package com.example.money;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.money.ui.main.RecordFragment;
import com.example.money.ui.main.SpenditureFragment;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private Context c;
    private List<SpentRecord> spentList;
    private RecyclerView recyclerView;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy, MMM d", Locale.getDefault());
    private RecordFragment parentFragment;
    //final View.OnClickListener buttonClickListener = new MyButtonClickListener();

    /*
    Single Record Type View
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView dateText;
        ProgressBar negativeBar, positiveBar, wholeBar;

        /*
        https://stackoverflow.com/questions/26076965/android-recyclerview-addition-removal-of-items
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.weekSpentTag);
            negativeBar = itemView.findViewById(R.id.negativeBar);
            positiveBar = itemView.findViewById(R.id.positiveBar);
            wholeBar = itemView.findViewById(R.id.wholeBar);
        }

    }

    public RecordAdapter(Context c, List<SpentRecord> spentList, RecyclerView recyclerView, RecordFragment recFrag) {
        this.c = c;
        this.spentList = spentList;
        this.recyclerView = recyclerView;
        parentFragment = recFrag;
    }

    @NonNull
    @Override
    public RecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        //Inflate the recycler with single_record layout
        View v = inflater.inflate(R.layout.single_record, parent, false);
        ViewHolder vH = new ViewHolder(v);
        return vH;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordAdapter.ViewHolder holder, int position) {
        SpentRecord i = spentList.get(position);
        //set text to start of the record week
        holder.dateText.setText(dateFormat.format(i.getStart()));
        //if within bounds, set appropriate progress bar visibility, and percent
        if(i.getAmount()<=100 && i.getAmount()>=-100)  {
            if(i.getAmount()>=0) {
                holder.negativeBar.setVisibility(View.INVISIBLE);
                holder.positiveBar.setProgress(i.getAmount());
            }
            else {
                holder.positiveBar.setVisibility(View.INVISIBLE);
                holder.negativeBar.setSecondaryProgress(-i.getAmount());
            }
        }
        else {
            //entire bar is either green or red
            holder.wholeBar.setVisibility(View.VISIBLE);
            if(i.getAmount()>100)
                holder.wholeBar.setProgress(100);
            else
                holder.wholeBar.setSecondaryProgress(100);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if click, show dialog of weekly spenditure
                RecordSpendingDialog dialog = new RecordSpendingDialog();
                dialog.setFrom(i.getStart(), parentFragment);
                dialog.show(((MainTab)c).getSupportFragmentManager(), "Remaining Funds Dialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return spentList.size();
    }

}
