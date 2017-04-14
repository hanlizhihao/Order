package com.hlz.adapter;

/**
 * 决定点菜页面的Adapter
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
import com.hlz.util.AddGreensAnimator;
import com.hlz.util.SortModel;

import java.util.List;

public class SortAdapter extends BaseAdapter implements SectionIndexer{
    private List<SortModel> list = null;
    private Context mContext;
    private ImageButton orderCart;//购物车图标
    private RelativeLayout relativeLayout;
    private ShoppingCart shoppingCart;//购物车
    private TextView sumSize;//总数
    private TextView sumPrice;//总价
    /**接下来的值用于标识购物车图标是否改变
     * 逻辑是如果点一次plus则对这个值加1，当点minus则对这个值减1，如果减一之后为0，再更改购物车的图标
     */
    public SortAdapter(Context mContext, List<SortModel> list, ImageButton imageView, RelativeLayout relativeLayout
    ,TextView sumSize,TextView sumPrice,ShoppingCart shoppingCart) {
        this.mContext = mContext;
        this.list = list;
        this.orderCart=imageView;
        this.relativeLayout=relativeLayout;
        this.shoppingCart=shoppingCart;
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

    public SortModel getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    private class ViewHolder{
        TextView tvTitle;
        TextView tvLetter;
        ImageButton makeOrder;
        ImageButton deleteOrder;
        TextView singleOrderNumber;
    }
    public View getView(final int position, View view, ViewGroup arg2) {
        SortAdapter.ViewHolder viewHolder;
        if (view==null){
            viewHolder=new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_makeorder, null);
            viewHolder.tvTitle=(TextView) view.findViewById(R.id.title);//具体是那个菜
            viewHolder.tvLetter=(TextView) view.findViewById(R.id.catalog);
            viewHolder.makeOrder=(ImageButton) view.findViewById(R.id.make_order);
            viewHolder.deleteOrder=(ImageButton) view.findViewById(R.id.delete_order);
            viewHolder.singleOrderNumber=(TextView)view.findViewById(R.id.single_order_number);
            view.setTag(viewHolder);
        }else{
            viewHolder=(SortAdapter.ViewHolder)view.getTag();
        }
        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(section)){
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(getItem(position).getSortLetters());
        }else{
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        /**
         * 表示这行的菜品Name
         */
        viewHolder.tvTitle.setText(getItem(position).getName());
        Integer single_size=shoppingCart.findSingleSize(list.get(position).getName());
        if (single_size!=null&&0!=single_size){
            viewHolder.deleteOrder.setVisibility(View.VISIBLE);
            viewHolder.singleOrderNumber.setText(single_size.toString());
        }
        final String order_name=list.get(position).getName();
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.makeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 动画，向购物车添加菜品，更新设置购物车图标及其附近的总价总数在这里！
                 */
                orderCart.setBackgroundResource(R.mipmap.cart_pressed);
                AddGreensAnimator anim=new AddGreensAnimator(mContext);
                anim.init((ImageButton) v,orderCart);
                relativeLayout.addView(anim);
                anim.initView();
                finalViewHolder.deleteOrder.setVisibility(View.VISIBLE); //设置删除图标显示
                //接下来添加菜品
                boolean result=shoppingCart.addSingleSize(getItem(position).getName());
                if (result){
                    String SumNumber=mContext.getResources().getString(R.string.sumSize)
                            +shoppingCart.getOrder_size().toString();
                    sumSize.setText(SumNumber);
                    String sumPriceNumber=mContext.getResources().getString(R.string.sumPrice)+
                            Double.toString(Math.round(shoppingCart.getPrice()*100)/100.0)+"元";
                    sumPrice.setText(sumPriceNumber);
                    finalViewHolder.singleOrderNumber.setText(shoppingCart.findSingleSize(getItem(position).getName()).toString());
                    Log.d("TAG","添加成功");
                }else
                {
                    Log.d("TAG","添加失败");
                }
            }
        });
        viewHolder.deleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 点击删除，减少总价和数量，减少购物车，判断是否是最后一个，如果是则隐藏删除图标，并更改标识
                 * deleteShow
                 */
                Integer singleSize=shoppingCart.findSingleSize(order_name);
                if (null==singleSize){
                    Toast toast=Toast.makeText(mContext,"您还没有点这个菜，不用删除",Toast.LENGTH_SHORT);
                    toast.show();
                    finalViewHolder.deleteOrder.setVisibility(View.GONE);
                }else{
                    if (singleSize==1){
                        finalViewHolder.deleteOrder.setVisibility(View.GONE);
                        orderCart.setBackgroundResource(R.mipmap.cart);
                        boolean result=shoppingCart.deleteSingleSize(getItem(position).getName());
                        if (result){
                            String sumNumber=mContext.getResources().getString(R.string.sumSize)+
                                    shoppingCart.getOrder_size().toString();
                            sumSize.setText(sumNumber);
                            //这里输出总价，使用了格式化double，保留小数点后两位
                            String sumPriceNumber=mContext.getResources().getString(R.string.sumPrice)+
                                    Double.toString(Math.round(shoppingCart.getPrice()*100)/100.0)+"元";
                            sumPrice.setText(sumPriceNumber);
                            shoppingCart.deleteSingleSize(getItem(position).getName());
                            finalViewHolder.singleOrderNumber.setText("");
                            Log.d("TAG","删除成功");
                        }else
                        {
                            Log.d("TAG","删除失败");
                        }
                    }else{
                        boolean result=shoppingCart.deleteSingleSize(getItem(position).getName());
                        if (result){
                            String sumNumber=mContext.getResources().getString(R.string.sumSize)
                                    +shoppingCart.getOrder_size().toString();
                            sumSize.setText(sumNumber);
                            String sumPriceNumber=mContext.getResources().getString(R.string.sumPrice)
                                    +Double.toString(Math.round(shoppingCart.getPrice()*100)/100.0)+"元";
                            sumPrice.setText(sumPriceNumber);
                            finalViewHolder.singleOrderNumber.setText(shoppingCart.findSingleSize(getItem(position).getName()).toString());
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
