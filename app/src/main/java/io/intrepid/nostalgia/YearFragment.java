package io.intrepid.nostalgia;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.util.EntityUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class YearFragment extends Fragment {
    public static final String TAG = YearFragment.class.getSimpleName();
    public static final String YEAR = "Display Year";
    public int currentYear;
    public int getCurrentYear() {
        return currentYear;
    }


    private PrevYearButtonListener prevYearButtonListener;
    @InjectView(R.id.song_artist_text)
    TextView yearTemp;
    @InjectView(R.id.facebook_view)
    RelativeLayout facebookView;
    @InjectView(R.id.no_facebook_account)
    TextView noFacebook;

    public interface PrevYearButtonListener {
        void onPrevYearButtonClicked();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            prevYearButtonListener = (PrevYearButtonListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PrevYearButtonListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
         //the current year, for future use.
        currentYear = getArguments().getInt(YEAR);

        sendNytGetRequest(Integer.toString(currentYear));

        View rootView = inflater.inflate(R.layout.fragment_year, container, false);
        ButterKnife.inject(this, rootView);
        yearTemp.setText(String.valueOf(currentYear));
        getChildFragmentManager().beginTransaction()
                .add(R.id.facebook_view, FacebookPostsFragment.getInstance(currentYear))
                .commit();

        Button prevYearButton = (Button) rootView.findViewById(R.id.previous_year_button);
        prevYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevYearButtonListener.onPrevYearButtonClicked();
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (preferences.getString(Constants.SHARED_PREFS_ACCESS_TOKEN, null) == null) {
            noFacebook.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    private void sendNytGetRequest(String currentYear) {
        String day = new SimpleDateFormat("MMdd").format(new Date());
        String date = currentYear + day;
        Log.d(TAG, date);

        NytServiceAdapter.getNytServiceInstance()
                .getNytArticle(date, date, new Callback<Object>() {
                    @Override
                    public void success(Object o, Response response) {
                        Log.d(TAG, o.toString());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, error.toString());
                    }
                });

    }
}
