package com.example.wayne.homesecurity;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class Upload{

    private String number;
    private String name;
    private String url;
    private String date;
    private String startTime;
    private String endTime;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Upload() {
    }

    public Upload(String url) {
        this.url= url;
    }

    public Upload(String name, String url) {
        this.name = name;
        this.url= url;
    }

    public Upload(String number, String name, String url, String date, String startTime, String endTime) {
        this.number = number;
        this.name = name;
        this.url= url;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getNumber() {
        return number;
    }

}