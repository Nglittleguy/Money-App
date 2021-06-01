package com.example.money;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.money.ui.main.SectionsPagerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Locale;

public class MainTab extends AppCompatActivity implements MoveSavingsDialog.MoveSavingListener {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private CoordinatorLayout tabLayout;

    //Construction of main activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //If going to main, update the allowance
                if(position==0 || position==2)
                    sectionsPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) { }

            @Override
            public void onPageScrollStateChanged(int arg0) { }
        });

        tabLayout = findViewById(R.id.tabCoordinatorLayout);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    public String getStartOfWeek() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(c.getTime());
    }

    public String getStartOfWeek(int i) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        switch(i) {
            case 0:
                return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", Locale.getDefault()).format(c.getTime());
            case 1:
                return new SimpleDateFormat("MMM d, yyyy HH", Locale.getDefault()).format(c.getTime());
            case 2:
                return new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(c.getTime());
            default:
                return new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(c.getTime());
        }
    }

    @Override
    public void onBackPressed() { }

    /*
    Go to AddSpending activity
     */
    public void addSpending(View view){
        Intent leaveActivity = new Intent(this, AddSpending.class);
        startActivity(leaveActivity);
    }

    /*
    Removal of savings open dialog to move remaining funds
     */
    public void openDialog(Saving take) {
        if(take.getAmountStored()!=0) {
            MoveSavingsDialog dialog = new MoveSavingsDialog();
            dialog.setFrom(take);
            dialog.show(getSupportFragmentManager(), "Remaining Funds Dialog");
        }
    }

    /*
    Dialog Interface method - moves the money to selected spot (weekly allowance, or other savings)
     */
    @Override
    public void applySaving(Saving s, Saving take) {
        if(s.getId()==-1) {
            Spending i = new Spending(-1, take.getDesc().split(" -", 2)[0] + ": Remaining",
                    -1 * (int) take.getAmountStored(), false);
            Databases.getDBHelper().addOneSpend(i);
        }
        else
            Databases.getDBHelper().addToSavings(s, (int)take.getAmountStored());
    }

    /*
    Go to Loading to reset allowances
     */
    public void forceLoading() {
        Intent leaveActivity = new Intent(this, MainLoading.class);
        startActivity(leaveActivity);
    }

    /*
    Exports parameter/spending data into a CSV file (export to drive or mail)
     */
    public void exportData() {
        DatabaseHelper db = Databases.getDBHelper();
        try{
            //Generate file output stream
            FileOutputStream out = openFileOutput("CoinDataExport.csv", Context.MODE_PRIVATE);
            out.write((db.getIncomeExport()).getBytes());
            out.write((db.getSavingExport()).getBytes());
            out.write((db.getRecordExport()).getBytes());
            out.write((db.getSpendingExport()).getBytes());
            out.close();

            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "CoinDataExport.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.money.fileprovider", filelocation);
            //Create new file intent
            Intent fileExport = new Intent(Intent.ACTION_SEND);
            fileExport.setType("text/csv");
            fileExport.putExtra(Intent.EXTRA_SUBJECT, "CoinDataExport");
            fileExport.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileExport.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            fileExport.putExtra(Intent.EXTRA_STREAM, path);

            Intent chooser = Intent.createChooser(fileExport, "Share File");
            List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                this.grantUriPermission(packageName, path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            startActivity(chooser);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}