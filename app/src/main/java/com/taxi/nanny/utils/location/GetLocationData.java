package com.taxi.nanny.utils.location;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetLocationData
{
    public GetLatLng getLatLng;

    public GetLocationData(GetLatLng getLatLng)
    {
        this.getLatLng=getLatLng;
    }

    /*Get Lat Lng Through PLACE API*/
    public void serviceArrive(String url1)
    {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            URL url = new URL(url1);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            Log.e("url", "" + url);
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("EXCEPTION", "Error processing Places API URL", e);
        } catch (IOException e) {
            Log.e("EXCEPTION", "Error connecting to Places API", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try
        {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONObject res = jsonObj.getJSONObject("result");
            JSONObject geo = res.getJSONObject("geometry");
            JSONObject loc = geo.getJSONObject("location");

            getLatLng.address(loc);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
