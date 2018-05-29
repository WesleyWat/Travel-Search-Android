package com.example.wesley.hw9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoriteRecyclerAdapter extends RecyclerView.Adapter<FavoriteRecyclerAdapter.ViewHolder> {
    private List<ResultActivity.results> mDataset;
    private final String TAG = "FavoriteRecyclerViewAdapter";
    private FavoriteRecyclerAdapter.OnItemClickListener clickListener;
    private Context mContext;

    private RequestQueue mRequestQueue;
    public ProgressDialog pd;
    public static final String DETAIL_MESSAGE ="detail";
    public TextView mnorecord;

    public void setClickListener(FavoriteRecyclerAdapter.OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public static interface OnItemClickListener {
        void onClick(View view, int position);
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public FavoriteRecyclerAdapter(List<ResultActivity.results> myDataset, Context myContext,TextView norecord) {
        mDataset = myDataset;
        mContext = myContext;
        mnorecord = norecord;
    }



    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView textView;
        public TextView textView2;
        public ImageView imageView;
        public ImageView imageButton;
        public LinearLayout textContainer;
        public String lID;
        public String heart,url;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView)v.findViewById(R.id.textView);
            textView2 = (TextView)v.findViewById(R.id.textView2);
            imageView = (ImageView)v.findViewById(R.id.imageView);
            textContainer = (LinearLayout)v.findViewById(R.id.textContainer);
            imageButton = (ImageView)v.findViewById(R.id.imageView2);

            textContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd = new ProgressDialog(mContext);
                    pd.setMessage("Fetching details");
                    pd.show();
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    String URL = "http://test-envnode.us-east-2.elasticbeanstalk.com/detail?place_id=" + lID;
                    //String URL ="http://10.0.2.2:8081/detail?place_id=" + lID;
                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Intent intent = new Intent(mContext, DetailActivity.class);
                                    intent.putExtra(DETAIL_MESSAGE,response);
                                    intent.putExtra("id",lID);
                                    intent.putExtra("url",url);
                                    intent.putExtra("address",textView2.getText().toString());
                                    intent.putExtra("name", textView.getText().toString());
                                    pd.dismiss();
                                    mContext.startActivity(intent);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();
                            Toast.makeText(mContext,"Something wrong!",Toast.LENGTH_SHORT);
                        }
                    });
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            50000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                }
            });

        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(itemView, getAdapterPosition());
            }
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FavoriteRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result_view, parent, false);
        FavoriteRecyclerAdapter.ViewHolder vh = new FavoriteRecyclerAdapter.ViewHolder(v);
        return vh;

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final FavoriteRecyclerAdapter.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(mDataset.get(position).getlName());
        holder.textView2.setText(mDataset.get(position).getlAaddress());
        holder.url = mDataset.get(position).getCateUrl();
        Picasso.get().load(mDataset.get(position).getCateUrl()).into(holder.imageView);
        holder.lID = mDataset.get(position).getId();
        holder.itemView.setTag(position);
        SharedPreferences diySP = PreferenceManager.getDefaultSharedPreferences(mContext) ;
        if(diySP.contains(holder.lID)){
            holder.heart = "red";
            holder.imageButton.setImageResource(R.drawable.heart_fill_red);
        }
        else{
            holder.heart = "white";
            holder.imageButton.setImageResource(R.drawable.heart_outline_black);
        }
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,holder.textView.getText().toString() + " was deleted from favorites",Toast.LENGTH_SHORT).show();
                SharedPreferences diySP = PreferenceManager.getDefaultSharedPreferences(mContext);
                diySP.edit()
                        .remove(holder.lID)
                        .remove(holder.lID+1)
                        .remove(holder.lID+2)
                        .remove(holder.lID+3)
                        .apply();
                mDataset.remove(position);
                if(mDataset.isEmpty()){
                    mnorecord.setVisibility(View.VISIBLE);
                }
                else{
                    mnorecord.setVisibility(View.INVISIBLE);
                }
                notifyDataSetChanged();
            }

        });
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}