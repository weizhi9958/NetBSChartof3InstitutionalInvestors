package com.softmobile.netbschartof3institutionalinvestors.base;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    boolean bIsTSE = true;

    LinearLayout llOuter;
    LinearLayout llTop;
    LinearLayout llBot;
    LinearLayout llBotOuter;

    Button btnTSE;
    Button btnOTC;

    ListFragment listTSEFragment;
    ListFragment listOTCFragment;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedInstanceState = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
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

        Display display = getWindowManager().getDefaultDisplay();
        if(display.getWidth() > display.getHeight()){
            llOuter.setOrientation(LinearLayout.HORIZONTAL);
            llBotOuter.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1.0f));
        }else{
            llOuter.setOrientation(LinearLayout.VERTICAL);
            llBotOuter.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,2.0f));
        }

        listTSEFragment = new ListFragment();
        listOTCFragment = new ListFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.llBot, listTSEFragment, "bot");
        bundle = new Bundle();
        bundle.putString(STool.TAG_URL, STool.TAGURL_TSE);
        listTSEFragment.setArguments(bundle);
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
                    bundle.putString(STool.TAG_URL,STool.TAGURL_TSE);
                    listTSEFragment.setArguments(bundle);
                    ftTSE.replace(R.id.llBot,listTSEFragment).commit();
                    bIsTSE = true;
                    changBtnColor();
                    break;
                case R.id.btnOTC:
                    STool.clearAllData();
                    FragmentTransaction ftOTC = getFragmentManager().beginTransaction();
                    bundle.putString(STool.TAG_URL,STool.TAGURL_OTC);
                    listOTCFragment.setArguments(bundle);
                    ftOTC.replace(R.id.llBot,listOTCFragment).commit();
                    bIsTSE = false;
                    changBtnColor();
                    break;
            }
        }
    }

    private void changBtnColor(){
        btnTSE.setEnabled(!bIsTSE);
        btnOTC.setEnabled(bIsTSE);
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
