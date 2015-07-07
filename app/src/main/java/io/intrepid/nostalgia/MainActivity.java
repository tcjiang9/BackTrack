package io.intrepid.nostalgia;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements
        YearFragment.PrevYearButtonListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private ViewPager viewPager;
    private int currentPosition = Constants.NUMBER_OF_YEARS - 1;

    @InjectView(R.id.facebook_switch)
    Switch facebookSwitch;

    @InjectView(R.id.autoplay_switch)
    Switch autoplaySwitch;

    @InjectView(R.id.feedback_setting)
    TextView feedbackSetting;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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
            ButterKnife.inject(this);
            facebookSwitch.setChecked(LoginActivity.isFacebook);
            setSettingsListeners();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrevYearButtonClicked() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    public void setSettingsListeners() {
        facebookSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loginToFacebook();
                } else {
                    //logout of facebook
                }
            }
        });

        autoplaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //turn autoplay on
                } else{
                    //turn autoplay off
                }
            }
        });

        feedbackSetting.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //go to feedback interface
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"hayley@intrepid.io"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Nostalgia App Feedback");
                startActivity(Intent.createChooser(intent, ""));
                return false;
            }
        });
    }

    private void loginToFacebook() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
