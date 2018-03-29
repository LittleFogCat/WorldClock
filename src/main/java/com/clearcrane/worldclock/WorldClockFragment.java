package com.clearcrane.worldclock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clearcrane.worldclock.base.BaseFragment;
import com.clearcrane.worldclock.utils.Auto;
import com.clearcrane.worldclock.utils.RandomUtil;
import com.clearcrane.worldclock.views.TimeView;
import com.clearcrane.worldclock.views.TimezoneView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by jjy on 2018/3/26.
 */

public class WorldClockFragment extends BaseFragment implements KeyEvent.Callback {
    private static final String TAG = "WorldClockFragment";

    private FrameLayout mRootView;
    private TimezoneView mTimezoneView;
    //    private ImageView mRuler;
    private TextView mTvTime;
    private TextView mTvDate;
    private TextView mTvTimezone;
    private TimeView mTimeView;

    private Handler mHandler = new Handler();
    private Runnable mUpdateTime;

    private Locale mLocale;
    private int mFocusTimezone = 8;// 当前指向的时区
    private int mLocalZone = 8;// 当地时区
    private DateFormat mDFTime;
    private DateFormat mDFDate;
    private Date mDate = new Date();
    private long mDelta;

    private List<City> mCityList = new ArrayList<>();

    public static WorldClockFragment newInstance() {
        Bundle args = new Bundle();
        WorldClockFragment fragment = new WorldClockFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onArguments(Bundle args) {
        if (args != null) {
            mLocalZone = args.getInt("timezone", 8);
            mDelta = args.getLong("delta", 0);
        }
    }

    @Override
    public int getContentView() {
        return R.layout.main_fragment;
    }

    @Override
    public void findViews() {
        mTimezoneView = findViewById(R.id.circular);
//        mRuler = findViewById(R.id.ruler);
        mTvTime = findViewById(R.id.tvTime);
        mTvDate = findViewById(R.id.tvDate);
        mTvTimezone = findViewById(R.id.tvTimezone);
        mTimeView = findViewById(R.id.timeView);
    }

    @Override
    public void initView(View rootView) {
        mRootView = (FrameLayout) rootView;
        try {
            readCityList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initLanguage();
        initBackground();
        initForeground();
        updateTime();
    }

    private void initLanguage() {
        mLocale = getResources().getConfiguration().locale;
        mDFTime = new SimpleDateFormat("HH:mm:ss", mLocale);
        if (mLocale.getLanguage().equals(Locale.CHINA.getLanguage())) {
            mDFDate = new SimpleDateFormat("yyyy年MM月dd日，EEE", mLocale);
        } else {
            mDFDate = new SimpleDateFormat("MMM dd, yyyy, EEE", mLocale);
        }
    }

    private void initBackground() {
        mTimezoneView.setTimezone(mLocalZone);
        onTimezoneChanged(mLocalZone);

        mTimezoneView.setOnAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                onTimezoneChanged(mTimezoneView.calCurrentTimezone());
                updateTime();
            }
        });
    }

    private void initForeground() {
//        Glide.with(this).load(R.drawable.worldclock8_02).into(mRuler);
//        mRuler.post(() -> mRuler.setTranslationX(Auto.size(42)));

        mUpdateTime = new Runnable() {
            @Override
            public void run() {
                mDate.setTime(getRealTime());
                mTvTime.setText(mDFTime.format(mDate));
                mTvDate.setText(mDFDate.format(mDate));
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.post(mUpdateTime);

        TimeZone timeZone = TimeZone.getDefault();
        int offset = timeZone.getOffset(System.currentTimeMillis());// 当前时区和标准时间的偏移
        mLocalZone = offset / 3600 / 1000;// 当前时区
        Log.d(TAG, "initView: 当前时区：" + mLocalZone);

        mCityList.add(new City("北京", "Beijing", 1293, 349));
        mCityList.add(new City("上海", "Shanghai", 1325, 406));
        mCityList.add(new City("阿瓦隆", "Avalon", 1288, 825));
//        mCityList.add(new City("北京", "Beijing", random(1266, 1377), random(100, 1000)));
//        mCityList.add(new City("上海", "Shanghai", random(1266, 1377), random(100, 1000)));
//        mCityList.add(new City("阿瓦隆", "Avalon", random(1266, 1377), random(100, 1000)));

        mTimeView.startTimer(System.currentTimeMillis() + mDelta);
    }

    private void onTimezoneChanged(int timezone) {
        mFocusTimezone = timezone;
        mTvTimezone.setText(String.format("%s%s",
                mTimezoneView.getTimezoneString(mFocusTimezone, mLocale),
                getString(R.string.StandardTime)));
        updateTime();
    }

    private void updateTime() {
        long realTime = getRealTime();
        mTvTime.setText(mDFTime.format(realTime));
        mTvDate.setText(mDFDate.format(realTime));
        mTimeView.setTime(realTime);

        updateCity(mFocusTimezone);
    }

    /**
     * 获取当前时区对应的当地时间。
     */
    private long getRealTime() {
        int zoneOffset = mFocusTimezone - mLocalZone;// 时区偏移
        int timeOffset = zoneOffset * 3600 * 1000;// 时间偏移

        return System.currentTimeMillis() + timeOffset;
    }

    private void addCity(String cityName, float x, float y) {
        FrameLayout outer = (FrameLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.city_item, mRootView, false);
        TextView tvName = outer.findViewById(R.id.tvCityName);
        tvName.setText(cityName);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) outer.getLayoutParams();
        params.leftMargin = (int) Auto.size(x);
        params.topMargin = (int) Auto.size(y);
        outer.setLayoutParams(params);

        outer.setAlpha(0);
        outer.setTag("city");
        mRootView.addView(outer);
        Animator animator = ObjectAnimator.ofFloat(outer, "alpha", 0, 1);
        animator.setDuration(300);
        animator.start();
    }

    private void addRandomCity(String cityName) {
        addCity(RandomUtil.randomHans(2, 7), RandomUtil.random(1266, 1377), RandomUtil.random(100, 1000));
    }

    private void clearCity() {
        for (int i = 0; i < mRootView.getChildCount(); i++) {
            View view = mRootView.getChildAt(i);
            if ("city".equals(view.getTag())) {
                mRootView.removeViewAt(i);
                i--;
            }
        }
    }

    private void updateCity(int timezone) {
        clearCity();
        int count = 0;
        for (City city : mCityList) {
            if (city.timezone == timezone) {
                addCity(city);
                count++;
            }
        }
        while (count < 3) {
            addRandomCity("");
            count++;
        }
    }

    private void addCity(City city) {
        String name = mLocale.getLanguage().equals(Locale.CHINA.getLanguage()) ?
                city.cityNameCN : city.cityNameEN;
        addCity(name, city.x, city.y);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP ||
                keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            changeLocale();
        }
        return false;
    }

    public void readCityList() throws IOException {
        InputStream is = getActivity().getResources().openRawResource(R.raw.city_list);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String json = sb.toString();

        mCityList = new Gson().fromJson(json, new TypeToken<List<City>>() {
        }.getType());
    }

    public void changeLocale() {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale;
        if (mLocale.getLanguage().equals(Locale.CHINA.getLanguage())) {
            locale = Locale.US;
        } else {
            locale = Locale.CHINA;
        }
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        initLanguage();
        updateTime();
        onTimezoneChanged(mFocusTimezone);
    }

    public static class City {

        private String cityNameCN;
        private String cityNameEN;
        private float x;
        private float y;
        private int timezone;

        public City() {
        }

        public City(String cityNameCN, String cityNameEN, float x, float y) {
            this.cityNameCN = cityNameCN;
            this.cityNameEN = cityNameEN;
            this.x = x;
            this.y = y;
        }

        public String getCityNameEN() {
            return cityNameEN;
        }

        public void setCityNameEN(String cityNameEN) {
            this.cityNameEN = cityNameEN;
        }

        public String getCityNameCN() {
            return cityNameCN;
        }

        public void setCityNameCN(String cityNameCN) {
            this.cityNameCN = cityNameCN;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
}
