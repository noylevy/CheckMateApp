package com.noy.finalprojectdesign.Model;

/**
 * Created by noy on 21/05/2016.
 */
public class Place {
    private int id;
    private String name;
    private String details;
    private String address;
    private String pic;

    public Place(int id, String name, String details, String address, String pic) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.address = address;
        this.pic = pic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
