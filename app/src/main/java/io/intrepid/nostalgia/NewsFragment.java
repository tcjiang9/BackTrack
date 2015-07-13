package io.intrepid.nostalgia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.intrepid.nostalgia.facebook.FacebookPostsFragment;
import io.intrepid.nostalgia.nytmodels.Doc;
import io.intrepid.nostalgia.nytmodels.NyTimesReturn;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewsFragment extends Fragment {
    public static final String TAG = NewsFragment.class.getSimpleName();
    public static final String YEAR = "Display Year";

    @InjectView(R.id.news_headline)
    TextView newsHeadline;

    @InjectView(R.id.news_body)
    TextView newsBody;

    @InjectView(R.id.news_byline)
    TextView newsByline;

    @InjectView(R.id.ribbon_date)
    TextView ribbonDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.inject(this, rootView);
        sendNytGetRequest();
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
        final String currentYear = Integer.toString(getArguments().getInt(YEAR));
        String date = DateFormatter.makeNytDate(currentYear);
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@" + currentYear);

        NytServiceAdapter.getNytServiceInstance()
                .getNytArticle(date, date, new Callback<NyTimesReturn>() {
                    @Override
                    public void success(NyTimesReturn timesReturn, Response response) {
                        Doc docs = timesReturn.getResponse().getDocs().get(0);
                        String webUrl = docs.getWebUrl();
                        String headline = docs.getHeadline().getMain();
                        String snippet = docs.getSnippet();
                        String pubDate = docs.getPubDate();
                        String byline = docs.getByline().getOriginal();

                        newsHeadline.setText(headline);
                        newsByline.setText(byline);
                        newsBody.setText(snippet);
                        ribbonDate.setText(DateFormatter.makeDateText(currentYear));
                        Log.d(TAG,"!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + pubDate + webUrl + headline + snippet);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG,"!!!!!!!!!!!!!!!!!!!!!!!!!!!" + error.toString());
                    }
                });

    }
}
