package com.kesari.trackingfresh.ProductPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 18/05/17.
 */

public class SubProductMainPOJO {

    private List<SubProductSubPOJO> data = new ArrayList<SubProductSubPOJO>();

    public List<SubProductSubPOJO> getData() {
        return data;
    }

    public void setData(List<SubProductSubPOJO> data) {
        this.data = data;
    }
}
