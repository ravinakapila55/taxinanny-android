package com.taxi.nanny.views.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.taxi.nanny.R;
import com.taxi.nanny.model.CabsBeans;
import com.taxi.nanny.model.DriverBookingModel;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.autoComplete.GooglePlacesAutocompleteAdapter;
import com.taxi.nanny.utils.autoComplete.PlacesAutoCompleteAdapter;
import com.taxi.nanny.utils.location.CustomMarker;
import com.taxi.nanny.utils.location.GPSTracker;
import com.taxi.nanny.utils.location.GeoLocation;
import com.taxi.nanny.utils.location.GooglePulseAnimation;
import com.taxi.nanny.utils.location.LatLngResponse;
import com.taxi.nanny.utils.location.permission.Permission;
import com.taxi.nanny.utils.location.permission.PermissionGranted;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.Drawer;
import com.taxi.nanny.views.booking.LiveTrackBooking;
import com.taxi.nanny.views.home.adapter.HomeCabsAdapter;
import com.taxi.nanny.views.login_section.dialog.InternetErrorDialog;
import com.taxi.nanny.views.login_section.register.parent.ListofChildren;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class ParentHome extends Drawer implements OnMapReadyCallback, LocationListener
            , GoogleMap.OnInfoWindowClickListener,GoogleApiClient.ConnectionCallbacks,PermissionGranted,
            RetrofitResponse,GoogleApiClient.OnConnectionFailedListener
    {
    RecyclerView recyclerCabs;
    ArrayList<CabsBeans> cabList = new ArrayList<>();
    GoogleMap map;
    AutoCompleteTextView atPick;
    AutoCompleteTextView atDrop;
    @BindViews({R.id.rlPckup,R.id.rldest})
    List<RelativeLayout> relativeLists;
    LocationListener locationListener;
    LocationManager locationManager;
    double lattitude, longitude;
    double lattituded, longituded;
    public static  final  String TAG=ParentHome.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    Context context;
    private GPSTracker gpsTracker;
    public Marker marker;
    List<Address> addresses;
    String address, country, city, state,subLocality;
    private String description="";
    Constant constant;
    MarkerOptions place1,place2;
    Polyline polyline;
    @BindView(R.id.tv_ride)
    TextView tv_ride;
    @BindView(R.id.rlBook)
    RelativeLayout rlBook;
    GoogleApiClient googleApiClient = null;
    private SupportMapFragment mapFragment;
    public static final Integer LOCATION = 0x1;
    String Key="";
    LatLngResponse latLngResponse;
    LocationRequest locationRequest;


   /* @Override
    protected int getContentId() {
        return R.layout.homepage;
    }*/

        @Override
        public void onBackPressed()
        {
            callExitAlert();
        }

        @Override
        public void onResponse(int RequestCode, String response)
        {
            super.onResponse(560,response);
            switch (RequestCode)
            {
                case 560:
                    Log.e("ResponseSchedule ",response);

                    try {
                        JSONObject jsonObject=new JSONObject(response);

                        if (jsonObject.getString("status").equalsIgnoreCase("true"))
                        {
                           rlBook.setVisibility(View.VISIBLE);


                           onGoing="1";

                           JSONArray data=jsonObject.getJSONArray("data");

                            driverList.clear();
                            riderList.clear();

                            for (int i = 0; i < data.length(); i++)
                            {
                                JSONObject driverDetails=data.getJSONObject(i);
                                JSONObject driver_detailsObj=driverDetails.getJSONObject("driver_details");

                                DriverBookingModel bookingModel=new DriverBookingModel();

                                bookingModel.setDriverId(driver_detailsObj.getString("id"));
                                bookingModel.setName(driver_detailsObj.getString("name"));
                                bookingModel.setEmail(driver_detailsObj.getString("email"));
                                bookingModel.setImage(driver_detailsObj.getString("image"));
                                bookingModel.setVehicle_name(driver_detailsObj.getString("model"));
                                bookingModel.setPhoneNumber(driver_detailsObj.getString("phone_no"));
                                bookingModel.setVehicle_make(driver_detailsObj.getString("make"));
                                bookingModel.setVehicle_image(driver_detailsObj.getString("vehicle_image"));
                                bookingModel.setLicence_number(driver_detailsObj.getString("licence_number"));
                                bookingModel.setIssue_state(driver_detailsObj.getString("state_id"));
                                bookingModel.setRide_id(driverDetails.getString("booking_id"));
                                bookingModel.setBooking_time(driverDetails.getString("time_of_ride"));


                                driverList.add(bookingModel);

                                JSONArray ride_history=driverDetails.getJSONArray("ride_history");


                                for (int k = 0; k < ride_history.length(); k++)
                                {
                                    RiderListModel riderListModel=new RiderListModel();

                                    JSONObject RiderListModelObj=ride_history.getJSONObject(k);


                                    riderListModel.setPickup(RiderListModelObj.getString("pickup_location"));
                                    riderListModel.setPickLat(RiderListModelObj.getString("pickup_latitude"));
                                    riderListModel.setPicklng(RiderListModelObj.getString("pickup_longitude"));
                                    riderListModel.setDropup(RiderListModelObj.getString("dropup_location"));
                                    riderListModel.setDroplat(RiderListModelObj.getString("drop_latitude"));
                                    riderListModel.setDroplng(RiderListModelObj.getString("drop_longitude"));
                                    riderListModel.setId(RiderListModelObj.getString("child_id"));
                                    riderListModel.setImage(RiderListModelObj.getString("image"));
                                    riderListModel.setFirst_name(RiderListModelObj.getString("Name"));
                                    riderListModel.setPickPriority(Integer.parseInt(RiderListModelObj.getString("priority_pick")));
                                    riderListModel.setDropPriority(Integer.parseInt(RiderListModelObj.getString("priority_drop")));
                                    riderListModel.setRide_status(RiderListModelObj.getString("ride_status"));

                                    riderList.add(riderListModel);
                                }



                            }

                        }
                        else
                        {
                            onGoing="0";
                            SharedPrefUtil.getInstance().saveString(SharedPrefUtil.LIVE_TRACKING_ONGOING,"0");
                            rlBook.setVisibility(View.GONE);
                        }

                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }

                    break;
            }
        }

        public void callExitAlert()
        {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_pop_up, null);
            final TextView no = (TextView) dialogView.findViewById(R.id.tvNo);
            final TextView yes = (TextView) dialogView.findViewById(R.id.tvYes);
            final TextView tvMsg = (TextView) dialogView.findViewById(R.id.tvMsg);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            final AlertDialog alertDialog = dialogBuilder.create();
            // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            int width = WindowManager.LayoutParams.WRAP_CONTENT;
            int height = WindowManager.LayoutParams.WRAP_CONTENT;

            tvMsg.setText("Exit the app ?");
            yes.setText("Yes");

            no.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    alertDialog.dismiss();
                }
            });

            yes.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    finish();
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }

        ArrayList<DriverBookingModel> driverList=new ArrayList<>();
        ArrayList<RiderListModel> riderList=new ArrayList<>();
        @SuppressLint("SetTextI18n")


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        context=this;

        ButterKnife.bind(this);
        tvtitle.setText(getString(R.string.home));
        Permission.checkPermissionLocation(this, this);

        sharedPrefUtil=SharedPrefUtil.getInstance();

        getLocation();
        getCurrentLocation();

        initializeMap();

        if (getIntent().hasExtra("Key"))
        {
            Key=getIntent().getExtras().getString("Key");
            if (Key.equalsIgnoreCase("fcm"))
            {
                showCancelRideNotify();
            }
        }

        setInitializeView();

        if (gpsTracker.canGetLocation())
        {
            Log.e(TAG, "onCreate: GPSLattitude "+gpsTracker.getLatitude());
            Log.e(TAG, "onCreate: GPSLongitude "+gpsTracker.getLongitude());
        }
        else
        {
//            gpsTracker.showSettingsAlert();
            locationRequestt();
        }
    }


