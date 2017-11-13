package com.trackingfresh.customer.YourOrders.RepeatOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 10/10/17.
 */

public class RepeatOrderSubPojo {

    private String isDeleted;
    private List<RepeatOrderAvailablePOJO> products = new ArrayList<RepeatOrderAvailablePOJO>();

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public List<RepeatOrderAvailablePOJO> getProducts() {
        return products;
    }

    public void setProducts(List<RepeatOrderAvailablePOJO> products) {
        this.products = products;
    }
}
