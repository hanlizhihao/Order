package com.hlz.adapter;

/**
 * Created by Hanlizhi on 2016/10/16.
 */
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.hlz.entity.ShoppingCart;
import com.hlz.order.R;
import com.hlz.util.addGreens;
import com.hlz.util.SortModel;

import java.util.List;

public class SortAdapter extends BaseAdapter implements SectionIndexer{
    private List<SortModel> list = null;
    private Context mContext;
    private ImageView orderCart;//购物车图标
    private RelativeLayout relativeLayout;
    private ShoppingCart shoppingCart;//购物车
    private TextView sumSize;//总数
    private TextView sumPrice;//总价
    /**接下来的值用于标识购物车图标是否改变
     * 逻辑是如果点一次plus则对这个值加1，当点minus则对这个值减1，如果减一之后为0，再更改购物车的图标
     */
    public SortAdapter(Context mContext, List<SortModel> list, ImageView imageView, RelativeLayout relativeLayout
    ,TextView sumSize,TextView sumPrice) {
        this.mContext = mContext;
        this.list = list;
        this.orderCart=imageView;
        this.relativeLayout=relativeLayout;
        this.shoppingCart=new ShoppingCart(mContext);
        this.sumSize=sumSize;
        this.sumPrice=sumPrice;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<SortModel> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        final int[] SingleSize = {0};//单菜品选择的数量
        final int[] CartChangedSign = {0};
        final SortModel mContent = list.get(position);
        view = LayoutInflater.from(mContext).inflate(R.layout.item_makeorder, null);
        final TextView tvTitle = (TextView) view.findViewById(R.id.title);//具体是那个菜
        final TextView tvLetter = (TextView) view.findViewById(R.id.catalog);
        final ImageButton[] tvButton = {(ImageButton) view.findViewById(R.id.make_order)};
        final ImageButton[] deleteOrder = {(ImageButton) view.findViewById(R.id.delete_order)};
        final TextView OrderSingleSize=(TextView)view.findViewById(R.id.make_order_single_size);
        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(section)){
            tvLetter.setVisibility(View.VISIBLE);
            tvLetter.setText(mContent.getSortLetters());
        }else{
            tvLetter.setVisibility(View.GONE);
        }
        /**
         * 表示这行的菜品Name
         */
        tvTitle.setText(this.list.get(position).getName());
        final String order_name=this.list.get(position).getName();
        tvButton[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 动画，向购物车添加菜品，更新设置购物车图标及其附近的总价总数在这里！
                 */
                addGreens anim=new addGreens(mContext);
                anim.init((ImageButton) v,orderCart);
                relativeLayout.addView(anim);
                anim.initView();
                if (CartChangedSign[0] >0){
                }
                else{
                    //更改购物车图标
                    orderCart.setBackgroundResource(R.mipmap.cart_pressed);
                }
                deleteOrder[0].setVisibility(View.VISIBLE); //设置删除图标显示
                CartChangedSign[0] = CartChangedSign[0] +1;
                //接下来添加菜品
                Boolean result=shoppingCart.addSingleSize(tvTitle.getText().toString());
                if (result){
                    String SumNumber=mContext.getResources().getString(R.string.sumSize)
                            +shoppingCart.getOrder_size().toString();
                    sumSize.setText(SumNumber);
                    String sumPriceNumber=mContext.getResources().getString(R.string.sumPrice)+
                            Double.toString(Math.round(shoppingCart.getPrice()*100)/100.0)+"元";
                    sumPrice.setText(sumPriceNumber);
                    SingleSize[0] = SingleSize[0] +1;
                    OrderSingleSize.setText(Integer.toString(SingleSize[0]));
                    Log.d("TAG","添加成功");
                }else
                {
                    Log.d("TAG","添加失败");
                }
            }
        });
        deleteOrder[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 点击删除，减少总价和数量，减少购物车，判断是否是最后一个，如果是则隐藏删除图标，并更改标识
                 * deleteShow
                 */
                Integer SingleSize=shoppingCart.findSingleSize(order_name);
                if (null==SingleSize){
                    Toast toast=Toast.makeText(mContext,"您还没有点这个菜，不用删除",Toast.LENGTH_SHORT);
                    toast.show();
                    deleteOrder[0].setVisibility(View.GONE);
                }else{
                    if (SingleSize==1){
                        deleteOrder[0].setVisibility(View.GONE);
                        CartChangedSign[0] = CartChangedSign[0] -1;
                        if (CartChangedSign[0] <=0){
                            orderCart.setBackgroundResource(R.mipmap.cart);
                        }
                        //接下来删除购物车里的菜品
                        Boolean result=shoppingCart.deleteSingleSize(tvTitle.getText().toString());
                        if (result){
                            String sumsize=mContext.getResources().getString(R.string.sumSize)+
                                    shoppingCart.getOrder_size().toString();
                            sumSize.setText(sumsize);
                            //这里输出总价，使用了格式化double，保留小数点后两位
                            String sumprice=mContext.getResources().getString(R.string.sumPrice)+
                                    Double.toString(Math.round(shoppingCart.getPrice()*100)/100.0)+"元";
                            sumPrice.setText(sumprice);
                            SingleSize=SingleSize-1;
                            OrderSingleSize.setText(Integer.toString(SingleSize));
                            if (SingleSize==0)
                            {
                                OrderSingleSize.setText("");
                            }
                            Log.d("TAG","删除成功");
                        }else
                        {
                            Log.d("TAG","删除失败");
                        }
                    }else{
                        CartChangedSign[0] = CartChangedSign[0] -1;
                        if (CartChangedSign[0] <=0){
                            orderCart.setBackgroundResource(R.mipmap.cart);
                        }
                        Boolean result=shoppingCart.deleteSingleSize(tvTitle.getText().toString());
                        if (result){
                            String sumsize=mContext.getResources().getString(R.string.sumSize)
                                    +shoppingCart.getOrder_size().toString();
                            sumSize.setText(sumsize);
                            String sumprice=mContext.getResources().getString(R.string.sumPrice)
                                    +Double.toString(Math.round(shoppingCart.getPrice()*100)/100.0)+"元";
                            sumPrice.setText(sumprice);
                            SingleSize=SingleSize-1;
                            OrderSingleSize.setText(Integer.toString(SingleSize));
                            if (SingleSize==0)
                            {
                                OrderSingleSize.setText("");
                            }
                            Log.d("TAG","删除成功");
                        }else
                        {
                            Log.d("TAG","删除失败");
                        }
                    }
                }
            }
        });
        return view;
    }
    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String  sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}
