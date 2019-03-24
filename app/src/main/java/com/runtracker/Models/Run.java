package com.runtracker.Models;

import java.util.ArrayList;

public class Run {

    private ArrayList<SingleLocation> locations;
    private String user_id;
    private String startTime;
    private int timeInSeconds;

    public Run (String user_id, String startTime) {
        this.user_id = user_id;
        this.startTime = startTime;
    }

    public ArrayList<SingleLocation> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<SingleLocation> locations) {
        this.locations = locations;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getTimeInSeconds() { return timeInSeconds; }

    public void setTimeInSeconds(int timeInSeconds) { this.timeInSeconds = timeInSeconds; }

}
