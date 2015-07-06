package io.intrepid.nostalgia;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity
        implements
        YearFragment.PrevYearButtonListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private ViewPager viewPager;
    private int currentPosition = Constants.NUMBER_OF_YEARS - 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final YearCollectionPagerAdapter pagerAdapter = new YearCollectionPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(Constants.NUMBER_OF_YEARS - 1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
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
            setContentView(R.layout.settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrevYearButtonClicked() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }
}
