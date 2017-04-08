package com.hlz.fragment;

/**
 * QQ：1430261583
 * Created by Hanlizhi on 2016/9/8.
 */
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.hlz.order.R;
import com.hlz.util.MonitoringTime;

public class MeFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_me,container,false) ;
        final HTextView timeUsed=(HTextView)v.findViewById(R.id.timeused);
        Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==0x123){
                    String showTime=msg.getData().getString("showTime");
                    timeUsed.setAnimateType(HTextViewType.RAINBOW);//TYPER
                    timeUsed.animateText(showTime);
                }
            }
        };
        MonitoringTime monitoringTime=new MonitoringTime(getActivity());
        monitoringTime.getShowTime(handler);
        return v ;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
