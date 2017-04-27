package com.ongoza.os.policevr;

import android.util.Log;

/**
 * Created by os on 25.04.2017.
 */

public class FPSCounter {
    private static int frames = 0;
    private static final String TAG = "PoliceVideo";
    private static long startTimeMillis = 0;
    private static final long interval = 10000;

    public static void tick() {
        ++frames;
        if (System.currentTimeMillis() - startTimeMillis >= interval) {
            Log.v(TAG, "FPS : " + frames / (interval / 1000.0f));
            frames = 0;
            startTimeMillis = System.currentTimeMillis();
        }
    }
}
