package com.smiligence.etmtechAdmin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmtechAdmin.bean.ItemDetails;
import com.smiligence.etmtechAdmin.bean.MenuModel;
import com.smiligence.etmtechAdmin.bean.OrderDetails;
import com.smiligence.etmtechAdmin.bean.UserDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.DateUtils;
import com.smiligence.etmtechAdmin.common.NetworkStateReceiver;
import com.smiligence.etmtechAdmin.common.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.smiligence.etmtechAdmin.common.Constant.ADMIN_REVENUE_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.ADVERTISEMT_DETAILS_FIREBASE_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.BILLED_DATE_COLUMN;
import static com.smiligence.etmtechAdmin.common.Constant.DASHBOARD;
import static com.smiligence.etmtechAdmin.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.PHONE_NUM_COLUMN;
import static com.smiligence.etmtechAdmin.common.Constant.SELLER_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.SELLER_PAYMENTS;
import static com.smiligence.etmtechAdmin.common.Constant.USER_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.TextUtils.removeDuplicatesList;


import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class DashBoardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , NetworkStateReceiver.NetworkStateReceiverListener {

    public String userNameStr, passwordStr;
    TextView sales, items, quantity, customer, bill_report, storeNameText;
    Button viewSalesReport, viewItemsReport;
    DatabaseReference billdataref,sellerDetailsDataRef,sellerPaymentsRef,orderdetailRef, advertisementsDetailsDataRef ,itemDataRef, userDetailsDataRef,adminrevenueDataRef;
    BarChart barChart, salesBarChart;
    ExpandableListView expandableListView;
    final ArrayList<OrderDetails> billDetailsArrayList = new ArrayList<>();
    final ArrayList<ItemDetails> itemDetailsArrayList = new ArrayList<>();
    List<String> customerList = new ArrayList<>();
    List<String> billTimeArrayList = new ArrayList<>();
    List<String> itemList = new ArrayList<>();
    List<String> storeList = new ArrayList<>();
    List<String> shippingPincodeList = new ArrayList<>();
    final ArrayList<String> itemName = new ArrayList<>();
    ArrayList<String> billList = new ArrayList<>();
    int Expense = 0;
    int uniqueItemCount = 0;
    int todaysTotalSalesAmt = 0;
    int todaysTotalQty = 0;
    int uniqueCustomerCount = 0;
    int totalItemCount = 0;
    int resultTotalAmount = 0;
    boolean isChecked = true;
    UserDetails sellerDetails = new UserDetails();
    List<UserDetails> userDetailsList = new ArrayList<>();
    final boolean[] onDataChangeCheck = {false};
   // TextView sellerListtxt, deliveryListtxt, approveItemListtxt;
    TextView approveItemListtxt;
    final ArrayList<ItemDetails> productDetailsArrayList = new ArrayList<>();
    public static String saved_username, saved_password, saved_Id;
    HashMap<String, Integer> billAmountHashMap = new HashMap<>();
    Query query;
    NavigationView navigationView;
    public static String saved_productKey, saved_businessName, saved_userName, saved_email;
  //  TextView categoryText, locationText, deliveryBoyText;
    ArrayList<String> categoryList = new ArrayList<>();
    boolean check = false;
    public static TextView textViewUsername;
    public static TextView textViewEmail;
    public static ImageView imageView;
    public static Menu menuNav;
    public static View mHeaderView;
    DatabaseReference logindataRef;
    static String roleName, userName;
    TextView reviewCount;
    boolean isCheck = false;
    List<MenuModel> headerList = new ArrayList<>();
    DatabaseReference SellerloginDetails ,orderDetailsDataref;
    private File filePath;
    String date=DateUtils.fetchFormatedCurrentDate();
    HSSFWorkbook hssfWorkbook;
    HSSFSheet hssfSheet;
    HSSFRow headerRow;
    HSSFRow headerRow1;
    ArrayList<OrderDetails> orderDetailsArrayList = new ArrayList<>();
    ArrayList<ItemDetails> itemDetailsArrayListExcel = new ArrayList<>();
    int defaultStartingCellCount = 1;
    private NetworkStateReceiver networkStateReceiver;
    androidx.appcompat.app.AlertDialog alertDialog;
    ViewGroup viewGroup;
    String lastMonthdate ,firstMonthdate;
    int advertisemetsAmount=0;
    final ArrayList<String> list = new ArrayList<String>();
    int initialJoiningFee = 0;
    int Percentage = 0;
    int storeBasedTotalAmount = 0;
    int storeBasedTotalAmountRes = 0;
    ArrayList<ItemDetails> newitemDetailsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

      //  FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        StrictMode.VmPolicy.Builder builder1 = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder1.build());
        builder1.detectFileUriExposure();

        filePath=new File(Environment.getExternalStorageDirectory() + "/Demo"+date+".xls");
        orderDetailsDataref =CommonMethods.fetchFirebaseDatabaseReference("OrderDetails");
  //      mProgressBar = (SmoothProgressBar) findViewById(R.id.gradient);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);



        viewSalesReport = findViewById(R.id.viewreport);
       // sales = findViewById(R.id.sales);
        items = findViewById(R.id.items);
        // quantity = findViewById(R.id.quantity);
        customer = findViewById(R.id.customer);
        bill_report = findViewById(R.id.bill);
        //  storeNameText = findViewById(R.id.storeName);
        barChart = findViewById(R.id.barChart);
        viewItemsReport = findViewById(R.id.itemReports);
        salesBarChart = findViewById(R.id.salesBarChart);
       // categoryText = findViewById(R.id.category);
      //  locationText = findViewById(R.id.location);
        expandableListView = findViewById(R.id.expandableListView);
      //  generateExcel=findViewById(R.id.generateExcel);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(DASHBOARD);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(DashBoardActivity.this);
        navigationView.setCheckedItem(R.id.dashboard);





        SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_userName = loginSharedPreferences.getString("userName", "");
        saved_productKey = loginSharedPreferences.getString("productkeyStr", "");
        saved_businessName = loginSharedPreferences.getString("businessNameStr", "");
        saved_email = loginSharedPreferences.getString("email", "");
        SellerloginDetails = CommonMethods.fetchFirebaseDatabaseReference("SellerLoginDetails");
        advertisementsDetailsDataRef=CommonMethods.fetchFirebaseDatabaseReference(ADVERTISEMT_DETAILS_FIREBASE_TABLE);
        sellerPaymentsRef = CommonMethods.fetchFirebaseDatabaseReference(SELLER_PAYMENTS);
        orderdetailRef = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);

        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);


        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);




        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

        billdataref = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);
        itemDataRef = CommonMethods.fetchFirebaseDatabaseReference("ProductDetails");
        userDetailsDataRef = CommonMethods.fetchFirebaseDatabaseReference(USER_DETAILS_TABLE);
        sellerDetailsDataRef = CommonMethods.fetchFirebaseDatabaseReference(SELLER_DETAILS_TABLE);



        // Get the current month and year
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

