package com.taxi.nanny.views.driver;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.location.GPSTracker;
import com.taxi.nanny.utils.location.permission.Permission;
import com.taxi.nanny.utils.location.permission.PermissionGranted;
import com.taxi.nanny.utils.realTimeTrack.GetCurrentLocation;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.home.ParentHome;
import com.taxi.nanny.views.login_section.login.LoginActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*OnMapReadyCallback, LocationListener
            , GoogleMap.OnInfoWindowClickListener,GoogleApiClient.ConnectionCallbacks,PermissionGranted,
            RetrofitResponse,GoogleApiClient.OnConnectionFailedListener*/

public class DriverHome  extends BaseActivity implements OnMapReadyCallback
        , GoogleMap.OnInfoWindowClickListener,GoogleApiClient.ConnectionCallbacks,
        Callback<ResponseBody>, RetrofitResponse, PermissionGranted,LocationListener,
        GetCurrentLocation.LatLngResponse,GoogleApiClient.OnConnectionFailedListener

    {
    public static  String TAG=DriverHome.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    Context context;
    private GPSTracker gpsTracker;
    public Marker m1;
    MyLocationListener locationListener;
    LocationManager locationManager;
    double lattitude, longitude;
    GoogleMap map;
    String result;
    String token = "";
    SharedPrefUtil sharedPrefUtil;
    String currentLocation="";

        GoogleApiClient googleApiClient = null;

    @BindView(R.id.ivLogout)
    CircleImageView ivLogout;

    public Socket mSocket;

    GPSTracker gpsTracker1;
        LocationRequest locationRequest;

    @Override
    protected int getContentId()
    {
        return R.layout.driver_home;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sharedPrefUtil = SharedPrefUtil.getInstance();
        token = sharedPrefUtil.getString(Constant.TOKEN, "");
        Permission.checkPermissionLocation(this, this);
        setTab();
        getLocation();
        gpsTracker1=new GPSTracker(DriverHome.this);

        if (gpsTracker1.canGetLocation())
        {
            Log.e(TAG, "onCreate: GPSLattitude "+gpsTracker1.getLatitude());
            Log.e(TAG, "onCreate: GPSLongitude "+gpsTracker1.getLongitude());
        }
        else
        {
//            gpsTracker.showSettingsAlert();
            locationRequestt();
        }

        getCurrentLocation();
        initializeMap();
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

        private void locationRequestt() {
            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API).addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks)
                                DriverHome.this)
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

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.DONUT) {
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                        @Override
                        public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

                            final Status status = locationSettingsResult.getStatus();
                            final LocationSettingsStates state = locationSettingsResult.getLocationSettingsStates();

                            switch (status.getStatusCode()) {
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
                                        status.startResolutionForResult(DriverHome.this, 1000);
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


    public void checkPermissionLocation(Context context)
    {
        Log.e(TAG, "checkPermissionLocation: "+"Inside");

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

            Log.e(TAG, "getLocation: Lattitude "+lattitude );
            Log.e(TAG, "getLocation: Lomgitude "+longitude );

            //  showMarkers();
        }
        else
        {
//            gpsTracker.showSettingsAlert();
            locationRequestt();
        }
    }

    String provider="";
        private void getCurrentLocation() {
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

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1L, 1f,
                    locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1L, 1f,
                    locationListener);

            Location oldLocation = locationManager.getLastKnownLocation(provider);

            if (oldLocation != null)
            {
                Log.v(TAG, "Got Old location");
                lattitude = oldLocation.getLatitude();
                longitude = oldLocation.getLongitude();

                Log.e(TAG, "OldLocationLat:  "+lattitude);
                Log.e(TAG, "OldLocationLng:  "+longitude);

            } else
            {
                Log.v(TAG, "NO Last Location found");
            }

        }

        private void getLatLong()
        {
            Log.e("Login", "getLatLong: " + "GetLocation");
            new GetCurrentLocation(DriverHome.this,this);
        }

        @Override
        public void currentLatLng(double lat, double lng) {
            if (lat == 0.0) {
                getLatLong();
            } else {
                this.lattitude = lat;
                this.longitude = lng;
            }
            Log.e(TAG, "currentLatLngLattttt "+lattitude );
            Log.e(TAG, "currentLatLngLongitude "+longitude );
        }




        private class MyLocationListener implements LocationListener
        {

            public void onLocationChanged(Location location) {

                Log.e(TAG, "onLocationChanged: "+"inside" );
                lattitude = location.getLatitude();
                longitude = location.getLongitude();

                Log.e(TAG, "onLocationChanged:Lattitude "+lattitude );
                Log.e(TAG, "onLocationChanged:Longitude "+longitude );

                LatLng  latLng=new LatLng(lattitude,longitude);

//        LatLng latLng=new LatLng(30.7046,76.7179);
                map.addMarker(new MarkerOptions().position(latLng)).showInfoWindow();

                map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(latLng)
                        .zoom(13).build()));

           /* Toast.makeText(DriverHome.this, "LattitudeChange--- "+
              lattitude+"LongitudeCHange----"+longitude, Toast.LENGTH_SHORT).show();*/

                if (SharedPrefUtil.getInstance().getString(SharedPrefUtil.USERTYPE).equalsIgnoreCase("driver"))
                {
                    Log.e(TAG, "Update: "+"InsideCondition" );
                    updateLoc();
                }

                locationManager.removeUpdates(this);
            }

            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.v(TAG, "onStatusChanged: " + s);
            }

            public void onProviderEnabled(String s) {
                Log.e(TAG, "onProviderEnabled " + s);
            }

            public void onProviderDisabled(String s) {
                Log.e(TAG, "onProviderDisabled " + s);
            }
        }


      /*  private void getCurrentLocation()
    {
        Log.e(TAG, "getCurrentLocation: "+"Inside");
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

        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }


    }*/

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
                        Intent intent = new Intent(DriverHome.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else
                        {

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
        }
    }


    public void initializeMap()
    {
        Log.e(TAG, "initializeMap: "+"insideinitializeViewMethod" );
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.e(TAG, "initializeMap: fragmentManager "+fm );
        fm.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map=googleMap;
        LatLng latLng=new LatLng(lattitude,longitude);
//        LatLng latLng=new LatLng(30.6496,76.7567);
//        map.addMarker(new MarkerOptions().position(latLng).title("Marker in Mohali"));
        map.addMarker(new MarkerOptions().position(latLng)).showInfoWindow();
        //    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        Log.e(TAG, "onMapReady: "+googleMap.getCameraPosition());

      /*  map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView
                        (DriverHome.this, "Mohali", 1)))).showInfoWindow();*/

        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(latLng)
                .zoom(13).build()));

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener()
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



             /*   map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView
                                (DriverHome.this, "Mohali", 1)))).showInfoWindow();*/
            }
        });
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        if (GPSTracker.isFromSetting==true){
            finish();
            startActivity(getIntent());
            GPSTracker.isFromSetting=false;
        }

        getLatLong();

        if (SharedPrefUtil.getInstance().getString(SharedPrefUtil.USERTYPE).equalsIgnoreCase("driver"))
        {
            updateLoc();
        }
    }

        @Override
        public void onBackPressed()
        {
            super.onBackPressed();
            if (GPSTracker.isFromSetting==true)
            {
                finish();
                startActivity(getIntent());
                GPSTracker.isFromSetting=false;
            }
        }

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

             /*   map.put("lattitude", String.valueOf("30.7046"));
                map.put("longitude", String.valueOf("76.7179"));*/
