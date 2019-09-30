/*
package com.taxi.nanny.utils.map;


import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.antypro.CommonToolbar;
import com.antypro.R;
import com.antypro.utils.App;
import com.antypro.utils.Constants;
import com.antypro.utils.ServiceApi;
import com.antypro.utils.retrofit.RetrofitResponse;
import com.antypro.utils.retrofit.RetrofitService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

public class MapsActivity extends CommonToolbar implements OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback, GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, RetrofitResponse {

    private GoogleMap Map;
    ImageView back;
    StringBuffer markeraddress;
    FrameLayout map;
    Context context;
    String description;
    private SupportMapFragment mapFragment;
    // MapFragment myMAPF;
    private LatLng latLng, lattouch;
    private GoogleApiClient mGoogleApiClient;
    boolean canGetLocation = false;
    AutoCompleteTextView txLocation;
    double latitude; // latitude
    double longitude; // longitude
    public Marker marker, marker1;
    Geocoder geocoder;
    String result;
    List<Address> addresses;
    String address, country, city, state,subLocality;
    GpsTracker gpsTracker;
    Boolean markerClicked;
    String routeId = "";
    double latitude_cordinate, longitude_cordinate;
    TextView tvAddress;
    private Button btnSend;
    private final int LOCATION_CODE = 66;
    File finalFile;
    Bitmap bitmap;
    TextView tvShare;
    double startLat, startLong, endLat, endLong = 0.0;
    GoogleApiClient googleApiClient = null;
    String intentCity="",intentCountry="";
    double intentlat=0.0,intentlng=0.0;
    Constants constants;
    String value="0";
    Timer timer;
    String formattedAddress="";
    String MY_API_KEY="AIzaSyCYE9GPApnjFFOa8kx5XhjxcJJlk6qUjLs"; //copy from google developer console api of Anty project

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        tvtitle.setText("Share Location");
        tvtitle.setTextColor(getResources().getColor(R.color.colorPrimary));
        txLocation = (AutoCompleteTextView) findViewById(R.id.txLocation);
        tvShare = (TextView) findViewById(R.id.tvShare);
        tvShare.setOnClickListener(this);
        constants = new Constants(MapsActivity.this);

        // back= (ImageView) findViewById(R.id.back);
        //    tv_doneback=(TextView)findViewById(R.id.tv_doneback);
        context = this;
        checkPermission();
        locationRequestt();

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            FragmentManager myFM = getSupportFragmentManager();
            mapFragment = (SupportMapFragment) myFM
                    .findFragmentById(R.id.map);
            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();
                myFM.beginTransaction().replace(R.id.map, mapFragment).commit();
            }
            mapFragment.getMapAsync(this);
        }

        markeraddress = new StringBuffer();
        txLocation.setAdapter(new PlaceAutoCompleteAdapter(this, R.layout.custom_spinner));
        txLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                description = (String) parent.getItemAtPosition(position);
                Map.clear();
                txLocation.setText(description);
                value="1";
                callGetLocationService();


    */
/*            try {
                    Geocoder geocoder;
                    geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

                    addresses = (ArrayList) geocoder.getFromLocationName(description, 1);


                    if (addresses != null && addresses.size() > 0) {
                        //todo for hide keyboard
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (getCurrentFocus() != null) {
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                        Map.clear();
                        Address returnedAddress = addresses.get(0);
                        txLocation.setText(description);

                        value="1";

                        LatLng latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                        latitude_cordinate = addresses.get(0).getLatitude();
                        longitude_cordinate = addresses.get(0).getLongitude();


                        marker = Map.addMarker(new MarkerOptions().position(latLng).draggable(true));
                        markerClicked = true;
                        Map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()), 14.0f));

                        address = addresses.get(0).getAddressLine(0);
                        country = addresses.get(0).getCountryName();
                        state = addresses.get(0).getAdminArea();
                        city = addresses.get(0).getLocality();
                        subLocality = addresses.get(0).getSubLocality();
                        getcoordinate();
                    }
                } catch (Exception e) {

                }*//*

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(LOCATION_CODE);
        finish();
    }


    */
/*
     * ======Get-Coordinate=======
     * *//*


    private void getcoordinate() {
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Configuration config = newBase.getResources().getConfiguration();
        SharedPreferences sharedPreferences = App.getGlobalPrefs();

        String language =sharedPreferences.getString(ServiceApi.LANGUAGE_KEY,"");

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        config.setLocale(new Locale(language));

        Resources resources = newBase.getResources();
        config.locale = locale;
        resources.updateConfiguration(config, resources.getDisplayMetrics());

       */
/* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            newBase = newBase.createConfigurationContext(config);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            newBase = newBase.createConfigurationContext(config);

        } else {
            Resources resources = newBase.getResources();
            config.locale = locale;
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }*//*

        super.attachBaseContext(newBase);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (Map == null) {
            Map = googleMap;
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
            Map.setOnMapLoadedCallback(this);
        }
    }

    private void showMarkers() {

        try {
         //   Toast.makeText(this, "currentLat"+latitude +" logi<<"+longitude, Toast.LENGTH_SHORT).show();

            if (latitude != 0.0 && longitude != 0.0) {


                marker = Map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).
                        title(" It's you").draggable(true));

                callGetLatLngService();

            */
