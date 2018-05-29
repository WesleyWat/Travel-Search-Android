package com.example.wesley.hw9;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


/**
 * A simple {@link Fragment} subclass.
 */
public class OneFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = "OneFragment";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private GoogleApiClient mGoogleApiClient;

    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private Button searchBtn, clearBtn;
    private EditText distance, keyword;
    private AutoCompleteTextView location;
    private TextView errorKeyword, errorLocation;
    private RadioButton current, other;
    private Spinner category;
    private RequestQueue mRequestQueue;
    private RadioGroup locationGroup;

    private LocationManager locationManager;
    private String locationProvider;
    private Location locationGet;

    public static final String HTML_MESSAGE = "HTML_MESSAGE";

    public ProgressDialog pd;


    public OneFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .addConnectionCallbacks(this)
                .build();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        searchBtn = (Button) view.findViewById(R.id.SearchButton);
        clearBtn = (Button) view.findViewById(R.id.ClearButton);
        keyword = (EditText) view.findViewById(R.id.EditKeyword);
        distance = (EditText) view.findViewById(R.id.EditDistance);
        location = (AutoCompleteTextView) view.findViewById(R.id.EditLocation);
        errorKeyword = (TextView) view.findViewById(R.id.ErrorKeyword);
        errorLocation = (TextView) view.findViewById(R.id.ErrorLocation);
        current = (RadioButton) view.findViewById(R.id.RadioCurrent);
        other = (RadioButton) view.findViewById(R.id.RadioOther);
        category = (Spinner) view.findViewById(R.id.ChooseCategory);
        locationGroup = (RadioGroup)view.findViewById(R.id.radioGroup);

        locationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.RadioCurrent:
                        location.setEnabled(false);
                        break;
                    case R.id.RadioOther:
                        location.setEnabled(true);
                        break;
                }
            }

        });


        //Search Button Onclick listener
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast;
                boolean flag = false;
                Log.i(TAG, "Search Onclick");
                if (keyword.getText().toString().trim().equals("")) {
                    Log.i(TAG, "Keyword Error");
                    errorKeyword.setVisibility(View.VISIBLE);
                    flag = true;
                } else {
                    Log.i(TAG, "Keyword Right");
                    errorKeyword.setVisibility(View.GONE);
                }
                if (other.isChecked() && location.getText().toString().trim().equals("")) {
                    Log.i(TAG, "Location Error");
                    errorLocation.setVisibility(View.VISIBLE);
                    flag = true;
                } else {
                    Log.i(TAG, "Location Right");
                    errorLocation.setVisibility(View.GONE);
                }
                if (flag) {
                    toast = Toast.makeText(getActivity(), "Please fix all fields with errors", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                } else {
                    //Search
                    // Instantiate the RequestQueue.
                    pd = new ProgressDialog(getContext());
                    pd.setMessage("Fetching results");
                    pd.show();
                    RequestQueue queue = Volley.newRequestQueue(getContext());

                    //Change to AWS or Local host
                    String url ="http://test-envnode.us-east-2.elasticbeanstalk.com";
                    //String url = "http://10.0.2.2:8081";
                    String keywordStr = keyword.getText().toString();
                    String typeStr = category.getSelectedItem().toString().toLowerCase().replaceAll(" ", "_");
                    String distanceStr;
                    if (distance.getText().toString().equals("")) {
                        distanceStr = "10";
                    } else {
                        distanceStr = distance.getText().toString();
                    }
                    String fromStr;
                    String locationStr = "";
                    if (current.isChecked()) {
                        fromStr = "current";
                    } else {
                        fromStr = "other";
                        locationStr = location.getText().toString();
                    }

                    LocationManager lm = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationGet= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(locationGet == null){
                        Log.i(TAG, "locationGet is null");
                        return;
                    }
                    double latStr = locationGet.getLatitude();
                    double lonStr = locationGet.getLongitude();

                    if (current.isChecked()) {
                        url = url + "/search?keyword=" + keywordStr + "&type=" + typeStr + "&distance=" + distanceStr
                                + "&from=" + fromStr + "&lon=" + lonStr + "&lat=" + latStr;
                    } else {
                        url = url + "/search?keyword=" + keywordStr + "&type=" + typeStr + "&distance=" + distanceStr
                                + "&from=" + fromStr + "&lon=" + lonStr + "&lat=" + latStr + "&location=" + locationStr;
                    }


                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the first 500 characters of the response string.
                                    Intent intent = new Intent(getContext(), ResultActivity.class);
                                    intent.putExtra(HTML_MESSAGE, response);
                                    SharedPreferences local = PreferenceManager.getDefaultSharedPreferences(getContext());
                                    SharedPreferences.Editor editor = local.edit();
                                    editor.remove("message");
                                    editor.apply();
                                    pd.dismiss();
                                    startActivity(intent);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Intent intent = new Intent(getContext(), ResultActivity.class);
                            intent.putExtra(HTML_MESSAGE, error.toString());
                            pd.dismiss();
                            startActivity(intent);
                        }
                    });
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            50000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                }
            }
        });

        //Clear button onclick listener
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLocation.setVisibility(View.GONE);
                errorKeyword.setVisibility(View.GONE);
                keyword.setText("");
                distance.setText("");
                location.setText("");
                current.setChecked(true);
                category.setSelection(0);
                location.setEnabled(false);
            }
        });



        mAutocompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.EditLocation);
        mAutocompleteTextView.setThreshold(1);

        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(getContext(), android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
        return view;
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

        }
    };


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(getContext(),
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();

    }

}
