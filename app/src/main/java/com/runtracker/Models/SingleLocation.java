package com.runtracker.Models;

import java.sql.Timestamp;
import java.util.Date;

public class SingleLocation {

    private Long latitude;
    private Long longitude;
    private String timestamp;

    public SingleLocation(Long latitude, Long longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = createTimestamp();
    }

    public Long getLatitude() {
        return latitude;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    public Long getLongitude() {
        return longitude;
    }

    public void setLongitude(Long longitude) {
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