/*    try {

                    geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();
                        country = addresses.get(0).getCountryName();
                        subLocality = addresses.get(0).getSubLocality();

                        try {

                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }

                        markeraddress.delete(0, markeraddress.length());
                        startLat = addresses.get(0).getLatitude();
                        startLong = addresses.get(0).getLongitude();



                        if (country != null) {
                            markeraddress = markeraddress.append(country);
                            if (state != null) {
                                markeraddress = markeraddress.append(",");
                                markeraddress = markeraddress.append(state);
                                if (city != null) {
                                    markeraddress = markeraddress.append(",");
                                    markeraddress = markeraddress.append(city);
                                    if (subLocality!=null)
                                    {
                                        markeraddress = markeraddress.append(",");
                                        markeraddress = markeraddress.append(subLocality);
                                        // description=markeraddress.toString();
                                    }
                                }
                            }

                            txLocation.setText("");
                            txLocation.setText(markeraddress);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }*//*


                Map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                    @Override
                    public void onMarkerDragStart(Marker marker1) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker1) {
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker1) {
                        if (marker1 != null) {
                            marker1.remove();
                        }

                        marker1 = Map.addMarker(new MarkerOptions().position(marker1.getPosition()).draggable(true));
                        latitude = marker1.getPosition().latitude;
                        longitude = marker1.getPosition().longitude;

                        callGetLatLngService();
                    }
                });

                Map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14.0f));
                Map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng click) {

                        value="1";
                        if (marker != null) {
                            marker.remove();
                            Map.clear();
                        }

                        latitude_cordinate = click.latitude;
                        longitude_cordinate = click.longitude;

                        lattouch = new LatLng(latitude_cordinate, longitude_cordinate);

                        marker = Map.addMarker(new MarkerOptions().position(lattouch).draggable(true));
                        callGetLatLngService();
         */
/*               try {
                            addresses = geocoder.getFromLocation(latitude_cordinate, longitude_cordinate, 1);
                            if (addresses.size() > 0) {

                                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                city = addresses.get(0).getLocality();
                                state = addresses.get(0).getAdminArea();
                                country = addresses.get(0).getCountryName();
                                subLocality=addresses.get(0).getSubLocality();

                                try {
                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }

                                markeraddress.delete(0, markeraddress.length());
                                if (country != null) {
                                    markeraddress = markeraddress.append(country);
                                    if (state != null) {
                                        markeraddress = markeraddress.append(",");
                                        markeraddress = markeraddress.append(state);
                                        if (city != null) {
                                            markeraddress = markeraddress.append(",");
                                            markeraddress = markeraddress.append(city);
                                            if (subLocality!=null)
                                            {
                                                markeraddress = markeraddress.append(",");
                                                markeraddress = markeraddress.append(subLocality);
                                                // description=markeraddress.toString();
                                            }
                                        }
                                    }

                                    txLocation.setText("");
                                    txLocation.setText(markeraddress);
                                }

                                getcoordinate();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }*//*

                    }
                });


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapLoaded() {
        getLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        getLocation();
    }

    public void getLocation() {
        gpsTracker = new GpsTracker(this);

        if (gpsTracker.getLatitude() != 0.0) {

            longitude = gpsTracker.getLongitude();
            latitude = gpsTracker.getLatitude();
            showMarkers();
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tvShare:

               shareLocation();
                break;

        }
    }

    public void shareLocation() {

        Intent intent = new Intent();

        if (value.equalsIgnoreCase("1"))
        {
            intent.putExtra("latitude", latitude_cordinate);
            intent.putExtra("longitude", longitude_cordinate);
        }

        else {
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
        }

        intent.putExtra("desription", txLocation.getText().toString());
        setResult(LOCATION_CODE, intent);
        finish();

   */
