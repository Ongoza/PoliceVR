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
import org.gearvrf.GVRTexture;
import org.gearvrf.IPickEvents;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;
import org.gearvrf.scene_objects.GVRVideoSceneObject;
import org.gearvrf.scene_objects.GVRVideoSceneObject.GVRVideoType;
import org.gearvrf.scene_objects.GVRVideoSceneObjectPlayer;
import java.util.Timer;
import java.util.concurrent.Future;

import java.util.Timer;
import java.util.concurrent.Future;

public class MainActivity extends GVRMain {
    private static final String TAG = "PoliceVideo";
    public class PickHandler implements IPickEvents { public GVRSceneObject PickedObject = null;
        public void onEnter(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo){}
        public void onExit(GVRSceneObject sceneObj){ }
        public void onNoPick(GVRPicker picker){if(PickedObject != null){PickedObject.getRenderData().getMaterial().setDiffuseColor(1, 0, 0, 0.5f); }PickedObject = null;}
        public void onPick(GVRPicker picker) {  GVRPicker.GVRPickedObject picked = picker.getPicked()[0];PickedObject = picked.hitObject; PickedObject.getRenderData().getMaterial().setDiffuseColor(1, 0, 1, 0.5f);       }
        public void onInside(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo){ }
    }

    private GVRTextViewSceneObject mScoreBoard;
    private Integer mScore = 0;
    private float zloc = 0;
    private Timer mTimer;
    private GVRSceneObject menuItem;
    MainActivity(GVRVideoSceneObjectPlayer<?> player){mPlayer = player;}
    private final GVRVideoSceneObjectPlayer<?> mPlayer;
    private GVRScene scene = null;  private PickHandler mPickHandler; private GVRPicker mPicker;

    @Override public void onInit(GVRContext gvrContext){ GVRScene scene = gvrContext.getNextMainScene();
        scene.getMainCameraRig().getTransform().setPosition(0.0f, 0.0f, 0.0f);
        GVRSceneObject headTracker = new GVRSceneObject(gvrContext, gvrContext.createQuad(0.1f, 0.1f),
                gvrContext.loadTexture(new GVRAndroidResource(gvrContext, R.drawable.headtrackingpointer)));
        headTracker.getTransform().setPosition(0.0f, 0.0f, -1.0f);  headTracker.getRenderData().setDepthTest(false);
        headTracker.getRenderData().setRenderingOrder(100000); scene.getMainCameraRig().addChildObject(headTracker);
        // android.util.Log.w(TAG, "create scene");
        GVRSphereSceneObject sphere = new GVRSphereSceneObject(gvrContext, 72, 144, false);
//        sphere.getTransform().setScale(30.0f, 30.0f, 30.0f);
        GVRMesh mesh = sphere.getRenderData().getMesh();
        GVRVideoSceneObject video = new GVRVideoSceneObject(gvrContext, mesh, mPlayer, GVRVideoType.MONO);  video.setName("video");
        scene.addSceneObject(video); //android.util.Log.w(TAG, "apply video to scene");
//        GVRSceneObject balloon = makeBalloon(gvrContext); scene.addSceneObject(balloon);
//        GVRSceneObject
//        menuItem = makeItem(gvrContext,"Item1",1.0f); scene.addSceneObject(menuItem);
        GVRDirectLight sunLight = new GVRDirectLight(gvrContext);
        sunLight.setAmbientIntensity(0.4f, 0.4f, 0.4f, 1.0f);
        sunLight.setDiffuseIntensity(0.6f, 0.6f, 0.6f, 1.0f);
        mPickHandler = new PickHandler(); scene.getEventReceiver().addListener(mPickHandler);  mPicker = new GVRPicker(gvrContext, scene);
//        mScoreBoard = makeScoreboard(gvrContext);  headTracker.addChildObject(mScoreBoard);
//        mScoreBoard.getTransform().setPosition(0f, 0f, 0.05f); mScoreBoard.setBackgroundColor(Color.BLUE); mScore = 0; mScoreBoard.setText("Score: 0");
        //GVRSceneObject environment = makeEnvironment(gvrContext); scene.addSceneObject(environment);
    }
    @Override public void onStep() {
        FPSCounter.tick();
        zloc = zloc+0.01f;
        String nm = "Item1";
        GVRSceneObject menuItem1 = scene.getSceneObjectByName(nm);
//        bal.getTransform().setRotation(0f,0f,0f,zloc);
        android.util.Log.w(TAG, "z="+String.valueOf(menuItem1.getName()));
//        float newZ = (float) zloc;
//        mScoreBoard.getTransform().setPosition(-0.2f, 0.2f, newZ);
    }

    GVRSceneObject makeItem(GVRContext context, String name, float zPos) {
        GVRSceneObject item = new GVRCubeSceneObject(context, true);
        GVRRenderData rdata1 = item.getRenderData();
        GVRMaterial mtl = new GVRMaterial(context);
        mtl.setDiffuseColor(0.2f, 0.0f, 0.2f, 0.5f);
        item.setName(name);
        rdata1.setShaderTemplate(GVRPhongShader.class);
        rdata1.setAlphaBlend(true);
        rdata1.setMaterial(mtl);
        rdata1.setRenderingOrder(GVRRenderData.GVRRenderingOrder.OVERLAY);
        item.getTransform().setPositionZ(zPos);
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
                    mPickHandler.PickedObject.getRenderData().getMaterial().setDiffuseColor(0, 0, 1, 1);
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
    private void onHit(GVRSceneObject sceneObj) { mScoreBoard.setText("Score: " + mScore.toString()); }
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
