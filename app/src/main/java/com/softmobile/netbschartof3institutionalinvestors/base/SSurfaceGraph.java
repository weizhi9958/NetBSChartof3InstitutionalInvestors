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


public class SSurfaceGraph extends SurfaceView implements SurfaceHolder.Callback{

    int m_iColumnSpace;  //柱子等分
    int m_iTextSize;     //文字大小
    int m_iColumnWeight; //柱子寬度
    int m_iColumnHeight; //柱子高度
    int m_iNowX;         //目前畫到的X軸
    int m_iLastQfiiXY[]; //上一個外資XY軸
    int m_iLastBrkXY[];  //上一個投信XY軸
    int m_iLastItXY[];   //上一個自營XY軸
    int m_iNowXY[];      //目前XY軸

    double m_dbProportion; //佔最大值中的比例

    Paint paint;

    SurfaceHolder sfHolder;
    CallBackGraph callBackGraph;

    Canvas canvas;


    public interface CallBackGraph{
        void callBack(int i);
    }

    public SSurfaceGraph(Context context) {
        super(context);
        sfHolder = this.getHolder();
        sfHolder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        MyDraw(-1, -1);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }


    public void setCallBackGraphListener(CallBackGraph callBackGraph){
        this.callBackGraph = callBackGraph;
    }




    public void MyDraw(int iListViewPos, int iSurfViewX){

        canvas = sfHolder.lockCanvas(); //鎖住畫布
        m_iColumnSpace = 10; //柱子要分成幾等分
        //柱子寬度 = View總寬度 — ( 柱子間距 * 總數量 ) / ( 總數量 + 1 )
        m_iColumnWeight = (int)((this.getWidth() -
                (m_iColumnSpace * SData.s_alDataList.size())) /
                (SData.s_alDataList.size() + 1));

        m_iTextSize = (int)(m_iColumnWeight / 2.6f); //左方文字大小

        //柱子高度 = ( View總高度 - 文字大小 ) / 等分
        m_iColumnHeight = (int)((this.getHeight() - m_iTextSize) / m_iColumnSpace);

        m_iNowX = 0; //目前x座標

        m_iLastQfiiXY = new int[2];
        m_iLastBrkXY = new int[2];
        m_iLastItXY = new int[2];
        m_iNowXY = new int[2];

        paint = new Paint();
        paint.setAntiAlias(true);
        //清除畫板
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        String strTouchSum = ""; //存放點擊到的合計數值
        int iSize = SData.s_alDataList.size();

//        STool.sla.changeClikcColor(-1); //預設ListView不變黃色
        if(null != callBackGraph) {
            callBackGraph.callBack(-1);
        }

        for(int i = iSize - 1; i >= 0; i--){
            //更新X座標 = 目前座標 + 柱子寬度 + 柱子間距
            m_iNowX += m_iColumnWeight + m_iColumnSpace;

            //畫日期 X軸: 目前x軸 + ( 柱寬 / 4 )  Y軸: 9.4個柱高
            paint.setTextSize(m_iColumnWeight / 2);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.WHITE);
            canvas.drawText(SData.getDay(i),
                    m_iNowX + (int)(m_iColumnWeight * 0.5f),
                    m_iColumnHeight * 9.4f,
                    paint);


            paint.setColor(getResources().getColor(R.color.Column_OtherBg));
            //如果是最後一筆畫筆改成黑
            if (0 == i) {
                paint.setColor(getResources().getColor(R.color.Black));
            }

            //畫圖表背景 ( 目前x軸, 文字大小, 目前x軸 + 柱寬, 9個柱高)
            //ps:文字大小是為了讓柱子起始點在於文字下方
            Rect r = new Rect(m_iNowX, m_iTextSize, m_iNowX + m_iColumnWeight, m_iColumnHeight * 9);
            canvas.drawRect(r, paint);

            String strNowSum = SData.getSum(i); //存放目前Sum值
            //計算目前總數佔最大值中的比例
            m_dbProportion = STool.getProportion(Double.parseDouble(strNowSum));

            //修改正負柱子顏色
            if (0 < m_dbProportion) {
                paint.setColor(getResources().getColor(R.color.Pillar_Top));
            } else {
                paint.setColor(getResources().getColor(R.color.Pillar_Bot));
            }

            //依點擊座標 或 點擊ListView 畫黃色柱子 及 黃色ListView背景
            if((iSurfViewX >= m_iNowX && iSurfViewX <= m_iNowX + m_iColumnWeight) ||
                iListViewPos == i){
                paint.setColor(getResources().getColor(R.color.ColumnTouch));

                strTouchSum = strNowSum;

                if(null != callBackGraph){
                    callBackGraph.callBack(i);
                }

            }

            //畫加總柱狀圖  目前x軸, 文字大小 + 4個柱高, 目前x軸 + 柱寬, 文字大小 + ( 柱高 * ( 4 + 所佔比例 ) )
            Rect rt = new Rect(m_iNowX,
                    m_iTextSize + (m_iColumnHeight * 4),
                    m_iNowX + m_iColumnWeight,
                    (int) (m_iTextSize + (m_iColumnHeight * (4 + -m_dbProportion))));

            canvas.drawRect(rt, paint);

            //畫點及連線
            drawPointAndLine(SData.getQfii(i), m_iLastQfiiXY, getResources().getColor(R.color.PaintRed));
            drawPointAndLine(SData.getBrk(i), m_iLastBrkXY, getResources().getColor(R.color.PaintOrange));
            drawPointAndLine(SData.getIt(i), m_iLastItXY, getResources().getColor(R.color.PaintGreen));

        }

