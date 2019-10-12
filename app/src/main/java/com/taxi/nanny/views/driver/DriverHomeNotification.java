package com.taxi.nanny.views.driver;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.location.GPSTracker;
import com.taxi.nanny.utils.location.LocationDistanceDuration;
import com.taxi.nanny.utils.location.TrackGoogleLocation;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.booking.PickDropConfirmation;
import com.taxi.nanny.views.login_section.login.LoginActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHomeNotification  extends BaseActivity implements OnMapReadyCallback, LocationListener
        , GoogleMap.OnInfoWindowClickListener,GoogleApiClient.ConnectionCallbacks, Callback<ResponseBody>,
        RetrofitResponse, LocationDistanceDuration
{
    public static  String TAG=DriverHomeNotification.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    Context context;
    private GPSTracker gpsTracker;
    public Marker marker;
    LocationListener locationListener;
    LocationManager locationManager;
    double lattitude, longitude;
    double lattitudeBook, longitudeBook;
    GoogleMap map;
    GoogleMap loc_map;
    String result;
    String token = "";

    SharedPrefUtil sharedPrefUtil;
    String currentLocation="";

    @BindView(R.id.ivLogout)
    CircleImageView ivLogout;

    ArrayList<RiderListModel> riderList;

    String bookingType="";
  /*
    double dropLat,dropLng;
    LatLng pick,drop;
  */

    @Override
    protected int getContentId()
    {
        return R.layout.driver_home_notifcation;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sharedPrefUtil = SharedPrefUtil.getInstance();
        token = sharedPrefUtil.getString(Constant.TOKEN, "");

        if (getIntent().hasExtra("rider_list"))
        {

            riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");

            bookingType=riderList.get(0).getBooking_type();

            Log.e(TAG, "onCreate: RideListSize "+riderList.size());
            Log.e(TAG, "onCreate: BookingType "+bookingType);

            lattitudeBook= Double.parseDouble(riderList.get(0).getPickLat());
            longitudeBook= Double.parseDouble(riderList.get(0).getPicklng());


//            double dropLat,dropLng;
//            LatLng pick,drop;

            LatLng latLng=null;
            LatLng latLng1=null;


            latLng=new LatLng(Double.parseDouble(riderList.get(0).getPickLat()),
                    Double.parseDouble(riderList.get(0).getPicklng()));
            latLng1=new LatLng(Double.parseDouble(riderList.get(0).getDroplat()),
                    Double.parseDouble(riderList.get(0).getDroplng()));

            Log.e(TAG, "callAlert:latLng "+latLng);
            Log.e(TAG, "callAlert:latLng1 "+latLng1);

            new TrackGoogleLocation(DriverHomeNotification.this,
                    DriverHomeNotification.this).getEstimate(latLng, latLng1);
        }

        setTab();

        checkPermissionLocation(this);

        getLocation();

        getCurrentLocation();


        //to show the rider's request

        initializeMap();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {

                callAlert();
            }
        },2000);
    }
    Socket mSocket;

    public void connectSocket(String rideId)
    {
        try
        {
            Log.e(TAG, "connectSocket: "+"inside");
            Log.e(TAG, "Url: "+Constant.SOCKET_URL+rideId);
            mSocket = IO.socket(Constant.SOCKET_URL+rideId);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onTimeConnectError);
            mSocket.on("callbackLoc",callbackLoc);
            mSocket.on("callbackStatus",callbackStatus);
            mSocket.connect();
            //emitRoomJoin();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    String update="0";
    public void emitUpdateLocation(String lat,String lng,String bearing,
                                   String accuracy,String speed,String altitude)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("lat",lat);
            jsonObject.put("lng",lng);
            jsonObject.put("bearing",bearing);
            jsonObject.put("accuracy",accuracy);
            jsonObject.put("speed",speed);
            jsonObject.put("altitude",altitude);
            Log.e(TAG, "emitUpdateLocation: Params "+jsonObject.toString());
            mSocket.emit("updateLoc",jsonObject);
            Log.e(TAG, "emitUpdateLocation: Afterrrrr "+jsonObject.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    public void updateStatus(String status)
    {
        try
        {
            mSocket.emit("updateStatus",status);
            Log.e(TAG, "updateStatus: Inside "+status);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private Emitter.Listener onConnect = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            Log.e(TAG, "call: onConnect"+args.length);
            Log.e(TAG, "call: onConnect"+args.toString());

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.e(TAG, "onConnect");
                    for (int jj = 0; jj < args.length; jj++)
                    {
                        Log.e(TAG, "OnConnectResponseDriverHome " + args[jj]);
                        update="1";
                        updateStatus("1");
                    }
                }
            });
        }
    };


    private void callRefreshAllTracks()
    {
        Log.e("InsideRefreahsAllTracks ","inside");
        new RetrofitService(this, this,
                "", 500, 1,"5").
                callService(true);
    }

    private Emitter.Listener callbackLoc = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.e(TAG, "onConnect");
                    for (int jj = 0; jj < args.length; jj++)
                    {
                        Log.e(TAG, "onConnectResponseOfSocketUpdate " + args[jj]);
                    }
                }
            });
        }
    };

    private Emitter.Listener callbackStatus = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.e(TAG, "onConnect");
                    for (int jj = 0; jj < args.length; jj++)
                    {
                        Log.e(TAG, "callbackStatus " + args[jj]);
                        Log.e(TAG, "callbackStatus: BookingType "+bookingType);
                        Intent intent=new Intent(DriverHomeNotification.this,OnTripDriver.class);
                        intent.putExtra("rider_list",(Serializable) rideList);
                      /*  intent.putExtra("sign_in",sign_in);
                        intent.putExtra("sign_up",sign_up);*/
                        intent.putExtra("bookingType",bookingType);
                        intent.putExtra("booking_time",booking_time);
                        startActivity(intent);
                    }
                }
            });
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
      //  disconnectSocket();
    }

    public void disconnectSocket()
    {
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onTimeConnectError);
        mSocket.off("callbackLoc", callbackLoc);
        mSocket.on("callbackStatus",callbackStatus);
    }

    private Emitter.Listener onDisconnect = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            Log.e(TAG, "call: onDisconnect"+args.length );
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            Log.e(TAG, "call: onConnectError"+args.length);

        }
    };

    private Emitter.Listener onTimeConnectError = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            Log.e(TAG, "call: onTimeConnectError"+args.length );
        }
    };

    public void delay()
    {
        Log.e(TAG, "delay: Inside" );
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Log.e(TAG, "delay: InsideRun " );
                //alertDialog.dismiss();
               /* Intent intent=new Intent(DriverHomeNotification.this,DriverHome.class);
                startActivity(intent);*/
            }
        },40000);
    }

    public void checkPermissionLocation(Context context)
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            int hasNetworkPermission = context.checkSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE);
            int hasaccessCoarseLocation = context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasAccessFineLocation = context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            int hasInternetPermission = context.checkSelfPermission(android.Manifest.permission.INTERNET);
            ArrayList<String> permissionList = new ArrayList<String>();
            if (hasInternetPermission != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(android.Manifest.permission.INTERNET);
            }
            if (hasNetworkPermission != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(android.Manifest.permission.ACCESS_NETWORK_STATE);
            }
            if (hasaccessCoarseLocation != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (hasAccessFineLocation != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (!permissionList.isEmpty())
            {
                requestPermissions(permissionList.toArray(new String[permissionList.size()]), 2);
            }
        }
    }

    public void getLocation()
    {
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.getLatitude() != 0.0)
        {
            longitude = gpsTracker.getLongitude();
            lattitude = gpsTracker.getLatitude();
            //  showMarkers();
        }
        else
        {
            gpsTracker.showSettingsAlert();
        }
    }

    private void getCurrentLocation()
    {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
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

    @OnClick({R.id.ivLogout})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ivLogout:
                callLogout();
                break;
        }
    }

    public void initializeMap()
    {
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getMapAsync(this);
    }

    public String getCurrentLocationAddress(double lat,double lng)
    {
        String address="";
        try
        {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

            address=addresses.get(0).getAddressLine(0);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return  address;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
//        map=googleMap;
        loc_map=googleMap;
        LatLng latLng=new LatLng(lattitude,longitude);
        LatLng latLng1=new LatLng(lattitudeBook,longitudeBook);
  //      String currentLoc=getCurrentLocationAddress(lattitude,longitude);
      String bookingLoc=getCurrentLocationAddress(lattitudeBook,longitudeBook);
      loc_map.addMarker(new MarkerOptions().position(latLng1).title("Marker in "+bookingLoc));
      loc_map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(latLng1)
                .zoom(13).build()));

