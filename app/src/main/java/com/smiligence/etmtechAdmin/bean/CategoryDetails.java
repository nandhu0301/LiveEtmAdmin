package com.smiligence.etmtechAdmin.bean;

import java.util.ArrayList;
import java.util.List;

public class CategoryDetails
{
    String categoryid;
    String categoryName;
    String categoryImage;
    String subCategoryId;
    String subCategoryName;
    String subCategoryImage;
    String categoryCreatedDate;
    String subCategoryCreatedDate;
    String categoryPriority;
    private Boolean value;
    String SuperCategory;
    String superCategoryId;
    String itemId;





    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getSuperCategoryId() {

        return superCategoryId;
    }

    public void setSuperCategoryId(String superCategoryId) {
        this.superCategoryId = superCategoryId;
    }



    public String getSuperCategory() {
        return SuperCategory;
    }

    public void setSuperCategory(String superCategory) {
        SuperCategory = superCategory;
    }



    public List<CategoryDetails> getSubCategoryList() {
        return subCategoryList;
    }

    public void setSubCategoryList(List<CategoryDetails> subCategoryList) {
        this.subCategoryList = subCategoryList;
    }

    List<CategoryDetails> subCategoryList=new ArrayList<>();


    public List<UserDetails> getSellerList() {
        return SellerList;
    }

    public void setSellerList(List<UserDetails> sellerList) {
        SellerList = sellerList;
    }

    List<UserDetails> SellerList = new ArrayList<>();




    public String getCategoryPriority() {
        return categoryPriority;
    }

    public void setCategoryPriority(String categoryPriority) {
        this.categoryPriority = categoryPriority;
    }

    public CategoryDetails(){
        super();
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public String getCategoryCreatedDate() {
        return categoryCreatedDate;
    }

    public void setCategoryCreatedDate(String categoryCreatedDate) {
        this.categoryCreatedDate = categoryCreatedDate;
    }

    public String getSubCategoryCreatedDate() {
        return subCategoryCreatedDate;
    }

    public void setSubCategoryCreatedDate(String subCategoryCreatedDate) {
        this.subCategoryCreatedDate = subCategoryCreatedDate;
    }

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

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getSubCategoryImage() {
        return subCategoryImage;
    }

    public void setSubCategoryImage(String subCategoryImage) {
        this.subCategoryImage = subCategoryImage;
    }
}
