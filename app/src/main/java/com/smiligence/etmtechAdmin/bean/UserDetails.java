package com.smiligence.etmtechAdmin.bean;

import java.util.ArrayList;
import java.util.List;

public class UserDetails {

    String userId;
    String creationDate;
    String firstName;
    String lastName;
    String phoneNumber;
    String address;
    String pincode;
    String password;
    String confirmPassword;
    String email_Id;
    String paymentType;
    String fssaiNumber;
    String gstNumber;
    int parcelCharge;
    int DeliveryCharge;
    int sgstCharge;
    int cgstCharge;

    String accountNumber;
    String bankName;
    String branchName;
    String ifscCode;
    String aadharNumber;

    String bikeNumber;
    String bikeLisenceNumber;
    String photo;
    String  gstCertificateImage ;
    String roleName;

    String storeName;
    String storeLogo;
    String productKey;
    String lastLoginDate;
    String businessType;
    String businessName;
    String aadharImage;
    String deviceToken;
    String formattedDate;
    List<CategoryDetailsNew> categoryList=new ArrayList<> (  );



    public List<CategoryDetailsNew> getCategoryList() {
        return categoryList;
    }


    public String getGstCertificateImage() {
        return gstCertificateImage;
    }

    public void setGstCertificateImage(String gstCertificateImage) {
        this.gstCertificateImage = gstCertificateImage;
    }

    public void setCategoryList(List<CategoryDetailsNew> categoryList) {
        this.categoryList = categoryList;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public UserDetails setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
        return this;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getAadharImage() {
        return aadharImage;
    }

    public void setAadharImage(String aadharImage) {
        this.aadharImage = aadharImage;
    }

    public String getFssaiCertificateImage() {
        return fssaiCertificateImage;
    }

    public void setFssaiCertificateImage(String fssaiCertificateImage) {
        this.fssaiCertificateImage = fssaiCertificateImage;
    }

    public double getStoreLongtide() {
        return storeLongtide;
    }

    public void setStoreLongtide(double storeLongtide) {
        this.storeLongtide = storeLongtide;
    }

    String fssaiCertificateImage;
    double storeLatitude;
    double storeLongtide;

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getCommentsIfAny() {
        return commentsIfAny;
    }

    public void setCommentsIfAny(String commentsIfAny) {
        this.commentsIfAny = commentsIfAny;
    }

    String approvalStatus;
    String commentsIfAny;



    List<UserDetails> userDetailsList=new ArrayList<>(  );

    public List<UserDetails> getUserDetailsList() {
        return userDetailsList;
    }

    public void setUserDetailsList(List<UserDetails> userDetailsList) {
        this.userDetailsList = userDetailsList;
    }


    public double getStoreLatitude() {
        return storeLatitude;
    }

    public void setStoreLatitude(double storeLatitude) {
        this.storeLatitude = storeLatitude;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail_Id() {
        return email_Id;
    }

    public void setEmail_Id(String email_Id) {
        this.email_Id = email_Id;
    }

    public String getFssaiNumber() {
        return fssaiNumber;
    }

    public void setFssaiNumber(String fssaiNumber) {
        this.fssaiNumber = fssaiNumber;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public int getParcelCharge() {
        return parcelCharge;
    }

    public void setParcelCharge(int parcelCharge) {
        this.parcelCharge = parcelCharge;
    }

    public int getDeliveryCharge() {
        return DeliveryCharge;
    }

    public void setDeliveryCharge(int deliveryCharge) {
        DeliveryCharge = deliveryCharge;
    }

    public int getSgstCharge() {
        return sgstCharge;
    }

    public void setSgstCharge(int sgstCharge) {
        this.sgstCharge = sgstCharge;
    }

    public int getCgstCharge() {
        return cgstCharge;
    }

    public void setCgstCharge(int cgstCharge) {
        this.cgstCharge = cgstCharge;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getBikeNumber() {
        return bikeNumber;
    }

    public void setBikeNumber(String bikeNumber) {
        this.bikeNumber = bikeNumber;
    }

    public String getBikeLisenceNumber() {
        return bikeLisenceNumber;
    }

    public void setBikeLisenceNumber(String bikeLisenceNumber) {
        this.bikeLisenceNumber = bikeLisenceNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreLogo() {
        return storeLogo;
    }

    public void setStoreLogo(String storeLogo) {
        this.storeLogo = storeLogo;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
