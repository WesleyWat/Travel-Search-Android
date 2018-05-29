package com.example.wesley.hw9;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ReviewFragment extends Fragment {

    public List<review> yelpR,googleR;
    private Spinner gory;
    private  final static String TAG ="Review Fragment";
    ReviewRecyclerAdapter mAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    TextView norecords;

    public ReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_review, container, false);

        norecords = (TextView)v.findViewById(R.id.textView17);
        norecords.setVisibility(View.INVISIBLE);

        Bundle bundle = getArguments();
        yelpR = new ArrayList<review>();
        googleR = new ArrayList<review>();
        try {
            final JSONObject jsonObject = new JSONObject(bundle.getString("message"));
            if(jsonObject.has("yelp")){
                JSONArray yelp = jsonObject.getJSONArray("yelp");
                String url,text,time,photo,name;
                int rating;
                for(int i =0 ;i<yelp.length();++i){
                    JSONObject temp = yelp.getJSONObject(i);
                    url = temp.getString("url");
                    text = temp.getString("text");
                    time = temp.getString("time_created");
                    photo = temp.getJSONObject("user").getString("image_url");
                    name = temp.getJSONObject("user").getString("name");
                    rating = temp.getInt("rating");
                    yelpR.add(new review(url,text,time,photo,rating,name));
                }
            }
            JSONObject result = jsonObject.getJSONObject("result");
            if(result.has("reviews")){
                JSONArray google = result.getJSONArray("reviews");
                JSONObject temp;
                Calendar cal=Calendar.getInstance();
                Date date;
                SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String url,text,time,photo,name;
                int rating;
                long temptime;
                for(int i = 0; i<google.length();++i){
                    temp = google.getJSONObject(i);
                    url = temp.getString("author_url");
                    text = temp.getString("text");
                    temptime = temp.getLong("time");
                    cal.setTimeInMillis(temptime*1000);
                    date = cal.getTime();
                    time = df.format(date);
                    photo = temp.getString("profile_photo_url");
                    name = temp.getString("author_name");
                    rating = temp.getInt("rating");
                    googleR.add(new review(url,text,time,photo,rating,name));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRecyclerView = (RecyclerView)v.findViewById(R.id.reviewRecycler);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ReviewRecyclerAdapter(googleR, getContext());

        norecords = (TextView)v.findViewById(R.id.textView17);

        if(googleR.isEmpty()){
            mRecyclerView.setVisibility(View.INVISIBLE);
            norecords.setVisibility(View.VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);
            norecords.setVisibility(View.INVISIBLE);
        }

        mRecyclerView.setAdapter(mAdapter);


        gory = (Spinner)v.findViewById(R.id.spinner4);
        gory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if(selected.equals("Google reviews")) {
                    mAdapter = new ReviewRecyclerAdapter(googleR, getContext());
                    mRecyclerView.setAdapter(mAdapter);
                    if(googleR.isEmpty()){
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        norecords.setVisibility(View.VISIBLE);
                    }
                    else{
                        mRecyclerView.setVisibility(View.VISIBLE);
                        norecords.setVisibility(View.INVISIBLE);
                    }
                }
                else{
                    mAdapter = new ReviewRecyclerAdapter(yelpR, getContext());
                    mRecyclerView.setAdapter(mAdapter);
                    if(yelpR.isEmpty()){
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        norecords.setVisibility(View.VISIBLE);
                    }
                    else{
                        mRecyclerView.setVisibility(View.VISIBLE);
                        norecords.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return v;
    }

    public class review{
        String urlR, textR, timeR, photoR,nameR;
        int ratingR;

        public review(String url,String text,String time,String photo,int rating,String name){
            urlR = url;
            textR = text;
            timeR = time;
            photoR = photo;
            ratingR = rating;
            nameR = name;
        }

        public String getPhotoR() {
            return photoR;
        }

        public int getRatingR() {
            return ratingR;
        }

        public String getTextR() {
            return textR;
        }

        public String getTimeR() {
            return timeR;
        }

        public String getUrlR() {
            return urlR;
        }

        public String getNameR() {
            return nameR;
        }
    }
}
