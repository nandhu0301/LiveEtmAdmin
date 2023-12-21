package com.smiligence.etmtechAdmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.smiligence.etmtechAdmin.Adapter.NormalDiscountAdapter;
import com.smiligence.etmtechAdmin.Adapter.NotApplicableDiscountsAdapter;
import com.smiligence.etmtechAdmin.bean.Discount;
import com.smiligence.etmtechAdmin.bean.ItemDetails;
import com.smiligence.etmtechAdmin.bean.OrderDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.DateUtils;
import com.smiligence.etmtechAdmin.common.TextUtils;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.smiligence.etmtechAdmin.common.Constant.CUSTOMER_DISCOUNT_DETAILS_FIREBASE_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.DATE_FORMAT;
import static com.smiligence.etmtechAdmin.common.Constant.DISCOUNT_DETAILS_FIREBASE_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;


public class OffersActivity extends AppCompatActivity {


    double roundOff = 0.0, getStoreLatitude, getStoreLongtitude;
    ImageView backButton;
    DatabaseReference oneTimeDiscountRef, discountsDataRef;
    Discount discount;
    Discount discount1;
    ArrayList<Discount> discountArrayList = new ArrayList<>();
    final ArrayList<Discount> discountArrayListNew = new ArrayList<>();
    final ArrayList<Discount> notApplicableDiscountArrayList = new ArrayList<>();
    ListView discountListview;
    ListView notApplicableListview;
    NormalDiscountAdapter normalDiscountAdapter;
    NotApplicableDiscountsAdapter notApplicableDiscountsAdapter;


    TextView availableTextView;
    LinearLayout linearLayout;
    DatabaseReference orderDetailsDataRef;
    String saved_id;
    ArrayList<String> discountNameArrayList = new ArrayList<>();
    String pattern = "hh:mm";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    String currentTime;
    DateFormat date;
    Date currentLocalTime;
    String currentDateAndTime;
    String finalBill;

    ArrayList<ItemDetails> itemDetailsList = new ArrayList<ItemDetails>();

