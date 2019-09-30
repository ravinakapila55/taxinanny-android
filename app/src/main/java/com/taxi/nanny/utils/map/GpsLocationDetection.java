package com.taxi.nanny.utils.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class GpsLocationDetection extends BroadcastReceiver {
    public static double lat=0.0, lng=0.0;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        GpsTracker gpsTracker=new GpsTracker(context);
        if(gpsTracker.isGPSEnabled)
        {
            lat=gpsTracker.getLatitude();
            lng= gpsTracker.getLongitude();
            Intent intentt = new Intent("LOCATION");
            intentt.putExtra("LAT",lat);
            intentt.putExtra("LONG",lng);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intentt);
        }
        else
        {
            // Toast.makeText(context,"For your current location, Enable your device GPS!",Toast.LENGTH_LONG).show();
        }
    }
}

