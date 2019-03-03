package com.runtracker.Models;

import java.util.ArrayList;

public class Run {

    private ArrayList<SingleLocation> locations;
    private String user_id;
    private String startDate;

    public Run (String user_id, ArrayList<SingleLocation> locations, String startDate) {
        this.user_id = user_id;
        this.locations = locations;
        this.startDate = startDate;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
