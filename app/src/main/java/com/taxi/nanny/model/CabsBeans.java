package com.taxi.nanny.model;

import java.io.Serializable;

public class CabsBeans implements Serializable {

    String id;
    String cabName;
    int image;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    String Status;

    boolean isAvailable;

    public CabsBeans(String id, String cabName,String status, int image,boolean isAvailable) {
        this.id = id;
        this.cabName = cabName;
        this.Status=status;
        this.image=image;
        this.isAvailable = isAvailable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCabName() {
        return cabName;
    }

    public void setCabName(String cabName) {
        this.cabName = cabName;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
