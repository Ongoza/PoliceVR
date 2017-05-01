package com.ongoza.os.policevr;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.view.MotionEvent;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRBoxCollider;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRPhongShader;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRSphereCollider;
import org.gearvrf.GVRTexture;
import org.gearvrf.IPickEvents;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;
import org.gearvrf.scene_objects.GVRVideoSceneObject;
import android.media.MediaPlayer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.gearvrf.scene_objects.GVRVideoSceneObject.GVRVideoType;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.Future;
import org.gearvrf.utility.Log;


public class MainActivity extends GVRMain {
    private static final String TAG = "PoliceVideo";
    private PickHandler mPickHandler = new PickHandler();
    private GVRPicker mPicker;
    private GVRTextViewSceneObject mScoreBoard;
    private Integer mScore = 0;
    private float zloc = 0;
    private Timer mTimer;
    private GVRTextViewSceneObject mMenuItem;
    public Context mContext;
    private  boolean MovieStoped = true;
    private MovieManager mMovieManager = null;
    private static String menuVideoId = "_videoID_";
    private static String menuItemId = "_itemID_";
    private ArrayList<GVRSceneObject> mItems = new ArrayList<GVRSceneObject>();
    private GVRScene scene = null;
    public boolean MovieStop = false;
    private GVRContext gContext = null;
    private GVRVideoSceneObject mVideo = null;

    public class PickHandler implements IPickEvents { public GVRSceneObject PickedObject = null;
        public void onEnter(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo){}
        public void onExit(GVRSceneObject sceneObj){if(PickedObject != null){
            Log.w(TAG, " pick exit="+PickedObject.getName());
            NonSelect(PickedObject);
            PickedObject = null;}}
        public void onNoPick(GVRPicker picker){if(PickedObject != null){
            Log.w(TAG, " noPick exit="+PickedObject.getName());
            NonSelect(PickedObject);
//            PickedObject.getRenderData().getMaterial().setDiffuseColor(0.424f, 0.843f, 0.961f, 0.4f);
              PickedObject = null;}}
        public void onPick(GVRPicker picker){ GVRPicker.GVRPickedObject picked = picker.getPicked()[0];
            String nameNew = picked.hitObject.getName();
            if(PickedObject != null){String namePic = PickedObject.getName();
                Log.w(TAG, " pick obj="+namePic+"new name="+nameNew);
                if(!namePic.equals(nameNew)){NonSelect(PickedObject);}}
            Log.w(TAG, " pick new="+nameNew);
            PickedObject = picked.hitObject; int d2 = nameNew.indexOf(menuItemId);
            if(d2!=-1) {PickedObject.getTransform().setPositionY(-0.6f);
            }else{PickedObject.getTransform().setPositionY(0.2f);
//                PickedObject.getRenderData().getMaterial().setDiffuseColor(0.424f, 0.843f, 0.961f, 0.9f);
            }}
        public void onInside(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo){ }
        private void NonSelect(GVRSceneObject Picked){
            String name = Picked.getName(); Log.w(TAG, " pick 2 obj="+name);
            int d2 = name.indexOf(menuItemId);
            if(d2!=-1) {Picked.getTransform().setPositionY(-0.8f);
            }else{Picked.getTransform().setPositionY(0f);
//                PickedObject.getRenderData().getMaterial().setDiffuseColor(0.424f, 0.843f, 0.961f, 0.9f);
            }
        }
    }

