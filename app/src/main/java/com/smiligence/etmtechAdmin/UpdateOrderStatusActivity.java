package com.smiligence.etmtechAdmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.smiligence.etmtechAdmin.Adapter.OrderStatusChangeAdapter;
import com.smiligence.etmtechAdmin.bean.CustomerDetails;
import com.smiligence.etmtechAdmin.bean.ItemReviewAndRatings;
import com.smiligence.etmtechAdmin.bean.OrderDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.Constant;
import com.smiligence.etmtechAdmin.common.FcmNotificationsSender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


import static com.smiligence.etmtechAdmin.common.Constant.ITEM_RATING_REVIEW_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.SELLER_IMAGE_STORAGE;
import static com.smiligence.etmtechAdmin.common.Constant.TITLE_UPDATE_ORDERS;

public class UpdateOrderStatusActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference orderDetailsRef, customerDetailsDataRef;

    OrderDetails orderDetails;
    OrderDetails orderDetailsNew;
    ArrayList<OrderDetails> orderDetailsList = new ArrayList<>();
    ListView orderListView;
    ArrayAdapter<CharSequence> statusChangeArray;
    String statusChangeString;
    AlertDialog dialog;
    int temp = 0;
    OrderStatusChangeAdapter orderStatusChangeAdapter;
    public static String saved_productKey, saved_businessName, saved_userName, saved_email;
    public static TextView textViewUsername;
    public static TextView textViewEmail;
    public static ImageView imageView;
    public static Menu menuNav;
    public static View mHeaderView;
    NavigationView navigationView;
    TextView reviewCount;
    ImageView trackImage;
    Button chooseTrackImage;
    Uri mimageuri;
    Intent intentImage = new Intent();
    StorageTask mItemStorageTask;
    StorageReference storeNameStorage;
    ImageView shareClickIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order_status);
        orderListView = findViewById(R.id.orderDetailsListView);
        shareClickIcon = findViewById(R.id.shareClickIcon);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        androidx.appcompat.widget.Toolbar toolbar1 = findViewById(R.id.toolbar);
        toolbar1.setTitle(TITLE_UPDATE_ORDERS);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar1, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(UpdateOrderStatusActivity.this);

        navigationView.setCheckedItem(R.id.updatestatus);
        reviewCount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.reviewApproval));
        reviewCount.setGravity(Gravity.CENTER_VERTICAL);
        reviewCount.setTypeface(null, Typeface.BOLD);
        reviewCount.setTextColor(getResources().getColor(R.color.redColor));
        storeNameStorage = CommonMethods.fetchFirebaseStorageReference("TrackingImages");

        customerDetailsDataRef = CommonMethods.fetchFirebaseDatabaseReference("CustomerLoginDetails");

        SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_userName = loginSharedPreferences.getString("userName", "");
        saved_productKey = loginSharedPreferences.getString("productkeyStr", "");
        saved_businessName = loginSharedPreferences.getString("businessNameStr", "");
        saved_email = loginSharedPreferences.getString("email", "");

        LinearLayout linearLayout = (LinearLayout) navigationView.getHeaderView(0);
        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);
        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);
        textViewUsername.setText(DashBoardActivity.roleName);
        textViewEmail.setText(DashBoardActivity.userName);


        shareClickIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PopupMenu popup = new PopupMenu(UpdateOrderStatusActivity.this, shareClickIcon);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {


                        //                    if ("Order Placed".equalsIgnoreCase(orderDetails.getOrderStatus()) ||
//                            ("Order confirmed".equalsIgnoreCase(orderDetails.getOrderStatus())) ||
//                            ("Packed and ready for shipping".equalsIgnoreCase(orderDetails.getOrderStatus()))) {
//                        orderDetailsList.add(orderDetails);
//                    }

                        if (item.getTitle().equals("All")) {

                            loadDataFunction("All");
                        } else if (item.getTitle().equals("Order Placed")) {

                            loadDataFunction("Order Placed");
                        } else if (item.getTitle().equals("Order Confirmed")) {
                            loadDataFunction("Order confirmed");
                        } else if (item.getTitle().equals("Packed and ready for shipping")) {

                            loadDataFunction("Packed and ready for shipping");
                        } else {
                            loadDataFunction("Shipped");
                        }
                        return true;
                    }
                });
                popup.show();

            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.vieworders) {
            Intent intent = new Intent(getApplicationContext(), ViewOrderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.category) {
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
        } else if (id == R.id.add_description) {
            Intent intent = new Intent(getApplicationContext(), Add_Description_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.discounts) {
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
        } else if (id == R.id.CustomerDetails) {
            Intent intent = new Intent(getApplicationContext(), CustomerDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.logoutscreen) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(UpdateOrderStatusActivity.this);
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
                    Intent intent = new Intent(UpdateOrderStatusActivity.this,
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
                    navigationView.setCheckedItem(R.id.updatestatus);
                }
            });
        } else if (id == R.id.contactdetails) {
            Intent intent = new Intent(getApplicationContext(), DeliverySettings.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.reviewApproval) {
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
        } else if (id == R.id.billingScreen) {
            Intent intent = new Intent(getApplicationContext(), BillingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadDataFunction("All");

        ArrayList<ItemReviewAndRatings> list = new ArrayList<>();
        DatabaseReference itemReviewDataRef = CommonMethods.fetchFirebaseDatabaseReference(ITEM_RATING_REVIEW_TABLE);
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

    public void loadDataFunction(String status) {

        orderDetailsRef = CommonMethods.fetchFirebaseDatabaseReference("OrderDetails");

        orderDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderDetailsList.clear();
                for (DataSnapshot orderSnap : dataSnapshot.getChildren()) {

                    orderDetails = orderSnap.getValue(OrderDetails.class);

                    if (status.equals("All")) {
                        orderDetailsList.add(orderDetails);
                    } else {
                        if (status.equalsIgnoreCase(orderDetails.getOrderStatus())) {
                            orderDetailsList.add(orderDetails);
                        }
                    }

//                    if ("Order Placed".equalsIgnoreCase(orderDetails.getOrderStatus()) ||
//                            ("Order confirmed".equalsIgnoreCase(orderDetails.getOrderStatus())) ||
//                            ("Packed and ready for shipping".equalsIgnoreCase(orderDetails.getOrderStatus()))) {
//                        orderDetailsList.add(orderDetails);
//                    }

                }
                if (!orderDetailsList.isEmpty() || orderDetailsList.size() != 0) {
                    orderStatusChangeAdapter = new OrderStatusChangeAdapter(UpdateOrderStatusActivity.this, orderDetailsList);
                    orderListView.setAdapter(orderStatusChangeAdapter);
                    orderStatusChangeAdapter.notifyDataSetChanged();
                }


                if (orderStatusChangeAdapter != null) {
                    int totalHeight = 0;

                    for (int i = 0; i < orderStatusChangeAdapter.getCount(); i++) {
                        View listItem = orderStatusChangeAdapter.getView(i, null, orderListView);
                        listItem.measure(0, 0);
                        totalHeight += listItem.getMeasuredHeight();
                    }

                    ViewGroup.LayoutParams params = orderListView.getLayoutParams();
                    params.height = totalHeight + (orderListView.getDividerHeight() * (orderStatusChangeAdapter.getCount() - 1));
                    orderListView.setLayoutParams(params);
                    orderListView.requestLayout();
                    orderListView.setAdapter(orderStatusChangeAdapter);
                    orderStatusChangeAdapter.notifyDataSetChanged();


                }

                orderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        orderDetailsNew = orderDetailsList.get(i);
                        if (!orderDetailsNew.getOrderStatus().equals("Shipped"))
                        {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(UpdateOrderStatusActivity.this);

                            View mView = getLayoutInflater().inflate(R.layout.status_change_popup, null);
                            ImageView cancel = mView.findViewById(R.id.cancelpop);
                         //   Spinner statuschangeSpinner = mView.findViewById(R.id.statusspinner);
                            TextView orderid = mView.findViewById(R.id.customerorderidtextdialog);
                            TextView cusName = mView.findViewById(R.id.cusnametextdialog);
                            TextView amount = mView.findViewById(R.id.amounttextdialog);
                            EditText courierPartnerNameEdt = mView.findViewById(R.id.courierPartnerName);
                            EditText trackingId = mView.findViewById(R.id.trackingId);
                            RelativeLayout trackingidRelativeLayout = mView.findViewById(R.id.trackingIdLayout);
                            RelativeLayout courierPartnerRelativeLayout = mView.findViewById(R.id.courierPartnerNameLayout);
                            RelativeLayout trackingLayout = mView.findViewById(R.id.trackingLayout);
                            chooseTrackImage = mView.findViewById(R.id.choose_track_Image);
                            trackImage = mView.findViewById(R.id.trackImage);


                            final EditText editText = mView.findViewById(R.id.edittext);
                            Button ok = mView.findViewById(R.id.buttonok);

                            orderid.setText("Order Id : " + orderDetailsNew.getOrderId());
                            cusName.setText("Customer Name : " + orderDetailsNew.getCustomerName());
                            amount.setText("Purchased Amount :  ₹" + orderDetailsNew.getTotalAmount());


                            statusChangeArray = ArrayAdapter
                                    .createFromResource(UpdateOrderStatusActivity.this, R.array.status_change,
                                            R.layout.support_simple_spinner_dropdown_item);
                            statusChangeArray.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                       //     statuschangeSpinner.setAdapter(statusChangeArray);


                            mBuilder.setView(mView);
                            dialog = mBuilder.create();
                            if (!((Activity) UpdateOrderStatusActivity.this).isFinishing()) {
                                dialog.show();
                            }
                            dialog.setCancelable(false);


                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                           /* statuschangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    statusChangeString = adapterView.getItemAtPosition(i).toString();
                                    if (statusChangeString.equals("Shipped")) {
                                        courierPartnerNameEdt.setEnabled(true);
                                        trackingId.setEnabled(true);
                                        trackingidRelativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                                        courierPartnerRelativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                                        trackingLayout.setVisibility(View.VISIBLE);

                                    } else {
                                        courierPartnerNameEdt.setEnabled(false);
                                        trackingId.setEnabled(false);
                                        courierPartnerRelativeLayout.setBackgroundColor(getResources().getColor(R.color.grey));
                                        trackingidRelativeLayout.setBackgroundColor(getResources().getColor(R.color.grey));
                                        courierPartnerNameEdt.setText("");
                                        trackingId.setText("");
                                        trackingLayout.setVisibility(View.INVISIBLE);
                                        trackImage.setImageResource(R.drawable.b_chooseimage);
                                        mimageuri = null;
                                        trackingId.setError(null);
                                        courierPartnerNameEdt.setError(null);
                                    }
                                    editText.setText(statusChangeString);

                                    chooseTrackImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            openFileChooser();
                                            startActivityForResult(intentImage, 100);
                                        }
                                    });


                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });*/

                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if ("Select Status".equalsIgnoreCase(editText.getText().toString())) {
                                        Toast.makeText(UpdateOrderStatusActivity.this, "select Status", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {


                                        if (editText.getText().toString().equals("Shipped")) {
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
                                            } else {

                                                sendNotificationForParticularUser(orderDetailsNew.getCustomerId(), editText.getText().toString());


                                                if (mimageuri != null) {
                                                    final SweetAlertDialog pDialog = new SweetAlertDialog(UpdateOrderStatusActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#1898ED"));
                                                    pDialog.setTitleText("Changing Status....");
                                                    pDialog.setCancelable(false);
                                                    pDialog.show();
                                                    StorageReference imageFileLogo = storeNameStorage.child(SELLER_IMAGE_STORAGE
                                                            + System.currentTimeMillis() + "." + mimageuri);
                                                    Bitmap bmp = null;
                                                    try {
                                                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mimageuri);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                                    byte[] data = baos.toByteArray();

                                                    mItemStorageTask = imageFileLogo.putBytes(data).addOnSuccessListener(
                                                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshotlogo) {

                                                                    Task<Uri> urlTaskLogo = taskSnapshotlogo.getStorage().getDownloadUrl();
                                                                    while (!urlTaskLogo.isSuccessful())
                                                                        ;
                                                                    Uri downloadUrl = urlTaskLogo.getResult();
                                                                    String imageLogo = downloadUrl.toString();


                                                                    if (!((Activity) UpdateOrderStatusActivity.this).isFinishing()) {
                                                                        pDialog.dismiss();


                                                                        orderDetailsRef.orderByChild("orderId").equalTo(orderDetailsNew.getOrderId())
                                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        if (dataSnapshot.exists())
                                                                                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                                                                data.getRef().child("trackingImage").setValue(imageLogo);
                                                                                                data.getRef().child("courierPartnerName").setValue(courierPartnerNameEdt.getText().toString());
                                                                                                data.getRef().child("trackingId").setValue(trackingId.getText().toString());
                                                                                                data.getRef().child("orderStatus").setValue(editText.getText().toString());
                                                                                                data.getRef().child("docType").setValue("img");
                                                                                                Intent intent = new Intent(getApplicationContext(), UpdateOrderStatusActivity.class);
                                                                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                                startActivity(intent);
                                                                                            }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                    }

                                                                                });

//
                                                                    }

                                                                }
                                                            });

                                                } else {

                                                    orderDetailsRef.orderByChild("orderId").equalTo(orderDetailsNew.getOrderId())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.exists())
                                                                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                                            data.getRef().child("courierPartnerName").setValue(courierPartnerNameEdt.getText().toString());
                                                                            data.getRef().child("trackingId").setValue(trackingId.getText().toString());
                                                                            data.getRef().child("orderStatus").setValue(editText.getText().toString());
                                                                            Intent intent = new Intent(getApplicationContext(), UpdateOrderStatusActivity.class);
                                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                            startActivity(intent);
                                                                        }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }

                                                            });


                                                    if (!((Activity) UpdateOrderStatusActivity.this).isFinishing()) {
                                                        Intent intent = new Intent(getApplicationContext(), UpdateOrderStatusActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                    }
                                                }
                                            }
                                            return;
                                        } else {

                                            sendNotificationForParticularUser(orderDetailsNew.getCustomerId(), editText.getText().toString());

                                            orderDetailsRef.orderByChild("orderId").equalTo(orderDetailsNew.getOrderId())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists())
                                                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                                    data.getRef().child("orderStatus").setValue(editText.getText().toString());
                                                                    Intent intent = new Intent(getApplicationContext(), UpdateOrderStatusActivity.class);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    startActivity(intent);
                                                                }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }

                                                    });

                                            Intent intent = new Intent(getApplicationContext(), UpdateOrderStatusActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }


                                    }


                                }
                            });
                        }



                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendNotificationForParticularUser(String userId, String status) {

        customerDetailsDataRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                CustomerDetails customerDetails = dataSnapshot.getValue(CustomerDetails.class);
                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(customerDetails.getDeviceId(),
                        "Order",
                        status.toString(), getApplicationContext(), UpdateOrderStatusActivity.this);
                notificationsSender.SendNotifications();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void openFileChooser() {
        intentImage = new Intent();
        intentImage.setType("image/*");
        intentImage.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intentImage.setAction(Intent.ACTION_GET_CONTENT);
    }

   /* protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mimageuri = data.getData();
            Glide.with(UpdateOrderStatusActivity.this).load(mimageuri).into(trackImage);
        }
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}