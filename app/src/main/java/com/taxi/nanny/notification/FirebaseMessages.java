package com.taxi.nanny.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.taxi.nanny.R;
import com.taxi.nanny.model.DriverBookingModel;
import com.taxi.nanny.model.RecurringDaysModel;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.booking.LiveTrackBooking;
import com.taxi.nanny.views.booking.SubmitReview;
import com.taxi.nanny.views.driver.DriverHomeNotification;
import com.taxi.nanny.views.home.ParentHome;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class FirebaseMessages extends FirebaseMessagingService
{
    private String title, message;
    private Map<String, String> data;
    private String  type;
    Intent intent;
    private static final String TAG = "FCM";
    Notification.Builder builder;

    /*Todo Payload*/
/*
   [{
	"drop_location_lat": "30.7333148",
	"ride_id": 1481,
	"lng": "74.8722642",
	"distance": 128.75809648232607,
	"sign_up": "1",
	"drop_location_lng": "76.7794179",
	"booking_id": 773,
	"location_name": "Amritsar, Punjab, India",
	"reaccuring": [],
	"enddate": null,
	"ridestatus": 0,
	"priority_drop": "1",
	"price": 203.6371447234891,
	"drop_location_name": "Chandigarh, India",
	"priority_pick": "1",
	"booking_type": "2",
	"time": 4.15,
	"time_of_ride": "aa:23:45",
	"rider_id": "518",
	"lat": "31.6339793",
	"sign_in": "0"
}]
*/

       //ArriveDriver
       /*
        {
	     data =
	     {
		"ride_id": "379",
		"vehicle_color": "black",
		"pickup_longitude": "77.3910265",
		"pickup_location_name": "Noida, Uttar Pradesh, India",
		"message": "Driver Arrived",
		"driver_name": "Nipun Rajput",
		"ridestatus": 1,
		"vehicle_number": "PB12C1254",
		"pickup_latitude": "28.5355161",
		"model": "Djdj",
		"drop_latitude": "31.6339793",
		"dropoff_location_name": "Amritsar, Punjab, India",
		"drop_longitude": "74.8722642",
		"vehicle_name": "Honda city"
	}, type = Driver Arrived, title = Driver Arrived, message = Driver Arrived
} */


    //pickedByDRiver
    /*{data={"ride_id":"379","vehicle_color":"black","pickup_longitude":"77.3910265",
    "pickup_location_name":"Noida, Uttar Pradesh, India","message":"Picked By Driver",
    "driver_name":"Nipun Rajput","ridestatus":3,"vehicle_number":"PB12C1254",
    "pickup_latitude":"28.5355161","model":"Djdj","drop_latitude":"31.6339793","dropoff_location_name":"Amritsar, Punjab, India","drop_longitude":"74.8722642","vehicle_name":"Honda city"}, type=Picked By Driver, title=Picked By Driver, message=Picked By Driver}*/

    //Complete Trip
    /*{
	data = {
		"ride_id": "379",
		"vehicle_color": "black",
		"pickup_longitude": "77.3910265",
		"pickup_location_name": "Noida, Uttar Pradesh, India",
		"message": "Droped By Driver",
		"driver_name": "Nipun Rajput",
		"ridestatus": 4,
		"vehicle_number": "PB12C1254",
		"pickup_latitude": "28.5355161",
		"model": "Djdj",
		"drop_latitude": "31.6339793",
		"dropoff_location_name": "Amritsar, Punjab, India",
		"drop_longitude": "74.8722642",
		"vehicle_name": "Honda city"
	}, type = Droped By Driver, title = Droped By Driver, message = Droped By Driver
}*/


    /*Todo driver ARrived notification to parent*/
    /*{data=[{"message":"Driver Arrived"}], type=booking, title=new booking, message=New Booking }*/

    /*Todo driver Start Trip*/
    /*{data={"message":"Picked By Driver"}, type=booking, title=new booking, message=New Booking }*/

    //todo complete notification
    /*{
	"ride_id": "1497",
	"vehicle_color": null,
	"driver_id": 10,
	"pickup_longitude": "75.911483",
	"distance": 74.47602383137983,
	"pickup_location_name": "Hoshiarpur, Punjab, India",
	"message": "Droped By Driver",
	"driver_name": "John Smith",
	"booking_id": "785",
	"ridestatus": 4,
	"vehicle_number": null,
	"pickup_latitude": "31.5143178",
	"Total_fare_deducted": 128.31001,
	"model": null,
	"drop_latitude": "30.7333148",
	"dropoff_location_name": "Chandigarh, India",
	"drop_longitude": "76.7794179",
	"vehicle_name": null
}*/


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        //AcceptRide
        try
        {
            if (remoteMessage.getData() != null)
            {
                data = remoteMessage.getData();
                Log.e(TAG, "onMessageReceived:data "+remoteMessage.getData().get("data"));
//                Log.e(TAG, "onMessageReceived:Body "+remoteMessage.getNotification().getBody());
                Log.e(TAG, "onMessageReceivedMapp: "+data);

                type = data.get("type");
                message = data.get("message");
                title = data.get("title");

                Log.e(TAG, "type "+type);
                Log.e(TAG, "message "+message);
                Log.e(TAG, "title "+title);
                JSONObject jsonObject;

                if (type.equalsIgnoreCase("Droped By Driver"))
                {
                    SharedPrefUtil.getInstance().saveString(SharedPrefUtil.LIVE_TRACKING_ONGOING,"0");
                }
                else if (type.equalsIgnoreCase("Driver Arrived"))
                {
                    SharedPrefUtil.getInstance().saveString(SharedPrefUtil.LIVE_TRACKING_ONGOING,"1");
                }
                else if (type.equalsIgnoreCase("Picked By Driver"))
                {
                    SharedPrefUtil.getInstance().saveString(SharedPrefUtil.LIVE_TRACKING_ONGOING,"1");
                }

                if (SharedPrefUtil.getInstance().getString(SharedPrefUtil.USER_ID)!=null)
                {
                    SharedPrefUtil.getInstance().saveString(SharedPrefUtil.NOTIFY_KEY,"notify");

                 /*   if (Constant.checkActivation(getApplicationContext()))
                    {*/
                        Log.e(TAG, "onMessageReceived: Inside"+"inside");

                        //todo when notification setting is enabled
                    if (SharedPrefUtil.getInstance().getString(SharedPrefUtil.NOTIFICATION_SETTING).
                            equalsIgnoreCase("1"))
                    {
                        if (SharedPrefUtil.getInstance().getString(SharedPrefUtil.USERTYPE).
                                equalsIgnoreCase("driver"))
                        {
                            if (type.equalsIgnoreCase("booking"))
                            {
                                Log.e(TAG, "type " + "inside");

                                String obj = data.get("data");
                                JSONArray ride_detail = new JSONArray(obj);
                                Log.e(TAG, "jsonArray : Inside " + ride_detail.length());
                                ArrayList<RiderListModel> rideList = new ArrayList<>();
                                rideList.clear();

                                for (int i = 0; i < ride_detail.length(); i++)
                                {
                                    JSONObject rideObj = ride_detail.getJSONObject(i);
                                    JSONObject rideObjBooking = ride_detail.getJSONObject(0);
                                    RiderListModel riderListModel = new RiderListModel();
                                    riderListModel.setId(rideObj.getString("rider_id"));
                                    riderListModel.setDropPriority(rideObj.getInt("priority_drop"));
                                    riderListModel.setPickPriority(rideObj.getInt("priority_pick"));
                                    riderListModel.setPickup(rideObj.getString("location_name"));
                                    riderListModel.setPickLat(rideObj.getString("lat"));
                                    riderListModel.setPicklng(rideObj.getString("lng"));
                                    riderListModel.setKids_in(rideObj.getString("kids_sign_in"));
                                    riderListModel.setKids_out(rideObj.getString("kids_sign_up"));
                                    riderListModel.setTime_booking(rideObj.getString("time_of_ride"));
                                    riderListModel.setBooking_date(rideObj.getString("booking_date"));
                                    riderListModel.setDroplat(rideObj.getString("drop_location_lat"));
                                    riderListModel.setDroplng(rideObj.getString("drop_location_lng"));
                                    riderListModel.setDropup(rideObj.getString("drop_location_name"));
                                    riderListModel.setEstDistance(rideObj.getString("distance"));
                                    riderListModel.setTime_eta(rideObj.getString("time"));
//                                    riderListModel.setEstPrice(rideObj.getString("price"));
                                    riderListModel.setEstPrice(rideObj.getString("new_amount"));
                                    riderListModel.setRide_status(rideObj.getString("ridestatus"));
                                    riderListModel.setPick_instructions(rideObj.getString("pick_up_instruction"));
                                    riderListModel.setDrop_instructions(rideObj.getString("drop_off_instruction"));

                                    JSONArray reaccuring=rideObj.getJSONArray("reaccuring");
                                    Log.e("ArrayLength ",reaccuring.length()+"");

                                    //for recurring
                                    if (reaccuring.length()>0)
                                    {
                                        Log.e("InsideRecurring ",reaccuring.length()+"");
                                        riderListModel.setBooking_type("1");
                                        riderListModel.setEnddate(rideObj.getString("enddate"));

                                        ArrayList<RecurringDaysModel> rideDaysList = new ArrayList<>();

                                        for (int k = 0; k <reaccuring.length() ; k++)
                                        {
                                            JSONObject  recObj=reaccuring.getJSONObject(k);

                                            RecurringDaysModel daysModel=new RecurringDaysModel();
                                            daysModel.setDay(recObj.getString("day"));
                                            // daysModel.setDate(recObj.getString("date"));
                                            rideDaysList.add(daysModel);
                                        }
                                        riderListModel.setRideDaysList(rideDaysList);
                                    }

                                    else if (reaccuring.length()==0)
                                    {
                                        riderListModel.setBooking_type("2");
                                    }

                                    riderListModel.setBooking_id(rideObjBooking.getString("booking_id"));
                                    //  riderListModel.setRide_id(rideObjBooking.getString("ride_id"));
                                    rideList.add(riderListModel);
                                }
                                Log.e("RideListSendBefore ",rideList.size()+"");
                                Log.e("BookinGTypeNoti ",rideList.get(0).getBooking_type()+"");

                                intent = new Intent(this, DriverHomeNotification.class);
                                intent.putExtra("rider_list", (Serializable) rideList);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(121);
                                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                //showSmallNotification(message,title,pendingIntent);
                                sendNotificationnn(getApplicationContext(), intent, message, title);
                            }
                        }

                        //for parent
                        else
                        {
                            if (type.equalsIgnoreCase("Driver Arrived"))
                            {
                                Log.e(TAG, "type " + "inside");

                                String obj = data.get("data");

                                Log.e(TAG, "onMessageReceived: ArriverObje "+obj);

                                JSONObject rideObj=new JSONObject(obj);

                                Log.e(TAG, "onMessageReceived: ArriverObjeAfter "+rideObj);
                                ArrayList<RiderListModel> rideList = new ArrayList<>();
                                rideList.clear();

                                ArrayList<DriverBookingModel> driverList=new ArrayList<>();
                                driverList.clear();

                                RiderListModel riderListModel = new RiderListModel();
//                                riderListModel.setId(rideObj.getString("rider_id"));
                               /* riderListModel.setDropPriority(rideObj.getInt("priority_drop"));
                                riderListModel.setPickPriority(rideObj.getInt("priority_pick"));*/

                                riderListModel.setPickup(rideObj.getString("pickup_location_name"));
                                riderListModel.setPickLat(rideObj.getString("pickup_latitude"));
                                riderListModel.setPicklng(rideObj.getString("pickup_longitude"));
                                riderListModel.setDroplat(rideObj.getString("drop_latitude"));
                                riderListModel.setDroplng(rideObj.getString("drop_longitude"));
                                riderListModel.setDropup(rideObj.getString("dropoff_location_name"));
                              /*  riderListModel.setEstDistance(rideObj.getString("distance"));
                                riderListModel.setTime_eta(rideObj.getString("time"));
                                riderListModel.setEstPrice(rideObj.getString("price"));*/
//                                riderListModel.setBooking_id(rideObjBooking.getString("booking_id"));
                                riderListModel.setRide_id(rideObj.getString("ride_id"));
                                riderListModel.setRide_status(rideObj.getString("ridestatus"));
                                rideList.add(riderListModel);

                                DriverBookingModel bookingModel=new DriverBookingModel();

                                bookingModel.setRide_id(rideObj.getString("ride_id"));
                                bookingModel.setVehicle_number(rideObj.getString("vehicle_number"));
                                bookingModel.setVehicle_name(rideObj.getString("vehicle_name"));
                                bookingModel.setVehicle_make("NA");
                                bookingModel.setVehicle_image("NA");
                                bookingModel.setLicence_number("NA");
                                bookingModel.setIssue_state("NA");
                                bookingModel.setPhoneNumber("12345678");
                                bookingModel.setName(rideObj.getString("driver_name"));
                                bookingModel.setKids_in("1");
                                bookingModel.setKids_out("0");
                                driverList.add(bookingModel);

                                SharedPrefUtil.getInstance().saveString(SharedPrefUtil.LIVE_TRACKING_ONGOING,"1");


                                intent = new Intent(this, LiveTrackBooking.class);
                                intent.putExtra("KeyNoti","noti");
                                intent.putExtra("rider_list", (Serializable) rideList);
                                intent.putExtra("driver_list", (Serializable) driverList);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(121);
                                PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);
                                //showSmallNotification(message,title,pendingIntent);
                                sendNotificationnn(getApplicationContext(), intent, message, title);
                            }

                            else if (type.equalsIgnoreCase("Picked By Driver"))
                            {
                                Log.e(TAG, "type " + "inside");

                                String obj = data.get("data");

                                Log.e(TAG, "onMessageReceived: ArriverObje "+obj);

                                JSONObject rideObj=new JSONObject(obj);

                                Log.e(TAG, "onMessageReceived: ArriverObjeAfter "+rideObj);
                                ArrayList<RiderListModel> rideList = new ArrayList<>();
                                rideList.clear();

                                ArrayList<DriverBookingModel> driverList=new ArrayList<>();
                                driverList.clear();

                                RiderListModel riderListModel = new RiderListModel();
//                                riderListModel.setId(rideObj.getString("rider_id"));
                               /* riderListModel.setDropPriority(rideObj.getInt("priority_drop"));
                                riderListModel.setPickPriority(rideObj.getInt("priority_pick"));*/

                                riderListModel.setPickup(rideObj.getString("pickup_location_name"));
                                riderListModel.setPickLat(rideObj.getString("pickup_latitude"));
                                riderListModel.setPicklng(rideObj.getString("pickup_longitude"));
                                riderListModel.setDroplat(rideObj.getString("drop_latitude"));
                                riderListModel.setDroplng(rideObj.getString("drop_longitude"));
                                riderListModel.setDropup(rideObj.getString("dropoff_location_name"));

                              /*  riderListModel.setEstDistance(rideObj.getString("distance"));
                                riderListModel.setTime_eta(rideObj.getString("time"));
                                riderListModel.setEstPrice(rideObj.getString("price"));*/
//                                riderListModel.setBooking_id(rideObjBooking.getString("booking_id"));
                                riderListModel.setRide_id(rideObj.getString("ride_id"));
                                riderListModel.setRide_status(rideObj.getString("ridestatus"));
                                rideList.add(riderListModel);

                                DriverBookingModel bookingModel=new DriverBookingModel();

                                bookingModel.setRide_id(rideObj.getString("ride_id"));
                                bookingModel.setVehicle_number(rideObj.getString("vehicle_number"));
                                bookingModel.setVehicle_name(rideObj.getString("vehicle_name"));
                                bookingModel.setPhoneNumber("12345678");
                                bookingModel.setName(rideObj.getString("driver_name"));
                                bookingModel.setVehicle_make("NA");
                                bookingModel.setVehicle_image("NA");
                                bookingModel.setLicence_number("NA");
                                bookingModel.setIssue_state("NA");
                                bookingModel.setKids_in("1");
                                bookingModel.setKids_out("0");
                                driverList.add(bookingModel);

                                SharedPrefUtil.getInstance().saveString(SharedPrefUtil.LIVE_TRACKING_ONGOING,"1");

                                intent = new Intent(this, LiveTrackBooking.class);
                                intent.putExtra("KeyNoti","noti");
                                intent.putExtra("rider_list", (Serializable) rideList);
                                intent.putExtra("driver_list", (Serializable) driverList);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(121);
                                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                //showSmallNotification(message,title,pendingIntent);
//                                sendNotificationnn(getApplicationContext(), intent, message, title);
                                sendNotificationnn(getApplicationContext(), intent, "ON TRIP", title);
                            }

                            else  if (type.equalsIgnoreCase("Droped By Driver"))
                            {
                                Log.e(TAG, "type " + "inside");

                                String obj = data.get("data");

                                Log.e(TAG, "onMessageReceived: ArriverObje "+obj);

                                JSONObject rideObj=new JSONObject(obj);

                                Log.e(TAG, "onMessageReceived: ArriverObjeAfter "+rideObj);
                                ArrayList<RiderListModel> rideList = new ArrayList<>();
                                rideList.clear();

                                ArrayList<DriverBookingModel> driverList=new ArrayList<>();
                                driverList.clear();

                                RiderListModel riderListModel = new RiderListModel();
//                                riderListModel.setId(rideObj.getString("rider_id"));
                               /* riderListModel.setDropPriority(rideObj.getInt("priority_drop"));
                                riderListModel.setPickPriority(rideObj.getInt("priority_pick"));*/
                                riderListModel.setPickup(rideObj.getString("pickup_location_name"));
                                riderListModel.setPickLat(rideObj.getString("pickup_latitude"));
                                riderListModel.setPicklng(rideObj.getString("pickup_longitude"));
                                riderListModel.setDroplat(rideObj.getString("drop_latitude"));
                                riderListModel.setDroplng(rideObj.getString("drop_longitude"));
                                riderListModel.setDropup(rideObj.getString("dropoff_location_name"));

                                riderListModel.setEstDistance(rideObj.getString("distance"));
//                                riderListModel.setTime_eta(rideObj.getString("time"));
                                riderListModel.setEstPrice(rideObj.getString("Total_fare_deducted"));
//                                riderListModel.setBooking_id(rideObjBooking.getString("booking_id"));
                                riderListModel.setRide_id(rideObj.getString("ride_id"));
                                riderListModel.setRide_status(rideObj.getString("ridestatus"));
                                riderListModel.setBooking_id(rideObj.getString("booking_id"));
                                rideList.add(riderListModel);

                                DriverBookingModel bookingModel=new DriverBookingModel();

                                bookingModel.setRide_id(rideObj.getString("ride_id"));
                                bookingModel.setDriverId(rideObj.getString("driver_id"));
                                bookingModel.setVehicle_number(rideObj.getString("vehicle_number"));
                                bookingModel.setVehicle_name(rideObj.getString("vehicle_name"));
                                bookingModel.setVehicle_make("NA");
                                bookingModel.setVehicle_image("NA");
                                bookingModel.setLicence_number("NA");
                                bookingModel.setIssue_state("NA");
                                bookingModel.setPhoneNumber("12345678");
                                bookingModel.setName(rideObj.getString("driver_name"));
                                bookingModel.setKids_in("1");
                                bookingModel.setKids_out("0");

                                driverList.add(bookingModel);
                                SharedPrefUtil.getInstance().saveString(SharedPrefUtil.LIVE_TRACKING_ONGOING,"0");
                                intent = new Intent(this, SubmitReview.class);
                                intent.putExtra("KeyNoti","noti");
                                intent.putExtra("driver_name",rideObj.getString("driver_name"));
                                intent.putExtra("ride_id",rideObj.getString("ride_id"));
                                intent.putExtra("rider_list", (Serializable) rideList);
                                intent.putExtra("driver_list", (Serializable) driverList);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(121);
                                startActivity(intent);
                                //   PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                //showSmallNotification(message,title,pendingIntent);
                                // sendNotificationnn(getApplicationContext(), intent, message, title);
                            }

                            //booking cancel by driver after accept
                            else if (type.equalsIgnoreCase("Booking Cancelled"))
                            {
                                Log.e(TAG, "type " + "inside");

                                intent = new Intent(this, ParentHome.class);
                                intent.putExtra("Key","fcm");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(121);
                                startActivity(intent);
                            }
                        }
                    }

                    else {

                        if (SharedPrefUtil.getInstance().getString(SharedPrefUtil.USERTYPE).
                                equalsIgnoreCase("parent"))
                        {

                            if (type.equalsIgnoreCase("Droped By Driver"))
                            {
                                Log.e(TAG, "type " + "inside");

                                String obj = data.get("data");

                                Log.e(TAG, "onMessageReceived: ArriverObje "+obj);

                                JSONObject rideObj=new JSONObject(obj);

                                Log.e(TAG, "onMessageReceived: ArriverObjeAfter "+rideObj);
                                ArrayList<RiderListModel> rideList = new ArrayList<>();
                                rideList.clear();

                                ArrayList<DriverBookingModel> driverList=new ArrayList<>();
                                driverList.clear();

                                RiderListModel riderListModel = new RiderListModel();
//                                riderListModel.setId(rideObj.getString("rider_id"));
                               /* riderListModel.setDropPriority(rideObj.getInt("priority_drop"));
                                riderListModel.setPickPriority(rideObj.getInt("priority_pick"));*/

                                riderListModel.setPickup(rideObj.getString("pickup_location_name"));
                                riderListModel.setPickLat(rideObj.getString("pickup_latitude"));
                                riderListModel.setPicklng(rideObj.getString("pickup_longitude"));
                                riderListModel.setDroplat(rideObj.getString("drop_latitude"));
                                riderListModel.setDroplng(rideObj.getString("drop_longitude"));
                                riderListModel.setDropup(rideObj.getString("dropoff_location_name"));

                                riderListModel.setEstDistance(rideObj.getString("distance"));
//                                riderListModel.setTime_eta(rideObj.getString("time"));
                                riderListModel.setEstPrice(rideObj.getString("Total_fare_deducted"));
//                                riderListModel.setBooking_id(rideObjBooking.getString("booking_id"));
                                riderListModel.setRide_id(rideObj.getString("ride_id"));
                                riderListModel.setRide_status(rideObj.getString("ridestatus"));
                                riderListModel.setBooking_id(rideObj.getString("booking_id"));
                                rideList.add(riderListModel);

                                DriverBookingModel bookingModel=new DriverBookingModel();

                                bookingModel.setRide_id(rideObj.getString("ride_id"));
                                bookingModel.setDriverId(rideObj.getString("driver_id"));
                                bookingModel.setVehicle_number(rideObj.getString("vehicle_number"));
                                bookingModel.setVehicle_name(rideObj.getString("vehicle_name"));
                                bookingModel.setVehicle_make("NA");
                                bookingModel.setVehicle_image("NA");
                                bookingModel.setLicence_number("NA");
                                bookingModel.setIssue_state("NA");
                                bookingModel.setPhoneNumber("12345678");
                                bookingModel.setName(rideObj.getString("driver_name"));
                                bookingModel.setKids_in("1");
                                bookingModel.setKids_out("0");
                                driverList.add(bookingModel);
                                SharedPrefUtil.getInstance().saveString(SharedPrefUtil.LIVE_TRACKING_ONGOING,"0");
                                intent = new Intent(this, SubmitReview.class);
                                intent.putExtra("KeyNoti","noti");
                                intent.putExtra("driver_name",rideObj.getString("driver_name"));
                                intent.putExtra("ride_id",rideObj.getString("ride_id"));
                                intent.putExtra("rider_list", (Serializable) rideList);
                                intent.putExtra("driver_list", (Serializable) driverList);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(121);
                                startActivity(intent);
                                //   PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                //showSmallNotification(message,title,pendingIntent);
                                // sendNotificationnn(getApplicationContext(), intent, message, title);
                            }
                        }

                    }



                   /* }
                    else
                    {
                        //background
                        Log.e(TAG, "onMessageReceived: Inside"+"inside" );
                        intent = new Intent(this, DriverHomeNotification.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(121);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        //showSmallNotification(message,title,pendingIntent);
                        sendNotificationnn(getApplicationContext(),intent,message,title);
                    }*/
               }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void sendNotificationnn(Context context,Intent intent,String message,String title)
    {
        Log.e("MyMessageCheck", "mmm hereee");
        Log.e("Message ", message);
        Log.e("title ", title);
        NotificationChannel mChannel = null;
        String NOTIFICATION_CHANNEL_ID = "1010";
        int NOTIFICATION_CODE = 10000;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(0)
                .setSubText(title)
                .setContentText(message);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            mChannel.setDescription(message);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            if (notificationManager != null)
            {
            notificationManager.createNotificationChannel(mChannel);
            }
        }
        else
         {
            builder.setContentTitle(context.getString(R.string.app_name))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setVibrate(new long[]{100, 250})
                    .setSound(uri)
                    .setContentText(message)
                    .setContentTitle(title)
                    .setLights(Color.GREEN, 500, 5000)
                    .setGroupSummary(true)
                    .setAutoCancel(true);
        }

        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_ALL;
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManager.notify(m, notification);
    }

    private void showSmallNotification(String message, String title, PendingIntent pendingIntent)
    {
        String channelId = getString(R.string.default_notification_channel_id);
        String channelName = getString(R.string.default_notification_channel_name);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            // create android channel
            NotificationChannel androidChannel = new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_DEFAULT);
            // Sets whether notifications posted to this channel should display notification lights
            androidChannel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            androidChannel.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            notificationManager.createNotificationChannel(androidChannel);

        }

        CharSequence cs = message;


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle("Taxi Nanny");
            builder = new Notification.Builder(getApplicationContext(), channelId)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(getNotificationIcon())
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent)
                    .setStyle(new Notification.BigTextStyle().bigText(cs))
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSound(defaultSoundUri)
                    .setWhen(0);
            notificationManager.notify(0, builder.build());

        } else {
            builder = new Notification.Builder(getApplicationContext())
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(getNotificationIcon())
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent)
                    .setStyle(new Notification.BigTextStyle().bigText(cs))
                    .setAutoCancel(true)
                    .setWhen(0);
            notificationManager.notify(0, builder.build());
        }

    }

    private void sendNotification(String message,String title)
    {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setSound(defaultSoundUri);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private static int getNotificationIcon()
    {
        boolean whiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        return whiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
    }

}