    @Override public void onInit(GVRContext gvrContext){scene = gvrContext.getNextMainScene(); gContext = gvrContext;
        scene.getMainCameraRig().getOwnerObject().attachComponent(new GVRPicker(gvrContext, scene));
        scene.getMainCameraRig().getTransform().setPosition(0.0f, 0.0f, 0.0f);
        GVRSceneObject headTracker = new GVRSceneObject(gvrContext, gvrContext.createQuad(0.1f, 0.1f),
                gvrContext.loadTexture(new GVRAndroidResource(gvrContext, R.drawable.headtrackingpointer)));
        headTracker.getTransform().setPosition(0.0f, 0.0f, -1.0f);  headTracker.getRenderData().setDepthTest(false);
        headTracker.getRenderData().setRenderingOrder(100000); scene.getMainCameraRig().addChildObject(headTracker);
        GVRSceneObject environment = makeEnvironment(gvrContext); scene.addSceneObject(environment);
        JSONArray videoList = openPlayList();
        for(int i=0; i<videoList.length(); i++) {
            try {String videoName = menuVideoId + videoList.getString(i);
                GVRSceneObject menuItem = makeItemVideo(gvrContext, videoName, i * 2f, 0f, -4f);
//                GVRSceneObject menuItem = makeItem(gvrContext,videoName,-4.0f,i*2f);
                scene.addSceneObject(menuItem); mItems.add(menuItem);
                Log.d(TAG, videoName);
            } catch (JSONException e) {android.util.Log.e(TAG, "error parse filename");}
        }
//        GVRSceneObject menuItem2 = makeItem(gvrContext,"quadTest",-4.0f,-2f); scene.addSceneObject(menuItem2);
        GVRDirectLight sunLight = new GVRDirectLight(gvrContext);
        sunLight.setAmbientIntensity(0.4f, 0.4f, 0.4f, 1.0f);
        sunLight.setDiffuseIntensity(0.6f, 0.6f, 0.6f, 1.0f);
        mMenuItem = makeItemMenuTxt(gvrContext, "Exit", 0f, -0.8f, 0f); scene.addSceneObject(mMenuItem);
        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(gvrContext, scene);
    }

    @Override public void onStep() { FPSCounter.tick();
        zloc = zloc+0.01f;//    String nm = "Item1";
//        GVRSceneObject menuItem1 = scene.getSceneObjectByName(nm);
        GVRSceneObject menuItem1 = mItems.get(0);
        menuItem1.getTransform().rotateByAxis(1f,0f,1f,0f);
//        android.util.Log.w(TAG, "movieStop="+String.valueOf(MovieStop));
        if(MovieStop){
            android.util.Log.w(TAG, "movieStop");
//            mMovieManager = null;
            if(mVideo!=null){
                android.util.Log.w(TAG, "movieStop mVideo");
                scene.removeSceneObject(mVideo);}
            MovieStop = false;
        }
    }

    private void stopMovie(){
        MovieStoped = true;
        mMovieManager.getMediaPlayer().stop();
        mMenuItem.getTransform().setPositionX(0f);
        scene.getMainCameraRig().getTransform().setPositionX(0f);}

    private void startMovie(String fileName){
        Log.w(TAG, "fileName="+fileName);
        if(!fileName.equals("")){
            mMovieManager = null;
            mVideo = null;
            mMovieManager = new MovieManager(gContext,fileName);
            GVRSphereSceneObject sphere = new GVRSphereSceneObject(gContext, 72, 144, false);
            GVRMesh mesh = sphere.getRenderData().getMesh();
            MediaPlayer mPlayer= mMovieManager.getMediaPlayer();
            mVideo = new GVRVideoSceneObject(gContext, mesh, mPlayer, GVRVideoType.MONO);
            mVideo.getTransform().setPositionX(1000f);
            mVideo.setName("Cinema");
            MovieStoped = false;
            scene.addSceneObject(mVideo);
            scene.getMainCameraRig().getTransform().setPositionX(1000f);
            mMenuItem.getTransform().setPositionX(1000f);
            Log.w(TAG, "apply video to scene");
        }else { android.util.Log.e(TAG, "No video for playing. Stopping application!"); }
    }

    private JSONArray openPlayList(){
        final AssetFileDescriptor afd2; //AssetManager assetManager =  mContext.getAssets();
        String jsonFile=""; JSONObject jVideoList;  JSONArray videoList = null;
        try {//   getResources().openRawResource(R.raw.test)
            InputStream is = mContext.getAssets().open("media.exolist.json");
            int size = is.available(); byte[] buffer = new byte[size]; is.read(buffer); is.close(); jsonFile = new String(buffer, "UTF-8");
//            android.util.Log.d(TAG, jsonFile);
        } catch (IOException e) { android.util.Log.e(TAG,"can't open video list file"); e.printStackTrace(); }
        try{jVideoList = new JSONObject(jsonFile); videoList = jVideoList.getJSONArray("Start");
        }catch (JSONException e){android.util.Log.e(TAG,"error parse list!!!!"); e.printStackTrace(); }
        return videoList;
    }

