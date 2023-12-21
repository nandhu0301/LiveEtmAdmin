package com.smiligence.etmtechAdmin.bean;

public class CategoryDetailsNew {

    String categoryid;
    String categoryName;
    String categoryImage;
    String categoryCreatedDate;
    String categoryPriority;
    private Boolean value;
    int minimumCartValue;
    int deliveryChargeValue;
    int normalDistanceDelivery;
    int longDistanceDelivery;

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryCreatedDate() {
        return categoryCreatedDate;
    }

    public void setCategoryCreatedDate(String categoryCreatedDate) {
        this.categoryCreatedDate = categoryCreatedDate;
    }

    public String getCategoryPriority() {
        return categoryPriority;
    }

    public void setCategoryPriority(String categoryPriority) {
        this.categoryPriority = categoryPriority;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public int getMinimumCartValue() {
        return minimumCartValue;
    }

    public void setMinimumCartValue(int minimumCartValue) {
        this.minimumCartValue = minimumCartValue;
    }

    public int getDeliveryChargeValue() {
        return deliveryChargeValue;
    }

    public void setDeliveryChargeValue(int deliveryChargeValue) {
        this.deliveryChargeValue = deliveryChargeValue;
    }

    public int getNormalDistanceDelivery() {
        return normalDistanceDelivery;
    }

    public void setNormalDistanceDelivery(int normalDistanceDelivery) {
        this.normalDistanceDelivery = normalDistanceDelivery;
    }

    public int getLongDistanceDelivery() {
        return longDistanceDelivery;
    }

    public void setLongDistanceDelivery(int longDistanceDelivery) {
        this.longDistanceDelivery = longDistanceDelivery;
    }
}
