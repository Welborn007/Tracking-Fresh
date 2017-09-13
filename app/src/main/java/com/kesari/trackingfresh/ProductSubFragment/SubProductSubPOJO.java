package com.kesari.trackingfresh.ProductSubFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 18/05/17.
 */

public class SubProductSubPOJO {

    private String productDescription;

    private String unitsOfMeasurement;

    private String productCategory;

    private String productImage;

    private String productId;

    private String unit;

    private String _id;

    private String unitsOfMeasurementId;

    private String productDetails;

    private String active;

    private String productName;

    private String productCategoryId;

    private String price;

    private String selling_price;

    private String quantity;

    private String brand;

    private String MRP;

    private String offer;

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    private List<ProductImagesPOJO> productImages = new ArrayList<ProductImagesPOJO>();

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public List<ProductImagesPOJO> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ProductImagesPOJO> productImages) {
        this.productImages = productImages;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getAvailableQuantity() {
        return quantity;
    }

    public void setAvailableQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(String selling_price) {
        this.selling_price = selling_price;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getUnitsOfMeasurement() {
        return unitsOfMeasurement;
    }

    public void setUnitsOfMeasurement(String unitsOfMeasurement) {
        this.unitsOfMeasurement = unitsOfMeasurement;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUnitsOfMeasurementId() {
        return unitsOfMeasurementId;
    }

    public void setUnitsOfMeasurementId(String unitsOfMeasurementId) {
        this.unitsOfMeasurementId = unitsOfMeasurementId;
    }


    public String getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(String productDetails) {
        this.productDetails = productDetails;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }
}
