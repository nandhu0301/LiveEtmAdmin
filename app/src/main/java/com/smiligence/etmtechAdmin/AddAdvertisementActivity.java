package com.smiligence.etmtechAdmin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.smiligence.etmtechAdmin.bean.AdvertisementDetails;
import com.smiligence.etmtechAdmin.bean.ItemDetails;
import com.smiligence.etmtechAdmin.bean.UserDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.Constant;
import com.smiligence.etmtechAdmin.common.DateUtils;
import com.smiligence.etmtechAdmin.common.NetworkStateReceiver;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.smiligence.etmtechAdmin.common.Constant.ADVERTISEMT_DETAILS_FIREBASE_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.DETAILS_INSERTED;
import static com.smiligence.etmtechAdmin.common.Constant.PRODUCT_DETAILS;
import static com.smiligence.etmtechAdmin.common.Constant.SELLER_DETAILS_TABLE;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddAdvertisementActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,NetworkStateReceiver.NetworkStateReceiverListener {

    NavigationView navigationView;
    public static TextView textViewUsername;
    public static TextView textViewEmail;
    public static View mHeaderView;
    public static Menu menuNav;
    Spinner storespinner ;
    Spinner  itemspinner;
    TextView itemtxt ;
    CheckBox checkBox ;
    ArrayList<String> storeNamearrayList = new ArrayList<>();
    ArrayList<String> itemNamelist = new ArrayList<>();
    ArrayAdapter<String> adv_storename_spinner;
    ArrayAdapter<String> adv_itemname_spinner;
    RadioGroup ad_TypeRadioGroup,ad_MediaRadioGroup ;
    String  advertisementSelectedString ,adv_MediaSelectedString ,adv_PricingSelectedString,advertisementstorename,advertisementitemname;
    int  adv_SelectedDuration ,adv_Priority ,adv_Selectedstore ,ad_itemname;
    DatabaseReference sellerDataRef,advertisementDataRef ,itemDataRef;
    String adv_TypeString ;
    String userid  ;
    int itemid ;
    boolean itemselect = false;
    LinearLayout mediaImage;
    Button pickMedia, uploadAdvertisement;
    static int PICK_IMAGE_REQUEST = 100;
    ImageView advertisementImage;
    Uri mimageuri;
    Spinner durationspinner ;
    ArrayAdapter<CharSequence>   adv_duration_spinner ,adv_pricing_spinner_adapter , adve_itemname_spinner;
    private DatePickerDialog.OnDateSetListener purchaseDateSetListener;
    String dateDayCal, monthCal;
    TextView dateText, hourText, datetextoneWeek;
    String advertisementScheduleDate, advertisementScheduledTime;
    static String oneDayAfterScheduledDate;
    String advertismentStartDurtionstring, advertisementExpiringDuration ;
    Button setDate, setTime  ;
    static int year, month, day;
    SearchableSpinner adv_Pricing_spinner;
    EditText advertisingAmount;
    StorageReference advertisementStorageRef;
    StorageTask mUploadTask;
    long addMaxId = 0;
    AdvertisementDetails advertisementDetails = new AdvertisementDetails();
    String adv_TypeSelectedString;
    private NetworkStateReceiver networkStateReceiver;
    androidx.appcompat.app.AlertDialog alertDialog;
    ViewGroup viewGroup;
   Button advertisement_btn;
    String pattern = "hh:mm";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    String currentTime;
    DateFormat date;
    Date currentLocalTime;
    String currentDateAndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_advertisement);

        androidx.appcompat.widget.Toolbar toolbar1 = findViewById(R.id.toolbar);
        toolbar1.setTitle(Constant.TITLE_MAINTAINBANNER);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar1, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(AddAdvertisementActivity.this);
        mHeaderView = navigationView.getHeaderView(0);
        navigationView.setCheckedItem(R.id.Add_Advertisment);

        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);
        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);

        textViewUsername.setText(DashBoardActivity.roleName);
        textViewEmail.setText(DashBoardActivity.userName);

        sellerDataRef  = CommonMethods.fetchFirebaseDatabaseReference(SELLER_DETAILS_TABLE);
        advertisementStorageRef = CommonMethods.fetchFirebaseStorageReference(ADVERTISEMT_DETAILS_FIREBASE_TABLE);
        advertisementDataRef = CommonMethods.fetchFirebaseDatabaseReference(ADVERTISEMT_DETAILS_FIREBASE_TABLE);
        itemDataRef = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS);

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

        storespinner = findViewById(R.id.storespinner);
        itemspinner = findViewById(R.id.itemspinner);
        ad_TypeRadioGroup = findViewById(R.id.ad_typeradioGroup);
        ad_MediaRadioGroup = findViewById(R.id.admedia);
        setTime = findViewById(R.id.settime);
        setDate = findViewById(R.id.setdate);
        hourText = findViewById(R.id.hourText);
        dateText = findViewById(R.id.setdatetext);
        pickMedia = findViewById(R.id.pickMedia);
        advertisementImage = findViewById(R.id.imageView);
        mediaImage = findViewById(R.id.imagelayout);
        durationspinner = findViewById(R.id.durationspinner);
        adv_Pricing_spinner = findViewById(R.id.ad_pricing_spinner);
        advertisingAmount = findViewById(R.id.amountForAdvertisements);
        uploadAdvertisement = findViewById(R.id.uploadadvertisement);
        checkBox = findViewById(R.id.checkbox);
        itemtxt = findViewById(R.id.item);
       advertisement_btn = findViewById(R.id.view_advertisement);

        storespinner.setVisibility(View.INVISIBLE);
        checkBox.setVisibility(View.INVISIBLE);
        itemtxt.setVisibility(View.INVISIBLE);
        itemspinner.setVisibility(View.INVISIBLE);



        storeNamearrayList.add("Select Category");
        itemNamelist.add("Select Item");

        sellerDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()>0){
                    storeNamearrayList.clear();
                    storeNamearrayList.add("Select Category");
                    for (DataSnapshot sellershap :snapshot.getChildren()){

                        UserDetails  userDetails = sellershap.getValue(UserDetails.class);

                        if (userDetails.getApprovalStatus().equals("Approved")) {
                            storeNamearrayList.add(userDetails.getStoreName());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//storenamespinner
        adv_storename_spinner = new ArrayAdapter<>(AddAdvertisementActivity.this, android.R.layout.simple_spinner_item, storeNamearrayList);
        adv_storename_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storespinner.setAdapter(adv_storename_spinner);


        //itemnamespinner
        adv_itemname_spinner = new ArrayAdapter<>(AddAdvertisementActivity.this, android.R.layout.simple_spinner_item, itemNamelist);
        adv_itemname_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemspinner.setAdapter(adv_itemname_spinner);

//durarationspinner
        adv_duration_spinner = ArrayAdapter.createFromResource(AddAdvertisementActivity.this, R.array.advertisement_duration,
                R.layout.support_simple_spinner_dropdown_item);
        adv_duration_spinner.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        durationspinner.setAdapter(adv_duration_spinner);



        //advertisimend placing
        adv_pricing_spinner_adapter = ArrayAdapter
                .createFromResource(AddAdvertisementActivity.this, R.array.advertisement_price,
                        R.layout.support_simple_spinner_dropdown_item);
        adv_pricing_spinner_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adv_Pricing_spinner.setAdapter(adv_pricing_spinner_adapter);
        adv_Pricing_spinner.setTitle("Select Advertisement Placing");


        ad_TypeRadioGroup.setOnCheckedChangeListener((RadioGroup.OnCheckedChangeListener) (group, checkedId) -> {
            checkedId = ad_TypeRadioGroup.getCheckedRadioButtonId();


            RadioButton radioButton = (RadioButton) findViewById(checkedId);
            adv_TypeString = radioButton.getText().toString();
            if (adv_TypeString.equals("Stores")) {
                storespinner.setVisibility(View.VISIBLE);
            }

        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()){
                    itemspinner.setVisibility(View.VISIBLE);
                    itemNameSelectFunction(advertisementstorename);
                }  else {
                    itemNamelist.clear();
                    itemNamelist.add("Select Item");
                    adv_itemname_spinner.notifyDataSetChanged();

                    itemspinner.setVisibility(View.INVISIBLE);
                }
            }
        });
        advertisement_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent = new Intent(AddAdvertisementActivity.this,ViewAdvertisementActivity.class);
                startActivity(intent);
            }
        });

        storespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                advertisementstorename = adapterView.getItemAtPosition(position).toString();
                adv_Selectedstore = position;


                if (!advertisementstorename.equals("Select Category")) {
                    checkBox.setVisibility(View.VISIBLE);
                    itemtxt.setVisibility(View.VISIBLE);
                    itemNamelist.clear();
                    itemNamelist.add("Select Item");
                    adv_itemname_spinner.notifyDataSetChanged();
                    if (checkBox.isChecked()) {
                        itemspinner.setVisibility(View.VISIBLE);
                        itemNameSelectFunction(advertisementstorename);
                    }
                }
                //  Toast.makeText(AddAdvertisementActivity.this, ""+advertisementstorename, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        itemspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                advertisementitemname = adapterView.getItemAtPosition(position).toString();
                ad_itemname = position;

                // Toast.makeText(AddAdvertisementActivity.this, ""+advertisementitemname, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        durationspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                advertisementSelectedString = parent.getItemAtPosition(position).toString();
                adv_SelectedDuration = position;

                if (adv_SelectedDuration == 0) {
                    //    Toast.makeText(AddAdvertisementActivity.this, "Select the Advertiement duration", Toast.LENGTH_SHORT).show();
                    return;
                } else if (adv_SelectedDuration == 1) {

                    SetDateTimePicker();

                    purchaseDateSetListener = (datePicker, year, month, day) -> {
                        final long millisToAdd = 3_600_000; //two hours
                        calculateDate(year, month, day, millisToAdd);
                    };
                    return;
                } else if (adv_SelectedDuration == 2) {

                    SetDateTimePicker();
                    purchaseDateSetListener = (datePicker, year, month, day) -> {

                        month = month + 1;
                        if (day == 0 || day < 10) {
                            dateDayCal = "0" + day;
                        } else if (day >= 10) {
                            dateDayCal = String.valueOf(day);
                        }
                        if (month == 0 || month < 10) {
                            monthCal = "0" + month;
                        } else if (month >= 10) {
                            monthCal = String.valueOf(month);
                        }

                        String date = dateDayCal + "-" + monthCal + "-" + year;
                        dateText.setText(date);

                        advertisementScheduleDate = dateText.getText().toString();
                        advertisementScheduledTime = hourText.getText().toString();

                        String dt = advertisementScheduleDate;  // Start date
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
                        Calendar c = Calendar.getInstance();
                        try {
                            c.setTime(sdf.parse(dt));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        c.add(Calendar.DATE, 1);  // number of days to add
                        dt = sdf.format(c.getTime());

                        oneDayAfterScheduledDate = dt;

                        String time = advertisementScheduledTime;
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");

                        final String dateString = time;
                        final long millisToAdd = 86_400_000 - 1; //23 hours 59 sec

                        DateFormat format = new SimpleDateFormat("hh:mm a");

                        Date d = null;
                        try {
                            d = format.parse(dateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        d.setTime(d.getTime() + millisToAdd);

                        String hoursAgo24 = timeformat.format(d.getTime());

                        advertismentStartDurtionstring = advertisementScheduleDate + "," + time;
                        advertisementExpiringDuration = oneDayAfterScheduledDate + "," + hoursAgo24;

                    };
                    return;
                } else if (adv_SelectedDuration == 3) {
                    SetDateTimePicker();

                    purchaseDateSetListener = (datePicker, year, month, day) -> {

                        month = month + 1;
                        if (day == 0 || day < 10) {
                            dateDayCal = "0" + day;
                        } else if (day >= 10) {
                            dateDayCal = String.valueOf(day);
                        }
                        if (month == 0 || month < 10) {
                            monthCal = "0" + month;
                        } else if (month >= 10) {
                            monthCal = String.valueOf(month);
                        }

                        String date = dateDayCal + "-" + monthCal + "-" + year;
                        dateText.setText(date);

                        advertisementScheduleDate = dateText.getText().toString();
                        advertisementScheduledTime = hourText.getText().toString();

                        String dt = advertisementScheduleDate;  // Start date
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
                        Calendar c = Calendar.getInstance();
                        try {
                            c.setTime(sdf.parse(dt));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        c.add(Calendar.DATE, 7);  // number of days to add
                        dt = sdf.format(c.getTime());


                        oneDayAfterScheduledDate = dt;


                        String time = advertisementScheduledTime;
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");


                        final String dateString = time;
                        final long millisToAdd = 604_800_000; //23 hours 59 sec

                        DateFormat format = new SimpleDateFormat("hh:mm a");

                        Date d = null;
                        try {
                            d = format.parse(dateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        d.setTime(d.getTime() + millisToAdd);

                        String hoursAgo24 = timeformat.format(d.getTime());


                        advertismentStartDurtionstring = advertisementScheduleDate + "," + time;
                        advertisementExpiringDuration = oneDayAfterScheduledDate + "," + hoursAgo24;
                    };
                }


            }

            private void calculateDate(int year, int month, int day, long millisToAdd) {
                month = month + 1;
                if (day == 0 || day < 10) {
                    dateDayCal = "0" + day;
                } else if (day >= 10) {
                    dateDayCal = String.valueOf(day);
                }
                if (month == 0 || month < 10) {
                    monthCal = "0" + month;
                } else if (month >= 10) {
                    monthCal = String.valueOf(month);
                }


                String date = dateDayCal + "-" + monthCal + "-" + year;

                dateText.setText(date);
                advertisementScheduleDate = dateText.getText().toString();
                advertisementScheduledTime = hourText.getText().toString();


                String dtStart = advertisementScheduledTime;

                SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");

                final String dateString = dtStart;


                DateFormat format = new SimpleDateFormat("hh:mm a");

                Date d = null;
                try {
                    d = format.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                d.setTime(d.getTime() + millisToAdd);

                String hourago = timeformat.format(d.getTime());

                advertismentStartDurtionstring = advertisementScheduledTime;
                advertisementExpiringDuration = hourago;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        adv_Pricing_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adv_PricingSelectedString = parent.getItemAtPosition(position).toString();
                adv_Priority = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ad_MediaRadioGroup.setOnCheckedChangeListener((RadioGroup.OnCheckedChangeListener) (group, checkedId) -> {
            checkedId = ad_MediaRadioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = (RadioButton) findViewById(checkedId);
            adv_MediaSelectedString = radioButton.getText().toString();


            if (adv_MediaSelectedString.equals("Image")) {
                //     mediaImage.setVisibility(View.VISIBLE);

            }

        });


        pickMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });


        uploadAdvertisement.setOnClickListener(v -> {
            long currentTimeMillis = System.currentTimeMillis();
            Date currentTimee = new Date(currentTimeMillis);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String currentTimeString = dateFormat.format(currentTimee);
            Log.e("currentTimeString",currentTimeString);

            if (ad_TypeRadioGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(getApplicationContext(), "Select advertising agent", Toast.LENGTH_SHORT).show();
            }
            else if (adv_TypeString.equals("Stores")) {
                if (storespinner.getSelectedItem().equals("Select Category")) {
                    Toast.makeText(getApplicationContext(), "Select store for advertisment", Toast.LENGTH_SHORT).show();
                }
                //   else   if (checkBox.isChecked()) {
                if (checkBox.isChecked() && itemspinner.getSelectedItem().equals("Select Category")) {
                    Toast.makeText(getApplicationContext(), "Select store item for advertisment", Toast.LENGTH_SHORT).show();
                }

                //    }
                else if (ad_MediaRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Select advertisement type", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mimageuri == null) {
                    Toast.makeText(getApplicationContext(), "Select  advertising Media Image", Toast.LENGTH_SHORT).show();
                    return;
                } else if (adv_SelectedDuration == 0) {
                    Toast.makeText(getApplicationContext(), "Select advertisement Duration", Toast.LENGTH_SHORT).show();
                    return;
                } else if (hourText.getText().toString().equals("") || hourText.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "Set Time Duration", Toast.LENGTH_SHORT).show();
                    return;
                } else if (dateText.getText().toString().equals("") || dateText.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "Set Date Duration", Toast.LENGTH_SHORT).show();
                    return;
                }


                else if (adv_Priority == 0) {
                    Toast.makeText(getApplicationContext(), "Select advertisement Screen", Toast.LENGTH_SHORT).show();
                    return;
                } else if (advertisingAmount.getText().toString().equals("") || advertisingAmount.getText().toString() == null) {

                    advertisingAmount.setError("Required");
                    Toast.makeText(this, "Enter advertising amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if(DateUtils.fetchFormatedCurrentDate().equals(dateText.getText().toString())){
                    try {

                        if ((sdf.parse(currentTimeString).compareTo(sdf.parse(hourText.getText().toString())) == 0) ||
                                (sdf.parse(currentTimeString).compareTo(sdf.parse(hourText.getText().toString())) == 1)){

                            Toast.makeText(AddAdvertisementActivity.this, "Current date and Current time cannot be equal  ", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (mimageuri != null) {


                            if (advertisementitemname !=null) {
                                itemDataRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.getChildrenCount() > 0) {
                                            itemNamelist.clear();

                                            for (DataSnapshot itemnamesanp : snapshot.getChildren()) {
                                                ItemDetails itemDetails = itemnamesanp.getValue(ItemDetails.class);
                                                if (itemDetails.getItemName().equals(advertisementitemname)) {
                                                    itemid = itemDetails.getItemId();
                                                    itemselect = true;

                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }


                            sellerDataRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getChildrenCount()>0){
                                        storeNamearrayList.clear();

                                        for (DataSnapshot sellershap :snapshot.getChildren()){
                                            UserDetails  userDetails = sellershap.getValue(UserDetails.class);

                                            if (userDetails.getApprovalStatus().equals("Approved")) {
                                                if (userDetails.getStoreName().equals(advertisementstorename)) {
                                                    userid = userDetails.getUserId();
                                                    UploadImage();
                                                }
                                            }
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            return;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else if (mimageuri != null) {


                    if (advertisementitemname !=null) {
                        itemDataRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() > 0) {
                                    itemNamelist.clear();

                                    for (DataSnapshot itemnamesanp : snapshot.getChildren()) {
                                        ItemDetails itemDetails = itemnamesanp.getValue(ItemDetails.class);
                                        if (itemDetails.getItemName().equals(advertisementitemname)) {
                                            itemid = itemDetails.getItemId();
                                            itemselect = true;

                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }


                    sellerDataRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount()>0){
                                storeNamearrayList.clear();

                                for (DataSnapshot sellershap :snapshot.getChildren()){
                                    UserDetails  userDetails = sellershap.getValue(UserDetails.class);

                                    if (userDetails.getApprovalStatus().equals("Approved")) {
                                        if (userDetails.getStoreName().equals(advertisementstorename)) {
                                            userid = userDetails.getUserId();
                                            UploadImage();
                                        }
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    return;
                }

            } else if (adv_TypeString.equals("Instruction Ad")) {
                if (ad_MediaRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Select advertisement type", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mimageuri == null) {
                    Toast.makeText(getApplicationContext(), "Select advertising Media", Toast.LENGTH_SHORT).show();
                    return;
                } else if (adv_Priority == 0) {
                    Toast.makeText(getApplicationContext(), "Select advertisement Priority", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mimageuri != null) {




                    // storespinner.getSelectedItem()
                    UploadImage();
                    return;
                }

            }


        });

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(AddAdvertisementActivity.this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public String pad(int input) {
        String str = "";
        if (input >= 10) {
            str = Integer.toString(input);
        } else {
            str = "0" + Integer.toString(input);
        }
        return str;
    }



    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    private void SetDateTimePicker() {
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                com.wdullaer.materialdatetimepicker.time.TimePickerDialog tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                        new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
                                if (hourOfDay < 12) {
                                    hourText.setText(pad(hourOfDay) + ":" + pad(minute) + " AM");

                                } else {
                                    hourText.setText(pad(hourOfDay) + ":" + pad(minute) + " PM");

                                }
                            }
                        }, hour, minute, true);
                tpd.show(getFragmentManager(), "TimePickerDialog");
            }
        });

        setDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    AddAdvertisementActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    purchaseDateSetListener,
                    year, month, day);
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
    }













    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mimageuri = data.getData();
            Glide.with(getApplicationContext()).load(mimageuri).into(advertisementImage);
        }
    }


    public void UploadImage() {


        if (mimageuri != null) {
            final SweetAlertDialog pDialog = new SweetAlertDialog(AddAdvertisementActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#08C5F4"));
            pDialog.setTitleText("Uploading Advertisement.....");
            pDialog.setCancelable(false);
            pDialog.show();
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mimageuri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();
            StorageReference fileRef = advertisementStorageRef.child("Advertisement/" + System.currentTimeMillis());
            mUploadTask = fileRef.putBytes(data).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                final Uri downloadUrl = urlTask.getResult();
                String creationDate = DateUtils.fetchCurrentDate();
                advertisementDetails.setImage(String.valueOf(downloadUrl));
                advertisementDetails.setId(String.valueOf(addMaxId + 1));
                advertisementDetails.setCreationdate(creationDate);
                advertisementDetails.setAdvertisingAgent(adv_TypeString);
                advertisementDetails.setAdvertisingBrandName("");
                advertisementDetails.setAdvertisingMedia(adv_MediaSelectedString);
                advertisementDetails.setAdvertisingType(adv_TypeSelectedString);
                advertisementDetails.setAdvertisementPlacing((adv_PricingSelectedString));
                advertisementDetails.setAdvertisementpriority(String.valueOf(adv_Priority));
                advertisementDetails.setAdvertisingDuration(advertisementSelectedString);
                advertisementDetails.setScheduledDate(advertisementScheduleDate);
                advertisementDetails.setScheduledTime(advertisementScheduledTime);
                advertisementDetails.setAdvertisementstatus("Active");
                advertisementDetails.setStoreName(advertisementstorename);
                advertisementDetails.setStoreId(userid);
                advertisementDetails.setItemName(advertisementitemname);
                advertisementDetails.setItemId(itemid);
                advertisementDetails.setItemSelect(itemselect);
                advertisementDetails.setAdvertisementAmount(Integer.parseInt(advertisingAmount.getText().toString()));
                advertisementDetails.setAdvertisingCategoryName("");


                if (adv_TypeString.equals("Stores")) {
                    if (advertisementSelectedString.equals("One Hour")) {
                        final String dateString = hourText.getText().toString();
                        final long millisToAdd = 3_600_000;
                        SimpleDateFormat format = new SimpleDateFormat("kk:mm a");
                        Date d = null;
                        try {
                            d = format.parse(dateString);
                            d.setTime(d.getTime() + millisToAdd);
                            System.out.println("New value: " + d);
                            DateFormat date = new SimpleDateFormat("kk:mm a");
                            advertisementDetails.setAdvertisementExpiringDuration(date.format(d));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        advertisementDetails.setAdvertisementstatus("Active");
                        advertisementDetails.setAdvertisementEndingDurationDate(advertisementScheduleDate);
                        advertisementDetails.setAdvertisementStartingDuration(advertismentStartDurtionstring);
                    } else if (advertisementSelectedString.equals("One Day")) {
                        String dt = advertisementScheduleDate;  // Start date
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        Calendar c = Calendar.getInstance();
                        try {
                            c.setTime(sdf.parse(dt));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        c.add(Calendar.DATE, 1);
                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
                        advertisementDetails.setAdvertisementEndingDurationDate(sdf1.format(c.getTime()));
                        advertisementDetails.setAdvertisementExpiringDuration(advertisementScheduledTime);
                    } else if (advertisementSelectedString.equals("One Week")) {
                        String dt = advertisementScheduleDate;  // Start date
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        Calendar c = Calendar.getInstance();
                        try {
                            c.setTime(sdf.parse(dt));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        c.add(Calendar.DATE, 6);
                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
                        advertisementDetails.setAdvertisementEndingDurationDate(sdf1.format(c.getTime()));
                        advertisementDetails.setAdvertisementExpiringDuration(advertisementScheduledTime);
                    }
                }
                advertisementDataRef.child(String.valueOf(addMaxId + 1)).setValue(advertisementDetails);
                Toast.makeText(AddAdvertisementActivity.this, DETAILS_INSERTED, Toast.LENGTH_SHORT).show();
                pDialog.dismiss();

                Intent intent = new Intent(getApplicationContext(), AddAdvertisementActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                advertisementImage.setImageDrawable(null);
                mimageuri = null;

            });
        }

    }


    public void itemNameSelectFunction(String storename){

        itemDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()>0){
                    itemNamelist.clear();
                    itemNamelist.add("Select Item");
                    for (DataSnapshot itemnamesanp :snapshot.getChildren()){
                        ItemDetails itemDetails = itemnamesanp.getValue(ItemDetails.class);
                        if (itemDetails.getStoreName().equals(storename)) {
                            itemNamelist.add(itemDetails.getItemName());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }





    @Override
    protected void onStart() {
        super.onStart();

        advertisementDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                addMaxId = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AddAdvertisementActivity.this);
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
                    Intent intent = new Intent(AddAdvertisementActivity.this,
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
        if (!((Activity) AddAdvertisementActivity.this).isFinishing()) {
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

