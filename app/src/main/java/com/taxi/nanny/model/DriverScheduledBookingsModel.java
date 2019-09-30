package com.taxi.nanny.model;

import java.io.Serializable;
import java.util.ArrayList;

public class DriverScheduledBookingsModel implements Serializable {

    String id,date;

    ArrayList<ScheduleBookingsModel> ScheduleBookingsModelList;

    public ArrayList<ScheduleBookingsModel> getScheduleBookingsModelList() {
        return ScheduleBookingsModelList;
    }

    public void setScheduleBookingsModelList(ArrayList<ScheduleBookingsModel> scheduleBookingsModelList) {
        ScheduleBookingsModelList = scheduleBookingsModelList;
    }

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
