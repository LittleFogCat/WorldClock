package com.clearcrane.worldclock;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

/**
 * Created by jjy on 2018/3/26.
 */

public abstract class BaseFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getContentView(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViews();
        initView(view);
    }

    public abstract int getContentView();

    public void findViews(){}

    public abstract void initView(View rootView);

    public <T extends View> T findViewById(int id) {
        if (getView() == null) {
            return null;
        }
        return getView().findViewById(id);
    }
}
