package io.intrepid.nostalgia;

public class SinglePlayer {
    private static android.media.MediaPlayer theMediaPlayer;

    public static android.media.MediaPlayer getInstance() {
        if (theMediaPlayer == null) {
            theMediaPlayer = new android.media.MediaPlayer();
        }
        return theMediaPlayer;
    }

}
