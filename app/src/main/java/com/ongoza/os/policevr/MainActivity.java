package com.ongoza.os.policevr;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.graphics.Color;
import android.view.MotionEvent;
import org.gearvrf.GVRAndroidResource;
import android.media.MediaPlayer;

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
import org.gearvrf.scene_objects.GVRVideoSceneObject.GVRVideoType;
import org.gearvrf.scene_objects.GVRVideoSceneObjectPlayer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.Future;

import java.util.Timer;
import java.util.concurrent.Future;

public class MainActivity extends GVRMain {
    private static final String TAG = "PoliceVideo";
    private PickHandler mPickHandler;
    private GVRPicker mPicker;
    private GVRTextViewSceneObject mScoreBoard;
    private Integer mScore = 0;
    private float zloc = 0;
    private Timer mTimer;
    public Context mContext;
    private MovieManager mMovieManager = null;
    private ArrayList<GVRSceneObject> mItems = new ArrayList<GVRSceneObject>();
    private GVRScene scene = null;
    public boolean MovieStop = false;
    private GVRContext gContext = null;
    private GVRVideoSceneObject mVideo = null;
    public class PickHandler implements IPickEvents { public GVRSceneObject PickedObject = null;
        public void onEnter(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo){}
        public void onExit(GVRSceneObject sceneObj){if(PickedObject != null){
                PickedObject.getRenderData().getMaterial().setDiffuseColor(0.424f, 0.843f, 0.961f, 1f); PickedObject = null;}}
        public void onNoPick(GVRPicker picker){if(PickedObject != null){
            PickedObject.getRenderData().getMaterial().setDiffuseColor(0.424f, 0.843f, 0.961f, 1f); }PickedObject = null;}
        public void onPick(GVRPicker picker){ GVRPicker.GVRPickedObject picked = picker.getPicked()[0];
            PickedObject = picked.hitObject;//  android.util.Log.w(TAG, " pick obj="+String.valueOf(PickedObject.getName()));
            PickedObject.getRenderData().getMaterial().setDiffuseColor(1, 0, 0, 0.5f);}
        public void onInside(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo){ } }
    @Override public void onInit(GVRContext gvrContext){scene = gvrContext.getNextMainScene(); gContext = gvrContext;
        scene.getEventReceiver().addListener(new PickHandler());
        scene.getMainCameraRig().getOwnerObject().attachComponent(new GVRPicker(gvrContext, scene));
        scene.getMainCameraRig().getTransform().setPosition(0.0f, 0.0f, 0.0f);
        GVRSceneObject headTracker = new GVRSceneObject(gvrContext, gvrContext.createQuad(0.1f, 0.1f),
                gvrContext.loadTexture(new GVRAndroidResource(gvrContext, R.drawable.headtrackingpointer)));
        headTracker.getTransform().setPosition(0.0f, 0.0f, -1.0f);  headTracker.getRenderData().setDepthTest(false);
        headTracker.getRenderData().setRenderingOrder(100000); scene.getMainCameraRig().addChildObject(headTracker);
        GVRSceneObject environment = makeEnvironment(gvrContext); scene.addSceneObject(environment);
        JSONArray videoList = openPlayList();
        for(int i=0; i<videoList.length(); i++) {
            try {String videofile = videoList.getString(i);
                GVRSceneObject menuItem = makeItem(gvrContext,videofile,-4.0f,i*2f);
                scene.addSceneObject(menuItem); mItems.add(menuItem);
                android.util.Log.d(TAG, videofile);
            } catch (JSONException e) {android.util.Log.e(TAG, "error parse filename");}
        }

        GVRSceneObject menuItem2 = makeItem(gvrContext,"quadTest",-4.0f,-2f);
        scene.addSceneObject(menuItem2);
        GVRDirectLight sunLight = new GVRDirectLight(gvrContext);
        sunLight.setAmbientIntensity(0.4f, 0.4f, 0.4f, 1.0f);
        sunLight.setDiffuseIntensity(0.6f, 0.6f, 0.6f, 1.0f);
        mPickHandler = new PickHandler();
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
            mMovieManager = null;
            if(mVideo!=null){
                android.util.Log.w(TAG, "movieStop mVideo");
                scene.removeSceneObject(mVideo);}
            MovieStop = false;
        }
    }

    void startMovie(String fileName){
        android.util.Log.w(TAG, "fileName="+fileName);
        if(fileName!=""){
            mMovieManager = new MovieManager(gContext,fileName);
            GVRSphereSceneObject sphere = new GVRSphereSceneObject(gContext, 72, 144, false);
            GVRMesh mesh = sphere.getRenderData().getMesh();
            MediaPlayer mPlayer= mMovieManager.getMediaPlayer();
            mVideo = new GVRVideoSceneObject(gContext, mesh, mPlayer, GVRVideoType.MONO);
            mVideo.setName("Cinema");
            scene.addSceneObject(mVideo);
            android.util.Log.w(TAG, "apply video to scene");
        }else { android.util.Log.e(TAG, "No video for playing. Stopping application!"); }
    }

    public void hideMovie(){
        android.util.Log.e(TAG, "Stopping application!"+mVideo.getName());
        MovieStop=true;
        mMovieManager = null;
//        scene.removeSceneObject(mVideo);
    }

    JSONArray openPlayList(){
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

    GVRSceneObject makeItem2(GVRContext context, String name, float zPos, float xPos) {
        android.util.Log.w(TAG, "create item="+name+"="+String.valueOf(zPos)+"="+String.valueOf(zPos));
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

    GVRSceneObject makeItem(GVRContext context, String name, float zPos, float xPos) {
        android.util.Log.w(TAG, "create item="+name+"="+String.valueOf(zPos)+"="+String.valueOf(zPos));
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
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mPickHandler.PickedObject != null) {
                   // mPickHandler.PickedObject.getRenderData().getMaterial().setDiffuseColor(0, 0, 1, 1);
                    android.util.Log.w(TAG, "select video="+String.valueOf(mPickHandler.PickedObject.getName()));
                    startMovie(mPickHandler.PickedObject.getName());
//                    scene.removeSceneObject(mPickHandler.PickedObject);
                }
                break;
            default:
                android.util.Log.w(TAG, "switch break");
                break;
        }
    }
    GVRTextViewSceneObject makeScoreboard(GVRContext ctx){
        GVRTextViewSceneObject scoreBoard = new GVRTextViewSceneObject(ctx, 2, 1, "Score: 0");
        GVRRenderData rdata = scoreBoard.getRenderData();
        scoreBoard.getTransform().setPosition(-1, 1, -10);
        scoreBoard.setTextColor(Color.YELLOW);
        scoreBoard.setBackgroundColor(Color.BLUE);
        return scoreBoard;
    }
    private void onHit(GVRSceneObject sceneObj) {
        mScoreBoard.setText("Score: " + mScore.toString());
    }
    GVRSceneObject makeEnvironment(GVRContext context) {
        android.util.Log.w(TAG, "start create room");
        Future<GVRTexture> tex = context.loadFutureCubemapTexture(new GVRAndroidResource(context, R.raw.hall_police));
        GVRMaterial material = new GVRMaterial(context, GVRMaterial.GVRShaderType.Cubemap.ID);
        android.util.Log.w(TAG, "create material");
        material.setMainTexture(tex);
        GVRSphereSceneObject environment = new GVRSphereSceneObject(context, 18, 36, false, material, 4, 4);
        environment.getTransform().setScale(20.0f, 20.0f, 20.0f);
        GVRDirectLight sunLight = new GVRDirectLight(context);
        sunLight.setAmbientIntensity(0.4f, 0.4f, 0.4f, 1.0f);
        sunLight.setDiffuseIntensity(0.6f, 0.6f, 0.6f, 1.0f);
        environment.attachComponent(sunLight);
        return environment;
    }
}
