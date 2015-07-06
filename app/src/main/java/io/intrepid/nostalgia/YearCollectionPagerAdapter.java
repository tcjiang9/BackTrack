package io.intrepid.nostalgia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class YearCollectionPagerAdapter extends FragmentStatePagerAdapter {

    private YearFragment[] yearFragments = new YearFragment[Constants.DEFAULT_YEAR];

    public YearCollectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if (yearFragments[i] == null) {
            yearFragments[i] = new YearFragment();
            //information about current year gets passed to fragment for later use
            Bundle args = new Bundle();
            args.putInt(YearFragment.YEAR, i + Constants.MIN_YEAR);
            yearFragments[i].setArguments(args);
            return yearFragments[i];
        } else {
            return yearFragments[i];
        }
    }

    @Override
    public int getCount() {
        return Constants.DEFAULT_YEAR; //The number of years to display since minimum year
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position + Constants.MIN_YEAR);
    }
}
