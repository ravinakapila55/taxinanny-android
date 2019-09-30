package com.taxi.nanny.utils.location.permission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

public class Permission
{
    /*Runtime permissions*/
    public static void checkPermission(Context context, PermissionGranted granted)
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            int hasCameraPermission = context.checkSelfPermission(Manifest.permission.CAMERA);
            int hasReadPermission = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int hasWritePermission = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            ArrayList<String> permissionList = new ArrayList<String>();

            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.CAMERA);
            }
            if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!permissionList.isEmpty())
            {
                granted.showPermissionAlert(permissionList, 2);
            }
        }
    }

    /*Runtime permissions*/
    public static void checkContactPermissions(Context context, PermissionGranted granted)
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            int hasReadContact = context.checkSelfPermission(Manifest.permission.READ_CONTACTS);
            int hasWriteContacts = context.checkSelfPermission(Manifest.permission.WRITE_CONTACTS);

            ArrayList<String> permissionList = new ArrayList<String>();

            if (hasReadContact != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_CONTACTS);
            }
            if (hasWriteContacts != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.WRITE_CONTACTS);
            }

            if (!permissionList.isEmpty())
            {
                granted.showPermissionAlert(permissionList, 2);
            }
        }
    }

    public static void checkPermissionLocation(Context context, PermissionGranted granted)
    {
        Log.e("checkPermissionLocation: InsideChekc ","" );
        if (Build.VERSION.SDK_INT >= 23)
        {
            Log.e("checkPermissionLocation: InsideChekcIFFF ","" );
            int hasNetworkPermission = context.checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);
            int hasaccessCoarseLocation = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasAccessFineLocation = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int hasInternetPermission = context.checkSelfPermission(Manifest.permission.INTERNET);

            ArrayList<String> permissionList = new ArrayList<String>();

            if (hasInternetPermission != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(Manifest.permission.INTERNET);
            }
            if (hasNetworkPermission != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(Manifest.permission.ACCESS_NETWORK_STATE);
            }
            if (hasaccessCoarseLocation != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (hasAccessFineLocation != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (!permissionList.isEmpty())
            {
                granted.showPermissionAlert(permissionList, 2);
            }
        }
    }
}
