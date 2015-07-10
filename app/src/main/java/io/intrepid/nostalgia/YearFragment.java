package io.intrepid.nostalgia;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.intrepid.nostalgia.facebook.FacebookPostsFragment;
import io.intrepid.nostalgia.nytmodels.Doc;
import io.intrepid.nostalgia.nytmodels.NyTimesReturn;
import io.intrepid.nostalgia.songdatabase.DatabaseExplorer;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class YearFragment extends Fragment implements ViewPagerFragmentLifeCycle {
    public static final String TAG = YearFragment.class.getSimpleName();
    public static final String YEAR = "Display Year";
    public static final String KEY = "year";
    public int currentYear;

    //modify this parameter through settings
    private boolean autoPlay = true;
    private PrevYearButtonListener prevYearButtonListener;
    private MediaPlayer mediaPlayer;
    private boolean isPreparing = false;
    private String iTunesUrl = "http://a1654.phobos.apple.com/us/r1000/022/Music/v4/06/a1/0c/06a10c8b-e358-4bc0-c443-a120a775d3df/mzaf_1439207983024487820.plus.aac.p.m4a";

    @InjectView(R.id.play_music_button)
    Button playMusicButton;

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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mediaPlayer = SinglePlayer.getInstance().getMediaPlayer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //the current year, for future use.
        currentYear = getArguments().getInt(YEAR);

        View rootView = inflater.inflate(R.layout.fragment_year, container, false);
        ButterKnife.inject(this, rootView);

        playMusicButton.setText(mediaPlayer != null && mediaPlayer.isPlaying()
                ? R.string.button_text_stop
                : R.string.button_text_play);

        playMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = SinglePlayer.getInstance().getMediaPlayer();
                if (isPreparing) {
                    Log.i(TAG, "it thinks we're preparing");
                    return;
                } else if (!mediaPlayer.isPlaying()) {
                    playMusic(mediaPlayer); //Todo: modify this method param to take a JSON string as well when the time comes
                } else {
                    Log.i(TAG, "You stopped the media player");
                    stopMusic();
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopMusic();
                isPreparing = false;
                Log.i(TAG, "Music completed");
            }
        });

        yearTemp.setText(String.valueOf(currentYear));
        getChildFragmentManager().beginTransaction()
                .add(R.id.facebook_view, FacebookPostsFragment.getInstance(currentYear))
                .commit();

        RelativeLayout placeHolder = (RelativeLayout) rootView.findViewById(R.id.news_view);
        inflater.inflate(R.layout.fragment_news, placeHolder);

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

    public void playMusic(final MediaPlayer mediaPlayer) {
        // Todo: fetch this url string from an iTunes JSON instead of hardcoding
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Log.i(TAG, "Right before data source");
            mediaPlayer.setDataSource(iTunesUrl);
            Log.i(TAG, "!!!!!!!!About to prepare async!!!!!!!!!!!");
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    playMusicButton.setText(R.string.button_text_stop);
                    Log.i(TAG, "this has prepared");
                    isPreparing = false;
                }
            });
            mediaPlayer.prepareAsync();
            isPreparing = true;
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void stopMusic() {
        if (mediaPlayer.isPlaying()) {
            Log.i(TAG, "Stopping mediaPlayer via stopMusic()");
            mediaPlayer.stop();
        }
        playMusicButton.setText(R.string.button_text_play);
        Log.i(TAG, "Button text set, resetting player");
        mediaPlayer.reset();
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

    @Override
    public void onPauseFragment() {
        Log.i(TAG, String.valueOf(currentYear) + "This has pausedfragment");
        isPreparing = false;
        stopMusic();
        /**
         Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
         Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
         for (int i = 0; i < threadArray.length; i ++) {
         Log.i(TAG, threadArray[i].toString());
         }
         **/
    }

    public void onResumeFragment() {
        playMusicButton.setText(R.string.button_text_play);
        mediaPlayer = SinglePlayer.getInstance().getMediaPlayer();
        //if (autoPlay) {
        //   playMusic(mediaPlayer);
        //      }
    }

    @OnClick(R.id.date_text)
    void dbConnect() {
        Intent intent = new Intent(getActivity(), DatabaseExplorer.class);
        Bundle addYear = new Bundle();
        addYear.putString(KEY, Integer.toString(currentYear));
        intent.putExtras(addYear);
        startActivity(intent);
    }
}