/*     if (txLocation.getText().toString().trim().equalsIgnoreCase(""))
        {
            intent.putExtra("desription", intentCity);
            intent.putExtra("latitude", startLat);
            intent.putExtra("longitude", startLong);
            setResult(LOCATION_CODE, intent);
            finish();
        }

        else {
            if (value.equalsIgnoreCase("1"))
            {
                intent.putExtra("latitude", latitude_cordinate);
                intent.putExtra("longitude", longitude_cordinate);
            }

            else {
                intent.putExtra("latitude", startLat);
                intent.putExtra("longitude", startLong);
            }

            intent.putExtra("desription", txLocation.getText().toString());
            setResult(LOCATION_CODE, intent);
            finish();
        }*//*


*/
/*        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // TODO Auto-generated method stub
                bitmap = snapshot;
                Uri tempUri = getImageUri(MapsActivity.this, bitmap);
                finalFile = new File(getRealPathFromURI(MapsActivity.this, tempUri));


                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Intent intent = new Intent();
                String newLat, newLong;
                newLat = String.valueOf(latitude_cordinate);
                newLong = String.valueOf(longitude_cordinate);
                intent.putExtra("Locationbitmap", byteArray);
                intent.putExtra("MAPFILE", finalFile);

                if (txLocation.getText().toString().trim().equalsIgnoreCase(""))
                {
                  //  Toast.makeText(MapsActivity.this, "Please enter location", Toast.LENGTH_SHORT).show();
                    intent.putExtra("desription", intentCity);
                    intent.putExtra("latitude", startLat);
                    intent.putExtra("longitude", startLong);
                    setResult(LOCATION_CODE, intent);
                    finish();
                }
                else {
                    if (value.equalsIgnoreCase("1"))
                    {
                        intent.putExtra("latitude", latitude_cordinate);
                        intent.putExtra("longitude", longitude_cordinate);
                    }

                   else {
                        intent.putExtra("latitude", startLat);
                        intent.putExtra("longitude", startLong);
                    }

                     *//*
*/
/* if (startLat != 0.0 && latitude_cordinate != 0.0) {
                        intent.putExtra("latitude", startLat);
                        intent.putExtra("longitude", startLong);
                    } else {
                        intent.putExtra("latitude", latitude_cordinate);
                        intent.putExtra("longitude", longitude_cordinate);
                    }*//*
*/
/*

                    intent.putExtra("desription", txLocation.getText().toString());
                    setResult(LOCATION_CODE, intent);
                    finish();
                }
            }
        };*//*

      //  Map.snapshot(callback);

    }

    public String getRealPathFromURI(Context inContext, Uri uri) {
        Cursor cursor = inContext.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= 23) {

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

    private void locationRequestt() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) MapsActivity.this)
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

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

                    final Status status = locationSettingsResult.getStatus();
                    final LocationSettingsStates state = locationSettingsResult.getLocationSettingsStates();

                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            getLocation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MapsActivity.this, 1000);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case 2:

                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        getLocation();
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


    private void callGetLatLngService() {
        if (constants.isNetworkAvailable()) {
//            new RetrofitService(MapsActivity.this, MapsActivity.this, "http://maps.googleapis.com/maps/api/geocode/json?latlng=28.6139,77.2090 & sensor=true", 111, 1).callService(true);
           if (value.equalsIgnoreCase("1"))
           {
               new RetrofitService(MapsActivity.this, MapsActivity.this,
                       "https://maps.googleapis.com/maps/api/geocode/json?latlng="+
                               latitude_cordinate +","+longitude_cordinate +"& sensor=true" + "&key=" + MY_API_KEY,
                       111, 1).callService(true);
           }
           else {
               new RetrofitService(MapsActivity.this, MapsActivity.this,
                       "https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude +","+longitude +
                               "& sensor=true"  + "&key=" + MY_API_KEY, 111, 1).
                       callService(true);
           }

        } else {
            Toast.makeText(this, getResources().getString(R.string.check_network_connection), Toast.LENGTH_SHORT).show();
        }
    }

    */
/*"https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters + "&key=" + MY_API_KEY *//*


    private void callGetLocationService() {
        if (constants.isNetworkAvailable()) {
            new RetrofitService(MapsActivity.this, MapsActivity.this,
                    "https://maps.google.com/maps/api/geocode/json?address="+description +"&sensor=false"
                            + "&key=" + MY_API_KEY,
                    115, 1).callService(true);
        } else {
            Toast.makeText(this, getResources().getString(R.string.check_network_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResponse(int RequestCode, String response) {

        switch (RequestCode)
        {
            case 111:


                try {
                    JSONObject result = new JSONObject(response);

                    JSONArray resultArray=result.getJSONArray("results");

                    if (resultArray.length()>0)
                    {
                        for (int i = 0; i < resultArray.length(); i++)
                        {
                            JSONObject obj=resultArray.getJSONObject(0);
                            formattedAddress="";
                            formattedAddress=obj.getString("formatted_address");
                            txLocation.setText(formattedAddress);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

                case 115:


                    try {
                        JSONObject result = new JSONObject(response);

                        JSONArray resultArray=result.getJSONArray("results");

                        if (resultArray.length()>0)
                        {
                            for (int i = 0; i < resultArray.length(); i++)
                            {
                                JSONObject obj=resultArray.getJSONObject(i);
                                JSONObject geometry=obj.getJSONObject("geometry");
                                JSONObject location=geometry.getJSONObject("location");

                                double lat=Double.parseDouble(location.getString("lat"));
                                double lng=Double.parseDouble(location.getString("lng"));

                                LatLng latLng = new LatLng(lat, lng);
                                latitude_cordinate = lat;
                                longitude_cordinate =lng;

                                marker = Map.addMarker(new MarkerOptions().position(latLng).draggable(true));
                                markerClicked = true;
                                Map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14.0f));

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                break;
        }

    }
}





*/
