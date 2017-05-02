package com.hlz.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.hlz.activity.MainActivity;
import com.hlz.activity.UnderwayDetailsActivity;
import com.hlz.entity.Indent;
import com.hlz.net.NetworkUtil;
import com.hlz.order.MyApplication;
import com.hlz.order.R;
import com.hlz.util.StringUtil;
import com.tapadoo.alerter.Alerter;

import java.util.List;

/**
 * Created by hlz on 2017/2/9
 */
public class UnderwayAdapter extends RecyclerView.Adapter<UnderwayAdapter.ViewHolder> {
    private List<Indent> indents;//数据源
    private MainActivity activity;
    private final String TAG="UnderwayAdapter";
    public UnderwayAdapter(MainActivity activity,List<Indent> indents){
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
        View normal_views = LayoutInflater.from(MyApplication.getContext()).inflate(
                R.layout.fragment_item_underway, parent, false);
        return new ViewHolder(normal_views);
    }
    @Override
    public void onBindViewHolder(final UnderwayAdapter.ViewHolder holder, final int position) {
        holder.tableID.setText(indents.get(position).getTableId());
        holder.reminderNumber.setText(indents.get(position).getReminderNumber().toString());
        holder.waitTime.setAnimateType(HTextViewType.TYPER);
        Long beginTime=indents.get(position).getBeginTime();
        Long firstTime=indents.get(position).getFirstTime();
        if (firstTime==null){
            holder.waitTime.animateText(StringUtil.change(System.currentTimeMillis()-beginTime));
        }else{
            holder.waitTime.animateText(StringUtil.change(firstTime-beginTime));
        }
        holder.servingProgress.setText(indents.get(position).getFulfillNumber()+"/"+indents.get(position).getReserveNumber());
        //为列表的每行元素设置点击启动activity
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Indent indent=indents.get(position);
                Intent intent=new Intent(activity,UnderwayDetailsActivity.class);
                intent.putExtra("tableID",indent.getTableId());
                intent.putExtra("id",indent.getId().toString());
                intent.putExtra("fulfill",indent.getFulfill());
                intent.putExtra("reserve",indent.getReserve());
                intent.putExtra("reminderNumber",indent.getReminderNumber().toString());
                intent.putExtra("firstTime",indent.getFirstTime().toString());
                intent.putExtra("price",indent.getPrice().toString());
                activity.startActivityForResult(intent,0);//将会获取Activity返回的结果
            }
        });
        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Indent indent=indents.get(position);
                Intent intent=new Intent(activity,UnderwayDetailsActivity.class);
                intent.putExtra("tableID",indent.getTableId());
                intent.putExtra("id",indent.getId().toString());
                intent.putExtra("fulfill",indent.getFulfill());
                intent.putExtra("reserve",indent.getReserve());
                intent.putExtra("reminderNumber",indent.getReminderNumber().toString());
                if (indent.getFirstTime()!=null){
                    intent.putExtra("firstTime",indent.getFirstTime().toString());
                }
                intent.putExtra("price",indent.getPrice().toString());
                activity.startActivityForResult(intent,0);//将会获取Activity返回的结果
            }
        });
        final Response.Listener<String> updateIndentListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String o) {
                if ("success".equals(o)){
                    indents.get(position).setReminderNumber(indents.get(position).getReminderNumber()+1);
                    Toast.makeText(MyApplication.getContext(),"催单成功",Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }else{
                    Alerter.create(activity)
                            .setBackgroundColor(R.color.colorLightBlue)
                            .setTitle("催单失败！")
                            .setText("服务器端异常！")
                            .setDuration(2000)
                            .show();
                }
                activity.hideWaitDialog();
            }
        };
        final Response.ErrorListener errorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                activity.hideWaitDialog();
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
                activity.showWaitDialog("正在催单...");
                NetworkUtil networkUtil=NetworkUtil.getNetworkUtil();
                //只发送id，由后台处理催单
                networkUtil.reminder(indents.get(position).getId().toString(),updateIndentListener,errorListener,TAG);
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
