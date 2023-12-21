package com.smiligence.etmtechAdmin.common;

public class Constant {

    public static String USER_DETAILS_TABLE = "UserDetails";
    public static String ADMIN = "ADMINISTRATION";
    public static String LOGIN_SUCCESS = "Login Successfully";
    public static String TITLE_ITEMS = "Items";
    public static String ADMIN_FARE = "Admin Fare";
    public static String MONTHLY_PAYMENT_STTLEMENTS = "Monthly Payment Settlements";
    public static String TITLE_DESCRIPTOB = "Description";
    public static String TITLE_VIEWORDERS = "View Orders";
    public static String TITLE_ORDERTRACKING = "Order Tracking";
    public static String TITLE_APPROVESTOREPARTNER = "Approve Store Partner";
    public static String TITLE_ADDDESCRIPTION = "Add Description";
    public static String TITLE_UPDATE_ORDERS = "Update Order Status";
    public static String TITLE_CATEGORY = "Categories";
    public static String TITLE_ITEM_REVIEW = "Approve Reviews";
    public static String TITLE_RESTRICTED_PINCODES = "Restricted Pincodes";
    public static String TITLE_CUSTOMER_DETAILS = "Customer Details";
    public static String TITLE_SUPER_CATEGORY = "Super Categories";
    public static String CUSTOMER_DETAILS_FIREBASE_TABLE = "CustomerLoginDetails";
    public static String ITEM_RATING_REVIEW_TABLE = "ItemRatingsAndReview";
    public static String STORE_HISTORY="Store History";
    public static String SELLER_PAYMENTS="SellerPayments";

    public static String DISCLAIMER = "Disclaimer";
    public static String TERMS_AND_CONDITIONS = "TermsAndConditions";
    public static String PRIVACY_POLICY = "PrivacyPolicy";
    public static String LEGAL_DETAILS = "LegalDetails";
    public static String ADMIN_REVENUE_TABLE="AdminRevenueDetails";
    public static String SUPER_CATEGORY_DETAILS_TABLE = "SuperCategory";
    public static String CATEGORY_DETAILS_TABLE = "Category";

    public static String PRODUCT_DETAILS = "ProductDetails";
    public static String ADVERTISEMT_DETAILS_FIREBASE_TABLE = "AdvertisementsForApp";
    public static String ADVERTISEMT_DETAILS_FIREBASESTORAGE_TABLE = "Advertisements/";
    public static String SubCATEGORY_DETAILS = "subCategoryList";
    public static String CATEGORY_NAME = "categoryName";
    public static String INGREDIENTS_NAME = "ingredientName";
    public static String SUBCATEGORY_NAME = "SubcategoryName";
    public static String DISCOUNT_DETAILS_FIREBASE_TABLE = "Discount";
    public static String CATEGORY_ID = "categoryId";
    public static String CATEGORY_IMAGE_STORAGE = "Categories/";
    public static String INGREDIENTS_IMAGE_STORAGE = "Categories/";
    public static String INGREDIENTS_TABLE = "Ingredients";
    public static String SUPER_CATEGORY_IMAGE_STORAGE = "Ingredients/";
    public static String SUBCATEGORY_IMAGE_STORAGE = "sub-Categories/";
    public static int DEFAULT_ROW_COUNT = 3;
    public static String TITLE_SUBCATEGORY = "Sub Categories";
    public static String ORDER_DETAILS_TABLE = "OrderDetails";

    public static String PAYMENTS = "Payments";
    public static String RESTRICTED_PINCODE = "RestrictedPinCode";
    public static String TITLE_SELLER = "My Profile";
    public static String TITLE_LEGAL_DETAILS = "Legal Details";
    public static String DETAILS_INSERTED = "Inserted Successfully";
    public static String TITLE_DISCOUNT = "Discounts";
    public static String TITLE_STOREPARTNER = "Approve Store Partner";
    public static String TITLE_BILLING = "Sales Screen";
    public static String SELECT_CATEGORY = "Select Category";
    public static String SELECT_SUB_CATEGORY = "Select Sub-Category";
    public static String GENERAL_CATEGORY = "General Category";
    public static String TITLE_STOREHISTORY = "Store History";
    public static String TITLE_MAINTAININGINPUTS = "Maintaining Inputs";
    public static String DELIVERY_DETAILS="Contact Details";
    public static String TITLE_UPDATEORDERSTATUS = "Update Order Status";
    public static String TITLE_DELIVERYSETTINGS = "Delivery Settings";
    public static String TITLE_LEGALDETAILS = "Legal Details";
    public static String TITLE_DASHBOARD = "DashBoard";
    public static String TITLE_LOGOUT = "Logout";
    public  static  String TITILE_ADMIN_CONTACT_DETAILS="Contact Details";
    public  static  String METROZ_STORE_TIMINGS="MetrozstoreTiming";

