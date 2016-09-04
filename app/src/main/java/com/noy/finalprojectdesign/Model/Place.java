package com.noy.finalprojectdesign.Model;

/**
 * Created by noy on 21/05/2016.
 */
public class Place {
    private String placeId;
    private String name;
    private String address;
    private String phoneNumber;
    private double distance;
    private String[] openHoursText;
    private int chosenTypeId;
    private String chosenType;
    private String photo;


    public Place(String placeId, String name, String address,
                 String phoneNumber, String[] openHoursText,
                 int chosenTypeId, String chosenType, String photo, double distance) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.openHoursText = openHoursText;
        this.chosenTypeId = chosenTypeId;
        this.chosenType = chosenType;
        this.photo = photo;
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String[] getOpenHours() {
        return openHoursText;
    }

    public String getOpenHoursText() {
        String text = "";
        if (openHoursText != null) {
            for (int i=0;i<openHoursText.length;i++) {
                text = openHoursText[i] + "\n";
            }
        }
        return text;
    }

    public void setOpenHours(String[] openHoursText) {
        this.openHoursText = openHoursText;
    }

    public int getChosenTypeId() {
        return chosenTypeId;
    }

    public void setChosenTypeId(int chosenTypeId) {
        this.chosenTypeId = chosenTypeId;
    }
    public String getChosenType() {
        return chosenType;
    }

    public void setChosenType(String chosenType) {
        this.chosenType = chosenType;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
