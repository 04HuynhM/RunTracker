package com.runtracker.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class Run {

    private ArrayList<SingleLocation> locations;
    private String user;
    private String startTime;
    private int timeInSeconds;
    private int run_id;

    public Run (String user, String startTime) {
        this.user = user;
        this.startTime = startTime;
    }

    public Run(int run_id, String user, ArrayList<SingleLocation> locations, String startTime) {
        this.locations = locations;
        this.user = user;
        this.startTime = startTime;
        this.run_id = run_id;
    }

    public int getRun_id() { return run_id; }

    public ArrayList<SingleLocation> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<SingleLocation> locations) {
        this.locations = locations;
    }

    public String getUser() {
        return user;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getTimeInSeconds() { return timeInSeconds; }

    public void setTimeInSeconds(int timeInSeconds) { this.timeInSeconds = timeInSeconds; }

}
