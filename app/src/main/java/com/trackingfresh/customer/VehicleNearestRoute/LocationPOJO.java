package com.trackingfresh.customer.VehicleNearestRoute;

/**
 * Created by kesari on 23/08/17.
 */

public class LocationPOJO {

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
