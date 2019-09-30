package com.taxi.nanny.appController;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import com.taxi.nanny.utils.realTimeTrack.GPSTracker;
import java.util.Timer;

public class App extends Application
{
    GPSTracker gpsTracker;
    Intent intent, intent2;
    public static Handler handler2;
    public static Timer timer2;

    @Override
    public void onCreate()
    {
        super.onCreate();
        handler2 = new Handler();
        timer2 = new Timer();
        gpsTracker = new GPSTracker(getApplicationContext());

        /*if (ConnectionDetector.isInternetAvailable(getApplicationContext())) {
            if (gpsTracker.canGetLocation()) {
                if (SharedPreference.retriveData(getApplicationContext(), Constants.USER_ID) != null) {
                    Log.e("App", "onCreate: ffff");
                    intent2 = new Intent(getApplicationContext(), TestOreoService.class);
                    startService(intent2);

//                    intent = new Intent(getApplicationContext(), CheckConnectivity.class);
//                    startService(intent);
                } else {
                    callAsynchronousTask1();
                    Log.e("App", "No Value");
                }

            } else {
                Log.e("GPS", "GPS Not ENABLED");
            }
        } else {
            Log.e("Internet", "Internet connection Not On");
        }*/

    }
/*
    public void callAsynchronousTask1() {
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler2.post(new Runnable() {
                    public void run() {
                        try {
                            if (SharedPreference.retriveData(getApplicationContext(), Constants.USER_ID) == null)
                            {
                                Log.e("App", "run: "+"IdNull" );
                            }
                            else {
                                intent2 = new Intent(getApplicationContext(), TestOreoService.class);
                                startService(intent2);
                                timer2.purge();
                                timer2.cancel();
                            }
                        } catch (Exception e) {

                        }
                    }
                });
            }
        };
        timer2.schedule(doAsynchronousTask, 0, 2000);
    }*/

    @Override
    public void onTerminate() {
        super.onTerminate();
        stopService(intent2);
        Log.e("App", "onTerminate:@# ");
    }

}
