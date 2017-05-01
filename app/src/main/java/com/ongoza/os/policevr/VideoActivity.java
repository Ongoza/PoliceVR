package com.ongoza.os.policevr;

import android.os.Bundle;
import android.view.MotionEvent;
import org.gearvrf.GVRActivity;


public class VideoActivity extends GVRActivity {
    private static final String TAG = "PoliceVideo";
    MainActivity main = new MainActivity();
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMain(main, "gvr.xml");
        main.mContext=this;
    }
    @Override public boolean onTouchEvent(MotionEvent event) {
//        android.util.Log.w(TAG, " motion event");
        main.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}