package com.softmobile.netbschartof3institutionalinvestors.base;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class STool {



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

    //設置所有必要資料
    public static void addAllArray(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");;
        Calendar calendar = Calendar.getInstance();
        Date date;
        double dbQfii;
        double dbBrk;
        double dbIt;
        double dbSum;

        for(int i = 0; i < SData.s_alDataList.size(); i++){

            //轉成日期型態
            try {
                date = simpleDateFormat.parse(SData.s_alDataList.get(i).get(SData.TAG_DATE));
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            dbQfii = getRound(Double.parseDouble(SData.s_alDataList.get(i).get(SData.TAG_QFII)));
            dbBrk  = getRound(Double.parseDouble(SData.s_alDataList.get(i).get(SData.TAG_BRK)));
            dbIt   = getRound(Double.parseDouble(SData.s_alDataList.get(i).get(SData.TAG_IT)));
            dbSum  = getRound(dbQfii + dbBrk + dbIt);

            //每個數字去判斷是否最大值
            censorMaxNumber(dbQfii);
            censorMaxNumber(dbBrk);
            censorMaxNumber(dbIt);
            censorMaxNumber(dbSum);

            SData.addMonth(STool.setDateZero(calendar.get(Calendar.MONTH) + 1));
            SData.addDay(STool.setDateZero(calendar.get(Calendar.DAY_OF_MONTH)));
            SData.addQfii(dbQfii);
            SData.addBrk(dbBrk);
            SData.addIt(dbIt);
            SData.addSum(dbSum);
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
        if(Math.abs(dbNum) >= SData.s_dbMaxNumber){
            SData.s_dbMaxNumber = Math.abs(dbNum);
        }
    }

    //取得左方範圍數值陣列
    public static ArrayList<Double> getLeftNumber(){
        ArrayList<Double> alLeftNum = new ArrayList<>();
        double dbNum = SData.s_dbMaxNumber;
        double dbNumSpace = SData.s_dbMaxNumber / 4;
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
        return (dbNum / SData.s_dbMaxNumber) * 4;
    }


}
