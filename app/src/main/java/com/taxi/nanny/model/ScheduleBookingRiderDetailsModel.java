package com.taxi.nanny.model;

import java.io.Serializable;

public class ScheduleBookingRiderDetailsModel implements Serializable {

    String rider_id;
    String name;
    String created;
    String pick_loc;
    String dest_loc;
    String pick_lat;
    String pick_lng;
    String drop_lat;
    String drop_lng;
    String image;
    String pick_priority;
    String ride_status;

    public String getRide_status() {
        return ride_status;
    }

    public void setRide_status(String ride_status) {
        this.ride_status = ride_status;
    }

    public String getPick_priority() {
        return pick_priority;
    }

    public void setPick_priority(String pick_priority) {
        this.pick_priority = pick_priority;
    }

    public String getDrop_priroty() {
        return drop_priroty;
    }

    public void setDrop_priroty(String drop_priroty) {
        this.drop_priroty = drop_priroty;
    }

    String drop_priroty;

    public String getRider_id() {
        return rider_id;
    }

    public void setRider_id(String rider_id) {
        this.rider_id = rider_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getPick_loc() {
        return pick_loc;
    }

    public void setPick_loc(String pick_loc) {
        this.pick_loc = pick_loc;
    }

    public String getDest_loc() {
        return dest_loc;
    }

    public void setDest_loc(String dest_loc) {
        this.dest_loc = dest_loc;
    }

    public String getPick_lat() {
        return pick_lat;
    }

    public void setPick_lat(String pick_lat) {
        this.pick_lat = pick_lat;
    }

    public String getPick_lng() {
        return pick_lng;
    }

    public void setPick_lng(String pick_lng) {
        this.pick_lng = pick_lng;
    }

    public String getDrop_lat() {
        return drop_lat;
    }

    public void setDrop_lat(String drop_lat) {
        this.drop_lat = drop_lat;
    }

    public String getDrop_lng() {
        return drop_lng;
    }

    public void setDrop_lng(String drop_lng) {
        this.drop_lng = drop_lng;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
