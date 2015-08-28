package com.softmobile.netbschartof3institutionalinvestors.base;


import android.content.Context;
import android.util.Xml;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class STool {

    //取得資料
    public static String getDataForURL(String strURL) throws URISyntaxException, IOException {

        URI uri = new URI(strURL);
        HttpClient hc = new DefaultHttpClient();
        HttpGet hg = new HttpGet();

        hg.setURI(uri);
        HttpResponse res = hc.execute(hg);

        return EntityUtils.toString(res.getEntity());
    }

    //解析XML
    public static void resolveXML(String strRes) throws IOException, XmlPullParserException {
        XmlPullParser xpp = Xml.newPullParser();
        InputStream inputs = new ByteArrayInputStream(strRes.getBytes());
        xpp.setInput(inputs, "utf-8");
        int iEt = xpp.getEventType();
        HashMap<String, String> map = null;
        String strName = null;
        while (iEt != XmlPullParser.END_DOCUMENT){
            switch (iEt){
                case XmlPullParser.START_TAG:
                    strName = xpp.getName();
                    if(strName.equals(SData.TAG_SUM)){
                        map = new HashMap<String, String>();
                    }
                    if(strName.equals(SData.TAG_QFII)){
                        map.put(strName, xpp.nextText());
                    }
                    if(strName.equals(SData.TAG_BRK)){
                        map.put(strName, xpp.nextText());
                    }
                    if(strName.equals(SData.TAG_IT)){
                        map.put(strName, xpp.nextText());
                    }
                    if(strName.equals(SData.TAG_DATE)) {
                        map.put(strName, xpp.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(xpp.getName().equals(SData.TAG_SUM)){
                        SData.s_alDataList.add(map);
                    }
                    break;
            }
            iEt = xpp.next();
        }

    }

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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
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
