package com.kesari.trackingfresh.CheckNearestVehicleAvailability;

/**
 * Created by kesari on 28/06/17.
 */

public class Location {

    private String type;

    private String[] coordinates;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String[] coordinates) {
        this.coordinates = coordinates;
    }
}