/*
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng) {
                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                lattitude=latLng.latitude;
                longitude=latLng.longitude;

                // Clears the previously touched position
                map.clear();

                // Animating to the touched position
                map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(latLng)
                        .zoom(13).build()));

                // Placing a marker on the touched position
                map.addMarker(markerOptions);
            }
        });*/

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (SharedPrefUtil.getInstance().getString(SharedPrefUtil.USERTYPE).equalsIgnoreCase("driver"))
        {
            updateLoc();
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        lattitude=location.getLatitude();
        longitude=location.getLongitude();

        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            currentLocation=addresses.get(0).getAddressLine(0);
            if (SharedPrefUtil.getInstance().getString(SharedPrefUtil.USERTYPE).
                    equalsIgnoreCase("driver"))
            {
                updateLoc();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }

    @Override
    public void onProviderEnabled(String provider)
    {
    }

    @Override
    public void onProviderDisabled(String provider)
    {
    }

    private void updateLoc()
    {
        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                HashMap<String, String> map = new HashMap<>();

                map.put("driver_id", sharedPrefUtil.getString(SharedPrefUtil.USER_ID,""));
                map.put("lattitude", String.valueOf(lattitude));
                map.put("longitude", String.valueOf(longitude));

   /*             map.put("lattitude", String.valueOf("30.7046"));
                map.put("longitude", String.valueOf("76.7179"));*/
//                map.put("location", currentLocation);
                map.put("location", "Mohali");

                Log.e(TAG, "run: UpdateLocationParams" + map.toString());
                // api(map,DriverHome.this,token,12);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE)
                {
                    new UpdateLoc(map).execute(Constant.API_UPDATE_DRIVER_LOCATION);
                   /* emitUpdateLocation("30.7046","76.7179",
                            "1","2","34","1"); */
                  if (update.equalsIgnoreCase("1"))
                  {
                      emitUpdateLocation(String.valueOf(lattitude),String.valueOf(longitude),
                              "1","2","34","1");
                  }
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 10000);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    ProgressDialog progress;
    ArrayList<RiderListModel> rideList=new ArrayList<>();

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
     //Accept Response
    /*{"status":"true","message":"Booking Confirmed",
    "rider_data":[{"distance":42.49743570279375,
    "price":74.24615355419063,"time":1.37,"pickup_latitude":"30.900965",
    "pickup_longitude":"75.8572758","drop_latitude":"31.5143178",
    "drop_longitude":"75.911483","booking_id":"370"
    ,"rider_id":381,"ride_id":369,"mobile_number":"5850680555",
    "rider_data":{"id":381,"parent_id":73,"first_name":"eyyeey","last_name":"rwetye",
    "gender":"Female","dob":"1998-05-20",
    "image":"http:\/\/178.128.116.149\/taxinanny1\/public\/images\/dp\/cropped2128904748664220964.jpg",
    "need_booster":0,"sit_front_seat":0,"created_at":"2019-07-05 04:aa:42","updated_at":"2019-07-05 04:aa:42"},
    "ridestatus":1}]}
    */

        Log.e(TAG, "onResponse:AcceptRide  "+response);
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);
        Log.e(TAG, "generalResponse  "+generalResponse);


        if (type.equalsIgnoreCase("1"))
        {
            try {
                JSONObject jsonObject=generalResponse.getResponse_object();
                Log.e("AcceptJsonObject ",jsonObject.toString());
                if (jsonObject.getString("status").equalsIgnoreCase("true"))
                {
                    rideList.clear();
                    JSONArray rider_data=jsonObject.getJSONArray("rider_data");
                    for (int i = 0; i <rider_data.length() ; i++)
                    {
                        RiderListModel riderListModel=new RiderListModel();
                        JSONObject object=rider_data.getJSONObject(i);
                        JSONObject object1=rider_data.getJSONObject(0);
                        riderListModel.setPickLat(object.getString("pickup_latitude"));
                        riderListModel.setPicklng(object.getString("pickup_longitude"));
                        riderListModel.setDroplat(object.getString("drop_latitude"));
                        riderListModel.setDroplng(object.getString("drop_longitude"));
                        riderListModel.setId(object.getString("rider_id"));
                        riderListModel.setKids_in(object.getString("kids_sign_in"));
                        riderListModel.setKids_out(object.getString("kids_sign_up"));
                        riderListModel.setPick_instructions(object.getString("pick_up_instruction"));
                        riderListModel.setDrop_instructions(object.getString("drop_off_instruction"));
                        riderListModel.setBooking_id(object1.getString("booking_id"));
                        riderListModel.setRide_id(object1.getString("ride_id"));
                        riderListModel.setPhone_number(object1.getString("mobile_number"));
                        riderListModel.setEstDistance(object1.getString("distance"));
                        riderListModel.setEstPrice(object1.getString("price"));
                        riderListModel.setTime_eta(object1.getString("time"));

                        JSONObject rider_dataObj=object.getJSONObject("rider_data");

                        riderListModel.setFirst_name(rider_dataObj.getString("first_name"));
                        riderListModel.setLast_name(rider_dataObj.getString("last_name"));
                        riderListModel.setGender(rider_dataObj.getString("gender"));
                        riderListModel.setImage(rider_dataObj.getString("image"));
                        riderListModel.setDob(rider_dataObj.getString("dob"));

                        riderListModel.setPickup(riderList.get(0).getPickup());
                        riderListModel.setDropup(riderList.get(0).getDropup());
                        rideList.add(riderListModel);
                    }

                        Log.e(TAG, "onCreateHOMEEEEEE: RideID "+riderList.get(0).getBooking_id());

                    bookingId=riderList.get(0).getBooking_id();
                 /*   sign_in=jsonObject.getString("sign_in");
                    sign_up=jsonObject.getString("sign_up");*/
                    booking_time=jsonObject.getString("time_of_ride");

              /*      Log.e(TAG, "onResponse: SignIn "+sign_in );
                    Log.e(TAG, "onResponse: sign_up "+sign_up );*/
                    Log.e(TAG, "onResponse: booking_time "+booking_time);

                    progressDDD = new ProgressDialog(this);
                    progressDDD.setMessage("Processing...");
                    progressDDD.setCancelable(false);
                    progressDDD.show();
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run() {
//                            progress.dismiss();

                            progressDDD.dismiss();
                            callRefreshAllTracks();
//                            connectSocket("100");
                        }
                    },2000);

//                    connectSocket("100");
                  /*  Intent intent=new Intent(DriverHomeNotification.this,OnTripDriver.class);
                    intent.putExtra("rider_list",(Serializable) rideList);
                    startActivity(intent);*/
                }
                else
                {
                    Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        else if (type.equalsIgnoreCase("2"))
        {
            //{"status":"true","message":"Rejected","rider_data":[]}
            try
            {
                JSONObject jsonObject=generalResponse.getResponse_object();
                Log.e("REjectRequestResponse  ",jsonObject.toString());
                Intent intent=new Intent(DriverHomeNotification.this,DriverHome.class);
                startActivity(intent);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    String bookingId="";
    String sign_in="";
    String sign_up="";
    String booking_time="";

    ProgressDialog progressDDD;

    public void callLogout()
    {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("device_token", SharedPrefUtil.getInstance().getString(SharedPrefUtil.DEVICE_FCM_TOKEN,""));

            Log.e("LogoutParams",jsonObject.toString());
            new RetrofitService(this, this,
                    Constant.API_LOGOUT,jsonObject, 105, 2,"1").
                    callService(true);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(int RequestCode, String response)
    {
        super.onResponse(RequestCode,response);
        switch (RequestCode)
        {
            case 105:
                Log.e(TAG, "onResponse: Logout " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        SharedPrefUtil.getInstance().onLogout();
                        Intent intent = new Intent(DriverHomeNotification.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                break;

            case 500:
                Log.e("RefreshAllTracks ",response);
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Intent intent=new Intent(DriverHomeNotification.this,OnTripDriver.class);
                        intent.putExtra("rider_list",(Serializable) rideList);
                      /*  intent.putExtra("sign_in",sign_in);
                        intent.putExtra("sign_up",sign_up);*/
                        intent.putExtra("booking_time",booking_time);
                        intent.putExtra("bookingType",bookingType);
                        startActivity(intent);
//                        connectSocket(bookingId);
                    }
                },2000);
                break;
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {

    }

    TextView tvDist;
    TextView tvTime;
    TextView tvFare;

    public void callAlert()
    {
        Log.e("InsideAlertDialog ","Yessssssssss");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_pick_up_view, null);

        final TextView tvReject = (TextView) dialogView.findViewById(R.id.tvReject);
        final TextView tvAccept = (TextView) dialogView.findViewById(R.id.tvAccept);
        final TextView tvOk = (TextView) dialogView.findViewById(R.id.tvOk);
        tvTime = (TextView) dialogView.findViewById(R.id.tvTime);
        tvFare = (TextView) dialogView.findViewById(R.id.tvFare);
        tvDist = (TextView) dialogView.findViewById(R.id.tvDist);
        final TextView tvPickUp = (TextView) dialogView.findViewById(R.id.tvPickUp);
        final TextView tvDropUp = (TextView) dialogView.findViewById(R.id.tvDropUp);
        final TextView tvPickInstruction = (TextView) dialogView.findViewById(R.id.tvPickInstruction);
        final TextView tvDropInstruction = (TextView) dialogView.findViewById(R.id.tvDropInstruction);
        final TextView tvRidedate = (TextView) dialogView.findViewById(R.id.tvRidedate);

        final TextView tvRecurring = (TextView) dialogView.findViewById(R.id.tvRecurring);
        final TextView tvRidePrefLabel = (TextView) dialogView.findViewById(R.id.tvRidePrefLabel);
        final TextView tvRidePref = (TextView) dialogView.findViewById(R.id.tvRidePref);
        final TextView tvRideTime = (TextView) dialogView.findViewById(R.id.tvRideTime);
        final TextView tvRideTimeLabel = (TextView) dialogView.findViewById(R.id.tvRideTimeLabel);
        final View vieww1 = (View) dialogView.findViewById(R.id.vieww1);
        final TextView tvRideLabel = (TextView) dialogView.findViewById(R.id.tvRideLabel);
        final ImageView ivCancel = (ImageView) dialogView.findViewById(R.id.ivCancel);
        final LinearLayout llInsruction = (LinearLayout) dialogView.findViewById(R.id.llInsruction);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        AlertDialog alertDialog = dialogBuilder.create();
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        //  delay();
        tvPickUp.setText("Pick:- "+riderList.get(0).getPickup());
        tvDropUp.setText("Drop:- "+riderList.get(0).getDropup());

        if (!riderList.get(0).getPick_instructions().equalsIgnoreCase("null") ||
                !riderList.get(0).getDrop_instructions().equalsIgnoreCase("null"))
        {
            llInsruction.setVisibility(View.VISIBLE);
            if (!riderList.get(0).getPick_instructions().equalsIgnoreCase("null")
                    && riderList.get(0).getDrop_instructions().equalsIgnoreCase("null"))
            {
                tvDropInstruction.setVisibility(View.GONE);
                tvPickInstruction.setVisibility(View.VISIBLE);
                tvPickInstruction.setText("Pick Up:-"+riderList.get(0).getPick_instructions());
            } else if (riderList.get(0).getPick_instructions().equalsIgnoreCase("null")
                    && !riderList.get(0).getDrop_instructions().equalsIgnoreCase("null"))
            {
                tvDropInstruction.setVisibility(View.VISIBLE);
                tvPickInstruction.setVisibility(View.GONE);
                tvDropInstruction.setText("Drop Up:-"+riderList.get(0).getDrop_instructions());
            }
            else {
                tvDropInstruction.setVisibility(View.VISIBLE);
                tvPickInstruction.setVisibility(View.VISIBLE);
                tvDropInstruction.setText("Drop Up:-"+riderList.get(0).getDrop_instructions());
                tvPickInstruction.setText("Pick Up:-"+riderList.get(0).getPick_instructions());

            }
        }
        else if (riderList.get(0).getPick_instructions().equalsIgnoreCase("null")
                && riderList.get(0).getDrop_instructions().equalsIgnoreCase("null"))
        {
            llInsruction.setVisibility(View.GONE);
        }

        String getDistance=disList.get(0);

        //todo for usa
        if (getDistance.contains("mi"))
        {
            Log.e("Inside ","if");
            tvDist.setText(getDistance);
        }

        //todo for india
        else
        {
            Log.e("Inside ","Else");
            Number valuee = null;
            try {
                valuee= NumberFormat.getNumberInstance(java.util.Locale.US).parse(getDistance);
                //todo to remove comma inbetween the string like 1,234
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            accDistance=Double.parseDouble(String.valueOf(valuee));

            Log.e("accDistance",accDistance+"");

            accDistMiles = 0.621 * accDistance;
            Log.e("accDistMiles",accDistMiles+"");

            DistancePppp= new DecimalFormat("##.##").format(accDistMiles);

            tvDist.setText(String.valueOf(DistancePppp) +" mi");
        }

        tvTime.setText(timList.get(0));



        tvDropUp.setText("Drop:- "+riderList.get(0).getDropup());
        tvDropUp.setText("Drop:- "+riderList.get(0).getDropup());

        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.loc_map);
        fm.getMapAsync(this);

        double  dropLat= Double.parseDouble(riderList.get(0).getDroplat());
        double  dropLng= Double.parseDouble(riderList.get(0).getDroplng());

        LatLng pick=new LatLng(lattitudeBook,longitudeBook);
        LatLng drop=new LatLng(dropLat,dropLng);

        Log.e(TAG, "onCreate: PickLatLng "+pick);
        Log.e(TAG, "onCreate: DropLatLng "+drop);

      //  new TrackGoogleLocation(this, this).getEstimate(pick,drop);

       /* new TrackGoogleLocation(this, loc_map, this).setMarkerLocate(pick, drop, 10,
                riderList.get(0).getPickup(),"");*/


        SimpleDateFormat input = new SimpleDateFormat("HH:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm a");

        Date date = null;
        String validTime = "";


        try
        {
            date = input.parse(riderList.get(0).getTime_booking());
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        validTime = output.format(date);

        tvRideTime.setText(validTime);
        tvRidedate.setText(riderList.get(0).getBooking_date());

        //todo ride preference i.e. kids-in/kids-out
        if (riderList.get(0).getKids_in().equalsIgnoreCase("0") &&
                riderList.get(0).getKids_out().equalsIgnoreCase("0"))
        {
            tvRidePrefLabel.setVisibility(View.GONE);
            tvRidePref.setVisibility(View.GONE);
        }
        else
        {
            tvRidePrefLabel.setVisibility(View.VISIBLE);
            tvRidePref.setVisibility(View.VISIBLE);

            if (riderList.get(0).getKids_in().equalsIgnoreCase("1") && riderList.get(0).getKids_out().equalsIgnoreCase("1"))
            {
                tvRidePref.setText("Kids-in,Kids-out");
            }  else if (riderList.get(0).getKids_in().equalsIgnoreCase("1") && riderList.get(0).getKids_out().equalsIgnoreCase("0"))
            {
                tvRidePref.setText("Kids-in");
            } else if (riderList.get(0).getKids_in().equalsIgnoreCase("0") && riderList.get(0).getKids_out().equalsIgnoreCase("1"))
            {
                tvRidePref.setText("Kids-out");
            }
        }

        //todo first time booking
        if (riderList.get(0).getRide_status().equalsIgnoreCase("0"))
        {
            tvOk.setVisibility(View.GONE);
            tvAccept.setVisibility(View.VISIBLE);
            tvReject.setVisibility(View.VISIBLE);
        }
        //todo next time booking in case of recurring ride
        else
        {
            tvOk.setVisibility(View.VISIBLE);
            tvAccept.setVisibility(View.GONE);
            tvReject.setVisibility(View.GONE);
        }

        //todo for recurring
        if (riderList.get(0).getBooking_type().equalsIgnoreCase("1"))
        {
            tvRecurring.setVisibility(View.VISIBLE);
            vieww1.setVisibility(View.VISIBLE);
            tvRideLabel.setVisibility(View.VISIBLE);
        }
        else
        {
            tvRecurring.setVisibility(View.GONE);
            vieww1.setVisibility(View.VISIBLE);
            tvRideLabel.setVisibility(View.GONE);
        }

      //  double time=Double.parseDouble(riderList.get(0).getTime_eta());
        double price=Double.parseDouble(riderList.get(0).getEstPrice());
       // double distance=Double.parseDouble(riderList.get(0).getEstDistance());

        String amount= new DecimalFormat("##.##").format(price);
      //  String timeee= new DecimalFormat("##.##").format(time);
       // String distanceeee= new DecimalFormat("##.##").format(distance);

        tvFare.setText("$ "+amount);
   /*     tvTime.setText(timeee);
        tvDist.setText(distanceeee+ " miles");*/

        tvRecurring.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               Intent intent=new Intent(DriverHomeNotification.this,RecurringDaysBooking.class);
                intent.putExtra("daysRecurringList",(Serializable) riderList.get(0).getRideDaysList());
                intent.putExtra("end_date",riderList.get(0).getEnddate());
                startActivity(intent);
            }
        });

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        tvReject.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
                type="2";
                callReject(riderList.get(0).getBooking_id());
               // updateStatus("2");//reject emitter
            }
        });

        tvAccept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                type="1";
                callAccept(riderList.get(0).getBooking_id());
                alertDialog.dismiss();

              /*
                alertDialog.dismiss();*/
                //updateStatus("1");//accept emitter
            }
        });

        tvOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
                Intent intent=new Intent(DriverHomeNotification.this,OnTripDriver.class);
                intent.putExtra("rider_list",(Serializable) riderList);
                      /*  intent.putExtra("sign_in",sign_in);
                        intent.putExtra("sign_up",sign_up);*/
                intent.putExtra("bookingType",bookingType);
                intent.putExtra("booking_time",booking_time);
                startActivity(intent);
                startActivity(intent);
            }
        });
        alertDialog.show();
    }

    String type="0";

    public void callAccept(String bookingId)
    {
        HashMap<String, String> param = new HashMap<>();
        param.put("booking_id", bookingId);

        Log.e("AcceptParams ",param.toString());
        api(param,this,token,16);
    }

    public void callReject(String bookingId)
    {
        HashMap<String, String> param = new HashMap<>();
        param.put("booking_id", bookingId);
        Log.e("RejectParams ",param.toString());
        api(param,this,token,17);
    }

    ArrayList<String> distList=new ArrayList<>();
    ArrayList<String> timeList=new ArrayList<>();

    public   String actualDist="";
    public   String actualTime="";

    ArrayList<String> disList=new ArrayList<>();
    ArrayList<String> timList=new ArrayList<>();

    @Override
    public void getResult(String distance, String duration)
    {
        Log.e("ResultConfirmDistance  ",distance);
        Log.e("ResultConfirmDuration  ",duration);

        disList.add(distance);
        timList.add(duration);

        distList.clear();
        timeList.clear();

        distList.add(distance);
        timeList.add(duration);

        actualDist=distList.get(0);
        actualTime=timeList.get(0);

        Log.e("disListSize  ",disList.size()+"");

     /*   new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {

                callAlert();
            }
        },2000);
*/

    }

    String DistancePppp="";
    double accDistance,accDistMiles;
    int acccccDist;

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class UpdateLoc extends AsyncTask<String, Void, String>
    {
        HashMap<String, String> params;
        public UpdateLoc(HashMap<String, String> params)
        {
            this.params = params;
        }

        @Override
        protected String doInBackground(String... strings)
        {
            try {
                Log.e(TAG, "url<< " +strings[0]);
                URL url1 = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
                httpURLConnection.setRequestProperty("Authorization","Bearer "+token);//for adding headers
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setConnectTimeout(8000);
                httpURLConnection.connect();
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                os.write(getPostData(params).getBytes());
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                {
                    InputStream is = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        builder.append(line + "\n");
                        line = bufferedReader.readLine();
                    }
                    is.close();
                    result = builder.toString();

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }

        private String getPostData(HashMap<String, String> postparams)
        {
            Log.e("ParamsPostData>> ", postparams.toString());
            StringBuilder builder = new StringBuilder();
            if (postparams != null)
            {
                for (Map.Entry<String, String> entry : postparams.entrySet())
                {
                    try {
                        builder.append("&");
                        builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                        builder.append("=");
                        builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                Log.e("PostData>> ", builder.toString());

                return builder.toString();
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE)
            {
                super.onPostExecute(s);
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {

    }

}
