package com.hlz.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.hlz.activity.UnderwayDetailsActivity;
import com.hlz.database.DatabaseUtil;
import com.hlz.order.MyApplication;
import com.hlz.order.R;
import com.hlz.util.DoubleClickExitHelper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class UnderwayItemAdapter extends BaseAdapter {
    private List<UnderwayDetailsActivity.IndentMenu> indentMenus;
    private Context context;
    private UnderwayDetailsActivity activity;
    private Map<String,Double> menus;
    public UnderwayItemAdapter(UnderwayDetailsActivity activity, List<UnderwayDetailsActivity.IndentMenu> indentMenus){
        this.indentMenus=indentMenus;
        this.activity=activity;
        context= MyApplication.getContext();
        DatabaseUtil databaseUtil=new DatabaseUtil();
        databaseUtil.DataBaseUtilInit(context);
        menus=databaseUtil.queryDatabase();
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view==null){
            viewHolder=new ViewHolder();
            view=LayoutInflater.from(context).inflate(R.layout.item_underway_details,null);
            viewHolder.greensName=(TextView)view.findViewById(R.id.greens_name);
            viewHolder.fulfillNumber=(TextView) view.findViewById(R.id.fulfill_number);
            viewHolder.price=(TextView)view.findViewById(R.id.price);
            viewHolder.reserveNumber=(TextView) view.findViewById(R.id.reserve_number);
            viewHolder.minusFulfillNumber=(ImageButton)view.findViewById(R.id.minus_fulfill_number);
            viewHolder.minusReserveNumber=(ImageButton)view.findViewById(R.id.minus_reserve_number);
            viewHolder.plusFulfillNumber=(ImageButton)view.findViewById(R.id.plus_fulfill_number);
            viewHolder.plusReserveNumber=(ImageButton)view.findViewById(R.id.plus_reserve_number);
            view.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)view.getTag();
        }
        final UnderwayDetailsActivity.IndentMenu indentMenu=getItem(i);
        viewHolder.greensName.setText(indentMenu.getName());
        viewHolder.price.setText(indentMenu.getPrice());
        viewHolder.reserveNumber.setText(getItem(i).getReserveNumber());
        viewHolder.fulfillNumber.setText(getItem(i).getFulfillNumber());
        viewHolder.minusReserveNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UnderwayDetailsActivity.IndentMenu tempIndentMenu=getItem(i);
                if (Integer.valueOf(tempIndentMenu.getReserveNumber())<=0){
                    Toast.makeText(activity,"已经是0了，不能再少了！",Toast.LENGTH_SHORT).show();
                }else{
                    //改变数量，改变价格
                    getItem(i).setReserveNumber(Integer.toString(Integer.valueOf(tempIndentMenu.getReserveNumber())-1));
                    double tempPrice=Double.valueOf(tempIndentMenu.getPrice());
                    tempPrice=tempPrice-menus.get(tempIndentMenu.getName());
                    getItem(i).setPrice(Double.toString(tempPrice));
                    activity.listIsChanged=true;
                    notifyDataSetChanged();
                }
            }
        });
        viewHolder.plusReserveNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getItem(i).setReserveNumber(Integer.toString(Integer.valueOf(getItem(i).getReserveNumber())+1));
                double tempPrice=Double.valueOf(getItem(i).getPrice());
                tempPrice=tempPrice+menus.get(getItem(i).getName());
                getItem(i).setPrice(Double.toString(tempPrice));
                activity.listIsChanged=true;
                notifyDataSetChanged();
            }
        });
        viewHolder.plusFulfillNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int fulfillNumber=Integer.valueOf(getItem(i).getFulfillNumber());
                int reserveNumber=Integer.valueOf(getItem(i).getReserveNumber());
                if (fulfillNumber<reserveNumber){
                    getItem(i).setFulfillNumber(Integer.toString(Integer.valueOf(getItem(i).getFulfillNumber())+1));
                    activity.listIsChanged=true;
                    notifyDataSetChanged();
                }else{
                    Toast.makeText(activity,"上菜数量不能大于订菜数量",Toast.LENGTH_LONG).show();
                }
            }
        });
        viewHolder.minusFulfillNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.valueOf(getItem(i).getFulfillNumber())>0){
                    getItem(i).setFulfillNumber(Integer.toString(Integer.valueOf(getItem(i).getFulfillNumber())-1));
                    activity.listIsChanged=true;
                    notifyDataSetChanged();
                }else{
                    Toast.makeText(activity,"已经是0了，不能再少了！",Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (i%2 ==1){
            view.setBackgroundColor(Color.parseColor("#F5F5DC"));
        }
        return view;
    }
    private class ViewHolder{
        TextView greensName;
        TextView price;
        TextView reserveNumber;
        TextView fulfillNumber;
        ImageButton minusReserveNumber;
        ImageButton plusReserveNumber;
        ImageButton minusFulfillNumber;
        ImageButton plusFulfillNumber;
    }
    public List<UnderwayDetailsActivity.IndentMenu> getIndentMenus(){
        return indentMenus;
    }
    public void setIndentMenus(List<UnderwayDetailsActivity.IndentMenu> indentMenus){
        this.indentMenus=indentMenus;
        notifyDataSetChanged();
    }
}
