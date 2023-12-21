package com.smiligence.etmtechAdmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.smiligence.etmtechAdmin.Adapter.ItemOrderDetails;
import com.smiligence.etmtechAdmin.Interface.ItemClickListener;
import com.smiligence.etmtechAdmin.bean.ItemDetails;
import com.smiligence.etmtechAdmin.bean.OrderDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.Constant;
import com.smiligence.etmtechAdmin.common.DateUtils;
import com.smiligence.etmtechAdmin.common.NetworkStateReceiver;
import com.smiligence.etmtechAdmin.common.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity implements  NetworkStateReceiver.NetworkStateReceiverListener{

    ImageView backButton ;
    DatabaseReference orderdetailRef;
    String orderIdFromHistory ,orderstatus  ;
    int temp = 0;
    int total =0;
    OrderDetails orderDetails;
    ArrayList<ItemDetails> itemDetailsList1 = new ArrayList<ItemDetails> ();
    ArrayList<ItemDetails> itemDetailsList = new ArrayList<ItemDetails> ();
    ArrayList<ItemDetails> newitemDetailsList = new ArrayList<ItemDetails> ();
    ArrayList<OrderDetails> billDetailsArrayList = new ArrayList<OrderDetails> ();
    ItemDetails itemDetails;
    RelativeLayout itemDetailsLayout, itemHeaderlayout,shippingAddressLayout;
    boolean check = true;
    ItemOrderDetails itemOrderDetails;
  //  ListView listView;
    RecyclerView recyclerView ;
    TextView orderStatusText, order_status, customerPhoneNumber, storeNameText,deductionAmount,deliveryfeeText, orderTimeTxt, order_Date, order_Id,
            order_Total, type_Of_Payment, fullName, shipping_Address,offerName, noContactDelivery, anyInstructions, amount, shipping, wholeCharge, deliveryBoyName ,cgstTxt,sgstTxt ,totaltaxTxt;
    ConstraintLayout orderDetailsLayout, paymentDetailsLayout, orderSummaryLayout, specialinstructionLayout;
    //String sellerIdIntent,storeNameIntent,storeImageIntent;
    ImageView trackImage;
    TextView cgst_Textview, sgst_Textview, totalTaxAmount_cgst_sgst;
    Button chooseTrackImage;
    AlertDialog dialog;
    String statusChangeString;
    Uri mimageuri ;
    Intent intentImage = new Intent();
    final static int PICK_IMAGE_REQUEST = 100;
    ItemClickListener itemClickListener;
    StorageReference Storaferef  ;
    StorageReference StorageRef;
    StorageTask profile_StorageTask;
    Uri  profile_DownloadUrl;
    StorageReference imageFileStorageRef  ;
    ProgressDialog progressdialog;
    int count=0;
    public int deliverycount = 0;
    private NetworkStateReceiver networkStateReceiver;
    androidx.appcompat.app.AlertDialog alertDialog;
    ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        backButton = findViewById ( R.id.back );

        //Order details value
        orderDetailsLayout = findViewById ( R.id.order_details_layout );
        order_Date = orderDetailsLayout.findViewById ( R.id.orderdate );
        order_Id = orderDetailsLayout.findViewById ( R.id.bill_num );
        order_Total = orderDetailsLayout.findViewById ( R.id.order_total );
        order_status = orderDetailsLayout.findViewById ( R.id.order_status );
        orderTimeTxt = orderDetailsLayout.findViewById ( R.id.ordertimetxt );

      cgstTxt = orderDetailsLayout.findViewById(R.id.CGSTtextview);
        sgstTxt = orderDetailsLayout.findViewById(R.id.SGSTtextview);
        totaltaxTxt =orderDetailsLayout.findViewById(R.id.toaltaxtextview);


        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

        itemDetailsLayout = findViewById ( R.id.itemdetailslayout );
        //ItemDetails
      //  listView = itemDetailsLayout.findViewById ( R.id.itemDetailslist );
        recyclerView = itemDetailsLayout.findViewById ( R.id.itemDetailslist );
        itemHeaderlayout = findViewById ( R.id.itemdetailslayoutheader );


        //ItemHeaderlayout
        storeNameText = itemHeaderlayout.findViewById ( R.id.storeName );

        orderIdFromHistory = getIntent ().getStringExtra ( "billidnum" );
        orderdetailRef = CommonMethods.fetchFirebaseDatabaseReference("OrderDetails");
        StorageRef = CommonMethods.fetchFirebaseStorageReference("Images").child("Trackingimages");

        //Shipping Address Details
        shippingAddressLayout = findViewById ( R.id.shipping_details_layout );
        fullName = shippingAddressLayout.findViewById ( R.id.full_name );
        shipping_Address = shippingAddressLayout.findViewById ( R.id.address );
        customerPhoneNumber = shippingAddressLayout.findViewById ( R.id.phoneNumber );

        //Payment details
        paymentDetailsLayout = findViewById ( R.id.payment_details );
        type_Of_Payment = paymentDetailsLayout.findViewById ( R.id.type_of_payment );

//Order summary
        orderSummaryLayout = findViewById(R.id.cart_total_amount_layout);
        amount = orderSummaryLayout.findViewById(R.id.tips_price1);

        wholeCharge =     orderSummaryLayout.findViewById(R.id.total_price);
        offerName =       orderSummaryLayout.findViewById(R.id.offerNameTextViewResult);
        deductionAmount = orderSummaryLayout.findViewById(R.id.deductionAmountTextViewResult);
        deliveryfeeText = orderSummaryLayout.findViewById(R.id.deliveryfeeText);


       /* if (!"".equals(SellerProfileActivity.saved_id) && SellerProfileActivity.saved_id != null && !"".equals(SellerProfileActivity.saved_customerPhonenumber) || SellerProfileActivity.saved_customerPhonenumber != null && !"".equals(SellerProfileActivity.storeImage) && SellerProfileActivity.storeImage != null) {
            sellerIdIntent = SellerProfileActivity.saved_id;
            storeNameIntent = SellerProfileActivity.storeName;
            storeImageIntent = SellerProfileActivity.storeImage;
        }*/


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);



        backButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( OrderDetailsActivity.this, OrderTrackingActivity.class );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );

            }
        } );

        loadParticularStoredetails();
    }

    public void loadParticularStoredetails(){

        Query storebasedQuery = orderdetailRef.orderByChild ( "orderId" ).equalTo ( String.valueOf ( orderIdFromHistory ) );

        storebasedQuery.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemDetailsList.clear ();
                if (check ) {
                    if (count == 0){

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            orderDetails = dataSnapshot1.getValue(OrderDetails.class);
                            billDetailsArrayList.add(orderDetails);

                            itemDetailsList = (ArrayList<ItemDetails>) orderDetails.getItemDetailList();


                            if (itemDetailsList.size() > 0) {
                                for (int i = 0; i < itemDetailsList.size(); i++) {
                                    //  if (itemDetailsList.get(i).getStoreName().equals(storeNameIntent)){
                                    newitemDetailsList.add(itemDetailsList.get(i));
                                    total = total + itemDetailsList.get(i).getTotalItemQtyPrice();
                                    orderstatus = itemDetailsList.get(i).getOrderStatus();
                                    Log.i("newitemDetailsList", "" + itemDetailsList.get(i));
                                    // }
                                }
                            }
                            TextUtils.removeDuplicatesList(newitemDetailsList);
                        }

                    count = 1;
                    order_Date.setText(orderDetails.getPaymentDate());
                    order_Id.setText(orderDetails.getOrderId());
                    order_Total.setText(" ₹" + String.valueOf(orderDetails.getPaymentamount()));
                    order_status.setText(orderDetails.getOrderStatus());
                    storeNameText.setText(orderDetails.getStoreName());
                    orderTimeTxt.setText(orderDetails.getOrderTime());
                    String  paymenttype = orderDetails.getPaymentType();
                    String paymentType = paymenttype.substring(0, 1).toUpperCase() + paymenttype.substring(1);
                    type_Of_Payment.setText(paymentType);
                    wholeCharge.setText(" ₹" + (orderDetails.getPaymentamount()));
                    offerName.setText(orderDetails.getDiscountName());
                    deductionAmount.setText(" - ₹" + (orderDetails.getDiscountAmount()));
                    amount.setText(" ₹" + (orderDetails.getTotalAmount()));
                    deliveryfeeText.setText(" ₹" + String.valueOf(orderDetails.getDeliveryFee()));
                    cgstTxt.setText("₹ " + orderDetails.getCgstValue());
                    sgstTxt.setText("₹ " + orderDetails.getSgstValue());
                    totaltaxTxt.setText("₹ " + orderDetails.getTotalTaxValue());

                    itemOrderDetails = new ItemOrderDetails(OrderDetailsActivity.this, newitemDetailsList, orderDetails.getOrderId());
                    recyclerView.setAdapter(itemOrderDetails);
                    itemOrderDetails.notifyDataSetChanged();


                    itemOrderDetails.setOnItemclickListener(new ItemOrderDetails.OnItemClicklistener() {
                        @Override
                        public void Onitemclick(int Position, String orderid) {

                            if (newitemDetailsList.get(Position).getOrderStatus().equals("Order Placed")) {


                                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(OrderDetailsActivity.this);
                                final View customLayout = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.status_change_popup, null);
                                builder.setView(customLayout);

                                ImageView cancel = customLayout.findViewById(R.id.cancelpop);
                                TextView orderidtxt = customLayout.findViewById(R.id.customerorderidtextdialog);
                                TextView storeName = customLayout.findViewById(R.id.cusnametextdialog);
                                TextView amount = customLayout.findViewById(R.id.amounttextdialog);
                                EditText courierPartnerNameEdt = customLayout.findViewById(R.id.courierPartnerName);
                                EditText trackingId = customLayout.findViewById(R.id.trackingId);
                                RelativeLayout trackingidRelativeLayout = customLayout.findViewById(R.id.trackingIdLayout);
                                RelativeLayout courierPartnerRelativeLayout = customLayout.findViewById(R.id.courierPartnerNameLayout);
                                RelativeLayout trackingLayout = customLayout.findViewById(R.id.trackingLayout);
                                Button chooseTrackImage = customLayout.findViewById(R.id.choose_track_Image);
                                trackImage = customLayout.findViewById(R.id.trackImage);
                                Button ok = customLayout.findViewById(R.id.buttonok);
                                androidx.appcompat.app.AlertDialog dialog = builder.create();


                                dialog.show();
                                dialog.setCancelable(false);

                                storeName.setText((newitemDetailsList.get(Position).getStoreName()));
                                amount.setText(String.valueOf((newitemDetailsList.get(Position).getTotalItemQtyPrice())));
                                orderidtxt.setText(orderid);


                                chooseTrackImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openFileChooser();
                                        startActivityForResult(intentImage, 100);
                                    }
                                });


                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                        if (courierPartnerNameEdt.getText().toString().trim().equals("") || courierPartnerNameEdt.getText().toString().trim().isEmpty()) {
                                            courierPartnerNameEdt.setError(Constant.REQUIRED);
                                            return;
                                        } else if (courierPartnerNameEdt.getText().toString().trim().length() < 3) {
                                            courierPartnerNameEdt.setError("Minimum 3 characters required");
                                            return;
                                        } else if (!(0 <= "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnoqrstuvwxyz".indexOf(courierPartnerNameEdt.getText().toString().trim().charAt(0)))) {
                                            courierPartnerNameEdt.setError("Courirer Partner Name Must be starts with Alphabets");
                                            return;
                                        } else if (trackingId.getText().toString().equals("") || trackingId.getText().toString().isEmpty()) {
                                            trackingId.setError(Constant.REQUIRED);
                                            return;
                                        } else if (trackingId.getText().toString().trim().length() < 3) {
                                            trackingId.setError("Minimum 3 characters required");
                                            return;
                                        } else if (mimageuri == null) {
                                            Toast.makeText(OrderDetailsActivity.this, "Please add tracking image", Toast.LENGTH_SHORT).show();
                                            return;
                                        } else {

                                            if ("Order Placed".equalsIgnoreCase(newitemDetailsList.get(Position).getOrderStatus())) {
                                                statusOrderplaced(newitemDetailsList.get(Position).getOrderStatus(), courierPartnerNameEdt.getText().toString().trim(), trackingId.getText().toString().trim(), storeName.getText().toString().trim(), orderid, mimageuri, dialog);
                                            }

                                        }

                                    }
                                });


                            } else if (newitemDetailsList.get(Position).getOrderStatus().equals("Your Order is Shipped")) {

                                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(OrderDetailsActivity.this, R.style.CustomAlertDialog);
                                final View customLayout = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.trackingdetail_layout, null);
                                builder.setView(customLayout);

                                final androidx.appcompat.app.AlertDialog dialog = builder.create();
                                dialog.show();

                                dialog.setCancelable(false);
                                Button cancel = customLayout.findViewById(R.id.cancel_btn);
                                Button okay = customLayout.findViewById(R.id.yes_btn);

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });

                                okay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        progressdialog = new ProgressDialog(OrderDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
                                        progressdialog.setMessage("Loading");
                                        progressdialog.setCanceledOnTouchOutside(false);
                                        progressdialog.show();
                                        Log.e("Shipped", "shippedad");
                                        if ("Your Order is Shipped".equalsIgnoreCase(newitemDetailsList.get(Position).getOrderStatus())) {
                                            statusPickup(newitemDetailsList.get(Position).getOrderStatus(), newitemDetailsList.get(Position).getStoreName(), orderid, dialog);

                                        }
                                    }
                                });


                            } else if (newitemDetailsList.get(Position).getOrderStatus().equals("Delivered")) {

                                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(OrderDetailsActivity.this, R.style.CustomAlertDialog);
                                final View customLayout = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.viewtrackingdetails_layout, null);

                                builder.setView(customLayout);
                                androidx.appcompat.app.AlertDialog dialog = builder.create();
                                dialog.show();

                                TextView deliverystatus = customLayout.findViewById(R.id.delivery_status);
                                TextView courierstatus = customLayout.findViewById(R.id.courierdelivery_status);
                                TextView trackingid = customLayout.findViewById(R.id.couriertrackingdelivery_status);
                                ImageView trackingimage = customLayout.findViewById(R.id.trackingimage);

                                deliverystatus.setText(newitemDetailsList.get(Position).getOrderStatus());
                                courierstatus.setText(newitemDetailsList.get(Position).getCourierName());
                                trackingid.setText(newitemDetailsList.get(Position).getTrackingId());
                                Glide.with(getApplicationContext()).load(newitemDetailsList.get(Position).getTrackingimage()).into(trackingimage);

                            }

                        }
                    });

                    fullName.setText(" " + orderDetails.getFullName());
                    shipping_Address.setText(" " + orderDetails.getShippingaddress());
                    customerPhoneNumber.setText(" " + orderDetails.getCustomerPhoneNumber());
                    fullName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cus, 0, 0, 0);
                    shipping_Address.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_01, 0, 0, 0);
                    customerPhoneNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phonenumicon_01, 0, 0, 0);

                }

            }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


    }

 public void statusOrderplaced(String status, String courieredt, String trackingid, String storeNameIntent, String orderId, Uri imageuri,androidx.appcompat.app.AlertDialog dialog) {
     ArrayList<String> newarraykey = new ArrayList<>();
        if (status.equals ( "Order Placed" )) {
            DatabaseReference ref = CommonMethods.fetchFirebaseDatabaseReference ( "OrderDetails" ).child ( String.valueOf ( orderId ) ).child("itemDetailList");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        newarraykey.clear();
                        for (DataSnapshot updatedeliverysnap : snapshot.getChildren()) {
                            ItemDetails itemDetails = updatedeliverysnap.getValue(ItemDetails.class);

                            if (storeNameIntent.equals(itemDetails.getStoreName())) {
                                String key = updatedeliverysnap.getKey();
                                Log.e("KeySnap", key);
                                newarraykey.add(key);

                                if (imageuri != null) {
                                    imageFileStorageRef = StorageRef.child("Image" + System.currentTimeMillis() + "." + getExtenstion(imageuri));

                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                    byte[] data = baos.toByteArray();

                                    profile_StorageTask = imageFileStorageRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!urlTask.isSuccessful()) ;
                                            profile_DownloadUrl = urlTask.getResult();

                                          /*  DatabaseReference changeref = CommonMethods.fetchFirebaseDatabaseReference("OrderDetails").child(String.valueOf(orderIdFromHistory)).child("itemDetailList").child(key);
                                            changeref.child("orderStatus").setValue("Your Order is Shipped");
                                            changeref.child("courierName").setValue(courieredt);
                                            changeref.child("trackingId").setValue(trackingid);
                                            changeref.child("trackingimage").setValue(profile_DownloadUrl.toString());
                                            dialog.dismiss();

                                           // newitemDetailsList.clear();
                                           itemOrderDetails.notifyDataSetChanged ();*/

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                                }

                            }
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < newarraykey.size(); i++) {
                                    Log.e("newarraykey.size()", "one");
                                    DatabaseReference changeref = CommonMethods.fetchFirebaseDatabaseReference("OrderDetails").child(String.valueOf(orderIdFromHistory)).child("itemDetailList").child(newarraykey.get(i));
                                    changeref.child("orderStatus").setValue("Your Order is Shipped");
                                    changeref.child("courierName").setValue(courieredt);
                                    changeref.child("trackingId").setValue(trackingid);
                                    changeref.child("deliveryby").setValue("Admin");
                                    changeref.child("trackingimage").setValue(profile_DownloadUrl.toString());

                                    if (newarraykey.size()-1 == i) {
                                        deliverydialogcancelfun(progressdialog,dialog);
                                        itemOrderDetails.notifyDataSetChanged();
                                    }
                                }
                            }
                        }, 2000);

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            progressdialog = new ProgressDialog(OrderDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
            progressdialog.setMessage("Loading");
            progressdialog.setCanceledOnTouchOutside(false);
            progressdialog.show();

        }

    }



    public void statusPickup(String pickupDtatus, String storeNameIntent, String orderId, androidx.appcompat.app.AlertDialog dialog) {
        ArrayList<String> deliverystatusarray = new ArrayList<>();
        if (pickupDtatus.equals ( "Your Order is Shipped" )) {

            DatabaseReference refdeliverted = CommonMethods.fetchFirebaseDatabaseReference ( "OrderDetails" ).child ( String.valueOf ( orderId ) ).child("itemDetailList");
            refdeliverted.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()){
                        deliverystatusarray.clear();
                        for (DataSnapshot updatedeliverysnap : snapshot.getChildren()){
                            ItemDetails itemDetails = updatedeliverysnap.getValue(ItemDetails.class);
                            if (storeNameIntent.equals(itemDetails.getStoreName())){
                                String key = updatedeliverysnap.getKey();
                                deliverystatusarray.add(key);

                            }
                        }
                        for (int i=0 ;i< deliverystatusarray.size();i++){
                            DatabaseReference changeref = CommonMethods.fetchFirebaseDatabaseReference ( "OrderDetails" ).child ( String.valueOf ( orderIdFromHistory ) ).child("itemDetailList").child(deliverystatusarray.get(i));
                            changeref.child ( "orderStatus" ).setValue ( "Delivered" );
                            changeref.child("deliveryDate").setValue(DateUtils.fetchCurrentDateAndTime());


                            if (deliverystatusarray.size()-1 == i) {
                                deliverydialogcancelfun(progressdialog,dialog);
                                statusfunction(orderId);

                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }


    public void deliverydialogcancelfun(ProgressDialog progressdialog , androidx.appcompat.app.AlertDialog dialog){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressdialog.dismiss();
                dialog.dismiss();
                startActivity(getIntent());

            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent ( OrderDetailsActivity.this, OrderTrackingActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );

    }



    private String getExtenstion(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void openFileChooser() {
        intentImage = new Intent();
        intentImage.setType("image/*");
        intentImage.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intentImage.setAction(Intent.ACTION_GET_CONTENT);
    }



    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mimageuri = data.getData();
            Glide.with(getApplicationContext()).load(mimageuri).into(trackImage);


        }



    }

    public void statusfunction(String orderId){

        orderdetailRef.child(orderId).child("itemDetailList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long count = snapshot.getChildrenCount();
                    deliverycount = 0;

                    for (DataSnapshot itemsnap : snapshot.getChildren()) {

                        ItemDetails itemDetails = itemsnap.getValue(ItemDetails.class);

                        if (itemDetails.getOrderStatus().equals("Delivered")) {
                            deliverycount = deliverycount + 1;
                        }

                    }


                    if (deliverycount == count) {
                        orderdetailRef.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                OrderDetails orderDetails = dataSnapshot.getValue(OrderDetails.class);

                                if (!orderDetails.getOrderStatus().equals("Completed")) {
                                    orderdetailRef.child(orderId).child("orderStatus").setValue("Completed");
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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
        if (!((Activity) OrderDetailsActivity.this).isFinishing()) {
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



