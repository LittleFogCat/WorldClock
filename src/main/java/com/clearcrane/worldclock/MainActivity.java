package com.clearcrane.worldclock;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.clearcrane.worldclock.base.BaseActivity;
import com.clearcrane.worldclock.utils.Auto;
import com.clearcrane.worldclock.utils.RandomUtil;

import java.io.IOException;
import java.util.Locale;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private WorldClockFragment mFragment;
    private Bundle mArgs = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Auto.init(this);
            RandomUtil.init(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        onIntent(getIntent());
        loadRootFragment();
    }

    private void loadRootFragment() {
        mFragment = WorldClockFragment.newInstance();

        getFragmentManager().beginTransaction()
                .add(R.id.container, mFragment)
                .commit();
        Log.d(TAG, "onCreate: " + onSize());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mFragment.onKeyDown(keyCode, event);
    }

    private void onIntent(Intent intent) {
        if (intent != null) {
            // 设置语言
            String languageCode = intent.getStringExtra("languageCode");
            Resources resources = getResources();
            Configuration configuration = resources.getConfiguration();
//            if (languageCode != null && (languageCode.contains("cn") ||
//                    languageCode.contains("CN") ||
//                    languageCode.contains("ZH") ||
//                    languageCode.equals(Locale.CHINA.getLanguage())// "zh"
//            )) {
//                resources.getConfiguration().locale = Locale.CHINA;
//            } else {
//                resources.getConfiguration().locale = Locale.US;
//            }
            if (languageCode != null &&
                    languageCode.equals(Locale.US.getLanguage())) {
                resources.getConfiguration().locale = Locale.US;
            } else {
                resources.getConfiguration().locale = Locale.CHINA;
            }
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());

            // 设置当前时区
            if (intent.hasExtra("timezone")) {
                int timezone = intent.getIntExtra("timezone", 8);
                mArgs.putInt("timezone", timezone);
            }

            // 设置当前时间
            if (intent.hasExtra("currentTimeMillis")) {
                long localTime = System.currentTimeMillis();
                long realTime = intent.getLongExtra("currentTimeMillis", localTime);

                long delta = realTime - localTime;
                if (Math.abs(delta) > 30 * 1000) {
                    mArgs.putLong("delta", delta);
                }
            }
        }
    }

}
