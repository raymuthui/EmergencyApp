package com.example.Safenow;

public class LocationStorage {
    private static final LocationStorage INSTANCE = new LocationStorage();
    private Double latitude;
    private Double longitude;

    private LocationStorage() {
    }

    public static LocationStorage getInstance() {
        return INSTANCE;
    }

    public void setLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
