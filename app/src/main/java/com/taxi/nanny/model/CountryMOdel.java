package com.taxi.nanny.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CountryMOdel implements Serializable {

    String cid,cname,numcode,phncode,iso3;

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getNumcode() {
        return numcode;
    }

    public void setNumcode(String numcode) {
        this.numcode = numcode;
    }

    public String getPhncode() {
        return phncode;
    }

    public void setPhncode(String phncode) {
        this.phncode = phncode;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
