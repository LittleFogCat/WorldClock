package com.clearcrane.worldclock.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.clearcrane.worldclock.R;

/**
 * Created by jjy on 2018/3/23.
 */

public class CircularView extends LinearLayout {
    private static final String TAG = "CircularView";

    private static final float IMAGE_WIDTH = 2647;
    private static final int DEFAULT_DURATION = 230;

    private int mResId;
    private LinearLayout mRootView;
    private ImageView mAliceImageView;
    private ImageView mBobImageView;


    private ImageView mLeftView;
    private ImageView mRightView;

    private float mScreenWidth = 1920;

    private boolean canMove = true;

    private float mStepPx = IMAGE_WIDTH / 24;

    private Animator.AnimatorListener mAnimatorListener;

    private Animator.AnimatorListener mDefaultListener = new AnimatorListenerAdapter() {

        @Override
        public void onAnimationStart(Animator animation) {
            canMove = false;
            if (mAnimatorListener != null) {
                mAnimatorListener.onAnimationStart(animation);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            canMove = true;
            if (mAnimatorListener != null) {
                mAnimatorListener.onAnimationEnd(animation);
            }
        }
    };

    public CircularView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularView);
        mResId = a.getResourceId(R.styleable.CircularView_src, 0);
        a.recycle();

        mRootView = (LinearLayout) View.inflate(context, R.layout.circular_view_layout, this);
        mAliceImageView = mRootView.findViewById(R.id.img1);
        mBobImageView = mRootView.findViewById(R.id.img2);

        if (mResId != 0) {
            Log.d(TAG, "init: load image");
            Glide.with(context).load(mResId).into(mAliceImageView);
            Glide.with(context).load(mResId).into(mBobImageView);
        }

        setFocusable(true);
        mLeftView = mAliceImageView;
        mRightView = mBobImageView;
        move(mLeftView, -IMAGE_WIDTH);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v(TAG, "onKeyDown: " + keyCode);
        if (!canMove) {
            return super.onKeyDown(keyCode, event);
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                return moveRight();
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                return moveLeft();
            }
        } else {
            Log.v(TAG, "onKeyDown: " + event.getAction());
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean moveLeft() {
        Log.d(TAG, "moveLeft: ");
        scroll(-mStepPx, DEFAULT_DURATION, mDefaultListener, null);
        return true;
    }

    private boolean moveRight() {
        Log.d(TAG, "moveLeft: ");
        scroll(mStepPx, DEFAULT_DURATION, mDefaultListener, null);
        return true;
    }

    /**
     * 瞬间移动基本法。所有move必须归源。
     */
    private void move(View view,
                      float toX) {
        view.setX(toX);
    }

    /**
     * 移动x个像素
     */
    public void move(float x) {
        while (x + mLeftView.getX() >= 0) {
            x -= IMAGE_WIDTH;
        }
        while (x + mLeftView.getX() <= mScreenWidth - 2 * IMAGE_WIDTH) {
            x += IMAGE_WIDTH;
        }

        move(mLeftView, mLeftView.getX() + x);
        move(mRightView, mRightView.getX() + x);
    }

    private void moveLeftViewToRight() {
        Log.d(TAG, "moveLeftViewToRight: from " + mLeftView.getX() + " to " + (mRightView.getX() + IMAGE_WIDTH));
        // 移动
        move(mLeftView, mRightView.getX() + IMAGE_WIDTH);
        Log.d(TAG, "moveLeftViewToRight: left " + mLeftView.getX() + ", right " + mRightView.getX());
        // 交换LR
        ImageView tmpView = mLeftView;
        mLeftView = mRightView;
        mRightView = tmpView;
        Log.d(TAG, "moveLeftViewToRight: left " + mLeftView.getX() + ", right " + mRightView.getX());
    }

    private void moveRightViewToLeft() {
        Log.d(TAG, "moveRightViewToLeft: ");
        // 移动
        move(mRightView, mLeftView.getX() - IMAGE_WIDTH);
        // 交换LR
        ImageView tmpView = mLeftView;
        mLeftView = mRightView;
        mRightView = tmpView;
    }

    /**
     * 滚动基本法。所有scroll必须归源。
     */
    private void scroll(View view,
                        float toX,
                        int duration,
                        Animator.AnimatorListener listener) {
        createAnimator(view, toX, duration, listener).start();
    }

    /**
     * 懒得滚。
     * 如果屏幕上只有一个ImageView呈现，那么只滚动这个View，另一个不滚。
     * 否则两个一起滚。
     * 特别的，当屏幕外的View进入屏幕，需要首先移动到正确的位置。
     */
    private void lazyScroll(float x) {
        // TODO: 2018/3/26 性能足够，必要性不大，且造成不必要麻烦。
    }

    /**
     * 滚。一起滚。
     * <p>
     * 特别的，当左(右)边滚到头，还要往左(右)滚，就要先把左(右)边的View调换到右(左)边再滚。
     */
    private void scroll(float x, int duration,
                        Animator.AnimatorListener listenerLeft,
                        Animator.AnimatorListener listenerRight) {
        Log.d(TAG, "scroll: " + mLeftView.getX() + ", " + mRightView.getX());
        if (mLeftView.getX() + IMAGE_WIDTH * 2 <= mScreenWidth - x + 10) {
            // 往左滚。x<0。滚到尽头，需要把左边的View调换到右边。
            moveLeftViewToRight();
        } else if (mLeftView.getX() + x >= -10) {
            // 往右滚。x>0。滚到尽头，需要把右边的View调换到左边。
            moveRightViewToLeft();
        }
        scroll(mLeftView, mLeftView.getX() + x, duration, listenerLeft);
        scroll(mRightView, mRightView.getX() + x, duration, listenerRight);
    }

    private static Animator createAnimator(View target, float value, Animator.AnimatorListener listener) {
        return createAnimator(target, value, 300, listener);
    }

    private static Animator createAnimator(View target, float value, int duration, Animator.AnimatorListener listener) {
        Animator animator = ObjectAnimator.ofFloat(target,
                "translationX",
                value);
        if (listener != null) animator.addListener(listener);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    private void order() {

    }


    public void setOnAnimatorListener(Animator.AnimatorListener listener) {
        mAnimatorListener = listener;
    }

    public ImageView getLeftView() {
        return mLeftView;
    }

    public ImageView getRightView() {
        return mRightView;
    }

    public float getImageWidth() {
        return IMAGE_WIDTH;
    }
}
