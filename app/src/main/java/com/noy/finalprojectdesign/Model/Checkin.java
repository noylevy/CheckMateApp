package com.noy.finalprojectdesign.Model;

import com.noy.finalprojectdesign.Utils;

/**
 * Created by Anna on 05-Mar-16.
 */
public class Checkin {

    private String type;
    private Utils.TimePart time;
    private int count;

    public Checkin(String type, Utils.TimePart time, int count) {
        this.type = type;
        this.time = time;
        this.count = count;
    }

    public Checkin(String type, Utils.TimePart time) {
        this.type = type;
        this.time = time;
        this.count = 1;
    }

    public String getType() {
        return type;
    }

    public Utils.TimePart getTime() {
        return time;
    }

    public int getCount() {
        return count;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTime(Utils.TimePart time) {
        this.time = time;
    }

/*    public void setCount(int count) {
        this.count = count;
    }*/
    public void increaseCount() {
        this.count++;
    }

}
