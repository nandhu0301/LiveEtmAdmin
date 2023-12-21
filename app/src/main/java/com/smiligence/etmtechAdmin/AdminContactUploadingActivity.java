package com.smiligence.etmtechAdmin;

import static com.smiligence.etmtechAdmin.common.Constant.DELIVERY_DETAILS;
import static com.smiligence.etmtechAdmin.common.Constant.METROZ_STORE_TIMINGS;
import static com.smiligence.etmtechAdmin.common.Constant.TITILE_ADMIN_CONTACT_DETAILS;
import static com.smiligence.etmtechAdmin.common.Constant.TITLE_APPROVESTOREPARTNER;
import static com.smiligence.etmtechAdmin.common.Constant.TITLE_CATEGORY;
import static com.smiligence.etmtechAdmin.common.Constant.TITLE_DASHBOARD;
import static com.smiligence.etmtechAdmin.common.Constant.TITLE_DISCOUNT;
import static com.smiligence.etmtechAdmin.common.Constant.TITLE_MAINTAINBANNER;
import static com.smiligence.etmtechAdmin.common.Constant.TITLE_STOREHISTORY;
import static com.smiligence.etmtechAdmin.common.Constant.USER_DETAILS_TABLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmtechAdmin.bean.ContactDetails;
import com.smiligence.etmtechAdmin.bean.MenuModel;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.Constant;
import com.smiligence.etmtechAdmin.common.TextUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AdminContactUploadingActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    DatabaseReference contactDetailsRef,storeTimingMaintenanceDataRef , userDetailsDataRef;
    NavigationView navigationView;
    public static Menu menuNav;
    public static View mHeaderView;
    public static TextView textViewUsername;
    public static TextView textViewEmail;
    public static ImageView imageView;
    TextView reviewCount;
    TextView dateText, hourText;
    String saved_username;
    CardView shopStartTime, shopEndTime;
   // TextView shopStartTimeTextView, shopEndTimeTextView;
    Button addStoreTimings;
    Button updateStoreTimings;
    String pattern = "hh:mm aa";
    SimpleDateFormat sdf = new SimpleDateFormat ( pattern );
    String currentTime;
    DateFormat date;
    Date currentLocalTime;
    public static String DATE_FORMAT = "MMMM dd, YYYY";
    String currentDateAndTime;
    EditText whatsAppContact;
    Button uploadDetails;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_contact_uploading);

        expandableListView = findViewById(R.id.expandableListView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        disableAutofill();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(TITILE_ADMIN_CONTACT_DETAILS);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.discounts);
        navigationView.setNavigationItemSelectedListener(AdminContactUploadingActivity.this);
        LinearLayout linearLayout = (LinearLayout) navigationView.getHeaderView(0);
        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);
        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);
        textViewUsername.setText(DashBoardActivity.roleName);
        textViewEmail.setText(DashBoardActivity.userName);
       /* reviewCount=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.reviewApproval));
        reviewCount.setGravity(Gravity.CENTER_VERTICAL);
        reviewCount.setTypeface(null, Typeface.BOLD);
        reviewCount.setTextColor(getResources().getColor(R.color.redColor));*/

        dateText = findViewById(R.id.setdatetext);
        hourText = findViewById(R.id.hourText);

        SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        SharedPreferences.Editor loginEditor = loginSharedPreferences.edit();
        saved_username = loginSharedPreferences.getString("userNameStr", "");

       // constant

        contactDetailsRef= CommonMethods.fetchFirebaseDatabaseReference("Metroz Contact");
        storeTimingMaintenanceDataRef = CommonMethods.fetchFirebaseDatabaseReference( METROZ_STORE_TIMINGS );
        userDetailsDataRef = CommonMethods.fetchFirebaseDatabaseReference(USER_DETAILS_TABLE);

        uploadDetails=findViewById(R.id.uploadDetails);

       Calendar cal = Calendar.getInstance ();
        currentLocalTime = cal.getTime ();

        date = new SimpleDateFormat( "HH:mm aa" );
        currentTime = date.format ( currentLocalTime );
        DateFormat dateFormat = new SimpleDateFormat ( DATE_FORMAT );
        currentDateAndTime = dateFormat.format ( new Date() );


        prepareMenuData();

        populateExpandableList();


    //    final Query roleNameQuery = userDetailsDataRef.orderByChild (PHONE_NUM_COLUMN ).equalTo ( String.valueOf (userName) );

       /* roleNameQuery.addListenerForSingleValueEvent ( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( DataSnapshot snap : dataSnapshot.getChildren () ) {
                    UserDetails loginUserDetailslist = snap.getValue ( UserDetails.class );
                    textViewUsername.setText ( loginUserDetailslist.getFirstName ().toUpperCase () );
                    textViewEmail.setText ( loginUserDetailslist.getRoleName ().toUpperCase () );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );*/


        whatsAppContact=findViewById(R.id.whatsappcontact);

   /*     storeTimingMaintenanceDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()!=0){

                    StoreTimings storeTimings=dataSnapshot.getValue(StoreTimings.class);
             //       shopStartTimeTextView.setText(storeTimings.getShopStartTime());
            //        shopEndTimeTextView.setText(storeTimings.getShopEndTime());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        contactDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0){
                    ContactDetails contactDetails=dataSnapshot.getValue(ContactDetails.class);
                    whatsAppContact.setText(contactDetails.getWhatsAppContact());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        uploadDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String whatsAppNumberString=whatsAppContact.getText().toString().trim();
                if(!"".equalsIgnoreCase(whatsAppNumberString)&&
                        !TextUtils.validatePhoneNumber(whatsAppNumberString)) {
                    whatsAppContact.setError("Enter Valid Phone Number");
                }else {
                    ContactDetails userDetails = new ContactDetails();
                    userDetails.setWhatsAppContact(whatsAppNumberString);
                    contactDetailsRef.setValue(userDetails);
                    Toast.makeText(AdminContactUploadingActivity.this, "WhatsApp contact uploaded Successfully",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });

       /* shopStartTime.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance ();
                final int hour = mcurrentTime.get ( Calendar.HOUR_OF_DAY );
                int minute = mcurrentTime.get ( Calendar.MINUTE );
                TimePickerDialog mTimePicker;

                mTimePicker = new TimePickerDialog( AdminContactUploadingActivity.this, new TimePickerDialog.OnTimeSetListener () {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if (selectedHour < 12) {
                            shopStartTimeTextView.setText(pad(selectedHour) + ":" + pad(selectedMinute)+ " AM");

                        } else {
                            //shopStartTimeTextView.setText(pad(selectedHour) + ":" + pad(selectedMinute)+ " PM");

                        }
                    }
                }, hour, minute, true );//Yes 24 hour time

                mTimePicker.setTitle ( "Select Time" );
                mTimePicker.show ();

            }
        } );

        shopEndTime.setOnClickListener ( new View.OnClickListener ()
        {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance ();
                final int hour = mcurrentTime.get ( Calendar.HOUR_OF_DAY );
                int minute = mcurrentTime.get ( Calendar.MINUTE );
                TimePickerDialog mTimePicker;

                mTimePicker = new TimePickerDialog ( AdminContactUploadingActivity.this, new TimePickerDialog.OnTimeSetListener () {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if (selectedHour < 12) {
                            shopEndTimeTextView.setText(pad(selectedHour) + ":" + pad(selectedMinute)+ " AM");
                        } else {
                            shopEndTimeTextView.setText(pad(selectedHour) + ":" + pad(selectedMinute)+ " PM");
                        }
                    }
                }, hour, minute, true );//Yes 24 hour time

                mTimePicker.setTitle ( "Select Time" );
                mTimePicker.show ();
            }
        } );

        addStoreTimings.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v)
            {

                if (shopStartTimeTextView.getText().toString().equals("") && shopEndTimeTextView.getText().toString().equals("") )
                {
                    Toast.makeText(AdminContactUploadingActivity.this, "Please update store timings", Toast.LENGTH_SHORT).show();
                }
                else if (shopStartTimeTextView.getText().toString().equals(""))
                {
                    Toast.makeText(AdminContactUploadingActivity.this, "Please update store starting time", Toast.LENGTH_SHORT).show();
                }else if (shopEndTimeTextView.getText().toString().equals(""))
                {
                    Toast.makeText(AdminContactUploadingActivity.this, "Please update store ending time", Toast.LENGTH_SHORT).show();
                }else
                {

                    storeTimingMaintenanceDataRef.child ( "shopStartTime" ).setValue ( shopStartTimeTextView.getText ().toString () );
                    storeTimingMaintenanceDataRef.child ( "shopEndTime" ).setValue ( shopEndTimeTextView.getText ().toString () );
                    storeTimingMaintenanceDataRef.child("createdDate").setValue(  DateUtils.fetchCurrentDateTime());
                    updateStoreTimings.setEnabled ( true );
                    updateStoreTimings.setVisibility(View.VISIBLE);
                    addStoreTimings.setEnabled(false);
                    addStoreTimings.setVisibility(View.INVISIBLE);
                    shopStartTime.setEnabled ( false );
                    shopEndTime.setEnabled ( false );

                }
            }
        });



        updateStoreTimings.setOnClickListener( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                updateStoreTimings.setEnabled(false);
                addStoreTimings.setEnabled (true);
                updateStoreTimings.setVisibility(View.INVISIBLE);
                addStoreTimings.setVisibility(View.VISIBLE);

                shopStartTime.setEnabled ( true );
                shopEndTime.setEnabled ( true );
                shopStartTime.setCardBackgroundColor ( getResources ().getColor ( R.color.white ) );
                shopEndTime.setCardBackgroundColor ( getResources ().getColor ( R.color.white ) );

            }
        } );

*/
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
        } else if (id == R.id.logoutscreen) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AdminContactUploadingActivity.this);
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
                    Intent intent = new Intent(AdminContactUploadingActivity.this,
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

    public String pad(int input)
    {

        String str = "";

        if (input >= 10) {

            str = Integer.toString(input);
        } else {
            str = "0" + Integer.toString(input);

        }
        return str;
    }



    private void prepareMenuData() {

        MenuModel menuModel = new MenuModel(Constant.TITLE_DASHBOARD, true, false); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }
        menuModel = new MenuModel(Constant.TITLE_APPROVESTOREPARTNER, true, true); //Menu of Java Tutorials
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }
        menuModel = new MenuModel(Constant.TITLE_DISCOUNT, true, true); //Menu of Java Tutorials
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }
        menuModel = new MenuModel(Constant.TITLE_MAINTAINBANNER, true, true); //Menu of Java Tutorials
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }


        List<MenuModel> childModelsList = new ArrayList<>();

        childModelsList = new ArrayList<>();
        menuModel = new MenuModel(Constant.TITLE_MAINTAININGINPUTS, true, true); //Menu of Python Tutorials
        headerList.add(menuModel);
        MenuModel  childModel = new MenuModel(Constant.TITLE_CATEGORY, false, false);
        childModelsList.add(childModel);
        childModel = new MenuModel(Constant.DELIVERY_DETAILS, false, false);
        childModelsList.add(childModel);


        if (menuModel.hasChildren) {
            childList.put(menuModel, childModelsList);
        }


        menuModel = new MenuModel(Constant.STORE_HISTORY, true, true); //Menu of Java Tutorials
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }
        menuModel = new MenuModel("Logout", true, true); //Menu of Java Tutorials
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }
    }





    private void populateExpandableList() {

        expandableListAdapter = new com.smiligence.etmtechAdmin.Adapter.ExpandableListAdapter(this, headerList, childList);
        expandableListView.setAdapter(expandableListAdapter);


        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                v.setSelected(true);
                MenuModel model = headerList.get(groupPosition);

                if (model.menuName.equals(TITLE_DASHBOARD)) {
                    Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else if (model.menuName.equals(TITLE_APPROVESTOREPARTNER)) {
                    Intent intent = new Intent(getApplicationContext(), ApproveSellersActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (model.menuName.equals(TITLE_DISCOUNT)) {
                    Intent intent = new Intent(getApplicationContext(), DiscountActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (model.menuName.equals(TITLE_MAINTAINBANNER)) {
                    Intent intent = new Intent(getApplicationContext(), AddAdvertisementActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else if (model.menuName.equals(TITLE_STOREHISTORY)) {
                    Intent intent = new Intent(getApplicationContext(), StoreHistory.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }


                else if (model.menuName.equals(TITLE_CATEGORY)) {
                    Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (model.menuName.equals(DELIVERY_DETAILS)) {
                    Intent intent = new Intent(getApplicationContext(), AdminContactUploadingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }  else if (model.menuName.equals("Logout")) {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AdminContactUploadingActivity.this);
                    bottomSheetDialog.setContentView(R.layout.logout_confirmation);
                    Button logout = bottomSheetDialog.findViewById(R.id.logout);
                    Button stayinapp = bottomSheetDialog.findViewById(R.id.stayinapp);

                    bottomSheetDialog.show();
                    bottomSheetDialog.setCancelable(false);

                    logout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            bottomSheetDialog.dismiss();
                        }
                    });

                    stayinapp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomSheetDialog.dismiss();
                            navigationView.setCheckedItem(R.id.dashboard);
                        }
                    });

                }

                return false;
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (childList.get(headerList.get(groupPosition)) != null) {
                    MenuModel model = childList.get(headerList.get(groupPosition)).get(childPosition);
                    if (model.menuName.equals(TITLE_CATEGORY)) {
                        Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else if (model.menuName.equals(DELIVERY_DETAILS)) {
                        Intent intent = new Intent(getApplicationContext(), AdminContactUploadingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
                return false;
            }
        });

    }



}