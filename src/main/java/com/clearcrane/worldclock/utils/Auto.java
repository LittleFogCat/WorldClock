package com.clearcrane.worldclock.utils;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

/**
 * Created by jjy on 2018/3/26.
 * <p>
 * 根据屏幕尺寸和设计尺寸来进行实际尺寸计算的工具类。
 */

public class Auto {
    private static int desingW = 1920;
    private static int desineH = 1080;
    private static int actualW = 1920;
    private static int actualH = 1080;

    private static float scale = 1;
    private static float scaleX = 1;
    private static float scaleY = 1;

    public static void init(Activity activity) throws PackageManager.NameNotFoundException {
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        defaultDisplay.getMetrics(metrics);
        if (metrics.widthPixels != 0 && metrics.heightPixels != 0) {
            actualW = metrics.widthPixels;
            actualH = metrics.heightPixels;
        }


        ApplicationInfo info = activity.getPackageManager()
                .getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
        int dw = info.metaData.getInt("design_width");
        int dh = info.metaData.getInt("design_height");
        if (dw != 0 && dh != 0) {
            desingW = dw;
            desineH = dh;
            scale = (actualW + 0f) / dw;
        }


        Log.d("Auto", "init: " + dw + ", " + dh);
    }

    public static float size(float originSize) {
        return originSize * scale;
    }
}
