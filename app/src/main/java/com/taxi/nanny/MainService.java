/*
package com.taxi.nanny;

package com.antypro;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.antypro.Notification.FirebaseMessages;
import com.antypro.adapter.MainServiceAdapter;
import com.antypro.booking.SpBookingRequests;
import com.antypro.customer.FeedbackRequestList;
import com.antypro.customer.ServiceProviderList;
import com.antypro.customer.SubmitFeedback;
import com.antypro.map.GpsTracker;
import com.antypro.serviceProvider.services.SubServices;
import com.antypro.utils.App;
import com.antypro.utils.Constants;
import com.antypro.utils.PreferenceFile;
import com.antypro.utils.ServiceApi;
import com.antypro.utils.retrofit.RetrofitResponse;
import com.antypro.utils.retrofit.RetrofitService;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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

public class MainService extends Drawer implements View.OnClickListener, RetrofitResponse,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Constants constants;
    PreferenceFile preferenceFile;
    public static int notificationCount;
    String language = "";
    private RecyclerView recyclerView;
    LinearLayout linearNodata;
    MainServiceAdapter adapter;
    private String cityId = "";
    String click = "", feedbackIntent = "";
    int Layposition;
    ArrayList<com.antypro.modal.MainService> list = new ArrayList<>();
    GoogleApiClient googleApiClient = null;
    LatLngResponse latLngResponse;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private BgService mSensorService;
    public static String curLat = "", curLng = "";
    String result;
    LocationManager locationManager;

    public static String TAG = "MainService";
    Context ctx;

    public Context getCtx()
    {
        return ctx;
    }

    LocationRequest locationRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainservice);
        rlMain.setBackgroundColor(getResources().getColor(R.color.white));
        tvtitle.setVisibility(View.GONE);
        ivCrown.setVisibility(View.VISIBLE);
        setTitle("");
        preferenceFile = new PreferenceFile();
        initializeView();
        checkPermission();
        locationRequestt();
        callMainService();
        startService(new Intent(MainService.this, BgService.class));
//        mSensorService = new BgService(getCtx());
//        mServiceIntent = new Intent(this, mSensorService.getClass());
//        if (!isMyServiceRunning(mSensorService.getClass())) {
//            startService(mServiceIntent);
//        }
    }

    private void getCurrentLocation() {
        Log.e(TAG, "getCurrentLocation: ");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void initializeView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearNodata = (LinearLayout) findViewById(R.id.linearNodata);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainService.this));
        constants = new Constants(MainService.this);
        preferenceFile.saveData(MainService.this, ServiceApi.REQUEST_KEY_PENDING, "1");
        preferenceFile.saveData(MainService.this, ServiceApi.REQUEST_KEY_COMPLETE, "0");
        preferenceFile.saveData(MainService.this, ServiceApi.REQUEST_KEY_CANCEL, "0");


        if (preferenceFile.getPrefrenceData(this, ServiceApi.LANGUAGE_KEY).equalsIgnoreCase("en")) {
            language = "1";
            preferenceFile.saveData(MainService.this, ServiceApi.REQUEST_KEY, "1");
            linear_notifications.setVisibility(View.VISIBLE);
            tvCount.setVisibility(View.GONE);
            linear_notifications.setOnClickListener(this);
        } else if (preferenceFile.getPrefrenceData(this, ServiceApi.LANGUAGE_KEY).equalsIgnoreCase("ar")) {
            language = "2";
            linear_notifications.setVisibility(View.VISIBLE);
            tvCount.setVisibility(View.GONE);
            linear_notifications.setOnClickListener(this);
        }

        if (getIntent().hasExtra("intent_from")) {
            if (getIntent().getStringExtra("intent_from").equalsIgnoreCase("feedback")) {
                feedbackIntent = "comeFromFeedback";
            }
        }

    }

    void callAboutStatus() {
        if (constants.isNetworkAvailable())
        {
            new RetrofitService(MainService.this, MainService.this, ServiceApi.GET_ABOUT_STATUS, ServiceApi.REQ_GET_ABOUT_STATUS, 1).callService(false);
        } else
        {
            Toast.makeText(this, getResources().getString(R.string.check_network_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void setMenuItems()
    {
        if (preferenceFile.getPrefrenceData(MainService.this, ServiceApi.USERTYPE_KEY).equalsIgnoreCase("1"))//for sp
        {
            home.setChecked(true);
            profile.setChecked(false);
            workingTime.setChecked(false);
            feedback.setChecked(false);
            discountOffers.setChecked(false);
            earnings.setChecked(false);
            contact.setChecked(false);
            share.setChecked(false);
            logout.setChecked(false);
        }
        else if (preferenceFile.getPrefrenceData(MainService.this, ServiceApi.USERTYPE_KEY).equalsIgnoreCase("2"))//for customer
        {
            home.setChecked(true);
            profile.setChecked(false);
            bookingRequests.setChecked(false);
            favourites.setChecked(false);
            contact.setChecked(false);
            share.setChecked(false);
            logout.setChecked(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent=new Intent(this,BgService.class);
        this.startService(intent);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //            startService(new Intent(this, BgService.class));
            startForegroundService(new Intent(this, BgService.class));
        } else {
            startService(new Intent(this, BgService.class));
        }



  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, MyService.class));
        } else {
            startService(new Intent(this, MyService.class));
        }

        if (preferenceFile.getPrefrenceData(MainService.this, ServiceApi.USERTYPE_KEY).equalsIgnoreCase("1"))//for sp
        {
            callNotificationCountService();//for sp booking request count show only in sp side

        } else if (preferenceFile.getPrefrenceData(MainService.this, ServiceApi.USERTYPE_KEY).equalsIgnoreCase("2"))//for sp
        {
            if (getIntent().hasExtra("cityId")) {
                cityId = getIntent().getExtras().getString("cityId");
            } else {
                cityId = preferenceFile.getPrefrenceData(MainService.this, ServiceApi.CITY_ID_KEY);
            }
        }

        getCurrentLocation();
getLocation();

        callService();//for save language service
        callAboutStatus();//for admin aboutus active/inactive
        setMenuItems();//for set enable of menu item
        callMainService();//to call the main services
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        callNotificationCountService();
    }

    private void callService() {
        if (constants.isNetworkAvailable()) {
            JSONObject postParam = new JSONObject();

            try {
                postParam.put("user_id", preferenceFile.getPrefrenceData(this, ServiceApi.ID_KEY));
                postParam.put("language", language);

                new RetrofitService(MainService.this, MainService.this, ServiceApi.LANGUAGE_SAVED, postParam,
                        ServiceApi.REQ_LANGUAGE_SAVED, 2).
                        callService(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.check_network_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void callMainService() {
        if (constants.isNetworkAvailable()) {
            // new RetrofitService(MainService.this, MainService.this, "http://maps.googleapis.com/maps/api/geocode/json?latlng=30.954886666666667 ,75.84880833333334 &sensor=true", ServiceApi.REQ_MAINSERVICE, 1).callService(true);
            new RetrofitService(MainService.this, MainService.this,
                    ServiceApi.MAINSERVICE + preferenceFile.getPrefrenceData(this,
                            ServiceApi.ID_KEY) + ".json", ServiceApi.REQ_MAINSERVICE, 1).
                    callService(true);
        } else {
            Toast.makeText(this, getResources().getString(R.string.check_network_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermission()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            int hasInternetPermission = checkSelfPermission(android.Manifest.permission.INTERNET);
            int hasNetworkPermission = checkSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE);
            int hasFinePermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarsePermission = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasReadPermission = checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int hasWritePermission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasReadPhonePermission = checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE);

            ArrayList<String> permissionList = new ArrayList<String>();

            if (hasInternetPermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.INTERNET);
            }

            if (hasReadPhonePermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
            }

            if (hasNetworkPermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.ACCESS_NETWORK_STATE);
            }

            if (hasFinePermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (hasCoarsePermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!permissionList.isEmpty()) {
                requestPermissions(permissionList.toArray(new String[permissionList.size()]), 2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {

            case 2:

                for (int i = 0; i < permissions.length; i++) {

                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {


                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        checkPermission();
                    }
                }

                break;

            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            }
        }
    }

    private void callNotificationCountService()
    {
        if (constants.isNetworkAvailable()) {
            JSONObject postParam = new JSONObject();
            try {
                postParam.put("service_provider_id", preferenceFile.getPrefrenceData(this, ServiceApi.ID_KEY));
                new RetrofitService(MainService.this, MainService.this,
                        ServiceApi.NOTIFICATION_COUNT, postParam, ServiceApi.REQ_NOTIFICATION_COUNT, 2).callService(true);
                Log.e("CountParams ",postParam.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.check_network_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setAdapter() {
        if (click.equalsIgnoreCase("")) {
            adapter = new MainServiceAdapter(MainService.this, list);
            recyclerView.setAdapter(adapter);
        } else if (click.equalsIgnoreCase("1")) {
            adapter = new MainServiceAdapter(MainService.this, list);
            recyclerView.getLayoutManager().scrollToPosition(Layposition);
            recyclerView.setAdapter(adapter);
        }

        adapter.onItemSelected(new MainServiceAdapter.MyClickListener() {
            @Override
            public void MyClickListener(int layoutPosition, View view) {

                click = "1";
                Layposition = layoutPosition;

                Log.e(TAG, "MyClickListener:lat " + curLat);
                Log.e(TAG, "MyClickListener:lng " + curLng);

                if (preferenceFile.getPrefrenceData(MainService.this, ServiceApi.USERTYPE_KEY).
                        equalsIgnoreCase("1"))//for sp
                {
                    Intent intent1 = new Intent(MainService.this, SubServices.class);
                    intent1.putExtra("service_id", list.get(layoutPosition).getId());
                    startActivity(intent1);
                } else if (preferenceFile.getPrefrenceData(MainService.this, ServiceApi.USERTYPE_KEY).equalsIgnoreCase("2"))//for customer
                {

                    Log.e(TAG, "LnagugaeValueMain "+preferenceFile.getPrefrenceData(MainService.this, ServiceApi.LANGUAGE_KEY));

                    Intent intent1 = new Intent(MainService.this, ServiceProviderList.class);
                    intent1.putExtra("service_id", list.get(layoutPosition).getId());
                    intent1.putExtra("cityId", cityId);
                    intent1.putExtra("lat", curLat);
                    intent1.putExtra("lng", curLng);
                    startActivity(intent1);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_notifications:

                if (preferenceFile.getPrefrenceData(MainService.this, ServiceApi.USERTYPE_KEY).
                        equalsIgnoreCase("1")) {
                    //to remove the badges from app icon
                    FirebaseMessages.count = 0;
  try {
                        Badges.removeBadge(MainService.this);
                        Badges.setBadge(MainService.this, FirebaseMessages.count);
                    } catch (BadgesNotSupportedException badgesNotSupportedException) {
                    }

                    preferenceFile.saveData(MainService.this, ServiceApi.REQUEST_KEY, "1");
                    //   Intent intent4 = new Intent(MainService.this, NewBookingRequest.class);
                    Intent intent4 = new Intent(MainService.this, SpBookingRequests.class);
                    startActivity(intent4);
                    break;
                } else if (preferenceFile.getPrefrenceData(MainService.this, ServiceApi.USERTYPE_KEY).
                        equalsIgnoreCase("2")) {
                    Intent intent5 = new Intent(MainService.this, FeedbackRequestList.class);
                    startActivity(intent5);
                    break;
                }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Configuration config = newBase.getResources().getConfiguration();
        SharedPreferences sharedPreferences = App.getGlobalPrefs();
        String language = sharedPreferences.getString(ServiceApi.LANGUAGE_KEY, "");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(language));
        }

        Resources resources = newBase.getResources();
        config.locale = locale;
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            newBase = newBase.createConfigurationContext(config);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            newBase = newBase.createConfigurationContext(config);

        } else {
            Resources resources = newBase.getResources();
            config.locale = locale;
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

        super.attachBaseContext(newBase);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResponse(int RequestCode, String response) {
        super.onResponse(RequestCode, response);
        switch (RequestCode) {
            case ServiceApi.REQ_LANGUAGE_SAVED:

                try {
                    JSONObject result = new JSONObject(response);

                    JSONObject res = result.getJSONObject("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case ServiceApi.REQ_GET_ABOUT_STATUS:

                try {
                    JSONObject result = new JSONObject(response);

                    JSONObject res = result.getJSONObject("result");

                    String status = res.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        JSONObject aboutdata = res.getJSONObject("aboutdata");

                        if (aboutdata.getString("status").equalsIgnoreCase("0")) //active from backend
                        {
                            preferenceFile.saveData(this, ServiceApi.ABOUT_KEY, "0");

                        } else if (aboutdata.getString("status").equalsIgnoreCase("1")) //active from backend
                        {
                            preferenceFile.saveData(this, ServiceApi.ABOUT_KEY, "1");
                        }
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;

            case ServiceApi.REQ_NOTIFICATION_COUNT:
                try {
                    JSONObject result = new JSONObject(response);

                    JSONObject res = result.getJSONObject("result");

                    String status = res.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        tvCount.setVisibility(View.VISIBLE);
                        tvCount.setText(res.getString("booking_count"));
                    } else {
                        tvCount.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case ServiceApi.REQ_SAVE_SP_LACATION:

                try {
                    JSONObject result = new JSONObject(response);

                    JSONObject res = result.getJSONObject("result");

                    String status = res.getString("status");

                    if (status.equalsIgnoreCase("1")) {

                        JSONObject data = res.getJSONObject("data");
                        Log.e(TAG, "onResponse: Lat" + data.getString("lat"));
                        Log.e(TAG, "onResponse: Lng" + data.getString("lng"));

                    } else {


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case ServiceApi.REQ_MAINSERVICE:
                try {
                    JSONObject result = new JSONObject(response);

                    JSONObject res = result.getJSONObject("result");

                    String status = res.getString("status");
                    String feedback_status = res.getString("feedback_status");

                    String message = "";

                    if (preferenceFile.getPrefrenceData(this, ServiceApi.LANGUAGE_KEY).equalsIgnoreCase("en")) {
                        message = res.getString("message");
                    } else if (preferenceFile.getPrefrenceData(this, ServiceApi.LANGUAGE_KEY).equalsIgnoreCase("ar")) {
                        message = res.getString("message_arabic");
                    }

                    if (status.equalsIgnoreCase("1")) {
                        list.clear();
                        linearNodata.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        JSONArray main_service = res.getJSONArray("main_service");

                        for (int i = 0; i < main_service.length(); i++) {

                            com.antypro.modal.MainService mainService = new com.antypro.modal.MainService();

                            JSONObject object = main_service.getJSONObject(i);

                            mainService.setId(object.getString("id"));
                            mainService.setFeedback_status(feedback_status);

                            if (preferenceFile.getPrefrenceData(this, ServiceApi.LANGUAGE_KEY).equalsIgnoreCase("en")) {
                                mainService.setName(object.getString("name"));
                            } else if (preferenceFile.getPrefrenceData(this, ServiceApi.LANGUAGE_KEY).equalsIgnoreCase("ar")) {
                                mainService.setName(object.getString("name_arabic"));
                            }

                            mainService.setImage(object.getString("image"));

                            list.add(mainService);
                        }

                        JSONObject feedbackObject = res.getJSONObject("feedbackdata");
                        String booking_id = feedbackObject.getString("booking_id");
                        String service_provider_id = feedbackObject.getString("service_provider_id");
                        String main_service_id = feedbackObject.getString("main_service_id");
                        String customer_id = feedbackObject.getString("customer_id");

                        // TODO: 18/4/18 by priya
                        if (preferenceFile.getPrefrenceData(MainService.this, ServiceApi.USERTYPE_KEY).equalsIgnoreCase("2"))//for customer
                        {
                            if (feedback_status.equalsIgnoreCase("1")) {

                                Intent intent = new Intent(MainService.this, SubmitFeedback.class);
                                intent.putExtra("booking_id_", feedbackObject.getString("booking_id"));
                                intent.putExtra("service_provider_id_", feedbackObject.getString("service_provider_id"));
                                intent.putExtra("main_service_id_", feedbackObject.getString("main_service_id"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);



   startActivity(new Intent(MainService.this, SubmitFeedback.class)
                                        .putExtra("booking_id_",
                                                feedbackObject.getString("booking_id")).
                                                putExtra("service_provider_id_",
                                                        feedbackObject.getString("service_provider_id"))
                                        .putExtra("main_service_id_",
                                                feedbackObject.getString("main_service_id")));

                            }
                        }

                        if (list.size() > 0) {
                            setAdapter();
                        }

                    } else {
                        linearNodata.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (feedbackIntent.equalsIgnoreCase("comeFromFeedback")) {
            return;
        } else {
            finish();
        }
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

   if (location1 == null) {
            latLngResponse.currentLatLng(0.0, 0.0);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1.0f, this);
        } else {
            latLngResponse.currentLatLng(location1.getLatitude(), location1.getLongitude());
        }



        try{
            curLng = String.valueOf(location.getLongitude());
            curLat = String.valueOf(location.getLatitude());

            Log.e("onConnectedValueLat", curLat);
            Log.e("onConnectedValueLng", curLng);
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }


        PreferenceFile preferenceFile=new PreferenceFile();

        if (preferenceFile.getPrefrenceData(MainService.this, ServiceApi.ID_KEY) != null &&
                preferenceFile.getPrefrenceData(MainService.this, ServiceApi.ID_KEY).length() > 0)
        {
            if (preferenceFile.getPrefrenceData(MainService.this, ServiceApi.USERTYPE_KEY).equalsIgnoreCase("1"))//for sp
            {
                Log.e(TAG, "onConnected: InsideProviderCondition" );
                updateLoc();
            }
        }

  if (location == null) {

            latLngResponse.currentLatLng(0.0, 0.0);

//            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        } else {

            //If everything went fine lets get latitude and longitude
            latLngResponse.currentLatLng(location.getLatitude(), location.getLongitude());
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

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
                    .addApi(LocationServices.API).addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) MainService.this)
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
                                    status.startResolutionForResult(MainService.this, 1000);
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

    private void getLocation() {
        GpsTracker gpsTracker = new GpsTracker(this);

        if (gpsTracker.canGetLocation) {
            Geocoder geocoder = new Geocoder(MainService.this, Locale.getDefault());
            String result = null;
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(),
                        1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addressList != null && addressList.size() > 0) {

                Address address = addressList.get(0);

                String postalCode = addressList.get(0).getPostalCode();//// TODO: 12/8/17  for zipcode
                Log.e("PostalCard<<", postalCode + "");

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(" ,");
                }
                sb.append(address.getCountryName());
                result = sb.toString();
                Log.e("result ", result + " current loc >> " + address.getLatitude() + " _ " + address.getLongitude());

                curLat = String.valueOf(address.getLatitude());
                curLng = String.valueOf(address.getLongitude());


                Log.e("CurrentLatLong<<<<<", curLat + "Lng<<<<" + curLng);

   //for sp save location
                if (preferenceFile.getPrefrenceData(MainService.this, ServiceApi.USERTYPE_KEY).equalsIgnoreCase("1"))//for sp
                {
                    updateLoc();
                }


PreferenceFile.getInstance().saveData(DashBoard.this, ServiceApi.KEY_PICKUP_LOC,
                        tvPickup.getText().toString().trim());

            }

        } else {
            Log.e("canGetLocation", " false ");
        }
    }

    private void updateLoc() {
        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                HashMap<String, String> map = new HashMap<>();

                map.put("user_id", preferenceFile.getPrefrenceData(MainService.this, ServiceApi.USER_ID));
                map.put("lat", curLat);
                map.put("lng", curLng);

                Log.e(TAG, "run: UpdateLocation" + map.toString());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                    new UpdateLoc(map).execute(ServiceApi.SAVE_SP_LACATION);
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 10000);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "onLocationChanged: Lat" + location.getLatitude());
        Log.e(TAG, "onLocationChanged: lng" + location.getLongitude());

        curLng = String.valueOf(location.getLongitude());
        curLat = String.valueOf(location.getLatitude());

        Log.e("lattitude...", curLat);
        Log.e("longitude...", curLng);

  PreferenceFile preferenceFile=new PreferenceFile();

        updateLoc();


  if (preferenceFile.getPrefrenceData(MainService.this, ServiceApi.USERTYPE_KEY).equalsIgnoreCase("1"))//for sp
        {
            updateLoc();
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class UpdateLoc extends AsyncTask<String, Void, String> {

        HashMap<String, String> params;

        public UpdateLoc(HashMap<String, String> params) {
            this.params = params;
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url1 = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
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
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
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

        private String getPostData(HashMap<String, String> postparams) {

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

                return builder.toString();
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                super.onPostExecute(s);
            }
        }
    }

    public interface LatLngResponse {
        void currentLatLng(double lat, double lng);
    }

}
*/