    private GVRSceneObject makeItem2(GVRContext context, String name, float zPos, float xPos) {
        Log.w(TAG, "create item="+name+"="+String.valueOf(zPos)+"="+String.valueOf(zPos));
        GVRSceneObject item = new GVRSceneObject(context, 4.0f, 2.0f);
        GVRRenderData rdata1 = item.getRenderData();
        GVRMaterial material = new GVRMaterial(context);
        material.setDiffuseColor(0.424f, 0.843f, 0.961f, 1f);
        material.setAmbientColor(1.0f, 1.0f, 1.0f, 1.0f);
        material.setSpecularColor(1.0f, 1.0f, 1.0f, 1.0f);
        material.setSpecularExponent(128.0f);
        GVRBoxCollider collider = new GVRBoxCollider(context);
//        collider.setRadius(1.0f);
        item.attachComponent(collider);
        item.setName(name);
        rdata1.setShaderTemplate(GVRPhongShader.class);
//        rdata1.setAlphaBlend(true);
        rdata1.setMaterial(material);
        rdata1.setRenderingOrder(GVRRenderData.GVRRenderingOrder.OVERLAY);
        item.getTransform().setPosition(xPos,0.0f,zPos);
        return item;
    }

    private GVRSceneObject makeItem(GVRContext context, String name, float zPos, float xPos) {
        Log.w(TAG, "create item="+name+"="+String.valueOf(zPos)+"="+String.valueOf(zPos));
        GVRSceneObject item = new GVRCubeSceneObject(context,true);
        GVRRenderData rdata1 = item.getRenderData();
        GVRMaterial material = new GVRMaterial(context);
        material.setDiffuseColor(0.424f, 0.843f, 0.961f, 1f);
        material.setAmbientColor(1.0f, 1.0f, 1.0f, 1.0f);
        material.setSpecularColor(1.0f, 1.0f, 1.0f, 1.0f);
        material.setSpecularExponent(128.0f);
        GVRSphereCollider collider = new GVRSphereCollider(context);
        collider.setRadius(1.0f);
        item.attachComponent(collider);
        item.setName(name);
        rdata1.setShaderTemplate(GVRPhongShader.class);
//        rdata1.setAlphaBlend(true);
        rdata1.setMaterial(material);
        rdata1.setRenderingOrder(GVRRenderData.GVRRenderingOrder.OVERLAY);
        item.getTransform().setPosition(xPos,0.0f,zPos);
        return item;
    }