// Set the calendar to the first day of the month
        calendar.set(year, month, 1);
        Date firstDate = calendar.getTime();

// Set the calendar to the last day of the month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDate = calendar.getTime();

// Format the dates in "DD-MM-YYYY" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());


// Print the first and last date of the month
        firstMonthdate = dateFormat.format(firstDate);
        lastMonthdate = dateFormat.format(lastDate);


        //run a for loop for  start to end date in a month

        // Get the current date
        Calendar currentDate = Calendar.getInstance();

// Set the start date to the first day of the current month
        Calendar startDate = (Calendar) currentDate.clone();
        startDate.set(Calendar.DAY_OF_MONTH, 1);

// Set the end date to the last day of the current month
        Calendar endDate = (Calendar) currentDate.clone();
        endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));

// Create a list to store the dates
        List<Calendar> dateList = new ArrayList<>();

// Iterate over each day within the range
        for (Calendar date = startDate; date.compareTo(endDate) <= 0; date.add(Calendar.DATE, 1)) {
            // Add the date to the list
            dateList.add((Calendar) date.clone());
        }

// Print the dates in the list
        for (Calendar date : dateList) {
            int yearr = date.get(Calendar.YEAR);
            int monthh = date.get(Calendar.MONTH) + 1; // Months are zero-based, so add 1
            int day = date.get(Calendar.DAY_OF_MONTH);

            // Format the date as desired (e.g., "YYYY-MM-DD")
            String formattedDate = String.format("%02d-%02d-%04d", day, monthh, yearr);
            System.out.println(formattedDate);
            list.add( formattedDate);

        }







        adminrevenueDataRef = CommonMethods.fetchFirebaseDatabaseReference(ADMIN_REVENUE_TABLE).child(firstMonthdate);
        adminrevenueDataRef.child("startDate").setValue(firstMonthdate);
        adminrevenueDataRef.child("endDate").setValue(lastMonthdate);

        onStart();


        Query waitingForApprovalQuery = SellerloginDetails.orderByChild("approvalStatus").equalTo("Waiting for approval");
        waitingForApprovalQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {

                    if (isChecked ) {
                        userDetailsList.clear();
                        for (DataSnapshot deliveryBoySnap : dataSnapshot.getChildren()) {
                            sellerDetails = deliveryBoySnap.getValue(UserDetails.class);
                            userDetailsList.add(sellerDetails);
                        }
                     //   sellerListtxt = findViewById(R.id.storeName);
                     /*   if (userDetailsList.size() > 0) {
                            sellerListtxt.setText(String.valueOf(userDetailsList.size()));
                            sellerListtxt.setVisibility(View.VISIBLE);
                        } else {
                            sellerListtxt.setVisibility(View.INVISIBLE);
                        }*/
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        itemDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (check) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        productDetailsArrayList.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            ItemDetails itemDetails = dataSnapshot1.getValue(ItemDetails.class);
                            if ("Waiting for approval".equalsIgnoreCase(itemDetails.getItemApprovalStatus())) {
                                productDetailsArrayList.add(itemDetails);
                            }
                        }
                        approveItemListtxt = findViewById(R.id.items);
                        if (productDetailsArrayList.size() > 0) {
                            approveItemListtxt.setText(String.valueOf(productDetailsArrayList.size()));
                            approveItemListtxt.setVisibility(View.VISIBLE);
                        } else {
                            approveItemListtxt.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        final Query billDetailsQuery = billdataref.orderByChild(BILLED_DATE_COLUMN).equalTo(DateUtils.fetchCurrentDate());



        billDetailsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clearData();
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot billSnapShot : dataSnapshot.getChildren()) {

                        billDetailsArrayList.add(billSnapShot.getValue(OrderDetails.class));
                        itemDetailsArrayList.add(billSnapShot.getValue(ItemDetails.class));
                        Log.e("itemDetailsArrayList",itemDetailsArrayList.toString());
                        onDataChangeCheck[0] = true;
                    }

                    if (onDataChangeCheck[0] ) {

                    Iterator billIterator = billDetailsArrayList.iterator();

                    while (billIterator.hasNext()) {

                        OrderDetails orderDetails = (OrderDetails) billIterator.next();
                        if (orderDetails.getOrderStatus().equals("Completed")) {
                            customerList.add(orderDetails.getCustomerName());
                            storeList.add(orderDetails.getStoreName());
                            shippingPincodeList.add(orderDetails.getShippingPincode());
                            todaysTotalSalesAmt += (orderDetails.getTotalAmount());
                            //     sales.setText(String.valueOf(todaysTotalSalesAmt));
                            billList.add(orderDetails.getOrderStatus());
                            billTimeArrayList.add(DateUtils.fetchTime(orderDetails.getOrderCreateDate()));
                            billAmountHashMap.put(DateUtils.fetchTimewithSeconds(orderDetails.getOrderCreateDate()), orderDetails.getTotalAmount());

                            Iterator itemIterator = orderDetails.getItemDetailList().iterator();

                            while (itemIterator.hasNext()) {

                                ItemDetails itemDetails = (ItemDetails) itemIterator.next();
                                itemName.add(itemDetails.getItemName());
                                categoryList.add(itemDetails.getCategoryName());
                                todaysTotalQty = todaysTotalQty + itemDetails.getItemBuyQuantity();
                                itemList.add(itemDetails.getItemName());


                            }
                       //     quantity.setText(String.valueOf(todaysTotalQty));
                            removeDuplicatesList(categoryList);
                            removeDuplicatesList(shippingPincodeList);


                            ArrayList<String> newItemList = TextUtils.removeDuplicates((ArrayList<String>) itemList);
                            ArrayList<String> newCustomerList = TextUtils.removeDuplicates((ArrayList<String>) customerList);
                            ArrayList<String> newItemNameList = TextUtils.removeDuplicates((ArrayList<String>) itemName);
                            ArrayList<String> newStoreNameList = TextUtils.removeDuplicates((ArrayList<String>) storeList);

                            uniqueItemCount = uniqueItemCount + newItemList.size();
                            uniqueCustomerCount = uniqueCustomerCount + newCustomerList.size();
                            items.setText(String.valueOf(uniqueItemCount));


                            int noOfBills = billList.size();
                            bill_report.setText(String.valueOf(noOfBills));
                            customer.setText(String.valueOf(uniqueCustomerCount));
                       //     storeNameText.setText(String.valueOf(newStoreNameList.size()));


                            CommonMethods.loadBarChart(barChart, (ArrayList<String>) billTimeArrayList);
                            barChart.animateXY(7000, 5000);
                            barChart.invalidate();
                            barChart.getDrawableState();
                            barChart.setPinchZoom(true);


                            CommonMethods.loadSalesBarChart(salesBarChart, billAmountHashMap);

                            Log.e("salesBarChart", salesBarChart.toString());
                            salesBarChart.animateXY(7000, 5000);
                            salesBarChart.invalidate();
                            salesBarChart.getDrawableState();

                        }
                    }
                }

                }
                else {
                   Toast.makeText(DashBoardActivity.this, "No data ", Toast.LENGTH_SHORT).show();
                }
            }

            private void clearData() {

                billDetailsArrayList.clear();
                customerList.clear();
                itemList.clear();
                itemDetailsArrayList.clear();
                billTimeArrayList.clear();
                storeList.clear();
                billList.clear();
                itemName.clear();
                shippingPincodeList.clear();
                uniqueItemCount = 0;
                uniqueCustomerCount = 0;
                todaysTotalSalesAmt = 0;
                todaysTotalQty = 0;
                totalItemCount = 0;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

/*
        generateExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                File file = new File(Environment.getExternalStorageDirectory() + "/Demo"+date+".xls");
                if(file.exists()) {
                    file.delete();
                }


                hssfWorkbook = new HSSFWorkbook();
                hssfSheet = hssfWorkbook.createSheet("Custom Sheet");
                headerRow1 = hssfSheet.createRow(0);

                //Header cells
                HSSFCell orderNumber = headerRow1.createCell(0);
                HSSFCell orderDate=headerRow1.createCell(1);
                HSSFCell productNumber = headerRow1.createCell(2);
                HSSFCell productName = headerRow1.createCell(3);
                HSSFCell productQty=headerRow1.createCell(4);
                HSSFCell productAmount = headerRow1.createCell(5);
                HSSFCell orderTotal = headerRow1.createCell(6);
                HSSFCell customerId = headerRow1.createCell(7);
                HSSFCell customerName = headerRow1.createCell(8);
                HSSFCell customerLocation = headerRow1.createCell(9);
                HSSFCell sellerId = headerRow1.createCell(10);
                HSSFCell sellerName = headerRow1.createCell(11);
                HSSFCell sellerLocation = headerRow1.createCell(12);
                HSSFCell orderPlacedStatus = headerRow1.createCell(13);
                HSSFCell readyForPickupStatus = headerRow1.createCell(14);
                HSSFCell deliveryIsOnTheWay = headerRow1.createCell(15);
                HSSFCell delivered = headerRow1.createCell(16);
                HSSFCell categoryName=headerRow1.createCell(17);
                HSSFCell subCategoryName=headerRow1.createCell(18);
                HSSFCell sellerPincode=headerRow1.createCell(19);
                HSSFCell distanceFromDeliveryLocationToStore=headerRow1.createCell(20);
                HSSFCell distanceFromStoreToCurrentLocation=headerRow1.createCell(21);


                orderNumber.setCellValue("Order Number");
                orderDate.setCellValue("Order Date");
                productNumber.setCellValue("Product Number");
                productName.setCellValue("Product Name");
                productQty.setCellValue("Product Qty");
                productAmount.setCellValue("Product Amount");
                orderPlacedStatus.setCellValue("Order Placed");
                readyForPickupStatus.setCellValue("Ready for pickup");
                deliveryIsOnTheWay.setCellValue("Delivery is on the way");
                delivered.setCellValue("Delivered");
                orderTotal.setCellValue("Order Total");
                customerLocation.setCellValue("Customer Location");
                customerId.setCellValue("Customer Id");
                customerName.setCellValue("Customer Name");
                sellerId.setCellValue("Seller Id");
                sellerName.setCellValue("Seller Name");
                sellerLocation.setCellValue("Seller Location");
                categoryName.setCellValue("Category Name");
                subCategoryName.setCellValue("SubCategory Name");
                sellerPincode.setCellValue("Seller Pincode");

                orderDetailsDataref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {

                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                OrderDetails orderDetails = dataSnapshot1.getValue(OrderDetails.class);

                                if(orderDetails.getFormattedDate()!=null) {
                                    if (orderDetails.getFormattedDate().equals(date)) {
                                        orderDetailsArrayList.add(orderDetails);
                                        itemDetailsArrayListExcel = (ArrayList<ItemDetails>) orderDetails.getItemDetailList();
                                        for (int i = 0; i < itemDetailsArrayListExcel.size(); i++) {
                                            defaultStartingCellCount = defaultStartingCellCount + 1;
                                            headerRow = hssfSheet.createRow(defaultStartingCellCount);
                                            HSSFCell orderNumber = headerRow.createCell(0);
                                            HSSFCell orderDate = headerRow.createCell(1);


                                            HSSFCell productNumber = headerRow.createCell(2);
                                            HSSFCell productName = headerRow.createCell(3);
                                            HSSFCell productQty = headerRow.createCell(4);
                                            HSSFCell productAmount = headerRow.createCell(5);
                                            HSSFCell orderTotal = headerRow.createCell(6);
                                            HSSFCell customerId = headerRow.createCell(7);
                                            HSSFCell customerName = headerRow.createCell(8);
                                            HSSFCell customerLocation = headerRow.createCell(9);
                                            HSSFCell sellerId = headerRow.createCell(10);
                                            HSSFCell sellerName = headerRow.createCell(11);
                                            HSSFCell sellerLocation = headerRow.createCell(12);
                                            HSSFCell orderPlacedStatus = headerRow.createCell(13);
                                            HSSFCell readyForPickupStatus = headerRow.createCell(14);
                                            HSSFCell deliveryIsOnTheWay = headerRow.createCell(15);
                                            HSSFCell delivered = headerRow.createCell(16);
                                            HSSFCell categoryName = headerRow.createCell(17);
                                            HSSFCell subCategoryName = headerRow.createCell(18);
                                            HSSFCell sellerPincode = headerRow.createCell(19);
                                            HSSFCell distanceFromDeliveryLocationToStore = headerRow.createCell(20);
                                            HSSFCell distanceFromStoreToCurrentLocation = headerRow.createCell(21);

                                            orderNumber.setCellValue(orderDetails.getOrderId());
                                            orderDate.setCellValue(orderDetails.getOrderCreateDate());
                                            orderPlacedStatus.setCellValue(orderDetails.getOrderTime());
                                           orderTotal.setCellValue(orderDetails.getTotalAmount());
                                            customerLocation.setCellValue(orderDetails.getShippingaddress());
                                            customerId.setCellValue(orderDetails.getCustomerId());
                                            customerName.setCellValue(orderDetails.getCustomerName());
                                            sellerName.setCellValue(orderDetails.getStoreName());
                                            sellerLocation.setCellValue(orderDetails.getStoreAddress());
                                            productAmount.setCellValue(String.valueOf(itemDetailsArrayListExcel.get(i).getItemPrice()));
                                            sellerId.setCellValue(itemDetailsArrayListExcel.get(i).getSellerId());
                                            productNumber.setCellValue(itemDetailsArrayListExcel.get(i).getItemId());
                                            productName.setCellValue(itemDetailsArrayListExcel.get(i).getItemName());
                                            productQty.setCellValue(itemDetailsArrayListExcel.get(i).getItemBuyQuantity());
                                            categoryName.setCellValue(itemDetailsArrayListExcel.get(i).getCategoryName());
                                            subCategoryName.setCellValue(itemDetailsArrayListExcel.get(i).getSubCategoryName());
                                            sellerPincode.setCellValue(itemDetailsArrayListExcel.get(i).getStorePincode());
                                            distanceFromStoreToCurrentLocation.setCellValue(orderDetails.getTotalDistanceTraveled());


                                            try {
                                                if (!filePath.exists()) {
                                                    filePath.createNewFile();
                                                }

                                                FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                                                hssfWorkbook.write(fileOutputStream);

                                                if (fileOutputStream != null) {
                                                    fileOutputStream.flush();
                                                    fileOutputStream.close();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                }
                            }
                            if (orderDetailsArrayList!=null)
                            {
                                if (orderDetailsArrayList.size()>0)
                                {
    //                                mProgressBar.progressiveStop();
                                    File file = new File(Environment.getExternalStorageDirectory()+ "/Demo"+date+".xls");
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(file),"application/vnd.ms-excel");
                                    try {
                                        startActivity(intent);
                                    }
                                    catch (ActivityNotFoundException e) {
                                        new SweetAlertDialog(DashBoardActivity.this, SweetAlertDialog.ERROR_TYPE)

                                                .setContentText("No Application Available to View generated Excel.")
                                                .show();

                                    }
                                }else if (orderDetailsArrayList.size()==0)
                                {
                                    new SweetAlertDialog(DashBoardActivity.this, SweetAlertDialog.ERROR_TYPE)

                                            .setContentText("No more data available to generate excel.")
                                            .show();
                                    //mProgressBar.progressiveStop();

                                }
                            }

                        }
                        else {

                            new SweetAlertDialog(DashBoardActivity.this, SweetAlertDialog.ERROR_TYPE)

                                    .setContentText("No more data available to generate excel.")
                                    .show();
         //                   mProgressBar.progressiveStop();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });*/

        viewSalesReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoardActivity.this, ReportGenerationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        viewItemsReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoardActivity.this, ItemsReportGenerationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

         if (id == R.id.storeHistory) {
            Intent intent = new Intent(getApplicationContext(), StoreHistory.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        else if (id == R.id.category) {
            Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.ApproveSellers) {
            Intent intent = new Intent(getApplicationContext(), ApproveSellersActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
         else if (id == R.id.uploadcontactdetails) {
             Intent intent = new Intent(getApplicationContext(), DeliverySettings.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
             startActivity(intent);
         }

        else if (id == R.id.Add_Advertisment) {
            Intent intent = new Intent(getApplicationContext(), AddAdvertisementActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.add_items) {
            Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }  else if (id == R.id.add_description) {
            Intent intent = new Intent(getApplicationContext(), Add_Description_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if (id == R.id.discounts) {
            Intent intent = new Intent(getApplicationContext(), DiscountActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.add_seller) {
            Intent intent = new Intent(getApplicationContext(), MyProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.updatestatus) {
            Intent intent = new Intent(getApplicationContext(), UpdateOrderStatusActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }



        else if (id == R.id.logoutscreen) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DashBoardActivity.this);
            bottomSheetDialog.setContentView(R.layout.logout_confirmation);
            Button logout = bottomSheetDialog.findViewById(R.id.logout);
            Button stayinapp = bottomSheetDialog.findViewById(R.id.stayinapp);

            bottomSheetDialog.show();
            bottomSheetDialog.setCancelable(false);

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences myPrefs = getSharedPreferences("LOGIN",
                            MODE_PRIVATE);
                    SharedPreferences.Editor editor = myPrefs.edit();
                    editor.clear();
                    editor.commit();
                    Intent intent = new Intent(DashBoardActivity.this,
                            LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    bottomSheetDialog.dismiss();
                }
            });
            stayinapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                    navigationView.setCheckedItem(R.id.dashboard);
                }
            });
        } else if (id == R.id.contactdetails) {

            Intent intent = new Intent(getApplicationContext(), DeliverySettings.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.CustomerDetails) {
            Intent intent = new Intent(getApplicationContext(), CustomerDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }  else if (id == R.id.legal) {
            Intent intent = new Intent(getApplicationContext(), LegalDetailsUploadActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.reviewApproval) {
            Intent intent = new Intent(getApplicationContext(), ItemRatingReviewApprovalActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
       //
         else if (id == R.id.adminfare) {
             Intent intent = new Intent(getApplicationContext(), MaintainDeliveryFairActivity.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
             startActivity(intent);
         }
        else if (id == R.id.adminMonthlyPayments) {
             Intent intent = new Intent(getApplicationContext(), MonthlyPaymentSettlements.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
             startActivity(intent);
         }

        else if (id == R.id.ordertracking) {
            Intent intent = new Intent(getApplicationContext(), OrderTrackingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        else if (id == R.id.dashboard) {
            Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if (id == R.id.billingScreen) {
            Intent intent = new Intent(getApplicationContext(), BillingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DashBoardActivity.this);
        bottomSheetDialog.setContentView(R.layout.application_exiting_dialog);
        Button quit = bottomSheetDialog.findViewById(R.id.quit_dialog);
        Button cancel = bottomSheetDialog.findViewById(R.id.cancel_dialog);

        bottomSheetDialog.show();
        bottomSheetDialog.setCancelable(false);

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
                bottomSheetDialog.dismiss();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Query roleNameQuery = userDetailsDataRef.orderByChild(PHONE_NUM_COLUMN).equalTo(String.valueOf(saved_userName));

        roleNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    UserDetails loginUserDetailslist = snap.getValue(UserDetails.class);
                    textViewUsername.setText(loginUserDetailslist.getFirstName().toUpperCase());
                    textViewEmail.setText(loginUserDetailslist.getRoleName().toUpperCase());

                    userName = loginUserDetailslist.getRoleName().toUpperCase();
                    roleName = loginUserDetailslist.getFirstName().toUpperCase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


       /* sellerDetailsDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot sellerSnap : snapshot.getChildren()) {
                        UserDetails userDetails = sellerSnap.getValue(UserDetails.class);

                        if (userDetails.getApprovalStatus().equals("Approved")) {

                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).equals(userDetails.getFormattedDate())) {

                                    sellerPaymentsRef.child(userDetails.getPaymentType()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getChildrenCount() > 0) {
                                                SellerPaymentDetails sellerPaymentDetailsConstant = snapshot.getValue(SellerPaymentDetails.class);
                                                Percentage = sellerPaymentDetailsConstant.getPercentage();
                                                initialJoiningFee = initialJoiningFee + sellerPaymentDetailsConstant.getPayment();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }

                            orderdetailRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getChildrenCount() > 0) {
                                        itemDetailsArrayList.clear();
                                        for (DataSnapshot itemSnap : snapshot.getChildren()) {
                                            OrderDetails orderDetails = itemSnap.getValue(OrderDetails.class);
                                            newitemDetailsArrayList = (ArrayList<ItemDetails>) orderDetails.getItemDetailList();
                                            resultTotalAmount = 0;
                                            if (newitemDetailsArrayList.size() > 0 && newitemDetailsArrayList != null) {
                                                for (int k = 0; k < newitemDetailsArrayList.size(); k++) {

                                                    if (newitemDetailsArrayList.get(k).getSellerId().equals(userDetails.getUserId())) {
                                                        for (int i = 0; i < list.size(); i++) {
                                                            if (orderDetails.getFormattedDate().equals(list.get(i))) {
                                                                if (orderDetails.getOrderStatus().equals("Completed")) {


                                                                    if (newitemDetailsArrayList.get(k).getDeliveryby().equals("Admin")) {
                                                                        resultTotalAmount = resultTotalAmount + 100;
                                                                    }

                                                                }
                                                            }
                                                        }

                                                        storeBasedTotalAmount = storeBasedTotalAmount + resultTotalAmount;
                                                        storeBasedTotalAmountRes = storeBasedTotalAmountRes + resultTotalAmount ;
                                                    }
                                                }
                                            }

                                        }
                                    }

                                    advertisementsDetailsDataRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getChildrenCount()>0) {
                                                advertisemetsAmount= 0;
                                                for (DataSnapshot advertisementSnap:snapshot.getChildren()) {
                                                    AdvertisementDetails advertisementDetails =advertisementSnap.getValue(AdvertisementDetails.class);
                                                    for (int i = 0; i < list.size(); i++) {

                                                        if (advertisementDetails.getScheduledDate().equals(list.get(i))) {
                                                            advertisemetsAmount=advertisemetsAmount+advertisementDetails.getAdvertisementAmount();
                                                        }
                                                    }

                                                    DatabaseReference startTimeDataRef = CommonMethods.fetchFirebaseDatabaseReference(ADMIN_REVENUE_TABLE).child(firstMonthdate);
                                                    startTimeDataRef.child("startDate").setValue(firstMonthdate);
                                                    startTimeDataRef.child("endDate").setValue(lastMonthdate);
                                                    startTimeDataRef.child("revenueFromNewStore").setValue(initialJoiningFee);
                                                    startTimeDataRef.child("revenueFromProductDelivery").setValue(storeBasedTotalAmountRes);
                                                    startTimeDataRef.child("revenueFromAdvertingBanners").setValue(advertisemetsAmount);
                                                    startTimeDataRef.child("totalRevenueAmount").setValue((initialJoiningFee + advertisemetsAmount+storeBasedTotalAmountRes));
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    @Override
    protected void onPause() {
        startNetworkBroadcastReceiver(this);
        unregisterNetworkBroadcastReceiver(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        startNetworkBroadcastReceiver(this);
        registerNetworkBroadcastReceiver(this);
        super.onResume();
    }

    @Override
    public void networkAvailable() {
        alertDialog.dismiss();
    }

    @Override
    public void networkUnavailable() {
        if (!((Activity) DashBoardActivity.this).isFinishing()) {
            showCustomDialog();
        }
    }

    public void startNetworkBroadcastReceiver(Context currentContext) {
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener((NetworkStateReceiver.NetworkStateReceiverListener) currentContext);
        registerNetworkBroadcastReceiver(currentContext);
    }


    public void registerNetworkBroadcastReceiver(Context currentContext) {
        currentContext.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void unregisterNetworkBroadcastReceiver(Context currentContext) {
        currentContext.unregisterReceiver(networkStateReceiver);
    }

    private void showCustomDialog() {

        alertDialog.setCancelable(false);
        alertDialog.show();

    }
}

