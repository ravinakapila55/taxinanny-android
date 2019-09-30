package com.taxi.nanny.model;

import java.io.Serializable;

public class EarningModel implements Serializable {

    String booking_id,amount,time,parent_name,parent_image;

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
}
