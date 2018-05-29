package com.example.wesley.hw9;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoRecyclerAdapter extends RecyclerView.Adapter<PhotoRecyclerAdapter.ViewHolder> {
    private List<Integer> mDataset;
    private final String TAG = "PhotoRecyclerViewAdapter";

    private Context mContext;

    private RequestQueue mRequestQueue;


    // Provide a suitable constructor (depends on the kind of dataset)
    public PhotoRecyclerAdapter(List<Integer> myDataset, Context myContext) {
        mDataset = myDataset;
        mContext = myContext;
    }



    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView)v.findViewById(R.id.imageView);
        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public PhotoRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Picasso.get()
                .load("http://test-envnode.us-east-2.elasticbeanstalk.com/normal/" + mDataset.get(position) + ".jpeg")
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                //.networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(holder.imageView);
        /*Picasso.get()
                .load("http://10.0.2.2:8081/normal/" + mDataset.get(position) + ".jpeg")
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(holder.imageView);*/
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}