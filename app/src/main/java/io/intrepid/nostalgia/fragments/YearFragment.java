package io.intrepid.nostalgia.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.intrepid.nostalgia.DateFormatter;
import io.intrepid.nostalgia.R;
import io.intrepid.nostalgia.SinglePlayer;
import io.intrepid.nostalgia.ViewPagerFragmentLifeCycle;
import io.intrepid.nostalgia.adapters.ItunesServiceAdapter;
import io.intrepid.nostalgia.constants.Constants;
import io.intrepid.nostalgia.models.itunesmodels.ItunesResults;
import io.intrepid.nostalgia.models.itunesmodels.ItunesSong;
import io.intrepid.nostalgia.services.ItunesService;
import io.intrepid.nostalgia.songdatabase.DatabaseHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class YearFragment extends Fragment implements ViewPagerFragmentLifeCycle {

    public static final String TAG = YearFragment.class.getSimpleName();
    public static final String YEAR = "Display Year";
    public static final String KEY = "year";
    public int currentYear;
    private boolean isActive = false;
    private boolean isMusicImageLoaded = false;

    // MediaPlayer variables
    private MediaPlayer mediaPlayer;
    private boolean isPreparing = false;
    private boolean isPaused = false;

    //Database variables
    private DatabaseHelper myDbHelper;

    //Song variables
    private String songUrl;
    private String imageUrl;

    //Animation variables
    private ObjectAnimator discAnimator;
    private ObjectAnimator loadingAnimator;
    private long currentDiscAnimTime;
    private ObjectAnimator handleAnimator;
    private boolean isHandleAnimComplete = false;


    @InjectView(R.id.play_music_button)
    ImageButton playMusicButton;

    @InjectView(R.id.song_title_text)
    TextView songTitleView;

    @InjectView(R.id.song_artist_text)
    TextView songArtistView;

    @InjectView(R.id.facebook_view)
    RelativeLayout facebookView;

    @InjectView(R.id.no_facebook_account)
    TextView noFacebook;

    @InjectView(R.id.date_text)
    TextView dateText;

    @InjectView(R.id.music_image)
    ImageView musicImage;

    @InjectView(R.id.handle)
    ImageView handleImage;

    private enum Actions {
        starting, stopping, loading
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // the current year, for future use.
        currentYear = getArguments().getInt(YEAR);
        Log.i(TAG, String.valueOf(currentYear) + " HAS CALLED ONCREATEVIEW");

        initializeDb();
        String[] songDetails = getDbSongInfo();

        String songTitle = songDetails[0];
        String songArtist = songDetails[1];
        String searchTerm = songTitle + " " + songArtist;

        if (songUrl == null || imageUrl == null) {
            fetchMusicJson(searchTerm);
        }

        View rootView = inflater.inflate(R.layout.fragment_year, container, false);
        ButterKnife.inject(this, rootView);

        initAnimators();
        initPlayer();

        dateText.setText(DateFormatter.makeDateText(Integer.toString(currentYear)));

        playMusicButton.setImageResource(mediaPlayer != null && mediaPlayer.isPlaying()
                ? R.drawable.pause_circle_button
                : R.drawable.play_circle_button);

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

        songTitleView.setText(songTitle);
        songArtistView.setText(songArtist);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.facebook_view, FacebookPostsFragment.getInstance(currentYear))
                .commit();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.news_view, NewsFragment.getInstance(currentYear))
                .commit();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (preferences.getString(Constants.SHARED_PREFS_ACCESS_TOKEN, null) == null) {
            noFacebook.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    private void initializeDb() {
        myDbHelper = new DatabaseHelper(getActivity());
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
    }

    private String[] getDbSongInfo() {
        String[] artistAndSong = new String[2];
        try {
            Cursor c = myDbHelper.getData(String.valueOf(currentYear));
            int i = DateFormatter.getDay();
            int index = c.getCount() % i;
            artistAndSong = getSongFromDb(c, index);
            myDbHelper.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return artistAndSong;
    }

    private String[] getSongFromDb(Cursor c, int index) {
        String[] artistAndSong = new String[2];
        c.moveToPosition(index);
        artistAndSong[0] = c.getString(0);
        artistAndSong[1] = c.getString(1);
        c.close();
        return artistAndSong;
    }

    /**
     * Fetches the music json and if autoplay is enabled, plays the music when the information is
     * ready
     * <p/>
     * Modifies songUrl to contain the iTunes preview url of the song found via the search term
     * Modifies imageUrl to contain the iTunes url for the artist image
     * Modifies musicImage to display the image from imageUrl
     *
     * @param searchTerm the "term=" query in our http request, the artist name and song name
     *                   concatanated with a space in between
     */
    private void fetchMusicJson(String searchTerm) {
        ItunesService itunesService = ItunesServiceAdapter.getItunesServiceInstance();
        String limit = "1";
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
                            if (getActivity() != null && !isMusicImageLoaded) {
                                Picasso.with(getActivity())
                                        .load(imageUrl)
                                        .placeholder(R.drawable.default_record)
                                        .into(musicImage);
                                Log.i(TAG, imageUrl);
                                isMusicImageLoaded = true;
                            }
                            if (isActive && checkAutoPlay()) {
                                playMusic(mediaPlayer, songUrl);
                            }

                        } else {
                            return;
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //Todo: show toast on failure
                    }
                });
    }

    private void initAnimators() {
        discAnimator = ObjectAnimator.ofFloat(musicImage, "rotation", 0f, 2160f);
        discAnimator.setDuration(Constants.SONG_DURATION); //30 seconds in ms
        discAnimator.setRepeatCount(ValueAnimator.INFINITE);

        loadingAnimator = ObjectAnimator.ofFloat(playMusicButton, "translation", 0f, 100f);
        loadingAnimator.setDuration(3000);
        loadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        loadingAnimator.setRepeatMode(ValueAnimator.REVERSE);
        loadingAnimator.setInterpolator(new DecelerateInterpolator());

        handleImage.setPivotX(getResources().getDimension(R.dimen.pivot_x));
        handleImage.setPivotY(getResources().getDimension(R.dimen.pivot_y));
        handleAnimator = ObjectAnimator.ofFloat(handleImage, "rotation", 0f, 30f);
        handleAnimator.setDuration(2000);
        handleAnimator.setRepeatCount(0);
        handleAnimator.setInterpolator(new DecelerateInterpolator());
        handleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //on animation complete
                mediaPlayer.start();
                Log.i(TAG, "this has prepared");
                isPreparing = false;
                updateUi(Actions.starting);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void pauseDiscAnimation() {
        currentDiscAnimTime = discAnimator.getCurrentPlayTime();
        discAnimator.cancel();
    }

    private void startDiscAnimation() {
        discAnimator.start();
        discAnimator.setCurrentPlayTime(currentDiscAnimTime);
    }

    private void playMusic(final MediaPlayer mediaPlayer, String songUrl) {
        if (songUrl == null) {
            Log.i(TAG, "!!!!!TRACK NOT FOUND!!!!!!!");
            return;
        }
        if (mediaPlayer.isPlaying()) {
            Log.i(TAG, "Attempted to play music while already playing");
            return;
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                updateUi(Actions.stopping);
                isPreparing = false;
                Log.i(TAG, "Music completed");
            }
        });
        if (isPaused) {
            mediaPlayer.start();
            Log.i(TAG, "QUICKSTART");
            isPaused = false;
            updateUi(Actions.starting);
        } else {
            try {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Log.i(TAG, songUrl);
                mediaPlayer.setDataSource(songUrl);
                Log.i(TAG, "!!!!!!!!About to prepare async!!!!!!!!!!!");
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        if (!isHandleAnimComplete) {
                            handleAnimator.start();
                        } else {
                            mediaPlayer.start();
                            Log.i(TAG, "this has prepared");
                            isPreparing = false;
                            updateUi(Actions.starting);
                        }
                    }
                });
                mediaPlayer.prepareAsync();
                isPreparing = true;
                updateUi(Actions.loading);
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
        updateUi(Actions.stopping);
        Log.i(TAG, "Button text set, resetting player");
        mediaPlayer.reset();
    }

    private void updateUi(Actions action) {
        if (action == Actions.stopping) {
            playMusicButton.setImageResource(R.drawable.play_circle_button);
            pauseDiscAnimation();
        } else if (action == Actions.starting) {
            playMusicButton.setImageResource(R.drawable.pause_circle_button);
            startDiscAnimation();
            playMusicButton.clearAnimation();
        } else if (action == Actions.loading) {
            playMusicButton.setImageResource(R.drawable.music_loading_circle);
            playMusicButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));
        }
    }

    @Override
    public void onPauseFragment() {
        isActive = false;
        if (mediaPlayer == null) {
            mediaPlayer = SinglePlayer.getInstance().getMediaPlayer();
        }
        discAnimator.end();
        handleImage.setRotation(0);

        Log.i(TAG, String.valueOf(currentYear) + " This has paused fragment");
        stopMusic();
        initPlayer();
    }

    @Override
    public void onResumeFragment() {
        isActive = true;
        initPlayer();
        Log.i(TAG, String.valueOf(currentYear) + " HAS RESUMED");
        updateUi(Actions.stopping);
        if (imageUrl != null && !isMusicImageLoaded) {
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .placeholder(R.drawable.default_record)
                    .into(musicImage);
            isMusicImageLoaded = true;
        }
        if (songUrl != null && checkAutoPlay()) {
            Log.i(TAG, "ITS PLAYING MUSIC THROUGH ONRESUMEFRAGMENT");
            playMusic(mediaPlayer, songUrl);
        }
    }

    private boolean checkAutoPlay() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPreferences.getBoolean(Constants.SHARED_PREFS_AUTOPLAY, true);
    }

    public void setActive() {
        isActive = true;
    }

    private void initPlayer() {
        mediaPlayer = SinglePlayer.getInstance().getMediaPlayer();
        isPreparing = false;
        isPaused = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, String.valueOf(currentYear) + " has called onPause");
        isMusicImageLoaded = false;
    }
}
