package com.softmobile.netbschartof3institutionalinvestors.base;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

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
import java.util.HashMap;

public class MainActivity extends AppCompatActivity{

    boolean m_bIsTSE = true; //目前是哪個按鈕

    LinearLayout llOuter; //最外層layout
    LinearLayout llTop; //SurfaceView的layout
    LinearLayout llBotOuter; //下層最外層
    LinearLayout llBot; //ListView的layout
    LinearLayout llProgress; //loading

    Button btnTSE;
    Button btnOTC;

    SSurfaceGraph sfvMyGraph;
    DynamicListView dyListView;
    //ListView lvData;
    SListAdapter myAdapter;

    CircleProgressBar progressWithoutBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.author_name);

        initView();

        SData.clearAllData();

        //撈資料
        new SGetData().execute(SData.TAGURL_TSE);
    }

    //螢幕翻轉時
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

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
                    2.0f));
        }

        createGraphView();

    }


    private void initView(){
        llOuter      = (LinearLayout) findViewById(R.id.llOuter);
        llTop        = (LinearLayout) findViewById(R.id.llTop);
        llBot        = (LinearLayout) findViewById(R.id.llBot);
        llBotOuter   = (LinearLayout) findViewById(R.id.llBotOuter);
        llProgress   = (LinearLayout) findViewById(R.id.llProgress);
        btnTSE       = (Button) findViewById(R.id.btnTSE);
        btnOTC       = (Button) findViewById(R.id.btnOTC);
       // lvData       = (ListView) findViewById(R.id.lvData);
        dyListView   = (DynamicListView) findViewById(R.id.dylistview);
        progressWithoutBg = (CircleProgressBar) findViewById(R.id.progressWithoutBg);

        progressWithoutBg.setColorSchemeResources(R.color.ListViewDivider);

        btnTSE.setOnClickListener(new SBtnOnClickListener());
        btnOTC.setOnClickListener(new SBtnOnClickListener());

        dyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //        mainAdapter.changeClikcColor(position);
                sfvMyGraph.MyDraw(position, -1);
            }
        });

        changBtnColor();
    }


    class SBtnOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            SData.clearAllData();
            m_bIsTSE = !m_bIsTSE;
            changBtnColor();

            switch (v.getId()){
                case R.id.btnTSE:
                    new SGetData().execute(SData.TAGURL_TSE);
                    break;
                case R.id.btnOTC:
                    new SGetData().execute(SData.TAGURL_OTC);
                    break;
            }
        }
    }

    private void changBtnColor(){
        btnTSE.setEnabled(!m_bIsTSE);
        btnOTC.setEnabled(m_bIsTSE);
    }


   private class SGetData extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            llProgress.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                URI uri = new URI(params[0]);
                HttpClient hc = new DefaultHttpClient();
                HttpGet hg = new HttpGet();

                hg.setURI(uri);
                HttpResponse res = hc.execute(hg);
                String strXmlData = EntityUtils.toString(res.getEntity());

                XmlPullParser xpp = Xml.newPullParser();
                InputStream inputs = new ByteArrayInputStream(strXmlData.getBytes());
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
                STool.addAllArray(); //產生之後必要資料


            } catch (IOException | URISyntaxException | XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void vd) {

            llProgress.setVisibility(View.INVISIBLE);

             myAdapter = new SListAdapter(MainActivity.this);
            //mainAdapter = lvAdapter;

            SwingRightInAnimationAdapter animAdapter = new SwingRightInAnimationAdapter(myAdapter);

            animAdapter.setAbsListView(dyListView);

            dyListView.setAdapter(animAdapter);

            createGraphView();



        }
    }

    public void createGraphView(){
        llTop.removeAllViews();
        sfvMyGraph = new SSurfaceGraph(MainActivity.this);
        llTop.addView(sfvMyGraph);


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

        sfvMyGraph.setCallBackGraphListener(new SSurfaceGraph.CallBackGraph() {
            @Override
            public void callBack(int i) {
                //myAdapter.changeClikcColor(i);
            }
        });
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
