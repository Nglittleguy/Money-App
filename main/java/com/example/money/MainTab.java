package com.example.money;

import android.content.Intent;
import android.os.Bundle;

import com.example.money.ui.main.MainFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.money.ui.main.SectionsPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainTab extends AppCompatActivity {
    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    CoordinatorLayout tabLayout;

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
                if(position==0)
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
            default:
                return new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(c.getTime());
        }
    }

    @Override
    public void onBackPressed() { }

    public void addSpending(View v){
        Intent leaveActivity = new Intent(this, AddSpending.class);
        startActivity(leaveActivity);
    }

}