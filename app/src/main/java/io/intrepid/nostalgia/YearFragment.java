package io.intrepid.nostalgia;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.intrepid.nostalgia.facebook.FacebookPostsFragment;
import io.intrepid.nostalgia.models.itunesmodels.ItunesResults;
import io.intrepid.nostalgia.models.itunesmodels.ItunesSong;
import io.intrepid.nostalgia.models.nytmodels.Doc;
import io.intrepid.nostalgia.models.nytmodels.NyTimesReturn;
import io.intrepid.nostalgia.songdatabase.DatabaseExplorer;
import io.intrepid.nostalgia.songdatabase.DatabaseHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class YearFragment extends Fragment implements ViewPagerFragmentLifeCycle {
    public static final String TAG = YearFragment.class.getSimpleName();
    public static final String YEAR = "Display Year";
    public static final String KEY = "year";
    public int currentYear;

    private boolean autoPlay = true;
    private PrevYearButtonListener prevYearButtonListener;
    private MediaPlayer mediaPlayer;
    private boolean isPreparing = false;
    private String iTunesUrl;
    private String previewUrl = "http://a1654.phobos.apple.com/us/r1000/022/Music/v4/06/a1/0c/06a10c8b-e358-4bc0-c443-a120a775d3df/mzaf_1439207983024487820.plus.aac.p.m4a";
    private String[] songDetails = new String[2];

    @InjectView(R.id.play_music_button)
    Button playMusicButton;

    @InjectView(R.id.song_artist_text)
    TextView yearTemp;

    @InjectView(R.id.facebook_view)
    RelativeLayout facebookView;

    @InjectView(R.id.no_facebook_account)
    TextView noFacebook;

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
        DatabaseHelper myDbHelper = new DatabaseHelper(getActivity());

// initialize db
        try {

            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        //call for song title and artist name
        try {
            Cursor c = myDbHelper.getData(String.valueOf(currentYear));
            Random random = new Random();
            int index = random.nextInt(c.getCount() + 1);
            songDetails = getRandomSongFromDb(c, index);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        String songTitle = songDetails[0]; //fetch from DB later
        String songArtist = songDetails[1];
        Log.i(TAG, songTitle);
        Log.i(TAG, songArtist);
        iTunesUrl = fetchPreviewUrl(songTitle, songArtist);

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
                    playMusic(mediaPlayer, iTunesUrl); //Todo: modify this method param to take a JSON string as well when the time comes
                } else {
                    Log.i(TAG, "You stopped the media player");
                    stopMusic();
                }
            }
        });


        yearTemp.setText(String.valueOf(currentYear));
        getChildFragmentManager().beginTransaction()
                .add(R.id.facebook_view, FacebookPostsFragment.getInstance(currentYear))
                .commit();

        getChildFragmentManager().beginTransaction()
                .add(R.id.news_view, NewsFragment.getInstance(currentYear))
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

        return rootView;
    }
    private String[] getRandomSongFromDb(Cursor c, int index) {
        String[] artistAndSong = new String[2];
        c.moveToPosition(index);
        artistAndSong[0] = c.getString(0);
        artistAndSong[1] = c.getString(1);

        c.close();
        return artistAndSong;
    }

    private String fetchPreviewUrl(String songTitle, final String songArtist) {
        ItunesService itunesService = ItunesServiceAdapter.getItunesServiceInstance();
        itunesService.listSongInfo(songTitle, Constants.COUNTRY, Constants.SONG, new Callback<ItunesResults>() {
            @Override
            public void success(ItunesResults itunesResults, Response response) {
                List<ItunesSong> itunesSongs = itunesResults.getResults();
                for (ItunesSong s : itunesSongs) {
                    if (s.getArtistName().equals(songArtist)){
                        iTunesUrl = s.getPreviewUrl();
                        Log.i(TAG, iTunesUrl);
                        break;
                    }
                }
            }
            @Override
            public void failure(RetrofitError error) {
            }
        });
        return null;
    }

    private void playMusic(final MediaPlayer mediaPlayer, String songUrl) {
        if (songUrl == null) {
            Log.i(TAG, "!!!!!TRACK NOT FOUND!!!!!!!");
            return;
        }
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Log.i(TAG, "Right before data source");
            Log.i(TAG, songUrl);
            mediaPlayer.setDataSource(songUrl);
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
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopMusic();
                    isPreparing = false;
                    Log.i(TAG, "Music completed");
                }
            });
            mediaPlayer.prepareAsync();
            isPreparing = true;
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            Log.i(TAG, "!!!!!TRACK NOT FOUND!!!!!!!");
            return;
        }
    }

    private void stopMusic() {
        if (mediaPlayer.isPlaying()) {
            Log.i(TAG, "Stopping mediaPlayer via stopMusic()");
            mediaPlayer.stop();
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playMusicButton.setText(R.string.button_text_play);
                Log.i(TAG, "Button text set, resetting player");
            }
        });
        mediaPlayer.reset();
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