/*
    private void locationRequestt()
    {
        if (googleApiClient == null)
        {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) ParentHome.this)
                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();

            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>()
            {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult)
                {
                    final Status status = locationSettingsResult.getStatus();
                    final LocationSettingsStates state = locationSettingsResult.getLocationSettingsStates();

                    switch (status.getStatusCode()) 
                    {
                        case LocationSettingsStatusCodes.SUCCESS:
//                            getLocation();
                        break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(ParentHome.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                        break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                        break;
                    }

                }
            });
        }

    }*/

        private void locationRequestt()
        {
            if (googleApiClient == null)
            {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API).addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks)
                                ParentHome.this)
                        .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this).build();
                googleApiClient.connect();

                locationRequest = LocationRequest.create();

                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(30 * 1000);
                locationRequest.setFastestInterval(5 * 1000);
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);

                builder.setAlwaysShow(true);

                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                        .checkLocationSettings(googleApiClient, builder.build());

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.DONUT)
                {
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>()
                    {
                        @Override
                        public void onResult(@NonNull LocationSettingsResult locationSettingsResult)
                        {
                            final Status status = locationSettingsResult.getStatus();
                            final LocationSettingsStates state = locationSettingsResult.getLocationSettingsStates();

                            switch (status.getStatusCode())
                            {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied. But could be
                                    // fixed by showing the user
                                    // a dialog.
                                    try {
                                        // Show the dialog by calling
                                        // startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        status.startResolutionForResult(ParentHome.this, 1000);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have
                                    // no way to fix the
                                    // settings so we won't show the dialog.
                                    break;
                            }

                        }
                    });
                }

            }
        }

        public void showCancelRideNotify()
        {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_pop_up, null);
            final TextView no = (TextView) dialogView.findViewById(R.id.tvNo);
            final TextView yes = (TextView) dialogView.findViewById(R.id.tvYes);
            final TextView tvMsg = (TextView) dialogView.findViewById(R.id.tvMsg);
            final TextView tvOk = (TextView) dialogView.findViewById(R.id.tvOk);
            final RelativeLayout rl = (RelativeLayout) dialogView.findViewById(R.id.rl);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            final AlertDialog alertDialog = dialogBuilder.create();
            // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            int width = WindowManager.LayoutParams.WRAP_CONTENT;
            int height = WindowManager.LayoutParams.WRAP_CONTENT;

            rl.setVisibility(View.GONE);
            tvOk.setVisibility(View.VISIBLE);

            tvMsg.setText("Your Ride has been Cancelled by driver");

            tvOk.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    alertDialog.dismiss();
                }
            });


            alertDialog.show();
        }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 2:

                for (int i = 0; i < permissions.length; i++)
                {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(this, "Permitions Allowed", Toast.LENGTH_SHORT).show();
                      //  getLocation();
                    }
                    else if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                       // checkPermissionLocation(context);
                        Toast.makeText(this, "Permitions Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:
                {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
        }
    }


    private void setInitializeView()
    {
        atPick=(AutoCompleteTextView)findViewById(R.id.atPick);
        atDrop=(AutoCompleteTextView)findViewById(R.id.atDrop);
//        recyclerCabs=(RecyclerView)findViewById(R.id.recyclerCabs);
        constant=new Constant();

        PlacesAutoCompleteAdapter mAdapter = new PlacesAutoCompleteAdapter(this, R.layout.custom_spinner);
        atPick.setAdapter(mAdapter);
        atPick.setSingleLine();
        atPick.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        setLoactionAdapter(atPick,"p");

        PlacesAutoCompleteAdapter mAdapter2 = new PlacesAutoCompleteAdapter(this, R.layout.custom_spinner);
        atDrop.setAdapter(mAdapter2);
        atDrop.setSingleLine();
        atDrop.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        setLoactionAdapter(atDrop,"d");
    }

    private void setLoactionAdapter(AutoCompleteTextView autoCompleteTextView,String type)
    {
        autoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(getApplicationContext(),
                R.layout.custom_spinner));

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                constant.hideKeyboard(ParentHome.this,view);
                String placeId = GooglePlacesAutocompleteAdapter.resultListId.get(position).toString();

                final String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" +
                        placeId + "&key=AIzaSyCxj7Z3cWeV8phaVuua1cSQ88bWT_ls5u0";

                new AsyncTask<Void, Void, String>()
                {
                    @Override
                    protected void onPreExecute()
                    {
                        super.onPreExecute();
//                        DialogClass.showDialog(Listing.this);
                    }
                    @Override
                    protected String doInBackground(final Void... params)
                    {
                        try
                        {
                            serviceArrive(url,type);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(final String result)
                    {
//                        DialogClass.logout();
                    }
                }.execute();
            }
        });
    }

    private void serviceArrive(String url1,String type)
    {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            URL url = new URL(url1);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            Log.e("url ", "" + url);
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1)
            {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e)
        {
            Log.e("EXC", "Error processing Places API URL", e);
        } catch (IOException e)
        {
            Log.e("EXC", "Error connecting to Places API", e);
        }
        finally
        {
            if (conn != null)
            {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());

            Log.e(TAG, "serviceArrive:JsonResult "+jsonObj);

            JSONObject res = jsonObj.getJSONObject("result");
            JSONObject geo = res.getJSONObject("geometry");
            JSONObject loc = geo.getJSONObject("location");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.tv_ride,R.id.rlBook})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_ride:
                Log.e(TAG, "onClick: "+ "tv_ride");

                Intent intent=new Intent(this, ListofChildren.class);
                startActivity(intent);

         /*       if (isNetworkConnected())
                {
                    if (sharedPrefUtil.getString(SharedPrefUtil.LIVE_TRACKING_ONGOING,"").equalsIgnoreCase("1")
                            || onGoing.equalsIgnoreCase("1"))

                    {
                        Toast.makeText(this, "You have one ongoing ride",
                                Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent=new Intent(this, ListofChildren.class);
                        startActivity(intent);
                    }
                }
                else
                 {
                     new InternetErrorDialog(this)
                    {
                        @Override
                        protected void dataOnClick()
                        {
                            if (sharedPrefUtil.getString(SharedPrefUtil.LIVE_TRACKING_ONGOING,"").equalsIgnoreCase("1")
                                    || onGoing.equalsIgnoreCase("1"))

                            {
                                Toast.makeText(ParentHome.this, "You have one ongoing ride",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Intent intent=new Intent(ParentHome.this, ListofChildren.class);
                                startActivity(intent);
                            }
                        }
                    }.show();
                }*/

                break;

                case R.id.rlBook:
                Log.e(TAG, "rlBookClick: "+ "rlBook");
                Intent intent1=new Intent(this, LiveTrackBooking.class);
                    intent1.putExtra("driver_list",(Serializable) driverList);
                    intent1.putExtra("rider_list",(Serializable) riderList);
                startActivity(intent1);
                break;
        }
    }

    public void showMarker(double Lat,double lng)
    {
        LatLng latLng = new LatLng(Lat, lng);

        marker = map.addMarker(new MarkerOptions().position(latLng).draggable(true));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Lat, lng), 14.0f));

      /*  map.addPolyline(new PolylineOptions().add(latLng));
        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(latLng)
        .zoom(13).build()));*/
    }

     public void getLocation()
          {
            gpsTracker = new GPSTracker(this);
            if (gpsTracker.getLatitude() != 0.0)
            {
                longitude = gpsTracker.getLongitude();
                lattitude = gpsTracker.getLatitude();

                Log.e(TAG, "getLocation: Lattitude "+lattitude );
                Log.e(TAG, "getLocation: Lomgitude "+longitude );
                //  showMarkers();
            }
            else
            {
//                gpsTracker.showSettingsAlert();
                locationRequestt();
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
        {
            super.onActivityResult(requestCode, resultCode, data);
            Log.e("DataParentHome ",data.getData()+"");
        }

        private void getCurrentLocation()
         {
            Log.e(TAG, "getCurrentLocation: "+"Inside");
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults)
                // case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            try
            {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
            }
            catch(SecurityException e)
            {
                e.printStackTrace();
            }
        }

        private void askForPermission(String permission, Integer requestCode)
        {
            if (ContextCompat.checkSelfPermission(ParentHome.this, permission) !=
                    PackageManager.PERMISSION_GRANTED)
            {
                Log.e(TAG, "askForPermission: IFInitial ");
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(ParentHome.this, permission))
                {
                    Log.e(TAG, "askForPermission: IFInitial1 ");
                    //This is called if user has denied the permission before
                    //In this case I am just asking the permission again
                    ActivityCompat.requestPermissions(ParentHome.this, new String[]{permission}, requestCode);

                    getLocation();
                    getCurrentLocation();

                } else
                {
                    Log.e(TAG, "askForPermission: IFInitial2");
                    ActivityCompat.requestPermissions(ParentHome.this, new String[]{permission}, requestCode);
                }
            }
            else
             {
                 Log.e(TAG, "askForPermission: Else");
                Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
             }
        }


    public void initializeMap()
    {
        Log.e(TAG, "initializeMap: "+"insideinitializeViewMethod" );
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.e(TAG, "initializeMap: fragmentManager "+fm );
        fm.getMapAsync(this);
    }

    public void setCabData()
    {
    cabList.clear();
    CabsBeans cabsBeans=new CabsBeans("0","Micro","No Cabs",R.drawable.micro,false);
    cabList.add(cabsBeans);

    cabsBeans=new CabsBeans("1","Mini","5 Mins",R.drawable.mini,true);
    cabList.add(cabsBeans);

    cabsBeans=new CabsBeans("2","Sedan","12 Mins",R.drawable.sedan,false);
    cabList.add(cabsBeans);

    cabsBeans=new CabsBeans("3","Share","5 Mins",R.drawable.ic_car,false);
    cabList.add(cabsBeans);
    }

    public void setcabsAdapter()
    {
        setCabData();
        HomeCabsAdapter homeCabsAdapter=new HomeCabsAdapter(cabList,this);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,false);
      /*  recyclerCabs.setLayoutManager(layoutManager);
        recyclerCabs.setAdapter(homeCabsAdapter);*/

        homeCabsAdapter.onItemSelectedListener(new HomeCabsAdapter.MyClickListener()
        {
            @Override
            public void onItemClick(int layoutPosition, View view)
            {
                for (int i = 0; i <cabList.size() ; i++)
                {
                    if (i==layoutPosition)
                    {
                        cabList.get(i).setAvailable(true);
                    }
                    else
                        {
                        cabList.get(i).setAvailable(false);
                    }
                }
                homeCabsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map=googleMap;
        LatLng  latLng=new LatLng(lattitude,longitude);

//        LatLng latLng=new LatLng(30.7046,76.7179);
        map.addMarker(new MarkerOptions().position(latLng)).showInfoWindow();
        //    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        Log.e(TAG, "onMapReady: "+googleMap.getCameraPosition());


        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(latLng)
                .zoom(13).build()));

  /*      map.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {
                Log.e(TAG, "onMapClick: "+latLng);
                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                lattitude=latLng.latitude;
                longitude=latLng.longitude;

                Log.e(TAG, "onMapReadyClick:latt "+lattitude);
                Log.e(TAG, "onMapReadyClick:long "+longitude);

                // Clears the previously touched position
                map.clear();

                // Animating to the touched position
                map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(latLng)
                        .zoom(13).build()));

                // Placing a marker on the touched position
                map.addMarker(markerOptions).showInfoWindow();
            }
        });*/
    }


     String currentLocation;
    @Override
    public void onLocationChanged(Location location)
    {
        Log.e(TAG, "onLocationChanged: "+location);
        lattitude=location.getLatitude();
        longitude=location.getLongitude();
        Log.e(TAG, "lattitude: "+lattitude);
        Log.e(TAG, "longitude: "+longitude);
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        Log.e("FusedLatt",currentLatitude+"");
        Log.e("FusedLong",currentLongitude+"");

        LatLng  latLng=new LatLng(lattitude,longitude);

//        LatLng latLng=new LatLng(30.7046,76.7179);
        map.addMarker(new MarkerOptions().position(latLng)).showInfoWindow();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(latLng)
                .zoom(13).build()));

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            Log.e(TAG, "onLocationChanged: List "+addresses.toString() );
            Log.e(TAG, "onLocationChanged: Location1 "+addresses.get(0).getAddressLine(0) );
            currentLocation=addresses.get(0).getAddressLine(0);


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.e(TAG, "onStatusChanged: "+status);
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Log.e(TAG, "onProviderEnabled: "+provider);
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Log.e(TAG, "onProviderDisabled: "+provider);
    }

        @Override
        public void onConnected(@Nullable Bundle bundle) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            Log.e(TAG, "onConnected: Location"+ location );
