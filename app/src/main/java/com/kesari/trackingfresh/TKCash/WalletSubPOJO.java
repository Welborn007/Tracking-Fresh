package com.kesari.trackingfresh.TKCash;

/**
 * Created by kesari on 07/07/17.
 */

public class WalletSubPOJO {

    private String operation;

    private String createdBy;

    private String source;

    private String createdAt;

    private String updatedAmount;

    private String innitialAmount;

    private String remarks;

    private String sourceAmount;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAmount() {
        return updatedAmount;
    }

    public void setUpdatedAmount(String updatedAmount) {
        this.updatedAmount = updatedAmount;
    }

    public String getInnitialAmount() {
        return innitialAmount;
    }

    public void setInnitialAmount(String innitialAmount) {
        this.innitialAmount = innitialAmount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSourceAmount() {
        return sourceAmount;
    }

    public void setSourceAmount(String sourceAmount) {
        this.sourceAmount = sourceAmount;
    }
}
