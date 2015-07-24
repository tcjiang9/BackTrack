package io.intrepid.nostalgia.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.intrepid.nostalgia.DateFormatter;
import io.intrepid.nostalgia.adapters.NytServiceAdapter;
import io.intrepid.nostalgia.R;
import io.intrepid.nostalgia.models.nytmodels.Byline;
import io.intrepid.nostalgia.models.nytmodels.Doc;
import io.intrepid.nostalgia.models.nytmodels.NyTimesReturn;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewsFragment extends Fragment {
    public static final String TAG = NewsFragment.class.getSimpleName();
    public static final String YEAR = "Display Year";
    public static final String TEXT_PLAIN = "text/plain";
    private String sharingHeader;

    public String url;
    String CURRENT_YEAR;

    @InjectView(R.id.ufo_image_transition)
    ImageView ufoTransition;

    @InjectView(R.id.news_headline)
    TextView newsHeadline;

    @InjectView(R.id.news_body)
    TextView newsBody;

    @InjectView(R.id.news_byline)
    TextView newsByline;

    @InjectView(R.id.ribbon_date)
    TextView ribbonDate;

    @OnClick(R.id.share_button)
    void shareNews() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType(TEXT_PLAIN);
        i.putExtra(Intent.EXTRA_SUBJECT, "This day in " + Integer.toString(getArguments().getInt(YEAR)));
        i.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(i, sharingHeader));
    }

    @OnClick(R.id.read_more_nyt)
    void onReadMore() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.inject(this, rootView);
        sendNytGetRequest();
        sharingHeader = getString(R.string.share_via);
        return rootView;
    }

    public static NewsFragment getInstance(int currentYear) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, currentYear);
        fragment.setArguments(args);
        return fragment;
    }

    public void sendNytGetRequest() {
        CURRENT_YEAR = Integer.toString(getArguments().getInt(YEAR));
        String date = DateFormatter.makeNytDate(CURRENT_YEAR);
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@" + CURRENT_YEAR);
        changeUfoImage();
        NytServiceAdapter.getNytServiceInstance()
                .getNytArticle(date, date, new Callback<NyTimesReturn>() {
                    @Override
                    public void success(NyTimesReturn timesReturn, Response response) {
                        Doc docs = timesReturn.getResponse().getDocs().get(0);
                        String webUrl = docs.getWebUrl();
                        String headline = docs.getHeadline().getMain();
                        String snippet = docs.getSnippet();
                        String pubDate = docs.getPubDate();
                        Byline byline = docs.getByline();

                        url = webUrl;
                        String by = (byline == null) ? " " : byline.getOriginal();
                        newsHeadline.setText(headline);
                        newsByline.setText(by);
                        newsBody.setText(snippet);
                        ribbonDate.setText(DateFormatter.makeRibbonDateText());
                        Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + pubDate + webUrl + headline + snippet);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!" + error.toString());
                    }
                });

    }

    private void changeUfoImage() {
        int constantYr = 1980;
        int currentYer = Integer.parseInt(CURRENT_YEAR);
        int difference = currentYer - constantYr;
        if (difference >= 30) {
            ufoTransition.setImageResource(R.drawable.ufo);
        } else if (difference >= 20) {
            ufoTransition.setImageResource(R.drawable.rocket_2000);
        } else if (difference >= 10) {
            ufoTransition.setImageResource(R.drawable.plane_90s);
        } else if (difference >= 0 || difference < 0) {
            ufoTransition.setImageResource(R.drawable.hot_balloon_80s);
        }
    }
}
