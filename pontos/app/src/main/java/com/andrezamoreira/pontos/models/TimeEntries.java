package com.andrezamoreira.pontos.models;

public class TimeEntries {
    private String id;
    private String description;
    private TimeInterval timeInterval;
    private String userID;

    public TimeEntries(){

    }

    public TimeEntries (String id, String description, TimeInterval timeInterval, String userID){
        this.id = id;
        this.description = description;
        this.timeInterval = timeInterval;
        this.userID = userID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TimeInterval getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(TimeInterval timeInterval) {
        this.timeInterval = timeInterval;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
