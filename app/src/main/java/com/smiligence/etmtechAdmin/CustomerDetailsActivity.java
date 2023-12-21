package com.smiligence.etmtechAdmin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmtechAdmin.Adapter.CustomerAdapter;
import com.smiligence.etmtechAdmin.Adapter.OrderDetailsAdapter;
import com.smiligence.etmtechAdmin.bean.CustomerDetails;
import com.smiligence.etmtechAdmin.bean.ItemReviewAndRatings;
import com.smiligence.etmtechAdmin.bean.OrderDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.smiligence.etmtechAdmin.common.Constant.CUSTOMER_DETAILS_FIREBASE_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.ITEM_RATING_REVIEW_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.ORDER_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.TITLE_CUSTOMER_DETAILS;
import static com.smiligence.etmtechAdmin.common.TextUtils.removeDuplicatesList;

public class CustomerDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    public static TextView textViewUsername;
    public static TextView textViewEmail;
    public static ImageView imageView;
    public static Menu menuNav;
    public static View mHeaderView;
    DatabaseReference customerDataRef, orderDataRef;
    List<CustomerDetails> customerDetailsList = new ArrayList<>();
    RecyclerView recyclerView;
    CustomerAdapter customerAdapter;
    CustomerDetails customerDetails;
    ExpandableListView expandableListView;
    ArrayList<String> header_list = new ArrayList<>();
    ArrayList<OrderDetails> billDetailsList = new ArrayList<>();
    String customerID;
    String billedDate;
    ArrayList<OrderDetails> billArrayList = new ArrayList<>();
    ArrayList<OrderDetails> orderDetailList = new ArrayList<>();
    Map<String, List<OrderDetails>> expandableBillDetail = new HashMap<>();
    OrderDetailsAdapter OrderListingAdapter;
    int counter;

    AlertDialog.Builder dialogBuilder;
    View dialogView;
    AlertDialog b;
    String intentIndicator, billId;
    TextView reviewCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(TITLE_CUSTOMER_DETAILS);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(CustomerDetailsActivity.this);
        navigationView.setCheckedItem(R.id.CustomerDetails);

        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);


        reviewCount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.reviewApproval));
        reviewCount.setGravity(Gravity.CENTER_VERTICAL);
        reviewCount.setTypeface(null, Typeface.BOLD);
        reviewCount.setTextColor(getResources().getColor(R.color.redColor));


        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);
        recyclerView = findViewById(R.id.customerDetails);

        textViewUsername.setText(DashBoardActivity.roleName);
        textViewEmail.setText(DashBoardActivity.userName);
        intentIndicator = getIntent().getStringExtra("intentIndicator");
        billId = getIntent().getStringExtra("billId");

        orderDataRef = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_TABLE);

        dialogBuilder = new AlertDialog.Builder(CustomerDetailsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.customer_order_details_dialog, null);
        dialogBuilder.setView(dialogView);
        expandableListView = dialogView.findViewById(R.id.orderhistory);
        ImageView cancel = dialogView.findViewById(R.id.cancel);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });

        if (intentIndicator != null && billId != null) {
            b = dialogBuilder.create();
            b.show();
            b.setCancelable(false);
            customerID = billId;
            fetchOrderDetails(customerID);
        }

        customerDataRef = CommonMethods.fetchFirebaseDatabaseReference(CUSTOMER_DETAILS_FIREBASE_TABLE);
        customerDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    customerDetailsList.clear();
                    for (DataSnapshot customerSnap : dataSnapshot.getChildren()) {
                        customerDetails = customerSnap.getValue(CustomerDetails.class);
                        customerDetailsList.add(customerDetails);

                    }


                    recyclerView.setLayoutManager(new GridLayoutManager(CustomerDetailsActivity.this, 1));
                    recyclerView.setHasFixedSize(true);
                    if (customerDetailsList != null) {

                        customerAdapter = new CustomerAdapter(CustomerDetailsActivity.this, customerDetailsList);
                        customerAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(customerAdapter);
                    }
                    // Collections.sort(customerDetailsList);

                    customerAdapter.setOnItemclickListener(new CustomerAdapter.OnItemClicklistener() {
                        @Override
                        public void Onitemclick(int Position) {


                            customerDetails = customerDetailsList.get(Position);

                            dialogBuilder = new AlertDialog.Builder(CustomerDetailsActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            dialogView = inflater.inflate(R.layout.customer_order_details_dialog, null);
                            dialogBuilder.setView(dialogView);


                            final AlertDialog b = dialogBuilder.create();
                            if (!((Activity) CustomerDetailsActivity.this).isFinishing()) {
                                b.show();
                            }

                            b.setCancelable(false);


                            expandableListView = dialogView.findViewById(R.id.orderhistory);
                            ImageView cancel = dialogView.findViewById(R.id.cancel);

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    b.dismiss();
                                }
                            });
                            String customerID = customerDetails.getCustomerId();


                            fetchOrderDetails(customerID);

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void fetchOrderDetails(String customerID) {

        orderDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                counter = 0;
                billArrayList.clear();
                header_list.clear();
                for (DataSnapshot dateSnap : dataSnapshot.getChildren()) {

                    OrderDetails billDetails = dateSnap.getValue(OrderDetails.class);
                    if (customerID.equalsIgnoreCase(billDetails.getCustomerId())) {
                        billArrayList.add(billDetails);
                        header_list.add(billDetails.getPaymentDate());
                    }


                }

                removeDuplicatesList(header_list);

                if (header_list.size() > 0) {

                    Collections.reverse(header_list);

                    for (int i = 0; i < header_list.size(); i++) {
                        billedDate = header_list.get(i);

                        for (int j = 0; j < billArrayList.size(); j++) {

                            OrderDetails billDetailsData = billArrayList.get(j);

                            if (billedDate.equalsIgnoreCase(billDetailsData.getPaymentDate())) {
                                billDetailsList.add(billDetailsData);
                            }
                        }

                        if (billArrayList != null) {

                            Collections.reverse(billDetailsList);
                            expandableBillDetail.put(header_list.get(counter), billDetailsList);
                            counter++;
                            billDetailsList = new ArrayList<>();

                        }
                    }

                }


                OrderDetailsAdapter receiptAdapter = new
                        OrderDetailsAdapter(CustomerDetailsActivity.this, header_list,
                        (HashMap<String, List<OrderDetails>>) expandableBillDetail);


                expandableListView.setAdapter(receiptAdapter);


                for (int i = 0; i < receiptAdapter.getGroupCount(); i++) {
                    expandableListView.expandGroup(i);
                }

                expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                    @Override
                    public void onGroupExpand(int groupPosition) {

                    }
                });

                expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                    @Override
                    public void onGroupCollapse(int groupPosition) {


                    }
                });

                expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v,
                                                int groupPosition, int childPosition, long id) {
                        OrderDetails billDetails = expandableBillDetail.get(header_list.get(groupPosition))
                                .get(childPosition);
                        String Id = billDetails.getOrderId();

                        Intent intent = new Intent(CustomerDetailsActivity.this, ViewItemDetailsCategoryActivity.class);
                        intent.putExtra("OrderidDetails", String.valueOf(Id));
                        intent.putExtra("intentIndicator", "fromCustomerOrderDetails");
                        intent.putExtra("billId", customerID);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        return false;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        orderDataRef = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.vieworders) {
            Intent intent = new Intent(getApplicationContext(), ViewOrderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if (id == R.id.ApproveSellers) {
            Intent intent = new Intent(getApplicationContext(), ApproveSellersActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        else if (id == R.id.category) {
            Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.Add_Advertisment) {

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
        }/*else if(id==R.id.deliveryfare){
            Intent intent = new Intent(getApplicationContext(), AddDeliveryFareActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }*/ else if (id == R.id.logoutscreen) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CustomerDetailsActivity.this);
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
                    Intent intent = new Intent(CustomerDetailsActivity.this,
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
                    navigationView.setCheckedItem(R.id.vieworders);
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
        }  else if (id == R.id.reviewApproval) {
            Intent intent = new Intent(getApplicationContext(), ItemRatingReviewApprovalActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.legal) {
            Intent intent = new Intent(getApplicationContext(), LegalDetailsUploadActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.dashboard) {
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
    protected void onStart() {
        super.onStart();

        ArrayList<ItemReviewAndRatings> list = new ArrayList<>();
        DatabaseReference
                itemReviewDataRef = CommonMethods.fetchFirebaseDatabaseReference(ITEM_RATING_REVIEW_TABLE);
        Query itemApprovalStatusQuery = itemReviewDataRef.orderByChild("itemRatingReviewStatus").equalTo("Waiting For Approval");
        itemApprovalStatusQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ItemReviewAndRatings itemReviewAndRatings = dataSnapshot1.getValue(ItemReviewAndRatings.class);

                    list.add(itemReviewAndRatings);
                }
                reviewCount.setText(String.valueOf(list.size()));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}