package com.example.nepaltourismmanagement.models;

public class Booking {
    private String id;
    private String trekId;
    private String touristId;
    private String trekName;
    private String touristName;
    private String bookingDate;
    private int duration;
    private double price;
    private String status;
    private String guideId;
    private boolean highRiskAcknowledged;

    public Booking() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrekId() {
        return trekId;
    }

    public void setTrekId(String trekId) {
        this.trekId = trekId;
    }

    public String getTouristId() {
        return touristId;
    }

    public void setTouristId(String touristId) {
        this.touristId = touristId;
    }

    public String getTrekName() {
        return trekName;
    }

    public void setTrekName(String trekName) {
        this.trekName = trekName;
    }

    public String getTouristName() {
        return touristName;
    }

    public void setTouristName(String touristName) {
        this.touristName = touristName;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGuideId() {
        return guideId;
    }

    public void setGuideId(String guideId) {
        this.guideId = guideId;
    }

    public boolean isHighRiskAcknowledged() {
        return highRiskAcknowledged;
    }

    public void setHighRiskAcknowledged(boolean highRiskAcknowledged) {
        this.highRiskAcknowledged = highRiskAcknowledged;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", trekName='" + trekName + '\'' +
                ", touristName='" + touristName + '\'' +
                ", bookingDate='" + bookingDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public void setGuideName(String name) {
    }
}