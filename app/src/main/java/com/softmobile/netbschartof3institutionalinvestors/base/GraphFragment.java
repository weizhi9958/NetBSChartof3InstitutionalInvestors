package com.softmobile.netbschartof3institutionalinvestors.base;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;


public class GraphFragment extends Fragment{

    SurfaceView sfv;
    SurfaceHolder sfh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        sfv = (SurfaceView) view.findViewById(R.id.sfvGraph);
        sfh = sfv.getHolder();
        new SDrawGraph(getActivity(), sfh, sfv);

        sfv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d("X",String.valueOf(event.getX()));
                        Log.d("Y",String.valueOf(event.getY()));
                        break;
                }
                return false;
            }
        });
        return view;
    }

}
