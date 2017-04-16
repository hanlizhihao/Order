package com.hlz.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hlz.activity.HistoryDetailsActivity;
import com.hlz.activity.UnderwayDetailsActivity;
import com.hlz.order.MyApplication;
import com.hlz.order.R;

import java.util.List;

/**
 * 历史订单
 * Created by hlz on 2017/4/16 0016.
 */

public class HistoryItemAdapter extends BaseAdapter {
    private List<UnderwayDetailsActivity.IndentMenu> indentMenus;
    private Context context;
    public HistoryItemAdapter(List<UnderwayDetailsActivity.IndentMenu> indentMenus){
        this.indentMenus=indentMenus;
        context= MyApplication.getContext();
    }
    private class ViewHolder{
        TextView greensName;
        TextView price;
        TextView reserveNumber;
    }
    @Override
    public int getCount() {
        return indentMenus.size();
    }

    @Override
    public UnderwayDetailsActivity.IndentMenu getItem(int i) {
        return indentMenus.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        HistoryItemAdapter.ViewHolder viewHolder = null;
        if (view==null){
            viewHolder=new ViewHolder();
            view= LayoutInflater.from(context).inflate(R.layout.item_history_details,null);
            viewHolder.greensName=(TextView)view.findViewById(R.id.history_greens_name);
            viewHolder.price=(TextView)view.findViewById(R.id.history_price);
            viewHolder.reserveNumber=(TextView)view.findViewById(R.id.history_reserve_number);
            view.setTag(viewHolder);
        }else {
            viewHolder=(HistoryItemAdapter.ViewHolder)view.getTag();
        }
        UnderwayDetailsActivity.IndentMenu indentMenu=getItem(i);
        viewHolder.greensName.setText(indentMenu.getName());
        viewHolder.reserveNumber.setText(indentMenu.getReserveNumber());
        viewHolder.price.setText(indentMenu.getPrice());
        if (i%2 ==1){
            view.setBackgroundColor(Color.parseColor("#F5F5DC"));
        }
        return view;
    }
    public void setIndentMenus(List<UnderwayDetailsActivity.IndentMenu> indentMenus){
        this.indentMenus=indentMenus;
        notifyDataSetChanged();
    }
}
