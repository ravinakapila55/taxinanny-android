package com.taxi.nanny.views.booking;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.taxi.nanny.HttpConnection;
import com.taxi.nanny.R;
import com.taxi.nanny.model.DriverBookingModel;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.directionhelpers.DataParser;
import com.taxi.nanny.utils.location.CustomMarker;
import com.taxi.nanny.utils.location.GPSTracker;
import com.taxi.nanny.utils.location.LocationDistanceDuration;
import com.taxi.nanny.utils.location.TrackGoogleLocation;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.booking.adapter.ViewPagerLocations;
import com.taxi.nanny.views.home.ParentHome;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.OnClick;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LiveTrackBooking extends BaseActivity implements OnMapReadyCallback, LocationListener,
        GoogleMap.OnInfoWindowClickListener,GoogleApiClient.ConnectionCallbacks, LocationDistanceDuration
   {
    ArrayList<RiderListModel> riderList;
    ArrayList<DriverBookingModel> driverList;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    Context context;

    private GPSTracker gpsTracker;
    LocationListener locationListener;
    LocationManager locationManager;
    double lattitude, longitude;

    public static final String TAG=LiveTrackBooking.class.getSimpleName();
    private String currentLocation="";

    MarkerOptions markerOptions;

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @BindView(R.id.ivLeft)
    ImageView ivLeft;

    @BindView(R.id.ivRight)
    ImageView ivRight;

    @BindView(R.id.ivDriver)
    ImageView ivDriver;

    @BindView(R.id.ivCall)
    ImageView ivCall;

    @BindView(R.id.tvDriverName)
    TextView tvDriverName;

    @BindView(R.id.tvVehicle)
    TextView tvVehicle;

    @BindView(R.id.tvPref)
    TextView tvPref;

    int tab;

    private Socket mSocket;

    @BindView(R.id.tvPick)
    TextView tvPick;

    @BindView(R.id.tvDest)
    TextView tvDest;

    static Marker marker;
    static GoogleMap map;
    LatLng latLngDropp;
    LatLng latlongPick;

    String KeyNoti="";

    SharedPrefUtil sharedPrefUtil;


       @Override
    protected int getContentId()
    {
        return R.layout.live_tracking;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("PickUp Arriving");
        checkPermissionLocation(this);

        markerOptions=new MarkerOptions();


        sharedPrefUtil=SharedPrefUtil.getInstance();

        if (getIntent().hasExtra("KeyNoti"))
        {
            KeyNoti=getIntent().getExtras().getString("KeyNoti");

            if (KeyNoti.equalsIgnoreCase("noti"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                driverList=(ArrayList<DriverBookingModel>) getIntent().getSerializableExtra("driver_list");
            }
        }

        else
        {
            riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
            driverList=(ArrayList<DriverBookingModel>) getIntent().getSerializableExtra("driver_list");
        }

        Log.e("TabValue ",tab+"");
        getLocation();
        getCurrentLocation();
        setPagerAdapter();

         latLngDropp=new LatLng(Double.parseDouble(riderList.get(0).getDroplat()),
                Double.parseDouble(riderList.get(0).getDroplng()));

        latlongPick=new LatLng(Double.parseDouble(riderList.get(0).getPickLat()),
                Double.parseDouble(riderList.get(0).getPicklng()));

        tab = viewpager.getCurrentItem();

        tvPick.setText(riderList.get(0).getPickup());
        tvDest.setText(riderList.get(0).getDropup());

        String ridePref="";

     /*   if (!driverList.get(0).getKids_in().equalsIgnoreCase("0") ||
                !driverList.get(0).getKids_out().equalsIgnoreCase("0"))
        {
            tvPref.setVisibility(View.VISIBLE);

            if (driverList.get(0).getKids_in().equalsIgnoreCase("1") && driverList.get(0).getKids_out().equalsIgnoreCase("1"))
            {

                ridePref="Kids-in,Kids-out";
            }
            else if (driverList.get(0).getKids_in().equalsIgnoreCase("1") && driverList.get(0).getKids_out().equalsIgnoreCase("0"))
            {
                ridePref="Kids-in";
            } else if (driverList.get(0).getKids_in().equalsIgnoreCase("0") && driverList.get(0).getKids_out().equalsIgnoreCase("1"))
            {
                ridePref="Kids-out";
            }

            String finalRidePref = ridePref;
            tvPref.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {

                    callAlertPref(finalRidePref);
                }
            });
        }
        else
        {
            tvPref.setVisibility(View.GONE);
        }*/

        if (riderList.size()==1)
        {
            ivLeft.setVisibility(View.INVISIBLE);
            ivRight.setVisibility(View.INVISIBLE);
        }

        else if (riderList.size()>1)
        {
            ivLeft.setVisibility(View.INVISIBLE);
            ivRight.setVisibility(View.VISIBLE);
        }

        setData();
        initializeMap();
//        connectSocket("101");

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {
                connectSocket(driverList.get(0).getRide_id());
//                connectSocket("100");
            }
        },5000);

    }

     public void callAlertPref(String pref)
     {
                 AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                 LayoutInflater inflater = getLayoutInflater();
                 View dialogView = inflater.inflate(R.layout.custom_pop_up, null);
                 final TextView no = (TextView) dialogView.findViewById(R.id.tvNo);
                 final TextView yes = (TextView) dialogView.findViewById(R.id.tvYes);
                 final TextView tvMsg = (TextView) dialogView.findViewById(R.id.tvMsg);
                 final TextView tvOk = (TextView) dialogView.findViewById(R.id.tvOk);
                 final RelativeLayout rl = (RelativeLayout) dialogView.findViewById(R.id.rl);
                 dialogBuilder.setView(dialogView);
                 dialogBuilder.setCancelable(false);
                 final AlertDialog alertDialog = dialogBuilder.create();
                 // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                 int width = WindowManager.LayoutParams.WRAP_CONTENT;
                 int height = WindowManager.LayoutParams.WRAP_CONTENT;

                 rl.setVisibility(View.GONE);
                 tvOk.setVisibility(View.VISIBLE);


                 tvMsg.setText(pref);

                 tvOk.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         alertDialog.dismiss();
                     }
                 });
                 alertDialog.show();
             }


     public void connectSocket(String rideId)
      {
         try {
                     Log.e(TAG, "connectSocket: "+"inside");
                     Log.e(TAG, "Url: "+Constant.SOCKET_URL+rideId);
                     mSocket = IO.socket(Constant.SOCKET_URL+rideId);
                     mSocket.on(Socket.EVENT_CONNECT, onConnect);
                     mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                     mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
                     mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onTimeConnectError);
                     mSocket.on("callbackLoc",callbackLoc);
                     mSocket.connect();
                     //emitRoomJoin();
            }
          catch (Exception e)
          {
                     e.printStackTrace();
          }
      }


     private Emitter.Listener onConnect = new Emitter.Listener()
     {
                 @Override
                 public void call(Object... args)
                 {
                     Log.e(TAG, "call: onConnect"+args.length);
                     Log.e(TAG, "call: onConnect"+args.toString());

                     runOnUiThread(new Runnable()
                     {
                         @Override
                         public void run()
                         {
                             Log.e(TAG, "onConnect");
                             sharedPrefUtil.saveString(SharedPrefUtil.LIVE_TRACKING_ONGOING,"1");
                             for (int jj = 0; jj < args.length; jj++)
                             {
                                 Log.e(TAG, "OnConnectResponseDriverHome " + args[jj]);
                             }
                         }
                     });
                 }
             };

   //{"lat":"30.7046","lng":"76.7179","bearing":"1","accuracy":"2","speed":"34","altitude":"1"}
   private final Emitter.Listener callbackLoc = new Emitter.Listener()
    {
        //Afterrrrr
                 @Override
                 public void call(final Object... args)
                 {
                     runOnUiThread(new Runnable()
                     {
                         @Override
                         public void run()
                         {
                             Log.e(TAG, "onConnect");
                             for (int jj = 0; jj < args.length; jj++)
                             {
                                 Log.e(TAG, "onConnectResponseOfSocketUpdate " + args[jj]);
                                 try {
                                     JSONObject data = (JSONObject) args[0];
                                     String latt = data.getString("lat");
                                     String longg = data.getString("lng");

                                     LatLng latLngDriverUpdate = new LatLng(Double.parseDouble(latt),
                                             Double.parseDouble(longg));

                                     Log.e(TAG, "run:LatLngDriver "+latLngDriverUpdate+"");

                                   //  Toast.makeText(LiveTrackBooking.this, "Update "+latt + "-"+longg, Toast.LENGTH_SHORT).show();

                                     //todo code here
                                /*
                                  if (marker==null)
                                     {
                                         MarkerOptions options=new MarkerOptions();
                                         options.position(latLngDriverUpdate);
                                         options.icon(BitmapDescriptorFactory.fromBitmap
                                                 (CustomMarker. getMarkerBitmapBo0kingLive
                                                         (LiveTrackBooking.this, "", 5)));
                                         marker=map.addMarker(options);

                                       //  map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngDriverUpdate,15));
                                     }
                                     else
                                     {
                                       marker.setPosition(latLngDriverUpdate);
                                     }



                                     map.addMarker(new MarkerOptions()
                                             .position(latLngDropp)
                                             .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.
                                                     getMarkerBitmapBo0kingLive
                                                             (LiveTrackBooking.this, riderList.get(0).getDropup(), 2))));




                                     String url = getUrl(latLngDriverUpdate,latLngDropp);
                                     Log.e(TAG, "url "+url+"");
                                     ReadTask downloadTask = new ReadTask();
                                     downloadTask.execute(url);*/

                                //todo different code

                                  // map.addMarker(new MarkerOptions().position(latlongPick).draggable(true));

                                     new TrackGoogleLocation(LiveTrackBooking.this, map,
                                             LiveTrackBooking.this).
                                             setMarkerLocate(latLngDriverUpdate, latLngDropp, 14,
                                                     "",riderList.get(0).getDropup());

                                  /*   setMarkerLocate(latLngDriverUpdate, latLngDropp, 14,
                                             "",riderList.get(0).getDropup());*/

                                  //todo when only marker move on the source to destination
                                     /*    if (marker==null)
                                     {
                                         MarkerOptions options=new MarkerOptions();
                                         options.position(latLngDriverUpdate);
                                         options.icon(BitmapDescriptorFactory.fromBitmap
                                                 (CustomMarker.getMarkerViewMovement(LiveTrackBooking.this)));
                                         marker=map.addMarker(options);

                                       //  map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngDriverUpdate,15));
                                     }
                                     else
                                     {
                                       marker.setPosition(latLngDriverUpdate);
                                     }*/
                                 }

                                 catch (Exception e)
                                 {
                                     e.printStackTrace();
                                 }
                             }
                         }
                     });
                 }
   };

       private class ReadTask extends AsyncTask<String, Void, String>
       {
           @Override
           protected String doInBackground(String... url)
           {
               String data = "";
               try
               {
                   HttpConnection http = new HttpConnection();
                   data = http.readUrl(url[0]);
               }
               catch (Exception e)
               {
                   Log.e("BackgroundTask", e.toString());
               }
               return data;
           }

           @Override
           protected void onPostExecute(String result)
           {
               Log.e("PostResultFirst ",result);
               super.onPostExecute(result);
               new ParserTask().execute(result);
           }
       }

       PolylineOptions lineOptions = new PolylineOptions();
       LatLng positionLat=null;

       ArrayList<LatLng> points;
       Polyline polyline;

       private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
       {
           // Parsing the data in non-ui thread
           @Override
           protected List<List<HashMap<String, String>>> doInBackground(String... jsonData)
           {

               JSONObject jObject;
               List<List<HashMap<String, String>>> routes = null;

               try {
                   jObject = new JSONObject(jsonData[0]);
                   Log.e("ParserTaskJObject ",jsonData[0].toString());
                   DataParser parser = new DataParser();
                   Log.e("DataParser ", parser.toString());

                   // Starts parsing data
                   routes = parser.parse(jObject);
                   Log.e("routesData..","Executing routes");
                   Log.e("routesValue After ",routes.size()+"");
               }
               catch (Exception e)
               {
                   Log.e("CatchParserTask",e.toString());
                   e.printStackTrace();
               }
               return routes;
           }

           // Executes in UI thread, after the parsing process
           @Override
           protected void onPostExecute(List<List<HashMap<String, String>>> result)
           {
//               ArrayList<LatLng> points;
               PolylineOptions lineOptions = null;
               Log.e("ParseronPostExecute ",result+"");
               Log.e("lineOptions  ",lineOptions+"");
               Log.e("resultLength  ",result.size()+"");

               // Traversing through all the routes
               for (int i = 0; i < result.size(); i++)
               {
                   points = new ArrayList<>();
                   points.clear();
                   Log.e("PointsLength ",points.size()+"");

                   // Fetching i-th route
                   List<HashMap<String, String>> path = result.get(i);

                   path = result.get(i);
                   Log.e("pathLength  ",path.size()+"");

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
                   lineOptions = new PolylineOptions();
                   lineOptions.addAll(points);
                   lineOptions.width(8);
                   lineOptions.color(getResources().getColor(R.color.colorGreen));
                   Log.e("onPostExecute","onPostExecute lineoptions decoded");
               }
               // Drawing polyline in the Google Map for the i-th route
               if(lineOptions != null)
               {
//                   polyline=map.addPolyline(lineOptions);
                   Log.e("LineOptionStatus ",lineOptions+"");
                   map.addPolyline(lineOptions);
               }
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
           Log.e("Parameters ",parameters);

           // Output format
           String output = "json";

           // Building the url to the web service
           String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?"
                   + parameters + "&key=" + Constant.PLACES_API;

           return url;
       }

       Route route;
       ArrayList<LatLng> directionPositionList=new ArrayList<>();
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

                               route = direction.getRouteList().get(0);
                               Log.e("LiveTrackRoute ",route.getBound()+"");

                               directionPositionList.clear();
                               Log.e("LatLngListSizeBefore ",directionPositionList.size()+"");

                               map.addMarker(new MarkerOptions()
                                       .position(latLngPickup)
                                       .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.
                                               getMarkerBitmapBo0kingLive
                                               (LiveTrackBooking.this, pickuptext, 5))));

                               map.addMarker(new MarkerOptions()
                                       .position(latLngDrop)
                                       .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.
                                               getMarkerBitmapBo0kingLive
                                               (LiveTrackBooking.this, dropText, 2))));

                               directionPositionList = route.getLegList().get(0).getDirectionPoint();

                               Log.e("LatLngListSize ",directionPositionList.size()+"");

                               map.addPolyline(DirectionConverter.createPolyline(LiveTrackBooking.this,
                                       directionPositionList,
                                       3, getResources().getColor(R.color.colorGreen)));

                               getDirectionPolyline(direction.getRouteList());

                                 /*
                               if (marker==null)
                               {
                                   MarkerOptions options=new MarkerOptions();
                                   options.position(latLngPickup);
                                   options.icon(BitmapDescriptorFactory.fromBitmap
                                           (CustomMarker.getMarkerBitmapBo0kingLive(LiveTrackBooking.this,
                                                   dropText, 5)));
                                   marker=map.addMarker(options);
                                   //  map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngDriverUpdate,15));
                               }
                               else
                               {
                                   marker.setPosition(latLngPickup);
                               }

*/
                           }
                           else
                           {
                               Toast.makeText(LiveTrackBooking.this, "Direction not found.", Toast.LENGTH_SHORT).show();
                           }
                       }

                       @Override
                       public void onDirectionFailure(Throwable t)
                       {
                           Log.e(TAG, "ERROR" + t.getMessage());
                       }
                   });
       }

       private void getDirectionPolyline(List<Route> routes)
       {
           for (Route route : routes)
           {
               List<Leg> legs = route.getLegList();
               for (Leg leg : legs)
               {
                   String routeDistance = leg.getDistance().getText();
                   String routeDuration = leg.getDuration().getText();
                 //  distanceDuration.getResult(routeDistance, routeDuration);
               }
           }
       }

   @Override
   public void onBackPressed()
    {
      super.onBackPressed();
     // callAlert();
    }

   public void disconnectSocket()
   {
       mSocket.disconnect();
       mSocket.off(Socket.EVENT_CONNECT, onConnect);
       mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
       mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
       mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onTimeConnectError);
       mSocket.off("callbackLoc", callbackLoc);
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
   {

   switch (requestCode)
   {
    case 2:

   for (int i = 0; i < permissions.length; i++)
   {
                             if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                 Toast.makeText(this, "Permitions Allow", Toast.LENGTH_SHORT).show();
                                 //  getLocation();
                             } else if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                             {
                                 // checkPermissionLocation(context);
                                 Toast.makeText(this, "Permitions Denied", Toast.LENGTH_SHORT).show();
                             }
                         }
   break;

   default: {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
         }
   }

           public void callAlert()
            {
                 AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                 LayoutInflater inflater = getLayoutInflater();
                 View dialogView = inflater.inflate(R.layout.custom_pop_up, null);
                 final TextView no = (TextView) dialogView.findViewById(R.id.tvNo);
                 final TextView yes = (TextView) dialogView.findViewById(R.id.tvYes);
                 final TextView tvMsg = (TextView) dialogView.findViewById(R.id.tvMsg);
                 dialogBuilder.setView(dialogView);
                 dialogBuilder.setCancelable(false);
                 final AlertDialog alertDialog = dialogBuilder.create();
                 // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                 int width = WindowManager.LayoutParams.WRAP_CONTENT;
                 int height = WindowManager.LayoutParams.WRAP_CONTENT;

                 tvMsg.setText("Are you sure you want to cancel this booking ?");

                 no.setOnClickListener(new View.OnClickListener()
                 {
                     @Override
                     public void onClick(View view)
                     {
                         alertDialog.dismiss();
                     }
                 });

                 yes.setOnClickListener(new View.OnClickListener()
                 {
                     @Override
                     public void onClick(View view)
                     {
                         SharedPrefUtil.getInstance().onLogout();
                         Intent intent = new Intent(LiveTrackBooking.this, ParentHome.class);
                         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                         startActivity(intent);
                         finish();
                         alertDialog.dismiss();
                     }
                 });

                 alertDialog.show();
             }

    private Emitter.Listener onDisconnect = new Emitter.Listener()
    {
                 @Override
                 public void call(Object... args)
                 {
                     Log.e(TAG, "call: onDisconnect"+args.length );
                 }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener()
    {
                 @Override
                 public void call(Object... args)
                 {
                     Log.e(TAG, "call: onConnectError"+args.length);

                 }
    };

    private Emitter.Listener onTimeConnectError = new Emitter.Listener()
    {
                 @Override
                 public void call(Object... args)
                 {
                     Log.e(TAG, "call: onTimeConnectError"+args.length );
                 }
    };

    public void setData()
    {
        tvDriverName.setText(driverList.get(0).getName());
//        tvVehicle.setText(driverList.get(0).getVehicle_name());
        tvVehicle.setText("Mini");
    }

    public void initializeMap()
    {
        Log.e(TAG, "initializeMap: "+"insideinitializeViewMethod");
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.e(TAG, "initializeMap: fragmentManager "+fm );
        fm.getMapAsync(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.e(TAG, "onDestroyMethod: "+"onDestroy");
     //   disconnectSocket();
    }

    @OnClick({R.id.ivLeft,R.id.ivRight,R.id.ivCall,R.id.img_back_btn,R.id.ivDriver})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ivLeft:

               /* if (tab > 0)
                {
                    tab--;
                    viewpager.setCurrentItem(tab);
                } else if (tab == 0)
                {
                    viewpager.setCurrentItem(tab);
                }
*/
                viewpager.arrowScroll(View.FOCUS_LEFT);

                break;

            case R.id.ivRight:

              /*  tab++;
                viewpager.setCurrentItem(tab);*/

                viewpager.arrowScroll(View.FOCUS_RIGHT);

                break;
             case R.id.ivCall:

                 Log.e(TAG, "onClick: PhoneNumber "+driverList.get(0).getPhoneNumber());
                 String phone = driverList.get(0).getPhoneNumber();
                 Intent intent = new Intent(Intent.ACTION_DIAL,
                         Uri.fromParts("tel", phone, null));
                 startActivity(intent);
                break;

            case R.id.img_back_btn:
                Intent it=new Intent(LiveTrackBooking.this,ParentHome.class);
                it.putExtra("driver_list",(Serializable) driverList);
                it.putExtra("rider_list",(Serializable) riderList);
                startActivityForResult(it,56);
                break;


                case  R.id.ivDriver:
                Intent intent1=new Intent(LiveTrackBooking.this,DriverProfile.class);
                intent1.putExtra("driver_list",(Serializable) driverList);
                intent1.putExtra("rider_list",(Serializable) riderList);
                startActivity(intent1);

                break;

        }
    }

    public void setPagerAdapter()
    {
        ViewPagerLocations pagerAdapter=new ViewPagerLocations(LiveTrackBooking.this,riderList);
        viewpager.setAdapter(pagerAdapter);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int i, float v, int i1)
            {


            }

            @Override
            public void onPageSelected(int i)
            {
                Log.e(TAG, "onPageSelected: PageSelected "+i);
                Log.e(TAG, "ListSize: PageSelected "+riderList.size());

                if (riderList.size()>0)
                {
                    if (i==0)
                    {
                        Log.e(TAG, "onPageSelected: Inside0 "+i);
                        ivLeft.setVisibility(View.INVISIBLE);
                        ivRight.setVisibility(View.VISIBLE);
                    }

                    else if (i>0)
                    {
                        if (i==riderList.size()-1)
                        {
                            Log.e(TAG, "onPageSelected: last "+i);
                            ivLeft.setVisibility(View.VISIBLE);
                            ivRight.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            Log.e(TAG, "onPageSelected: gretaerthan0 " + i);
                            ivLeft.setVisibility(View.VISIBLE);
                            ivRight.setVisibility(View.VISIBLE);
                        }
                    }
                }

                tvPick.setText(riderList.get(i).getPickup());
                tvDest.setText(riderList.get(i).getDropup());

                LatLng latLng=new LatLng(Double.parseDouble(riderList.get(i).getPickLat()),
                        Double.parseDouble(riderList.get(i).getPicklng()));
                LatLng latLng1=new LatLng(Double.parseDouble(riderList.get(i).getDroplat()),
                        Double.parseDouble(riderList.get(i).getDroplng()));

                String setTime=riderList.get(i).getTime_eta();

                setMultipleMarkers(latLng,latLng1,riderList.get(i).getPickup(),riderList.get(i).getDropup(),setTime);
            }

            @Override
            public void onPageScrollStateChanged(int i)
            {

            }
        });
    }

    public void setMultipleMarkers(LatLng pick,LatLng drop,String pickLoc,String dropLoc,String est)
    {
        map.clear();
        map.moveCamera(CameraUpdateFactory.newLatLng(pick));
        map.animateCamera(CameraUpdateFactory.zoomTo(11));
//        map.addMarker(new MarkerOptions().position(pick).draggable(true));
//        map.addMarker(new MarkerOptions().position(drop).draggable(true));

        //to draw poly line
        new TrackGoogleLocation(this, map, this).setMarkerLocate(pick, drop, 10,
                pickLoc, dropLoc);
    }

    @Override
    public void getResult(String distance, String duration)
    {
        Log.e("SetResult  ",distance);
        Log.e("Dureation  ",duration);

        for (int i = 0; i <riderList.size() ; i++)
        {
            riderList.get(i).setEstDistance(distance);
            riderList.get(i).setTime_eta(duration);
        }
    }

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
        gpsTracker = new GPSTracker(this);
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

    private void getCurrentLocation()
    {
        Log.e(TAG, "getCurrentLocation: "+"Inside");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider callingSaveFrameFaceDetector
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults)
            // case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try
        {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.e(TAG, "onLocationChanged: "+location);
        lattitude=location.getLatitude();
        longitude=location.getLongitude();
        Log.e(TAG, "lattitude: "+lattitude);
        Log.e(TAG, "longitude: "+longitude);
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        Log.e("FusedLatt",currentLatitude+"");
        Log.e("FusedLong",currentLongitude+"");

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            Log.e(TAG, "onLocationChanged: List "+addresses.toString());
            Log.e(TAG, "onLocationChanged: Location1 "+addresses.get(0).getAddressLine(0));
            currentLocation=addresses.get(0).getAddressLine(0);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map=googleMap;
        LatLng latLng=new LatLng(Double.parseDouble(riderList.get(0).getPickLat()),
                Double.parseDouble(riderList.get(0).getPicklng()));
        LatLng latLng1=new LatLng(Double.parseDouble(riderList.get(0).getDroplat()),
                Double.parseDouble(riderList.get(0).getDroplng()));

        String setTime=riderList.get(0).getTime_eta();

      /*  double time=Double.parseDouble(riderList.get(0).getTime_eta());
        String timeee= new DecimalFormat("##.##").format(time);

        String time1= String.valueOf(Math.round(Double.parseDouble(timeee)));

        String setTime="";

        if (Integer.parseInt(time1)==60)
        {
            setTime="1 hr";
        }
        else if (Integer.parseInt(time1)<60)
        {

            setTime=time+" min";
        }
        else if (Integer.parseInt(time1)>60)
        {
            int hours=Integer.parseInt(time1)/60;
            int minutes=Integer.parseInt(time1)%60;

            if (minutes==0)
            {

                setTime=hours+" hrs ";
            }
            else
            {
                setTime=hours+" hrs "+minutes+" min";
            }
        }*/
        setMultipleMarkers(latLng,latLng1,riderList.get(0).getPickup(),riderList.get(0).getDropup(),setTime);
    }
}
