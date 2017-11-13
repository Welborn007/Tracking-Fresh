package com.trackingfresh.customer.CheckNearestVehicleAvailability;

/**
 * Created by kesari on 28/06/17.
 */

public class NearestVehicleSubPOJO {

    private Geo geo;

    private String _id;

    private String __v;

    private String created_at;

    private String vehicle_id;

    private Dist dist;

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public Dist getDist() {
        return dist;
    }

    public void setDist(Dist dist) {
        this.dist = dist;
    }
}
