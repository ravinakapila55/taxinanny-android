package com.taxi.nanny;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.directionhelpers.DataParser;
import com.taxi.nanny.utils.map.GpsTracker;
import com.taxi.nanny.views.booking.ConfirmBooking;
import com.taxi.nanny.views.driver.DriverHome;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DemoMap extends AppCompatActivity implements OnMapReadyCallback {


        public static  String TAG= DemoMap.class.getSimpleName();
        private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
        private GoogleApiClient mGoogleApiClient;
        private LocationRequest mLocationRequest;
        private double currentLatitude;
        private double currentLongitude;
        Context context;
        private GpsTracker gpsTracker;
        public Marker marker;
        LocationListener locationListener;
        LocationManager locationManager;
        double lattitude, longitude;
        GoogleMap map;
        String result;
        String token = "";
        SharedPrefUtil sharedPrefUtil;
        String currentLocation="";


    private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543,
            -73.998585);
    private static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);
    private static final LatLng WALL_STREET = new LatLng(40.7064, -74.0094);

    GoogleMap googleMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_map);
        checkPermissionLocation(this);
    /*    getLocation();
        getCurrentLocation();*/
       // initializeMap();

        Log.e(TAG, "initializeMap: "+"insideinitializeViewMethod" );
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.e(TAG, "initializeMap: fragmentManager "+fm );
        fm.getMapAsync(this);

       // addMarkers();


    }


    ArrayList<LatLng> MarkerPoints;

    public void setMultipleMarkers()
    {

        if (map != null)
        {
            map.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE)
                    .title("First Point"));
            map.addMarker(new MarkerOptions().position(LOWER_MANHATTAN)
                    .title("Second Point"));
            map.addMarker(new MarkerOptions().position(WALL_STREET)
                    .title("Third Point"));
        }

   /*     map.addMarker(new MarkerOptions().position(LOWER_MANHATTAN).draggable(true).title("first")).showInfoWindow();
        map.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE).draggable(true).title("second")).showInfoWindow();
        map.addMarker(new MarkerOptions().position(WALL_STREET).draggable(true).title("Third")).showInfoWindow();

        map.moveCamera(CameraUpdateFactory.newLatLng(LOWER_MANHATTAN));
        map.animateCamera(CameraUpdateFactory.zoomTo(aa));*/

        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);


    }

    private class FetchUrl extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... url)
        {
            Log.e("url ",url+"");
            // For storing data from web service
            String data = "";
            try
            {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.e("Background Task data", data.toString());
            }
            catch (Exception e)
            {
                Log.e("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result)
        {
            Log.e("onPostExecute ", result);
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.e("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.e("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.e("ParserTask","Executing routes");
                Log.e("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.e("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result)
        {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            Log.e("ParseronPostExecute ",result+"");

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++)
            {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++)
                {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.GRAY);
                Log.e("onPostExecute","onPostExecute lineoptions decoded");
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null)
            {
                Log.e("InsidelineOptions ",lineOptions+"");
                map.addPolyline(lineOptions);
            }
            else
            {
                Log.e("onPostExecute ","without Polylines drawn");
            }
        }
    }


    private String downloadUrl(String strUrl) throws IOException
    {
        Log.e("downloadUrl ",strUrl);
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            Log.e("url ",url+"");

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            data = sb.toString();
            Log.e("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.e("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    //todo for drawing polyline
    private String getUrl(LatLng origin, LatLng dest)
    {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+
                "&key="+"AIzaSyCxj7Z3cWeV8phaVuua1cSQ88bWT_ls5u0";
        /*String url="https://maps.googleapis.com/maps/api/directions/"+output+"?"
                +parameters+"&key="+getString(R.string.google_map_key);*/
        return url;
    }


    private String getMapsApiDirectionsUrl()
    {
        String waypoints = "waypoints=optimize:true|"
                + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
                + "|" + "|" + BROOKLYN_BRIDGE.latitude + ","
                + BROOKLYN_BRIDGE.longitude + "|" + WALL_STREET.latitude + ","
                + WALL_STREET.longitude;

        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;
        String output = "json";
      /*  String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;*/

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + params+
                "&key="+"AIzaSyCxj7Z3cWeV8phaVuua1cSQ88bWT_ls5u0";
        return url;
    }

    private void addMarkers() {

        MarkerOptions options = new MarkerOptions();
        options.position(LOWER_MANHATTAN);
        options.position(BROOKLYN_BRIDGE);
        options.position(WALL_STREET);
        if (map != null) {
         /*   map.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE)
                    .title("First Point"));
            map.addMarker(new MarkerOptions().position(LOWER_MANHATTAN)
                    .title("Second Point"));
            map.addMarker(new MarkerOptions().position(WALL_STREET)
                    .title("Third Point"));*/
            map.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE).draggable(true).title("First Point")).showInfoWindow();
            map.addMarker(new MarkerOptions().position(LOWER_MANHATTAN).draggable(true).title("second point")).showInfoWindow();
            map.addMarker(new MarkerOptions().position(WALL_STREET).draggable(true).title("Third point")).showInfoWindow();
        }
    }


    private class ReadTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

/*    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
    {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(2);
                polyLineOptions.color(Color.BLUE);
            }

            map.addPolyline(polyLineOptions);
        }
    }*/

    public void checkPermissionLocation(Context context)
    {
        Log.e(TAG, "checkPermissionLocation: "+"Inside");
        if (Build.VERSION.SDK_INT >= 23)
        {
            int hasNetworkPermission = context.checkSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE);
            int hasaccessCoarseLocation = context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasAccessFineLocation = context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            int hasInternetPermission = context.checkSelfPermission(android.Manifest.permission.INTERNET);
            ArrayList<String> permissionList = new ArrayList<String>();

            if (hasInternetPermission != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(android.Manifest.permission.INTERNET);
            }
            if (hasNetworkPermission != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(android.Manifest.permission.ACCESS_NETWORK_STATE);
            }
            if (hasaccessCoarseLocation != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (hasAccessFineLocation != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (!permissionList.isEmpty())
            {
                requestPermissions(permissionList.toArray(new String[permissionList.size()]), 2);
            }
        }
    }

    public void getLocation()
    {
        gpsTracker = new GpsTracker(this);
        if (gpsTracker.getLatitude() != 0.0)
        {
            longitude = gpsTracker.getLongitude();
            lattitude = gpsTracker.getLatitude();

            Log.e(TAG, "getLocation: Lattitude "+lattitude );
            Log.e(TAG, "getLocation: Lomgitude "+longitude );


            //  showMarkers();
        }
        else
        {
            gpsTracker.showSettingsAlert();
        }
    }


    public void initializeMap()
    {
        Log.e(TAG, "initializeMap: "+"insideinitializeViewMethod" );
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.e(TAG, "initializeMap: fragmentManager "+fm );
        fm.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map=googleMap;



        MarkerOptions options = new MarkerOptions();
        options.position(LOWER_MANHATTAN);
        options.position(BROOKLYN_BRIDGE);
        options.position(WALL_STREET);
        map.addMarker(options);
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BROOKLYN_BRIDGE,
                13));
        setMultipleMarkers();

    }



}
