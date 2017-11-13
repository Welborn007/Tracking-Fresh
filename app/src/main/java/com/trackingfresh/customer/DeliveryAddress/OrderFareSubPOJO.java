package com.trackingfresh.customer.DeliveryAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 28/06/17.
 */

public class OrderFareSubPOJO {

    private String delivery_charge;

    private String total_price;

    private List<OrderFareListPOJO> orders = new ArrayList<OrderFareListPOJO>();

    public String getDelivery_charge() {
        return delivery_charge;
    }

    public void setDelivery_charge(String delivery_charge) {
        this.delivery_charge = delivery_charge;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public List<OrderFareListPOJO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderFareListPOJO> orders) {
        this.orders = orders;
    }
}
