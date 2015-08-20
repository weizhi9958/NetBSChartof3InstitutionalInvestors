package com.softmobile.netbschartof3institutionalinvestors.base;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


public class GraphFragment extends Fragment{

    SSurfaceGraph sfv;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        FrameLayout flSurface = (FrameLayout) view.findViewById(R.id.flSurface);

        sfv = new SSurfaceGraph(getActivity());

       // final SSurfaceGraph ssfg = new SSurfaceGraph(getActivity(),sfv);

        flSurface.addView(sfv);

        sfv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        sfv.MyDraw(-1,(int) event.getX());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        sfv.MyDraw(-1,(int) event.getX());
                        break;
                }
                return true;
            }
        });
        return view;
    }


    public void drawGraph(int ipos){
        sfv.MyDraw(ipos,-1);
    }

    public void setFragmentListener(FragmentListener fml){
        if(null != sfv) {
            sfv.setSurfViewListener(fml);
        }
    }
}
