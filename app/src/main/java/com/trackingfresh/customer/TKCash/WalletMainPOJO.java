package com.trackingfresh.customer.TKCash;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 07/07/17.
 */

public class WalletMainPOJO {

    private List<WalletSubPOJO> data = new ArrayList<WalletSubPOJO>();

    public List<WalletSubPOJO> getData() {
        return data;
    }

    public void setData(List<WalletSubPOJO> data) {
        this.data = data;
    }
}
