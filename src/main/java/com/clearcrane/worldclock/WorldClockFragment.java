package com.clearcrane.worldclock;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jjy on 2018/3/26.
 */

public class WorldClockFragment extends BaseFragment {
    private static final String TAG = "WorldClockFragment";

    private CircularView mCircularView;
    private ImageView mRuler;
    private TextView mTvTime;
    private TextView mTvDate;

    private Handler mHandler = new Handler();
    private Runnable mUpdateTime;

    private DateFormat mDFTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private DateFormat mDFDate = new SimpleDateFormat("yyyy年MM月dd日，EEE", Locale.getDefault());

    @Override
    public int getContentView() {
        return R.layout.main_fragment;
    }

    @Override
    public void initView(View rootView) {
        mCircularView = findViewById(R.id.circular);
        mRuler = findViewById(R.id.ruler);
        mTvTime = findViewById(R.id.tvTime);
        mTvDate = findViewById(R.id.tvDate);

//        mCircularView.moveImmediatelyTo(8);

        Glide.with(this).load(R.drawable.worldclock8_02).into(mRuler);
        mRuler.post(() -> mRuler.setTranslationX(42));

        mUpdateTime = new Runnable() {
            @Override
            public void run() {
                Date date = new Date();
                mTvTime.setText(mDFTime.format(date));
                mTvDate.setText(mDFDate.format(date));
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.post(mUpdateTime);
    }

    private <T extends View> T findViewById(int id) {
        if (getView() == null) {
            return null;
        }
        return getView().findViewById(id);
    }
}
