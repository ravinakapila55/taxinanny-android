package com.taxi.nanny.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ScheduleBookingsModel implements Serializable {

    String parent_name,parent_image,booking_id,booking_type,booking_time;

    ArrayList<ScheduleBookingRiderDetailsModel> ScheduleBookingRiderDetailsModellist;

    public ArrayList<ScheduleBookingRiderDetailsModel> getScheduleBookingRiderDetailsModellist() {
        return ScheduleBookingRiderDetailsModellist;
    }

    public void setScheduleBookingRiderDetailsModellist(ArrayList<ScheduleBookingRiderDetailsModel> scheduleBookingRiderDetailsModellist) {
        ScheduleBookingRiderDetailsModellist = scheduleBookingRiderDetailsModellist;
    }

    public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }

    public String getParent_image() {
        return parent_image;
    }

    public void setParent_image(String parent_image) {
        this.parent_image = parent_image;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getBooking_type() {
        return booking_type;
    }

    public void setBooking_type(String booking_type) {
        this.booking_type = booking_type;
    }

    public String getBooking_time() {
        return booking_time;
    }

    public void setBooking_time(String booking_time) {
        this.booking_time = booking_time;
    }
}
