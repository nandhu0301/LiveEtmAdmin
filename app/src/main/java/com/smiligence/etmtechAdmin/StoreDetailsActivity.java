package com.smiligence.etmtechAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmtechAdmin.bean.CategoryDetailsNew;
import com.smiligence.etmtechAdmin.bean.StoreTimings;
import com.smiligence.etmtechAdmin.bean.UserDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.NetworkStateReceiver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StoreDetailsActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener  {

    DatabaseReference databaseReference, storeMaintainenceRef, metrozStoreTimingRef;
    EditText storeNameEditText, firstname_storeEditText, lastNameEditText, selectedcategoryEditText, storeAddress,
            email_store, phonenumber_store, pincode_store, bankname, branchName_store, Accountnumber, ifsc_store, Business_type, aadharnumber, gstnumber_store;
    ImageView storeLogo, aadharImage ,gstImage;
    ArrayList<CategoryDetailsNew> categoryDetailsArrayList = new ArrayList<>();
    ArrayList<CategoryDetailsNew> categoryDetailsArrayList1 = new ArrayList<>();
    ImageView backButton;
    String getStoreID;
    AlertDialog dialog;
    Switch shopOpenCloseStatus;
    String pattern = "hh:mm aa";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    String currentTime;
    public static String DATE_FORMAT = "MMMM dd, yyyy";
    DateFormat date;
    Date currentLocalTime;
    String currentDateAndTime;
    String metrozStartTime;
    String metrozStopTime;
    androidx.appcompat.app.AlertDialog alertDialogForSuspenson;
    private NetworkStateReceiver networkStateReceiver;
    androidx.appcompat.app.AlertDialog alertDialog;
    ViewGroup viewGroup;


   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_details2);


       storeNameEditText = findViewById(R.id.storeName);
       firstname_storeEditText = findViewById(R.id.firstname_store);
       lastNameEditText = findViewById(R.id.lastName);
       selectedcategoryEditText = findViewById(R.id.selectedcategory);
       storeAddress = findViewById(R.id.storeAddress_store);
       email_store = findViewById(R.id.email_store);
       phonenumber_store = findViewById(R.id.phonenumber_store);
       pincode_store = findViewById(R.id.pincode_store);
       bankname = findViewById(R.id.bankname_store);
       branchName_store = findViewById(R.id.branchName_store);
       Accountnumber = findViewById(R.id.Accountnumber);
       ifsc_store = findViewById(R.id.ifsc_store);
       Business_type = findViewById(R.id.Business_type);
       aadharnumber = findViewById(R.id.aadharnumber);
       gstnumber_store = findViewById(R.id.gstnumber_store);
       storeLogo = findViewById(R.id.storelogo);
       aadharImage = findViewById(R.id.aadharID);
       gstImage = findViewById(R.id.gstid);
       backButton = findViewById(R.id.back_button);
       shopOpenCloseStatus = findViewById(R.id.openShopManuallySwitch);


       databaseReference = CommonMethods.fetchFirebaseDatabaseReference("SellerLoginDetails");
       storeMaintainenceRef =CommonMethods.fetchFirebaseDatabaseReference("storeTimingMaintenance");
       metrozStoreTimingRef = CommonMethods.fetchFirebaseDatabaseReference("MetrozstoreTiming");

       getStoreID = getIntent().getStringExtra("StoreId");
       Query query = databaseReference.child(getStoreID);

       viewGroup = findViewById(android.R.id.content);
       View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
       androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
       builder.setView(dialogView);
       alertDialog = builder.create();

       Calendar cal = Calendar.getInstance();
       currentLocalTime = cal.getTime();
       date = new SimpleDateFormat("HH:mm aa");
       DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
       currentDateAndTime = dateFormat.format(new Date());
       currentTime = date.format(currentLocalTime);

       backButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(StoreDetailsActivity.this, StoreHistory.class);
               intent.putExtra("StoreId", getStoreID);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);

           }
       });



       if (getStoreID != null && !"".equals(getStoreID)) {

           storeMaintainenceRef.child(String.valueOf(getStoreID)).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                               if (dataSnapshot1.getChildrenCount() > 0) {

                                   StoreTimings storeTimings = dataSnapshot1.getValue(StoreTimings.class);


                                   if (storeTimings.getStoreStatus().equalsIgnoreCase("Opened")) {
                                                       shopOpenCloseStatus.setChecked(true);
                                                   }
                                                   if (storeTimings.getStoreStatus().equalsIgnoreCase("Closed")) {
                                                       shopOpenCloseStatus.setChecked(false);
                                                   }

                               }

                               shopOpenCloseStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                   @Override
                                   public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                       if (b) {
                                           DatabaseReference startTimeDataRef = CommonMethods.fetchFirebaseDatabaseReference("storeTimingMaintenance").child(getStoreID);
                                           startTimeDataRef.child("storeStatus").setValue("Opened");
                                           startTimeDataRef.child("creationDate").setValue(currentDateAndTime);
                                       } else {
                                           DatabaseReference startTimeDataRef = CommonMethods.fetchFirebaseDatabaseReference("storeTimingMaintenance").child(getStoreID);
                                           startTimeDataRef.child("storeStatus").setValue("Closed");
                                           startTimeDataRef.child("creationDate").setValue(currentDateAndTime);
                                       }
                                   }
                               });

                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });

       }
       query.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);

                   if (!"".equalsIgnoreCase(userDetails.getStoreName()) && userDetails.getCategoryList() != null) {


                       String trimmedfirstName = userDetails.getFirstName ().replaceFirst("^\\s+", ""); // Remove leading space
                       String trimmedstorename = userDetails.getStoreName ().replaceFirst("^\\s+", ""); // Remove leading space
                       String trimmedlastName = userDetails.getLastName ().replaceFirst("^\\s+", ""); // Remove leading space
                       String trimmedemailid = userDetails.getEmail_Id ().replaceFirst("^\\s+", ""); // Remove leading space
                       String trimmedaddress = userDetails.getAddress ().replaceFirst("^\\s+", ""); // Remove leading space
                       String trimmedbankname = userDetails.getBankName ().replaceFirst("^\\s+", ""); // Remove leading space
                       String trimmedbranchname = userDetails.getBranchName ().replaceFirst("^\\s+", ""); // Remove leading space
                       String trimmedbusinnesstype = userDetails.getBusinessType ().replaceFirst("^\\s+", ""); // Remove leading space




                       storeNameEditText.setText(trimmedstorename);
                       firstname_storeEditText.setText(trimmedfirstName);
                       lastNameEditText.setText(trimmedlastName);


                       categoryDetailsArrayList = (ArrayList<CategoryDetailsNew>) userDetails.getCategoryList();


                       for (int i = 0; i < categoryDetailsArrayList.size(); i++) {
                           if (userDetails.getCategoryList().size() == 1) {
                               selectedcategoryEditText.append(categoryDetailsArrayList.get(i).getCategoryName() + ".");
                           } else {
                               selectedcategoryEditText.append(categoryDetailsArrayList.get(i).getCategoryName() + ",");
                           }
                       }


                       storeAddress.setText(trimmedaddress);
                       email_store.setText(trimmedemailid);
                       phonenumber_store.setText(userDetails.getPhoneNumber());
                       pincode_store.setText(userDetails.getPincode());
                       bankname.setText(trimmedbankname);
                       branchName_store.setText(trimmedbranchname);
                       Accountnumber.setText(userDetails.getAccountNumber());
                       ifsc_store.setText(userDetails.getIfscCode());
                       Business_type.setText(trimmedbusinnesstype);
                       aadharnumber.setText(userDetails.getAadharNumber());
                       gstnumber_store.setText(userDetails.getGstNumber());


                       Glide.with(StoreDetailsActivity.this).load(userDetails.getStoreLogo()).fitCenter().into(storeLogo);

                       Glide.with(StoreDetailsActivity.this).load(userDetails.getAadharImage()).fitCenter().into(aadharImage);

                       Glide.with(StoreDetailsActivity.this).load(userDetails.getGstCertificateImage()).fitCenter().into(gstImage);

                       aadharImage.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               AlertDialog.Builder mBuilder = new AlertDialog.Builder(StoreDetailsActivity.this);
                               View mView = getLayoutInflater().inflate(R.layout.seller_aadhar_dialog, null);
                               ImageView AadharImage = mView.findViewById(R.id.Aadharimage_store);
                               ImageView Cancel = mView.findViewById(R.id.cancelIcon);
                               TextView title = mView.findViewById(R.id.texttitle);

                               title.setText("Aadhar Image");
                               mBuilder.setView(mView);
                               dialog = mBuilder.create();
                               dialog.show();
                               dialog.setCancelable(false);

                               Cancel.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       dialog.dismiss();
                                   }
                               });


                               Glide.with(StoreDetailsActivity.this).load(userDetails.getAadharImage()).fitCenter().into(AadharImage);

                           }
                       });



                   }


               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
       }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(StoreDetailsActivity.this,StoreHistory.class);
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
        if (!((Activity) StoreDetailsActivity.this).isFinishing()) {
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
