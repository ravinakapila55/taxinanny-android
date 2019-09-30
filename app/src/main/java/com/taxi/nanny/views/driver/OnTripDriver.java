package com.taxi.nanny.views.driver;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;
import com.taxi.nanny.CallApiBackgroundService;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.DriverLatLngModel;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.directionhelpers.DataParser;
import com.taxi.nanny.utils.location.CustomMarker;
import com.taxi.nanny.utils.location.GPSTracker;
import com.taxi.nanny.utils.location.LocationDistanceDuration;
import com.taxi.nanny.utils.location.TrackGoogleLocation;
import com.taxi.nanny.utils.realTimeTrack.GetCurrentLocation;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.booking.CollectCash;
import com.taxi.nanny.views.booking.ParentProfile;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

public class OnTripDriver extends BaseActivity implements OnMapReadyCallback,
        LocationDistanceDuration, Callback<ResponseBody>,
        GetCurrentLocation.LatLngResponse, RetrofitResponse
{
    public static final String TAG = OnTripDriver.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    Context context;
    private GPSTracker gpsTracker;
    static Marker marker;
    GoogleMap map;
//    LocationListener locationListener;
    LocationListener locationListener;
    LocationManager locationManager;
    double lattitude, longitude;

    @BindView(R.id.tvStart)
    TextView tvStart;

    ArrayList<LatLng> MarkerPoints;

    String trip = "0";

    @BindView(R.id.tv)
    TextView tv;
    ArrayList<RiderListModel> riderList;

    @BindView(R.id.tvDrop)
    TextView tvDrop;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvDistanceeeee)
    TextView tvDistanceeeee;

    @BindView(R.id.tvTimeOnline)
    TextView tvTimeOnline;

    @BindView(R.id.ivUser)
    ImageView ivUser;

    @BindView(R.id.tvComplete)
    TextView tvComplete;

    @BindView(R.id.tvLocLabel)
    TextView tvLocLabel;

    double lattDrop, longDRop;
    double lattPick, longPick;

    @BindView(R.id.ivCall)
    ImageView ivCall;

    String token = "";
    SharedPrefUtil sharedPrefUtil;

    GPSTracker gpsTracker1;
    CallApiBackgroundService apiBackgroundService;

    @BindView(R.id.tvPref)
    TextView tvPref;

    String sign_in="",sign_up="",booking_time="",bookingType="";

    @BindView(R.id.ivMap)
    CircleImageView ivMap;

    @BindView(R.id.rlMap)
    RelativeLayout rlMap;

    @BindView(R.id.tvEnd)
    TextView tvEnd;

    ArrayList<DriverLatLngModel> latLngList=new ArrayList<>();


    @Override
    protected int getContentId()
    {
//        return R.layout.ontrip;
        return R.layout.on_trip_driver;
    }

    LatLng  latLngPick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("OnTrip");

        sharedPrefUtil = SharedPrefUtil.getInstance();
        token = sharedPrefUtil.getString(Constant.TOKEN, "");

        if (getIntent().hasExtra("rider_list"))
        {
            riderList = (ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
            lattDrop = Double.parseDouble(riderList.get(0).getDroplat());
            longDRop = Double.parseDouble(riderList.get(0).getDroplng());
            lattPick = Double.parseDouble(riderList.get(0).getPickLat());
            longPick = Double.parseDouble(riderList.get(0).getPicklng());
            pickUp = riderList.get(0).getPickup();
            dropUp = riderList.get(0).getDropup();

            sign_in=riderList.get(0).getKids_in();
            sign_up=riderList.get(0).getKids_out();
            booking_time=getIntent().getExtras().getString("booking_time");
            bookingType=getIntent().getExtras().getString("bookingType");


           if (bookingType.equalsIgnoreCase("1"))
           {
               tvEnd.setVisibility(View.GONE);

           }
           else
           {
               tvEnd.setVisibility(View.VISIBLE);
               tvEnd.setText("Cancel");
           }

            Log.e(TAG, "onCreate: OnTripPICK " + pickUp);
            Log.e(TAG, "onCreate: OnTripDrop " + dropUp);

            Log.e(TAG, "onCreate: PickLat " + lattPick);
            Log.e(TAG, "onCreate: PickLng " + longPick);
            Log.e(TAG, "onCreate: DropLat " + lattDrop);
            Log.e(TAG, "onCreate: longDRop " + longDRop);
            Log.e(TAG, "onCreate: sign_in " + sign_in);
            Log.e(TAG, "onCreate: sign_up " + sign_up);
            Log.e(TAG, "onCreate: booking_time " + booking_time);

            latLngPick=new LatLng(lattPick,longPick);


        /*    String ridePref="";

            if (!sign_in.equalsIgnoreCase("0") || !sign_up.equalsIgnoreCase("0"))
            {
                tvPref.setVisibility(View.VISIBLE);

                if (sign_in.equalsIgnoreCase("1") && sign_up.equalsIgnoreCase("1"))
                {

                    ridePref="Kids-in,Kids-out";
                }  else if (sign_in.equalsIgnoreCase("1") && sign_up.equalsIgnoreCase("0"))
                {
                    ridePref="Kids-in";
                } else if (sign_in.equalsIgnoreCase("0") && sign_up.equalsIgnoreCase("1"))
                {
                    ridePref="Kids-out";
                }

                String finalRidePref = ridePref;
                tvPref.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        callAlertPref(finalRidePref);
                    }
                });
            }
            else
            {
                tvPref.setVisibility(View.GONE);
            }*/

        }

        checkPermissionLocation(this);
        getLocation();


        gpsTracker1=new GPSTracker(OnTripDriver.this);

        if (gpsTracker1.canGetLocation())
        {
            Log.e(TAG, "onCreate: GPSLattitude "+gpsTracker1.getLatitude());
            Log.e(TAG, "onCreate: GPSLongitude "+gpsTracker1.getLongitude());
        }
        else
        {
            gpsTracker.showSettingsAlert();
        }

        getCurrentLocation();
        initializeMap();
        setData();
        delay();
        connectSocket(riderList.get(0).getBooking_id());
    }

    public void callAlertPref(String pref)
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


        tvMsg.setText(pref);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void getLatLong()
    {
        Log.e("Login", "getLatLong: " + "GetLocation");
        new GetCurrentLocation(OnTripDriver.this,this);
    }

    @Override
    public void currentLatLng(double lat, double lng)
    {
        if (lat == 0.0)
        {
            getLatLong();
        } else
        {
            this.lattitude = lat;
            this.longitude = lng;
        }
        Log.e(TAG, "currentLatLngLattttt "+lattitude );
        Log.e(TAG, "currentLatLngLongitude "+longitude );
    }

    String pickUp = "", dropUp = "";
    Socket mSocket;

    public void connectSocket(String rideId)
    {
        try {
            Log.e(TAG, "connectSocket: " + "inside");
            Log.e(TAG, "Url: " + Constant.SOCKET_URL + rideId);
            mSocket = IO.socket(Constant.SOCKET_URL + rideId);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onTimeConnectError);
            mSocket.on("callbackLoc", callbackLoc);
            mSocket.on("callbackStatus", callbackStatus);
            mSocket.connect();
            //emitRoomJoin();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void emitUpdateLocation(String lat, String lng, String bearing, String accuracy,
                                   String speed, String altitude) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lat", lat);
            jsonObject.put("lng", lng);
            jsonObject.put("bearing", bearing);
            jsonObject.put("accuracy", accuracy);
            jsonObject.put("speed", speed);
            jsonObject.put("altitude", altitude);
            Log.e(TAG, "emitUpdateLocation: Params " + jsonObject.toString());

            mSocket.emit("updateLoc", jsonObject);
            Log.e(TAG, "emitUpdateLocation: Afterrrrr " + jsonObject.toString());

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.e("OnResume ", "onResume");
        getLatLong();
        if (SharedPrefUtil.getInstance().getString(SharedPrefUtil.USERTYPE).
                equalsIgnoreCase("driver"))
        {
            updateLoc();
        }

    }

    private void updateLoc()
    {
        Log.e(TAG, "updateLoc: " + "Inside");

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE)
                {
                    emitUpdateLocation(String.valueOf(lattitude), String.valueOf(longitude),
                            "1", "2", "34", "1");
                    if (ApiResultStart.equalsIgnoreCase("1") && ApiResultComplete.equalsIgnoreCase("0"))
                    {
//                        callDriverUpdateLocation();//todo to update driver location for calculating actual price
                        apiBackgroundService=new CallApiBackgroundService(OnTripDriver.this,String.valueOf(lattitude),
                                String.valueOf(longitude),riderList.get(0).getBooking_id(),riderList.get(0).getRide_id());
                    }
                }
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 10000);
    }

    public void callDriverUpdateLocation()
    {
//        ApiCall="5";
        Log.e("emitUpdateLocation ","UpdateLat "+lattitude);
        Log.e("emitUpdateLocation ","UpdateLng "+longitude);

      /*  HashMap<String, String> param = new HashMap<>();
        param.put("ride_id", riderList.get(0).getRide_id());
        param.put("booking_id",riderList.get(0).getBooking_id());
        param.put("lattitude", String.valueOf(lattitude));
        param.put("longitude", String.valueOf(longitude));
        Log.e("callUpdateDriverLocationParams ",param.toString());
        api(param,this,token,38);*/

        JSONObject param=new JSONObject();

        try
        {
            param.put("ride_id", riderList.get(0).getRide_id());
            param.put("booking_id",riderList.get(0).getBooking_id());
            param.put("lat", String.valueOf(lattitude));
            param.put("long", String.valueOf(longitude));
            Log.e("API_ACTUAL_DRIVER_LOCAIONParams ",param.toString());
            new RetrofitService(this, this,
                    Constant.API_ACTUAL_DRIVER_LOCAION,param, 105, 2,"1").
                    callService(false);
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
            mSocket.emit("updateStatus", status);
            Log.e(TAG, "updateStatus: Inside " + status);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
     //   disconnectSocket();
    }

    private Emitter.Listener onConnect = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            Log.e(TAG, "call: onConnect" + args.length);
            Log.e(TAG, "call: onConnect" + args.toString());

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.e(TAG, "onConnect");
                    for (int jj = 0; jj < args.length; jj++)
                    {
                        Log.e(TAG, "OnConnectResponseDriverHome " + args[jj]);
                        updateStatus("1");
                    }
                }
            });
        }
    };

    String pickUpdateLatt="",driverUpdateLatt="";
    String pickUpdateLong="",driverUpdateLong="";


    private Emitter.Listener callbackLoc = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    Log.e(TAG, "onConnect");

                    //todo to update driver location to calclate actual route fair taken by driver

                    Log.e("ApiResultStart ",ApiResultStart);
                    Timer timer1 = new Timer();
                    if (ApiResultStart.equalsIgnoreCase("1") && ApiResultComplete.equalsIgnoreCase("0"))
                    {
                        TimerTask timerTask1=new TimerTask()
                        {
                            @Override
                            public void run()
                            {
                                callDriverUpdateLocation();//todo to update driver location for calculating actual price
                            }
                        };
                        timer1.scheduleAtFixedRate(timerTask1,0,60000);
                    }

                    for (int jj = 0; jj < args.length; jj++)
                    {
                        Log.e(TAG, "onConnectResponseOfSocketUpdate " + args[jj]);
                        try {
                            JSONObject data = (JSONObject) args[0];
                            String latt = "";
                            String longg= "";


                            if (data.getString("lat").equalsIgnoreCase("0"))
                            {
                                latt= String.valueOf(lattPick);
                            }
                            else {
                                latt = data.getString("lat");
                            }


                            if (data.getString("lng").equalsIgnoreCase("0"))
                            {
                                longg= String.valueOf(longPick);
                            }
                            else {
                                longg = data.getString("lng");
                            }


                            LatLng latLngDriverUpdate = new LatLng(Double.parseDouble(latt),
                                    Double.parseDouble(longg));

                            Log.e(TAG, "run:LatLngDriver "+latLngDriverUpdate+"");
                            Log.e(TAG, "run:latLngPick "+latLngPick+"");

                         //   Toast.makeText(OnTripDriver.this, "Update "+latt + "-"+longg, Toast.LENGTH_SHORT).show();

                            Log.e(TAG, "run:lattDriver "+latt+"");
                            Log.e(TAG, "run:longgDriver "+longg+"");

                            Log.e(TAG, "run:lattPick "+lattPick+"");
                            Log.e(TAG, "run:longPick "+longPick+"");

                            double driverLatt=Double.parseDouble(latt);
                            double driverLong=Double.parseDouble(longg);

                            driverUpdateLatt= new DecimalFormat("##.#").format(driverLatt);
                            driverUpdateLong= new DecimalFormat("##.#").format(driverLong);

                            Log.e(TAG, "pickUpdate "+driverUpdateLatt+"");
                            Log.e(TAG, "driverUpdate "+driverUpdateLong+"");

                            pickUpdateLatt= new DecimalFormat("##.#").format(lattPick);
                            pickUpdateLong= new DecimalFormat("##.#").format(longPick);

                            Log.e(TAG, "pickUpdate "+pickUpdateLatt+"");
                            Log.e(TAG, "driverUpdate "+pickUpdateLong+"");

                            if (driverUpdateLatt.equalsIgnoreCase(pickUpdateLatt) && driverUpdateLong.equalsIgnoreCase(pickUpdateLong) )
                            {
                                arriveStatus="1";
                                Log.e(TAG, "run:arriveStatus "+arriveStatus+"");

                                if (arriveStatus.equalsIgnoreCase("1") && testStatus.equalsIgnoreCase("1"))
                                {
                                    Log.e(TAG, "run:arriveStatusInsideCondition "+arriveStatus+"");
                                  //  callArrive(riderList.get(0).getRide_id());
                                }
                            }

                            if (marker==null)
                            {
                                MarkerOptions options=new MarkerOptions();
                                options.position(latLngDriverUpdate);
                                options.icon(BitmapDescriptorFactory.fromBitmap
                                        (CustomMarker.getMarkerViewMovement(OnTripDriver.this)));
                                marker=map.addMarker(options);
                              //  map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngDriverUpdate,15));

                                map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(latLngDriverUpdate)
                                        .zoom(13).build()));
                            }
                            else
                            {
                                marker.setPosition(latLngDriverUpdate);
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    String arriveStatus="0";
    String testStatus="1";

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
                        updateLoc();
                    }
                }
            });
        }
    };

    public void disconnectSocket()
    {
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onTimeConnectError);
        mSocket.off("callbackLoc", callbackLoc);
        mSocket.on("callbackStatus", callbackStatus);
    }

    private Emitter.Listener onDisconnect = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            Log.e(TAG, "call: onDisconnect" + args.length);
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            Log.e(TAG, "call: onConnectError" + args.length);
        }
    };

    private Emitter.Listener onTimeConnectError = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            Log.e(TAG, "call: onTimeConnectError" + args.length);
        }
    };

    String picLoc = "", dropLoc = "";

    public void setData()
    {
        Log.e(TAG, "setData: PickLat " + lattPick);
        Log.e(TAG, "setData: PickLng " + longPick);

        Log.e("BookingLocationPick ", picLoc);
        tvDrop.setText(pickUp);

        tvName.setText(riderList.get(0).getFirst_name() + " " + riderList.get(0).getLast_name());

        if (!riderList.get(0).getImage().equalsIgnoreCase(""))
        {
            Picasso.with(this).load(riderList.get(0).getImage()).
                    placeholder(getResources().getDrawable(R.drawable.pic_dummy_user))
                    .into(ivUser);
        }
    }

    public void delay()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                callArrive(riderList.get(0).getRide_id());
            }
        }, 20000);
    }

    public void checkPermissionLocation(Context context)
    {
        Log.e(TAG, "checkPermissionLocation: " + "Inside");
        if (Build.VERSION.SDK_INT >= 23)
        {
            int hasNetworkPermission = context.checkSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE);
            int hasaccessCoarseLocation = context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasAccessFineLocation = context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            int hasInternetPermission = context.checkSelfPermission(android.Manifest.permission.INTERNET);
            ArrayList<String> permissionList = new ArrayList<String>();

            if (hasInternetPermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.INTERNET);
            }
            if (hasNetworkPermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.ACCESS_NETWORK_STATE);
            }
            if (hasaccessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (hasAccessFineLocation != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (!permissionList.isEmpty()) {
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

            Log.e(TAG, "getGPSLocation: Lattitude " + lattitude);
            Log.e(TAG, "getGPSLocation: Lomgitude " + longitude);
        }
        else
        {
         gpsTracker.showSettingsAlert();
        }
    }

    String provider;
    private void getCurrentLocation()
    {
        Log.e(TAG, "getCurrentLocation: " + "Inside");

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(criteria, true);
        locationListener=new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1.0f,
                locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1.0f,
                locationListener);

   /*    locationManager. requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
      locationManager.  requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
*/
        Location oldLocation = locationManager.getLastKnownLocation(provider);

        if (oldLocation != null)
        {
            Log.v(TAG, "Got Old location");
            lattitude = oldLocation.getLatitude();
            longitude = oldLocation.getLongitude();

        } else
        {
            Log.v(TAG, "NO Last Location found");
        }

    }

    public void callCancelAlert()
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
        tvMsg.setText("Are you sure you Want to cancel this ride?");

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
                alertDialog.dismiss();
                callCancelRide();
            }
        });
        alertDialog.show();
    }

    public void callCancelRide()
    {
        ApiCall="4";
        HashMap<String, String> param = new HashMap<>();
        param.put("booking_id", riderList.get(0).getBooking_id());

        Log.e("callStartParams ",param.toString());
        api(param,this,token,37);
    }

    @OnClick({R.id.img_back_btn,R.id.tvStart,R.id.ivCall,R.id.tvComplete,R.id.rlMap,R.id.tvEnd,R.id.ivUser})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                finish();
                break;


                case R.id.tvEnd:
                callCancelAlert();
                break;


                case R.id.ivUser:
                Intent intent=new Intent(OnTripDriver.this, ParentProfile.class);
                intent.putExtra("rider_list",(Serializable) riderList);
                intent.putExtra("time",actualTimeSet);
                startActivity(intent);
                break;

           case R.id.tvStart:
               tvDrop.setText(dropUp);
               tvStart.setVisibility(View.GONE);
               tvLocLabel.setText("Drop Up Location");
               tv.setText("DROP OFF");
               ApiCall="2";
               updateStatus("3");//emit on the way socket
               callStart(riderList.get(0).getRide_id());
               break;

            case R.id.ivCall:

                String phone = riderList.get(0).getPhone_number();
                Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent2);
                break;


           case R.id.tvComplete:
               ApiCall="3";
               updateStatus("4");//emit on the way socket
               callComplete(riderList.get(0).getRide_id(),riderList.get(0).getBooking_id());

                break;

          case R.id.rlMap:
                openGoogleMap();
                 break;
        }
    }

    public void openGoogleMap()
    {
        String uri = "http://maps.google.com/maps?daddr=" + lattDrop + "," +longDRop;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    public void setMarkers()
    {
        trip="1";
        LatLng  latLng=new LatLng(lattPick,longPick);
        LatLng latLng1=new LatLng(lattDrop,longDRop);


        map.clear();

        String pick=riderList.get(0).getPickup();
        String drop=riderList.get(0).getDropup();
        //to draw poly line
        new TrackGoogleLocation(this, map, this).setMarkerLocate(latLng, latLng1,
                10,picLoc,dropLoc);

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

        String getDistance=disList.get(0);

        //todo for usa
        if (getDistance.contains("mi"))
        {
            Log.e("Inside ","if");
            tvDistanceeeee.setText(getDistance);
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

            tvDistanceeeee.setText(String.valueOf(DistancePppp) +" mi");
        }
        tvTimeOnline.setText(timList.get(0));
        actualTimeSet=timList.get(0);
    }

    String actualTimeSet="";

    String DistancePppp="";

    double accDistance,accDistMiles;
    int acccccDist;

    String ApiCall="1";
    String ApiResultStart="0";
    String ApiResultComplete="0";
    //1-arrive
    //2-start
    //3-complete
    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        Log.e(TAG, "onResponse:OnTrip  "+response);
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);
        Log.e(TAG, "generalResponse  "+generalResponse);
        Log.e(TAG, "ApiCall  "+ApiCall);

        //{ride_id=368}
        try{
            //for arrive
            if (ApiCall.equalsIgnoreCase("1"))
            {
                //for arrive
                JSONObject jsonObject = generalResponse.getResponse_object();
                Log.e(TAG, "onResponse: ArriveResponse "+jsonObject.toString());

                /*{"status":"true","message":"Driver has been reached",
                "fcm":{"multicast_id":6497854775417531774,"success":0,"failure":1,"canonical_ids":0,
                "results":[{"error":"NotRegistered"}]},"ridestatus":1}*/
                if (jsonObject.getString("status").equalsIgnoreCase("true"))
                {
                    ApiResultStart="0";
                    arriveStatus="0";
                    testStatus="0";
                    tvStart.setVisibility(View.VISIBLE);
                    tvEnd.setVisibility(View.GONE);
                    tvComplete.setVisibility(View.GONE);
                }
            }
            //for start
            else if (ApiCall.equalsIgnoreCase("2"))
            {
                //for start

                JSONObject jsonObject = generalResponse.getResponse_object();
                Log.e(TAG, "onResponse: StartResponse "+jsonObject.toString());
                if (jsonObject.getString("status").equalsIgnoreCase("true"))
                {
                    ApiResultStart="1";
                    ApiResultComplete="0";
                    tvStart.setVisibility(View.GONE);

                    tvComplete.setVisibility(View.VISIBLE);
                    rlMap.setVisibility(View.VISIBLE);
            /*{"status":"true",
            "message":"Picked up by the driver",
            "fcm":{"multicast_id":7673492142047578712,"success":0,"failure":1,"canonical_ids":0,
            "results":[{"error":"NotRegistered"}]},"ridestatus":3}*/
                }
            }

           else if (ApiCall.equalsIgnoreCase("3"))
            {
                //for complete

                JSONObject jsonObject = generalResponse.getResponse_object();
                Log.e(TAG, "onResponse: CompleteResponse "+jsonObject.toString());
                if (jsonObject.getString("status").equalsIgnoreCase("true"))
                {
                    ApiResultComplete="1";
                  JSONObject object=jsonObject.getJSONObject("data");
                  Intent intent1=new Intent(OnTripDriver.this,CollectCash.class);
                  intent1.putExtra("distance",object.getString("distance"));
                  intent1.putExtra("fare",jsonObject.getString("Total_fare_deducted"));
                  intent1.putExtra("pick",pickUp);
                  intent1.putExtra("drop",dropUp);
                  startActivity(intent1);
                  finish();
                }
            }     //for cancel Ride
           else if (ApiCall.equalsIgnoreCase("4"))
            {
                //for camcelRIde
                JSONObject jsonObject = generalResponse.getResponse_object();
                Log.e(TAG, "onResponse: CancelRide "+jsonObject.toString());
                if (jsonObject.getString("status").equalsIgnoreCase("true"))
                {
                  Intent intent1=new Intent(OnTripDriver.this,DriverHome.class);
                  startActivity(intent1);
                  finish();
                }
            }

          else if (ApiCall.equalsIgnoreCase("5"))
            {
                //for camcelRIde

                JSONObject jsonObject = generalResponse.getResponse_object();
                Log.e(TAG, "onResponse: CancelRide "+jsonObject.toString());
                if (jsonObject.getString("status").equalsIgnoreCase("true"))
                {

                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {

    }

  /*  @Override
    public void onLocationChanged(Location location)
    {
        Log.e("CheckOnLocationChange "," location Change");
        lattitude=location.getLatitude();
        longitude=location.getLongitude();

        Toast.makeText(OnTripDriver.this, "LocChangeLattitude  :- "+lattitude, Toast.LENGTH_SHORT).show();
        Toast.makeText(OnTripDriver.this, "LocChangeLongitude  :- "+longitude, Toast.LENGTH_SHORT).show();


        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();


        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            currentLocation=addresses.get(0).getAddressLine(0);
            if (SharedPrefUtil.getInstance().getString(SharedPrefUtil.USERTYPE).equalsIgnoreCase("driver"))
            {
                updateLoc();
            }

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }*/


    private class FetchUrl extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... url)
        {
            Log.e("url ",url+"");
            // For storing data from web service
            String data = "";
            try
            {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.e("Background Task data", data.toString());
            }
            catch (Exception e)
            {
                Log.e("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result)
        {
            Log.e("onPostExecute ", result);
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
    {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData)
        {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.e("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.e("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.e("ParserTask","Executing routes");
                Log.e("ParserTask",routes.toString());
            } catch (Exception e)
            {
                Log.e("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result)
        {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            Log.e("ParseronPostExecute ",result+"");

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++)
            {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++)
                {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(7);
                lineOptions.color(Color.GRAY);
                Log.e("onPostExecute","onPostExecute lineoptions decoded");
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null)
            {
                Log.e("InsidelineOptions ",lineOptions+"");
                map.addPolyline(lineOptions);
            }
            else
            {
                Log.e("onPostExecute ","without Polylines drawn");
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException
    {
        Log.e("downloadUrl ",strUrl);
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            Log.e("url ",url+"");

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            data = sb.toString();
            Log.e("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.e("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public void initializeMap()
    {
        Log.e(TAG, "initializeMap: "+"insideinitializeViewMethod" );
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.e(TAG, "initializeMap: fragmentManager "+fm );
        fm.getMapAsync(this);
    }

    public String getCurrentLocationAddress(double lat,double lng)
    {
        Log.e(TAG, "getCurrentLocationAddress: lat "+lat );
        Log.e(TAG, "getCurrentLocationAddress: lng "+lng );
        String address="";
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Log.e(TAG, "addresses: lng "+addresses.size() );
            address=addresses.get(0).getAddressLine(0);
            Log.e(TAG, "addresses: address "+address);
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
        Log.e("INsideGoogleMap ","onMapReady");
        map=googleMap;
//        LatLng latLng=new LatLng(lattitude,longitude);
        LatLng latLng=new LatLng(lattPick,longPick);
        String bookingLoc=getCurrentLocationAddress(lattPick,longPick);

        setMarkers();

 /*       map.setOnMapClickListener(new GoogleMap.OnMapClickListener()
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
                map.addMarker(markerOptions);
            }
        });*/
    }

    //arrive api
    public void callArrive(String ride_id)
    {
        HashMap<String, String> param = new HashMap<>();
        param.put("ride_id", ride_id);

        Log.e("callArriveParams ",param.toString());
        api(param,this,token,18);
    }

    //start trip api
    public void callStart(String ride_id)
    {
        HashMap<String, String> param = new HashMap<>();
        param.put("ride_id", ride_id);

        Log.e("callStartParams ",param.toString());
        api(param,this,token,19);
    }

     //Complete api
    public void callComplete(String ride_id,String booking_id)
    {
        HashMap<String, String> param = new HashMap<>();
        param.put("ride_id", ride_id);
        param.put("booking_id", booking_id);

        Log.e("callCompleteParams ",param.toString());
        api(param,this,token,20);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture)
    {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {

            case 2:

                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permitions Allow", Toast.LENGTH_SHORT).show();
                        //  getLocation();
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        // checkPermissionLocation(context);
                        Toast.makeText(this, "Permitions Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            }
        }
    }

    private class MyLocationListener implements LocationListener
    {
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: "+"inside" );
            lattitude = location.getLatitude();
            longitude = location.getLongitude();

            Log.e(TAG, "onLocationChanged:Lattitude "+lattitude );
            Log.e(TAG, "onLocationChanged:Longitude "+longitude );

           /* Toast.makeText(OnTripDriver.this, "LattitudeChange--- "+
                    lattitude+"LongitudeCHange----"+longitude, Toast.LENGTH_SHORT).show();*/

         /*   if (waitingForLocationUpdate) {
                getNearbyStores();
                waitingForLocationUpdate = false;
            }*/

            if (SharedPrefUtil.getInstance().getString(SharedPrefUtil.USERTYPE).equalsIgnoreCase("driver"))
            {
                updateLoc();
            }

            locationManager.removeUpdates(this);
        }

        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.v(TAG, "Status changed: " + s);
        }

        public void onProviderEnabled(String s) {
            Log.e(TAG, "PROVIDER DISABLED: " + s);
        }

        public void onProviderDisabled(String s) {
            Log.e(TAG, "PROVIDER DISABLED: " + s);
        }
    }

    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 105:
                Log.e( "onResponse: ActualLocation " , response);
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {

                    }
                    else
                    {

                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                break;
        }
    }
}
