package com.taxi.nanny.model;


import java.io.Serializable;
import java.util.ArrayList;


public class RiderListModel implements Serializable
{

// @SerializedName("id")
  String id;
  String fav_loc_id;
    String fav_loc;
    String fav_nick_name;
    String fav_description;
    String time_eta;
    String phone_number;
    String kids_in;
    String kids_out;
    String time_booking;
    String booking_date;

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    String pick_instructions="",drop_instructions="";

    boolean signIn=false,signOut=false;

    public boolean isSignIn() {
        return signIn;
    }

    public void setSignIn(boolean signIn) {
        this.signIn = signIn;
    }

    public boolean isSignOut() {
        return signOut;
    }

    public void setSignOut(boolean signOut) {
        this.signOut = signOut;
    }

    public String getPick_instructions() {
        return pick_instructions;
    }

    public void setPick_instructions(String pick_instructions) {
        this.pick_instructions = pick_instructions;
    }

    public String getDrop_instructions() {
        return drop_instructions;
    }

    public void setDrop_instructions(String drop_instructions) {
        this.drop_instructions = drop_instructions;
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

    public String getTime_booking() {
        return time_booking;
    }

    public void setTime_booking(String time_booking) {
        this.time_booking = time_booking;
    }

    ArrayList<RecurringDaysModel> rideDaysList;

    public ArrayList<RecurringDaysModel> getRideDaysList() {
        return rideDaysList;
    }

    public void setRideDaysList(ArrayList<RecurringDaysModel> rideDaysList) {
        this.rideDaysList = rideDaysList;
    }

    public String getRide_status() {
        return ride_status;
    }

    public void setRide_status(String ride_status) {
        this.ride_status = ride_status;
    }

    String ride_status;

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    String ride_id;

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    String booking_id;

    public String getTime_eta() {
        return time_eta;
    }

    public void setTime_eta(String time_eta) {
        this.time_eta = time_eta;
    }

    public String getEstDistance() {
        return estDistance;
    }

    public void setEstDistance(String estDistance)
    {
        this.estDistance = estDistance;
    }

    public String getEstPrice() {
        return estPrice;
    }

    public void setEstPrice(String estPrice) {
        this.estPrice = estPrice;
    }

    String fav_latt;
    String estDistance="";
    String estPrice="";
    String booking_type="";
    String enddate="";

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getBooking_type() {
        return booking_type;
    }

    public void setBooking_type(String booking_type) {
        this.booking_type = booking_type;
    }

    public String getFav_loc_id() {
        return fav_loc_id;
    }

    public void setFav_loc_id(String fav_loc_id) {
        this.fav_loc_id = fav_loc_id;
    }

    public String getFav_loc() {
        return fav_loc;
    }

    public void setFav_loc(String fav_loc) {
        this.fav_loc = fav_loc;
    }

    public String getFav_nick_name() {
        return fav_nick_name;
    }

    public void setFav_nick_name(String fav_nick_name) {
        this.fav_nick_name = fav_nick_name;
    }

    public String getFav_description() {
        return fav_description;
    }

    public void setFav_description(String fav_description)
    {
        this.fav_description = fav_description;
    }

    public String getFav_latt() {
        return fav_latt;
    }

    public void setFav_latt(String fav_latt) {
        this.fav_latt = fav_latt;
    }

    public String getFav_lng() {
        return fav_lng;
    }

    public void setFav_lng(String fav_lng) {
        this.fav_lng = fav_lng;
    }

    String fav_lng;

// @SerializedName("first_name")
    String first_name;

// @SerializedName("last_name")
    String last_name;

// @SerializedName("gender")
    String gender;

// @SerializedName("dob")
    String dob;

// @SerializedName("image")
    String image;

// @SerializedName("need_booster")
    String need_booster;

// @SerializedName("sit_front_seat")
    String sit_front_seat;

    String pick_saved_id,drop_saved_id;

    int pickPriority,dropPriority;

    public int getPickPriority() {
        return pickPriority;
    }

    public void setPickPriority(int pickPriority) {
        this.pickPriority = pickPriority;
    }

    public int getDropPriority() {
        return dropPriority;
    }

    public void setDropPriority(int dropPriority) {
        this.dropPriority = dropPriority;
    }

    public String getPick_saved_id() {
        return pick_saved_id;
    }

    public void setPick_saved_id(String pick_saved_id) {
        this.pick_saved_id = pick_saved_id;
    }

    public String getDrop_saved_id() {
        return drop_saved_id;
    }

    public void setDrop_saved_id(String drop_saved_id) {
        this.drop_saved_id = drop_saved_id;
    }

    boolean viewSelected;


 String pickup="",dropup="",pickLat="",picklng="",droplat="",droplng="";

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getDropup() {
        return dropup;
    }

    public void setDropup(String dropup) {
        this.dropup = dropup;
    }

    public String getPickLat() {
        return pickLat;
    }

    public void setPickLat(String pickLat) {
        this.pickLat = pickLat;
    }

    public String getPicklng() {
        return picklng;
    }

    public void setPicklng(String picklng) {
        this.picklng = picklng;
    }

    public String getDroplat() {
        return droplat;
    }

    public void setDroplat(String droplat) {
        this.droplat = droplat;
    }

    public String getDroplng() {
        return droplng;
    }

    public void setDroplng(String droplng) {
        this.droplng = droplng;
    }

    boolean flag=false;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public boolean isViewSelected() {
        return viewSelected;
    }

    public void setViewSelected(boolean viewSelected) {
        this.viewSelected = viewSelected;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNeed_booster() {
        return need_booster;
    }

    public void setNeed_booster(String need_booster) {
        this.need_booster = need_booster;
    }

    public String getSit_front_seat() {
        return sit_front_seat;
    }

    public void setSit_front_seat(String sit_front_seat) {
        this.sit_front_seat = sit_front_seat;
    }
}
