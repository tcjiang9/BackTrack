package io.intrepid.nostalgia;

import android.media.MediaPlayer;

public class SinglePlayer {

    private MediaPlayer mediaPlayerInstance;
    //private boolean isPreparing;

    private SinglePlayer() {
        mediaPlayerInstance = new android.media.MediaPlayer();
    }

    private static SinglePlayer singlePlayerInstance;

    public static SinglePlayer getInstance() {
        if (singlePlayerInstance == null) {
            singlePlayerInstance = new SinglePlayer();
        }
        return singlePlayerInstance;
    }

    public MediaPlayer getMediaPlayer() {
        return this.mediaPlayerInstance;
    }

}
