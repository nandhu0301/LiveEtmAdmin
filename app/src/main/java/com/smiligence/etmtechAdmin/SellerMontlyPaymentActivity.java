package com.smiligence.etmtechAdmin;

import static com.smiligence.etmtechAdmin.common.Constant.ORDER_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.PAYMENTS;
import static com.smiligence.etmtechAdmin.common.Constant.RECEIPT_SETTLEMENT;
import static com.smiligence.etmtechAdmin.common.Constant.SELLER_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.SELLER_PAYMENTS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.smiligence.etmtechAdmin.Adapter.SellerMonthlySettlementAdapter;
import com.smiligence.etmtechAdmin.bean.ItemDetails;
import com.smiligence.etmtechAdmin.bean.OrderDetails;
import com.smiligence.etmtechAdmin.bean.PaymentDetails;
import com.smiligence.etmtechAdmin.bean.SellerPaymentDetails;
import com.smiligence.etmtechAdmin.bean.UserDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.Constant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SellerMontlyPaymentActivity extends AppCompatActivity {

    ListView list_details;
    ImageView backIcon;
    String firstMonthdate, lastMonthdate;
    DatabaseReference orderdetailRef ,sellerDataRef ,sellerPaymentsRef ,paymentRef;
    OrderDetails orderDetails;
    ArrayList<ItemDetails> itemDetailsArrayList = new ArrayList<>();
    String getStoreID;
    int resultTotalAmount = 0;
    int totalbilledAmount =0;
    int admindeliveryAmount = 0;
    int totaldeliveryAmount =0;
    int sellerdeliveryAmount = 0;
    final ArrayList<String> orderListSize = new ArrayList<String>();
    final ArrayList<String> list = new ArrayList<String>();
    TextView totalSalesAmount ,totalAmountInfo;
    String storeType;
    Date currentLocalTime;
    int Percentage;
    ArrayList<PaymentDetails> paymentDetailsArrayList = new ArrayList<PaymentDetails>();
    PaymentDetails sellerPaymentDetailsConstant;
    DateFormat date;
    String currentDateAndTime;
    String pattern = "dd-MM-yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    AlertDialog.Builder dialogBuilder ;
    TextView heading_txtview ,enddate_textview,totalbillamount_textview;
    ImageView cancelDialog;
    Button chooseeReceipt, changePaymentStatus;
    ImageView receiptImage;
    ListView cashExpenselistview ;
    private static final int PICK_IMAGE_REQUEST = 1;
    boolean check = true;
    ArrayList<OrderDetails> orderDetailsArrayList = new ArrayList<>();
    ArrayList<ItemDetails> itemDetailsArrayListHere = new ArrayList<>();
    int totalAmountTemp = 0;
    AlertDialog cashDialog;
    SweetAlertDialog pDialog;
    Uri storeImageUri;
    StorageReference receiptReference;
    private StorageTask mItemStorageTask;
    RadioGroup radioGroup;
    EditText amount_edittext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_montly_payment);

        list_details = findViewById(R.id.list_details);
        backIcon = findViewById(R.id.back);
        totalSalesAmount = findViewById(R.id.totalSalesamount);

        orderdetailRef = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_TABLE);
        sellerDataRef =CommonMethods.fetchFirebaseDatabaseReference(SELLER_DETAILS_TABLE);
        sellerPaymentsRef = CommonMethods.fetchFirebaseDatabaseReference(SELLER_PAYMENTS);
        paymentRef = CommonMethods.fetchFirebaseDatabaseReference(PAYMENTS);
        receiptReference = CommonMethods.fetchFirebaseStorageReference(RECEIPT_SETTLEMENT);
        getStoreID = getIntent().getStringExtra("StoreId");


        list.clear();

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

        orderdetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot itemSnap : snapshot.getChildren()) {
                        orderDetails = itemSnap.getValue(OrderDetails.class);
                        if (orderDetails.getOrderStatus().equals("Completed")){


                            itemDetailsArrayList = (ArrayList<ItemDetails>) orderDetails.getItemDetailList();

                            if (itemDetailsArrayList.size() > 0 && itemDetailsArrayList != null) {
                            for(int k=0;k< itemDetailsArrayList.size();k++){
                                if (itemDetailsArrayList.get(k).getSellerId().equals(getStoreID)){
                                    Log.e("itemDetailsArrayListseller",itemDetailsArrayList.get(k).getSellerId());


                         //   if (itemDetailsArrayList.get(0).getSellerId().equals(getStoreID)) {


                                for (int i = 0; i < list.size(); i++) {
                                    if (orderDetails.getFormattedDate().equals(list.get(i))) {
                                        if (itemDetailsArrayList.get(k).getDeliveryby().equals("Admin")) {
                                            admindeliveryAmount = admindeliveryAmount + 100;
                                        } else {
                                            sellerdeliveryAmount = sellerdeliveryAmount + 100;
                                        }

                                        totaldeliveryAmount = totaldeliveryAmount + 100;
                                        totalbilledAmount = totalbilledAmount +itemDetailsArrayList.get(k).getTotalItemQtyPrice() ;

                                        resultTotalAmount = resultTotalAmount + itemDetailsArrayList.get(k).getTotalItemQtyPrice() +100;
                                        orderListSize.add(orderDetails.getOrderId());

                                    }
                                }
                                }

                            }
                        }
                    }
                }

                    totalSalesAmount.setText("₹ " + resultTotalAmount);

                    DatabaseReference startTimeDataRef = CommonMethods.fetchFirebaseDatabaseReference(PAYMENTS).child(getStoreID).child(firstMonthdate);
                    startTimeDataRef.child("startDate").setValue(firstMonthdate);
                    startTimeDataRef.child("endDate").setValue(lastMonthdate);
                    startTimeDataRef.child("totalBilledAmount").setValue(totalbilledAmount);
                    startTimeDataRef.child("totalAmount").setValue(resultTotalAmount);
                    startTimeDataRef.child("paymentStatus").setValue("Pending");
                    startTimeDataRef.child("orderCount").setValue(String.valueOf(orderListSize.size()));
                    startTimeDataRef.child("totaldeliveryamount").setValue(totaldeliveryAmount);
                    startTimeDataRef.child("admindeliveryAmount").setValue(admindeliveryAmount);
                    startTimeDataRef.child("sellerdeliveryAmount").setValue(sellerdeliveryAmount);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sellerDataRef.child(getStoreID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                storeType = userDetails.getPaymentType();
                if (storeType == null) {
                    storeType = "Basic";
                }
                if (storeType != null) {
                    sellerPaymentsRef.child(storeType).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount() > 0) {
                                SellerPaymentDetails sellerPaymentDetails = snapshot.getValue(SellerPaymentDetails.class);
                                Percentage = sellerPaymentDetails.getPercentage();
                            }
                            paymentRef.child(getStoreID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getChildrenCount() > 0) {
                                        paymentDetailsArrayList.clear();
                                        for (DataSnapshot snap : snapshot.getChildren()) {
                                            PaymentDetails paymentDetails = snap.getValue(PaymentDetails.class);
                                            paymentDetailsArrayList.add(paymentDetails);
                                            Collections.reverse(paymentDetailsArrayList);
                                        }
                                        SellerMonthlySettlementAdapter paymentAdapter = new SellerMonthlySettlementAdapter(SellerMontlyPaymentActivity.this, paymentDetailsArrayList, storeType, Percentage);
                                        list_details.setAdapter(paymentAdapter);
                                        paymentAdapter.notifyDataSetChanged();
                                        if (paymentAdapter != null) {
                                            int totalHeight = 0;
                                            for (int i = 0; i < paymentAdapter.getCount(); i++) {
                                                View listItem = paymentAdapter.getView(i, null, list_details);
                                                listItem.measure(0, 0);
                                                totalHeight += listItem.getMeasuredHeight();
                                            }
                                            ViewGroup.LayoutParams params = list_details.getLayoutParams();
                                            params.height = totalHeight + (list_details.getDividerHeight() * (paymentAdapter.getCount() - 1));
                                            list_details.setLayoutParams(params);
                                            list_details.requestLayout();
                                            list_details.setAdapter(paymentAdapter);
                                            paymentAdapter.notifyDataSetChanged();
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        list_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sellerPaymentDetailsConstant = paymentDetailsArrayList.get(i);
                Calendar cal = Calendar.getInstance();
                currentLocalTime = cal.getTime();
                date = new SimpleDateFormat(pattern);
                DateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT_YYYYMD);
                currentDateAndTime = dateFormat.format(new Date());
                try {
                    if (sdf.parse(currentDateAndTime).compareTo(sdf.parse(sellerPaymentDetailsConstant.getEndDate())) == 1) {

                        if ( sellerPaymentDetailsConstant.getPaymentStatus().equals("Pending") )  {
                            dialogBuilder = new AlertDialog.Builder(SellerMontlyPaymentActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            final View dialogView = inflater.inflate(R.layout.bill_details, null);
                            dialogBuilder.setView(dialogView);

                            radioGroup = dialogView.findViewById(R.id.radioGroup);
                            heading_txtview = dialogView.findViewById(R.id.textview_amt);
                            chooseeReceipt = dialogView.findViewById(R.id.uploadReceipt);
                            enddate_textview = dialogView.findViewById(R.id.enddate_txt);
                            totalbillamount_textview = dialogView.findViewById(R.id.totalamount_txt);
                            changePaymentStatus = dialogView.findViewById(R.id.changePaymentStatusButton);
                            receiptImage = dialogView.findViewById(R.id.receiptImageview);
                            amount_edittext = dialogView.findViewById(R.id.edttext_amount);
                            cancelDialog = dialogView.findViewById(R.id.Cancel);


                            totalbillamount_textview.setText("Start Date:"+sellerPaymentDetailsConstant.getStartDate());
                            enddate_textview.setText("End Date:"+sellerPaymentDetailsConstant.getEndDate());


                            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                    switch(i)
                                    {
                                        case R.id.radiocheque:
                                            heading_txtview.setText("Enter Cheque Amount");

                                         break;
                                        case R.id.radioonline:
                                            heading_txtview.setText("Enter Online Transaction Amount");
                                            break;
                                    }
                                }
                            });


                            chooseeReceipt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                                }
                            });

