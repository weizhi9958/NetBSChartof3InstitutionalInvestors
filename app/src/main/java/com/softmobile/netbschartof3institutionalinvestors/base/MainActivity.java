package com.softmobile.netbschartof3institutionalinvestors.base;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    public static String TAGURL_TSE = "http://iwow.systex.com.tw/webService/ThreeCommOverBS_TSE.ashx";
    public static String TAGURL_OTC    = "http://iwow.systex.com.tw/webService/ThreeCommOverBS_OTC.ashx";
    public static String TAG_SYM  = "Symbol";
    public static String TAG_QFII = "qfiiNetAmount";
    public static String TAG_BRK  = "brkNetAmount";
    public static String TAG_IT   = "itNetAmount";
    public static String TAG_DATE = "date";

    boolean bIsTSE = true;

    LinearLayout llTop;
    LinearLayout llBot;

    Button btnTSE;
    Button btnOTC;



    ListFragment listTSEFragment;
    ListFragment listOTCFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
        llTop     = (LinearLayout) findViewById(R.id.llTop);
        llBot     = (LinearLayout) findViewById(R.id.llBot);
        btnTSE = (Button) findViewById(R.id.btnTSE);
        btnOTC    = (Button) findViewById(R.id.btnOTC);

        btnTSE.setOnClickListener(new SBtnOnClickListener());
        btnOTC.setOnClickListener(new SBtnOnClickListener());



        listTSEFragment = new ListFragment(MainActivity.this, TAGURL_TSE);
        listOTCFragment = new ListFragment(MainActivity.this, TAGURL_OTC);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.llBot, listTSEFragment, "bot");
        ft.commit();
        changBtnColor();
    }

    class SBtnOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnTSE:
                    STool.clearAllData();
                    FragmentTransaction ftTSE = getFragmentManager().beginTransaction();
                    ftTSE.replace(R.id.llBot,listTSEFragment).commit();
                    bIsTSE = true;
                    changBtnColor();
                    break;
                case R.id.btnOTC:
                    STool.clearAllData();
                    FragmentTransaction ftOTC = getFragmentManager().beginTransaction();
                    ftOTC.replace(R.id.llBot,listOTCFragment).commit();
                    bIsTSE = false;
                    changBtnColor();
                    break;
            }
        }
    }

    private void changBtnColor(){
        if(false == bIsTSE){
            btnTSE.setAlpha(0.6f);
            btnOTC.setAlpha(1);
        }else{
            btnTSE.setAlpha(1);
            btnOTC.setAlpha(0.6f);
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
