package com.hlz.fragment;

/**
 * Created by Administrator on 2016/9/8.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hlz.order.R;

public class DiscoverFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_discover,container,false) ;
        return v ;
    }
}
