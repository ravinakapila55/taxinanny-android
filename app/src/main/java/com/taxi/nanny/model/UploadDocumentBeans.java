package com.taxi.nanny.model;

import java.util.ArrayList;

public class UploadDocumentBeans  {

    String title;
    boolean status;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    public ArrayList<UploadDocumentType> getUploadDocumentTypes() {
        return uploadDocumentTypes;
    }

    public void setUploadDocumentTypes(ArrayList<UploadDocumentType> uploadDocumentTypes) {
        this.uploadDocumentTypes = uploadDocumentTypes;
    }

    ArrayList<UploadDocumentType> uploadDocumentTypes;
    public void setUploadDocumentType(ArrayList<UploadDocumentType>  uploadDocumentType) {
        this.uploadDocumentTypes=uploadDocumentType;
    }


}
