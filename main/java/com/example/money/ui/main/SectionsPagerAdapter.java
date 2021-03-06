package com.example.money.ui.main;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.money.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment main, spenditure, record, parameter;
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_3, R.string.tab_text_4, R.string.tab_text_2};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        //either create and return the fragment, or just return the fragment
        switch(position+1) {
            case 1:
                if(main==null)
                    main = MainFragment.newInstance(position+1);
                return main;
            case 2:
                if(spenditure==null)
                    spenditure = SpenditureFragment.newInstance(position+1);
                return spenditure;
            case 3:
                if(record==null)
                    record = RecordFragment.newInstance(position+1);
                return record;
            default:
                if(parameter==null)
                    parameter = ParameterFragment.newInstance(position+1);
                return parameter;

        }
    }

    //Removes the old fragment to update main allowance
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 4;
    }
}