package com.taxi.nanny.model;

import com.google.gson.annotations.SerializedName;

public class SelectVehicleTypeBeans {

    @SerializedName("id")
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPassengerSeat() {
        return passengerSeat;
    }

    public void setPassengerSeat(String passengerSeat) {
        this.passengerSeat = passengerSeat;
    }

    public String getImgSelected() {
        return imgSelected;
    }

    public void setImgSelected(String imgSelected) {
        this.imgSelected = imgSelected;
    }

    public String getImgUnselected() {
        return imgUnselected;
    }

    public void setImgUnselected(String imgUnselected) {
        this.imgUnselected = imgUnselected;
    }

    @SerializedName("type")
    String name;
    @SerializedName("description")
    String description;
    @SerializedName("capacity")
    String passengerSeat;
    @SerializedName("img_url")
    String imgSelected;
    @SerializedName("img_url2")
    String  imgUnselected;


    boolean flag;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
