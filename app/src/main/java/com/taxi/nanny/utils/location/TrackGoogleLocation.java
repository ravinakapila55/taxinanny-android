package com.taxi.nanny.utils.location;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.taxi.nanny.R;
import com.taxi.nanny.utils.Constant;
import org.json.JSONArray;
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

public class TrackGoogleLocation
{
    Context context;
    static GoogleMap mMap;
    LocationDistanceDuration distanceDuration;
    PolylineModel polylineModel;

    int value = 0;
    String pickuptext = "", dropText = "";

    private static String TAG = TrackGoogleLocation.class.getSimpleName();

    public TrackGoogleLocation(Context context, GoogleMap mMap, LocationDistanceDuration distanceDuration)
    {
        this.context = context;
        this.mMap = mMap;
        this.distanceDuration = distanceDuration;
    }

    public TrackGoogleLocation(Context context, LocationDistanceDuration distanceDuration)
    {
        Log.e("InsideTrack ","constructor");
        this.context = context;
        this.distanceDuration = distanceDuration;
    }

    public  class FetchUrl extends AsyncTask<String, Void, String>
    {
        FetchUrl(Context mcontext)
        {
            context = mcontext;
        }

        @Override
        protected String doInBackground(String... url)
        {
            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
                }
            catch (Exception e)
            {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    public String getUrl(LatLng origin, LatLng dest)
    {
        Log.e("getting", "url");

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?"
                + parameters + "&key=" + Constant.PLACES_API;

        return url;
    }

    private  String downloadUrl(String strUrl) throws IOException
    {

        String data = "";

        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try
        {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        }

        catch (Exception e)
        {
            Log.d("Exception", e.toString());
        }

        finally
        {
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    private  class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
    {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData)
        {

            JSONObject jObject;

            List<List<HashMap<String, String>>> routes = null;

            try
            {
                jObject = new JSONObject(jsonData[0]);
                Log.e("ParserTask", jsonData[0].toString());

                DataParser parser = new DataParser();
                Log.e("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);

                Log.e("ParserTask", "Executing routes");
                Log.e("ParserTask", routes.toString());


                JSONObject jsonObject = new JSONObject(jsonData[0]);
                Log.e("mate", jsonObject.toString());

                JSONArray routesArray = jsonObject.getJSONArray("routes");
                {
                    for (int i = 0; i < routesArray.length(); i++) {
                        JSONObject routesObject = routesArray.getJSONObject(0);
                        if (!routesObject.isNull("legs")) {
                            JSONArray legsArray = routesObject.getJSONArray("legs");

                            for (int j = 0; j < legsArray.length(); j++) {
                                JSONObject legsObject = legsArray.getJSONObject(0);
                                if (!legsObject.isNull("duration")) {
                                    final JSONObject durationObject = legsObject.getJSONObject("duration");
                                    Log.e("namefhf", "mascg gbhf" + durationObject.getString("text"));


                                }
                                if (!legsObject.isNull("distance")) {
                                    JSONObject distanceObject = legsObject.getJSONObject("distance");


                                }
                            }
                        }

                    }
                }
            }

            catch (Exception e)
            {
                Log.e("ParserTask", e.toString());
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
                lineOptions.width(7);
                lineOptions.color(R.color.polyline_gray);

                Log.e("onPostExecute", "onPostExecute lineoptions decoded");
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.e("onPostExecute", "without Polylines drawn");
            }
        }
    }

    public void getEstimate(final LatLng latlngPick,final LatLng latlngDrop)
    {
        Log.e("InsideEstimate ","");
        GoogleDirection.withServerKey(Constant.PLACES_API)
                .from(latlngPick)
                .to(latlngDrop).alternativeRoute(true)
                .avoid("toll")
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody)
                    {

                        if (direction.isOK())
                        {
                            getDirectionPolyline(direction.getRouteList(),"0");
                        }
                        else
                        {
                            Log.e("RouteNotDefinec","");
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t)
                    {

                    }
                });
    }

    public void setMarkerLocateBooking( final LatLng latLngPickup, final LatLng latLngDrop,
                                 final int value, final String pickuptext, final String dropText)
    {

        Log.e("InsideSetmarkerLocate ","setMarkerLocate");

        GoogleDirection.withServerKey(Constant.PLACES_API)
                .from(latLngPickup)
                .to(latLngDrop)
                .alternativeRoute(true)
                .avoid("toll")
                .execute(new DirectionCallback()
                {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody)
                    {
                        if (direction.isOK())
                        {
                            Log.e(TAG, "onDirectionSuccess:" + direction.getRouteList().toString());

                            Route route = direction.getRouteList().get(0);

                            if (value == 1)
                            {
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngPickup)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView
                                                (context, pickuptext, 1)))).showInfoWindow();
                            }

                            else if (value == 2)
                            {
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngDrop)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView
                                                (context, dropText, 2)))).showInfoWindow();

                            }


                            else if (value==4)
                            {
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngDrop)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerViewMovement
                                                (context)))).showInfoWindow();
                            }

                            else if (value==8)
                            {
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngPickup)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView11
                                                (context, pickuptext, 1)))).showInfoWindow();

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngDrop)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView11
                                                (context, dropText, 2)))).showInfoWindow();
                            }

                            else if (value ==10)
                            {

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngPickup)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapBo0king
                                                (context, pickuptext, 1))));

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngDrop)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapBo0king
                                                (context, dropText, 2))));

                            }

                            else if (value ==16)
                            {

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngPickup)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapBo0king
                                                (context, pickuptext, 1))));

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngDrop)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapBo0king
                                                (context, dropText, 2))));

                            }

                            else if (value ==14)
                            {

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngPickup)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapBo0kingLive
                                                (context, pickuptext, 5))));

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngDrop)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapBo0kingLive
                                                (context, dropText, 2))));
                            }

                            else
                            {
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngPickup)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView
                                                (context, pickuptext, 1)))).showInfoWindow();

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngDrop)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView
                                                (context, dropText, 2)))).showInfoWindow();
                            }

                            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();

                            if (value==14)
                            {
                                mMap.addPolyline(DirectionConverter.createPolyline(context, directionPositionList,
                                        3, context.getResources().getColor(R.color.colorGreen)));
                            }

                            else if (value==16)
                            {
                                mMap.addPolyline(DirectionConverter.createPolyline(context, directionPositionList,
                                        7, context.getResources().getColor(R.color.polyline_gray)));
                            }

                            else
                            {
                                mMap.addPolyline(DirectionConverter.createPolyline(context, directionPositionList,
                                        7, context.getResources().getColor(R.color.polyline_gray)));
                                setCameraWithCoordinationBounds(route);
                            }

                            //todo new
                            mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(context));
                            getDirectionPolyline(direction.getRouteList(),"1");
                        }
                        else
                        {
                            Toast.makeText(context, "Direction not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onDirectionFailure(Throwable t)
                    {
                        Log.e(TAG, "ERROR" + t.getMessage());
                    }
                });
    }

    public void setMarkerLocate( final LatLng latLngPickup, final LatLng latLngDrop,
                                final int value, final String pickuptext, final String dropText)
    {

        Log.e("InsideSetmarkerLocate ","setMarkerLocate");

        GoogleDirection.withServerKey(Constant.PLACES_API)
                .from(latLngPickup)
                .to(latLngDrop)
                .alternativeRoute(true)
                .avoid("toll")
                .execute(new DirectionCallback()
                {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody)
                    {
                       if (direction.isOK())
                       {
                            Log.e(TAG, "onDirectionSuccess:" + direction.getRouteList().toString());

                            Route route = direction.getRouteList().get(0);

                            if (value == 1)
                            {
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngPickup)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView
                                                (context, pickuptext, 1)))).showInfoWindow();
                            }

                            else if (value == 2)
                            {
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngDrop)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView
                                                (context, dropText, 2)))).showInfoWindow();

                            }


                            else if (value==4)
                            {
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngDrop)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerViewMovement
                                                (context)))).showInfoWindow();
                            }

                            else if (value==8)
                            {
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngPickup)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView11
                                                (context, pickuptext, 1)))).showInfoWindow();

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngDrop)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView11
                                                (context, dropText, 2)))).showInfoWindow();
                            }

                            else if (value ==10)
                            {

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngPickup)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapBo0king
                                                (context, pickuptext, 1))));

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngDrop)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapBo0king
                                                (context, dropText, 2))));

                            }

                            else if (value ==16)
                            {

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngPickup)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapBo0king
                                                (context, pickuptext, 1))));

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngDrop)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapBo0king
                                                (context, dropText, 2))));

                            }

                            else if (value ==14)
                            {

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngPickup)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapBo0kingLive
                                                (context, pickuptext, 5))));

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngDrop)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapBo0kingLive
                                                (context, dropText, 2))));
                            }

                            else
                            {
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngPickup)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView
                                                (context, pickuptext, 1)))).showInfoWindow();

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngDrop)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView
                                                (context, dropText, 2)))).showInfoWindow();
                            }

                            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();

                            if (value==14)
                            {
                                mMap.addPolyline(DirectionConverter.createPolyline(context, directionPositionList,
                                        3, context.getResources().getColor(R.color.colorGreen)));
                            }

                            else if (value==16)
                            {
                                mMap.addPolyline(DirectionConverter.createPolyline(context, directionPositionList,
                                        7, context.getResources().getColor(R.color.polyline_gray)));
                            }

                            else
                            {
                                mMap.addPolyline(DirectionConverter.createPolyline(context, directionPositionList,
                                        7, context.getResources().getColor(R.color.polyline_gray)));
                                setCameraWithCoordinationBounds(route);
                            }

                            //todo new
                            mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(context));
                            getDirectionPolyline(direction.getRouteList(),"0");
                        }
                       else
                       {
                            Toast.makeText(context, "Direction not found.", Toast.LENGTH_SHORT).show();
                       }
                    }
                    @Override
                    public void onDirectionFailure(Throwable t)
                    {
                        Log.e(TAG, "ERROR" + t.getMessage());
                    }
                });
    }

    public void setMarkerLocateNew(final LatLng latLngPickup, final LatLng latLngDrop,
                                   final int value)
    {
        GoogleDirection.withServerKey(Constant.PLACES_API)
                .from(latLngPickup)
                .to(latLngDrop)
                .alternativeRoute(true)
                .avoid("toll")
                .execute(new DirectionCallback()
                {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody)
                    {
                        if (direction.isOK())
                        {
                            Log.e(TAG, "onDirectionSuccess: " + direction.getRouteList().toString());
                            Route route = direction.getRouteList().get(0);

                            if (value == 1)
                            {
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngPickup)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView
                                                (context, pickuptext, 1)))).showInfoWindow();
                            } else if (value == 2)
                            {
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngDrop)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView
                                                (context, dropText, 2)))).showInfoWindow();

                            }
                           else
                          {
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngPickup)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView
                                                (context, pickuptext, 1)))).showInfoWindow();
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngDrop)
                                        .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getMarkerBitmapFromView
                                                (context, dropText, 2)))).showInfoWindow();
                            }

                            ArrayList<LatLng> directionPositionList =
                                    route.getLegList().get(0).getDirectionPoint();
                            mMap.addPolyline(DirectionConverter.createPolyline(context,
                                    directionPositionList,
                                    7, context.getResources().getColor(R.color.polyline_gray)));
                            setCameraWithCoordinationBounds(route);
                            //todo new
                            mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(context));
                            getDirectionPolyline(direction.getRouteList(),"0");
                        }

                        else
                        {
                            Toast.makeText(context, "Direction not found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t)
                    {
                        Log.e(TAG, "ERROR" + t.getMessage());
                    }
                });
    }

    private void getDirectionPolyline(List<Route> routes,String value)
    {
        for (Route route : routes)
        {
            List<Leg> legs = route.getLegList();
            for (Leg leg : legs)
            {
                String routeDistance = leg.getDistance().getText();
                String routeDuration = leg.getDuration().getText();
                distanceDuration.getResult(routeDistance, routeDuration);
            }
        }
    }

    private void setCameraWithCoordinationBounds(Route route)
    {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        private Context ctx;

        public MyInfoWindowAdapter(Context ctx)
        {
            this.ctx = ctx;
        }

        @Override
        public View getInfoContents(Marker marker)
        {
            return null;
        }

        @Override
        public View getInfoWindow(Marker marker)
        {
            View v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.custom_map_window, null);

            LatLng latLng = marker.getPosition();

            TextView tvLocation = (TextView) v.findViewById(R.id.tvLocation);

            ImageView ivLocation = (ImageView) v.findViewById(R.id.ivLocation);

            tvLocation.setText(GeoLocation.getAddress(context,
                    Double.parseDouble(String.valueOf(marker.getPosition().latitude)),
                    Double.parseDouble(String.valueOf(marker.getPosition().longitude))));

            return v;
        }
    }
}
