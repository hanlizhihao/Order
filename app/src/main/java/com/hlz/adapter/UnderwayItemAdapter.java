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
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.hlz.activity.UnderwayDetailsActivity;
import com.hlz.order.MyApplication;
import com.hlz.order.R;

import java.lang.reflect.Field;
import java.util.List;

public class UnderwayItemAdapter extends BaseAdapter {
    private List<UnderwayDetailsActivity.IndentMenu> indentMenus;
    private Context context;
    private UnderwayDetailsActivity activity;
    public UnderwayItemAdapter(UnderwayDetailsActivity activity, List<UnderwayDetailsActivity.IndentMenu> indentMenus){
        this.indentMenus=indentMenus;
        this.activity=activity;
        context= MyApplication.getContext();
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
            viewHolder.fulfillNumber=(NumberPicker)view.findViewById(R.id.fulfill_number);
            viewHolder.price=(TextView)view.findViewById(R.id.price);
            viewHolder.reserveNumber=(NumberPicker)view.findViewById(R.id.reserve_number);
            view.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)view.getTag();
        }
        final UnderwayDetailsActivity.IndentMenu indentMenu=getItem(i);
        viewHolder.greensName.setText(indentMenu.getName());
        viewHolder.price.setText(indentMenu.getPrice());
        viewHolder.reserveNumber.setMinValue(0);
        viewHolder.reserveNumber.setMaxValue(99);
        setNumberPickerTextColor(viewHolder.reserveNumber,R.color.best_head_bg);
        setNumberPickerTextColor(viewHolder.fulfillNumber,R.color.best_head_bg);
        viewHolder.reserveNumber.setValue(Integer.valueOf(indentMenu.getReserveNumber()));
        viewHolder.reserveNumber.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int old, int newi) {
                activity.listIsChanged=true;
                if (newi>=Integer.valueOf(getItem(i).getFulfillNumber())){
                    getItem(i).setReserveNumber(Integer.toString(newi));
                    notifyDataSetChanged();
                }else{//出现了订菜数量小于上菜数量的情况
                    Toast.makeText(context,"订菜数量不能小于上菜数量！",Toast.LENGTH_LONG).show();
                    notifyDataSetChanged();
                }
            }
        });
        viewHolder.fulfillNumber.setMinValue(0);
        viewHolder.fulfillNumber.setMaxValue(99);
        viewHolder.fulfillNumber.setValue(Integer.valueOf(indentMenu.getFulfillNumber()));
        viewHolder.fulfillNumber.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int old, int newi) {
                activity.listIsChanged=true;
                if (newi<=Integer.valueOf(getItem(i).getReserveNumber())){
                    getItem(i).setReserveNumber(Integer.toString(newi));
                    notifyDataSetChanged();
                }else{
                    Toast.makeText(context,"上菜数量不能大于订菜数量",Toast.LENGTH_LONG).show();
                    notifyDataSetChanged();
                }
            }
        });
        if (i%2 ==1){
            view.setBackgroundColor(Color.parseColor("#F5F5DC"));
        }
        return view;
    }
    public boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                Field selectorWheelPaintField;
                try {
                    selectorWheelPaintField = numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    try {
                        ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    private class ViewHolder{
        TextView greensName;
        TextView price;
        NumberPicker reserveNumber;
        NumberPicker fulfillNumber;
    }
    public List<UnderwayDetailsActivity.IndentMenu> getIndentMenus(){
        return indentMenus;
    }
    public void setIndentMenus(List<UnderwayDetailsActivity.IndentMenu> indentMenus){
        this.indentMenus=indentMenus;
        notifyDataSetChanged();
    }
}
