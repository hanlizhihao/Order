package com.hlz.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.hlz.activity.MainActivity;
import com.hlz.activity.UnderwayDetailsActivity;
import com.hlz.entity.Indent;
import com.hlz.entity.TestDinnerTable;
import com.hlz.net.NetworkUtil;
import com.hlz.order.R;
import com.hlz.util.TransformLongToString;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hlz on 2017/2/9
 */
public class UnderwayAdapter extends RecyclerView.Adapter<UnderwayAdapter.ViewHolder> {
    private List<Indent> indents;//数据源
    private FragmentActivity activity;
    private final String TAG="UnderwayAdapter";
    public UnderwayAdapter(FragmentActivity activity,List<Indent> indents){
        this.indents=indents;
        this.activity=activity;
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        //正在进行的列表的子视图
        TextView tableID;
        TextView servingProgress;
        HTextView waitTime;
        TextView reminderNumber;
        //对列表中的每个行元素设置监听器
        LinearLayout layout;
        Button reminder;
        Button details;
        ViewHolder(View itemView) {
            super(itemView);
            layout=(LinearLayout)itemView.findViewById(R.id.item_underway_content);
            tableID=(TextView)itemView.findViewById(R.id.table_id);
            servingProgress=(TextView)itemView.findViewById(R.id.serving_progress);
            waitTime=(HTextView)itemView.findViewById(R.id.wait_time);
            reminderNumber=(TextView)itemView.findViewById(R.id.reminder_number);
            reminder=(Button)itemView.findViewById(R.id.reminder);
            details=(Button)itemView.findViewById(R.id.underway_details);
        }
    }
    @Override
    public UnderwayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View normal_views = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.fragment_item_underway, parent, false);
        return new ViewHolder(normal_views);
    }
    @Override
    public void onBindViewHolder(final UnderwayAdapter.ViewHolder holder, final int position) {
        holder.tableID.setText(indents.get(position).getTableId());
        holder.reminderNumber.setText(indents.get(position).getReminderNumber());
        holder.waitTime.setAnimateType(HTextViewType.TYPER);
        Long beginTime=indents.get(position).getBeginTime();
        Long firstTime=indents.get(position).getFirstTime();
        if (firstTime==null){
            holder.waitTime.animateText(TransformLongToString.change(System.currentTimeMillis()-beginTime));
        }else{
            holder.waitTime.animateText(TransformLongToString.change(firstTime-beginTime));
        }
        holder.servingProgress.setText(indents.get(position).getReserveNumber()+"/"+indents.get(position).getReserveNumber());
        //为列表的每行元素设置点击启动activity
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Indent indent=indents.get(position);
                Intent intent=new Intent(activity,UnderwayDetailsActivity.class);
                intent.putExtra("tableID",indent.getTableId());
                intent.putExtra("id",indent.getId().toString());
                intent.putExtra("fulFill",indent.getFulfill());
                intent.putExtra("reserve",indent.getReserve());
                intent.putExtra("reminderNumber",indent.getReminderNumber());
                activity.startActivity(intent);
            }
        });
        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Indent indent=indents.get(position);
                Intent intent=new Intent(activity,UnderwayDetailsActivity.class);
                intent.putExtra("tableID",indent.getTableId());
                intent.putExtra("id",indent.getId());
                intent.putExtra("fulFill",indent.getFulfill());
                intent.putExtra("reserve",indent.getReserve());
                intent.putExtra("reminderNumber",indent.getReminderNumber());
                activity.startActivity(intent);
            }
        });
        final Response.Listener updateIndentListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String o) {
                if ("success".equals(o)){
                    indents.get(position).setReminderNumber(indents.get(position).getReminderNumber()+1);
                    notifyDataSetChanged();
                }else{
                    Alerter.create(activity)
                            .setBackgroundColor(R.color.colorLightBlue)
                            .setTitle("催单失败！")
                            .setText("服务器端异常！")
                            .setDuration(2000)
                            .show();
                }
            }
        };
        final Response.ErrorListener errorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Alerter.create(activity)
                        .setBackgroundColor(R.color.colorLightBlue)
                        .setTitle("催单失败！")
                        .setText("您可能与服务器不在一个网络次元！")
                        .setDuration(2000)
                        .show();
            }
        };
        //催单
        holder.reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkUtil networkUtil=NetworkUtil.getNetworkUtil();
                networkUtil.updateIndent(updateIndentListener,errorListener,TAG);
            }
        });
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return indents.size();
    }
    public void setIndents(List<Indent> indents){
        this.indents=indents;
    }
    public List<Indent> getIndents(){
        return indents;
    }
}
