package com.trackingfresh.customer.VehicleNearestRoute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 23/08/17.
 */

public class NearestRouteSubPOJO {

    private String cuid;

    private String createdBy;

    private String vehicleId;

    private String _id;

    private String editedBy;

    private String createdAt;

    private String __v;

    private String slug;

    private String vehicleNo;

    private Dist dist;

    private String editedAt;

    private List<RouteNearestPOJO> routes = new ArrayList<RouteNearestPOJO>();

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(String editedBy) {
        this.editedBy = editedBy;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public Dist getDist() {
        return dist;
    }

    public void setDist(Dist dist) {
        this.dist = dist;
    }

    public String getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(String editedAt) {
        this.editedAt = editedAt;
    }

    public List<RouteNearestPOJO> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteNearestPOJO> routes) {
        this.routes = routes;
    }
}
