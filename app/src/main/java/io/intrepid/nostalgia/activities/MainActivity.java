package io.intrepid.nostalgia.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import io.intrepid.nostalgia.constants.Constants;
import io.intrepid.nostalgia.R;
import io.intrepid.nostalgia.SinglePlayer;
import io.intrepid.nostalgia.ViewPagerFragmentLifeCycle;
import io.intrepid.nostalgia.adapters.YearCollectionPagerAdapter;
import io.intrepid.nostalgia.fragments.YearFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity
        implements
        YearFragment.PrevYearButtonListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int currentPosition = Constants.NUMBER_OF_YEARS - 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/ProximaNova-Regular.otf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        SinglePlayer.getInstance();
        final YearCollectionPagerAdapter pagerAdapter = new YearCollectionPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(Constants.NUMBER_OF_YEARS - 1);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabTextColors(getResources().getColorStateList(R.color.tabview_selector_color));
        tabLayout.setupWithViewPager(viewPager);
        ViewPager.OnPageChangeListener viewPageListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int newPosition) {
                ViewPagerFragmentLifeCycle fragmentToHide = (ViewPagerFragmentLifeCycle) pagerAdapter.getItem(currentPosition);
                fragmentToHide.onPauseFragment();

                ViewPagerFragmentLifeCycle fragmentToResume = (ViewPagerFragmentLifeCycle) pagerAdapter.getItem(newPosition);
                fragmentToResume.onResumeFragment();
                currentPosition = newPosition;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        viewPager.addOnPageChangeListener(viewPageListener);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrevYearButtonClicked() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }
}
