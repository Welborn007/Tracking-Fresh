package com.trackingfresh.customer.NotificationList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 14/09/17.
 */

public class NotificationMainPOJO {

    private List<NotificationSubPOJO> data = new ArrayList<NotificationSubPOJO>();

    public List<NotificationSubPOJO> getData() {
        return data;
    }

    public void setData(List<NotificationSubPOJO> data) {
        this.data = data;
    }
}
