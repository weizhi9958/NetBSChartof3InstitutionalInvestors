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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SListAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater layoutInflater = null;
    private ArrayList<HashMap<String, String>> alMyDataList;
    private double m_dbQfii;
    private double m_dbBrk;
    private double m_dbIt;
    private double m_dbSum;

    public SListAdapter(Context context, ArrayList alData){
        this.context = context;
        this.layoutInflater = layoutInflater.from(context);
        this.alMyDataList   = alData;
    }

    @Override
    public int getCount() {
        return alMyDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class SViewHold
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
        convertView = layoutInflater.inflate(R.layout.listview_item, null);
        SViewHold viewHold = null;
        int iBackColor = -1;
        if(position == 0){
            iBackColor = context.getResources().getColor(R.color.Black);
        }else if (position % 2 == 0) {
            iBackColor = context.getResources().getColor(R.color.ListViewOdd);
        }else{
            iBackColor = context.getResources().getColor(R.color.ListViewEven);
        }

        if(viewHold == null){
            viewHold = new SViewHold();
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

        m_dbQfii = getRound(Double.parseDouble(alMyDataList.get(position).get(MainActivity.TAG_QFII)));
        m_dbBrk  = getRound(Double.parseDouble(alMyDataList.get(position).get(MainActivity.TAG_BRK)));
        m_dbIt   = getRound(Double.parseDouble(alMyDataList.get(position).get(MainActivity.TAG_IT)));
        m_dbSum  = getRound(m_dbQfii + m_dbBrk + m_dbIt);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        Calendar c = Calendar.getInstance();
        Date dt = null;
        try {
            dt = sdf.parse(alMyDataList.get(position).get(MainActivity.TAG_DATE));
            c.setTime(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        viewHold.tvDate.setText(setDateZero(c.get(Calendar.MONTH) + 1) + "/" + setDateZero(c.get(Calendar.DAY_OF_MONTH)));
        viewHold.tvQfiiNet.setText(String.valueOf(m_dbQfii));
        viewHold.tvBrkNet.setText(String.valueOf(m_dbBrk));
        viewHold.tvItNet.setText(String.valueOf(m_dbIt));
        viewHold.tvSum.setText(String.valueOf(m_dbSum));
        viewHold.llListBack.setBackgroundColor(iBackColor);

        setTextColor(viewHold.tvQfiiNet);
        setTextColor(viewHold.tvBrkNet);
        setTextColor(viewHold.tvItNet);
        setTextColor(viewHold.tvSum);


        return convertView;
    }


    private double getRound(double dbNum){
        return Double.parseDouble(String.format("%.2f",dbNum));
    }

    private void setTextColor(TextView tv){
        double tvText = Double.parseDouble(tv.getText().toString());
        if(0 < tvText){
            tv.setTextColor(context.getResources().getColor(R.color.Num_Positive));
        } else {
            tv.setTextColor(context.getResources().getColor(R.color.Num_Negative));
        }
    }

    private String setDateZero(int iNum){
        String strReturn = String.valueOf(iNum);
        if(10 > iNum){
            strReturn = 0 + strReturn;
        }
        return strReturn;
    }

}
