package com.smiligence.etmtechAdmin.bean;

public class PaymentDetails {

    String creationDate;
    String sellerId;
    String startDate;
    String endDate;
    int totalAmount;
    String orderCount;
    String paymentStatus;
    String settledAmount;
    String receiptURL;
    int  admindeliveryAmount ;
    int  sellerdeliveryAmount ;
    int totalBilledAmount;
    int totaldeliveryamount ;


    public String getSettledAmount() {
        return settledAmount;
    }

    public void setSettledAmount(String settledAmount) {
        this.settledAmount = settledAmount;
    }

    public int getTotaldeliveryamount() {
        return totaldeliveryamount;
    }

    public void setTotaldeliveryamount(int totaldeliveryamount) {
        this.totaldeliveryamount = totaldeliveryamount;
    }

    public int getTotalBilledAmount() {
        return totalBilledAmount;
    }

    public void setTotalBilledAmount(int totalBilledAmount) {
        this.totalBilledAmount = totalBilledAmount;
    }

    public int getAdmindeliveryAmount() {
        return admindeliveryAmount;
    }

    public void setAdmindeliveryAmount(int admindeliveryAmount) {
        this.admindeliveryAmount = admindeliveryAmount;
    }

    public int getSellerdeliveryAmount() {
        return sellerdeliveryAmount;
    }

    public void setSellerdeliveryAmount(int sellerdeliveryAmount) {
        this.sellerdeliveryAmount = sellerdeliveryAmount;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
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

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(String orderCount) {
        this.orderCount = orderCount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getReceiptURL() {
        return receiptURL;
    }

    public void setReceiptURL(String receiptURL) {
        this.receiptURL = receiptURL;
    }
}
