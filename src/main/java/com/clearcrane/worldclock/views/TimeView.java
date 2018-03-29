package com.clearcrane.worldclock.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.clearcrane.worldclock.R;
import com.clearcrane.worldclock.utils.Auto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jjy on 2018/3/29.
 */
@SuppressLint("SimpleDateFormat")
public class TimeView extends FrameLayout {
    private static final String TAG = "TimeView";
    private long mCurrentTime;
    private long mDeltaTime;
    private TextSwitcher mTenHour, mHour, mTenMin, mMin, mTenSec, mSec;
    private float mTextSize = 96;
    private ViewSwitcher.ViewFactory mFactory = () -> {
        TextView textView = new TextView(getContext());
        textView.setShadowLayer(10, 4, 4, Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, Auto.size(mTextSize));
        textView.setTextColor(Color.WHITE);
        textView.setText("0");
        return textView;
    };

    private Handler mHandler = new Handler();
    private Runnable mTimerTask = new Runnable() {
        @Override
        public void run() {
            updateTimeNormal();
            postDelayed(this, 1000);
        }
    };

    private TimeViewTime mTimeViewTime;
    private boolean isStarted = false;

    public TimeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.time_view_layout, this);
        initTextSwitcher();
    }

    private void initTextSwitcher() {
        mTenHour = findViewById(R.id.tsTenHour);
        mTenHour.setFactory(mFactory);
        mHour = findViewById(R.id.tsHour);
        mHour.setFactory(mFactory);
        mTenMin = findViewById(R.id.tsTenMin);
        mTenMin.setFactory(mFactory);
        mMin = findViewById(R.id.tsMin);
        mMin.setFactory(mFactory);
        mTenSec = findViewById(R.id.tsTenSec);
        mTenSec.setFactory(mFactory);
        mSec = findViewById(R.id.tsSec);
        mSec.setFactory(mFactory);
    }

    /**
     * 设置时间。翻页效果。
     */
    public void setTime(long time) {
        if (!isStarted) {
            return;
        }
        mDeltaTime = time - System.currentTimeMillis();
        mTimeViewTime.set(realTime());
        checkAndSet();
    }

    private void checkAndSet() {
        checkAndSet(mTenHour, mTimeViewTime.tenHour);
        checkAndSet(mHour, mTimeViewTime.hour);
//        checkAndSet(mTenMin, mTimeViewTime.tenMin);
//        checkAndSet(mMin, mTimeViewTime.min);
//        checkAndSet(mTenSec, mTimeViewTime.tenSec);
//        checkAndSet(mSec, mTimeViewTime.sec);
    }

    private static void checkAndSet(TextSwitcher switcher, int value) {
        int val = Integer.parseInt(((TextView) switcher.getCurrentView()).getText().toString());
        if (val != value) {
            switcher.setText(String.valueOf(value));
        }
    }

    /**
     * 设置时间。无效果。
     */
    public void setTimeNormal(long time) {
        if (!isStarted) {
            return;
        }
    }

    /**
     * 开始计时
     */
    public void startTimer(long realTime) {
        long localTime = System.currentTimeMillis();
        mDeltaTime = realTime - localTime;
        mTimeViewTime = new TimeViewTime(realTime);
        mHandler.post(mTimerTask);
        isStarted = true;
    }

    public void setDelta(long delta) {
        mDeltaTime = delta;
    }

    private void updateTimeNormal() {
        mTimeViewTime.set(realTime());
        Log.v(TAG, "updateTimeNormal: " + mTimeViewTime);
        mTenHour.setCurrentText(String.valueOf(mTimeViewTime.tenHour));
        mHour.setCurrentText(String.valueOf(mTimeViewTime.hour));
        mTenMin.setCurrentText(String.valueOf(mTimeViewTime.tenMin));
        mMin.setCurrentText(String.valueOf(mTimeViewTime.min));
        mTenSec.setCurrentText(String.valueOf(mTimeViewTime.tenSec));
        mSec.setCurrentText(String.valueOf(mTimeViewTime.sec));
    }

    private void updateTime() {

    }

    /**
     * 调整时间。
     *
     * @return 经过调整后的，显示出来的真实时间。
     */
    private long realTime() {
        return System.currentTimeMillis() + mDeltaTime;
    }

    public static class TimeViewTime {
        private DateFormat df = new SimpleDateFormat("HHmmss");
        public int tenHour;
        public int hour;
        public int tenMin;
        public int min;
        public int tenSec;
        public int sec;

        public TimeViewTime(long time) {
            set(time);
        }

        public void set(long time) {
            String timeStr = df.format(new Date(time));
            tenHour = timeStr.charAt(0) - '0';
            hour = timeStr.charAt(1) - '0';
            tenMin = timeStr.charAt(2) - '0';
            min = timeStr.charAt(3) - '0';
            tenSec = timeStr.charAt(4) - '0';
            sec = timeStr.charAt(5) - '0';
        }

        @Override
        public String toString() {
            return "TimeViewTime{" +
                    "df=" + df +
                    ", tenHour=" + tenHour +
                    ", hour=" + hour +
                    ", tenMin=" + tenMin +
                    ", min=" + min +
                    ", tenSec=" + tenSec +
                    ", sec=" + sec +
                    '}';
        }
    }
}
