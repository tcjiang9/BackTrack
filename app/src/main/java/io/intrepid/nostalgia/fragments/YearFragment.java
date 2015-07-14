package io.intrepid.nostalgia.fragments;

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
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.intrepid.nostalgia.DateFormatter;
import io.intrepid.nostalgia.adapters.ItunesServiceAdapter;
import io.intrepid.nostalgia.R;
import io.intrepid.nostalgia.SinglePlayer;
import io.intrepid.nostalgia.ViewPagerFragmentLifeCycle;
import io.intrepid.nostalgia.constants.Constants;
import io.intrepid.nostalgia.models.itunesmodels.ItunesResults;
import io.intrepid.nostalgia.models.itunesmodels.ItunesSong;
import io.intrepid.nostalgia.services.ItunesService;
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

    private PrevYearButtonListener prevYearButtonListener;
    private MediaPlayer mediaPlayer;
    private boolean autoPlay = true;
    private boolean isPreparing = false;
    private boolean isPaused = false;
    private String[] songDetails = new String[2];
    private String songUrl;
    private String imageUrl;

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

    @InjectView(R.id.music_image)
    ImageView musicImage;

    public interface PrevYearButtonListener {
        void onPrevYearButtonClicked();
    }

    private enum Actions {
        starting, stopping
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
            int index = random.nextInt(c.getCount());
            songDetails = getRandomSongFromDb(c, index);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        String songTitle = songDetails[0];
        String songArtist = songDetails[1];
        String searchTerm = songTitle + " " + songArtist;

        fetchMusicJson(searchTerm);

        View rootView = inflater.inflate(R.layout.fragment_year, container, false);
        ButterKnife.inject(this, rootView);

        playMusicButton.setText(mediaPlayer != null && mediaPlayer.isPlaying()
                ? R.string.button_text_pause
                : R.string.button_text_play);

        playMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = SinglePlayer.getInstance().getMediaPlayer();
                if (isPreparing) {
                    Log.i(TAG, "it thinks we're preparing");
                    return;
                } else if (!mediaPlayer.isPlaying()) {
                    playMusic(mediaPlayer, songUrl);
                } else { //music is currently playing
                    Log.i(TAG, "You stopped the media player");
                    mediaPlayer.pause();
                    updateUi(Actions.stopping);
                    isPaused = true;
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

    /**
     * Modifies songUrl to contain the iTunes preview url of the song found via the search term
     * Modifies imageUrl to contain the iTunes url for the artist image
     *
     * @param searchTerm the "term=" query in our http request, the artist name and song name concatanated with a space
     *                   in between
     */
    private void fetchMusicJson(String searchTerm) {
        ItunesService itunesService = ItunesServiceAdapter.getItunesServiceInstance();
        String limit = "2";
        itunesService.listSongInfo(
                searchTerm,
                Constants.COUNTRY,
                Constants.SONG,
                limit,
                new Callback<ItunesResults>() {
                    @Override
                    public void success(ItunesResults itunesResults, Response response) {
                        List<ItunesSong> itunesSongs = itunesResults.getResults();
                        if (itunesSongs.size() > 0) {
                            Log.i(TAG, itunesSongs.toString());
                            songUrl = itunesSongs.get(0).getPreviewUrl();
                            imageUrl = itunesSongs.get(0).getArtworkUrl100()
                                    .replaceAll("100x100", Constants.IMAGE_WIDTH + "x" + Constants.IMAGE_HEIGHT);
                            Picasso.with(getActivity()).load(imageUrl).into(musicImage);
                            Log.i(TAG, imageUrl);
                        } else {
                            return;
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
    }

    private void playMusic(final MediaPlayer mediaPlayer, String songUrl) {
        if (songUrl == null) {
            Log.i(TAG, "!!!!!TRACK NOT FOUND!!!!!!!");
            return;
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPaused = true;
                updateUi(Actions.stopping);
                isPreparing = false;
                Log.i(TAG, "Music completed");
            }
        });
        if (isPaused) {
            mediaPlayer.start();
            isPaused = false;
            updateUi(Actions.starting);
        } else {
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
                        Log.i(TAG, "this has prepared");
                        isPreparing = false;
                        updateUi(Actions.starting);
                    }
                });
                mediaPlayer.prepareAsync();
                isPreparing = true;
                playMusicButton.setText(R.string.button_text_loading);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return;
            }
        }
    }

    private void stopMusic() {
        if (mediaPlayer.isPlaying()) {
            Log.i(TAG, "Stopping mediaPlayer via stopMusic()");
            mediaPlayer.stop();
        }
        //  getActivity().runOnUiThread(new Runnable() {
        //    @Override
        //    public void run() {
        updateUi(Actions.stopping);
        Log.i(TAG, "Button text set, resetting player");
        //  }
        //});
        mediaPlayer.reset();
    }

    public void updateUi(Actions action) {
        if (action == Actions.stopping) {
            playMusicButton.setText(R.string.button_text_play);
            musicImage.clearAnimation();
        } else if (action == Actions.starting) {
            playMusicButton.setText(R.string.button_text_pause);
            musicImage.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.spinner_clockwise));
        }
    }
    @Override
    public void onPauseFragment() {
        if (mediaPlayer == null) {
            mediaPlayer = SinglePlayer.getInstance().getMediaPlayer();
        }
        Log.i(TAG, String.valueOf(currentYear) + " This has paused fragment");
        stopMusic();
        initPlayer();
        /**
         Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
         Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
         for (int i = 0; i < threadArray.length; i ++) {
         Log.i(TAG, threadArray[i].toString());
         }
         **/
    }

    public void onResumeFragment() {
        updateUi(Actions.stopping);
        initPlayer();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        //if (sharedPreferences.getBoolean(Constants.SHARED_PREFS_AUTOPLAY, true)) {
        //   playMusic(mediaPlayer);
        //      }
    }

    private void initPlayer() {
        mediaPlayer = SinglePlayer.getInstance().getMediaPlayer();
        isPreparing = false;
        isPaused = false;
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
