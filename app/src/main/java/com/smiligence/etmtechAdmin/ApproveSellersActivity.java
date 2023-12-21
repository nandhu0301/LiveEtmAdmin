package com.smiligence.etmtechAdmin;

import static com.smiligence.etmtechAdmin.common.Constant.TITLE_STOREPARTNER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmtechAdmin.bean.CategoryDetails;
import com.smiligence.etmtechAdmin.bean.CategoryDetailsNew;
import com.smiligence.etmtechAdmin.bean.SellerAdapter;
import com.smiligence.etmtechAdmin.bean.UserDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.DateUtils;
import com.smiligence.etmtechAdmin.common.NetworkStateReceiver;

import java.util.ArrayList;
import java.util.List;

public class ApproveSellersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener ,NetworkStateReceiver.NetworkStateReceiverListener {

    NavigationView navigationView;
    public static Menu menuNav;
    public static TextView textViewUsername;
    public static TextView textViewEmail;
    public static ImageView imageView;
    public static View mHeaderView;
    TextView reviewCount;
    String saved_username;
    DatabaseReference SellerloginDetails , categoryDetailsdataref ;
    ListView listView;
    ArrayList<CategoryDetails> categoryList = new ArrayList<> ();
    boolean isChecked=true;
    List<UserDetails> userDetailsList = new ArrayList<> ();
    UserDetails sellerDetails = new UserDetails();
    AlertDialog dialog;
    String variableAssBas="false";
    ArrayList<CategoryDetailsNew> categoryDetailsNewList = new ArrayList<> ();
    List<UserDetails> sellerList = new ArrayList<> ();
    int swipeCount = 0;
    private NetworkStateReceiver networkStateReceiver;
    androidx.appcompat.app.AlertDialog alertDialog;
    ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_sellers);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        disableAutofill();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(TITLE_STOREPARTNER);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.ApproveSellers);



        navigationView.setNavigationItemSelectedListener(ApproveSellersActivity.this);
        LinearLayout linearLayout = (LinearLayout) navigationView.getHeaderView(0);

        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);
        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);
        textViewUsername.setText(DashBoardActivity.roleName);
        textViewEmail.setText(DashBoardActivity.userName);
        /*reviewCount=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.reviewApproval));
        reviewCount.setGravity(Gravity.CENTER_VERTICAL);
        reviewCount.setTypeface(null, Typeface.BOLD);
        reviewCount.setTextColor(getResources().getColor(R.color.redColor));*/


        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();



        SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        SharedPreferences.Editor loginEditor = loginSharedPreferences.edit();
        saved_username = loginSharedPreferences.getString("userNameStr", "");

        listView = findViewById ( R.id.sellersList );



        SellerloginDetails = CommonMethods.fetchFirebaseDatabaseReference( "SellerLoginDetails");
        categoryDetailsdataref = CommonMethods.fetchFirebaseDatabaseReference ("Category" );




        categoryDetailsdataref.addValueEventListener ( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount () > 0) {
                    categoryList.clear();
                    for ( DataSnapshot categorySnap : dataSnapshot.getChildren () ) {
                        CategoryDetails categoryDetails = categorySnap.getValue ( CategoryDetails.class );
                        categoryList.add ( categoryDetails );
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //waiting for approval
        Query waitingforapproval = SellerloginDetails.orderByChild("approvalStatus").equalTo("Waiting for approval");

        waitingforapproval.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getChildrenCount() >0){
                     if (isChecked ){
                         userDetailsList.clear();
                  for (DataSnapshot userdetailSnap :snapshot.getChildren()){
                      sellerDetails = userdetailSnap.getValue(UserDetails.class);
                      userDetailsList.add(sellerDetails);
                  }

                  if (userDetailsList != null) {
                      SellerAdapter sellerAdapter = new SellerAdapter(ApproveSellersActivity.this, userDetailsList);
                      listView.setAdapter(sellerAdapter);
                      sellerAdapter.notifyDataSetChanged();
                  }

                     }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener ((AdapterView.OnItemClickListener) (parent, view, position, id) -> {
            sellerDetails = userDetailsList.get ( position );

            AlertDialog.Builder mBuilder = new AlertDialog.Builder ( ApproveSellersActivity.this );
            View mView = getLayoutInflater ().inflate ( R.layout.seller_details, null );

            final RelativeLayout applicantHeader = mView.findViewById ( R.id.applicantHeader );
            final ImageView storeLogo = mView.findViewById ( R.id.store_logoimage );

            final ImageView aadharProof = mView.findViewById ( R.id.aadhar_imageview );
            final ImageView gstCertificate = mView.findViewById ( R.id.gst_certificate_imageview );


            final LinearLayout detailsLayout = mView.findViewById ( R.id.detailsLayout );
            final TextView imageHeaderName = mView.findViewById ( R.id.imageHeader );

            TextView firstName = mView.findViewById ( R.id.firstName1 );
            TextView lastName = mView.findViewById ( R.id.lastname1 );
            TextView mobileNumber = mView.findViewById ( R.id.mobilenumbertxt1 );
            TextView emailid = mView.findViewById ( R.id.emailid1 );
            final TextView storeAddress = mView.findViewById ( R.id.storeaddress1 );
            TextView pincode = mView.findViewById ( R.id.pincode1 );
            TextView bankname = mView.findViewById ( R.id.bankname1 );
            TextView branchName = mView.findViewById ( R.id.branchName1 );
            TextView accountNumber = mView.findViewById ( R.id.accountnumber1 );
            TextView ifscCode = mView.findViewById ( R.id.ifscCode1 );
            TextView businessName = mView.findViewById ( R.id.businessname1 );
            TextView businessType = mView.findViewById ( R.id.businesstype1 );
            TextView fssaiNumber = mView.findViewById ( R.id.fssaiNumber1 );
            final TextView aadharNumber = mView.findViewById ( R.id.aadharNumber1 );
            TextView gstNumber = mView.findViewById ( R.id.gstnumber1 );
            final TextView commentsIfAny = mView.findViewById ( R.id.commentsSectionEdt );


            Button approveButton = mView.findViewById ( R.id.approve );
            Button declineButton = mView.findViewById ( R.id.reject );


            ImageView cancelButton = mView.findViewById ( R.id.canceldetails );

            String trimmedfirstName = sellerDetails.getFirstName ().replaceFirst("^\\s+", ""); // Remove leading space
            String trimmedlastName = sellerDetails.getLastName ().replaceFirst("^\\s+", ""); // Remove leading space
            String trimmedemailid = sellerDetails.getEmail_Id ().replaceFirst("^\\s+", ""); // Remove leading space
            String trimmedaddress = sellerDetails.getAddress ().replaceFirst("^\\s+", ""); // Remove leading space
            String trimmedbankname = sellerDetails.getBankName ().replaceFirst("^\\s+", ""); // Remove leading space
            String trimmedbranchname = sellerDetails.getBranchName ().replaceFirst("^\\s+", ""); // Remove leading space
            String trimmedstorename = sellerDetails.getStoreName ().replaceFirst("^\\s+", ""); // Remove leading space
            String trimmedbusinnesstype = sellerDetails.getBusinessType ().replaceFirst("^\\s+", ""); // Remove leading space


            firstName.setText ( trimmedfirstName );
            lastName.setText ( trimmedlastName );
            mobileNumber.setText ( sellerDetails.getPhoneNumber () );
            emailid.setText (trimmedemailid );
            storeAddress.setText (trimmedaddress);
            pincode.setText ( sellerDetails.getPincode () );
            bankname.setText (trimmedbankname);
            branchName.setText (trimmedbranchname );
            accountNumber.setText ( sellerDetails.getAccountNumber () );
            ifscCode.setText ( sellerDetails.getIfscCode () );
            businessName.setText ( trimmedstorename );
            businessType.setText ( trimmedbusinnesstype );
            fssaiNumber.setText ( sellerDetails.getFssaiNumber () );
            aadharNumber.setText ( sellerDetails.getAadharNumber () );
            gstNumber.setText ( sellerDetails.getGstNumber () );
            //storeLogo
            Glide.with(getApplicationContext()).load(sellerDetails.getStoreLogo()).error(R.mipmap.ic_launcher_new).fitCenter().into(storeLogo);
//aadhar logo
            Glide.with(getApplicationContext()).load(sellerDetails.getAadharImage()).error(R.mipmap.ic_launcher_new).fitCenter().into(aadharProof);
//Gst logo
            Glide.with(getApplicationContext()).load(sellerDetails.getGstCertificateImage()).error(R.mipmap.ic_launcher_new).fitCenter().into(gstCertificate);

            mBuilder.setView ( mView );
            dialog = mBuilder.create ();
            dialog.show ();
            dialog.setCancelable ( false );



            approveButton.setOnClickListener ((View.OnClickListener) v -> {


                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                LayoutInflater inflater = ApproveSellersActivity.this.getLayoutInflater();
                View dialogview = inflater.inflate(R.layout.paymenttype_radio_layout, null);
                dialogBuilder.setView(dialogview);

                RadioButton basicradio=dialogview.findViewById(R.id.basicRadio);
                RadioButton silverradio=dialogview.findViewById(R.id.silverRadio);
                RadioButton goldradio=dialogview.findViewById(R.id.goldRadio);
                TextView ok=dialogview.findViewById(R.id.ok_pay);
                TextView cancel=dialogview.findViewById(R.id.cancel_pay);
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (basicradio.isChecked())
                        {
                            variableAssBas="Basic";
                        }else if (silverradio.isChecked())
                        {
                            variableAssBas="Silver";
                        }else if (goldradio.isChecked())
                        {
                            variableAssBas="Gold";
                        }
                        if (basicradio.isChecked() || silverradio.isChecked()==true || goldradio.isChecked()==true)
                        {
                            Query sellerCategoryDetails = SellerloginDetails.orderByChild("userId").equalTo(sellerDetails.getUserId());

                            sellerCategoryDetails.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        categoryDetailsNewList.clear();


                                        for (DataSnapshot deliveryBoySnap : dataSnapshot.getChildren())
                                        {
                                            sellerDetails = deliveryBoySnap.getValue(UserDetails.class);
                                            categoryDetailsNewList = (ArrayList<CategoryDetailsNew>) sellerDetails.getCategoryList();
                                            sellerList.clear();

                                            for (int sellercategoryIterator = 0; sellercategoryIterator < categoryDetailsNewList.size(); sellercategoryIterator++) {
                                                for (int categoryIterator = 0; categoryIterator < categoryList.size(); categoryIterator++) {

                                                    final CategoryDetailsNew categoryDetailsNew = categoryDetailsNewList.get(sellercategoryIterator);
                                                    String categoryId = categoryList.get(categoryIterator).getCategoryid();

                                                    if (categoryDetailsNew.getCategoryid().equalsIgnoreCase(categoryId)) {
                                                        CategoryDetails updatedCategoryDetails = categoryList.get(categoryIterator);
                                                        sellerList = updatedCategoryDetails.getSellerList();
                                                        sellerList.add(sellerDetails);
                                                        updatedCategoryDetails.setSellerList(sellerList);
                                                        if (!((Activity) ApproveSellersActivity.this).isFinishing()) {
                                                            categoryDetailsdataref.child(categoryId).setValue(updatedCategoryDetails);
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Intent intent = getIntent();
                                    startActivity(intent);
                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            new backGroundClass().execute();
                            alertDialog.dismiss();
                        }else
                        {
                            Toast.makeText(ApproveSellersActivity.this, "Please select type of settlement", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();

                    }
                });
            });

            declineButton.setOnClickListener (v -> {

                commentsIfAny.setVisibility(View.VISIBLE);
                if (commentsIfAny.getVisibility() == View.VISIBLE) {
                    if ("".equals(commentsIfAny.getText().toString())) {
                        commentsIfAny.setError("Required");
                    } else {
                        DatabaseReference deliveryBoyDetailsDataRef =  SellerloginDetails.child(sellerDetails.getUserId());
                        deliveryBoyDetailsDataRef.child("approvalStatus").setValue("Rejected");
                        deliveryBoyDetailsDataRef.child("commentsIfAny").setValue(commentsIfAny.getText().toString());
                        dialog.dismiss();
                        Intent intent = getIntent();
                        startActivity(intent);
                        Toast.makeText(ApproveSellersActivity.this, "Rejected", Toast.LENGTH_SHORT).show();
                    }
                }
            });

/*
            mView.setOnTouchListener ( new OnSwipeTouchListener( ApproveSellersActivity.this ) {
                public void onSwipeRight() {
                    swipeCount = swipeCount - 1;
                    if (swipeCount == 0) {
                        detailsLayout.setVisibility ( View.VISIBLE );
                        storeLogo.setVisibility ( View.INVISIBLE );
                        aadharProof.setVisibility ( View.INVISIBLE );
                        fssaiCertificate.setVisibility ( View.INVISIBLE );
                        applicantHeader.setVisibility ( View.VISIBLE );
                        imageHeaderName.setText ( "Seller details" );
                    }
                    if (swipeCount == 1) {

                        detailsLayout.setVisibility ( View.INVISIBLE );
                        storeLogo.setVisibility ( View.VISIBLE );
                        aadharProof.setVisibility ( View.INVISIBLE );
                        applicantHeader.setVisibility ( View.VISIBLE );
                        fssaiCertificate.setVisibility ( View.INVISIBLE );
                        imageHeaderName.setText ( "Store Logo" );
                        Picasso.get ().load ( sellerDetails.getStoreLogo () ).into ( storeLogo );

                    } else if (swipeCount == 2) {

                        detailsLayout.setVisibility ( View.INVISIBLE );
                        storeLogo.setVisibility ( View.INVISIBLE );
                        aadharProof.setVisibility ( View.VISIBLE );
                        applicantHeader.setVisibility ( View.VISIBLE );
                        fssaiCertificate.setVisibility ( View.INVISIBLE );
                        imageHeaderName.setText ( "Aadhar Id" );
                        Picasso.get ().load ( sellerDetails.getAadharImage () ).into ( aadharProof );
                    }
                }

                public void onSwipeLeft() {
                    swipeCount = swipeCount + 1;
                    if (swipeCount == 1) {
                        detailsLayout.setVisibility ( View.INVISIBLE );
                        storeLogo.setVisibility ( View.VISIBLE );
                        aadharProof.setVisibility ( View.INVISIBLE );
                        imageHeaderName.setText ( "Store Logo" );
                        fssaiCertificate.setVisibility ( View.INVISIBLE );
                        Picasso.get ().load ( sellerDetails.getStoreLogo () ).into ( storeLogo );
                    } else if (swipeCount == 2) {
                        detailsLayout.setVisibility ( View.INVISIBLE );
                        storeLogo.setVisibility ( View.INVISIBLE );
                        aadharProof.setVisibility ( View.VISIBLE );
                        fssaiCertificate.setVisibility ( View.INVISIBLE );
                        imageHeaderName.setText ( "Aadhar Id" );
                        Picasso.get ().load ( sellerDetails.getAadharImage () ).into ( aadharProof );
                    } else if (swipeCount == 3) {
                        detailsLayout.setVisibility ( View.INVISIBLE );
                        storeLogo.setVisibility ( View.INVISIBLE );
                        aadharProof.setVisibility ( View.INVISIBLE );
                        fssaiCertificate.setVisibility ( View.VISIBLE );
                        imageHeaderName.setText ( "Fssai Certificate" );
                        Picasso.get ().load ( sellerDetails.getFssaiCertificateImage () ).into ( fssaiCertificate );
                    }
                }
            } );*/

            cancelButton.setOnClickListener (v -> {
                dialog.dismiss();
                swipeCount = 0;
            });
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
        else if (id == R.id.ordertracking) {
            Intent intent = new Intent(getApplicationContext(), OrderTrackingActivity.class);
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
        } else if (id == R.id.adminfare) {
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
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ApproveSellersActivity.this);
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
                    Intent intent = new Intent(ApproveSellersActivity.this,
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

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }

    private class backGroundClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids)
        {
            DatabaseReference deliveryBoyDetailsDataRef = SellerloginDetails.child ( sellerDetails.getUserId () );
            deliveryBoyDetailsDataRef.child ( "approvalStatus" ).setValue ( "Approved" );
            deliveryBoyDetailsDataRef.child ( "paymentType" ).setValue ( variableAssBas );
            deliveryBoyDetailsDataRef.child("formattedDate").setValue(DateUtils.fetchFormatedCurrentDate());
            return null;
        }
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
        if (!((Activity) ApproveSellersActivity.this).isFinishing()) {
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