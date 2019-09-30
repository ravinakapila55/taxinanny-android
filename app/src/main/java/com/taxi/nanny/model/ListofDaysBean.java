package com.taxi.nanny.model;

import java.io.Serializable;

public class ListofDaysBean implements Serializable {

    String id,day;

    boolean flag;
    boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public ListofDaysBean(String id, String day, boolean flag,boolean enable) {
        this.id = id;
        this.day = day;
        this.flag = flag;
        this.enable=enable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
