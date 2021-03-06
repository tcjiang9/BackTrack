package io.intrepid.nostalgia.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import io.intrepid.nostalgia.constants.Constants;
import io.intrepid.nostalgia.fragments.YearFragment;

public class YearCollectionPagerAdapter extends FragmentStatePagerAdapter {

    private YearFragment[] yearFragments = new YearFragment[Constants.NUMBER_OF_YEARS];

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
        return Constants.NUMBER_OF_YEARS; //The number of years to display since minimum year
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position + Constants.MIN_YEAR);
    }
}
