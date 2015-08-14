package com.softmobile.netbschartof3institutionalinvestors.base;


import android.content.Context;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class STool {

    public static final String TAGURL_TSE = "http://iwow.systex.com.tw/webService/ThreeCommOverBS_TSE.ashx";
    public static final String TAGURL_OTC = "http://iwow.systex.com.tw/webService/ThreeCommOverBS_OTC.ashx";
    public static final String TAG_URL    = "URL";
    public static final String TAG_SYM    = "Symbol";
    public static final String TAG_QFII   = "qfiiNetAmount";
    public static final String TAG_BRK    = "brkNetAmount";
    public static final String TAG_IT     = "itNetAmount";
    public static final String TAG_DATE   = "date";

    public static ArrayList<HashMap<String,String>> alDataList = new ArrayList<>();
    public static LinkedHashSet<String> s_lhsDayOfMonth = new LinkedHashSet<>();
    public static LinkedHashSet<Double> s_lhsSum = new LinkedHashSet<>();
    public static LinkedHashSet<Double> s_lhsQfii = new LinkedHashSet<>();
    public static LinkedHashSet<Double> s_lhsBrk = new LinkedHashSet<>();
    public static LinkedHashSet<Double> s_lhsIt = new LinkedHashSet<>();
    public static double s_dbMaxNumber = 0;

    public static void setAlData(ArrayList alData){
        alDataList = alData;
    }

    public static ArrayList getAlData(){
        return alDataList;
    }

    public static double getRound(double dbNum){
        return Double.parseDouble(String.format("%.2f",dbNum));
    }

    public static void setTextColor(Context context, TextView tv){
        double tvText = Double.parseDouble(tv.getText().toString());
        if(0 < tvText){
            tv.setTextColor(context.getResources().getColor(R.color.Num_Positive));
        } else {
            tv.setTextColor(context.getResources().getColor(R.color.Num_Negative));
        }
    }

    public static String setDateZero(int iNum){
        String strReturn = String.valueOf(iNum);
        if(10 > iNum){
            strReturn = 0 + strReturn;
        }
        return strReturn;
    }

    public static void addDayArray(String strDay){
        s_lhsDayOfMonth.add(strDay);
    }

    public static LinkedList<String> changeHsFirstLast(LinkedHashSet lhsData){
        Iterator<String> iter = lhsData.iterator();
        LinkedList<String> lklDay = new LinkedList<>();
        while (iter.hasNext()){
            lklDay.addFirst(iter.next());
        }
        return lklDay;
    }

    public static void censorMaxNumber(double dbNum){
        if(Math.abs(dbNum) >= s_dbMaxNumber){
            s_dbMaxNumber = Math.abs(dbNum);
        }
    }

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

    public static void addAlSum(double dbNum){
        s_lhsSum.add(dbNum);
    }

    public static void addAlQfii(double dbNum){
        s_lhsQfii.add(dbNum);
    }

    public static void addAlBrk(double dbNum){
        s_lhsBrk.add(dbNum);
    }

    public static void addAlIt(double dbNum){
        s_lhsIt.add(dbNum);
    }
    public static double getPillarLength(double dbNum){
        return (dbNum / s_dbMaxNumber) * 4;

    }

    public static void clearAllData(){
        s_dbMaxNumber = 0;
        s_lhsSum.clear();
        s_lhsQfii.clear();
        s_lhsBrk.clear();
        s_lhsIt.clear();
    }

}