//location.setAccuracy(ACCURACY_FINE);
            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

         /*   if (location1 == null) {
                latLngResponse.currentLatLng(0.0, 0.0);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,
                        1.0f, this);
            } else {
                latLngResponse.currentLatLng(location1.getLatitude(), location1.getLongitude());
            }*/



            try{
                lattitude = location.getLongitude();
                longitude = location.getLatitude();

                Log.e("onConnectedValueLat", lattitude+"");
                Log.e("onConnectedValueLng", longitude+"");


                LatLng  latLng=new LatLng(lattitude,longitude);

//        LatLng latLng=new LatLng(30.7046,76.7179);
                map.addMarker(new MarkerOptions().position(latLng)).showInfoWindow();



                map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(latLng)
                        .zoom(13).build()));
            }catch (Exception ex)
            {
                ex.printStackTrace();
            }



/*

            if (location == null) {

                latLngResponse.currentLatLng(0.0, 0.0);

//            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
            } else {

                //If everything went fine lets get latitude and longitude
                latLngResponse.currentLatLng(location.getLatitude(), location.getLongitude());
            }
*/

        }

        private void callCheckOngoingBooking()
        {
            Log.e(TAG, "callCheckOngoingBooking: "+" inside" );
            new RetrofitService(this, this, Constant.API_PARENT_ONGOING_RIDES ,
                    560, 1,"1").callService(true);
        }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.e("onConnectionSuspended  ",i+"");
    }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult((AppCompatActivity) this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
            }

        }
        SharedPrefUtil sharedPrefUtil;
    @Override
    protected void onResume()
    {
        Log.e(TAG, "onResume: " + "onResumeMethod");

        //todo as lomg as live track is going for one booking

        if (sharedPrefUtil.getString(SharedPrefUtil.LIVE_TRACKING_ONGOING,"").
                equalsIgnoreCase("1"))
        {
            rlBook.setVisibility(View.VISIBLE);
        }
        else
        {
            rlBook.setVisibility(View.GONE);
        }

        callCheckOngoingBooking();
        super.onResume();

//        getLoc();
//        getLocation();
       // mGoogleApiClient.connect();
    }

    String onGoing="0";

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
       /* if (mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    (com.google.android.gms.location.LocationListener) this);
            mGoogleApiClient.disconnect();
        }*/
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
        Log.e(TAG, "onInfoWindowClick: Marker "+marker);
    }


