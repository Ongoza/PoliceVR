package com.ongoza.os.policevr;

import android.content.Context;
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
import java.io.IOException;
import java.io.InputStream;

public class VideoActivity extends GVRActivity {
    private static final String TAG = "PoliceVideo";
    private MainActivity main;
//    private GVRVideoSceneObjectPlayer<?> videoSceneObjectPlayer;
//    static final boolean USE_EXO_PLAYER = false;
    public Context context;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = new MainActivity();
        setMain(main, "gvr.xml");
        main.mContext=this;
    }
    @Override public boolean onTouchEvent(MotionEvent event) {
        main.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

//    public GVRVideoSceneObjectPlayer<MediaPlayer> makeMediaPlayer(String file) {
//        final MediaPlayer mediaPlayer = new MediaPlayer();
//        final Object player = videoSceneObjectPlayer.getPlayer();
//         AssetFileDescriptor afd2;
//        if(file!=""){
//            try { afd2 = context.getAssets().openFd(file);
//                android.util.Log.d(TAG, "Assets was found.");
//                mediaPlayer.setDataSource(afd2.getFileDescriptor(), afd2.getStartOffset(), afd2.getLength());
//                android.util.Log.d(TAG, "DataSource was set.");
//                afd2.close();
//                mediaPlayer.prepare();
//                mediaPlayer.start();
//            } catch (IOException e) {
//                e.printStackTrace(); finish();
//                android.util.Log.e(TAG, "Assets were not loaded. Stopping application!");}
//        }else { android.util.Log.e(TAG, "No video for playing. Stopping application!"); }
//        mediaPlayer.setLooping(false);android.util.Log.d(TAG, "starting player.");
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//        @Override public void onCompletion(MediaPlayer mp) { android.util.Log.d(TAG, "End video");
//            main.hideMovie();
//           }});
////        mediaPlayer.setOnPreparedListener (new MediaPlayer.OnPreparedListener() {
////        @Override public void onPrepared(MediaPlayer mp) {
////                android.util.Log.d(TAG, "prepared video");
////                mediaPlayer.pause();
////            }});
//
//        return GVRVideoSceneObject.makePlayerInstance(mediaPlayer);
//    }




    /// does not work!!!!!
//    private GVRVideoSceneObjectPlayer<SimpleExoPlayer> makeExoPlayer() {
//        Handler mainHandler = new Handler();
//        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
//        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
//        LoadControl loadControl = new DefaultLoadControl();
//        final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
//        String urimp4 = "asset:///video1.mp4";
//        Uri mp4VideoUri = Uri.parse(urimp4);
////yachts livestream m3m8 file:
//        //     Uri mp4VideoUri =Uri.parse("http://fluvod1.giniko.com/all-luxury-yachts/all-luxury-yachts/tracks-1,6/index.m3u8");
//        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
//        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayerText"), bandwidthMeterA);
//        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);
//        final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource); player.prepare(loopingSource);
//        return new GVRVideoSceneObjectPlayer<SimpleExoPlayer>() {
//            @Override public SimpleExoPlayer getPlayer() { android.util.Log.e(TAG,"START EXO"); return player;}
//            @Override public void setSurface(final Surface surface) {
//                player.addListener(new EventListener() {
//                    @Override public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                        // 4- STATE_ENDED 3 - STATE_READY 1 - STATE_IDLE 2 - STATE_BUFFERINg
//                        android.util.Log.d(TAG, "StateChanged="+String.valueOf(playbackState)); }
//                    @Override public void onPositionDiscontinuity(){android.util.Log.d(TAG, "PositionDiscontinuity");}
//                    @Override public void onLoadingChanged(boolean dl){android.util.Log.d(TAG, "LoadingChanged"); }
//                    @Override public void onTimelineChanged(Timeline tm, Object obj){android.util.Log.d(TAG, "TimelineChanged"); }
//                    @Override public void onPlayerError(ExoPlaybackException  err){ android.util.Log.d(TAG, "PlayerError"); player.stop();  }
//                    @Override public void onTracksChanged(TrackGroupArray trackGr, TrackSelectionArray TrackSel) { android.util.Log.d(TAG, "TracksChanged"); }
//                });
//                // !!!!!!!!does not work output video to shpera
//                // player.setPlayWhenReady(true);
//                //  player.sendMessage(videoRenderer, MediaCodecRenderer. .MSG_SET_SURFACE, surface);
//            }
//            @Override public void release() { android.util.Log.e(TAG,"2 STATE RELEASE"); player.release(); }
//            @Override public boolean canReleaseSurfaceImmediately() { android.util.Log.e(TAG,"2 STATE CAN RELEASE"); return false; }
//            @Override public void pause() {android.util.Log.e(TAG,"2 STATE PAUSE"); player.setPlayWhenReady(false);}
//            @Override public void start() {android.util.Log.e(TAG,"2 STATE START");player.setPlayWhenReady(true);}
//        };
//    }

}