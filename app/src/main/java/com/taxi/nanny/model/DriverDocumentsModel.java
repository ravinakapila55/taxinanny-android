package com.taxi.nanny.model;

import java.io.Serializable;

public class DriverDocumentsModel implements Serializable {

    String licence_id;
    String licence_full_name;
    String lCountry;
    String lIssuedOn;
    String licence_no;
    String lBirthday;
    String lExpiry_date;
    String laddress1;
    String laddress2;
    String lcity;
    String lstate;
    String lzipcode;

    public String getlImage() {
        return lImage;
    }

    public void setlImage(String lImage) {
        this.lImage = lImage;
    }

    String lImage;

    String insuranceId,ICompnayName,IPolicyNumber,InsuranceIssuedDate,InsuranceExpiryDate;

    String permitID,permit_doc;

    String vehicleRegId,vehicleReg_file;

    String vehicleId;

    public String getLicence_id() {
        return licence_id;
    }

    public void setLicence_id(String licence_id) {
        this.licence_id = licence_id;
    }

    public String getLicence_full_name() {
        return licence_full_name;
    }

    public void setLicence_full_name(String licence_full_name) {
        this.licence_full_name = licence_full_name;
    }

    public String getlCountry() {
        return lCountry;
    }

    public void setlCountry(String lCountry) {
        this.lCountry = lCountry;
    }

    public String getlIssuedOn() {
        return lIssuedOn;
    }

    public void setlIssuedOn(String lIssuedOn) {
        this.lIssuedOn = lIssuedOn;
    }

    public String getLicence_no() {
        return licence_no;
    }

    public void setLicence_no(String licence_no) {
        this.licence_no = licence_no;
    }

    public String getlBirthday() {
        return lBirthday;
    }

    public void setlBirthday(String lBirthday) {
        this.lBirthday = lBirthday;
    }

    public String getlExpiry_date() {
        return lExpiry_date;
    }

    public void setlExpiry_date(String lExpiry_date) {
        this.lExpiry_date = lExpiry_date;
    }

    public String getLaddress1() {
        return laddress1;
    }

    public void setLaddress1(String laddress1) {
        this.laddress1 = laddress1;
    }

    public String getLaddress2() {
        return laddress2;
    }

    public void setLaddress2(String laddress2) {
        this.laddress2 = laddress2;
    }

    public String getLcity() {
        return lcity;
    }

    public void setLcity(String lcity) {
        this.lcity = lcity;
    }

    public String getLstate() {
        return lstate;
    }

    public void setLstate(String lstate) {
        this.lstate = lstate;
    }

    public String getLzipcode() {
        return lzipcode;
    }

    public void setLzipcode(String lzipcode) {
        this.lzipcode = lzipcode;
    }

    public String getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(String insuranceId) {
        this.insuranceId = insuranceId;
    }

    public String getICompnayName() {
        return ICompnayName;
    }

    public void setICompnayName(String ICompnayName) {
        this.ICompnayName = ICompnayName;
    }

    public String getIPolicyNumber() {
        return IPolicyNumber;
    }

    public void setIPolicyNumber(String IPolicyNumber) {
        this.IPolicyNumber = IPolicyNumber;
    }

    public String getInsuranceIssuedDate() {
        return InsuranceIssuedDate;
    }

    public void setInsuranceIssuedDate(String insuranceIssuedDate) {
        InsuranceIssuedDate = insuranceIssuedDate;
    }

    public String getInsuranceExpiryDate() {
        return InsuranceExpiryDate;
    }

    public void setInsuranceExpiryDate(String insuranceExpiryDate) {
        InsuranceExpiryDate = insuranceExpiryDate;
    }

    public String getPermitID() {
        return permitID;
    }

    public void setPermitID(String permitID) {
        this.permitID = permitID;
    }

    public String getPermit_doc() {
        return permit_doc;
    }

    public void setPermit_doc(String permit_doc) {
        this.permit_doc = permit_doc;
    }

    public String getVehicleRegId() {
        return vehicleRegId;
    }

    public void setVehicleRegId(String vehicleRegId) {
        this.vehicleRegId = vehicleRegId;
    }

    public String getVehicleReg_file() {
        return vehicleReg_file;
    }

    public void setVehicleReg_file(String vehicleReg_file) {
        this.vehicleReg_file = vehicleReg_file;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }
}
