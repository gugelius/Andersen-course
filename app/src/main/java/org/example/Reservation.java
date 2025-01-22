package org.example;

class Reservation {
    private int id;
    private String userName;
    private int spaceId;
    private String date;
    private String startTime;
    private String endTime;
    public Reservation(int id, String userName, int spaceId, String date, String startTime, String endTime) {
        this.id = id;
        this.userName = userName;
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
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
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
        return "ID: " + id + ", User: " + userName + ", Space ID: " + spaceId + ", Date: " + date + ", Start Time: " + startTime + ", End Time: " + endTime;
    }
}