    public static String FORMATED_BILLED_DATE = "formattedDate";
    public static String TITLE_MAINTAINBANNER = "Maintain Banners";

    public static String TEXT_BLANK = "";
    public static String REQUIRED = "Required";
    public static String DATE_FORMAT = "MMMM dd, YYYY";

    public static String DATE_FORMAT_YYYYMD = "dd-MM-YYYY";
    public static String DATE_AND_TIME= "MMMM dd, yyyy HH:MM:SS";
    public static String DATE_TIME_FORMAT = "MMMM dd, YYYY HH:mm:ss";
    public static final int PASSWORD_LENGTH = 8;
    public static boolean BOOLEAN_FALSE = false;
    public static boolean BOOLEAN_TRUE = true;
    public static String ITEM_NAME_PATTERN = "^[a-zA-Z0-9]+([\\s][a-zA-Z0-9]+)*$";
    public static final String ITEM_LIMITATION_PATTERN = "[0-9]*$";
    public static final String PERCENTAGE_PATTERN = "^100$|^[0-9]{1,2}$|^[0-9]{1,2}\\,[0-9]{1,3}$";
    // public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@%^&+=!;'])(?=\\S+$)[^\\s$.#{}$`,.\\\\\\\"|]{4,}$";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@%^&+=!;'])(?=\\S+$)[^\\s$.#{}$`,.\\\\\\\"|]{4,}$";
    public static String DATE_TIME_FORMAT_NEW = "MMMM dd, YYYY HH:mm:ss";
    public static final String NAME_PATTERNS_CATAGORY_ITEM = "^[a-zA-Z0-9]+([\\s][a-zA-Z0-9]+)*$";
    public static String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+(\\.+[a-z]+)?";
    public static String INSTAGRAM_URL_PATTERN = "/^\\s*(http\\:\\/\\/)?instagram\\.com\\/[a-z\\d-_]{1,255}\\s*$/i";
    public static String USER_NAME_PATTERN = "^[a-zA-Z0-9_-]{3,15}$";
    public static String FIRST_NAME_PATTERN = "[a-zA-Z ]+\\.?";
    public static String NAME_PATTERN = "[a-zA-Z .@/]+";
    public static String BANK_NAME_PATTERN = "[a-zA-Z][a-zA-Z ]+[a-zA-Z]$";
    public static String LAST_NAME_PATTERN = "[a-zA-Z ]+\\.?";
    public static String ALPHA_NUMERIC_PATTERN = "^(?=.*?[a-zA-Z])(?=.*?[0-9])[0-9a-zA-Z]+$";
    public static String ADDRESS_PATTERN = "^[a-zA-Z0-9\\s,'-]*$/";
    public static String NUMERIC_PATTERN = "[0-9]*";
    public static final String ITEM_PRICE_PATTERN = "[0-9]*$";
    public static String ALPHA_NUMERIC_STRING_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[/])[^\\s$`,.\\\\\\;:'\"|]{3,40}$";
    public static String ACTIVE_STATUS = "Available";
    public static String DISCOUNT_NAME_PATTERN = "^[a-zA-Z0-9]+([\\s][a-zA-Z0-9]+)*$";
    public static String PHONE_NUM_COLUMN = "phoneNumber";
    public static String EMAIL_COLUMN = "emailId";
    public static String SELLER_DETAILS_TABLE = "SellerLoginDetails";

    public static String SELLER_IMAGE_STORAGE = "SellerLogo/";
    public static String STORE_DETAILS = "SellerDetails";
    public static String STORE_STORAGE_DETAILS = "SellerDetails/";
    public static String INVALID_PASSWORD = "Invalid Password";
    public static String INVALID_USERNAME = "Invalid UserName";
    public static String INVALID_PHONENUMBER = "Invalid Phone Number";
    public static String INVALID_FIRSTNAME_SPECIFICATION = "First Name accepts Alphabet and WhiteSpaces only!";
    public static String ALPHA_NUMERIC = "Accepts Alphanumeric values!";
    public static String NUMERIC = "Accepts numeric values!";
    public static String PINCODE_VALIDATION = "Pincode accepts 6 digits number value only!";
    public static String INVALID_NAME_SPECIFICATION = "Accepts Alphabets Only(Minimum 3 characters!";
    public static String INVALID_LASTNAME_SPECIFICATION = "LastName allows alphabets or whitespaces";
    public static String INVALID_PASSWORD_SPECIFICATION = "Password must be minimum 8 charaters and should be a combination of AlphaNumeric and Specifical characters (?=.*[@#$%^&+=!]) !!!";
    public static String RE_ENTER_PASSWORD = "Re-Enter Password";
    public static String ENTER_VALID_PRICE = "Enter valid Price";
    public static String REQUIRED_MSG="Required";

    public static String TAMIL_NADU_PINCODE_PATTERN = "^[6]{1}[2]{1}[0-9]{4}$";
    public static String INDIA_PINCODE = "^[1-9]{1}[0-9]{5}$";
    public static final String GST_NUMBER_PATTERN = "[0-9a-zA-Z]{15}";
    public static final String GST_PRICE_PATTERN = "[0-9]+(\\.[0-9][0-9]?)?";
    public static final String ZIPCODE_PATTERN = "^[1-9][0-9]{5}$";


    public static String RECEIPT_SETTLEMENT = "ReceiptSettlements";
    public static String BILLED_DATE_COLUMN = "paymentDate";
    public static String ORDER_DETAILS_FIREBASE_TABLE = "OrderDetails";
    public static String DASHBOARD = "DashBoard 1.11";
    public static String ORDERDETAILS = "Order Details";
    public static String MONTHLYPAYMENTSETTLEMENT = "Monthly Payment Settlement";
    public static String PASSWORD_LENGTH_TOO_SHORT = "Password length should be minimum 8 charater!";
    public static String FSSAI_NUMBER_VALIDATION = "Fssai Number accepts 14 digit numeric value only!";
    public static String GST_NUMBER_VALIDATION = "Gst Number accepts 15 digit numeric value only!";
    public static String EMAIL_EXIST = "Email already exists!";
    public static String PHONE_NUMBER_EXIST = "PhoneNumber already exists!";
    public static String INVALID_EMAIL = "Invalid email address";
    public static String SELECT_ROLE_ERROR_MSG = "Please Select Role";
    public static String USER_NAME_NOTFOUND = "Please Enter correct username";
    public static String INVALID_PRODUCT_KEy = "Please enter valid productKey";
    public static String RESISTRATION_SUCCESS = "Successfully Registerd";
    public static String ASSIGNED_TO_STATUS = "assignedTo";
    public static String IMAGE_SELECT_MSG = "Please Select an image";
    public static String IMAGE_UPLOAD_SUCCESSFUL = "Item Uploaded Successfully";

    public static String DISCOUNT_CATEGORY = "Select Discount Category";
    public static String SELECT_DISCOUNT_ITEMS = "***ADD ITEMS***";
    public static String BILL_DISCOUNT = "Bill Discount";
    public static String FREE_OFFER_DISCOUNT = "Free Offers";
    public static String DISCOUNT_NAME_COLUMN = "discountName";
    public static String DISCOUNT_STATUS_COLUMN = "discountStatus";
    public static String FREE_OFFER_COLUMN = "Free Offers";
    public static String BILL_DISCOUNT_COLUMN = "Bill Discount";

    public static String CUSTOMER_DISCOUNT_DETAILS_FIREBASE_TABLE = "CustomerDiscount";
    public static String INACTIVE_STATUS = "UnAvailable";


    public static String PRICE_DISCOUNT = "Price";
    public static String PERCENT_DISCOUNT = "Percent";

    //Finacial Year constant

    public static String Order_format_start = "Order_FY(";
    public static String Order_format_end = ")";

    public static String Order_Placed = "Order Placed";
    public static String Order_confirmed = "Order confirmed";
    public static String Packed_and_ready_for_shipping = "Packed and ready for shipping";
    public static String Shipped = "Shipped";

    //
    public static String TamilNadu = "TamilNadu";
    public static String OutsideTamilnadu = "OutsideTamilNadu";


}
