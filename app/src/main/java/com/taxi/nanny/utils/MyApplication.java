package com.taxi.nanny.utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.taxi.nanny.utils.location.ConnectionDetector;
import com.taxi.nanny.utils.location.GPSTracker;


public class MyApplication extends Application
{

    private static MyApplication instance;
    GPSTracker gpsTracker;

    public static final String TAG=MyApplication.class.getSimpleName();

    public static MyApplication getInstance()
    {
        return instance;
    }

    public static boolean hasNetwork()
    {
        return instance.checkIfHasNetwork();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
        initSharedHelper();
        gpsTracker = new GPSTracker(getApplicationContext());

        if (ConnectionDetector.isInternetAvailable(getApplicationContext()))
        {
            if (gpsTracker.canGetLocation()) {
                Log.e(TAG, "onCreate:MyApplicationLat "+gpsTracker.getLatitude() );
                Log.e(TAG, "onCreate:MyApplicationLng "+gpsTracker.getLongitude() );
              /*  if (SharedPreference.retriveData(getApplicationContext(), Constants.USER_ID) != null) {
                    Log.e("App", "onCreate: ffff");
                    intent2 = new Intent(getApplicationContext(), TestOreoService.class);
                    startService(intent2);

                } else {
                    callAsynchronousTask1();
                    Log.e("App", "No Value");
                }*/

            } else {
                Log.e("GPS", "GPS Not ENABLED");
            }
        } else {
            Log.e("Internet", "Internet connection Not On");
        }



    }


    private void initSharedHelper() {
        SharedPrefUtil.init(this);
    }

    public boolean checkIfHasNetwork()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
