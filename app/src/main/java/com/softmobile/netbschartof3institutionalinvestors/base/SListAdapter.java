package com.softmobile.netbschartof3institutionalinvestors.base;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SListAdapter extends BaseAdapter{

    private int m_iTouchPos = -1;
    private Context context;
    private LayoutInflater layoutInflater = null;

    public SListAdapter(Context context){
        this.context = context;
        this.layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return SData.s_alDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class SViewHold
    {
        LinearLayout llListBack;
        TextView tvDate;
        TextView tvQfiiNet;
        TextView tvBrkNet;
        TextView tvItNet;
        TextView tvSum;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SViewHold viewHold = null;
        int iBackColor = -1;

        if(null == convertView){
            viewHold = new SViewHold();
            convertView = layoutInflater.inflate(R.layout.listview_item, parent, false);
            viewHold.llListBack = (LinearLayout) convertView.findViewById(R.id.llListBack);
            viewHold.tvDate     = (TextView) convertView.findViewById(R.id.tvDate);
            viewHold.tvQfiiNet  = (TextView) convertView.findViewById(R.id.tvQfiiNet);
            viewHold.tvBrkNet   = (TextView) convertView.findViewById(R.id.tvBrkNet);
            viewHold.tvItNet    = (TextView) convertView.findViewById(R.id.tvItNet);
            viewHold.tvSum      = (TextView) convertView.findViewById(R.id.tvSum);

            convertView.setTag(viewHold);
        }else{
            viewHold = (SViewHold)convertView.getTag();
        }

        //判斷奇偶列顏色及第一筆顏色
        if(0 == position){
            iBackColor = context.getResources().getColor(R.color.Black);
        }else if (0 == position % 2) {
            iBackColor = context.getResources().getColor(R.color.ListViewOdd);
        }else{
            iBackColor = context.getResources().getColor(R.color.ListViewEven);
        }

        //設置每個TextView文字
        viewHold.tvDate.setText(SData.getMonth(position) + "/" + SData.getDay(position));
        viewHold.tvQfiiNet.setText(SData.getQfii(position));
        viewHold.tvBrkNet.setText(SData.getBrk(position));
        viewHold.tvItNet.setText(SData.getIt(position));
        viewHold.tvSum.setText(SData.getSum(position));
        viewHold.llListBack.setBackgroundColor(iBackColor);

        //依正負值判斷TextView顏色
        STool.setTextColor(context, viewHold.tvQfiiNet);
        STool.setTextColor(context, viewHold.tvBrkNet);
        STool.setTextColor(context, viewHold.tvItNet);
        STool.setTextColor(context, viewHold.tvSum);

        //目前點擊改顏色
        if(m_iTouchPos == position){
            viewHold.llListBack.setBackgroundColor(context.getResources().getColor(R.color.ColumnTouch));
        }

        return convertView;
    }

    //修改點擊顏色
    public void changeClickColor(int iPos){
        m_iTouchPos = iPos;
        notifyDataSetChanged();
    }

}
