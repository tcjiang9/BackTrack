package io.intrepid.nostalgia;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Calendar;

import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Year that appears upon first logging in. The previous year from current
     */
    private static final int DEFAULT_YEAR =  Calendar.getInstance().get(Calendar.YEAR) - 1852;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new YearCollectionPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(DEFAULT_YEAR);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent  activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class YearCollectionPagerAdapter extends FragmentStatePagerAdapter{
        public YearCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new NostalgiaFragment();
            Bundle args = new Bundle();
            args.putInt(NostalgiaFragment.YEAR, i + 1852);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return DEFAULT_YEAR; //The number of years to display since 1851
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.valueOf(position + 1852);
        }
    }
}
