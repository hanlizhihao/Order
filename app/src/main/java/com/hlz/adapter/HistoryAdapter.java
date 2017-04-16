package com.hlz.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hlz.activity.HistoryDetailsActivity;
import com.hlz.activity.MainActivity;
import com.hlz.entity.Indent;
import com.hlz.order.MyApplication;
import com.hlz.order.R;

import java.util.List;

/**
 * 历史订单适配器
 * Created by hlz on 2017/4/16 0016.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{
    private List<Indent> indents;//数据源
    private final String TAG="UnderwayAdapter";
    private MainActivity activity;
    public HistoryAdapter(List<Indent> indents,MainActivity activity){
        this.indents=indents;
        this.activity=activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View normal_views = LayoutInflater.from(MyApplication.getContext()).inflate(
                R.layout.fragment_item_history, parent, false);
        return new HistoryAdapter.ViewHolder(normal_views);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tableID.setText(indents.get(position).getTableId());
        holder.reminderNumber.setText(indents.get(position).getReminderNumber().toString());
        holder.reserveNumber.setText(indents.get(position).getReserveNumber().toString());
        holder.price.setText(indents.get(position).getPrice().toString()+"元");
        //为列表的每行元素设置点击启动activity
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Indent indent=indents.get(position);
                Intent intent=new Intent(activity,HistoryDetailsActivity.class);
                intent.putExtra("tableID",indent.getTableId());
                intent.putExtra("id",indent.getId());
                intent.putExtra("fulFill",indent.getFulfill());
                intent.putExtra("reserve",indent.getReserve());
                intent.putExtra("reminderNumber",indent.getReminderNumber());
                intent.putExtra("firstTime",indent.getFirstTime());
                intent.putExtra("price",indent.getPrice());
                activity.startActivity(intent);
            }
        });
        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Indent indent=indents.get(position);
                Intent intent=new Intent(activity,HistoryDetailsActivity.class);
                intent.putExtra("tableID",indent.getTableId());
                intent.putExtra("id",indent.getId());
                intent.putExtra("fulFill",indent.getFulfill());
                intent.putExtra("reserve",indent.getReserve());
                intent.putExtra("reminderNumber",indent.getReminderNumber());
                intent.putExtra("firstTime",indent.getFirstTime());
                intent.putExtra("price",indent.getPrice());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return indents.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        //正在进行的列表的子视图
        TextView tableID;
        TextView reserveNumber;
        TextView price;
        TextView reminderNumber;
        //对列表中的每个行元素设置监听器
        LinearLayout layout;
        Button details;
        ViewHolder(View itemView) {
            super(itemView);
            layout=(LinearLayout)itemView.findViewById(R.id.item_history_content);
            tableID=(TextView)itemView.findViewById(R.id.table_id);
            price=(TextView)itemView.findViewById(R.id.history_price);
            reserveNumber=(TextView)itemView.findViewById(R.id.history_reserve_number);
            reminderNumber=(TextView)itemView.findViewById(R.id.reminder_number);
            details=(Button)itemView.findViewById(R.id.history_details);
        }
    }
}
