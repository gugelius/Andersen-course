package org.example.entity;

public class Reservation {
    private int id;
    private int user_id;
    private int spaceId;
    private String date;
    private String startTime;
    private String endTime;
    public Reservation(int id, int user_id, int spaceId, String date, String startTime, String endTime) {
        this.id = id;
        this.user_id = user_id;
        this.spaceId = spaceId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getUserId() {
        return user_id;
    }
    public void setUserId(int user_id) {
        this.user_id = user_id;
    }
    public int getSpaceId() {
        return spaceId;
    }
    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    @Override public String toString() {
        return "ID: " + id + ", User_id: " + user_id + ", Space ID: " + spaceId + ", Date: " + date + ", Start Time: " + startTime + ", End Time: " + endTime;
    }
}