package com.ongoza.os.policevr;

import android.graphics.Color;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.graphics.Color;
import android.view.MotionEvent;
import org.gearvrf.GVRAndroidResource;
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

import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.Future;

import java.util.Timer;
import java.util.concurrent.Future;

public class MainActivity extends GVRMain {
    private static final String TAG = "PoliceVideo";
    private PickHandler mPickHandler;
    private GVRPicker mPicker;
    public class PickHandler implements IPickEvents { public GVRSceneObject PickedObject = null;
        public void onEnter(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo){}
        public void onExit(GVRSceneObject sceneObj){
            if(PickedObject != null){
                PickedObject.getRenderData().getMaterial().setDiffuseColor(0.424f, 0.843f, 0.961f, 1f); PickedObject = null;
            }
        }
        public void onNoPick(GVRPicker picker){if(PickedObject != null){
//            android.util.Log.w(TAG, " no pick obj");
            PickedObject.getRenderData().getMaterial().setDiffuseColor(0.424f, 0.843f, 0.961f, 1f); }PickedObject = null;}
        public void onPick(GVRPicker picker){
            GVRPicker.GVRPickedObject picked = picker.getPicked()[0];
            PickedObject = picked.hitObject;
//            android.util.Log.w(TAG, " pick obj="+String.valueOf(PickedObject.getName()));
            PickedObject.getRenderData().getMaterial().setDiffuseColor(1, 0, 0, 0.5f);}
        public void onInside(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo){
//            android.util.Log.w(TAG, " inside obj"+String.valueOf(sceneObj.getName()));
        }
    }

    private GVRTextViewSceneObject mScoreBoard;
    private Integer mScore = 0;
    private float zloc = 0;
    private Timer mTimer;
    private ArrayList<GVRSceneObject> mItems = new ArrayList<GVRSceneObject>();
    MainActivity(GVRVideoSceneObjectPlayer<?> player){mPlayer = player;}
    private final GVRVideoSceneObjectPlayer<?> mPlayer;
    private GVRScene scene = null;
    private GVRVideoSceneObject mVideo = null;

    @Override public void onInit(GVRContext gvrContext){
        GVRScene scene = gvrContext.getNextMainScene();
        scene.getEventReceiver().addListener(new PickHandler());
        scene.getMainCameraRig().getOwnerObject().attachComponent(new GVRPicker(gvrContext, scene));
        scene.getMainCameraRig().getTransform().setPosition(0.0f, 0.0f, 0.0f);
        GVRSceneObject headTracker = new GVRSceneObject(gvrContext, gvrContext.createQuad(0.1f, 0.1f),
                gvrContext.loadTexture(new GVRAndroidResource(gvrContext, R.drawable.headtrackingpointer)));
        headTracker.getTransform().setPosition(0.0f, 0.0f, -1.0f);  headTracker.getRenderData().setDepthTest(false);
        headTracker.getRenderData().setRenderingOrder(100000); scene.getMainCameraRig().addChildObject(headTracker);
        // android.util.Log.w(TAG, "create scene");
        GVRSphereSceneObject sphere = new GVRSphereSceneObject(gvrContext, 72, 144, false);
//        sphere.getTransform().setScale(30.0f, 30.0f, 30.0f);
        GVRMesh mesh = sphere.getRenderData().getMesh();
        mVideo = new GVRVideoSceneObject(gvrContext, mesh, mPlayer, GVRVideoType.MONO);
        mVideo.setName("video");
        mVideo.getTransform().setPosition(100f,100f,100f);
        scene.addSceneObject(mVideo); //android.util.Log.w(TAG, "apply video to scene");

//        GVRSceneObject balloon = makeBalloon(gvrContext); scene.addSceneObject(balloon);
        GVRSceneObject menuItem1 = makeItem(gvrContext,"Item1",-4.0f,0f); scene.addSceneObject(menuItem1);
        mItems.add(menuItem1);
        GVRSceneObject menuItem2 = makeItem(gvrContext,"Item2",-4.0f,2f); scene.addSceneObject(menuItem2);
        mItems.add(menuItem2);
        GVRDirectLight sunLight = new GVRDirectLight(gvrContext);
        sunLight.setAmbientIntensity(0.4f, 0.4f, 0.4f, 1.0f);
        sunLight.setDiffuseIntensity(0.6f, 0.6f, 0.6f, 1.0f);
        mPickHandler = new PickHandler();
        scene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(gvrContext, scene);

//        mPickHandler = new PickHandler();
//        scene.getEventReceiver().addListener(mPickHandler);
//        mPicker = new GVRPicker(gvrContext, scene);
//        mScoreBoard = makeScoreboard(gvrContext);  headTracker.addChildObject(mScoreBoard);
//        mScoreBoard.getTransform().setPosition(0f, 0f, 0.05f); mScoreBoard.setBackgroundColor(Color.BLUE); mScore = 0; mScoreBoard.setText("Score: 0");
        GVRSceneObject environment = makeEnvironment(gvrContext); scene.addSceneObject(environment);
    }
    @Override public void onStep() {
        FPSCounter.tick();
        zloc = zloc+0.01f;
        String nm = "Item1";
//        GVRSceneObject menuItem1 = scene.getSceneObjectByName(nm);
        GVRSceneObject menuItem1 = mItems.get(0);
        menuItem1.getTransform().rotateByAxis(1f,0f,1f,0f);
     //  android.util.Log.w(TAG, "z="+String.valueOf(zloc));
//        float newZ = (float) zloc;
//        mItems.get(0).getTransform().rotate(0f,0f,0f,zloc);
//        mScoreBoard.getTransform().setPosition(-0.2f, 0.2f, newZ);
    }

    void startMovie(){
        android.util.Log.w(TAG, "zzzzzz=");
        mVideo.getTransform().setPosition(0f,0f,0f);
        mPlayer.start();
       // mPlayer.prepare();
    }

    public void hideMovie(){
        mVideo.getTransform().setPosition(100f,100f,100f);
    }

    GVRSceneObject makeItem(GVRContext context, String name, float zPos, float xPos) {
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

    GVRSceneObject makeBalloon(GVRContext context) {
        GVRSceneObject sphere = new GVRSphereSceneObject(context, true);
        GVRRenderData rdata1 = sphere.getRenderData();
        GVRMaterial mtl = new GVRMaterial(context);
        mtl.setDiffuseColor(0.2f, 0.0f, 0.2f, 0.5f);
        sphere.setName("balloon");
        rdata1.setShaderTemplate(GVRPhongShader.class);
        rdata1.setAlphaBlend(true);
        rdata1.setMaterial(mtl);
        rdata1.setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        sphere.getTransform().setPositionZ(1.0f);
        // sphere.getTransform().setScale(0.5f, 0.5f, 0.5f);
        return sphere;
    }

    public void onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mPickHandler.PickedObject != null) {
                   // mPickHandler.PickedObject.getRenderData().getMaterial().setDiffuseColor(0, 0, 1, 1);
                    android.util.Log.w(TAG, "motion video="+String.valueOf(mVideo.getName()));
                    startMovie();
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
