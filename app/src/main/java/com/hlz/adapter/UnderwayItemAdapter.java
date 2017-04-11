package com.hlz.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.hlz.activity.UnderwayDetailsActivity;
import com.hlz.order.MyApplication;
import com.hlz.order.R;

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
        viewHolder.reserveNumber.setValue(Integer.valueOf(indentMenu.getReserveNumber()));
        viewHolder.reserveNumber.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int old, int newi) {
                activity.listIsChanged=true;
                if (newi>=Integer.valueOf(getItem(i).getFulfillNumber())){
                    UnderwayDetailsActivity.IndentMenu indentMenu1=getItem(i);
                    indentMenu1.setReserveNumber(Integer.toString(newi));
                    indentMenus.add(i,indentMenu1);
                }else{//出现了订菜数量小于上菜数量的情况
                    Toast.makeText(context,"订菜数量不能小于上菜数量！",Toast.LENGTH_LONG).show();
                    notifyDataSetChanged();
                }
            }
        });
        viewHolder.fulfillNumber.setMinValue(0);
        viewHolder.fulfillNumber.setValue(Integer.valueOf(indentMenu.getFulfillNumber()));
        viewHolder.fulfillNumber.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int old, int newi) {
                activity.listIsChanged=true;
                if (newi<=Integer.valueOf(getItem(i).getReserveNumber())){
                    UnderwayDetailsActivity.IndentMenu indentMenu1=getItem(i);
                    indentMenu1.setReserveNumber(Integer.toString(newi));
                    indentMenus.add(i,indentMenu1);
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
    private class ViewHolder{
        TextView greensName;
        TextView price;
        NumberPicker reserveNumber;
        NumberPicker fulfillNumber;
    }
    public List<UnderwayDetailsActivity.IndentMenu> getIndentMenus(){
        return indentMenus;
    }
//    @Override
//    public View getChildView(final int groupPosition, final int childPosition, boolean isLastchild, View view, ViewGroup viewGroup) {
//        LinearLayout linearLayouts=new LinearLayout(context);
//        linearLayouts.setOrientation(LinearLayout.HORIZONTAL);
//        linearLayouts.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        /**
//         * 子数据中textView的配置
//         */
//        TextView textView=new TextView(context);
//        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,64));
//        textView.setGravity(Gravity.START);textView.setText(getChild(groupPosition,childPosition).toString());
//        textView.setTextSize(20);textView.setTextColor(context.getResources().getColor(R.color.black));
//        //利用getChild返回的参数设定
//        textView.setText(getChild(groupPosition,childPosition).firstKey().toString());
//        /**
//         * 子数据中表上没上完这个菜的图标的配置
//         * 思路是比对book和serve相同key部分的value值是否相同
//         * 因此也引出了另一个问题，就是如何设定+号上限的问题
//         */
//        final ImageButton MarkFinished=new ImageButton(context);
//        MarkFinished.setLayoutParams(new ViewGroup.LayoutParams(64,64));
//        String key=getChild(groupPosition,childPosition).firstKey().toString();
//        int serve=(int)getChild(groupPosition,childPosition).values().iterator().next();
//        int book=getGroup(groupPosition).getOrder_book().get(key);
//        /**
//         * 设定大于等于，考虑兼容性（可能程序错误导致脏数据问题）
//         */
//        if (serve>=book)
//        {
//            MarkFinished.setBackgroundResource(R.mipmap.finished);
//        }
//        else{
//            MarkFinished.setBackgroundResource(R.mipmap.unfinished);
//        }
//        /**
//         * 子数据中被包含的LinerLayout的配置
//         */
//        LinearLayout linearLayout=new LinearLayout(context);
//        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        linearLayout.setGravity(Gravity.END);
//        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//        /**
//         * 子LinerLayout包含的组件两个ImageButton和一个TextView的配置
//         */
//        ImageButton minus=new ImageButton(context);
//        final ImageButton plus=new ImageButton(context);
//        //这个变量是菜品数量
//        TextView NumberOfDishes=new TextView(context);
//        /**
//         * 给减号绑定事件监听与参数设置
//         */
//        minus.setLayoutParams(new ViewGroup.LayoutParams(64,64));
//        minus.setBackgroundResource(R.mipmap.plan_minus);
//        minus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                /**
//                 * 弹窗提示是否删除这个菜品
//                 */
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("提示");
//                builder.setMessage("您确定要删除这个菜吗？");
//                builder.setIcon(R.drawable.z);
//                DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        if (arg1 == DialogInterface.BUTTON_POSITIVE) {
//                            arg0.cancel();
//                        } else if (arg1 == DialogInterface.BUTTON_NEGATIVE) {
//                            /**
//                             * 根据getChild返回的key,value来删除实体中的一个映射关系
//                             */
//                            TreeMap map=getChild(groupPosition,childPosition);
//                            String remove=map.firstKey().toString();
//                            getGroup(groupPosition).getOrder_display().remove(remove);
//                            getGroup(groupPosition).getOrder_book().remove(remove);
//                            getGroup(groupPosition).getOrder_serve().remove(remove);
//                            notifyDataSetChanged();
//                        }
//                    }
//                };
//                builder.setPositiveButton("取消", dialog);
//                builder.setNegativeButton("确定", dialog);
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//            }
//        });
//        plus.setLayoutParams(new ViewGroup.LayoutParams(64,64));
//        plus.setBackgroundResource(R.mipmap.plan_plus);
//        /**
//         * 给加号绑定事件监听器
//         */
//        plus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String add=getChild(groupPosition,childPosition).firstKey().toString();
//                int book=getGroup(groupPosition).getOrder_book().get(add);int display=getGroup(groupPosition).getOrder_display().get(add);
//                if (book<=display)
//                {
//                    Toast toast=Toast.makeText(context,"这个菜已经上完了",Toast.LENGTH_SHORT);
//                    toast.show();
//                }else{
//                    /**
//                     * 如果订菜数大于目前的上菜数量，因此要更新serve和display的数据
//                     */
//                    getGroup(groupPosition).getOrder_serve().put(add,display+1);
//                    getGroup(groupPosition).getOrder_display().put(add,display+1);
//                    notifyDataSetChanged();
//                    if (book==display+1){
//                        plus.setVisibility(View.GONE);
//                        MarkFinished.setBackgroundResource(R.mipmap.finished);
//                        notifyDataSetChanged();
//                    }
//                    //注意设置隐藏或者不能访问
//                }
//            }
//        });
//        NumberOfDishes.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,64));
//        NumberOfDishes.setTextSize(20);
//        NumberOfDishes.setText(getGroup(groupPosition).getOrder_display().get(getChild(groupPosition,childPosition).firstKey().toString()).toString());
//        linearLayouts.addView(textView);linearLayouts.addView(MarkFinished);linearLayouts.addView(linearLayout);
//        linearLayout.addView(minus);linearLayout.addView(NumberOfDishes);linearLayout.addView(plus);
//        return linearLayouts;
//    }
}
