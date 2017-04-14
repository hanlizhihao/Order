package com.hlz.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hlz.entity.ItemShoppingCart;
import com.hlz.entity.ShoppingCart;
import com.hlz.order.R;

import java.util.List;

/**
 * 购物车弹窗 Adapter
 * Created by hlz on 2017/4/13 0013.
 */

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder>{
    private ShoppingCart shoppingCart;
    private List<ItemShoppingCart> data;
    private Context context;
    private TextView sumSize;
    private TextView sumPrice;
    public ShoppingCartAdapter (ShoppingCart shoppingCart,Context context,TextView sumSize,TextView sumPrice){
        this.shoppingCart=shoppingCart;
        this.context=context;
        data=shoppingCart.getItemShoppingCart();
        this.sumPrice=sumPrice;
        this.sumSize=sumSize;
    }
    @Override
    public ShoppingCartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View normal_views = LayoutInflater.from(context).inflate(
                R.layout.item_shopping_cart, parent, false);
        return new ShoppingCartAdapter.ViewHolder(normal_views);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(data.get(position).getName());
        holder.number.setText(data.get(position).getNumber());
        holder.price.setText(Double.toString(data.get(position).getPrice()));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shoppingCart.deleteSingleSize(data.get(position).getName());
                data=shoppingCart.getItemShoppingCart();
                sumPrice.setText("总价："+shoppingCart.getPrice().toString());
                sumSize.setText("总数："+shoppingCart.getOrder_size().toString());
                notifyDataSetChanged();
            }
        });
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shoppingCart.addSingleSize(data.get(position).getName());
                data=shoppingCart.getItemShoppingCart();
                sumPrice.setText(shoppingCart.getPrice().toString());
                sumSize.setText(shoppingCart.getOrder_size().toString());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView price;
        ImageButton delete;
        ImageButton add;
        TextView number;
        ViewHolder(View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.shopping_cart_item_name);
            price=(TextView)itemView.findViewById(R.id.shopping_cart_item_price);
            delete=(ImageButton)itemView.findViewById(R.id.shopping_cart_item_delete);
            add=(ImageButton)itemView.findViewById(R.id.shopping_cart_item_add);
            number=(TextView)itemView.findViewById(R.id.shopping_cart_item_number);
        }
    }
    public List<ItemShoppingCart> getData(){
        return data;
    }
}
