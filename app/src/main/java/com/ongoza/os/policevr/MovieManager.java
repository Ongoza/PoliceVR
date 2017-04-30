package com.ongoza.os.policevr;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import org.gearvrf.GVRContext;
import org.gearvrf.utility.Log;
import java.io.IOException;

public class MovieManager {
    private static final String TAG = "PoliceVideo";
    private MediaPlayer mMediaPlayer = null;
    private MainActivity main;

    public MovieManager (GVRContext context, String fileName) {
        main = new MainActivity();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setLooping(false);android.util.Log.d(TAG, "starting player.");
        try {
            AssetFileDescriptor fileDescriptor = context.getContext().getAssets().openFd(fileName);
            mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            fileDescriptor.close();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override public void onCompletion(MediaPlayer mp) {
            android.util.Log.d(TAG, "End video");
//            main.MoviePlaying = false;
//            destroy();
           }});

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "onPrepared");
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Failed to open the media file");
            e.printStackTrace();
            mMediaPlayer = null;
        } catch (IllegalStateException e) {
            Log.e(TAG, "Failed to prepare media player");
            e.printStackTrace();
            mMediaPlayer = null;
        }
    }
    private void destroy(){ mMediaPlayer = null;}
    public MediaPlayer getMediaPlayer() { return mMediaPlayer; }
}
