package com.noy.finalprojectdesign.Model;

/**
 * Created by ankol on 10-Aug-16.
 */
public class User {
    private String userId;
    private String tokenString;

    public User(String userId, String tokenString) {
        this.userId = userId;
        this.tokenString = tokenString;
    }

    public String getTokenString() {
        return tokenString;
    }

    public void setTokenString(String tokenString) {
        this.tokenString = tokenString;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