/*    @Override
    public void onMapLoaded() {
        getLocation();
    }*/



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

   /* @Override
    public void currentLatLng(double lat, double lng) {

        Log.e(TAG, "currentLatLng: InsideCurrent LatLng " );
        try {

            currentLatitude = lat;
            currentLongitude = lat;
            if (getIntent().getStringExtra("key").equalsIgnoreCase("new")) {
                lattitude = currentLatitude;
                longitude = currentLongitude;
            }
            Log.e(TAG, "currentLatLng : " + currentLatitude + " " + currentLongitude);
            Log.e(TAG, "currentLatLng1 : " + lattitude + " " + longitude);
            //setCurrentLocation();

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
*/



    public void createMarkers(JSONObject jsonObj)
    {

        try {

            lattitude = Double.parseDouble(jsonObj.getString("lat"));
            longitude = Double.parseDouble(jsonObj.getString("lng"));
            Log.e("Latt", lattitude + " " + longitude);

            Bitmap image = CustomMarker.customImage(context, R.drawable.location_black);


            if (map != null) {
                map.clear();
            }

            LatLng latLng = new LatLng(lattitude, longitude);
            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("It's you")
                    .snippet(GeoLocation.getAddress(context, lattitude,longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(image)));

            GooglePulseAnimation.addPulseEffect(latLng, map);

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.5f));


            //todo new
         /*  Map.setInfoWindowAdapter(new MyInfoWindowAdapter(AddLocation.this));

            // Adding and showing marker while touching the GoogleMap
            Map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng arg0) {
                    // Clears any existing markers from the GoogleMap
                    Map.clear();

                    // Creating an instance of MarkerOptions to set position
                    MarkerOptions markerOptions = new MarkerOptions();

                    // Setting position on the MarkerOptions
                    markerOptions.position(arg0);

                    // Animating to the currently touched position
                    Map.animateCamera(CameraUpdateFactory.newLatLng(arg0));

                    // Adding marker on the GoogleMap
                    Marker marker = Map.addMarker(markerOptions);

                    // Showing InfoWindow on the GoogleMap
                    marker.showInfoWindow();

                }
            });*/

/*
    Map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker arg0) {

                    View v = getLayoutInflater().inflate(R.layout.custom_map_window, null);
                    LatLng latLng = arg0.getPosition();
                    TextView tvLocation = (TextView) v.findViewById(R.id.tvLocation);
                    ImageView ivLocation = (ImageView) v.findViewById(R.id.ivLocation);
                    tvLocation.setText(location.getText().toString().trim());


                    return v;

                }
            });
*/




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        @Override
        public void showPermissionAlert(ArrayList<String> permissionList, int code)
        {
            Log.e(TAG, "showPermissionAlert: "+"Popup" );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                Log.e(TAG, "showPermissionAlert: "+"Inside" );
                requestPermissions(permissionList.toArray(new String[permissionList.size()]), code);

            }
        }
    }
