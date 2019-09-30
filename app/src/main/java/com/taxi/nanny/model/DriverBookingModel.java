package com.taxi.nanny.model;

import java.io.Serializable;

public class DriverBookingModel implements Serializable {

    String driverId;
    String name;
    String email;
    String phoneNumber;
    String image;
    String vehicle_name;
    String kids_in;
    String kids_out;
    String booking_time;
    String vehicle_image,vehicle_make,licence_number,issue_state;

    public String getVehicle_image() {
        return vehicle_image;
    }

    public void setVehicle_image(String vehicle_image) {
        this.vehicle_image = vehicle_image;
    }

    public String getVehicle_make() {
        return vehicle_make;
    }

    public void setVehicle_make(String vehicle_make) {
        this.vehicle_make = vehicle_make;
    }

    public String getLicence_number() {
        return licence_number;
    }

    public void setLicence_number(String licence_number) {
        this.licence_number = licence_number;
    }

    public String getIssue_state() {
        return issue_state;
    }

    public void setIssue_state(String issue_state) {
        this.issue_state = issue_state;
    }

    public String getKids_in() {
        return kids_in;
    }

    public void setKids_in(String kids_in) {
        this.kids_in = kids_in;
    }

    public String getKids_out() {
        return kids_out;
    }

    public void setKids_out(String kids_out) {
        this.kids_out = kids_out;
    }

    public String getBooking_time() {
        return booking_time;
    }

    public void setBooking_time(String booking_time) {
        this.booking_time = booking_time;
    }

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    String ride_id;

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    String vehicle_number;

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
