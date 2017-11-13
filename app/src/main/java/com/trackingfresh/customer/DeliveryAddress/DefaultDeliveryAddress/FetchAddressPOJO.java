package com.trackingfresh.customer.DeliveryAddress.DefaultDeliveryAddress;

import com.trackingfresh.customer.DeliveryAddress.AddressPOJO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 05/06/17.
 */

public class FetchAddressPOJO {

    private List<AddressPOJO> data = new ArrayList<AddressPOJO>();

    public List<AddressPOJO> getData() {
        return data;
    }

    public void setData(List<AddressPOJO> data) {
        this.data = data;
    }
}
