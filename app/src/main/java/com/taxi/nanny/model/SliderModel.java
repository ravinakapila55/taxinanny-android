package com.taxi.nanny.model;

import java.io.Serializable;

public class SliderModel implements Serializable {

    String id,title,description;

    int image;

    public SliderModel(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
