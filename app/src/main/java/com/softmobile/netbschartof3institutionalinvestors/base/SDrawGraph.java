package com.softmobile.netbschartof3institutionalinvestors.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.ListIterator;


public class SDrawGraph extends SurfaceView implements SurfaceHolder.Callback {

    int m_iColumnSpace;
    int m_iColumnWeight;
    int m_iTextSize;
    int m_iColumnHeight;
    int m_iNowX;
    int m_iLastQfiiXY[];
    int m_iLastBrkXY[];
    int m_iLastItXY[];
    int m_iNowXY[];

    double m_dbPillarLength;

    Paint p;

    ListIterator literDay;
    ListIterator literQfii;
    ListIterator literBrk;
    ListIterator literIt;
    ListIterator literSum;

    SurfaceView sfView;
    SurfaceHolder sfholder;

    Canvas canvas;

    public SDrawGraph(Context context, SurfaceHolder sfh, SurfaceView sfv) {
        super(context);
        sfholder = sfh;
        sfView = sfv;
        sfholder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        canvas = sfholder.lockCanvas();
        m_iColumnSpace = 10;
        //柱子寬度 = View總寬度 — ( 柱子間距 * 總數量 ) / ( 總數量 + 1 )
        m_iColumnWeight = (int)((sfView.getWidth() - (m_iColumnSpace * STool.alDataList.size())) / (STool.alDataList.size() + 1));
        m_iTextSize = m_iColumnWeight / 3;
        m_iColumnHeight = (int)((sfView.getHeight() - m_iTextSize) / 10);
        m_iNowX = 0;

        p = new Paint();
        p.setTextSize(m_iTextSize);
        //化柱子背景及底部文字
        literDay = STool.changeHsFirstLast(STool.s_lhsDayOfMonth).listIterator();
        literQfii = STool.changeHsFirstLast(STool.s_lhsQfii).listIterator();
        literBrk = STool.changeHsFirstLast(STool.s_lhsBrk).listIterator();
        literIt = STool.changeHsFirstLast(STool.s_lhsIt).listIterator();
        literSum = STool.changeHsFirstLast(STool.s_lhsSum).listIterator();

        m_iLastQfiiXY = new int[2];
        m_iLastBrkXY = new int[2];
        m_iLastItXY = new int[2];
        m_iNowXY = new int[2];
        while (literDay.hasNext()) {
            //更新X座標 = 目前座標 + 柱子寬度 + 柱子間距
            m_iNowX += m_iColumnWeight + m_iColumnSpace;

            //畫日期
            p.setColor(Color.WHITE);
            canvas.drawText(String.valueOf(literDay.next()), m_iNowX + (m_iColumnWeight / 4), m_iColumnHeight * 9.4f, p);

            p.setColor(getResources().getColor(R.color.Column_OtherBg));
            //如果是最後一個將畫筆改成黑色
            if (false == literDay.hasNext()) {
                p.setColor(getResources().getColor(R.color.Black));
            }

            //畫圖表背景
            Rect r = new Rect(m_iNowX, m_iTextSize, m_iNowX + m_iColumnWeight, m_iColumnHeight * 9);
            canvas.drawRect(r, p);

            //畫加總柱狀圖
            m_dbPillarLength = STool.getPillarLength(Double.parseDouble(String.valueOf(literSum.next())));
            if (0 < m_dbPillarLength) {
                p.setColor(getResources().getColor(R.color.Pillar_Top));
            } else {
                p.setColor(getResources().getColor(R.color.Pillar_Bot));
            }
            Rect rt = new Rect(m_iNowX, m_iTextSize + (m_iColumnHeight * 4), m_iNowX + m_iColumnWeight, (int) (m_iTextSize + (m_iColumnHeight * (4 + -m_dbPillarLength))));
            canvas.drawRect(rt, p);


            drawPointAndLine(literQfii,m_iLastQfiiXY,getResources().getColor(R.color.PaintRed));
            drawPointAndLine(literBrk,m_iLastBrkXY,getResources().getColor(R.color.PaintOrange));
            drawPointAndLine(literIt,m_iLastItXY,getResources().getColor(R.color.PaintGreen));
        }

        //畫左方文字
        ArrayList<Double> alLeftNum = STool.getLeftNumber();
        p.setColor(Color.WHITE);
        for(int i = 0; i < alLeftNum.size(); i++){
            if(0 == i){
                canvas.drawText(getResources().getString(R.string.e), m_iColumnWeight * 0.5f, (int)((m_iColumnHeight * (i + 0.5)) + m_iTextSize), p);
            }
            canvas.drawText(String.valueOf(alLeftNum.get(i)), 0, (m_iColumnHeight * i) + m_iTextSize, p);
        }



        sfholder.unlockCanvasAndPost(canvas);
    }

    private void drawPointAndLine(ListIterator literItem, int[] iLastXY, int color){
        //畫點和線
        double dbPillarLength = STool.getPillarLength(Double.parseDouble(String.valueOf(literItem.next())));
        p.setColor(color);
        p.setStrokeWidth(2.5f);
        m_iNowXY[0] = m_iNowX + (m_iColumnWeight / 2);
        m_iNowXY[1] = m_iTextSize + (m_iColumnHeight * 4) + (int) (m_iColumnHeight *  -dbPillarLength);

        if(0 != iLastXY[0] && 0 != iLastXY[1]){
            canvas.drawLine(iLastXY[0],iLastXY[1],m_iNowXY[0],m_iNowXY[1],p);
        }
        iLastXY[0] = m_iNowX + (m_iColumnWeight / 2);
        iLastXY[1] = m_iTextSize + (m_iColumnHeight * 4) + (int) (m_iColumnHeight *  -dbPillarLength);

        canvas.drawCircle(m_iNowXY[0], m_iNowXY[1], m_iColumnWeight / 10, p);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

}
