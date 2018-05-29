package com.example.wesley.hw9;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wesley.hw9.ResultActivity.results;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TwoFragment extends Fragment {

    private List<results> favoriteList;

    private RecyclerView mRecyclerView;
    private FavoriteRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "TwoFragment";


    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_two, container, false);
        TextView norecords = v.findViewById(R.id.textView18);
        SharedPreferences diySP = PreferenceManager.getDefaultSharedPreferences(getContext());
        Map<String,?> favorite = diySP.getAll();
        favoriteList = new ArrayList<results>();
        if(favorite.size() == 0 ||(favorite.size()==1 && favorite.containsKey("message"))){
            norecords.setVisibility(View.VISIBLE);
        }
        else{
            norecords.setVisibility(View.INVISIBLE);
            String name,address,url,id;
            for (String key : favorite.keySet()) {
                if (key.equals(favorite.get(key))) {
                    id = key;
                    name = (String) favorite.get(key + 1);
                    address = (String) favorite.get(key + 2);
                    url = (String) favorite.get(key + 3);
                    favoriteList.add(new results(name,address,url,id));
                }
            }

            mRecyclerView = (RecyclerView) v.findViewById(R.id.favorite_recycler);
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new FavoriteRecyclerAdapter(favoriteList, getContext(),norecords);
            mRecyclerView.setAdapter(mAdapter);
        }
        return v;
    }

    public void onResume() {

        TextView norecords = getActivity().findViewById(R.id.textView18);
        SharedPreferences diySP = PreferenceManager.getDefaultSharedPreferences(getContext());
        Map<String,?> favorite = diySP.getAll();
        favoriteList = new ArrayList<results>();
        if(favorite.size() == 0 ||(favorite.size()==1 && favorite.containsKey("message"))){
            norecords.setVisibility(View.VISIBLE);
        }
        else {
            norecords.setVisibility(View.INVISIBLE);
            String name, address, url, id;
            for (String key : favorite.keySet()) {
                if (key.equals(favorite.get(key))) {
                    id = key;
                    name = (String) favorite.get(key + 1);
                    address = (String) favorite.get(key + 2);
                    url = (String) favorite.get(key + 3);
                    favoriteList.add(new results(name, address, url, id));
                }
            }
            mAdapter = new FavoriteRecyclerAdapter(favoriteList, getContext(), norecords);
            mRecyclerView.setAdapter(mAdapter);

        }

        super.onResume();
    }

}
