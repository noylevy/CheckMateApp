package com.noy.finalprojectdesign.Model;

/**
 * Created by adi on 12-Jun-16.
 */
public class EmotionsPlace {
    protected int googleType;
    protected String placeId;

    public EmotionsPlace(int googleType, String placeId) {
        this.googleType = googleType;
        this.placeId = placeId;
    }

    public int getGoogleType() {
        return googleType;
    }

    public void setGoogleType(int googleType) {
        this.googleType = googleType;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}

