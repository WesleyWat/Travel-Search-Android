package com.example.wesley.hw9;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private InfoFragment infoF;
    private PhotosFragment photosF;
    private MapFragment mapF;
    private ReviewFragment reviewsF;
    private String message;
    private String name,address,website,googlesite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        Intent intent = getIntent();
        message = intent.getStringExtra(MyRecyclerAdapter.DETAIL_MESSAGE);
        String title = "";
        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject result = jsonObject.getJSONObject("result");
            title = result.getString("name");
            name = title;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        infoF = new InfoFragment();
        photosF = new PhotosFragment();
        mapF = new MapFragment();
        reviewsF = new ReviewFragment();

        //tab
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments to adapter one by one
        adapter.addFragment(infoF, "Info");
        adapter.addFragment(photosF, "PHOTOS");
        adapter.addFragment(mapF, "MAP");
        adapter.addFragment(reviewsF, "REVIEWS");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        int [] imgs = new int[] {R.drawable.info_outline, R.drawable.photos,R.drawable.maps,R.drawable.review};
        for (int i = 0;i< tabLayout.getTabCount(); ++i) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            LinearLayout tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tabContent = (TextView) tabLinearLayout.findViewById(R.id.tabContent);
            tabContent.setText(adapter.getPageTitle(i));
            tabContent.setCompoundDrawablesWithIntrinsicBounds(imgs[i], 0, 0, 0);
            tab.setCustomView(tabContent);
        }


        //get info information
        Bundle bundle = new Bundle();
        //TextView textView = findViewById(R.id.textView);
        //textView.setText(message);
        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject result = jsonObject.getJSONObject("result");
            bundle.putString("address",result.getString("formatted_address"));
            address = result.getString("formatted_address");
            if(result.has("formatted_phone_number")){
                bundle.putString("phone", result.getString("formatted_phone_number"));
            }
            else {
                bundle.putString("phone", "No phone number.");
            }
            if(result.has("price_level"))
                bundle.putInt("price_level",result.getInt("price_level"));
            else
                bundle.putInt("price_level",0);
            if(result.has("rating")) {
                bundle.putDouble("rating", result.getDouble("rating"));
            }
            else{
                bundle.putDouble("rating", 0);
            }
            if(result.has("url")) {
                bundle.putString("google", result.getString("url"));
                googlesite = result.getString("url");
            }
            else{
                bundle.putString("google", "No Google page.");
                googlesite = "";
            }
            if(result.has("website")) {
                bundle.putString("website", result.getString("website"));
                website = result.getString("website");
            }
            else{
                bundle.putString("website", "No website.");
                website = "No website";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        infoF.setArguments(bundle);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_share:
                        Toast.makeText(DetailActivity.this, "Share !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        String URL = "https://twitter.com/intent/tweet?text=";
                        String tempString = "Check out " + name + " located at " + address + ". Website: ";
                        URL += tempString + "&url=";
                        if(!website.equals("")){
                            tempString = website;
                        }
                        else{
                            tempString = googlesite;
                        }
                        URL += tempString + "&hashtags=TravelAndEnertainmentSearch";
                        Uri content_url = Uri.parse(URL);
                        intent.setData(content_url);
                        DetailActivity.this.startActivity(intent);
                        break;
                    case R.id.action_favorite:
                        Intent intent2 = getIntent();
                        String name,address,id,url;
                        id = intent2.getStringExtra("id");
                        name = intent2.getStringExtra("name");
                        address = intent2.getStringExtra("address");
                        url = intent2.getStringExtra("url");
                        SharedPreferences diySP = PreferenceManager.getDefaultSharedPreferences(DetailActivity.this);
                        if(diySP.contains(id)){
                            diySP.edit()
                                    .remove(id)
                                    .remove(id+1)
                                    .remove(id+2)
                                    .remove(id+3)
                                    .apply();
                            item.setIcon(R.drawable.heart_outline_white);
                            Toast.makeText(DetailActivity.this, name + " was deleted from the favorite list!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            diySP.edit()
                                    .putString(id,id)
                                    .putString(id+1,name)
                                    .putString(id+2,address)
                                    .putString(id+3,url)
                                    .apply();
                            item.setIcon(R.drawable.heart_fill_white);
                            Toast.makeText(DetailActivity.this, name + " was added to the favorite list!", Toast.LENGTH_SHORT).show();
                        }
                }
                return true;
            }

        });

        Bundle bundle1 = new Bundle();
        try{
            JSONObject jsonObject = new JSONObject(message);
            JSONObject result = jsonObject.getJSONObject("result");
            if(result.has("photos")){
                JSONArray photos = result.getJSONArray("photos");
                bundle1.putInt("photos",photos.length());
            }
            else{
                bundle1.putInt("photos",0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        photosF.setArguments(bundle1);

        Bundle bundle2 = new Bundle();
        bundle2.putString("message",message);
        reviewsF.setArguments(bundle2);

        //twitter
        Twitter.initialize(this);

    }

    public void shareToTwitter(View view) {
        try {
            TweetComposer.Builder builder = new TweetComposer.Builder(this)
                    .url(new URL("https://www.google.com/"));
            builder.show();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    //menu heart
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem favorite = menu.findItem(R.id.action_favorite);
        SharedPreferences diySP = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent =getIntent();
        String id;
        if(diySP.contains(intent.getStringExtra("id"))){
            favorite.setIcon(R.drawable.heart_fill_white);
        }
        else {
            favorite.setIcon(R.drawable.heart_outline_white);
        }
        // Configure the search info and add any event listeners...
        return super.onCreateOptionsMenu(menu);
    }

}
