package com.clearcrane.worldclock.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

/**
 * Created by jjy on 2018/3/29.
 */

public class SpreadCircleView extends View {
    public SpreadCircleView(Context context) {
        this(context, null, 0);
    }

    public SpreadCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpreadCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        AnimationSet set = new AnimationSet(true);

        Animation scale = new ScaleAnimation(0.1f, 1f, 0.1f, 1f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scale.setRepeatMode(Animation.RESTART);
        scale.setRepeatCount(Animation.INFINITE);

        Animation alpha = new AlphaAnimation(1, 0);
        alpha.setRepeatMode(Animation.RESTART);
        alpha.setRepeatCount(Animation.INFINITE);

        set.addAnimation(scale);
        set.addAnimation(alpha);
        set.setDuration(2000);

        startAnimation(set);
    }

}
