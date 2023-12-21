package com.smiligence.etmtechAdmin;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
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
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
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
import com.smiligence.etmtechAdmin.bean.ContactDetails;
import com.smiligence.etmtechAdmin.bean.ItemReviewAndRatings;
import com.smiligence.etmtechAdmin.bean.StoreDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.DateUtils;
import com.smiligence.etmtechAdmin.common.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligence.etmtechAdmin.ViewOrderActivity.textViewEmail;
import static com.smiligence.etmtechAdmin.common.Constant.INVALID_FIRSTNAME_SPECIFICATION;
import static com.smiligence.etmtechAdmin.common.Constant.INVALID_LASTNAME_SPECIFICATION;
import static com.smiligence.etmtechAdmin.common.Constant.ITEM_RATING_REVIEW_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.SELLER_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.SELLER_IMAGE_STORAGE;
import static com.smiligence.etmtechAdmin.common.Constant.TITLE_SELLER;
import static com.smiligence.etmtechAdmin.common.MessageConstant.REQUIRED_MSG;


public class MyProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    EditText storename, storeAddress, zipCode, GSTNumber,
            storeContactNumber, customerSupportNumber, GSTPercentage;
    Button storeLogo, coverImage_button, uploadStoreDetails;
    ImageView logoImage;


    String storeNameString, storeAddressString, GSTNumberString, zipCodeString, GSTString,
            businessTypeString, storeContactnumberString, customerSupportString;

    private BigDecimal value;
    private NumberFormat nbFmt = NumberFormat.getInstance();
    private static int PICK_IMAGE_REQUEST;
    Uri mimageuri, mimageuriHeader;
    StorageTask mItemStorageTask, mStorgeTaskLogo;
    String saved_username, saved_password;
    String imageLogo;
    // String image_headerString;

    DatabaseReference storeNameRef, dropdownRef;
    StorageReference storeNameStorage;
    Intent intentImage = new Intent();

    TextView textViewUsername;
    ArrayList<String> arrayList = new ArrayList<String>();

    ArrayList<StoreDetails> storeDetailArrayList = new ArrayList<StoreDetails>();

    ArrayList<String> BusinessTypeList = new ArrayList<String>();
    EditText businessTypeEditText;
    StoreDetails storeDetails;
    ArrayAdapter<String> arrayAdapter;
    NavigationView navigationView;
    static Menu menuNav;
    static View mHeaderView;
    TextView reviewCount;
    EditText firsNameEdt, lastNameEdt, aadharNumberEdt, youTube;

    // EditText bankNameEdt,accountNumberEdt,ifscCodeEdt;
    DatabaseReference databaseReference;
    Button uploadContactDetails;
    EditText whatsApp, faceBook, instagram, emailText;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_details);

        SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        SharedPreferences.Editor loginEditor = loginSharedPreferences.edit();
        saved_username = loginSharedPreferences.getString("userNameStr", "");
        saved_password = loginSharedPreferences.getString("passwordStr", "");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        storeNameRef = CommonMethods.fetchFirebaseDatabaseReference(SELLER_DETAILS_TABLE);
        storeNameStorage = CommonMethods.fetchFirebaseStorageReference(SELLER_IMAGE_STORAGE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(TITLE_SELLER);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MyProfileActivity.this);
        drawer.setDrawerListener(toggle);
        navigationView.setCheckedItem(R.id.add_seller);

        LinearLayout linearLayout = (LinearLayout) navigationView.getHeaderView(0);
        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);
        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);
        textViewUsername.setText(DashBoardActivity.roleName);
        textViewEmail.setText(DashBoardActivity.userName);
        navigationView.setCheckedItem(R.id.add_seller);

        // disableAutofill();
        storename = findViewById(R.id.storename);
        storeAddress = findViewById(R.id.storeLocation);
        zipCode = findViewById(R.id.zipcode);
        GSTNumber = findViewById(R.id.Gstnumber);
        GSTPercentage = findViewById(R.id.GSTPercentage);

        firsNameEdt = findViewById(R.id.fistname);
        lastNameEdt = findViewById(R.id.lastname);
        /*bankNameEdt=findViewById(R.id.bankname);
        accountNumberEdt=findViewById(R.id.accountnumber);
        ifscCodeEdt=findViewById(R.id.ifscCode);*/
        aadharNumberEdt = findViewById(R.id.aadhar);
        storeContactNumber = findViewById(R.id.storecontactnumber);
        customerSupportNumber = findViewById(R.id.storecustomersupportnumber);
        storeLogo = findViewById(R.id.storeLogo);

        youTube = findViewById(R.id.youtube);

        uploadStoreDetails = findViewById(R.id.uploadStoredetails);
        logoImage = findViewById(R.id.logo);

        uploadContactDetails = findViewById(R.id.upload_contactDetails);

        businessTypeEditText = findViewById(R.id.businessTypeEditText);

        reviewCount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.reviewApproval));
        reviewCount.setGravity(Gravity.CENTER_VERTICAL);
        reviewCount.setTypeface(null, Typeface.BOLD);
        reviewCount.setTextColor(getResources().getColor(R.color.redColor));


        storeLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();

                startActivityForResult(intentImage, 0);
            }
        });


        storeNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    storeDetails = dataSnapshot.getValue(StoreDetails.class);

                    uploadStoreDetails.setVisibility(View.VISIBLE);

                    storename.setText(storeDetails.getStoreName());
                    storeAddress.setText(storeDetails.getStoreAddress());
                    zipCode.setText(storeDetails.getZipCode());
                    GSTNumber.setText(storeDetails.getGstNumber());

                    GSTPercentage.setText("" + storeDetails.getGst());
                    firsNameEdt.setText(storeDetails.getFirstName());
                    lastNameEdt.setText(storeDetails.getLastName());
                  /*  bankNameEdt.setText(storeDetails.getBankName());
                     ifscCodeEdt.setText(storeDetails.getIFSCCode());
                    accountNumberEdt.setText(storeDetails.getAccountNumber());*/
                    aadharNumberEdt.setText(storeDetails.getAadharNumber());


                    customerSupportNumber.setText(storeDetails.getCustomerSupportContactNumber());
                    storeContactNumber.setText(storeDetails.getStoreContactNumber());

                    int counter = 0;
                    businessTypeEditText.setText(storeDetails.getBusinessType());
                    businessTypeString = businessTypeEditText.getText().toString();

                    for (int j = 0; j < BusinessTypeList.size(); j++) {

                        if (BusinessTypeList.get(j).equals(businessTypeString)) {
                            counter = j;
                            break;
                        }
                    }


                    if (logoImage != null && (storeDetails.getStoreLogo() != null
                            || !"".equalsIgnoreCase(storeDetails.getStoreLogo()))) {
                        imageLogo = storeDetails.getStoreLogo();
                        Glide.with(MyProfileActivity.this).load(storeDetails.getStoreLogo()).fitCenter().into(logoImage);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        uploadStoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                storeNameString = storename.getText().toString().toUpperCase().trim();
                storeAddressString = storeAddress.getText().toString().trim();

                GSTNumberString = GSTNumber.getText().toString();
                zipCodeString = zipCode.getText().toString();


                storeContactnumberString = storeContactNumber.getText().toString();
                customerSupportString = customerSupportNumber.getText().toString();

                businessTypeString = businessTypeEditText.getText().toString();

                if ("".equals(firsNameEdt.getText().toString())) {
                    firsNameEdt.setError(REQUIRED_MSG);

                    return;
                } else if (!TextUtils.isValidFirstName(firsNameEdt.getText().toString().trim())) {
                    firsNameEdt.setError(INVALID_FIRSTNAME_SPECIFICATION);

                    return;
                } else if ("".equals(lastNameEdt.getText().toString().trim())) {
                    lastNameEdt.setError(REQUIRED_MSG);

                    return;
                } else if (!TextUtils.isValidlastName(lastNameEdt.getText().toString())) {
                    lastNameEdt.setError(INVALID_LASTNAME_SPECIFICATION);

                    return;
                } else if ("".equalsIgnoreCase(businessTypeString) || businessTypeString == null || " ".equalsIgnoreCase(businessTypeString)) {
                    Toast.makeText(MyProfileActivity.this, "Enter Business Type", Toast.LENGTH_LONG).show();
                    return;
                }/*else if (!TextUtils.isValidAlphaCharacters(bankNameEdt.getText().toString().trim())) {
                    bankNameEdt.setError(INVALID_NAME_SPECIFICATION);
                    return;
                } else if ("".equals(accountNumberEdt.getText().toString().trim())) {
                    accountNumberEdt.setError(REQUIRED_MSG);
                    return;
                } else if (!TextUtils.isValidnumeric(accountNumberEdt.getText().toString().trim())) {
                    accountNumberEdt.setError(NUMERIC);
                    return;
                } else  if(!((accountNumberEdt.length()) >=12)){
                    accountNumberEdt.setError("Invalid Account number");
                    return;
                }else if ("".equals(ifscCodeEdt.getText().toString().trim())) {
                    ifscCodeEdt.setError(REQUIRED_MSG);
                    return;
                } else  if(!((ifscCodeEdt.length()) ==11)){
                    ifscCodeEdt.setError("Invalid IFSC number");
                    return;
                }*/ else if ("".equals(aadharNumberEdt.getText().toString().trim())) {
                    aadharNumberEdt.setError(REQUIRED_MSG);

                    return;
                } else if (!"".equals(aadharNumberEdt.getText().toString()) && !TextUtils.isValidAadharNumber(aadharNumberEdt.getText().toString())) {
                    aadharNumberEdt.setError("Aadhar number accepts follwing pattern Eg(3675 9834 6015)");
                    return;
                } else if (storeNameString == null || "".equals(storeNameString) || android.text.TextUtils.isEmpty(storeNameString)) {
                    storename.setError(REQUIRED_MSG);

                    return;
                } else if (TextUtils.isNumeric(storeNameString) == true) {
                    storename.setError("Enter valid Store name");
                    return;

                } else if (!android.text.TextUtils.isEmpty(storeNameString) && !TextUtils.validateNames_catagoryItems(storeNameString)) {
                    storename.setError("Enter Valid Store Name");
                    return;
                } else if (storeAddressString == null || "".equals(storeAddressString) || android.text.TextUtils.isEmpty(storeAddressString)) {
                    storeAddress.setError(REQUIRED_MSG);
                    return;
                } else if (zipCodeString == null || "".equals(zipCodeString) && android.text.TextUtils.isEmpty(zipCodeString)) {
                    zipCode.setError(REQUIRED_MSG);

                    return;
                } else if (!android.text.TextUtils.isEmpty(zipCodeString) && !TextUtils.validateZipcode(zipCodeString)) {
                    zipCode.setError("Enter Valid Zip code");
                    return;
                } else if (storeContactnumberString == null || "".equalsIgnoreCase(storeContactnumberString)) {
                    storeContactNumber.setError(REQUIRED_MSG);
                    return;
                } else if (!android.text.TextUtils.isEmpty(storeContactnumberString) && !TextUtils.validatePhoneNumber(storeContactnumberString)) {
                    storeContactNumber.setError("Enter valid Phone Number ");
                    return;
                } else if (customerSupportString == null || "".equalsIgnoreCase(customerSupportString)) {
                    customerSupportNumber.setError(REQUIRED_MSG);
                    return;
                } else if (!android.text.TextUtils.isEmpty(customerSupportString) && !TextUtils.validatePhoneNumber(customerSupportString)) {
                    customerSupportNumber.setError("Enter valid Phone Number ");
                    return;
                } else if (!android.text.TextUtils.isEmpty(GSTNumberString) && !TextUtils.validate_GSTNumber(GSTNumberString)) {
                    GSTNumber.setError("Enter Valid GST Number");
                    return;
                } else if (mimageuri != null) {
                    final SweetAlertDialog pDialog = new SweetAlertDialog(MyProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#1898ED"));
                    pDialog.setTitleText("Adding Store Details....");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    StorageReference imageFileLogo = storeNameStorage.child(SELLER_IMAGE_STORAGE
                            + System.currentTimeMillis() + "." + getExtenstion(mimageuri));


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
                                    while (!urlTaskLogo.isSuccessful()) ;
                                    Uri downloadUrl = urlTaskLogo.getResult();
                                    imageLogo = downloadUrl.toString();


                                    storeDetails = new StoreDetails();
                                    storeDetails.setStoreName(storeNameString);
                                    storeDetails.setStoreAddress(storeAddressString);
                                    storeDetails.setZipCode(zipCodeString);
                                    storeDetails.setBusinessType(businessTypeString);
                                    storeDetails.setGstNumber(GSTNumberString);
                                    storeDetails.setGst(Integer.parseInt(GSTPercentage.getText().toString()));


                                    storeDetails.setStoreLogo(imageLogo);
                                    storeDetails.setCreateDate(DateUtils.fetchCurrentDateAndTime());
                                    storeDetails.setStoreContactNumber(storeContactnumberString);
                                    storeDetails.setCustomerSupportContactNumber(customerSupportString);

                                    storeDetails.setFirstName(firsNameEdt.getText().toString().trim());
                                    storeDetails.setLastName(lastNameEdt.getText().toString().trim());
                                    //  storeDetails.setBankName(bankNameEdt.getText().toString().trim());
                                    // storeDetails.setAccountNumber(accountNumberEdt.getText().toString().trim());
                                    storeDetails.setAadharNumber(aadharNumberEdt.getText().toString().trim());
                                    //   storeDetails.setIFSCCode(ifscCodeEdt.getText().toString().trim());


                                    storeNameRef.setValue(storeDetails);
                                    Toast.makeText(MyProfileActivity.this, "Store Details Uploaded Successfully", Toast.LENGTH_LONG).show();
                                    pDialog.dismiss();
                                    uploadStoreDetails.setVisibility(View.INVISIBLE);

                                    logoImage.setImageResource(R.drawable.b_chooseimage);

                                }
                            });

                } else {
                    Toast.makeText(MyProfileActivity.this, "Select Image Logo", Toast.LENGTH_LONG).show();
                    validateAndUpdateStoreDetails();
                }

                if (mimageuri == null) {
                    validateAndUpdateStoreDetails();
                }
            }
        });
        uploadStoreDetails.setVisibility(View.VISIBLE);


        emailText = findViewById(R.id.email);
        whatsApp = findViewById(R.id.whatsapp);
        faceBook = findViewById(R.id.fb);
        instagram = findViewById(R.id.instagram);

        databaseReference = CommonMethods.fetchFirebaseDatabaseReference("contactDetails");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    ContactDetails contactDetails = dataSnapshot.getValue(ContactDetails.class);
                    whatsApp.setText(contactDetails.getWhatsAppContact());
                    instagram.setText(contactDetails.getInstagramUrl());
                    faceBook.setText(contactDetails.getFacebookUrl());
                    emailText.setText(contactDetails.getEmail());
                    youTube.setText(contactDetails.getYouTubeUrl());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        uploadContactDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String whatsAppNumberString = whatsApp.getText().toString().trim();
                String instaLink = instagram.getText().toString().trim();
                String fbLink = faceBook.getText().toString().trim();
                String emailLink = emailText.getText().toString().trim();
                String youTubeString = youTube.getText().toString().trim();
                if ("".equalsIgnoreCase(whatsAppNumberString)) {
                    whatsApp.setError(REQUIRED_MSG);
                    return;
                } else if (!"".equalsIgnoreCase(whatsAppNumberString) &&
                        !TextUtils.validatePhoneNumber(whatsAppNumberString)) {
                    whatsApp.setError("Enter Valid Phone Number");
                    return;
                }
                if ("".equalsIgnoreCase(emailLink)) {
                    emailText.setError(REQUIRED_MSG);
                    Toast.makeText(MyProfileActivity.this, "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                    return;
                } else if (!"".equalsIgnoreCase(emailLink) &&
                        !TextUtils.isValidEmail(emailLink)) {
                    emailText.setError("Enter Valid Email-Id");
                    Toast.makeText(MyProfileActivity.this, "Enter Valid Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                } else if ("".equalsIgnoreCase(instaLink)) {
                    instagram.setError(REQUIRED_MSG);
                    Toast.makeText(MyProfileActivity.this, "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                    return;
                } else if ("".equalsIgnoreCase(fbLink)) {
                    faceBook.setError(REQUIRED_MSG);
                    Toast.makeText(MyProfileActivity.this, "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                    return;
                } else if ("".equalsIgnoreCase(youTubeString)) {
                    youTube.setError(REQUIRED_MSG);
                    Toast.makeText(MyProfileActivity.this, "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    ContactDetails contactDetails = new ContactDetails();
                    contactDetails.setWhatsAppContact(whatsAppNumberString);
                    contactDetails.setInstagramUrl(instaLink);
                    contactDetails.setFacebookUrl(fbLink);
                    contactDetails.setEmail(emailLink);
                    contactDetails.setYouTubeUrl(youTubeString);
                    databaseReference.setValue(contactDetails);

                    Toast.makeText(MyProfileActivity.this, "Contact Details uploaded Successfully",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void validateAndUpdateStoreDetails() {

        if ("".equals(firsNameEdt.getText().toString())) {
            firsNameEdt.setError(REQUIRED_MSG);
            Toast.makeText(this, "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
            return;
        } else if (!TextUtils.isValidFirstName(firsNameEdt.getText().toString())) {
            firsNameEdt.setError(INVALID_FIRSTNAME_SPECIFICATION);
            Toast.makeText(this, "" + INVALID_FIRSTNAME_SPECIFICATION, Toast.LENGTH_SHORT).show();
            return;
        } else if ("".equals(lastNameEdt.getText().toString())) {
            lastNameEdt.setError(REQUIRED_MSG);
            Toast.makeText(this, "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
            return;
        } else if (!TextUtils.isValidlastName(lastNameEdt.getText().toString())) {
            lastNameEdt.setError(INVALID_LASTNAME_SPECIFICATION);
            Toast.makeText(this, "" + INVALID_LASTNAME_SPECIFICATION, Toast.LENGTH_SHORT).show();
            return;
        } else if ("".equalsIgnoreCase(businessTypeString) || businessTypeString == null || " ".equalsIgnoreCase(businessTypeString)) {
            Toast.makeText(MyProfileActivity.this, "Enter Business Type", Toast.LENGTH_LONG).show();
            return;
        } else if ("".equals(aadharNumberEdt.getText().toString())) {
            aadharNumberEdt.setError(REQUIRED_MSG);
            Toast.makeText(this, "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
            return;
        } else if (!"".equals(aadharNumberEdt.getText().toString()) && !TextUtils.isValidAadharNumber(aadharNumberEdt.getText().toString())) {
            aadharNumberEdt.setError("Aadhar number accepts following pattern Eg(3675 9834 6015)");
            Toast.makeText(this, "Aadhar number accepts following pattern Eg(3675 9834 6015)", Toast.LENGTH_SHORT).show();
            return;
        } else if (storeNameString == null || "".equals(storeNameString) || android.text.TextUtils.isEmpty(storeNameString)) {
            storename.setError(REQUIRED_MSG);
            Toast.makeText(this, "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
            return;
        } else if (!android.text.TextUtils.isEmpty(storeNameString) && !TextUtils.validateNames_catagoryItems(storeNameString)) {
            storename.setError("Enter Valid Store Name");
            Toast.makeText(this, "Enter Valid Store Name", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isNumeric(storeNameString) == true) {
            storename.setError("Enter valid Store name");
            Toast.makeText(this, "Enter valid Store name", Toast.LENGTH_SHORT).show();
            return;

        } else if (storeAddressString == null || "".equals(storeAddressString) || android.text.TextUtils.isEmpty(storeAddressString)) {
            storeAddress.setError(REQUIRED_MSG);
            Toast.makeText(this, "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
            return;
        } else if (zipCodeString == null || "".equals(zipCodeString) && android.text.TextUtils.isEmpty(zipCodeString)) {
            zipCode.setError(REQUIRED_MSG);
            Toast.makeText(this, "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
            return;
        } else if (!android.text.TextUtils.isEmpty(zipCodeString) && !TextUtils.validateZipcode(zipCodeString)) {
            zipCode.setError("Enter Valid Zip code");
            Toast.makeText(this, "Enter Valid Zip code", Toast.LENGTH_SHORT).show();
            return;
        } else if (storeContactnumberString == null || "".equalsIgnoreCase(storeContactnumberString)) {
            storeContactNumber.setError(REQUIRED_MSG);
            Toast.makeText(this, "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
            return;
        } else if (!android.text.TextUtils.isEmpty(storeContactnumberString) && !TextUtils.validatePhoneNumber(storeContactnumberString)) {
            storeContactNumber.setError("Enter valid Phone Number ");
            Toast.makeText(this, "Enter valid Phone Number ", Toast.LENGTH_SHORT).show();
            return;
        } else if (customerSupportString == null || "".equalsIgnoreCase(customerSupportString)) {
            customerSupportNumber.setError(REQUIRED_MSG);
            Toast.makeText(this, "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
            return;
        } else if (!android.text.TextUtils.isEmpty(customerSupportString) && !TextUtils.validatePhoneNumber(customerSupportString)) {
            customerSupportNumber.setError("Enter valid Phone Number ");
            Toast.makeText(this, "Enter valid Phone Number", Toast.LENGTH_SHORT).show();
            return;
        } else if (!android.text.TextUtils.isEmpty(GSTNumberString) && !TextUtils.validate_GSTNumber(GSTNumberString)) {
            GSTNumber.setError("Enter Valid GST Number");
            Toast.makeText(this, "Enter Valid GST Number", Toast.LENGTH_SHORT).show();
            return;
        } else {
            StoreDetails storeDetails = new StoreDetails();
            storeDetails.setStoreName(storeNameString);
            storeDetails.setStoreAddress(storeAddressString);
            storeDetails.setZipCode(zipCodeString);
            storeDetails.setBusinessType(businessTypeString);
            storeDetails.setGstNumber(GSTNumberString);
            storeDetails.setGst(Integer.parseInt(GSTPercentage.getText().toString()));

            Toast.makeText(this, "here", Toast.LENGTH_SHORT).show();

            storeDetails.setStoreLogo(imageLogo);

            storeDetails.setCreateDate(DateUtils.fetchCurrentDateAndTime());
            storeDetails.setStoreContactNumber(storeContactnumberString);
            storeDetails.setCustomerSupportContactNumber(customerSupportString);

            storeDetails.setFirstName(firsNameEdt.getText().toString().trim());
            storeDetails.setLastName(lastNameEdt.getText().toString().trim());
            // storeDetails.setBankName(bankNameEdt.getText().toString().trim());
            //   storeDetails.setAccountNumber(accountNumberEdt.getText().toString().trim());
            storeDetails.setAadharNumber(aadharNumberEdt.getText().toString().trim());
            //   storeDetails.setIFSCCode(ifscCodeEdt.getText().toString().trim());

            storeNameRef.setValue(storeDetails);
            Toast.makeText(MyProfileActivity.this, "Store details Updated Successfully", Toast.LENGTH_LONG).show();
        }
    }

    private void openFileChooser() {

        intentImage = new Intent();
        intentImage.setType("image/*");
        intentImage.setAction(Intent.ACTION_GET_CONTENT);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:

                PICK_IMAGE_REQUEST = 0;
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    mimageuri = data.getData();

                    Glide.with(MyProfileActivity.this).load(mimageuri).into(logoImage);
                }
                break;

        }
    }

    private String getExtenstion(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.vieworders) {
            Intent intent = new Intent(getApplicationContext(), ViewOrderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if (id == R.id.ApproveSellers) {
            Intent intent = new Intent(getApplicationContext(), ApproveSellersActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        else if (id == R.id.category) {
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
        }/*else if(id==R.id.deliveryfare){
            Intent intent = new Intent(getApplicationContext(), AddDeliveryFareActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }*/ else if (id == R.id.logoutscreen) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MyProfileActivity.this);
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
                    Intent intent = new Intent(MyProfileActivity.this,
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
                    navigationView.setCheckedItem(R.id.add_seller);
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
        } else if (id == R.id.legal) {
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
        } else if (id == R.id.billingScreen) {
            Intent intent = new Intent(getApplicationContext(), BillingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }

   /* @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        ArrayList<ItemReviewAndRatings> list = new ArrayList<>();
        DatabaseReference
                itemReviewDataRef = CommonMethods.fetchFirebaseDatabaseReference(ITEM_RATING_REVIEW_TABLE);
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
}