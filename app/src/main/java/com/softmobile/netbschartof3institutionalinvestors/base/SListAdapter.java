package com.softmobile.netbschartof3institutionalinvestors.base;


import android.content.Context;
import android.graphics.Color;
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

    private Context context;
    private LayoutInflater layoutInflater = null;
    private double m_dbQfii;
    private double m_dbBrk;
    private double m_dbIt;
    private double m_dbSum;
    private SimpleDateFormat simpleDateFormat;
    Calendar calendar;
    Date date = null;

    public SListAdapter(Context context){
        this.context = context;
        this.layoutInflater = layoutInflater.from(context);
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        calendar = Calendar.getInstance();
    }

    @Override
    public int getCount() {
        return STool.alDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
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

        if(convertView == null){
            viewHold = new SViewHold();
            convertView = layoutInflater.inflate(R.layout.listview_item, null);
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
        if(position == 0){
            iBackColor = context.getResources().getColor(R.color.Black);
        }else if (position % 2 == 0) {
            iBackColor = context.getResources().getColor(R.color.ListViewOdd);
        }else{
            iBackColor = context.getResources().getColor(R.color.ListViewEven);
        }

        //四捨五入
        m_dbQfii = STool.getRound(Double.parseDouble(STool.alDataList.get(position).get(STool.TAG_QFII)));
        m_dbBrk  = STool.getRound(Double.parseDouble(STool.alDataList.get(position).get(STool.TAG_BRK)));
        m_dbIt   = STool.getRound(Double.parseDouble(STool.alDataList.get(position).get(STool.TAG_IT)));
        m_dbSum  = STool.getRound(m_dbQfii + m_dbBrk + m_dbIt);

        //轉成日期型態
        try {
            date = simpleDateFormat.parse(STool.alDataList.get(position).get(STool.TAG_DATE));
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //設置每個TextView文字
        viewHold.tvDate.setText(STool.setDateZero(calendar.get(Calendar.MONTH) + 1) + "/" + STool.setDateZero(calendar.get(Calendar.DAY_OF_MONTH)));
        viewHold.tvQfiiNet.setText(String.valueOf(m_dbQfii));
        viewHold.tvBrkNet.setText(String.valueOf(m_dbBrk));
        viewHold.tvItNet.setText(String.valueOf(m_dbIt));
        viewHold.tvSum.setText(String.valueOf(m_dbSum));
        viewHold.llListBack.setBackgroundColor(iBackColor);

        //依正負值判斷TextView顏色
        STool.setTextColor(context, viewHold.tvQfiiNet);
        STool.setTextColor(context, viewHold.tvBrkNet);
        STool.setTextColor(context, viewHold.tvItNet);
        STool.setTextColor(context, viewHold.tvSum);

        //依類型依序存入各自ArrayList
        STool.addDayArray(STool.setDateZero(calendar.get(Calendar.DAY_OF_MONTH)));
        STool.addAlQfii(m_dbQfii);
        STool.addAlBrk(m_dbBrk);
        STool.addAlIt(m_dbIt);
        STool.addAlSum(m_dbSum);

        //每個數字去判斷是否最大值
        STool.censorMaxNumber(m_dbQfii);
        STool.censorMaxNumber(m_dbBrk);
        STool.censorMaxNumber(m_dbIt);
        STool.censorMaxNumber(m_dbSum);

        return convertView;
    }

}
