package com.taxi.nanny;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;

import org.json.JSONException;
import org.json.JSONObject;

public class CallApiBackgroundService extends Service implements RetrofitResponse
{
    Context context;
    String latt="",lng="",booking_id="",ride_id="";

    public CallApiBackgroundService(Context context, String latt, String lng, String booking_id, String ride_id)
    {
        this.context = context;
        this.latt = latt;
        this.lng = lng;
        this.booking_id = booking_id;
        this.ride_id = ride_id;
        callDriverUpdateLocation(latt,lng,booking_id,ride_id);
    }

    public void callDriverUpdateLocation(String lattitude, String longitude, String booking_id, String ride_id)
    {
        Log.e("callDriverUpdateLocation ","UpdateLat "+lattitude);
        Log.e("callDriverUpdateLocation ","UpdateLng "+longitude);
        Log.e("callDriverUpdateLocation ","booking_id "+booking_id);
        Log.e("callDriverUpdateLocation ","ride_id "+ride_id);

        JSONObject param=new JSONObject();
        try
        {
            param.put("ride_id", ride_id);
            param.put("booking_id",booking_id);
            param.put("lat", String.valueOf(lattitude));
            param.put("long", String.valueOf(longitude));
            Log.e("API_ACTUAL_DRIVER_LOCAIONParams ",param.toString());
            new RetrofitService(this, this,
                    Constant.API_ACTUAL_DRIVER_LOCAION,param, 105, 2,"1").
                    callService(false);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onResponse(int RequestCode, String response)
    {
        Log.e("REsponseserviceeee ",response);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
