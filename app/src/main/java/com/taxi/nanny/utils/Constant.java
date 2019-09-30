package com.taxi.nanny.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.google.android.gms.maps.model.LatLng;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Constant
{
  SharedPrefUtil sharedPrefUtil= SharedPrefUtil.getInstance();
  public final static String BUNDLE_USER_TYPE="USER_TYPE";
  // Registeration Section
  public final static int ACT_RESULT_DRIVING_LICENCE=132;
  public final static int ACT_RESULT_VEHICLE_INSURANCE=134;
  public final static int ACT_RESULT_VEHICLE_PERMIT=136;
  public final static int ACT_RESULT_VEHICLE_REGISTRATION=138;

  /*http://178.128.116.149*/
 public final static  String id=SharedPrefUtil.getInstance().getString(SharedPrefUtil.USER_ID,"");

 //159.65.145.230:9002/
//  public final static String BASE_URL="http://178.128.116.149/taxinanny/public/api/";
  public final static String BASE_URL="http://178.128.116.149/taxinanny1/public/api/";
  public final static String BASE_DRIVER_DOC="http://178.128.116.149/taxinanny1/public/";
  public final static String PLACES_API="AIzaSyAK5It4p1CiJ2gFzWRbfs24Cibo2QTcPRU";

//  public final static String SOCKET_URL="http://159.65.145.230:9002";
// public final static String SOCKET_URL="http://192.168.1.21:9002/";
//  public final static String SOCKET_URL="http://159.65.145.230:9002/";
  public final static String SOCKET_URL="http://165.22.215.99:9002/";
  public final static String API_ALLTRACKS="track";

  //  public final static String SOCKET_URL="http://159.65.145.230:9002/track/";
  public final static String SOCKET_UPDATE_LOCATION="updateLoc";


//http://159.65.145.230:9002/track
  public final static String API_REGISTER="register";
  public final static String API_LOGIN="login";
  public final static String API_ADD_LICENCE="addlicencedetail";
  public final static String API_VEHICLE_TYPE="getvehicletype";
  public final static String API_COUNTRY_LIST="countryList";
  public final static String API_LIST_OF_CHILDREN="riderList";
  public final static String API_ADD_RIDER="addRider";
  public final static String API_ADD_VEHICLE_DETAILS="addvehicledetaildriver";
  public final static String API_ADD_DRIVING_LICENCE="addlicencedetaildriver";
  public final static String API_ADD_DRIVING_INSURANCE="addinsurencedetaildriver";
  public final static String API_SAVE_RIDER_FAVOURITE_LOCATION="addFavouriteLocations";
  public final static String API_RIDER_FAVOURITE_LOCATION_LIST="FavouriteLocationList";
  public final static String API_GET_ESTIMATED_TIME_FARE="estimatedFareAndTime";
  public final static String API_DRIVER_STATUS="updateDriverStatus";
  public final static String API_UPDATE_DRIVER_LOCATION=BASE_URL+"updateDriverLocation";
  public final static String API_DELETE_RIDER="deleterider";
  public final static String API_CONFIRM_BOOKING="bookNow";
  public final static String API_ACCEPT_REQUEST="acceptRequest";
  public final static String API_REJECT_REQUEST="rejectRequest";
  public final static String API_ARRIVE_DRIVER="reachedAtDestination";
  public final static String API_START_TRIP="pickedUpByDriver";
  public final static String API_COMPLETE_TRIP="dropAtDestination";
  public final static String API_LOGOUT="logout";
  public final static String API_RIDE_HISTORY="ridehistoryparent";
  public final static String API_VIEW_PROFILE="userdetails";
  public final static String API_EDIT_PROFILE="updateuserdetails";
  public final static String API_EDIT_FAV_LOCATIONS="updatefavoratelocation";
  public final static String API_VEHICLE_REGISTRATION="addvehicledocumentdriver";
  public final static String API_VEHICLE_PERMIT="addvehiclepermitdriver";
  public final static String API_DRIVER_ALL_DOC="driveralldetail";
  public final static String API_DRIVER_VEHICLE_DETAIL="VehicleDetail";
  public final static String API_DRIVER_VEHICLE_EDIT_DETAIL="updatevehicleDetail";
  public final static String API_DRIVER_EARNING="tripDetail";
  public final static String API_DRIVER_ALL_RIDES="allridesdriver";
  public final static String API_PARENT_CARD_LIST="cards";
  public final static String API_DELETE_PAYMENT_CARD="card/delete";
  public final static String API_ADD_CARD="card/add";
  public final static String API_SUBMIT_RATING="save-rating";
  public final static String API_FARE_CHECK="farediscount";
  public final static String API_MAKE_PENDING_PAYMENT="pendingamount";
  public final static String API_ADD_EMERGENCY="add-emergency-contact";
  public final static String API_EMERGENCY_CONTACT_LIST="get-emergency-contact";
  public final static String API_CHANGE_PASSWORD="change-password";
  public final static String API_CHANGE_NOTI="change-push-settings";
  public final static String API_CHANGE_SMS="change-text-settings";
  public final static String API_SUPPORT_QUERY="add-support-query";
  public final static String API_SCHEDULED_BOOKINGS="recurring-list";
  public final static String API_FORGOT_PASSWORD="forgot-pass";
  public final static String API_CANCEL_RIDE="cancelride-driver";
  public final static String API_ACTUAL_DRIVER_LOCAION="update-triplatlong";
  public final static String API_PARENT_ONGOING_RIDES="ongoing-rides";
  public final static String API_ESTIMATED_FARE="estimated-Fare";

  public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 12 * 60 * 60 * 1000;
  public static final float GEOFENCE_RADIUS_IN_METERS = 1;

  public static final HashMap<String, LatLng> LANDMARKS = new HashMap<String, LatLng>();
  static {
    // San Francisco International Airport.
    LANDMARKS.put("Mohali", new LatLng(30.7046,76.7179));

    // Googleplex.
    LANDMARKS.put("8A", new LatLng(30.7036,76.6918));

    // Test
  }

//  public final static String TOKEN="eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjI4ZDQxYzdjMjdjN2Y0ZWU0OWI0NWRkZDhiNjhhNjBkZDZjYjc0MmEzY2I1MWE3NGFkN2NmNDViOTJlZDlhM2U0OGEyYTM1Yzk3MWJmNWYyIn0.eyJhdWQiOiIxIiwianRpIjoiMjhkNDFjN2MyN2M3ZjRlZTQ5YjQ1ZGRkOGI2OGE2MGRkNmNiNzQyYTNjYjUxYTc0YWQ3Y2Y0NWI5MmVkOWEzZTQ4YTJhMzVjOTcxYmY1ZjIiLCJpYXQiOjE1NTcyMjA5NDIsIm5iZiI6MTU1NzIyMDk0MiwiZXhwIjoxNTg4ODQzMzQyLCJzdWIiOiIzIiwic2NvcGVzIjpbXX0.nxXPutrp1B9GQYVU1dtGzYSQyWu21LrAUntZnLhDniTo0YpnS0pQ2yGHllDDiefPoXm5lPnAgk7qBZDTjdNAhrJeUCrIrWx9TcFytYPsvtvrwRx6v8mi5708iSMs8JkLOp6xQXYugjTTVqutYZp2by9MCtZ2zGBfsSxEAJ1PArRf4IR3k5Zsmd7na9u7yR0T3VJYMHxImPp8M3ZZLFqaytB4OM4HVC91hgqIF0NiVU8lZgRTiwBz3FaAWgIsPh5gHEQTxDWxgfpMJKkj1rcYXtzuzDhamDLjsyho_vjiZ0ZFpuuruAWknF2vxE97Bw_0trX6Q7S1fAHgwQLUYSCFqel3colfRPP88yEmq_PVXHFhaXGpxIKmodOsOGXNvaAoT3e5fw38zp-lwGhF2qJaqfaROvMySeKiJ1M-7XSxSQTdnqRCNBuABVWRsaAI3athztYQIVHirALV2sd12ZaQOqFAKzr86ALrcVX7BaQB_jm_r_rW33b2EZFZ-t8guSwq1dfv786r9-HXPqgag21hSJ0f0RAEYC3Q34AR3IWX9Hu-QhSbKgtg17Tx3CxHJxIjt-STcFTqDRQQbyPht8BRZZXeE_hGC5C74LUKayOxANxer16_OqXuHgq_IP1ztkhyqVfty0fe6wegXvgHd3p2LwAZdBNVPsMg7KC16x-6DsE";
  public final static String TOKEN="token";

  public static  void hideKeyboard(Context context, View view)
  {
    InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    if (view!=null)
    {
      inputManager.hideSoftInputFromWindow(view.getWindowToken(),
              InputMethodManager.HIDE_NOT_ALWAYS);
    }
  }

  public static  String getCurrentDate()
  {
    int cYear = Calendar.getInstance().get(Calendar.YEAR);
    int cMonth = Calendar.getInstance().get(Calendar.MONTH);
    int cDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    int currentMonth=cMonth+1;

    String currentDate= cYear+"-"+currentMonth+"-"+cDay;
    Log.e("setDatePicker: Current ",currentDate);

    return currentDate;
  }

  public static String campareLicencseDatesValidate(String expiry,String issue)
  {
    String result="";

    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    try {
      Date dd1=dateFormat.parse(expiry);
      Date dd2=dateFormat.parse(issue);

      if (dd1.equals(dd2))
      {
        result="equal";
      }
      else
      {
        result ="ok";
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return result;
  }

  public static String compareDates(String current,String selected)
  {
    String result="";
    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");

    try
    {
      Date dd1=dateFormat.parse(current);
      Date dd2=dateFormat.parse(selected);
      if (dd1.equals(dd2))
      {
        result="equal";
      }
      else if (dd1.before(dd2))
      {
        result="before";
      }
      else if (dd1.after(dd2))
      {
        result="after";
      }

      Log.e("compareDates: Result ",result);
    }
    catch
    (ParseException e)
    {
      e.printStackTrace();
    }

    return result;
  }

  public static long difference2Dates(String selected,String current)
  {
    long elapsedDays = 0;

    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    try {
      Date ddSelected=dateFormat.parse(selected);
      Date ddCurrent=dateFormat.parse(current);
      long difference=ddSelected.getTime()-ddCurrent.getTime();
      Log.e("DatesDifference ",difference+"");

      long secondsInMilli = 1000;
      long minutesInMilli = secondsInMilli * 60;
      long hoursInMilli = minutesInMilli * 60;
      long daysInMilli = hoursInMilli * 24;

       elapsedDays = difference / daysInMilli;
//      difference = difference % daysInMilli;

      Log.e("DatesDifference ",difference+"");

    }
    catch (ParseException e) {
      e.printStackTrace();
    }

    return elapsedDays;
  }

  public static int getCurrentDay()
  {
    int current=0;
    Calendar calendar = Calendar.getInstance();
    int day = calendar.get(Calendar.DAY_OF_WEEK);

    switch (day)
    {
      case Calendar.SUNDAY:
        current=1;
        break;

      case Calendar.MONDAY:
        current=2;
        break;

      case Calendar.TUESDAY:
        current=3;
        break;

        case Calendar.WEDNESDAY:
          current=4;
          break;

        case Calendar.THURSDAY:
          current=5;
          break;

        case Calendar.FRIDAY:
          current=6;
          break;

        case Calendar.SATURDAY:
          current=7;
          break;
    }

    return current;
  }

 /* public static String getDeviceToken()
  {
    String deviceToken="";

    if(deviceToken==null)
    {
      deviceToken = FirebaseInstanceId.getInstance().getToken();
      Log.e("token: ","c " + deviceToken);
    }
    return deviceToken;
  }
*/

  public static boolean checkActivation(Context context)
  {

    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

    List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);

    boolean isActivityFound = false;

    if (services.get(0).topActivity.getPackageName().toString().equalsIgnoreCase(context.getPackageName().toString())) {
      isActivityFound = true;
    }


    return isActivityFound;
  }

  public static  String EstDist="";;

  public static String  getEstDistTime(LatLng pick,LatLng drop)
  {
    Log.e("getEstDistTime: ",pick+"" );
    Log.e("getEstDistTime:DRop ",drop+"" );
      GoogleDirection.withServerKey(PLACES_API)
              .from(pick)
              .to(drop)
              .alternativeRoute(true)
              .avoid("toll")
              .execute(new DirectionCallback() {
                  @Override
                  public void onDirectionSuccess(Direction direction, String rawBody) {
                      if (direction.isOK()) {

                          Log.e( "onDirectionSuccess: " ,direction.getRouteList().toString());

                        EstDist= getEstDistTime(direction.getRouteList());
                        Log.e("EstDist ",EstDist+"" );

                      } else {
                        Log.e("else ","else" );
                      }
                  }

                  @Override
                  public void onDirectionFailure(Throwable t) {
                    Log.e("onDirectionFailure ",t.getMessage());
                  }
              });

      return EstDist;
  }

   private static String getEstDistTime(List<Route> routes)
    {
      Log.e("getEstDistTime ",routes.toString() );
        String distIme="";
        String routeDistance="";
        String routeDuration="";
        for (Route route : routes)
        {
            List<Leg> legs = route.getLegList();
            for (Leg leg : legs)
            {
                 routeDistance = leg.getDistance().getText();
                 routeDuration = leg.getDuration().getText();

            }
        }

        distIme=routeDistance+"-"+routeDuration;
      Log.e("distIme ",distIme.toString() );
        return distIme;
    }
}
