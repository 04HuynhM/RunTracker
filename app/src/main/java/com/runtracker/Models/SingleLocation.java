package com.runtracker.Models;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class SingleLocation implements Serializable {

    private Double latitude;
    private Double longitude;
    private String timestamp;

    public SingleLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = createTimestamp();
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    private String createTimestamp() {
        Date date = new Date();
        long time = date.getTime();
        Timestamp timestamp = new Timestamp(time);

        return timestamp.toString();
    }
}
