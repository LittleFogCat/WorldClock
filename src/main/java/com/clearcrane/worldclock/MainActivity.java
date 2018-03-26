package com.clearcrane.worldclock;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private int[] screenSize = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction()
                .add(R.id.container, new WorldClockFragment())
                .commit();
        Log.d(TAG, "onCreate: " + onSize());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: " + onSize());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + onSize());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged: " + onSize());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v(TAG, "onKeyDown: " + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.v(TAG, "dispatchKeyEvent: " + event.getAction() + event.getKeyCode());
        return super.dispatchKeyEvent(event);
    }

    public String onSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenSize[0] = metrics.widthPixels;
        screenSize[1] = metrics.heightPixels;
        return screenSize[0] + ", " + screenSize[1];
    }
}
