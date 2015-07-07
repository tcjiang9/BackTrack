package io.intrepid.nostalgia;

import android.app.Activity;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.intrepid.nostalgia.facebook.*;
import io.intrepid.nostalgia.nytmodels.Doc;
import io.intrepid.nostalgia.nytmodels.NyTimesReturn;
import io.intrepid.nostalgia.songdatabase.DatabaseExplorer;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class YearFragment extends Fragment {
    public static final String TAG = YearFragment.class.getSimpleName();
    public static final String YEAR = "Display Year";
    public static final String KEY = "year";
    public int currentYear;

    private PrevYearButtonListener prevYearButtonListener;

    @InjectView(R.id.song_artist_text)
    TextView yearTemp;

    @InjectView(R.id.facebook_view)
    RelativeLayout facebookView;

    @InjectView(R.id.no_facebook_account)
    TextView noFacebook;

    @InjectView(R.id.news_headline)
    TextView newsHeadline;

    @InjectView(R.id.news_body)
    TextView newsBody;

    @InjectView(R.id.date_text)
    TextView dateText;

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
    @OnClick(R.id.date_text) void dbConnect(){
        Intent intent = new Intent(getActivity(), DatabaseExplorer.class);
        Bundle addYear = new Bundle();
        addYear.putString(KEY, Integer.toString(currentYear));
        intent.putExtras(addYear);
        startActivity(intent);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

         //the current year, for future use.
        currentYear = getArguments().getInt(YEAR);

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

        dateText.setText(DateFormatter.makeDateText(Integer.toString(currentYear)));

        sendNytGetRequest(Integer.toString(currentYear));
        return rootView;
    }

    private void sendNytGetRequest(String currentYear) {
        String date = DateFormatter.makeNytDate(currentYear);
        Log.d(TAG, date);

        NytServiceAdapter.getNytServiceInstance()
                .getNytArticle(date, date, new Callback<NyTimesReturn>() {
                    @Override
                    public void success(NyTimesReturn timesReturn, Response response) {
                        Doc docs = timesReturn.getResponse().getDocs().get(0);
                        String webUrl = docs.getWebUrl();
                        String headline = docs.getHeadline().getMain();
                        String snippet = docs.getSnippet();
                        String pubDate = docs.getPubDate();

                        newsHeadline.setText(headline);
                        newsBody.setText(snippet);
                        Log.d(TAG, pubDate + webUrl + headline + snippet);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, error.toString());
                    }
                });

    }
}
