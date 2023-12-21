package com.smiligence.etmtechAdmin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.smiligence.etmtechAdmin.Adapter.ItemOrderDetails;
import com.smiligence.etmtechAdmin.bean.ItemDetails;
import com.smiligence.etmtechAdmin.bean.OrderDetails;
import com.smiligence.etmtechAdmin.bean.StoreDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.Constant;

import java.util.ArrayList;

public class ViewItemDetailsCategoryActivity extends AppCompatActivity {
    //Order details
    TextView order_Date, orderTypeTxt, order_Id, order_Total, offerName, deductionAmount;
    //Payment details
    TextView card_Details, type_Of_Payment;
    //Shipping Address
    TextView fullName, shipping_Address; //pinocde;
    //Order Summary
    Uri  adaptermimageuri;
    String customerName, customerAddress, getPincodeCodeIntent;
    TextView amount, shipping, wholeCharge;
    ImageView itemImage, itemName;
    int finalBillAmount;

    ConstraintLayout orderDetailsLayout, paymentDetailsLayout, shippingAddressLayout, orderSummaryLayout, trackingDetailsLayout;
    RelativeLayout itemDetailsLayout, itemHeaderlayout;
    DatabaseReference databaseReference;
    ArrayList<ItemDetails> itemDetailList = new ArrayList<>();
    ImageView returnhome;
    private ArrayList<ItemDetails> openTicketItemList = new ArrayList<>();
    ItemOrderDetails itemOrderDetails;
    ListView listView;
    TextView order_status;
    TextView customerPhoneNumber;
    TextView storeNameText;
    TextView orderTimeTxt;
    String intentIndicator;
    TextView courierPartnerName, trackingId, trackingImage;
    String billId;
    TextView deliveryfeeText;
    //int taxPrice;
    String orderStatusText;
    OrderDetails orderDetails;
    ArrayList<ItemDetails> itemDetailsList1 = new ArrayList<>();
    RelativeLayout taxAmountCalculationLayout;
    View cgst_sgst_calculation_layout, igst_calculation_layout;
    TextView cgst_Textview, sgst_Textview, totalTaxAmount_cgst_sgst;
    TextView igst_Textview, totalTaxAmount_igst;
    //  float cgst_sgst_calculation = 0;
    boolean insideOrOutsideTag;
    int gstAmount;
    DatabaseReference storeDetailsDataRef;
    String orderIdForItemRatings;
    int taxPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item_details_category);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        returnhome = findViewById(R.id.redirecttohome);
        itemDetailsLayout = findViewById(R.id.itemdetailslayout);
        itemHeaderlayout = findViewById(R.id.itemdetailslayoutheader);
        intentIndicator = getIntent().getStringExtra("intentIndicator");
        billId = getIntent().getStringExtra("billId");

        storeDetailsDataRef = CommonMethods.fetchFirebaseDatabaseReference("SellerDetails");

        taxAmountCalculationLayout = findViewById(R.id.taxAmountCalculationLayout);

        cgst_sgst_calculation_layout = getLayoutInflater()
                .inflate(R.layout.cgst_sgst_order_details, taxAmountCalculationLayout, false);

        //cgst-sgst
        cgst_Textview = cgst_sgst_calculation_layout.findViewById(R.id.cgstTextview);
        sgst_Textview = cgst_sgst_calculation_layout.findViewById(R.id.sgstTextview);
        totalTaxAmount_cgst_sgst = cgst_sgst_calculation_layout.findViewById(R.id.totaltaxAmount_cgst_sgst);


        igst_calculation_layout = getLayoutInflater()
                .inflate(R.layout.igst_order_details, taxAmountCalculationLayout, false);
        igst_Textview = igst_calculation_layout.findViewById(R.id.igstTextview);
        totalTaxAmount_igst = igst_calculation_layout.findViewById(R.id.totalTaxAmount_igst);


        checkGPSConnection(getApplicationContext());


        returnhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intentIndicator == null && billId == null) {
                    Intent intent = new Intent(getApplicationContext(), ViewOrderActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                if (intentIndicator != null && billId != null) {

                    Intent intent = new Intent(getApplicationContext(), CustomerDetailsActivity.class);
                    intent.putExtra("intentIndicator", "fromCustomerOrderDetails");
                    intent.putExtra("billId", billId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }

            }
        });


        databaseReference = CommonMethods.fetchFirebaseDatabaseReference("OrderDetails");


        String getOrderIdValue = getIntent().getStringExtra("OrderidDetails");

        itemDetailsLayout = findViewById(R.id.itemdetailslayout);
        //Order details value
        orderDetailsLayout = findViewById(R.id.order_details_layout);

        // order_Date = orderDetailsLayout.findViewById(R.id.orderdate);
        order_Id = orderDetailsLayout.findViewById(R.id.bill_num);
        order_Total = orderDetailsLayout.findViewById(R.id.order_total);
        order_status = orderDetailsLayout.findViewById(R.id.order_status);
        orderTimeTxt = orderDetailsLayout.findViewById(R.id.ordertimetxt);
        orderTypeTxt = orderDetailsLayout.findViewById(R.id.orderTypeTxt);

        //Payment details
        paymentDetailsLayout = findViewById(R.id.payment_details);
        //card_Details = paymentDetailsLayout.findViewById ( R.id.card_details );
        type_Of_Payment = paymentDetailsLayout.findViewById(R.id.type_of_payment);

        //Shipping Address Details
        shippingAddressLayout = findViewById(R.id.shipping_details_layout);
        fullName = shippingAddressLayout.findViewById(R.id.full_name);
        shipping_Address = shippingAddressLayout.findViewById(R.id.address);
        customerPhoneNumber = shippingAddressLayout.findViewById(R.id.phoneNumber);

        //Order summary
        orderSummaryLayout = findViewById(R.id.cart_total_amount_layout);
        amount = orderSummaryLayout.findViewById(R.id.tips_price1);
        //  shipping = orderSummaryLayout.findViewById(R.id.tips_price);
        wholeCharge = orderSummaryLayout.findViewById(R.id.total_price);
        // giftPrice = orderSummaryLayout.findViewById(R.id.gift_price);
        offerName = orderSummaryLayout.findViewById(R.id.offerNameTextViewResult);
        deductionAmount = orderSummaryLayout.findViewById(R.id.deductionAmountTextViewResult);
        deliveryfeeText = orderSummaryLayout.findViewById(R.id.deliveryfeeText);


        //Special instruction layout

        //ItemDetails
        listView = itemDetailsLayout.findViewById(R.id.itemDetailslist);
        trackingDetailsLayout = findViewById(R.id.tracking_details_layout);
        courierPartnerName = trackingDetailsLayout.findViewById(R.id.courierPartnerNameEdt);
        trackingId = trackingDetailsLayout.findViewById(R.id.trackingIdEdt);

        trackingImage = trackingDetailsLayout.findViewById(R.id.order_time);