/*
                            orderdetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (check) {
                                        if (snapshot.getChildrenCount() > 0) {
                                            orderDetailsArrayList.clear();
                                            itemDetailsArrayListHere.clear();
                                            totalAmountTemp = 0;

                                            for (DataSnapshot resSnap : snapshot.getChildren()) {
                                                orderDetails = resSnap.getValue(OrderDetails.class);
                                                itemDetailsArrayListHere = (ArrayList<ItemDetails>) orderDetails.getItemDetailList();

                                                DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                                                List<Date> dates = getDates(sellerPaymentDetailsConstant.getStartDate(), sellerPaymentDetailsConstant.getEndDate());
                                                for (Date date : dates) {
                                                    if (orderDetails.getFormattedDate().equals(df1.format(date))) {
                                                        if (orderDetails.getOrderStatus().equals("Delivered")) {
                                                            if (itemDetailsArrayListHere.get(0).getSellerId().equals(getStoreID)) {

                                                                orderDetailsArrayList.add(orderDetails);
                                                                totalAmountTemp = totalAmountTemp + orderDetails.getTotalAmount();
                                                            }
                                                        }
                                                    }
                                                   */
/* totalAmountInfo.setText("₹ " + totalAmountTemp);
                                                    BillDetailsAdapter billDetailsAdapter = new BillDetailsAdapter(SellerMontlyPaymentActivity.this, orderDetailsArrayList,"Seller");
                                                    cashExpenselistview.setAdapter(billDetailsAdapter);*//*

                                                }
                                            }
                                            if (sellerPaymentDetailsConstant.getTotalAmount() == 0) {

                                                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(SellerMontlyPaymentActivity.this, SweetAlertDialog.ERROR_TYPE);
                                                sweetAlertDialog.setCancelable(false);
                                                sweetAlertDialog.setTitleText("No more orders taken for this Month").show();
                                            } else {
                                                cashDialog = dialogBuilder.create();
                                                cashDialog.show();
                                            }

                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
*/
                            cashDialog = dialogBuilder.create();
                            cashDialog.show();

                            changePaymentStatus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int selectedId = radioGroup.getCheckedRadioButtonId();
                                    if (selectedId == -1) {
                                        Toast.makeText(SellerMontlyPaymentActivity.this, "Please select Cheque or Online", Toast.LENGTH_SHORT).show();
                                    }else if (amount_edittext.getText().toString()!=null && amount_edittext.getText().toString().equals("")){
                                        Toast.makeText(SellerMontlyPaymentActivity.this, "Please enter amount", Toast.LENGTH_SHORT).show();

                                    }else if (storeImageUri != null) {
                                            pDialog = new SweetAlertDialog(SellerMontlyPaymentActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#73b504"));
                                            pDialog.setTitleText("Uploading receipt Details....");
                                            pDialog.setCancelable(false);
                                            pDialog.show();
                                            StorageReference imageFileStorageRef = receiptReference.child("ReceiptDetails/" + System.currentTimeMillis() + "." + getExtenstion(storeImageUri));

                                            Bitmap bmp = null;
                                            try {
                                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), storeImageUri);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                            byte[] data = baos.toByteArray();

                                            mItemStorageTask = imageFileStorageRef.putBytes(data).addOnSuccessListener(
                                                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                            while (!urlTask.isSuccessful()) ;
                                                            final Uri downloadUrl = urlTask.getResult();

                                                            paymentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    //if (dataSnapshot.getChildrenCount() > 0) {
                                                                    storeImageUri = null;
                                                                    receiptImage.setImageBitmap(null);
                                                                    if (!((Activity) SellerMontlyPaymentActivity.this).isFinishing()) {
                                                                        DatabaseReference startTimeDataRef = CommonMethods.fetchFirebaseDatabaseReference(PAYMENTS).child(getStoreID).child(sellerPaymentDetailsConstant.getStartDate());
                                                                        startTimeDataRef.child("paymentStatus").setValue("Settled");
                                                                        startTimeDataRef.child("settledAmount").setValue(amount_edittext.getText().toString());
                                                                        startTimeDataRef.child("receiptURL").setValue(downloadUrl.toString());
                                                                    }
                                                                    if (!((Activity) SellerMontlyPaymentActivity.this).isFinishing()) {
                                                                        pDialog.dismiss();
                                                                        cashDialog.dismiss();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SellerMontlyPaymentActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(SellerMontlyPaymentActivity.this, "Please upload receipt", Toast.LENGTH_SHORT).show();



                                    }

                                }
                            });

                            cancelDialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cashDialog.dismiss();
                                }
                            });
                        }

                        //   Toast.makeText(SellerMontlyPaymentActivity.this, "true", Toast.LENGTH_SHORT).show();
                     /*    if ( sellerPaymentDetailsConstant.getPaymentStatus()==null ){

                            dialogBuilder = new AlertDialog.Builder(SellerMontlyPaymentActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            final View dialogView = inflater.inflate(R.layout.bill_details, null);
                            dialogBuilder.setView(dialogView);
                            totalAmountInfo = dialogView.findViewById(R.id.totalAmountInfo);
                            cancelDialog = dialogView.findViewById(R.id.Cancel);
                            chooseeReceipt = dialogView.findViewById(R.id.uploadReceipt);
                            changePaymentStatus = dialogView.findViewById(R.id.changePaymentStatusButton);
                            receiptImage = dialogView.findViewById(R.id.receiptImageview);
                            cashExpenselistview = dialogView.findViewById(R.id.billetailslist);


                            chooseeReceipt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                                }
                            });

                            orderdetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (check) {
                                        if (snapshot.getChildrenCount() > 0) {
                                            orderDetailsArrayList.clear();
                                            itemDetailsArrayListHere.clear();
                                            totalAmountTemp = 0;

                                            for (DataSnapshot resSnap : snapshot.getChildren()) {
                                                orderDetails = resSnap.getValue(OrderDetails.class);
                                                itemDetailsArrayListHere = (ArrayList<ItemDetails>) orderDetails.getItemDetailList();

                                                DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                                                List<Date> dates = getDates(sellerPaymentDetailsConstant.getStartDate(), sellerPaymentDetailsConstant.getEndDate());
                                                for (Date date : dates) {
                                                    if (orderDetails.getFormattedDate().equals(df1.format(date))) {
                                                        if (orderDetails.getOrderStatus().equals("Delivered")) {
                                                            if (itemDetailsArrayListHere.get(0).getSellerId().equals(getStoreID)) {

                                                                orderDetailsArrayList.add(orderDetails);
                                                                totalAmountTemp = totalAmountTemp + orderDetails.getTotalAmount();
                                                            }
                                                        }
                                                    }
                                                    totalAmountInfo.setText("₹ " + totalAmountTemp);
                                                    BillDetailsAdapter billDetailsAdapter = new BillDetailsAdapter(SellerMontlyPaymentActivity.this, orderDetailsArrayList,"Seller");
                                                    cashExpenselistview.setAdapter(billDetailsAdapter);
                                                }
                                            }
                                            if (sellerPaymentDetailsConstant.getTotalAmount() == 0) {

                                                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(SellerMontlyPaymentActivity.this, SweetAlertDialog.ERROR_TYPE);
                                                sweetAlertDialog.setCancelable(false);
                                                sweetAlertDialog.setTitleText("No more orders taken for this Month").show();
                                            } else {
                                                cashDialog = dialogBuilder.create();
                                                cashDialog.show();
                                            }

                                        }

                                        }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            changePaymentStatus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    {
                                        if (storeImageUri != null) {
                                            pDialog = new SweetAlertDialog(SellerMontlyPaymentActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#73b504"));
                                            pDialog.setTitleText("Uploading receipt Details....");
                                            pDialog.setCancelable(false);
                                            pDialog.show();
                                            StorageReference imageFileStorageRef = receiptReference.child("ReceiptDetails/" + System.currentTimeMillis() + "." + getExtenstion(storeImageUri));

                                            Bitmap bmp = null;
                                            try {
                                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), storeImageUri);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                            byte[] data = baos.toByteArray();

                                            mItemStorageTask = imageFileStorageRef.putBytes(data).addOnSuccessListener(
                                                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                            while (!urlTask.isSuccessful()) ;
                                                            final Uri downloadUrl = urlTask.getResult();

                                                            paymentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    //if (dataSnapshot.getChildrenCount() > 0) {
                                                                    storeImageUri = null;
                                                                    receiptImage.setImageBitmap(null);
                                                                    if (!((Activity) SellerMontlyPaymentActivity.this).isFinishing()) {
                                                                        DatabaseReference startTimeDataRef = CommonMethods.fetchFirebaseDatabaseReference(PAYMENTS).child(getStoreID).child(sellerPaymentDetailsConstant.getStartDate());
                                                                        startTimeDataRef.child("paymentStatus").setValue("Settled");
                                                                        startTimeDataRef.child("receiptURL").setValue(downloadUrl.toString());
                                                                    }
                                                                    if (!((Activity) SellerMontlyPaymentActivity.this).isFinishing()) {
                                                                        pDialog.dismiss();
                                                                        cashDialog.dismiss();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SellerMontlyPaymentActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(SellerMontlyPaymentActivity.this, "Please upload receipt", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            });

                            cancelDialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cashDialog.dismiss();
                                }
                            });



                        }*/

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
/*
                if (sellerPaymentDetailsConstant.getPaymentStatus()!=null) {
                    if (sellerPaymentDetailsConstant.getPaymentStatus().equals("Settled")) {

                        dialogBuilder = new AlertDialog.Builder(SellerMontlyPaymentActivity.this);
                        LayoutInflater inflater1 = getLayoutInflater();
                        final View dialogView1 = inflater1.inflate(R.layout.bill_details_new, null);
                        dialogBuilder.setView(dialogView1);
                        cancelDialog = dialogView1.findViewById(R.id.Cancel);
                        receiptImage = dialogView1.findViewById(R.id.receiptImageview);


                        if (sellerPaymentDetailsConstant.getReceiptURL()!=null && !sellerPaymentDetailsConstant.getReceiptURL().equals("")) {
                            String drivingLicenseProofUri = String.valueOf(Uri.parse(sellerPaymentDetailsConstant.getReceiptURL()));
                        //    Picasso.get().load(drivingLicenseProofUri).into(receiptImage);
                            Glide.with(getApplicationContext()).load(drivingLicenseProofUri).into(receiptImage);
                            cashDialog = dialogBuilder.create();
                            cashDialog.show();
                        }
                        cancelDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cashDialog.dismiss();
                            }
                        });

                    }
                }*/
            }
        });




        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToHomeintent = new Intent(SellerMontlyPaymentActivity.this, StoreHistory.class);
                startActivity(backToHomeintent);
            }
        });

    }


    private static List<Date> getDates(String dateString1, String dateString2) {
        ArrayList<Date> dates = new ArrayList<Date>();
        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }
    private String getExtenstion(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            storeImageUri = data.getData();
         //   Picasso.get().load(storeImageUri).into(receiptImage);
            Glide.with(getApplicationContext()).load(storeImageUri).into(receiptImage);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),StoreHistory.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}