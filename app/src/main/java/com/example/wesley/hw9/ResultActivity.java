package com.example.wesley.hw9;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "ResultActivity";
    private Button previousBtn,nextBtn;
    private List<results> results1,results2,results3,current;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Log.i(TAG,"onCreate");

        SharedPreferences local = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = local.edit();

        if(!local.contains("message")){
            // Get the Intent that started this activity and extract the string
            Intent intent = getIntent();
            message = intent.getStringExtra(OneFragment.HTML_MESSAGE);
            editor.putString("message",message);
            editor.apply();
        }
        else{
            message = local.getString("message",null);
        }

        if(savedInstanceState != null){
            Log.i(TAG,"Entered");
            message = savedInstanceState.getString("messageINM").toString();
        }

        // Capture the layout's TextView and set the string as its text
        //TextView textView = findViewById(R.id.textView);
        //textView.setText(message);

        previousBtn = findViewById(R.id.previous);
        nextBtn = findViewById(R.id.next);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        //deal with message
        results1 = new ArrayList<results>();
        results2 = new ArrayList<results>();
        results3 = new ArrayList<results>();
        current = new ArrayList<results>();
        String name,address,url,id;
        try {
            JSONObject jsonObj = new JSONObject(message);
            JSONArray temp = jsonObj.getJSONArray("results");
            int len = temp.length();
            for (int i = 0 ; i< len ; ++i){
                JSONObject jsonResult = temp.getJSONObject(i);
                //Log.i(TAG, jsonResult.getString("name"));
                name = jsonResult.getString("name");
                address = jsonResult.getString("vicinity");
                url = jsonResult.getString("icon");
                id = jsonResult.getString("place_id");
                results1.add(new results(name,address,url,id));
            }
            if (jsonObj.has("results2")){
                temp = jsonObj.getJSONArray("results2");
                len = temp.length();
                for (int i = 0; i < len; ++i) {
                    JSONObject jsonResult = temp.getJSONObject(i);
                    //Log.i(TAG, jsonResult.getString("name"));
                    name = jsonResult.getString("name");
                    address = jsonResult.getString("vicinity");
                    url = jsonResult.getString("icon");
                    id = jsonResult.getString("place_id");
                    results2.add(new results(name, address, url, id));
                }
            }
            if (jsonObj.has("results3")){
                temp = jsonObj.getJSONArray("results3");
                len = temp.length();
                for (int i = 0; i < len; ++i) {
                    JSONObject jsonResult = temp.getJSONObject(i);
                    //Log.i(TAG, jsonResult.getString("name"));
                    name = jsonResult.getString("name");
                    address = jsonResult.getString("vicinity");
                    url = jsonResult.getString("icon");
                    id = jsonResult.getString("place_id");
                    results3.add(new results(name, address, url, id));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(results1.isEmpty()){
            ConstraintLayout norecords = findViewById(R.id.norecords_view);
            norecords.setVisibility(View.VISIBLE);
            ConstraintLayout container = findViewById(R.id.result_view);
            container.setVisibility(View.INVISIBLE);
        }
        else {
            ConstraintLayout norecords = findViewById(R.id.norecords_view);
            norecords.setVisibility(View.INVISIBLE);
            ConstraintLayout container = findViewById(R.id.result_view);
            container.setVisibility(View.VISIBLE);
            Log.i(TAG, results1.toString());
            // specify an adapter (see also next example)
            current = results1;
            mAdapter = new MyRecyclerAdapter(current, ResultActivity.this);

            mRecyclerView.setAdapter(mAdapter);
            previousBtn.setEnabled(false);
            if (results2.isEmpty()) {
                nextBtn.setEnabled(false);
            }
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current.equals(results1)) {
                        current = results2;
                        mAdapter = new MyRecyclerAdapter(current, ResultActivity.this);
                        mRecyclerView.setAdapter(mAdapter);
                        if (results3.isEmpty()) {
                            nextBtn.setEnabled(false);
                        }
                        previousBtn.setEnabled(true);
                    } else if (current.equals(results2)) {
                        current = results3;
                        mAdapter = new MyRecyclerAdapter(current, ResultActivity.this);
                        mRecyclerView.setAdapter(mAdapter);
                        nextBtn.setEnabled(false);
                        previousBtn.setEnabled(true);
                    }
                }
            });

            previousBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current.equals(results2)) {
                        current = results1;
                        mAdapter = new MyRecyclerAdapter(current, ResultActivity.this);
                        mRecyclerView.setAdapter(mAdapter);
                        nextBtn.setEnabled(true);
                        previousBtn.setEnabled(false);
                    } else if (current.equals(results3)) {
                        current = results2;
                        mAdapter = new MyRecyclerAdapter(current, ResultActivity.this);
                        mRecyclerView.setAdapter(mAdapter);
                        nextBtn.setEnabled(true);
                        previousBtn.setEnabled(true);
                    }
                }
            });
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("messageINM", message);
        Log.i(TAG,outState.getString("messageINM").toString());
        super.onSaveInstanceState(outState);
    }

    static class results {
        String lName,lAaddress;
        String cateUrl,id;


        public results(String name, String address, String url,String ID){
            lName = name;
            lAaddress = address;
            cateUrl = url;
            id = ID;
        }

        public String getCateUrl() {
            return cateUrl;
        }

        public String getlAaddress() {
            return lAaddress;
        }

        public String getlName() {
            return lName;
        }

        public String getId() {
            return id;
        }
    }

    @Override
    public void onStart() {
        Log.i(TAG,"onStart");
        super.onStart();
    }

    @Override
    public void onRestart() {
        Log.i(TAG,"onRestart");
        super.onRestart();
    }

    @Override
    public void onResume() {

        Log.i(TAG,"onResume");
        super.onResume();
    }

    @Override
    public void onPause() {

        Log.i(TAG,"onPause");
        super.onPause();
    }

    @Override
    public void onStop() {

        Log.i(TAG,"onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {

        Log.i(TAG,"onDestroy");
        super.onDestroy();
    }
}
