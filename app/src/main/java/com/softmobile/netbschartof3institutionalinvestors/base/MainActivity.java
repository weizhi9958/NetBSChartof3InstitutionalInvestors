package com.softmobile.netbschartof3institutionalinvestors.base;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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

public class MainActivity extends AppCompatActivity {

    boolean bIsTSE = true;

    LinearLayout llOuter;
    LinearLayout llTop;
    LinearLayout llBot;
    LinearLayout llBotOuter;

    Button btnTSE;
    Button btnOTC;

    ListFragment listFragment;
    GraphFragment graphFragment;

    ProgressDialog pgdDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Display display = getWindowManager().getDefaultDisplay();
        if(display.getWidth() > display.getHeight()){
            llOuter.setOrientation(LinearLayout.HORIZONTAL);
            llBotOuter.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1.0f));
        }else{
            llOuter.setOrientation(LinearLayout.VERTICAL);
            llBotOuter.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,2.0f));
        }
        FragmentManager frm = getFragmentManager();
        FragmentTransaction ft = frm.beginTransaction();
        ft.replace(R.id.llTop, new GraphFragment());
        ft.commit();
        frm.executePendingTransactions();
    }

    private void initView(){
        llOuter    = (LinearLayout) findViewById(R.id.llOuter);
        llTop      = (LinearLayout) findViewById(R.id.llTop);
        llBot      = (LinearLayout) findViewById(R.id.llBot);
        llBotOuter = (LinearLayout) findViewById(R.id.llBotOuter);
        btnTSE     = (Button) findViewById(R.id.btnTSE);
        btnOTC     = (Button) findViewById(R.id.btnOTC);

        btnTSE.setOnClickListener(new SBtnOnClickListener());
        btnOTC.setOnClickListener(new SBtnOnClickListener());

        listFragment  = new ListFragment();
        graphFragment = new GraphFragment();

        new SGetData(new ListFragment(),new GraphFragment()).execute(STool.TAGURL_TSE);

        changBtnColor();
    }

    class SBtnOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnTSE:
                    STool.clearAllData();
                    bIsTSE = true;
                    changBtnColor();

                    new SGetData(new ListFragment(),new GraphFragment()).execute(STool.TAGURL_TSE);

                    break;
                case R.id.btnOTC:
                    STool.clearAllData();
                    bIsTSE = false;
                    changBtnColor();

                    new SGetData(new ListFragment(),new GraphFragment()).execute(STool.TAGURL_OTC);

                    break;
            }
        }
    }

    private void changBtnColor(){
        btnTSE.setEnabled(!bIsTSE);
        btnOTC.setEnabled(bIsTSE);
    }


   private class SGetData extends AsyncTask<String, Void, Void> {

       ListFragment listFragment = null;
       GraphFragment graphFragment = null;

        private SGetData(ListFragment lf, GraphFragment gf){
            this.listFragment = lf;
            this.graphFragment = gf;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pgdDialog = new ProgressDialog(MainActivity.this);
            pgdDialog.setCancelable(false);
            pgdDialog.show();
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
                            if(strName.equals(STool.TAG_SYM)){
                                map = new HashMap<String, String>();
                            }
                            if(strName.equals(STool.TAG_QFII)){
                                map.put(strName, xpp.nextText());
                            }
                            if(strName.equals(STool.TAG_BRK)){
                                map.put(strName, xpp.nextText());
                            }
                            if(strName.equals(STool.TAG_IT)){
                                map.put(strName, xpp.nextText());
                            }
                            if(strName.equals(STool.TAG_DATE)) {
                                map.put(strName, xpp.nextText());
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if(xpp.getName().equals(STool.TAG_SYM)){
                                STool.alDataList.add(map);
                            }
                            break;
                    }
                    iEt = xpp.next();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void vd) {

            FragmentManager frm = getFragmentManager();
            FragmentTransaction ft = frm.beginTransaction();
            ft.replace(R.id.llBot, listFragment);
            ft.replace(R.id.llTop, graphFragment);
            ft.commit();
            frm.executePendingTransactions();

            pgdDialog.dismiss();
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
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