    String discountName, discountType;
    int discountAmountIntent, maximimDiscountAmount;
boolean isCustomerDetailsPresent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);

        backButton = findViewById(R.id.backButtomImageView);
        linearLayout = findViewById(R.id.textLayout);

        finalBill = getIntent().getStringExtra("BillAmount");
        itemDetailsList = (ArrayList<ItemDetails>) getIntent().getSerializableExtra("itemDetailsArrayList");





        discountsDataRef = CommonMethods.fetchFirebaseDatabaseReference(DISCOUNT_DETAILS_FIREBASE_TABLE);
        oneTimeDiscountRef = CommonMethods.fetchFirebaseDatabaseReference(CUSTOMER_DISCOUNT_DETAILS_FIREBASE_TABLE);
        orderDetailsDataRef = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);

        discountListview = findViewById(R.id.discountsListview);
        notApplicableListview = findViewById(R.id.notapplicablediscountsListview);
        availableTextView = findViewById(R.id.textAvailoffers);

        getStoreLatitude = getIntent().getDoubleExtra("storeatitude", 0.0);
        getStoreLongtitude = getIntent().getDoubleExtra("storeLongitude", 0.0);

        Calendar cal = Calendar.getInstance();
        currentLocalTime = cal.getTime();
        date = new SimpleDateFormat("HH:mm aa");
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        currentDateAndTime = dateFormat.format(new Date());
        currentTime = date.format(currentLocalTime);

        final SharedPreferences loginSharedPreferences1 = getSharedPreferences("CustomerID", MODE_PRIVATE);
        saved_id = loginSharedPreferences1.getString("ID", "");


        discountName = getIntent().getStringExtra("discountName");
        discountType = getIntent().getStringExtra("discountType");
        discountAmountIntent = getIntent().getIntExtra("discountAmount",0);
        maximimDiscountAmount = getIntent().getIntExtra("maximumDiscountAmount",0);
        isCustomerDetailsPresent=getIntent().getExtras().getBoolean("isCustomerDetailsPresent");


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), BillingActivity.class);
                intent.putExtra("From", "ApplyOffers");
                intent.putExtra("itemDetailsArrayList", itemDetailsList);
                intent.putExtra("discountName", discountName);
                intent.putExtra("discountType", discountType);
                intent.putExtra("discountAmount", discountAmountIntent);
                intent.putExtra("maximumDiscountAmount",maximimDiscountAmount);
                intent.putExtra("isCustomerDetailsPresent",isCustomerDetailsPresent);

                //intent.putExtra("dicountGivenBy", "");

                startActivity(intent);

            }
        });




        orderDetailsDataRef.orderByChild("customerId").equalTo(saved_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                discountNameArrayList.clear();
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot orderSnap : dataSnapshot.getChildren()) {
                        OrderDetails orderDetails = orderSnap.getValue(OrderDetails.class);
                        discountNameArrayList.add(orderDetails.getDiscountName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });
        discountsDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                discountArrayList.clear();
                notApplicableDiscountArrayList.clear();
                if (dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot discountSnap : dataSnapshot.getChildren()) {
                        discount = discountSnap.getValue(Discount.class);
                        if (discount.getValidTillDate() != null) {
                            try {
                                if ((new SimpleDateFormat("dd-MM-yyyy").parse(DateUtils.fetchFormatedCurrentDate()).before((new SimpleDateFormat("dd-MM-yyyy").parse(discount.getValidTillDate()))))) {
                                    if (discount.getDiscountStatus().equals("Active")) {
                                        if (discount.getTypeOfDiscount() != null) {
                                            if (discount.getVisibility().equals("Visible")) {
                                                if (discount.getTypeOfDiscount().equals("Price")) {
                                                    discountPriceFunction(discount);
                                                } else if (discount.getTypeOfDiscount().equals("Percent")) {
                                                    discountPercentFunction(discount);
                                                }
                                            }
                                        }
                                    } else {
                                        notApplicableDiscountArrayList.add(discount);
                                    }
                                } else if (DateUtils.fetchFormatedCurrentDate().equals(discount.getValidTillDate())) {
                                    try {
                                        if ((sdf.parse(currentTime).compareTo(sdf.parse(discount.getValidTillTime())) == -1) ||
                                                (sdf.parse(currentTime).compareTo(sdf.parse(discount.getValidTillTime())) == 0)) {
                                            if (discount.getDiscountStatus().equals("Active")) {
                                                if (discount.getTypeOfDiscount() != null) {
                                                    if (discount.getVisibility().equals("Visible")) {
                                                        if (discount.getTypeOfDiscount().equals("Price")) {
                                                            discountPriceFunction(discount);
                                                        } else if (discount.getTypeOfDiscount().equals("Percent")) {
                                                            discountPercentFunction(discount);
                                                        }
                                                    }
                                                }
                                            } else {
                                                notApplicableDiscountArrayList.add(discount);
                                            }
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                oneTimeDiscountRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {

                            for (DataSnapshot discountSnap : dataSnapshot.getChildren()) {
                                discount = discountSnap.getValue(Discount.class);
                                if (discount.getCustomerId().equals(saved_id)) {
                                    if (discount.getValidTillDate() != null) {
                                        try {
                                            if ((new SimpleDateFormat("dd-MM-yyyy").parse(DateUtils.fetchFormatedCurrentDate()).before((new SimpleDateFormat("dd-MM-yyyy").parse(discount.getValidTillDate()))))) {

                                                System.out.println("Comes 1");
                                                if (discount.getDiscountStatus().equals("Active")) {
                                                    if (discount.getTypeOfDiscount() != null) {
                                                        if (discount.getVisibility().equals("Visible")) {
                                                            if (discount.getTypeOfDiscount().equals("Price")) {
                                                                discountPriceFunction(discount);
                                                            } else if (discount.getTypeOfDiscount().equals("Percent")) {

                                                                discountPercentFunction(discount);
                                                            }
                                                        }

                                                    }
                                                }
                                            } else if (DateUtils.fetchFormatedCurrentDate().equals(discount.getValidTillDate())) {
                                                try {
                                                    System.out.println("Comes 2");
                                                    if ((sdf.parse(currentTime).compareTo(sdf.parse(discount.getValidTillTime())) == -1) ||
                                                            (sdf.parse(currentTime).compareTo(sdf.parse(discount.getValidTillTime())) == 0)) {
                                                        System.out.println("Comes 3");
                                                        if (discount.getDiscountStatus().equals("Active")) {
                                                            System.out.println("Comes 3");

                                                            if (discount.getTypeOfDiscount() != null) {

                                                                if (discount.getVisibility().equals("Visible")) {
                                                                    if (discount.getTypeOfDiscount().equals("Price")) {
                                                                        discountPriceFunction(discount);
                                                                        System.out.println("Comes 4");
                                                                    } else if (discount.getTypeOfDiscount().equals("Percent")) {
                                                                        discountPercentFunction(discount);
                                                                        System.out.println("Comes 5");
                                                                    }
                                                                }

                                                            }
                                                        }
                                                    }
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                            }


                        } else {

                        }
                        TextUtils.removeDuplicatesList(discountArrayList);
                        normalDiscountAdapter = new NormalDiscountAdapter(OffersActivity.this, discountArrayList);
                        normalDiscountAdapter.notifyDataSetChanged();
                        discountListview.setAdapter(normalDiscountAdapter);
                        if (normalDiscountAdapter != null) {
                            availableTextView.setVisibility(View.VISIBLE);
                            int totalHeight = 0;
                            for (int i = 0; i < normalDiscountAdapter.getCount(); i++) {
                                View listItem = normalDiscountAdapter.getView(i, null, discountListview);
                                listItem.measure(0, 0);
                                totalHeight += listItem.getMeasuredHeight();
                            }
                            ViewGroup.LayoutParams params = discountListview.getLayoutParams();
                            params.height = totalHeight + (discountListview.getDividerHeight() * (normalDiscountAdapter.getCount() - 1));
                            discountListview.setLayoutParams(params);
                            discountListview.requestLayout();
                            discountListview.setAdapter(normalDiscountAdapter);
                            normalDiscountAdapter.notifyDataSetChanged();
                        }
                        notApplicableDiscountsAdapter = new NotApplicableDiscountsAdapter(OffersActivity.this, notApplicableDiscountArrayList);
                        notApplicableDiscountsAdapter.notifyDataSetChanged();
                        notApplicableListview.setAdapter(notApplicableDiscountsAdapter);

                        if (notApplicableDiscountsAdapter != null) {
                            availableTextView.setVisibility(View.VISIBLE);
                            int totalHeight = 0;
                            for (int i = 0; i < notApplicableDiscountsAdapter.getCount(); i++) {
                                View listItem = notApplicableDiscountsAdapter.getView(i, null, notApplicableListview);
                                listItem.measure(0, 0);
                                totalHeight += listItem.getMeasuredHeight();
                            }
                            ViewGroup.LayoutParams params = notApplicableListview.getLayoutParams();
                            params.height = totalHeight + (notApplicableListview.getDividerHeight() * (notApplicableDiscountsAdapter.getCount() - 1));
                            notApplicableListview.setLayoutParams(params);
                            notApplicableListview.requestLayout();
                            notApplicableListview.setAdapter(notApplicableDiscountsAdapter);
                            notApplicableDiscountsAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        discountListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                discount1 = discountArrayList.get(i);
                int price = 0;
                int percentage = 0;
                int maximumBillAmount = 0;
                if (discount1.getTypeOfDiscount().equals("Price")) {
                    if (!discount1.getDiscountPrice().equals("") && discount1.getDiscountPrice() != null) {
                        price = Integer.parseInt(discount1.getDiscountPrice());
                    } else {
                        price = 0;
                    }
                } else {
                    if (!discount1.getDiscountPercentageValue().equals("") && discount1.getDiscountPercentageValue() != null) {
                        percentage = Integer.parseInt(discount1.getDiscountPercentageValue());
                    } else {
                        percentage = 0;
                    }
                }
                if (!discount1.getMinmumBillAmount().equals("") && discount1.getMinmumBillAmount() != null) {
                    maximumBillAmount = Integer.parseInt(discount1.getMinmumBillAmount());
                } else {
                    maximumBillAmount = 0;
                }
                if (discount1.getTypeOfDiscount().equals("Price")) {
                    Intent intent = new Intent(getApplicationContext(), BillingActivity.class);
                    intent.putExtra("From", "ApplyOffers");
                    intent.putExtra("itemDetailsArrayList", itemDetailsList);

                    intent.putExtra("discountName", discount1.getDiscountName());
                    intent.putExtra("discountType", "Price");
                    intent.putExtra("discountAmount", price);
                    intent.putExtra("maximumDiscountAmount", maximumBillAmount);
                    intent.putExtra("dicountGivenBy", discount1.getDiscountGivenBy());
                    intent.putExtra("isCustomerDetailsPresent",isCustomerDetailsPresent);


                    startActivity(intent);
                } else if (discount1.getTypeOfDiscount().equals("Percent")) {
                    Intent intent = new Intent(getApplicationContext(), BillingActivity.class);
                    intent.putExtra("From", "ApplyOffers");
                    intent.putExtra("itemDetailsArrayList", itemDetailsList);
                    intent.putExtra("discountName", discount1.getDiscountName());
                    intent.putExtra("discountType", "Percent");
                    intent.putExtra("discountAmount", percentage);
                    intent.putExtra("maximumDiscountAmount", maximumBillAmount);
                    intent.putExtra("dicountGivenBy", discount1.getDiscountGivenBy());
                    intent.putExtra("isCustomerDetailsPresent",isCustomerDetailsPresent);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OffersActivity.this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void discountPriceFunction(Discount discount) {
        if (finalBill != null) {
            try {
                if (((Number) NumberFormat.getInstance().parse(finalBill)).intValue() >=
                        ((Number) NumberFormat.getInstance().parse(discount.getMinmumBillAmount())).intValue()) {
                    if (discount.getUsage().equals("One Time Discount")) {
                        if (discountNameArrayList == null || discountNameArrayList.size() == 0) {
                            discountArrayList.add(discount);
                        } else {
                            int count = 0;
                            for (int i = 0; i < discountNameArrayList.size(); i++) {
                                if (discountNameArrayList.get(i).equals(discount.getDiscountName())) {
                                    count = count + 1;
                                    break;
                                }
                            }
                            if (count == 0) {
                                discountArrayList.add(discount);
                            }
                        }
                    } else {
                        discountArrayList.add(discount);
                    }
                } else {
                    if (!discount.getUsage().equals("One Time Discount")) {
                        notApplicableDiscountArrayList.add(discount);
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }



    }

    public void discountPercentFunction(Discount discount) {
        if (finalBill != null) {
            System.out.println("DiscountPercent" + discount + discount.getUsage());
            try {
                if (((Number) NumberFormat.getInstance().parse(finalBill)).intValue() >=
                        ((Number) NumberFormat.getInstance().parse(discount.getMinmumBillAmount())).intValue()) {

                    if (discount.getUsage().equals("One Time Discount")) {
                        if (discountNameArrayList == null || discountNameArrayList.size() == 0) {
                            discountArrayList.add(discount);
                        } else {

                            int count = 0;
                            for (int i = 0; i < discountNameArrayList.size(); i++) {
                                if (discount.getDiscountName().equalsIgnoreCase(discountNameArrayList.get(i))) {
                                    count = count + 1;
                                    break;
                                }
                            }
                            if (count == 0) {
                                discountArrayList.add(discount);
                            }
                        }
                    } else {
                        discountArrayList.add(discount);
                    }
                } else {
                    notApplicableDiscountArrayList.add(discount);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
