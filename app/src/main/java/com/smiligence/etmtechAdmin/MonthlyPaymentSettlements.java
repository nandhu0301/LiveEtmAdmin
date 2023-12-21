package com.smiligence.etmtechAdmin;

import static com.smiligence.etmtechAdmin.common.Constant.ADMIN_REVENUE_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.ADVERTISEMT_DETAILS_FIREBASE_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.MONTHLY_PAYMENT_STTLEMENTS;
import static com.smiligence.etmtechAdmin.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.SELLER_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.SELLER_PAYMENTS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmtechAdmin.Adapter.AdminMonthlyPaymentAdapter;
import com.smiligence.etmtechAdmin.bean.AdminRevenueDetails;
import com.smiligence.etmtechAdmin.bean.AdvertisementDetails;
import com.smiligence.etmtechAdmin.bean.ItemDetails;
import com.smiligence.etmtechAdmin.bean.OrderDetails;
import com.smiligence.etmtechAdmin.bean.SellerPaymentDetails;
import com.smiligence.etmtechAdmin.bean.UserDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonthlyPaymentSettlements extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    public static Menu menuNav;
    public static TextView textViewUsername;
    public static TextView textViewEmail;
    public static ImageView imageView;
    public static View mHeaderView;
    RecyclerView recycler_listview;
    DatabaseReference adminrevenueDataRef,adminRevenueDetails,sellerDetailsDataRef,sellerPaymentsRef ,orderdetailRef,advertisementsDetailsDataRef;
    final ArrayList<String> list = new ArrayList<String>();
    ArrayList<AdminRevenueDetails> adminRevenueDetailsArrayList=new ArrayList<>();
    int Percentage = 0;
    int initialJoiningFee = 0;
    ArrayList<ItemDetails> itemDetailsArrayList = new ArrayList<>();
    int resultTotalAmount = 0;
    int storeBasedTotalAmount = 0;
    int storeBasedTotalAmountRes = 0;
    int advertisemetsAmount=0;
    String lastMonthdate ,firstMonthdate;
    ViewGroup viewGroup;
    androidx.appcompat.app.AlertDialog alertDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_payment_settlements);



        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(MONTHLY_PAYMENT_STTLEMENTS);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MonthlyPaymentSettlements.this);
        navigationView.setCheckedItem(R.id.adminMonthlyPayments);


        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);

        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();


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


        recycler_listview = findViewById(R.id.list_details);
        recycler_listview.setHasFixedSize ( true );
        recycler_listview.setLayoutManager ( new LinearLayoutManager( MonthlyPaymentSettlements.this ) );


        adminrevenueDataRef = CommonMethods.fetchFirebaseDatabaseReference(ADMIN_REVENUE_TABLE).child(firstMonthdate);
        adminrevenueDataRef.child("startDate").setValue(firstMonthdate);
        adminrevenueDataRef.child("endDate").setValue(lastMonthdate);

        adminRevenueDetails = CommonMethods.fetchFirebaseDatabaseReference(ADMIN_REVENUE_TABLE);
        sellerDetailsDataRef = CommonMethods.fetchFirebaseDatabaseReference(SELLER_DETAILS_TABLE);
        sellerPaymentsRef = CommonMethods.fetchFirebaseDatabaseReference(SELLER_PAYMENTS);
        orderdetailRef = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);
        advertisementsDetailsDataRef=CommonMethods.fetchFirebaseDatabaseReference(ADVERTISEMT_DETAILS_FIREBASE_TABLE);



        sellerDetailsDataRef.addValueEventListener(new ValueEventListener() {
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
                                            itemDetailsArrayList = (ArrayList<ItemDetails>) orderDetails.getItemDetailList();
                                            resultTotalAmount = 0;
                                            if (itemDetailsArrayList.size() > 0 && itemDetailsArrayList != null) {
                                                for (int k = 0; k < itemDetailsArrayList.size(); k++) {

                                                    if (itemDetailsArrayList.get(k).getSellerId().equals(userDetails.getUserId())) {
                                                    for (int i = 0; i < list.size(); i++) {
                                                        if (orderDetails.getFormattedDate().equals(list.get(i))) {
                                                            if (orderDetails.getOrderStatus().equals("Completed")) {


                                                                if (itemDetailsArrayList.get(k).getDeliveryby().equals("Admin")) {
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
        });

        adminRevenueDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    adminRevenueDetailsArrayList.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        AdminRevenueDetails adminRevenueDetails = snap.getValue(AdminRevenueDetails.class);
                        adminRevenueDetailsArrayList.add(adminRevenueDetails);
                      //  Collections.reverse(adminRevenueDetailsArrayList);
                    }
                    AdminMonthlyPaymentAdapter paymentAdapter = new AdminMonthlyPaymentAdapter(MonthlyPaymentSettlements.this, adminRevenueDetailsArrayList);
                    recycler_listview.setAdapter(paymentAdapter);
                    paymentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        else if (id == R.id.adminfare) {
            Intent intent = new Intent(getApplicationContext(), MaintainDeliveryFairActivity.class);
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
        } else if (id == R.id.logoutscreen) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MonthlyPaymentSettlements.this);
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
                    Intent intent = new Intent(MonthlyPaymentSettlements.this,
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
        } else if (id == R.id.adminMonthlyPayments) {
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
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}