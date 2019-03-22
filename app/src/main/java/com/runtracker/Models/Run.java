package com.runtracker.Models;

import java.util.ArrayList;

public class Run {

    private ArrayList<SingleLocation> locations;
    private String user_id;
    private String startTime;

    public Run (String user_id, ArrayList<SingleLocation> locations, String startTime) {
        this.user_id = user_id;
        this.locations = locations;
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

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void addLocation(SingleLocation location) {
        locations.add(location);
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
