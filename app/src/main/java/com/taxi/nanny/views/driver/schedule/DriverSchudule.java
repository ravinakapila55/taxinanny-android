package com.taxi.nanny.views.driver.schedule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.applandeo.materialcalendarview.utils.DateUtils;
import com.taxi.nanny.R;
import com.taxi.nanny.model.DriverScheduleModel;
import com.taxi.nanny.model.DriverScheduledBookingsModel;
import com.taxi.nanny.model.ScheduleBookingRiderDetailsModel;
import com.taxi.nanny.model.ScheduleBookingsModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DriverSchudule extends BaseActivity implements RetrofitResponse {

    String token = "";
    SharedPrefUtil sharedPrefUtil;

    public static final String RESULT = "result";
    public static final String EVENT = "event";
    private static final int ADD_NOTE = 44;

    private CalendarView mCalendarView;
    private List<EventDay> mEventDays = new ArrayList<>();

    private int mYear, mMonth, mDay;


    List<EventDay> events;
    List<EventDay> events1;

    ArrayList<DriverScheduleModel> scheduleList=new ArrayList<>();
    ArrayList<String> scheduleBookingDateList=new ArrayList<>();

    ArrayList<DriverScheduledBookingsModel> list=new ArrayList<>();



    @Override
    protected int getContentId()
    {
        return R.layout.driver_schedule;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sharedPrefUtil = SharedPrefUtil.getInstance();
        token = sharedPrefUtil.getString(Constant.TOKEN, "");
        setTab();

        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
//        addData();
//        setCalendar();

    }

    @Override
    protected void onResume() {
        super.onResume();
        callAllBookings();
    }

    public void callAllBookings()
    {
        new RetrofitService(this, this, Constant.API_SCHEDULED_BOOKINGS ,
                105, 1,"1").callService(true);
    }


    ArrayList<Date> dateList=new ArrayList<>();

    private void setAppointmentDatesEvent()
    {
        //2019-08-bb 08:54:27
        events = new ArrayList<>();
        dateList.clear();

        Log.e("ScheduleListData ",list.size()+"");
        Log.e("listtttt ",list.size()+"");

        for (int i = 0; i < list.size(); i++)
        {
            Log.e("ScheduleListDataAAAA ",list.get(i).getDate()+"");
            String d1 = list.get(i).getDate();
            Log.e("D1  ",d1);
//            int date = Integer.parseInt(d1.substring(8));
//            Log.e("date212", "getSelectedDays:" + date);

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date11 = null;
            try
            {
                date11 = formatter.parse(d1);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            dateList.add(date11);

            Calendar calendar= DateUtils.getCalendar();
            calendar.setTime(date11);
            Log.e("EventListBeforeSize ",events.size()+"");
            events.add(new EventDay(calendar, R.drawable.note));
            Log.e("EventListAfterSize ",events.size()+"");
            Log.e("calendar ",calendar.getTime()+"");
        }

        Log.e("DateList ",dateList.size()+"");


        mCalendarView.setEvents(events);

        mCalendarView.setOnDayClickListener(new OnDayClickListener()
        {
            @SuppressLint("RestrictedApi")

            @Override
            public void onDayClick(EventDay eventDay)
            {
                for (int i = 0; i < events.size(); i++)
                {
                    if (eventDay.getImageDrawable() != null && /*i != events.size() - 1 &&*/
                            eventDay.getImageDrawable() == events.get(i).getImageDrawable())  //last index is of current date
                    {
                        Log.e("Date_clicked", "onDayClick: " + events.get(i).getCalendar().getTime());

                       Intent intent=new Intent(DriverSchudule.this,ScheduledBookings.class);
                       intent.putExtra("list",(Serializable)list.get(i).getScheduleBookingsModelList());
                       startActivity(intent);
                    }
                }
            }
        });
    }


    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 105:

                Log.e("ResponseSchedule ",response);

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        list.clear();

                        JSONArray Array = jsonObject.getJSONArray("schedule");

                        if (Array.length() > 0)
                        {
                            for (int i = 0; i < Array.length(); i++)
                            {

                                JSONObject object = Array.getJSONObject(i);

                                DriverScheduledBookingsModel driverScheduledBookingsModel=new DriverScheduledBookingsModel();

                                driverScheduledBookingsModel.setDate(object.getString("booking_date"));

                                JSONObject booking=object.getJSONObject("booking");

                                ArrayList<ScheduleBookingsModel> ScheduleBookingsModelList=new ArrayList<>();


                                ScheduleBookingsModel scheduleBookingsModel=new ScheduleBookingsModel();

                                scheduleBookingsModel.setBooking_id(booking.getString("booking_id"));
                                scheduleBookingsModel.setParent_name(booking.getString("parent_name"));
                                scheduleBookingsModel.setBooking_type(booking.getString("booking_type"));
                                scheduleBookingsModel.setBooking_time(booking.getString("time_of_ride"));
                                scheduleBookingsModel.setParent_image(booking.getString("parent_image"));
                                ScheduleBookingsModelList.add(scheduleBookingsModel);

                               JSONArray array=booking.getJSONArray("ride_history");

                               ArrayList<ScheduleBookingRiderDetailsModel> ScheduleBookingRiderDetailsModellist=new ArrayList<>();

                                for (int k = 0; k <array.length() ; k++) {
                                    JSONObject jsonObject1=array.getJSONObject(k);

                                    ScheduleBookingRiderDetailsModel scheduleBookingRiderDetailsModel=new ScheduleBookingRiderDetailsModel();

                                    scheduleBookingRiderDetailsModel.setRider_id(jsonObject1.getString("child_id"));
                                    scheduleBookingRiderDetailsModel.setName(jsonObject1.getString("Name"));
                                    scheduleBookingRiderDetailsModel.setImage(jsonObject1.getString("image"));
                                    scheduleBookingRiderDetailsModel.setPick_loc(jsonObject1.getString("pickup_location"));
                                    scheduleBookingRiderDetailsModel.setPick_lat(jsonObject1.getString("pickup_latitude"));
                                    scheduleBookingRiderDetailsModel.setPick_lng(jsonObject1.getString("pickup_longitude"));
                                    scheduleBookingRiderDetailsModel.setPick_priority(jsonObject1.getString("priority_pick"));
                                    scheduleBookingRiderDetailsModel.setDest_loc(jsonObject1.getString("dropup_location"));
                                    scheduleBookingRiderDetailsModel.setDrop_lat(jsonObject1.getString("drop_latitude"));
                                    scheduleBookingRiderDetailsModel.setDrop_lng(jsonObject1.getString("drop_longitude"));
                                    scheduleBookingRiderDetailsModel.setDrop_priroty(jsonObject1.getString("priority_drop"));
                                    scheduleBookingRiderDetailsModel.setRide_status(jsonObject1.getString("ride_status"));

                                    ScheduleBookingRiderDetailsModellist.add(scheduleBookingRiderDetailsModel);
                                }

                                scheduleBookingsModel.setScheduleBookingRiderDetailsModellist(ScheduleBookingRiderDetailsModellist);

                                driverScheduledBookingsModel.setScheduleBookingsModelList(ScheduleBookingsModelList);
                                list.add(driverScheduledBookingsModel);

                                if (list.size()>0)
                                {
                                    setCalendar();
                                }
                            }
                        }
                        else
                        {
                            Toast.makeText(this, "No Scheduled Bookings Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(this, "No Scheduled Bookings Found", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                break;
        }
    }

    public void setCalendar()
    {
        mCalendarView.setMinimumDate(DateUtils.getCalendar());
        setAppointmentDatesEvent();
    }
}
