package com.example.wesley.hw9;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotosFragment extends Fragment {

    private final static String TAG = "Photos Fragment";
    private TextView norecord;

    public PhotosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_photos, container, false);
        Bundle bundle = getArguments();
        int number =  bundle.getInt("photos");
        norecord = v.findViewById(R.id.textView13);
        if(number != 0) {
            norecord.setVisibility(View.INVISIBLE);
            List<Integer> photolist = new ArrayList<Integer>();
            for (int i = 0; i < number; ++i) {
                photolist.add(i);
            }
            RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.photoRecycler);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);

            PhotoRecyclerAdapter mAdapter;
            mAdapter = new PhotoRecyclerAdapter(photolist, getContext());
            mRecyclerView.setAdapter(mAdapter);
        }
        else{
            norecord.setVisibility(View.VISIBLE);
        }

        return v;
    }

}
