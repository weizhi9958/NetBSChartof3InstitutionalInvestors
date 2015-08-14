package com.softmobile.netbschartof3institutionalinvestors.base;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
import java.util.ArrayList;
import java.util.HashMap;

public class ListFragment extends Fragment{

    String m_strXmlData;
    String m_strUrl;

    Context context;
    ProgressDialog pgdDialog;

    SListAdapter myAdapter;
    ArrayList<HashMap<String, String>> alDataList;
    ListView lvData;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alDataList = new ArrayList<HashMap<String, String>>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lvlayout, container, false);
        lvData = (ListView) v.findViewById(R.id.lvData);
        Bundle bundle = getArguments();

        m_strUrl = bundle.getString("URL");
        context = getActivity();
        new SGetData().execute();
        return v;
    }

    class SGetData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pgdDialog = new ProgressDialog(context);
            pgdDialog.setCancelable(false);
            pgdDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URI uri = new URI(m_strUrl);
                HttpClient hc = new DefaultHttpClient();
                HttpGet hg = new HttpGet();

                hg.setURI(uri);
                HttpResponse res = hc.execute(hg);
                m_strXmlData = EntityUtils.toString(res.getEntity());

                XmlPullParser xpp = Xml.newPullParser();
                InputStream inputs = new ByteArrayInputStream(m_strXmlData.getBytes());
                xpp.setInput(inputs, "utf-8");
                int iEt = xpp.getEventType();
                HashMap<String, String> map = null;
                while (iEt != XmlPullParser.END_DOCUMENT){
                    switch (iEt){
                        case XmlPullParser.START_TAG:
                            String strName = xpp.getName();
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
                                alDataList.add(map);
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            myAdapter = new SListAdapter(context, alDataList);
            lvData.setAdapter(myAdapter);

            GraphFragment myGraphFragment = new GraphFragment();
            FragmentManager frm = getFragmentManager();
            FragmentTransaction ft = frm.beginTransaction();
            STool.setAlData(alDataList);
            ft.replace(R.id.llTop, myGraphFragment).commit();
            frm.executePendingTransactions();

            pgdDialog.dismiss();
        }
    }
}