//                map.put("location", currentLocation);
                map.put("location", "Mohali");

                Log.e(TAG, "run: UpdateLocationParams" + map.toString());
               // api(map,DriverHome.this,token,12);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE)
                {
                    Log.e(TAG, "Inside " +"inside");
                    new UpdateLoc(map).execute(Constant.API_UPDATE_DRIVER_LOCATION);
                  /*  emitUpdateLocation("30.7046","76.7179",
                            "1","2","34","1");*/
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 10000);
    }



    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);

        Log.e(TAG, "onResponse: General<< "+generalResponse);
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {

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

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class UpdateLoc extends AsyncTask<String, Void, String>
    {
        HashMap<String, String> params;
        public UpdateLoc(HashMap<String, String> params)
        {
            Log.e(TAG, "InsideConstructor " +"inside");
            this.params = params;
        }

        @Override
        protected String doInBackground(String... strings)
        {
            try {
                Log.e(TAG, "InsideDoingBackground " +"inside");
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
                    Log.e("buildercontains", builder.toString());
                    is.close();
                    result = builder.toString();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        private String getPostData(HashMap<String, String> postparams)
        {
            Log.e("ParamsPostData>> ", postparams.toString());
            StringBuilder builder = new StringBuilder();
            if (postparams != null) {
                for (Map.Entry<String, String> entry : postparams.entrySet()) {
                    try {
                        builder.append("&");
                        builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                        builder.append("=");
                        builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Log.e("Hello>> ", builder.toString());
                Log.e("PostData>> ", builder.toString());

                return builder.toString();
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s)
        {
            Log.e("PostExecute >> ", "Execute");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE)
            {
                super.onPostExecute(s);
            }
        }
    }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

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



        @Override
    public void onInfoWindowClick(Marker marker)
    {

    }

}
