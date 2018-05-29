package com.example.wesley.hw9;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.example.wesley.hw9.ReviewFragment.review;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewRecyclerAdapter extends  RecyclerView.Adapter<ReviewRecyclerAdapter.ViewHolder> {
    private List<review> mDataset;
    private final String TAG = "PhotoRecyclerViewAdapter";

    private Context mContext;

    private RequestQueue mRequestQueue;


    // Provide a suitable constructor (depends on the kind of dataset)
    public ReviewRecyclerAdapter(List<review> myDataset, Context myContext) {
        mDataset = myDataset;
        mContext = myContext;
    }



    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView name,time,text;
        public RatingBar rating;
        public String url;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView)v.findViewById(R.id.imageView3);
            name = (TextView)v.findViewById(R.id.textView14);
            time = (TextView)v.findViewById(R.id.textView15);
            text = (TextView)v.findViewById(R.id.textView16);
            rating = (RatingBar) v.findViewById(R.id.ratingBar2);

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    mContext.startActivity(intent);
                }
            });
        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReviewRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review, parent, false);
        ReviewRecyclerAdapter.ViewHolder vh = new ReviewRecyclerAdapter.ViewHolder(v);
        return vh;

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ReviewRecyclerAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.name.setText(mDataset.get(position).getNameR());
        holder.time.setText(mDataset.get(position).getTimeR());
        holder.text.setText(mDataset.get(position).getTextR());
        Picasso.get().load(mDataset.get(position).getPhotoR()).into(holder.imageView);
        holder.rating.setNumStars(mDataset.get(position).getRatingR());
        holder.rating.setRating(mDataset.get(position).getRatingR());
        holder.rating.setIsIndicator(true);
        holder.url = mDataset.get(position).getUrlR();
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}