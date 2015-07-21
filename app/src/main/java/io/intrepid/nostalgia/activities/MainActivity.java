package io.intrepid.nostalgia.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import io.intrepid.nostalgia.constants.Constants;
import io.intrepid.nostalgia.R;
import io.intrepid.nostalgia.SinglePlayer;
import io.intrepid.nostalgia.ViewPagerFragmentLifeCycle;
import io.intrepid.nostalgia.adapters.YearCollectionPagerAdapter;
import io.intrepid.nostalgia.fragments.YearFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;


public class MainActivity extends AppCompatActivity
        implements
        YearFragment.PrevYearButtonListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String SELECTED_FONT = "fonts/ProximaNova-Bold.otf";
    public static final String UNSELECTED_FONT = "fonts/ProximaNova-Semibold.otf";
    public static final int SELECTED_SIZE = 111;
    public static final int SELECTED_WIDTH = 30;
    public static final int UNSELECTED_SIZE = 65;
    public static final int UNSELECTED_WIDTH = 17;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int currentPosition;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SinglePlayer.getInstance();
        final YearCollectionPagerAdapter pagerAdapter = new YearCollectionPagerAdapter(getSupportFragmentManager());
        currentPosition = -1;
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        //TODO: align color changing with font/size
        tabLayout.setTabTextColors(getResources().getColorStateList(R.color.tabview_selector_color));
        tabLayout.setupWithViewPager(viewPager);
        for (int j = 0; j<= (Constants.NUMBER_OF_YEARS - 1); j ++){
            focusSelectedYear(j, UNSELECTED_SIZE, UNSELECTED_WIDTH, UNSELECTED_FONT);
        }
        ViewPager.OnPageChangeListener viewPageListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int newPosition) {

                if (currentPosition != -1) {
                    focusSelectedYear(currentPosition, UNSELECTED_SIZE, UNSELECTED_WIDTH, UNSELECTED_FONT);
                    ViewPagerFragmentLifeCycle fragmentToHide = (ViewPagerFragmentLifeCycle) pagerAdapter.getItem(currentPosition);
                    fragmentToHide.onPauseFragment();
                    ViewPagerFragmentLifeCycle fragmentToResume = (ViewPagerFragmentLifeCycle) pagerAdapter.getItem(newPosition);
                    fragmentToResume.onResumeFragment();
                }
                focusSelectedYear(newPosition, SELECTED_SIZE, SELECTED_WIDTH, SELECTED_FONT);
                currentPosition = newPosition;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        viewPager.addOnPageChangeListener(viewPageListener);
        viewPager.setCurrentItem(Constants.NUMBER_OF_YEARS - 1);
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

    public void openSettings(View v){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    @Override
    public void onPrevYearButtonClicked() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    public void focusSelectedYear(int newPosition, int w, int textSize, String font) {
        ViewGroup viewGroup = (ViewGroup) tabLayout.getChildAt(0);
        ViewGroup tabView = (ViewGroup) viewGroup.getChildAt(newPosition);
        TextView textView = (TextView) tabView.getChildAt(0);
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, w, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        tabView.setLayoutParams(layoutParams);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        SpannableStringBuilder sBuilder = new SpannableStringBuilder();
        sBuilder.append(textView.getText());
        CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(TypefaceUtils.load(getAssets(), font));
        sBuilder.setSpan(typefaceSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(sBuilder, TextView.BufferType.SPANNABLE);
    }
}