        //畫左方文字
        paint.setTextSize(m_iTextSize);
        paint.setTextAlign(Paint.Align.RIGHT);
        ArrayList<Double> alLeftNum = STool.getLeftNumber();
        paint.setColor(Color.WHITE);
        for(int i = 0; i < alLeftNum.size(); i++){
            if(0 == i){
                canvas.drawText(getResources().getString(R.string.e),
                        m_iColumnWeight,
                        (int)((m_iColumnHeight * (i + 0.7)) + m_iTextSize),
                        paint);
            }
            canvas.drawText(String.valueOf(alLeftNum.get(i)),
                    m_iColumnWeight,
                    (m_iColumnHeight * i) + (int)(m_iTextSize * 1.5),
                    paint);
        }

        //畫點擊顯示合計文字
        paint.setTextSize(m_iColumnWeight);
        paint.setTextAlign(Paint.Align.CENTER);
        if(false == "".equals(strTouchSum)) {
            paint.setColor(STool.getTextColor(getContext(), strTouchSum));
            canvas.drawText(strTouchSum, this.getWidth() / 2, m_iColumnHeight * 1.5f, paint);
        }

        sfHolder.unlockCanvasAndPost(canvas); //解鎖並顯示畫布
    }

    private void drawPointAndLine(String strNext, int[] iLastXY, int color){
        //畫點和線
        double dbProportion = STool.getProportion(Double.parseDouble(strNext));
        paint.setColor(color);
        paint.setStrokeWidth(2.5f); //線寬
        //取得目前的XY座標
        m_iNowXY[0] = m_iNowX + (m_iColumnWeight / 2);
        m_iNowXY[1] = m_iTextSize + (m_iColumnHeight * 4) + (int) (m_iColumnHeight *  -dbProportion);

        //如果有上筆XY就畫線
        if(0 != iLastXY[0] && 0 != iLastXY[1]){
            canvas.drawLine(iLastXY[0],iLastXY[1],m_iNowXY[0],m_iNowXY[1],paint);
        }
        //將目前座標儲存供下次使用
        iLastXY[0] = m_iNowXY[0];
        iLastXY[1] = m_iNowXY[1];

        canvas.drawCircle(m_iNowXY[0], m_iNowXY[1], m_iColumnWeight / 10, paint);
    }


}
