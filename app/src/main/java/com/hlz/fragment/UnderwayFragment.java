package com.hlz.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017/4/9 0009.
 */

public class UnderwayFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(android.support.design.R.layout)
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onRefresh() {

    }
}
