package com.taxi.nanny.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import java.util.ArrayList;

/**
 * Created by ben on 5/16/2017.
 */

public class AppUtils
{
    public static String[] getCompletePermissionsPrivacy(Context context)
    {
        String[] permission = new String[]
                {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        return permission;
    }

    public static String[] getCameraPermission(Context context)
    {
        String[] permission = new String[]
                {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        return permission;
    }

    public static ArrayList<String> permissionNeeded(String[] permissions, Context context) {
        ArrayList<String> permitted = new ArrayList<String>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permitted.add(permission);
            }
        }
        return permitted;
    }


    public static String[] getLocationPermission(Context context) {
        String[] permission = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,

        };
        return permission;
    }


    public static boolean hasCameraPermission(Context context)
    {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED);
    }

    public static boolean hasLocationPermission(Context context)
    {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public static String[] getStringArray(ArrayList<String> objects)
    {
        String[] strings = new String[objects.size()];
        for(int i = 0; i < objects.size(); i++)
        {
            strings[i] = objects.get(i);
        }
        return strings;
    }


}


