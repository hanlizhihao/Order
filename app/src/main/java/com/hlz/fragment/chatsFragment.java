package com.hlz.fragment;

/**
 * Created by Administrator on 2016/9/8.
  */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hlz.entity.DinnerTable;
import com.hlz.entity.TestDinnerTable;
import com.hlz.order.R;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class chatsFragment extends Fragment
{

    private ExpandableListView expandableListView;
    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.chats_fragment,container,false) ;
        expandableListView=(ExpandableListView)v.findViewById(R.id.expandList);
        ExpandableListAdapter adapter=new BaseExpandableListAdapter() {
            TestDinnerTable dinnerTable=new TestDinnerTable();
            private TextView getTextView(){//设定TextView的样式
                AbsListView.LayoutParams lp=new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,64);
                TextView textView=new TextView(getActivity());
                textView.setLayoutParams(lp);
                textView.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);
                textView.setPadding(36,0,0,0);
                textView.setTextSize(20);
                return textView;
            }
            @Override
            public int getGroupCount() {
                return dinnerTable.getDinnerTables().size();//组数据长度
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return dinnerTable.getDinnerTables().get(groupPosition).getOrder_book().size();//子组件的长度
            }

            @Override
            public DinnerTable getGroup(int groupPosition) {
                return dinnerTable.getDinnerTables().get(groupPosition);//组数据
            }

            //获取子组的菜单
            @Override
            public TreeMap getChild(int groupPosition, int childPosition) {
                /**
                 * 如果有序的Map映射出来是有序的set，则这个方法是可行的，否则就出错
                 */
                TreeMap<String,Integer> result=new TreeMap<>();
                int i=0;
                for (Map.Entry<String,Integer> entry:dinnerTable.getDinnerTables().get(groupPosition).getOrder_display().entrySet()
                     ) {
                    if (childPosition==i){
                        String k=entry.getKey();
                        Integer v=entry.getValue();
                        result.put(k,v);
                        break;
                    }
                    i=i+1;
                }
                return result;
            }

            @Override
            public long getGroupId(int i) {
                return i;
            }

            @Override
            public long getChildId(int i, int i1) {
                return i1;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            /**
             * 这个函数返回了组数据的视图
             * @param groupPosition 组位置
             * @param isExpanded 是否扩展
             * @param convertView 参数
             * @param viewGroup 父容器
             * @return 组数据项要显示的视图
             */
            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup viewGroup) {
                LinearLayout linearLayouts=new LinearLayout(getActivity());//整个布局
                LinearLayout linerLayoutOne=new LinearLayout(getActivity());//第一行数据
                LinearLayout linerLayoutTwo=new LinearLayout(getActivity());//第二行数据
                linerLayoutOne.setOrientation(LinearLayout.HORIZONTAL);//设置排列方式
                linearLayouts.setOrientation(LinearLayout.VERTICAL);
                linerLayoutTwo.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup
                .LayoutParams.WRAP_CONTENT);
                //初始化数据
                TextView table_id=getTextView();
                TextView table_id_value=getTextView();
                TextView greens_plan=getTextView();
                TextView greens_plan_value=getTextView();
                TextView wait_time=getTextView();
                TextView wait_time_value=getTextView();
                TextView number_reminder=getTextView();
                TextView number_reminder_value=getTextView();
                table_id.setText("桌号：");linerLayoutOne.addView(table_id,params);//将组件添加到水平布局中
                table_id_value.setText(getGroup(groupPosition).getTable_id());
                linerLayoutOne.addView(table_id_value,params);
                greens_plan.setText("上菜进度：");linerLayoutOne.addView(greens_plan,params);
                String NumberOfServe,NumberOfBook;
                /**
                 * 获取Map集合的value值，并将它们相加得到NumberOfServe
                 */
                Collection<Integer> CollectionNumberServe=dinnerTable.getDinnerTables().get(groupPosition).getOrder_serve().values();
                int sum=0;
                for (Integer i:CollectionNumberServe
                     ) {
                    sum=i+sum;
                }
                NumberOfServe=Integer.toString(sum);
                /**
                 * 获取order_book的Map集合的value值，得到NumberOfBook
                 */
                Collection<Integer> CollectionNumberBook=dinnerTable.getDinnerTables().get(groupPosition).getOrder_book().values();
                sum=0;
                for (Integer j:CollectionNumberBook
                     ) {
                    sum=j+sum;
                }
                NumberOfBook=Integer.toString(sum);
                String p0=NumberOfServe+"/"+NumberOfBook;
                greens_plan_value.setText(p0);
                linerLayoutOne.addView(greens_plan_value,params);
                wait_time.setText("等待时间：");linerLayoutTwo.addView(wait_time,params);
                wait_time_value.setText(getGroup(groupPosition).getTime_wait() + "分钟");
                linerLayoutTwo.addView(wait_time_value,params);
                number_reminder.setText("催单次数：");linerLayoutTwo.addView(number_reminder,params);
                number_reminder_value.setText(Integer.toString(getGroup(groupPosition).getNumber_reminder()));
                linerLayoutTwo.addView(number_reminder_value,params);
                linearLayouts.addView(linerLayoutOne);linearLayouts.addView(linerLayoutTwo);
                return linearLayouts ;
            }

            @Override
            public View getChildView(final int groupPosition, final int childPosition, boolean isLastchild, View view, ViewGroup viewGroup) {
                LinearLayout linearLayouts=new LinearLayout(getActivity());
                linearLayouts.setOrientation(LinearLayout.HORIZONTAL);
                linearLayouts.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                /**
                 * 子数据中textView的配置
                 */
                TextView textView=new TextView(getActivity());
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,64));
                textView.setGravity(Gravity.START);textView.setText(getChild(groupPosition,childPosition).toString());
                int color=R.color.black;
                textView.setTextSize(20);textView.setTextColor(getResources().getColor(color));
                //利用getChild返回的参数设定
                textView.setText(getChild(groupPosition,childPosition).firstKey().toString());
                /**
                 * 子数据中表上没上完这个菜的图标的配置
                 * 思路是比对book和serve相同key部分的value值是否相同
                 * 因此也引出了另一个问题，就是如何设定+号上限的问题
                 */
                final ImageButton MarkFinished=new ImageButton(getActivity());
                MarkFinished.setLayoutParams(new ViewGroup.LayoutParams(64,64));
                String key=getChild(groupPosition,childPosition).firstKey().toString();
                int serve=(int)getChild(groupPosition,childPosition).values().iterator().next();
                int book=getGroup(groupPosition).getOrder_book().get(key);
                /**
                 * 设定大于等于，考虑兼容性（可能程序错误导致脏数据问题）
                 */
                if (serve>=book)
                    {
                        MarkFinished.setBackgroundResource(R.mipmap.finished);
                    }
                else{
                        MarkFinished.setBackgroundResource(R.mipmap.unfinished);
                    }
                /**
                 * 子数据中被包含的LinerLayout的配置
                 */
                LinearLayout linearLayout=new LinearLayout(getActivity());
                linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayout.setGravity(Gravity.END);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                /**
                 * 子LinerLayout包含的组件两个ImageButton和一个TextView的配置
                 */
                ImageButton minus=new ImageButton(getActivity());
                final ImageButton plus=new ImageButton(getActivity());
                //这个变量是菜品数量
                TextView NumberOfDishes=new TextView(getActivity());
                /**
                 * 给减号绑定事件监听与参数设置
                 */
                minus.setLayoutParams(new ViewGroup.LayoutParams(64,64));
                minus.setBackgroundResource(R.mipmap.plan_minus);
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /**
                         * 弹窗提示是否删除这个菜品
                         */
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("提示");
                        builder.setMessage("您确定要删除这个菜吗？");
                        builder.setIcon(R.drawable.z);
                        DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (arg1 == DialogInterface.BUTTON_POSITIVE) {
                                    arg0.cancel();
                                } else if (arg1 == DialogInterface.BUTTON_NEGATIVE) {
                                    /**
                                     * 根据getChild返回的key,value来删除实体中的一个映射关系
                                     */
                                    TreeMap map=getChild(groupPosition,childPosition);
                                    String remove=map.firstKey().toString();
                                    getGroup(groupPosition).getOrder_display().remove(remove);
                                    getGroup(groupPosition).getOrder_book().remove(remove);
                                    getGroup(groupPosition).getOrder_serve().remove(remove);
                                    notifyDataSetChanged();
                                }
                            }
                        };
                        builder.setPositiveButton("取消", dialog);
                        builder.setNegativeButton("确定", dialog);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
                plus.setLayoutParams(new ViewGroup.LayoutParams(64,64));
                plus.setBackgroundResource(R.mipmap.plan_plus);
                /**
                 * 给加号绑定事件监听器
                 */
                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String add=getChild(groupPosition,childPosition).firstKey().toString();
                        int book=getGroup(groupPosition).getOrder_book().get(add);int display=getGroup(groupPosition).getOrder_display().get(add);
                        if (book<=display)
                        {
                            Toast toast=Toast.makeText(getActivity(),"这个菜已经上完了",Toast.LENGTH_SHORT);
                            toast.show();
                        }else{
                            /**
                             * 如果订菜数大于目前的上菜数量，因此要更新serve和display的数据
                             */
                            getGroup(groupPosition).getOrder_serve().put(add,display+1);
                            getGroup(groupPosition).getOrder_display().put(add,display+1);
                            notifyDataSetChanged();
                            if (book==display+1){
                               plus.setVisibility(View.GONE);
                                MarkFinished.setBackgroundResource(R.mipmap.finished);
                                notifyDataSetChanged();
                            }
                            //注意设置隐藏或者不能访问
                        }
                    }
                });
                NumberOfDishes.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,64));
                NumberOfDishes.setTextSize(20);
                NumberOfDishes.setText(getGroup(groupPosition).getOrder_display().get(getChild(groupPosition,childPosition).firstKey().toString()).toString());
                linearLayouts.addView(textView);linearLayouts.addView(MarkFinished);linearLayouts.addView(linearLayout);
                linearLayout.addView(minus);linearLayout.addView(NumberOfDishes);linearLayout.addView(plus);
                return linearLayouts;
            }
            @Override
            public boolean isChildSelectable(int i, int i1) {
                return true;
            }
        };
        expandableListView.setAdapter(adapter);
        return v ;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
