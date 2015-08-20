package com.softmobile.netbschartof3institutionalinvestors.base;


import android.app.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


public class ListFragment extends Fragment {

    SListAdapter myAdapter;
    ListView lvData;

    FragmentListener callback;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lvlayout, container, false);

        lvData = (ListView) view.findViewById(R.id.lvData);

        myAdapter = new SListAdapter(getActivity());
        STool.sla = myAdapter;

        lvData.setAdapter(myAdapter);
        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myAdapter.changeClikcColor(position);
                if (null != callback) {
                    callback.OnListViewClick(position);
                }

            }
        });
        return view;
    }

    public void setListItemBackColor(int iPos){
        myAdapter.changeClikcColor(iPos);
    }

    public void setOnListViewClickListener(FragmentListener fml){
        this.callback = fml;
    }


}
