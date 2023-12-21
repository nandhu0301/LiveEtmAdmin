package com.smiligence.etmtechAdmin;

import static com.smiligence.etmtechAdmin.common.Constant.ORDERDETAILS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmtechAdmin.Adapter.ReceiptExpandableAdapter;
import com.smiligence.etmtechAdmin.bean.ItemDetails;
import com.smiligence.etmtechAdmin.bean.OrderDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.Constant;
import com.smiligence.etmtechAdmin.common.NetworkStateReceiver;
import com.smiligence.etmtechAdmin.common.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderTrackingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , NetworkStateReceiver.NetworkStateReceiverListener{

ExpandableListView orderList ;
ExpandableListView expandableListView;
DatabaseReference orderdetailRef;
ArrayList<OrderDetails> billArrayList = new ArrayList<OrderDetails> ();
    List<String> header_list = new ArrayList<String> ();
    List<String> date_list = new ArrayList<String> ();
    int counter = 0;
    OrderDetails orderDetails;
    List<String> customer_list = new ArrayList<String> ();
    List<String> billnumber_list = new ArrayList<String> ();
    List<String> timeStamp_list = new ArrayList<String> ();
    List<Integer> finalBill_list = new ArrayList<Integer> ();
    List<String> payment_type_list = new ArrayList<String> ();
    List<String> orderstatus_list = new ArrayList<String> ();
    ArrayList<ItemDetails> itemDetails = new ArrayList<>();
    String orderstatus ;
    String billedDate;
    int flag = 0;
    ArrayList<ItemDetails>itemDetailsArrayList = new ArrayList<>() ;
    Map<String, List<String>> expandableListDetail = new HashMap<>();
    Map<String, List<String>> expandable_billList = new HashMap<> ();
    Map<String, List<String>> expandable_timeList = new HashMap<> ();
    Map<String, List<Integer>> expandable_finalBillList = new HashMap<> ();
    Map<String, List<String>> expandable_payment_detail = new HashMap<> ();
    Map<String, List<String>> expandable_orderStatus_detail = new HashMap<> ();
    ReceiptExpandableAdapter receiptAdapter;
    NavigationView navigationView;
    public static Menu menuNav;
    public static TextView textViewUsername;
    public static TextView textViewEmail;
    public static ImageView imageView;
    public static View mHeaderView;
    private NetworkStateReceiver networkStateReceiver;
    androidx.appcompat.app.AlertDialog alertDialog;
    ViewGroup viewGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);


        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(ORDERDETAILS);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(OrderTrackingActivity.this);
        navigationView.setCheckedItem(R.id.ordertracking);
        expandableListView = findViewById(R.id.expandableListView);

        navigationView.setNavigationItemSelectedListener(OrderTrackingActivity.this);
        LinearLayout linearLayout = (LinearLayout) navigationView.getHeaderView(0);

        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);
        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);


        textViewUsername.setText(DashBoardActivity.roleName);
        textViewEmail.setText(DashBoardActivity.userName);

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();


        orderList = findViewById(R.id.orderdetail_listview);
        orderdetailRef = CommonMethods.fetchFirebaseDatabaseReference(Constant.ORDER_DETAILS_FIREBASE_TABLE);


        orderdetailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {

                    billArrayList.clear ();
                    header_list.clear ();
                    counter = 0;
                    for (DataSnapshot detailsSnap : snapshot.getChildren()) {
                        orderDetails = detailsSnap.getValue(OrderDetails.class);
                        itemDetails = (ArrayList<ItemDetails>) orderDetails.getItemDetailList();
                        if (itemDetails.size() > 0 && itemDetails != null) {
                            for (int i = 0; i < itemDetails.size(); i++) {
                                Log.e("itemdetail",itemDetails.get(i).getItemName());

                                orderstatus =  itemDetails.get(i).getOrderStatus();
                                    billArrayList.add(orderDetails);
                                    header_list.add(orderDetails.getPaymentDate());
                                    date_list.add(orderDetails.getPaymentDate());

                            }
                        }
                    }

                    TextUtils.removeDuplicatesList ( header_list );
                    TextUtils.removeDuplicatesList(billArrayList);

                    if (header_list.size () > 0) {
                        Collections.reverse ( header_list );

                        for ( int i = 0; i < header_list.size (); i++ ) {
                            billedDate = header_list.get ( i );

                            for ( int j = 0; j < billArrayList.size (); j++ ) {
                                OrderDetails billDetailsData = billArrayList.get ( j );

                                if (billedDate.equalsIgnoreCase ( billDetailsData.getPaymentDate () )) {

                                    customer_list.add ( billDetailsData.getFullName () );
                                    billnumber_list.add ( billDetailsData.getOrderId () );
                                    timeStamp_list.add ( billDetailsData.getOrderTime () );
                                    Log.e("billdetailorderid",  billDetailsData.getOrderId ());



                                    finalBill_list.add ( billDetailsData.getPaymentamount () );
                                    payment_type_list.add ( billDetailsData.getPaymentType () );
                                    orderstatus_list.add ( billDetailsData.getOrderStatus() );
                                }
                            }
                            if (billArrayList != null) {

                                expandableListDetail.put ( header_list.get ( counter ), customer_list );
                                expandable_billList.put ( header_list.get ( counter ), billnumber_list );
                                expandable_finalBillList.put ( header_list.get ( counter ), finalBill_list );
                                expandable_timeList.put ( header_list.get ( counter ), timeStamp_list );
                                expandable_payment_detail.put ( header_list.get ( counter ), payment_type_list );
                                expandable_orderStatus_detail.put ( header_list.get ( counter ), orderstatus_list );
                                Collections.reverse ( customer_list );
                                Collections.reverse ( billnumber_list );
                                Collections.reverse ( finalBill_list );
                                Collections.reverse ( timeStamp_list );
                                Collections.reverse ( payment_type_list );
                                Collections.reverse ( orderstatus_list );
                                counter++;

                                customer_list = new ArrayList<> ();
                                billnumber_list = new ArrayList<> ();
                                payment_type_list = new ArrayList<> ();
                                finalBill_list = new ArrayList<> ();
                                timeStamp_list = new ArrayList<> ();
                                orderstatus_list = new ArrayList<> ();
                                flag = flag + 1;
                            }
                        }
                    }

                    receiptAdapter = new ReceiptExpandableAdapter ( OrderTrackingActivity.this, header_list, (HashMap<String, List<String>>) expandableListDetail,
                            (HashMap<String, List<String>>) expandable_billList,
                            (HashMap<String, List<String>>) expandable_timeList,
                            (HashMap<String, List<Integer>>) expandable_finalBillList,
                            (HashMap<String, List<String>>) expandable_payment_detail,
                            (HashMap<String, List<String>>) expandable_orderStatus_detail );


                    orderList.setAdapter ( receiptAdapter );
                    receiptAdapter.notifyDataSetChanged ();


                    orderList.setOnGroupExpandListener ( new ExpandableListView.OnGroupExpandListener () {

                        @Override
                        public void onGroupExpand(int groupPosition) {


                        }
                    });

                    orderList.setOnGroupCollapseListener ( new ExpandableListView.OnGroupCollapseListener () {

                        @Override
                        public void onGroupCollapse(int groupPosition) {

                        }
                    });



                    /*orderList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v,int  groupPosition, long id) {

                            return false;
                        }
                    });*/


                    orderList.setOnChildClickListener ( new ExpandableListView.OnChildClickListener () {
                        @Override
                        public boolean onChildClick(ExpandableListView parent, View v,
                                                    int groupPosition, int childPosition, long id) {

                            Intent intent = new Intent ( OrderTrackingActivity.this, OrderDetailsActivity.class );
                            intent.putExtra ( "billidnum", expandable_billList.get ( header_list.get ( groupPosition ) ).get ( childPosition ) );
                            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                            startActivity ( intent );
                            return false;
                        }
                    } );




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
        }else if (id == R.id.adminfare) {
            Intent intent = new Intent(getApplicationContext(), MaintainDeliveryFairActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else if (id == R.id.adminMonthlyPayments) {
            Intent intent = new Intent(getApplicationContext(), MonthlyPaymentSettlements.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else if (id == R.id.logoutscreen) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(OrderTrackingActivity.this);
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
                    Intent intent = new Intent(OrderTrackingActivity.this,
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
        } else if (id == R.id.ordertracking) {
            Intent intent = new Intent(getApplicationContext(), OrderTrackingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }


        else if (id == R.id.reviewApproval) {
            Intent intent = new Intent(getApplicationContext(), ItemRatingReviewApprovalActivity.class);
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
        if (!((Activity) OrderTrackingActivity.this).isFinishing()) {
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