package com.softmobile.netbschartof3institutionalinvestors.base;


import android.content.Context;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class STool {

    public static final String TAGURL_TSE = "http://iwow.systex.com.tw/webService/ThreeCommOverBS_TSE.ashx";
    public static final String TAGURL_OTC = "http://iwow.systex.com.tw/webService/ThreeCommOverBS_OTC.ashx";
    public static final String TAG_SUM    = "Symbol";
    public static final String TAG_QFII   = "qfiiNetAmount";
    public static final String TAG_BRK    = "brkNetAmount";
    public static final String TAG_IT     = "itNetAmount";
    public static final String TAG_DATE   = "date";

    public static ArrayList<HashMap<String,String>> s_alDataList = new ArrayList<>();

    private static ArrayList<String> s_alDayofMonth = new ArrayList<>();
    private static ArrayList<String> s_alMonth = new ArrayList<>();
    private static ArrayList<Double> s_alQfii = new ArrayList<>();
    private static ArrayList<Double> s_alBrk = new ArrayList<>();
    private static ArrayList<Double> s_alIt = new ArrayList<>();
    private static ArrayList<Double> s_alSum = new ArrayList<>();

    public static double s_dbMaxNumber = 0;

    public static SSurfaceGraph sdg;
    public static SListAdapter sla;

    //小數第二位四捨五入
    public static double getRound(double dbNum){
        return Double.parseDouble(String.format("%.2f",dbNum));
    }

    //傳入TextView文字判斷正負改文字顏色
    public static void setTextColor(Context context, TextView tv){
        double dbText = Double.parseDouble(tv.getText().toString());
        if(0 < dbText){
            tv.setTextColor(context.getResources().getColor(R.color.Num_Positive));
        } else {
            tv.setTextColor(context.getResources().getColor(R.color.Num_Negative));
        }
    }

    //傳入String判斷正負回傳顏色
    public static int getTextColor(Context context, String strNum){
        double dbText = Double.parseDouble(strNum);
        if(0 < dbText){
            return context.getResources().getColor(R.color.Num_Positive);
        } else {
            return context.getResources().getColor(R.color.Num_Negative);
        }
    }

    //日期不足十位數補0
    public static String setDateZero(int iNum){
        String strReturn = String.valueOf(iNum);
        if(10 > iNum){
            strReturn = 0 + strReturn;
        }
        return strReturn;
    }

    //檢查並修改最大數值
    public static void censorMaxNumber(double dbNum){
        if(Math.abs(dbNum) >= s_dbMaxNumber){
            s_dbMaxNumber = Math.abs(dbNum);
        }
    }

    //取得左方範圍數值陣列
    public static ArrayList<Double> getLeftNumber(){
        ArrayList<Double> alLeftNum = new ArrayList<>();
        double dbNum = s_dbMaxNumber;
        double dbNumSpace = s_dbMaxNumber / 4;
        int iCount = 0;
        do {
            alLeftNum.add(getRound(dbNum));
            dbNum -= dbNumSpace;
            iCount++;
        }while (iCount <= 8);

        return alLeftNum;
    }

    //取得與最大值相除後比例
    public static double getProportion(double dbNum){
        return (dbNum / s_dbMaxNumber) * 4;
    }

    //清除所有資料
    public static void clearAllData(){
        s_dbMaxNumber = 0;
        s_alDataList.clear();
        s_alMonth.clear();
        s_alDayofMonth.clear();
        s_alQfii.clear();
        s_alBrk.clear();
        s_alIt.clear();
        s_alSum.clear();
    }

    //設置所有必要資料
    public static void addAllArray(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");;
        Calendar calendar = Calendar.getInstance();
        Date date;
        double dbQfii;
        double dbBrk;
        double dbIt;
        double dbSum;

        for(int i = 0; i < s_alDataList.size(); i++){

            //轉成日期型態
            try {
                date = simpleDateFormat.parse(STool.s_alDataList.get(i).get(STool.TAG_DATE));
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            dbQfii = STool.getRound(Double.parseDouble(STool.s_alDataList.get(i).get(STool.TAG_QFII)));
            dbBrk  = STool.getRound(Double.parseDouble(STool.s_alDataList.get(i).get(STool.TAG_BRK)));
            dbIt   = STool.getRound(Double.parseDouble(STool.s_alDataList.get(i).get(STool.TAG_IT)));
            dbSum  = STool.getRound(dbQfii + dbBrk + dbIt);

            //每個數字去判斷是否最大值
            STool.censorMaxNumber(dbQfii);
            STool.censorMaxNumber(dbBrk);
            STool.censorMaxNumber(dbIt);
            STool.censorMaxNumber(dbSum);

            s_alMonth.add(STool.setDateZero(calendar.get(Calendar.MONTH) + 1));
            s_alDayofMonth.add(STool.setDateZero(calendar.get(Calendar.DAY_OF_MONTH)));
            s_alQfii.add(dbQfii);
            s_alBrk.add(dbBrk);
            s_alIt.add(dbIt);
            s_alSum.add(dbSum);
        }
    }

    //取得月份
    public static String getMonth(int iPos){
        return s_alMonth.get(iPos);
    }

    //取得日
    public static String getDayOf(int iPos){
        return s_alDayofMonth.get(iPos);
    }

    //取得外資
    public static String getQfiiOf(int iPos){
        return String.valueOf(s_alQfii.get(iPos));
    }

    //取得投信
    public static String getBrkOf(int iPos){
        return String.valueOf(s_alBrk.get(iPos));
    }

    //取得自營
    public static String getItOf(int iPos){
        return String.valueOf(s_alIt.get(iPos));
    }

    //取得合計
    public static String getSumOf(int iPos){
        return String.valueOf(s_alSum.get(iPos));
    }



}
