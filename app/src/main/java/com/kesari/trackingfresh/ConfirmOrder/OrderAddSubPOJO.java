package com.kesari.trackingfresh.ConfirmOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 08/06/17.
 */

public class OrderAddSubPOJO {

    private String _id;

    private String createdBy;

    private String status;

    private String createdAt;

    private String userId;

    private String __v;

    private String active;

    private String total_price;

    private String addressId;

    private List<OrderAddListPOJO> order = new ArrayList<OrderAddListPOJO>();

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public List<OrderAddListPOJO> getOrder() {
        return order;
    }

    public void setOrder(List<OrderAddListPOJO> order) {
        this.order = order;
    }
}
