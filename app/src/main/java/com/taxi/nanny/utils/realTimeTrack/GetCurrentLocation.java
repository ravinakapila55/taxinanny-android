package com.taxi.nanny.utils.realTimeTrack;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class GetCurrentLocation implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, android.location.LocationListener {
    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Context context;
    private LatLngResponse lngResponse;
    private LocationManager locationManager;

    public GetCurrentLocation(Context context, LatLngResponse lngResponse) {
        Log.e( "GetCurrentLocation: ","insideClass" );
        this.context = context;
        this.lngResponse = lngResponse;
        googleApiLocation(context);
        mGoogleApiClient.connect();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    private void googleApiLocation(Context context)
    {
        if (context != null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            // Create the LocationRequest object
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Log.e("onConnectedGEtCurrentLocationLat ","onConnected ");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        //Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //location.setAccuracy(ACCURACY_FINE);
        // Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (locationGps == null)
        {
            Log.e("locationGps ","null ");
            if (locationNetwork == null)
            {
                Log.e("locationNetwork ","null ");
                lngResponse.currentLatLng(0.0, 0.0);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1.0f, this);
            } else

                {
                    Log.e("locationNetworkElse ","null ");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1.0f, this);
            }
        }
        else
            {
                Log.e("onConnected:GEtCurrentLocationLat ",locationGps.getLatitude() +"");
                Log.e("onConnected:GEtCurrentLocationLng ",locationGps.getLongitude() +"");
            lngResponse.currentLatLng(locationGps.getLatitude(), locationGps.getLongitude());
        }

//
//        if (location == null) {
//
//            lngResponse.currentLatLng(0.0, 0.0);
//
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//        } else {
//
//            //If everything went fine lets get latitude and longitude
//            lngResponse.currentLatLng(location.getLatitude(), location.getLongitude());
//        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        if (connectionResult.hasResolution())
        {
            try
            {
                connectionResult.startResolutionForResult((AppCompatActivity) context, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e)
            {
                e.printStackTrace();
            }
        } else {
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(final Location location)
    {
        Log.e("REalllll ",location+"");
        Log.e("REalllll ",location.getLatitude()+"");
        Log.e("REalllll ",location.getLongitude()+"");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle)
    {
        Log.e( "onStatusChanged: ",s );
    }

    @Override
    public void onProviderEnabled(String s)
    {
        Log.e( "onProviderEnabled: ",s );
    }

    @Override
    public void onProviderDisabled(String s)
    {
        Log.e( "onProviderDisabled: ",s );
    }

    public interface LatLngResponse
    {
        void currentLatLng(double lat, double lng);
    }
}