//        final Query getitemDetailsQuery = databaseReference.orderByChild("orderId").equalTo(String.valueOf(getOrderIdValue));
//
//        getitemDetailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot postdataSnapshot : dataSnapshot.getChildren()) {
//
//                    OrderDetails openTickets = postdataSnapshot.getValue(OrderDetails.class);
//                    openTicketItemList = (ArrayList<ItemDetails>) openTickets.getItemDetailList();
//
//                    orderStatusText = openTickets.getOrderStatus();
//                    Iterator itemIterator = openTicketItemList.iterator();
//                    //taxPrice=0;
//
//                    taxPrice = orderDetails.getPaymentamount() - (orderDetails.getPaymentamount() * 100) / (100 + gstAmount);
//
//                    Log.d("Result",taxPrice+"jj"+orderDetails.getPaymentamount() + "cc" + gstAmount);
//
//                    System.out.print("Result" + (orderDetails.getPaymentamount() + "cc" + gstAmount));
//                    cgst_sgst_calculation = taxPrice / 2;
//
//
//                    if (orderDetails.getInsideOrOutside() != null) {
//                        removeView();
//
//                        if (orderDetails.getInsideOrOutside().equals(Constant.TamilNadu)) {
//                            insideOrOutsideTag = true;
//                            taxAmountCalculationLayout.addView(cgst_sgst_calculation_layout);
//                            cgst_Textview.setText("₹ " + cgst_sgst_calculation);
//                            sgst_Textview.setText("₹ " + cgst_sgst_calculation);
//                            totalTaxAmount_cgst_sgst.setText("₹ " + taxPrice);
//                        } else {
//                            insideOrOutsideTag = false;
//                            taxAmountCalculationLayout.addView(igst_calculation_layout);
//
//                            igst_Textview.setText("₹ " + taxPrice);
//                            totalTaxAmount_igst.setText("₹ " + taxPrice);
//                        }
//                    }
//                    itemOrderDetails = new
//                            ItemOrderDetails(ViewItemDetailsCategoryActivity.this, openTicketItemList);
//                    itemOrderDetails.notifyDataSetChanged();
//                    listView.setAdapter(itemOrderDetails);
//
//                    if (listView != null) {
//                        int totalHeight = 0;
//                        for (int i = 0; i < itemOrderDetails.getCount(); i++) {
//                            View listItem = itemOrderDetails.getView(i, null, listView);
//                            listItem.measure(0, 0);
//                            totalHeight += listItem.getMeasuredHeight();
//                        }
//                        ViewGroup.LayoutParams params = listView.getLayoutParams();
//                        params.height = totalHeight + (listView.getDividerHeight() * (listView.getCount() - 1));
//                        listView.setLayoutParams(params);
//                        listView.requestLayout();
//                        listView.setAdapter(itemOrderDetails);
//                        itemOrderDetails.notifyDataSetChanged();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        storeDetailsDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    StoreDetails storeDetails = dataSnapshot.getValue(StoreDetails.class);

                    gstAmount = storeDetails.getGst();

                }
                final Query getitemDetailsQuery = databaseReference.orderByChild("orderId").equalTo(String.valueOf(getOrderIdValue));


                getitemDetailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postdataSnapshot : dataSnapshot.getChildren()) {

                            OrderDetails openTickets = postdataSnapshot.getValue(OrderDetails.class);
                            openTicketItemList = (ArrayList<ItemDetails>) openTickets.getItemDetailList();
                            orderIdForItemRatings = openTickets.getOrderId();
                            orderStatusText = openTickets.getOrderStatus();

                            // taxPrice = orderDetails.getPaymentamount() - (orderDetails.getPaymentamount() * 100) / (100 + gstAmount);

                            // Log.d("Result",taxPrice+"jj"+orderDetails.getPaymentamount() + "cc" + gstAmount);

                            System.out.print("Result" + (orderDetails.getPaymentamount() + "cc" + gstAmount));
                            // cgst_sgst_calculation = taxPrice / 2;


                            if (orderDetails.getInsideOrOutside() != null) {
                                removeView();

                                if (orderDetails.getInsideOrOutside().equals(Constant.TamilNadu)) {
                                    insideOrOutsideTag = true;
                                    taxAmountCalculationLayout.addView(cgst_sgst_calculation_layout);
                                    cgst_Textview.setText("₹ " + orderDetails.getCgstValue());
                                    sgst_Textview.setText("₹ " + orderDetails.getSgstValue());
                                    totalTaxAmount_cgst_sgst.setText("₹ " + orderDetails.getTotalTaxValue());
                                } else {
                                    insideOrOutsideTag = false;
                                    taxAmountCalculationLayout.addView(igst_calculation_layout);
                                    igst_Textview.setText("₹ " + orderDetails.getTotalTaxValue());
                                    totalTaxAmount_igst.setText("₹ " + orderDetails.getTotalTaxValue());
                                }
                            }
                            //taxAmount.setText("₹ " + (taxPrice));

                          /*  itemOrderDetails = new
                                    ItemOrderDetails(ViewItemDetailsCategoryActivity.this, openTicketItemList,"",ViewItemDetailsCategoryActivity.this,adaptermimageuri);
                            itemOrderDetails.notifyDataSetChanged();
                            listView.setAdapter(itemOrderDetails);*/
/*
                            if (listView != null) {
                                int totalHeight = 0;
                                for (int i = 0; i < itemOrderDetails.getCount(); i++) {
                                    View listItem = itemOrderDetails.getView(i, null, listView);
                                    listItem.measure(0, 0);
                                    totalHeight += listItem.getMeasuredHeight();
                                }
                                ViewGroup.LayoutParams params = listView.getLayoutParams();
                                params.height = totalHeight + (listView.getDividerHeight() * (listView.getCount() - 1));
                                listView.setLayoutParams(params);
                                listView.requestLayout();
                                listView.setAdapter(itemOrderDetails);
                                itemOrderDetails.notifyDataSetChanged();
                            }*/

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });
        final Query getOrderDetails = databaseReference.orderByChild("orderId").equalTo(getOrderIdValue);


        getOrderDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        orderDetails = dataSnapshot1.getValue(OrderDetails.class);
                        itemDetailsList1 = (ArrayList<ItemDetails>) orderDetails.getItemDetailList();

                        order_Id.setText("#" + orderDetails.getOrderNumberForFinancialYearCalculation());
                        order_Total.setText(" ₹" + String.valueOf(orderDetails.getPaymentamount()));
                        order_status.setText(orderDetails.getOrderStatus());
                        if (orderDetails.getOrderType() != null) {
                            if (orderDetails.getOrderType().equals("Retail")) {
                                orderTypeTxt.setText("Retail");
                            } else {
                                orderTypeTxt.setText("Online");
                            }
                        }

                        if (orderDetails.getCourierPartnerName() != null) {
                            courierPartnerName.setText(orderDetails.getCourierPartnerName());
                        }
                        if (orderDetails.getTrackingId() != null) {
                            trackingId.setText(orderDetails.getTrackingId());
                        }

                        trackingImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (orderDetails.getTrackingImage() != null) {
                                    if (!orderDetails.getTrackingImage().equals("")) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewItemDetailsCategoryActivity.this);
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });

                                        final AlertDialog dialog = builder.create();
                                        LayoutInflater inflater = getLayoutInflater();
                                        View dialogLayout = inflater.inflate(R.layout.tracking_image_layout, null);
                                        dialog.setView(dialogLayout);

                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.show();
                                        ImageView trackImage = (ImageView) dialog.findViewById(R.id.goProDialogImage);
                                        RequestOptions requestOptions = new RequestOptions();
                                        requestOptions.placeholder(R.mipmap.ic_launcher_new);
                                        requestOptions.error(R.mipmap.ic_launcher_new);
                                        Glide.with(ViewItemDetailsCategoryActivity.this)
                                                .setDefaultRequestOptions(requestOptions)
                                                .load(orderDetails.getTrackingImage()).fitCenter().into(trackImage);


                                    }
                                }

                            }
                        });


                        if (orderDetails.getDeliveryFee() == 0) {
                            deliveryfeeText.setText("Free");
                        } else {
                            deliveryfeeText.setText("₹ " + orderDetails.getDeliveryFee());
                        }

                        orderTimeTxt.setText(orderDetails.getOrderCreateDate());
                        if (orderDetails.getPaymentType() != null && !"".equals(orderDetails.getPaymentType())) {
                            String upperString = orderDetails.getPaymentType();
                            String newString = upperString.substring(0, 1).toUpperCase() + upperString.substring(1).toLowerCase();
                            type_Of_Payment.setText(newString);
                        } else {
                            type_Of_Payment.setText("-");
                        }


                        fullName.setText(" " + orderDetails.getFullName());
                        shipping_Address.setText(" " + orderDetails.getShippingaddress());
                        customerPhoneNumber.setText(" " + orderDetails.getCustomerPhoneNumber());
                        fullName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cus, 0, 0, 0);
                        shipping_Address.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_01, 0, 0, 0);
                        customerPhoneNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phonenumicon_01, 0, 0, 0);

                        amount.setText(" ₹" + String.valueOf(orderDetails.getTotalAmount()));
                        wholeCharge.setText(" ₹" + String.valueOf(orderDetails.getPaymentamount()));
                        offerName.setText(orderDetails.getDiscountName());
                        deductionAmount.setText(" - ₹" + String.valueOf(orderDetails.getDiscountAmount()));


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void removeView() {
        if (cgst_sgst_calculation_layout.getParent() != null) {
            ((ViewGroup) cgst_sgst_calculation_layout.getParent()).removeView(cgst_sgst_calculation_layout); // <- fix
        }
        if (igst_calculation_layout.getParent() != null) {
            ((ViewGroup) igst_calculation_layout.getParent()).removeView(igst_calculation_layout); // <- fix
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewItemDetailsCategoryActivity.this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void checkGPSConnection(Context context) {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!statusOfGPS)
            Toast.makeText(context.getApplicationContext(), "GPS is disable!", Toast.LENGTH_LONG).show();
    }

}