package com.clearcrane.worldclock.base;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by jjy on 2018/3/29.
 */

public class BaseActivity extends Activity {
    private static final String TAG = "BaseActivity";
    private int[] screenSize = new int[2];

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
