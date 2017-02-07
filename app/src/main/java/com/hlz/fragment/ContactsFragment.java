package com.hlz.fragment;

/**
 * Created by Administrator on 2016/9/8.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hlz.activity.MakeOrder;
import com.hlz.order.R;

public class ContactsFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.contacts_fragment,container,false) ;
        Button makeorder=(Button) v.findViewById(R.id.f);
        makeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), MakeOrder.class);
                startActivity(intent);
            }
        });
        return v;
    }

}
