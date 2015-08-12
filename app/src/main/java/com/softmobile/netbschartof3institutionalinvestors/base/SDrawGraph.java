package com.softmobile.netbschartof3institutionalinvestors.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.HashMap;


public class SDrawGraph extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceView sfView;
    SurfaceHolder sfholder;
    Canvas canvas;
    ArrayList<HashMap<String, String>> alDataList;
    int m_iColumnSpace = 10;

    public SDrawGraph(Context context, SurfaceHolder sfh, SurfaceView sfv,ArrayList alData) {
        super(context);
        sfholder = sfh;
        sfView = sfv;
        alDataList = alData;
        sfholder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        //在 canvas 畫布上貼圖的三個步驟

        //1. 鎖住畫布
        canvas = sfholder.lockCanvas();

        //柱子寬度 = View總寬度 — ( 柱子間距 * 總數量 ) / ( 總數量 + 1 )
        int iColumnWeight = (int)((sfView.getWidth() - (m_iColumnSpace * alDataList.size())) / (alDataList.size() + 1));
        int iColumnHeight = (int)(sfView.getHeight() / 10) * 9;
        int iNowX = 0;
        //2. 在畫布上貼圖
        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.Column_OtherBg));

        for(int i = 0; i < alDataList.size(); i++){
            //如果是最後一個將畫筆改成黑色
            if(alDataList.size()-1 == i){
                p.setColor(getResources().getColor(R.color.Black));
            }

            //更新X座標 = 目前座標 + 柱子寬度 + 柱子間距
            iNowX += iColumnWeight + m_iColumnSpace;
            Rect r = new Rect(iNowX, 0, iNowX + iColumnWeight, iColumnHeight);
            canvas.drawRect(r, p);

        }



        //3. 解鎖並po出畫布
        sfholder.unlockCanvasAndPost(canvas);


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
