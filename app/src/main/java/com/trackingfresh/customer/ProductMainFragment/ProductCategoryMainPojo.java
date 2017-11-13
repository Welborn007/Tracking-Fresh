package com.trackingfresh.customer.ProductMainFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 18/05/17.
 */

public class ProductCategoryMainPojo {

    private List<ProductCategorySubPOJO> data = new ArrayList<ProductCategorySubPOJO>();

    public List<ProductCategorySubPOJO> getData() {
        return data;
    }

    public void setData(List<ProductCategorySubPOJO> data) {
        this.data = data;
    }
}
