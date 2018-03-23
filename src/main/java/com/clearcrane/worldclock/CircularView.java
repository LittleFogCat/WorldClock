package com.clearcrane.worldclock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

/**
 * Created by jjy on 2018/3/23.
 */

public class CircularView extends LinearLayout {
    private static final String TAG = "CircularView";
    private int mResId;
    private ImageView mMainImageView;
    private ImageView mRecycledImageView;

    private float mSreenWidth;

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

        View view = View.inflate(context, R.layout.circular_view_layout, this);
        mMainImageView = view.findViewById(R.id.img1);
        mRecycledImageView = view.findViewById(R.id.img2);

        if (mResId != 0) {
            Log.d(TAG, "init: load IMAGE");
            Glide.with(context).load(mResId).into(mMainImageView);
            Glide.with(context).load(mResId).into(mRecycledImageView);
        }

        setFocusable(true);

        post(() -> Log.d(TAG, "run: " + mMainImageView.getWidth() + "," +
                mMainImageView.getHeight() + "," +
                mRecycledImageView.getWidth() + "," +
                mRecycledImageView.getHeight()));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: " + keyCode);
        if (!canMove) {
            return super.onKeyDown(keyCode, event);
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                return moveLeft();
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                return moveRight();
            }
        } else {
            Log.d(TAG, "onKeyDown: " + event.getAction());
        }
        return super.onKeyDown(keyCode, event);
    }

    private float mMainTransX = 0;
    private boolean canMove = true;

    private boolean moveLeft() {
        Log.d(TAG, "moveLeft: ");
        ObjectAnimator animator = ObjectAnimator.ofFloat(mMainImageView,
                "translationX",
                mMainTransX -= 100);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                canMove = true;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                canMove = false;
            }
        });
        animator.start();

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mRecycledImageView,
                "translationX",
                mMainTransX -= 100);
        animator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                canMove = true;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                canMove = false;
            }
        });
        animator1.start();
//        Matrix matrix = getMatrix();
//        matrix.postTranslate(mMainTransX -= 100, 0);
//        invalidate();
//        int left = mMainImageView.getLeft();
//        mMainImageView.setLeft(left - 100);
//        getMatrix().postTranslate(-100, 0);
        return true;
    }

    private boolean moveRight() {
        Log.d(TAG, "moveLeft: ");
        ObjectAnimator animator = ObjectAnimator.ofFloat(mMainImageView,
                "translationX",
                mMainTransX += 100);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                canMove = true;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                canMove = false;
            }
        });
        animator.start();

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mRecycledImageView,
                "translationX",
                mMainTransX += 100);
        animator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                canMove = true;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                canMove = false;
            }
        });
        animator1.start();
//        Log.d(TAG, "moveRight: ");
//        int left = mMainImageView.getLeft();
//        mMainImageView.setLeft(left + 100);
        return true;
    }

    public void setResId(int resId) {
        mResId = resId;
    }
}
