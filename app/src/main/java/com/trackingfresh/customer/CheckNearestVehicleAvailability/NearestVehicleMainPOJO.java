package com.trackingfresh.customer.CheckNearestVehicleAvailability;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 28/06/17.
 */

public class NearestVehicleMainPOJO {

    private List<NearestVehicleSubPOJO> data = new ArrayList<NearestVehicleSubPOJO>();

    public List<NearestVehicleSubPOJO> getData() {
        return data;
    }

    public void setData(List<NearestVehicleSubPOJO> data) {
        this.data = data;
    }
}
