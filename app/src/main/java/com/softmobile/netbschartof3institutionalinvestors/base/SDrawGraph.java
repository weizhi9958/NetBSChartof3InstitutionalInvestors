package com.softmobile.netbschartof3institutionalinvestors.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.ListIterator;


public class SDrawGraph extends SurfaceView implements SurfaceHolder.Callback {

    int m_iColumnSpace;
    int m_iTextSize;
    int m_iColumnWeight;
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
        MyDraw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void MyDraw(){
        canvas = sfholder.lockCanvas(); //鎖住畫布

        m_iColumnSpace = 10; //柱子要分成幾等分

        //柱子寬度 = View總寬度 — ( 柱子間距 * 總數量 ) / ( 總數量 + 1 )
        m_iColumnWeight = (int)((sfView.getWidth() - (m_iColumnSpace * STool.alDataList.size())) / (STool.alDataList.size() + 1));

        m_iTextSize = m_iColumnWeight / 3; //文字大小

        //柱子高度 = ( View總高度 - 文字大小 ) / 等分
        m_iColumnHeight = (int)((sfView.getHeight() - m_iTextSize) / m_iColumnSpace);

        m_iNowX = 0; //目前x座標

        p = new Paint();
        //清除畫板
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(p);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        p.setTextSize(m_iTextSize);

        //將陣列前後翻轉
        literDay = STool.changeHsFirstLast(STool.s_lhsDayOfMonth).listIterator();
        literQfii = STool.changeHsFirstLast(STool.s_lhsQfii).listIterator();
        literBrk = STool.changeHsFirstLast(STool.s_lhsBrk).listIterator();
        literIt = STool.changeHsFirstLast(STool.s_lhsIt).listIterator();
        literSum = STool.changeHsFirstLast(STool.s_lhsSum).listIterator();

        m_iLastQfiiXY = new int[2];
        m_iLastBrkXY = new int[2];
        m_iLastItXY = new int[2];
        m_iNowXY = new int[2];
        String strTouchSum = "";

        while (literDay.hasNext()) {
            //更新X座標 = 目前座標 + 柱子寬度 + 柱子間距
            m_iNowX += m_iColumnWeight + m_iColumnSpace;

            //畫日期 X軸: 目前x軸 + ( 柱寬 / 4 )  Y軸: 9.4個柱高
            p.setColor(Color.WHITE);
            canvas.drawText(String.valueOf(literDay.next()), m_iNowX + (m_iColumnWeight / 4), m_iColumnHeight * 9.4f, p);


            p.setColor(getResources().getColor(R.color.Column_OtherBg));
            //如果是最後一筆畫筆改成黑
            if (false == literDay.hasNext()) {
                p.setColor(getResources().getColor(R.color.Black));
            }

            //畫圖表背景 ( 目前x軸, 文字大小, 目前x軸 + 柱寬, 9個柱高)
            //ps:文字大小是為了讓柱子起始點在於文字下方
            Rect r = new Rect(m_iNowX, m_iTextSize, m_iNowX + m_iColumnWeight, m_iColumnHeight * 9);
            canvas.drawRect(r, p);


            String strNowSum = String.valueOf(literSum.next());
            //計算目前總數佔最大值中的比例
            m_dbPillarLength = STool.getPillarLength(Double.parseDouble(strNowSum));
            if (0 < m_dbPillarLength) {
                p.setColor(getResources().getColor(R.color.Pillar_Top));
            } else {
                p.setColor(getResources().getColor(R.color.Pillar_Bot));
            }

            //依點擊座標畫黃色柱子
            if(STool.s_iNowTouchX >= m_iNowX && STool.s_iNowTouchX <= m_iNowX + m_iColumnWeight){
                p.setColor(getResources().getColor(R.color.ColumnTouch));
                strTouchSum = strNowSum;
            }

            //畫加總柱狀圖  目前x軸, 文字大小 + 4個柱高, 目前x軸 + 柱寬, 文字大小 + ( 柱高 * ( 4 + 所佔比例 ) )
            Rect rt = new Rect(m_iNowX, m_iTextSize + (m_iColumnHeight * 4), m_iNowX + m_iColumnWeight, (int) (m_iTextSize + (m_iColumnHeight * (4 + -m_dbPillarLength))));
            canvas.drawRect(rt, p);

            //畫點及連線
            drawPointAndLine(literQfii,m_iLastQfiiXY,getResources().getColor(R.color.PaintRed));
            drawPointAndLine(literBrk, m_iLastBrkXY, getResources().getColor(R.color.PaintOrange));
            drawPointAndLine(literIt, m_iLastItXY, getResources().getColor(R.color.PaintGreen));
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

        //畫點擊顯示合計文字
        p.setTextSize(m_iColumnWeight);
        if(false == "".equals(strTouchSum)) {
            p.setColor(STool.getTextColor(getContext(),strTouchSum));
            canvas.drawText(strTouchSum, (int)(sfView.getWidth() / 2.5f), m_iColumnHeight, p);
        }

        sfholder.unlockCanvasAndPost(canvas);
    }


    private void drawPointAndLine(ListIterator literItem, int[] iLastXY, int color){
        //畫點和線
        double dbPillarLength = STool.getPillarLength(Double.parseDouble(String.valueOf(literItem.next())));
        p.setColor(color);
        p.setStrokeWidth(2.5f); //線寬
        //取得目前的XY座標
        m_iNowXY[0] = m_iNowX + (m_iColumnWeight / 2);
        m_iNowXY[1] = m_iTextSize + (m_iColumnHeight * 4) + (int) (m_iColumnHeight *  -dbPillarLength);

        //如果有上筆XY就畫線
        if(0 != iLastXY[0] && 0 != iLastXY[1]){
            canvas.drawLine(iLastXY[0],iLastXY[1],m_iNowXY[0],m_iNowXY[1],p);
        }
        //將目前座標儲存供下次使用
        iLastXY[0] = m_iNowXY[0];
        iLastXY[1] = m_iNowXY[1];

        canvas.drawCircle(m_iNowXY[0], m_iNowXY[1], m_iColumnWeight / 10, p);
    }
}
