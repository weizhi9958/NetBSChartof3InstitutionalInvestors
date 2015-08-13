package com.softmobile.netbschartof3institutionalinvestors.base;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;


public class GraphFragment extends Fragment{

    SurfaceView sfv;
    SurfaceHolder sfh;
    Context context;
    ArrayList<HashMap<String, String>> alDataList;

    public GraphFragment(){

    }

    public GraphFragment(Context context, ArrayList alData){
        this.context = context;
        this.alDataList = alData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_graph, container, false);
        sfv = (SurfaceView) v.findViewById(R.id.sfvGraph);
        sfh = sfv.getHolder();

        new SDrawGraph(context, sfh, sfv, alDataList);
        return v;
    }

}
