package com.softmobile.netbschartof3institutionalinvestors.base;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    boolean m_bIsTSE = false; //目前是哪個按鈕

    LinearLayout llOuter; //最外層layout
    LinearLayout llTop; //SurfaceView的layout
    LinearLayout llBotOuter; //下層最外層
    LinearLayout llBot; //ListView的layout
    LinearLayout llProgress; //loading
    FrameLayout  flMask; //按鈕遮罩
    TextView tvMask;

    CircularProgressButton btnProTSE;
    CircularProgressButton btnProOTC;
    CircleProgressBar progressBg;

    SSurfaceGraph sfvMyGraph;
    DynamicListView dyListView;
    SListAdapter myAdapter;

    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.author_name);

        initView();

        Display display = getWindowManager().getDefaultDisplay();

        //判斷直向橫向
        if(display.getWidth() > display.getHeight()){
            //最外層layout改為水平
            llOuter.setOrientation(LinearLayout.HORIZONTAL);
            //比重設為1.0f
            llBotOuter.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1.0f));
        }else{
            llOuter.setOrientation(LinearLayout.VERTICAL);
            llBotOuter.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    2.5f));
        }

        //判斷是否有儲存資料
        if(null != savedInstanceState){
            SData.s_alDataList = (ArrayList) savedInstanceState.getSerializable("Data");
            //判斷資料數是否大於0
            if(0 < SData.s_alDataList.size()) {
                STool.addAllArray();
                createListView();
                createGraphView();
                m_bIsTSE = savedInstanceState.getBoolean("Btn");
            }else{
                //判斷是否有網路
                censorInternet(SData.TAG_TSE, SData.TAGURL_TSE);
            }
        }else{
            //判斷是否有網路
            censorInternet(SData.TAG_TSE, SData.TAGURL_TSE);
        }
        changeBtnColor();
    }

    private void initView(){
        llOuter    = (LinearLayout) findViewById(R.id.llOuter);
        llTop      = (LinearLayout) findViewById(R.id.llTop);
        llBot      = (LinearLayout) findViewById(R.id.llBot);
        llBotOuter = (LinearLayout) findViewById(R.id.llBotOuter);
        llProgress = (LinearLayout) findViewById(R.id.llProgress);
        flMask     = (FrameLayout)  findViewById(R.id.flMask);
        btnProTSE  = (CircularProgressButton) findViewById(R.id.btnProTSE);
        btnProOTC  = (CircularProgressButton) findViewById(R.id.btnProOTC);
        tvMask     = (TextView) findViewById(R.id.tvMask);
        dyListView = (DynamicListView) findViewById(R.id.dylistview);
        progressBg = (CircleProgressBar) findViewById(R.id.progressWithoutBg);

        progressBg.setColorSchemeResources(R.color.ListViewDivider);

        //監聽Button Click
        btnProTSE.setOnClickListener(new SBtnOnClickListener());
        btnProOTC.setOnClickListener(new SBtnOnClickListener());

        //監聽ListView Click
        dyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myAdapter.changeClickColor(position);
                sfvMyGraph.MyDraw(position, -1);
            }
        });

        //監聽刷新按鈕
        tvMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判斷是否有網路
                censorInternet(SData.TAG_TSE, SData.TAGURL_TSE);
            }
        });

    }

    class SBtnOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            SData.clearAllData();
            switch (v.getId()){
                case R.id.btnProTSE:
                    changeBtnColor();
                    censorInternet(SData.TAG_TSE, SData.TAGURL_TSE);
                    break;
                case R.id.btnProOTC:
                    changeBtnColor();
                    censorInternet(SData.TAG_OTC, SData.TAGURL_OTC);
                    break;
            }
        }
    }

    //修改按鈕顏色及開關
    private void changeBtnColor(){
        m_bIsTSE = !m_bIsTSE;
        btnProTSE.setEnabled(!m_bIsTSE);
        btnProOTC.setEnabled(m_bIsTSE);
    }

    //撈資料 & 解析XML
    private class SGetData extends AsyncTask<String, Void, Void> {

        private String strBtn;

        private SGetData(String str){
            this.strBtn = str;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            llProgress.setVisibility(View.VISIBLE); //顯示進度條
            flMask.setVisibility(View.VISIBLE); //顯示按鈕透明遮罩
            dyListView.setAdapter(null);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                String strRes = STool.getDataForURL(params[0]);
                STool.resolveXML(strRes);
            } catch (URISyntaxException | IOException | XmlPullParserException e) {
                e.printStackTrace();
            }

            STool.addAllArray(); //產生之後必要資料

            return null;
        }

        @Override
        protected void onPostExecute(Void vd) {
            createListView();
            createGraphView();

            thread = new SThread(strBtn);
            thread.start();

            llProgress.setVisibility(View.GONE); //隱藏進度條
            tvMask.setVisibility(View.GONE); //隱藏刷新文字
        }
    }

    //產生ListView
    private void createListView(){
        myAdapter = new SListAdapter(MainActivity.this);

        SwingRightInAnimationAdapter animAdapter = new SwingRightInAnimationAdapter(myAdapter);
        animAdapter.setAbsListView(dyListView);

        dyListView.setAdapter(animAdapter);
    }

    //產生圖表
    private void createGraphView(){
        llTop.removeAllViews();
        sfvMyGraph = new SSurfaceGraph(MainActivity.this);
        llTop.addView(sfvMyGraph);

        //Touch監聽
        sfvMyGraph.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sfvMyGraph.MyDraw(-1, (int) event.getX());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        sfvMyGraph.MyDraw(-1, (int) event.getX());
                        break;
                }
                return true;
            }
        });

        //監聽畫圖時的callBack
        sfvMyGraph.setCallBackGraphListener(new SSurfaceGraph.CallBackGraph() {
            @Override
            public void callBack(int i) {
                myAdapter.changeClickColor(i);
            }
        });
    }

    //修改按鈕進度條
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(SData.TAG_TSE.equals(msg.getData().getString("name"))){
                btnProOTC.setProgress(msg.getData().getInt("end"));
            }else{
                btnProTSE.setProgress(msg.getData().getInt("end"));
            }

            //如果收到此數值 將按鈕上方透明遮罩關掉
            if(400 == msg.getData().getInt("delay")){
                flMask.setVisibility(View.GONE);
            }
        }
    };

    private class SThread extends Thread {

        private String strName;

        private SThread(String str) {
            this.strName = str;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            try {
                Bundle countBundle = null;
                Message msg = null;

                for (int i = 1; i <= 100; i++) {
                    Thread.sleep(5);
                    //因按鈕變化有動畫 此sleep是為了讓動畫結束在開始跑進度條
                    if (2 == i) {
                        Thread.sleep(550);
                    }
                    countBundle = new Bundle();
                    countBundle.putString("name", strName);
                    countBundle.putInt("end", i);
                    msg = new Message();

                    if (100 == i) {
                        countBundle.putInt("end", 0);
                        msg.setData(countBundle);
                        handler.sendMessage(msg);

                        //同起始動畫 延遲些許時間在將遮罩關掉
                        Thread.sleep(400);
                        countBundle.putInt("delay", 400);
                        msg = new Message();
                        msg.setData(countBundle);
                        handler.sendMessage(msg);
                        break;
                    }

                    msg.setData(countBundle);
                    handler.sendMessage(msg);
                }

            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //轉向時儲存必要資料
        outState.putSerializable("Data", SData.s_alDataList);
        outState.putBoolean("Btn", !m_bIsTSE);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止執行緒
        if (null != thread) {
            if (!thread.isInterrupted()) {
                thread.interrupt();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnProTSE.setProgress(0);
        btnProOTC.setProgress(0);
    }

    public void censorInternet(String strBtn, String strURL){
        ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()){
            //有網路
            SData.clearAllData();
            new SGetData(strBtn).execute(strURL);
        }else{
            //無網路
            llProgress.setVisibility(View.VISIBLE); //顯示進度條
            tvMask.setVisibility(View.VISIBLE);  //顯示刷新文字
            Toast.makeText(MainActivity.this, R.string.internet_error, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

}
