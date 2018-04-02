package com.clearcrane.worldclock;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.clearcrane.worldclock.base.BaseActivity;
import com.clearcrane.worldclock.utils.Auto;
import com.clearcrane.worldclock.utils.RandomUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends BaseActivity implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "MainActivity";

    private WorldClockFragment mFragment;
    private Bundle mArgs = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread.setDefaultUncaughtExceptionHandler(this);

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

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        super.onBackPressed();
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            File file = getFilesDir();
            File logFileDir = new File(file, "log");
            logFileDir.mkdirs();
            String fileName = "errorlog" + new SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(new Date());
            File logFile = new File(logFileDir, fileName);
            logFile.createNewFile();
            FileWriter fw = new FileWriter(logFile);
            PrintWriter pw = new PrintWriter(fw);
            e.printStackTrace(pw);

            while (e.getCause() != null) {
                e.getCause().printStackTrace(pw);
                e = e.getCause();
            }

            pw.flush();
            pw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
