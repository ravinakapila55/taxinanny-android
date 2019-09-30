package com.taxi.nanny.utils.location;

import android.util.Log;

import com.taxi.nanny.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;


public class PlaceAPI
{
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = Constant.PLACES_API;
    public static HashMap<String,String> place_id;



//    https://maps.googleapis.com/maps/api/place/autocomplete/json?
//    // key=AIzaSyCDl6ZDBRDkG1KMCZkAFavadSBthuruHvE&input=lu&components=country:in
//

    public ArrayList<String> autocomplete (String input)
    {
        ArrayList<String> resultList = null;
        HttpURLConnection conn = null;
        place_id=new HashMap<>();
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            sb.append("&components=country:in");
//            sb.append("?key=" + API_KEY);
//            sb.append("&components=country:sa");

            Log.e("toString",""+sb.toString());

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return resultList;
        } catch (IOException e) {
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try
        {
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            Log.e("predsJsonArray",""+predsJsonArray.length());
            resultList = new ArrayList<String>(predsJsonArray.length());

            if (place_id!=null)
            {
                place_id.clear();
            }
            place_id=new HashMap<>();
            for (int i = 0; i < predsJsonArray.length(); i++)
            {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                place_id.put(predsJsonArray.getJSONObject(i).getString("description"),predsJsonArray.getJSONObject(i).getString("place_id"));
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return resultList;
    }
}

