package io.intrepid.nostalgia;

public class SinglePlayer {
    private static android.media.MediaPlayer mediaPlayerInstance;

    public static android.media.MediaPlayer getInstance() {
        if (mediaPlayerInstance == null) {
            mediaPlayerInstance = new android.media.MediaPlayer();
        }
        return mediaPlayerInstance;
    }

}
