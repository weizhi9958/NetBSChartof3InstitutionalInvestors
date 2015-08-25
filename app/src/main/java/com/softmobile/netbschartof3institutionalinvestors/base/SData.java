package com.softmobile.netbschartof3institutionalinvestors.base;


import java.util.ArrayList;
import java.util.HashMap;

public class SData {

    public static final String TAG_TSE    = "TSE";
    public static final String TAG_OTC    = "OTC";
    public static final String TAGURL_TSE = "http://iwow.systex.com.tw/webService/ThreeCommOverBS_TSE.ashx";
    public static final String TAGURL_OTC = "http://iwow.systex.com.tw/webService/ThreeCommOverBS_OTC.ashx";
    public static final String TAG_SUM    = "Symbol";
    public static final String TAG_QFII   = "qfiiNetAmount";
    public static final String TAG_BRK    = "brkNetAmount";
    public static final String TAG_IT     = "itNetAmount";
    public static final String TAG_DATE   = "date";

    public static ArrayList<HashMap<String,String>> s_alDataList = new ArrayList<>();

    private static ArrayList<String> s_alMonth = new ArrayList<>();
    private static ArrayList<String> s_alDay = new ArrayList<>();
    private static ArrayList<Double> s_alQfii = new ArrayList<>();
    private static ArrayList<Double> s_alBrk = new ArrayList<>();
    private static ArrayList<Double> s_alIt = new ArrayList<>();
    private static ArrayList<Double> s_alSum = new ArrayList<>();

    public static double s_dbMaxNumber = 0;

    //清除所有資料
    public static void clearAllData(){
        s_dbMaxNumber = 0;
        s_alDataList.clear();
        s_alMonth.clear();
        s_alDay.clear();
        s_alQfii.clear();
        s_alBrk.clear();
        s_alIt.clear();
        s_alSum.clear();
    }

    //存入月份
    public static void addMonth(String strData){
        s_alMonth.add(strData);
    }

    //存入日
    public static void addDay(String strData){
        s_alDay.add(strData);
    }

    //存入外資
    public static void addQfii(double dbData){
        s_alQfii.add(dbData);
    }

    //存入自營
    public static void addBrk(double dbData){
        s_alBrk.add(dbData);
    }

    //存入投信
    public static void addIt(double dbData){
        s_alIt.add(dbData);
    }

    //存入合計
    public static void addSum(double dbData){
        s_alSum.add(dbData);
    }

    //取得月份
    public static String getMonth(int iPos){
        return s_alMonth.get(iPos);
    }

    //取得日
    public static String getDay(int iPos){
        return s_alDay.get(iPos);
    }

    //取得外資
    public static String getQfii(int iPos){
        return String.valueOf(s_alQfii.get(iPos));
    }

    //取得自營
    public static String getBrk(int iPos){
        return String.valueOf(s_alBrk.get(iPos));
    }

    //取得投信
    public static String getIt(int iPos){
        return String.valueOf(s_alIt.get(iPos));
    }

    //取得合計
    public static String getSum(int iPos){
        return String.valueOf(s_alSum.get(iPos));
    }


}
