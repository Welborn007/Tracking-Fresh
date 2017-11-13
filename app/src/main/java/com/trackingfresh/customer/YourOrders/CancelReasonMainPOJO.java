package com.trackingfresh.customer.YourOrders;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 26/07/17.
 */

public class CancelReasonMainPOJO {

    private List<CancelReasonDataPOJO> data = new ArrayList<CancelReasonDataPOJO>();

    public List<CancelReasonDataPOJO> getData() {
        return data;
    }

    public void setData(List<CancelReasonDataPOJO> data) {
        this.data = data;
    }
}
