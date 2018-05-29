package com.example.wesley.hw9;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    public static  final String TAG = "InfoFragment";
    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);

        Bundle bundle = getArguments();

        TextView address = v.findViewById(R.id.addressText);
        address.setText(bundle.getString("address"));

        TextView phone = v.findViewById(R.id.phoneText);
        phone.setText(bundle.getString("phone"));
        Linkify.addLinks(phone, Linkify.ALL);

        TextView price = v.findViewById(R.id.priceText);
        int level = bundle.getInt("price_level");
        String levelD="";
        for(int i = 0; i< level; ++i){
            levelD += '$';
        }
        if(level == 0){
            levelD = "No price level";
        }
        price.setText(levelD);

        RatingBar ratingBar = v.findViewById(R.id.ratingBar);
        ratingBar.setRating((float) bundle.getDouble("rating"));
        ratingBar.setIsIndicator(true);

        TextView google = v.findViewById(R.id.googleText);
        google.setText(bundle.getString("google"));
        Linkify.addLinks(google, Linkify.ALL);

        TextView web = v.findViewById(R.id.webText);
        web.setText(bundle.getString("website"));
        Linkify.addLinks(web, Linkify.ALL);
        return v;
    }


}
