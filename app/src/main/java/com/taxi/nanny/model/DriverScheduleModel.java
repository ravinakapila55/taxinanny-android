package com.taxi.nanny.model;

import java.io.Serializable;

public class DriverScheduleModel implements Serializable {

    String id,date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
