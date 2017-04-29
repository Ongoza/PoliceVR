package com.ongoza.os.policevr;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.Surface;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayer.EventListener;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import org.gearvrf.GVRActivity;
import org.gearvrf.scene_objects.GVRVideoSceneObject;
import org.gearvrf.scene_objects.GVRVideoSceneObjectPlayer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;

public class VideoActivity extends GVRActivity {
    private static final String TAG = "PoliceVideo";
    private MainActivity main;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.w(TAG, "create player");
        if (!USE_EXO_PLAYER) { videoSceneObjectPlayer = makeMediaPlayer();
        }else { videoSceneObjectPlayer = makeExoPlayer(); }
        if (null != videoSceneObjectPlayer) {
            main = new MainActivity(videoSceneObjectPlayer);
            setMain(main, "gvr.xml");}
    }
    @Override protected void onPause() { super.onPause();
        if (null != videoSceneObjectPlayer) { final Object player = videoSceneObjectPlayer.getPlayer();
            if (!USE_EXO_PLAYER) {
                MediaPlayer mediaPlayer = (MediaPlayer) player; mediaPlayer.pause();
            } else { ExoPlayer exoPlayer = (ExoPlayer) player; exoPlayer.setPlayWhenReady(false); }
        }
    }
    @Override protected void onResume() { super.onResume();
        if (null != videoSceneObjectPlayer) { final Object player = videoSceneObjectPlayer.getPlayer();
            if (!USE_EXO_PLAYER) { MediaPlayer mediaPlayer = (MediaPlayer) player; mediaPlayer.start();
            } else { ExoPlayer exoPlayer = (ExoPlayer) player; exoPlayer.setPlayWhenReady(true);}
        }
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        main.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private GVRVideoSceneObjectPlayer<MediaPlayer> makeMediaPlayer() { final MediaPlayer mediaPlayer = new MediaPlayer();
        final AssetFileDescriptor afd2; AssetManager assetManager = getAssets();  String jsonFile="";
        JSONObject jVideoList;  JSONArray videoList = null;
        try { InputStream is = getAssets().open("media.exolist.json");
            int size = is.available(); byte[] buffer = new byte[size]; is.read(buffer); is.close(); jsonFile = new String(buffer, "UTF-8");
            android.util.Log.d(TAG, jsonFile);
        } catch (IOException e) { android.util.Log.e(TAG,"can't open video list file"); e.printStackTrace(); }
        try{jVideoList = new JSONObject(jsonFile); videoList = jVideoList.getJSONArray("Start");
        }catch (JSONException e){android.util.Log.e(TAG,"error"); e.printStackTrace(); }
        if(videoList!=null){String videofile="";
            try{videofile = videoList.getString(0); android.util.Log.d(TAG,  videofile);
            }catch (JSONException e){ android.util.Log.e(TAG,"error parse filename");}
            try { afd2 = getAssets().openFd(videofile);
                android.util.Log.d(TAG, "Assets was found.");
                mediaPlayer.setDataSource(afd2.getFileDescriptor(), afd2.getStartOffset(), afd2.getLength());
                android.util.Log.d(TAG, "DataSource was set.");
                afd2.close();
                mediaPlayer.prepare();
//                mediaPlayer.pause();
                // PlaybackCompleted
//            afd3 = getAssets().openFd("scenario_3.mp4");
//            android.util.Log.d(TAG, "Assets was found.");
//            mediaPlayer.setDataSource(afd3.getFileDescriptor(), afd3.getStartOffset(), afd3.getLength());
//            android.util.Log.d(TAG, "DataSource was set.");
//            afd3.close();
            } catch (IOException e) { e.printStackTrace(); finish();  android.util.Log.e(TAG, "Assets were not loaded. Stopping application!"); return null; }
        }else { android.util.Log.e(TAG, "No video for playing. Stopping application!"); }
        mediaPlayer.setLooping(false);android.util.Log.d(TAG, "starting player.");
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
           @Override public void onCompletion(MediaPlayer mp) { android.util.Log.d(TAG, "End video");
            main.hideMovie();
           }});
        mediaPlayer.setOnPreparedListener (new MediaPlayer.OnPreparedListener() {
            @Override public void onPrepared(MediaPlayer mp) {
                android.util.Log.d(TAG, "prepared video");
                mediaPlayer.pause();
            }});

        return GVRVideoSceneObject.makePlayerInstance(mediaPlayer);
    }




    /// does not work!!!!!
    private GVRVideoSceneObjectPlayer<SimpleExoPlayer> makeExoPlayer() {
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        String urimp4 = "asset:///video1.mp4";
        Uri mp4VideoUri = Uri.parse(urimp4);
//yachts livestream m3m8 file:
        //     Uri mp4VideoUri =Uri.parse("http://fluvod1.giniko.com/all-luxury-yachts/all-luxury-yachts/tracks-1,6/index.m3u8");
        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayerText"), bandwidthMeterA);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);
        final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource); player.prepare(loopingSource);
        return new GVRVideoSceneObjectPlayer<SimpleExoPlayer>() {
            @Override public SimpleExoPlayer getPlayer() { android.util.Log.e(TAG,"START EXO"); return player;}
            @Override public void setSurface(final Surface surface) {
                player.addListener(new EventListener() {
                    @Override public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        // 4- STATE_ENDED 3 - STATE_READY 1 - STATE_IDLE 2 - STATE_BUFFERINg
                        android.util.Log.d(TAG, "StateChanged="+String.valueOf(playbackState)); }
                    @Override public void onPositionDiscontinuity(){android.util.Log.d(TAG, "PositionDiscontinuity");}
                    @Override public void onLoadingChanged(boolean dl){android.util.Log.d(TAG, "LoadingChanged"); }
                    @Override public void onTimelineChanged(Timeline tm, Object obj){android.util.Log.d(TAG, "TimelineChanged"); }
                    @Override public void onPlayerError(ExoPlaybackException  err){ android.util.Log.d(TAG, "PlayerError"); player.stop();  }
                    @Override public void onTracksChanged(TrackGroupArray trackGr, TrackSelectionArray TrackSel) { android.util.Log.d(TAG, "TracksChanged"); }
                });
                // !!!!!!!!does not work output video to shpera
                // player.setPlayWhenReady(true);
                //  player.sendMessage(videoRenderer, MediaCodecRenderer. .MSG_SET_SURFACE, surface);
            }
            @Override public void release() { android.util.Log.e(TAG,"2 STATE RELEASE"); player.release(); }
            @Override public boolean canReleaseSurfaceImmediately() { android.util.Log.e(TAG,"2 STATE CAN RELEASE"); return false; }
            @Override public void pause() {android.util.Log.e(TAG,"2 STATE PAUSE"); player.setPlayWhenReady(false);}
            @Override public void start() {android.util.Log.e(TAG,"2 STATE START");player.setPlayWhenReady(true);}
        };
    }
    private GVRVideoSceneObjectPlayer<?> videoSceneObjectPlayer;
    static final boolean USE_EXO_PLAYER = false;
}