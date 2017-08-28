package com.kesari.trackingfresh.VehicleNearestRoute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 23/08/17.
 */

public class NearestRouteMainPOJO {

    private List<NearestRouteSubPOJO> data = new ArrayList<NearestRouteSubPOJO>();

    public List<NearestRouteSubPOJO> getData() {
        return data;
    }

    public void setData(List<NearestRouteSubPOJO> data) {
        this.data = data;
    }
}
