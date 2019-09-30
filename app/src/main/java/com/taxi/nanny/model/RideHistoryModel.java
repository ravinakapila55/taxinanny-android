package com.taxi.nanny.model;

import java.io.Serializable;
import java.util.ArrayList;

public class RideHistoryModel implements Serializable {

    String bookingId;
    String Amount;
    String driverName;
    String BookingType;
    String bookingDays;
    String date;
    String rideArrayLength;

    public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }

    String parent_name;

    ArrayList<RiderListModel> riderList;

    public ArrayList<RiderListModel> getRiderList() {
        return riderList;
    }

    public void setRiderList(ArrayList<RiderListModel> riderList) {
        this.riderList = riderList;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getBookingType() {
        return BookingType;
    }

    public void setBookingType(String bookingType) {
        BookingType = bookingType;
    }

    public String getBookingDays() {
        return bookingDays;
    }

    public void setBookingDays(String bookingDays) {
        this.bookingDays = bookingDays;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRideArrayLength() {
        return rideArrayLength;
    }

    public void setRideArrayLength(String rideArrayLength) {
        this.rideArrayLength = rideArrayLength;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
}
