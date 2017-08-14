package com.kesari.trackingfresh.VehicleRoute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 10/08/17.
 */

public class RouteData {

    private String _id;
    private String vehicleNo;
    private String vehicleId;
    private String createdBy;
    private String createdAt;
    private String __v;

    private List<RouteSubPOJO> routes = new ArrayList<RouteSubPOJO>();

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    public List<RouteSubPOJO> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteSubPOJO> routes) {
        this.routes = routes;
    }
}
