package com.smiligence.etmtechAdmin.bean;

public class AdminRevenueDetails {

    String creationDate;
    String startDate;
    String endDate;
    int revenueFromNewStore;
    int revenueFromProductDelivery;
    int revenueFromAdvertingBanners;
    int totalRevenueAmount;


    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getRevenueFromNewStore() {
        return revenueFromNewStore;
    }

    public void setRevenueFromNewStore(int revenueFromNewStore) {
        this.revenueFromNewStore = revenueFromNewStore;
    }

    public int getRevenueFromProductDelivery() {
        return revenueFromProductDelivery;
    }

    public void setRevenueFromProductDelivery(int revenueFromProductDelivery) {
        this.revenueFromProductDelivery = revenueFromProductDelivery;
    }

    public int getRevenueFromAdvertingBanners() {
        return revenueFromAdvertingBanners;
    }

    public void setRevenueFromAdvertingBanners(int revenueFromAdvertingBanners) {
        this.revenueFromAdvertingBanners = revenueFromAdvertingBanners;
    }

    public int getTotalRevenueAmount() {
        return totalRevenueAmount;
    }

    public void setTotalRevenueAmount(int totalRevenueAmount) {
        this.totalRevenueAmount = totalRevenueAmount;
    }
}
