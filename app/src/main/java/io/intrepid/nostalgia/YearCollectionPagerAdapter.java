package io.intrepid.nostalgia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class YearCollectionPagerAdapter extends FragmentStatePagerAdapter {
    public YearCollectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new YearFragment();
        Bundle args = new Bundle();
        args.putInt(YearFragment.YEAR, i + 1852);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return Constants.DEFAULT_YEAR; //The number of years to display since 1851
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position + 1852);
    }
}