    public void onTouchEvent(MotionEvent event) {
//        Log.w(TAG, "select event obj="+String.valueOf(event.getAction())+"=mpt="+String.valueOf(MotionEvent.ACTION_MASK));
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                if(mPickHandler.PickedObject != null){
//                    mPickHandler.PickedObject.getRenderData().getMaterial().setDiffuseColor(0, 0, 1, 1);
                    String nm = mPickHandler.PickedObject.getName();
                    Log.w(TAG, "select item="+nm+String.valueOf(MovieStoped));
                    if(MovieStoped){int d = nm.indexOf(menuVideoId);
                        android.util.Log.w(TAG, "select video="+String.valueOf(d));
                        if(d!=-1){ int len =  menuVideoId.length();
                            String nm2 = nm.substring(len); Log.w(TAG, "select video="+nm2); startMovie(nm2);}}
                    int d2 = nm.indexOf(menuItemId);
                    if(d2!=-1) {int len2 =  menuItemId.length();String nm3 = nm.substring(len2);
                        Log.w(TAG, "select menu item =" + nm3);
                        menuAction(nm3);}}; break;
            default: Log.w(TAG, "switch break"); break;}
    }

    private GVRTextViewSceneObject makeItemMenuTxt(GVRContext context, String name, float xPos, float yPos, float zPos){
        GVRTextViewSceneObject item = new GVRTextViewSceneObject(context, 1, 0.5f, name);
        GVRSphereCollider collider = new GVRSphereCollider(context);
        collider.setRadius(0.3f);
        item.attachComponent(collider);
        GVRRenderData rdata = item.getRenderData();
        item.setName(menuItemId+name);
        item.getTransform().setPosition(xPos, yPos, zPos);
        item.getTransform().rotateByAxis(270f,1f,0f,0f);
        item.setTextColor(Color.argb(200,200,200,200));
        item.setBackgroundColor(Color.argb(150,0,200,50));
       // item.getRenderData().getMaterial().setOpacity(0.5f);
        return item;
    }

    private void menuAction (String nm){
        Log.w(TAG, "menuAction");
        switch(nm){
            case "Exit":
//                Log.w(TAG, "menuAction Exit"+String.valueOf(MovieStoped));
                if(MovieStoped){
                    Log.w(TAG, "menuAction system exit");
                    System.exit(1);
                }else{
                    Log.w(TAG, "menuAction stop movie");
                    stopMovie();} break;
            default: break;
        }}

    private GVRTextViewSceneObject makeScoreboard(GVRContext ctx){
        GVRTextViewSceneObject scoreBoard = new GVRTextViewSceneObject(ctx, 2, 1, "Score: 0");
        GVRRenderData rdata = scoreBoard.getRenderData();
        scoreBoard.getTransform().setPosition(-1, 1, -10);
        scoreBoard.setTextColor(Color.YELLOW);
        scoreBoard.setBackgroundColor(Color.BLUE);
        return scoreBoard;
    }

    private GVRSceneObject makeEnvironment(GVRContext context) {
        android.util.Log.w(TAG, "start create room");
        Future<GVRTexture> tex = context.loadFutureCubemapTexture(new GVRAndroidResource(context, R.raw.hall_police));
        GVRMaterial material = new GVRMaterial(context, GVRMaterial.GVRShaderType.Cubemap.ID);
        Log.w(TAG, "create material");
        material.setMainTexture(tex);
        GVRSphereSceneObject environment = new GVRSphereSceneObject(context, 18, 36, false, material, 4, 4);
        environment.getTransform().setScale(20.0f, 20.0f, 20.0f);
        GVRDirectLight sunLight = new GVRDirectLight(context);
        sunLight.setAmbientIntensity(0.4f, 0.4f, 0.4f, 1.0f);
        sunLight.setDiffuseIntensity(0.6f, 0.6f, 0.6f, 1.0f);
        environment.attachComponent(sunLight);
        return environment;
    }

    private GVRSceneObject makeItemVideo(GVRContext context, String name, float xPos, float yPos,float zPos){
        android.util.Log.w(TAG, "create item="+name+"="+String.valueOf(zPos)+"="+String.valueOf(zPos));
        GVRSceneObject item = new GVRCubeSceneObject(context,true);
        GVRRenderData rdata1 = item.getRenderData();
        GVRMaterial material = new GVRMaterial(context);
        material.setDiffuseColor(0.424f, 0.843f, 0.961f, 0.6f);
        material.setAmbientColor(1.0f, 1.0f, 1.0f, 1f);
//        material.setSpecularColor(1.0f, 1.0f, 1.0f, 1.0f);
//        material.setSpecularExponent(128.0f);
        GVRSphereCollider collider = new GVRSphereCollider(context);
        collider.setRadius(0.4f);
        item.attachComponent(collider);
        item.setName(name);
        rdata1.setShaderTemplate(GVRPhongShader.class);
//        rdata1.setAlphaBlend(true);
        rdata1.setMaterial(material);
        rdata1.setRenderingOrder(GVRRenderData.GVRRenderingOrder.OVERLAY);
        item.getTransform().setPosition(xPos,yPos,zPos);
        return item;
    }

    private  class MovieManager { private static final String TAG = "PoliceVideo";
        private MediaPlayer mMediaPlayer = null;
        private  MovieManager (GVRContext context, String fileName){
            mMediaPlayer = new MediaPlayer(); mMediaPlayer.setLooping(false);android.util.Log.d(TAG, "starting player.");
            try {AssetFileDescriptor fileDescriptor = context.getContext().getAssets().openFd(fileName);
                mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength()); fileDescriptor.close();
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { @Override public void onCompletion(MediaPlayer mp) {
                        android.util.Log.d(TAG, "End video");
                    mp.stop(); mp.release(); stopMovie();}});
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { @Override public void onPrepared(MediaPlayer mp) { mMediaPlayer.start();}});
                mMediaPlayer.prepare();
            }catch(IOException e){Log.e(TAG, "Failed to open the media file"); e.printStackTrace();  mMediaPlayer = null;
            }catch(IllegalStateException e){ Log.e(TAG, "Failed to prepare media player"); e.printStackTrace();  mMediaPlayer = null;
            }
        }
        private  MediaPlayer getMediaPlayer() { return mMediaPlayer; }
    }
}