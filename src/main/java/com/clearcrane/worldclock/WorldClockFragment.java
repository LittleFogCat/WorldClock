package com.clearcrane.worldclock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by jjy on 2018/3/26.
 */

public class WorldClockFragment extends BaseFragment {
    private static final String TAG = "WorldClockFragment";

    private FrameLayout mRootView;
    private TimezoneView mCircularView;
    private ImageView mRuler;
    private TextView mTvTime;
    private TextView mTvDate;
    private TextView mTvTimezone;

    private Handler mHandler = new Handler();
    private Runnable mUpdateTime;

    private Locale mLocale;
    private int mFocusTimezone = 8;// 当前指向的时区
    private int mLocalZone = 8;// 当地时区
    private DateFormat mDFTime;
    private DateFormat mDFDate;
    private Date mDate = new Date();

    private List<City> mCityList = new ArrayList<>();

    @Override
    public int getContentView() {
        return R.layout.main_fragment;
    }

    @Override
    public void findViews() {
        mCircularView = findViewById(R.id.circular);
        mRuler = findViewById(R.id.ruler);
        mTvTime = findViewById(R.id.tvTime);
        mTvDate = findViewById(R.id.tvDate);
        mTvTimezone = findViewById(R.id.tvTimezone);
    }

    @Override
    public void initView(View rootView) {
        mRootView = (FrameLayout) rootView;
        initData();

        Glide.with(this).load(R.drawable.worldclock8_02).into(mRuler);
        mRuler.post(() -> mRuler.setTranslationX(42));

        initBackground();

        mUpdateTime = new Runnable() {
            @Override
            public void run() {
                Date date = getTime();
                mTvTime.setText(mDFTime.format(date));
                mTvDate.setText(mDFDate.format(date));
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.post(mUpdateTime);

        TimeZone timeZone = TimeZone.getDefault();
        int offset = timeZone.getOffset(System.currentTimeMillis());// 当前时区和标准时间的偏移
        mLocalZone = offset / 3600 / 1000;// 当前时区
        Log.d(TAG, "initView: 当前时区：" + mLocalZone);

        mHandler.postDelayed(() -> {
            addCity("北京", 1293, 349);
            addCity("上海", 1325, 406);
            addCity("阿瓦隆", 1288, 825);
        }, 1000);

        mCityList.add(new City("北京", "Beijing", 1293, 349));
        mCityList.add(new City("上海", "Shanghai", 1325, 406));
        mCityList.add(new City("阿瓦隆", "Avalon", 1288, 825));
//        mCityList.add(new City("北京", "Beijing", random(1266, 1377), random(100, 1000)));
//        mCityList.add(new City("上海", "Shanghai", random(1266, 1377), random(100, 1000)));
//        mCityList.add(new City("阿瓦隆", "Avalon", random(1266, 1377), random(100, 1000)));
    }

    private void initData() {
        mLocale = getResources().getConfiguration().locale;
        mDFTime = new SimpleDateFormat("HH:mm:ss", mLocale);
        if (mLocale.getLanguage().equals(Locale.CHINA.getLanguage())) {
            mDFDate = new SimpleDateFormat("yyyy年MM月dd日，EEE", mLocale);
        } else {
            mDFDate = new SimpleDateFormat("MMM dd, yyyy, EEE", mLocale);
        }
    }

    private void initBackground() {
        mCircularView.setTimezone(8);

        mCircularView.setOnAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFocusTimezone = mCircularView.calCurrentTimezone();

                mTvTimezone.setText(String.format("%s%s",
                        TimezoneView.getTimezoneString(mFocusTimezone, mLocale),
                        getString(R.string.StandardTime)));

                int zoneOffset = mFocusTimezone - mLocalZone;// 时区偏移
                int timeOffset = zoneOffset * 3600 * 1000;// 时间偏移

                long focusTime = System.currentTimeMillis() + timeOffset;
                mTvTime.setText(mDFTime.format(focusTime));
                mTvDate.setText(mDFDate.format(focusTime));

                updateCity(mFocusTimezone);
            }
        });
    }

    private Date getTime() {
        int zoneOffset = mFocusTimezone - mLocalZone;// 时区偏移
        int timeOffset = zoneOffset * 3600 * 1000;// 时间偏移

        long focusTime = System.currentTimeMillis() + timeOffset;// 展示时间
        mDate.setTime(focusTime);
        return mDate;
    }

    private void addCity(City city) {
        addCity(city.cityNameCN, city.x, city.y);
    }

    private void addCity(String cityName, float x, float y) {
        FrameLayout outer = (FrameLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.city_item, mRootView, false);
        TextView tvName = outer.findViewById(R.id.tvCityName);
        tvName.setText(cityName);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) outer.getLayoutParams();
        params.leftMargin = (int) x;
        params.topMargin = (int) y;
        outer.setLayoutParams(params);

        outer.setAlpha(0);
        outer.setTag("city");
        mRootView.addView(outer);
        Animator animator = ObjectAnimator.ofFloat(outer, "alpha", 0, 1);
        animator.setDuration(300);
        animator.start();
    }

    private void addRandomCity(String cityName) {
        addCity(randomName(), random(1266, 1377), random(100, 1000));
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
        for (City city : mCityList) {
//            if (city.timezone == timezone) {
//            addCity(city);
//            }
            addRandomCity(city.cityNameCN);
        }
    }

    private int random(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    private String randomName() {
        String s = "";
        s += RandomUtil.getRandomHan();
        s += RandomUtil.getRandomHan();
        if (RandomUtil.random(0, 100) < 50)
            s += RandomUtil.getRandomHan();

        return s;
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
