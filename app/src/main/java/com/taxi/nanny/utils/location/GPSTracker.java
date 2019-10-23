package com.taxi.nanny.utils.location;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.taxi.nanny.utils.map.GpsTracker;


public class GPSTracker extends Service implements LocationListener
{
    Context mContext;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    public static boolean isFromSetting=false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    // The minimum time between updates in milliseconds
//    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 minute
    protected LocationManager locationManager;
    private static int LOCATION_PERMISSION_REQUEST_CODE = 3;
    public static final String TAG= GpsTracker.class.getSimpleName();

    public GPSTracker(Context context)
    {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation()
    {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled)
            {
                this.canGetLocation = true;
                if (location == null)
                {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        return location;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this, Looper.getMainLooper());
                    Log.e("GPS Enabled", "GPS Enabled");

                    if (locationManager != null)
                    {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null)
                        {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            Log.e(TAG, "getLocation:Lattitude "+latitude );
                            Log.e(TAG, "getLocation:Longitude "+longitude );
                        }
                    }
                }
            }

            if (isNetworkEnabled)
            {
                this.canGetLocation=true;
                if (location == null)
                {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null)
                    {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        updateGPSCoordinates();
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS()
    {
        if(locationManager != null)
        {
            // locationManager.removeUpdates(GpsTracker.this);
        }
    }

    public void updateGPSCoordinates()
    {
        if (location != null)
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Log.e(TAG, "updateGPSCoordinates:Lattitude "+latitude );
            Log.e(TAG, "updateGPSCoordinates:Longitude "+longitude );
        }
    }

    /**
     * Function to get latitude
     * */

    public double getLatitude()
    {
        if(location != null)
        {
            latitude = location.getLatitude();
            Log.e(TAG, "getLatitude:Method "+latitude);
        }
        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude()
    {
        if(location != null)
        {
            longitude = location.getLongitude();
            Log.e(TAG, "getLongitude:Method "+longitude);
        }
        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation()
    {
        return this.canGetLocation;
    }

    public void showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled.Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                isFromSetting = true;
                mContext.startActivity(intent);
              /*  new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        getLocation();
                    }
                }, 1500);*/
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.e(TAG, "onLocationChanged: "+"inside");
        Log.e(TAG, "onLocationChanged:Lat  "+location.getLatitude());
        Log.e(TAG, "onLocationChanged:Lng "+location.getLongitude());

       /* Toast.makeText(mContext,
                "LocaChanged Lat "+location.getLatitude()+" locationChnaged Lng "+location.getLongitude(), Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.e(TAG, "onStatusChanged: "+"inside");
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Log.e(TAG, "onProviderEnabled: "+"inside");
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Log.e(TAG, "onProviderDisabled: "+"inside");
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.e("SERVICE","running");
        return null;
    }
}