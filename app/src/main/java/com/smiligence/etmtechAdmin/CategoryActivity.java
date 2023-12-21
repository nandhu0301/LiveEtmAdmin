package com.smiligence.etmtechAdmin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.smiligence.etmtechAdmin.Adapter.CategoryAdapter;
import com.smiligence.etmtechAdmin.bean.CategoryDetails;
import com.smiligence.etmtechAdmin.bean.UserDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.Constant;
import com.smiligence.etmtechAdmin.common.DateUtils;
import com.smiligence.etmtechAdmin.common.NetworkStateReceiver;
import com.smiligence.etmtechAdmin.common.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.smiligence.etmtechAdmin.common.Constant.BOOLEAN_FALSE;
import static com.smiligence.etmtechAdmin.common.Constant.BOOLEAN_TRUE;
import static com.smiligence.etmtechAdmin.common.Constant.CATEGORY_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.CATEGORY_IMAGE_STORAGE;
import static com.smiligence.etmtechAdmin.common.Constant.CATEGORY_NAME;
import static com.smiligence.etmtechAdmin.common.Constant.DEFAULT_ROW_COUNT;
import static com.smiligence.etmtechAdmin.common.Constant.PHONE_NUM_COLUMN;
import static com.smiligence.etmtechAdmin.common.Constant.TITLE_CATEGORY;
import static com.smiligence.etmtechAdmin.common.Constant.USER_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.MessageConstant.CATAGORY_EXIST;
import static com.smiligence.etmtechAdmin.common.MessageConstant.INVALID_CATAGORY_NAME;
import static com.smiligence.etmtechAdmin.common.MessageConstant.PLEASE_SELECT_IMAGE;


public class CategoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,  NetworkStateReceiver.NetworkStateReceiverListener{

    NavigationView navigationView;
    String userName;
    public static TextView textViewUsername , textStorename;
    public static TextView textViewEmail;
    public static ImageView imageView;
    public static Menu menuNav;
    public static View mHeaderView;
    TextView reviewCount;
    FloatingActionButton add_Categories;
    DatabaseReference categoryRef, userDetailsDataRef;
    StorageReference category_storage;
    RecyclerView categoryRecyclerView;
    private ArrayList<CategoryDetails> catagoryList = new ArrayList<>();
    CategoryAdapter categoryAdapter;
    EditText categoryName_edittext ;
    ImageView categoryImage_imageview;
    ImageView updateCategoryImage;
    private static int PICK_IMAGE_REQUEST;
    private Uri mimageuri, mimageuriUpdate;
    Intent intentImage = new Intent();
    String catagoryName;
    private StorageTask mItemStorageTask;
    long maxid = 0;
    private NetworkStateReceiver networkStateReceiver;
    androidx.appcompat.app.AlertDialog alertDialog;
    ViewGroup viewGroup;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        disableAutofill();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(TITLE_CATEGORY);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(CategoryActivity.this);
        navigationView.setCheckedItem(R.id.category);

        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);


        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);

        textViewUsername.setText(DashBoardActivity.roleName);
      //  textStorename = mHeaderView.findViewById(R.id.roleName);
        textViewEmail.setText(DashBoardActivity.userName);

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

       /* reviewCount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.reviewApproval));
        reviewCount.setGravity(Gravity.CENTER_VERTICAL);
        reviewCount.setTypeface(null, Typeface.BOLD);
        reviewCount.setTextColor(getResources().getColor(R.color.redColor));*/

        userDetailsDataRef = CommonMethods.fetchFirebaseDatabaseReference(USER_DETAILS_TABLE);
        categoryRef = CommonMethods.fetchFirebaseDatabaseReference(CATEGORY_DETAILS_TABLE);
        category_storage = CommonMethods.fetchFirebaseStorageReference(CATEGORY_IMAGE_STORAGE);

        add_Categories = findViewById(R.id.add_Catagories);
        categoryRecyclerView = findViewById(R.id.catagoryGrid);


        categoryRecyclerView.setLayoutManager(new GridLayoutManager(CategoryActivity.this, DEFAULT_ROW_COUNT));
        categoryRecyclerView.setHasFixedSize(true);


        final Query roleNameQuery = userDetailsDataRef.orderByChild(PHONE_NUM_COLUMN).equalTo(String.valueOf(userName));

        roleNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    UserDetails loginUserDetailslist = snap.getValue(UserDetails.class);
                    textViewUsername.setText(loginUserDetailslist.getFirstName().toUpperCase());
                    textViewEmail.setText(loginUserDetailslist.getRoleName().toUpperCase());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                catagoryList.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    CategoryDetails categoryDetails = postSnapshot.getValue(CategoryDetails.class);
                    catagoryList.add(categoryDetails);
                }

                if (catagoryList.size() > 0) {
                    categoryAdapter = new CategoryAdapter(CategoryActivity.this, catagoryList, 0);
                    categoryAdapter.notifyDataSetChanged();
                    categoryRecyclerView.setAdapter(categoryAdapter);
                }


                if (categoryAdapter != null) {

/*
                    categoryAdapter.setOnItemclickListener(Position -> {

                        final CategoryDetails categoryDetails = catagoryList.get(Position);
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CategoryActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.update_category_dialog, null);
                        dialogBuilder.setView(dialogView);

                        final EditText updateCatagoryName = dialogView.findViewById(R.id.updatecatagoryname_dialog);


                        final Button updateCategoryPickImageButton = dialogView.findViewById(R.id.updatecatgory_pickimage_button_dialog);
                        updateCategoryImage = dialogView.findViewById(R.id.updatecatagoryImage);
                        final Button updateCategoryDetails = dialogView.findViewById(R.id.updateaddCatagory_dialog);
                        ImageView cancel = dialogView.findViewById(R.id.Cancel);
                        final ProgressBar progressBarUpdate = dialogView.findViewById(R.id.progressUpdate);
                        updateCatagoryName.setText(categoryDetails.getCategoryName());


                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.mipmap.ic_launcher);
                        requestOptions.error(R.mipmap.ic_launcher);
                        Glide.with(CategoryActivity.this)
                                .setDefaultRequestOptions(requestOptions)
                                .load(categoryDetails.getCategoryImage())
                                .fitCenter().into(updateCategoryImage);

                        final AlertDialog b = dialogBuilder.create();
                        b.show();

                        cancel.setOnClickListener(v -> b.dismiss());

                        updateCategoryPickImageButton.setOnClickListener(v -> {
                            openFileChooser();
                            startActivityForResult(intentImage, 1);
                            mimageuri = null;
                        });

                        updateCategoryDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final String nameOfCategory = updateCatagoryName.getText().toString().trim().toUpperCase();

                                if (nameOfCategory == null || "".equals(nameOfCategory)
                                        || android.text.TextUtils.isEmpty(nameOfCategory)) {
                                    updateCatagoryName.setError(Constant.REQUIRED_MSG);
                                    return;
                                } else if (!android.text.TextUtils.isEmpty(nameOfCategory)
                                        && !TextUtils.validateNames_catagoryItems(nameOfCategory)) {
                                    updateCatagoryName.setError(INVALID_CATEGORY_NAME);
                                    return;
                                }

                                Query query = categoryRef.orderByChild(CATEGORY_NAME).equalTo(nameOfCategory);

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {


                                        if (!"".equals(nameOfCategory)) {

                                            updateCategoryDetails.setVisibility(View.INVISIBLE);
                                            updateCatagoryName.setTextIsSelectable(BOOLEAN_FALSE);
                                            updateCatagoryName.setFocusable(BOOLEAN_FALSE);

                                            if (mimageuriUpdate != null) {
                                                StorageReference imageFileStorageRef = category_storage.child(CATEGORY_IMAGE_STORAGE
                                                        + System.currentTimeMillis() + "." + getExtenstion(mimageuriUpdate));
                                                Bitmap bmp = null;
                                                try {
                                                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mimageuriUpdate);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                                byte[] data = baos.toByteArray();


                                                mItemStorageTask = imageFileStorageRef.putBytes(data).addOnSuccessListener(
                                                        taskSnapshot -> {
                                                            Handler handler = new Handler();
                                                            handler.postDelayed(() -> progressBarUpdate.setProgress(0), 5000);
                                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                            while (!urlTask.isSuccessful())
                                                                ;

                                                            Uri downloadUrl_update = urlTask.getResult();

                                                            uploadImageAndInsertData(updateCatagoryName, updateCategoryDetails, b, downloadUrl_update);
                                                        }).addOnFailureListener(e -> Toast.makeText(CategoryActivity.this, e.getMessage(), Toast.LENGTH_LONG).show()).addOnProgressListener(taskSnapshot -> {
                                                    progressBarUpdate.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                    progressBarUpdate.setProgress((int) progress);
                                                });
                                            } else {
                                                final Uri downloadUrl = null;
                                                mimageuriUpdate = null;

                                                uploadImageAndInsertData(updateCatagoryName, updateCategoryDetails, b, downloadUrl);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                            public void uploadImageAndInsertData(final EditText updateCatagory, final Button updateCategoryDetails,
                                                                 final AlertDialog b, final Uri downloadUrl_update) {

                                final String newCategoryName = updateCatagory.getText().toString().toUpperCase().trim();


                                final Query updatequery = categoryRef.orderByChild(CATEGORY_NAME).equalTo(newCategoryName);
                                final String previousCategoryName = categoryDetails.getCategoryName();
                                final String previousImageUri = categoryDetails.getCategoryImage();

                                updatequery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                                        if (dataSnapshot1.getChildrenCount() > 0) {

                                            if (!previousCategoryName.equalsIgnoreCase(newCategoryName)) {

                                                updateCategoryDetails.setVisibility(View.VISIBLE);
                                                updateCatagory.setTextIsSelectable(BOOLEAN_TRUE);
                                                updateCatagory.setTextIsSelectable(BOOLEAN_TRUE);
                                                updateCatagory.setFocusable(BOOLEAN_TRUE);

                                                Toast.makeText(CategoryActivity.this, CATAGORY_EXIST, Toast.LENGTH_SHORT).show();

                                            } else if (previousCategoryName.equalsIgnoreCase(newCategoryName)) {

                                                CategoryDetails updateDetails = new CategoryDetails();
                                                updateDetails.setCategoryid(categoryDetails.getCategoryid());
                                                updateDetails.setCategoryName(newCategoryName);

                                                updateDetails.setSellerList(categoryDetails.getSellerList());
                                                updateDetails.setSubCategoryList(categoryDetails.getSubCategoryList());

                                                if (downloadUrl_update != null) {
                                                    updateDetails.setCategoryImage(downloadUrl_update.toString());
                                                } else {
                                                    updateDetails.setCategoryImage(categoryDetails.getCategoryImage());
                                                }
                                                updateDetails.setCategoryCreatedDate(categoryDetails.getCategoryCreatedDate());

                                                categoryRef.child(String.valueOf(categoryDetails.getCategoryid())).setValue(updateDetails);
                                                Toast.makeText(CategoryActivity.this, "Category Updated Successfully", Toast.LENGTH_LONG).show();

                                                // Code to delete the previous image from Storage
                                            //    deletePreviousCategoryImage(previousImageUri);

                                                mimageuriUpdate = null;
                                                b.dismiss();
                                            }
                                        } else {
                                            CategoryDetails updateDetails = new CategoryDetails();
                                            updateDetails.setCategoryid(categoryDetails.getCategoryid());
                                            updateDetails.setCategoryName(newCategoryName);

                                            updateDetails.setCategoryPriority(categoryDetails.getCategoryPriority());
                                            updateDetails.setSellerList(categoryDetails.getSellerList());
                                            updateDetails.setSubCategoryList(categoryDetails.getSubCategoryList());

                                            if (downloadUrl_update != null) {
                                                updateDetails.setCategoryImage(downloadUrl_update.toString());
                                            } else {
                                                updateDetails.setCategoryImage(categoryDetails.getCategoryImage());
                                            }
                                            updateDetails.setCategoryCreatedDate(categoryDetails.getCategoryCreatedDate());
                                            categoryRef.child(String.valueOf(categoryDetails.getCategoryid())).setValue(updateDetails);
                                            Toast.makeText(CategoryActivity.this, "Category Updated Successfully", Toast.LENGTH_LONG).show();

                                            // Code to delete the previous image from the storage
                                          //  deletePreviousCategoryImage(previousImageUri);

                                            mimageuriUpdate = null;
                                            b.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    });
*/
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        add_Categories.setOnClickListener(v -> {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CategoryActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            final View dialogview = inflater.inflate(R.layout.add_catagory_dialog, null);
            dialogBuilder.setView(dialogview);

            categoryName_edittext = dialogview.findViewById(R.id.catagoryname_dialog);
            categoryImage_imageview = dialogview.findViewById(R.id.catagoryImage);

            Button PickImageCatagory_button = dialogview.findViewById(R.id.catgory_image_button_dialog);
            final Button addCatagory_button = dialogview.findViewById(R.id.addCatagory_dialog);
            ImageView cancel = dialogview.findViewById(R.id.Cancel);
            final ProgressBar progressBar = dialogview.findViewById(R.id.progressbar);

            final AlertDialog b = dialogBuilder.create();
            b.show();

            cancel.setOnClickListener(v13 -> b.dismiss());

            PickImageCatagory_button.setOnClickListener(v12 -> {
                openFileChooser();
                startActivityForResult(intentImage, 0);
            });

            addCatagory_button.setOnClickListener(v1 -> {
                catagoryName = categoryName_edittext.getText().toString().toUpperCase();

                if (catagoryName == null || "".equals(catagoryName) || android.text.TextUtils.isEmpty(catagoryName)) {
                    categoryName_edittext.setError(Constant.REQUIRED_MSG);
                    return;
                } else if (!android.text.TextUtils.isEmpty(catagoryName) && !TextUtils.validateNames_catagoryItems(catagoryName)) {
                    categoryName_edittext.setError(INVALID_CATAGORY_NAME);
                    return;
                } else {
                    addCatagory_button.setVisibility(View.INVISIBLE);

                    if (mimageuri != null) {

                        StorageReference imageFileStorageRef = category_storage.child(CATEGORY_IMAGE_STORAGE
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
                        mItemStorageTask = imageFileStorageRef.putBytes(data).addOnSuccessListener(
                                taskSnapshot -> {
                                    Handler handler = new Handler();
                                    handler.postDelayed(() -> progressBar.setProgress(0), 5000);

                                    //  @Override
                                    //  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    final Uri downloadUrl = urlTask.getResult();

                                    Toast.makeText(CategoryActivity.this, "catagoryName::" + catagoryName, Toast.LENGTH_LONG).show();

                                    Query query = categoryRef.orderByChild(CATEGORY_NAME).equalTo(catagoryName);

                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.getChildrenCount() > 0) {
                                                addCatagory_button.setVisibility(View.VISIBLE);
                                                categoryName_edittext.setText("");
                                                Toast.makeText(CategoryActivity.this, CATAGORY_EXIST, Toast.LENGTH_LONG).show();
                                            } else {

                                                addCatagory_button.setVisibility(View.INVISIBLE);
                                                CategoryDetails categoryDetails = new CategoryDetails();

                                                categoryDetails.setCategoryid(String.valueOf(maxid + 1));
                                                categoryDetails.setCategoryName(catagoryName);
                                                categoryDetails.setCategoryImage(downloadUrl.toString());

                                                categoryDetails.setCategoryCreatedDate(DateUtils.fetchCurrentDateAndTime());

                                                categoryRef.child(String.valueOf(maxid + 1)).setValue(categoryDetails);

                                                Toast.makeText(CategoryActivity.this, "Category Added Successfully", Toast.LENGTH_LONG).show();
                                                categoryName_edittext.setFocusable(BOOLEAN_FALSE);
                                                categoryName_edittext.setTextIsSelectable(BOOLEAN_TRUE);
                                                addCatagory_button.setVisibility(View.INVISIBLE);
                                                b.dismiss();

                                                mimageuri = null;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }).addOnFailureListener(e -> Toast.makeText(CategoryActivity.this, e.getMessage(), Toast.LENGTH_LONG).show()).addOnProgressListener(taskSnapshot -> {
                            progressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        });
                    } else {
                        mimageuri = null;
                        Toast.makeText(CategoryActivity.this, PLEASE_SELECT_IMAGE, Toast.LENGTH_LONG).show();
                        addCatagory_button.setVisibility(View.VISIBLE);
                    }
                }
            });
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CategoryActivity.this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
        else if (id == R.id.ordertracking) {
            Intent intent = new Intent(getApplicationContext(), OrderTrackingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else if (id == R.id.storeHistory) {
            Intent intent = new Intent(getApplicationContext(), StoreHistory.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        else if (id == R.id.uploadcontactdetails) {
            Intent intent = new Intent(getApplicationContext(), DeliverySettings.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else if (id == R.id.category) {
            Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if (id == R.id.ApproveSellers) {
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
        }
        else if (id == R.id.adminfare) {
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
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CategoryActivity.this);
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
                    Intent intent = new Intent(CategoryActivity.this,
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
                    navigationView.setCheckedItem(R.id.category);
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

        return true;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }

    private void openFileChooser() {

        intentImage = new Intent();
        intentImage.setType("image/*");
        intentImage.setAction(Intent.ACTION_GET_CONTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                PICK_IMAGE_REQUEST = 0;
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    mimageuri = data.getData();
                    //Picasso.get().load(mimageuri).into(catagoryImage_imageview);
                    Glide.with(CategoryActivity.this).load(mimageuri).into(categoryImage_imageview);
                }
                break;

            case 1:
                PICK_IMAGE_REQUEST = 1;
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    mimageuriUpdate = data.getData();
                    Glide.with(CategoryActivity.this).load(mimageuriUpdate).into(updateCategoryImage);
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
    protected void onStart() {
        super.onStart();

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                maxid = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
        if (!((Activity) CategoryActivity.this).isFinishing()) {
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






/*{

    FloatingActionButton add_Categories;
    DatabaseReference categoryRef, userDetailsDataRef;
    StorageReference category_storage;
    private Uri mimageuri, mimageuriUpdate;
    private StorageTask mItemStorageTask;
    long maxid = 0;
    ImageView categoryImage_imageview;
    EditText categoryName_edittext;
    String previousCategoryName, newCategoryName;
    ImageView updateCategoryImage;
    private ArrayList<CategoryDetails> catagoryList = new ArrayList<>();
    CategoryAdapter categoryAdapter;
    RecyclerView categoryRecyclerView;
    Intent intentImage = new Intent();
    private static int PICK_IMAGE_REQUEST;
    String selectedCategoryId;

    String catagoryName;
    String categoryPrioritySelected;
    Boolean isCategoryPriorityValuePresent;

    NavigationView navigationView;
    String userName;

    public static TextView textViewUsername;
    public static TextView textViewEmail;
    public static ImageView imageView;
    public static Menu menuNav;
    public static View mHeaderView;
    TextView reviewCount;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        disableAutofill();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(TITLE_CATEGORY);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(CategoryActivity.this);
        navigationView.setCheckedItem(R.id.category);

        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);


        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);

        textViewUsername.setText(DashBoardActivity.roleName);
        textViewEmail.setText(DashBoardActivity.userName);

        reviewCount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.reviewApproval));
        reviewCount.setGravity(Gravity.CENTER_VERTICAL);
        reviewCount.setTypeface(null, Typeface.BOLD);
        reviewCount.setTextColor(getResources().getColor(R.color.redColor));

        categoryRef = CommonMethods.fetchFirebaseDatabaseReference(CATEGORY_DETAILS_TABLE);
        category_storage = CommonMethods.fetchFirebaseStorageReference(CATEGORY_IMAGE_STORAGE);

        add_Categories = findViewById(R.id.add_Catagories);
        categoryRecyclerView = findViewById(R.id.catagoryGrid);

        categoryRecyclerView.setLayoutManager(new GridLayoutManager(CategoryActivity.this, DEFAULT_ROW_COUNT));
        categoryRecyclerView.setHasFixedSize(true);

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                catagoryList.clear();


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CategoryDetails categoryDetails = postSnapshot.getValue(CategoryDetails.class);
                    catagoryList.add(categoryDetails);
                }

                if (catagoryList.size() > 0) {
                    categoryAdapter = new CategoryAdapter(CategoryActivity.this, catagoryList, 0);
                    categoryAdapter.notifyDataSetChanged();
                    categoryRecyclerView.setAdapter(categoryAdapter);
                }

                if (categoryAdapter != null) {

                    categoryAdapter.setOnItemclickListener(new CategoryAdapter.OnItemClicklistener() {
                        @Override
                        public void Onitemclick(int Position) {


                            final CategoryDetails categoryDetails = catagoryList.get(Position);
                            selectedCategoryId = categoryDetails.getCategoryid();


                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CategoryActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            final View dialogView = inflater.inflate(R.layout.update_category_dialog, null);
                            dialogBuilder.setView(dialogView);

                            final EditText updateCatagoryName = dialogView.findViewById(R.id.updatecatagoryname_dialog);


                            final Button updateCategoryPickImageButton = dialogView.findViewById(R.id.updatecatgory_pickimage_button_dialog);
                            updateCategoryImage = dialogView.findViewById(R.id.updatecatagoryImage);
                            final Button updateCategoryDetails = dialogView.findViewById(R.id.updateaddCatagory_dialog);
                            ImageView cancel = dialogView.findViewById(R.id.Cancel);
                            final ProgressBar progressBarUpdate = dialogView.findViewById(R.id.progressUpdate);
                            updateCatagoryName.setText(categoryDetails.getCategoryName());

                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.placeholder(R.mipmap.ic_launcher);
                            requestOptions.error(R.mipmap.ic_launcher);
                            Glide.with(CategoryActivity.this)
                                    .setDefaultRequestOptions(requestOptions)
                                    .load(categoryDetails.getCategoryImage())
                                    .fitCenter().into(updateCategoryImage);

                            final AlertDialog b = dialogBuilder.create();
                            b.show();

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    b.dismiss();
                                }
                            });


                            updateCategoryPickImageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openFileChooser();
                                    startActivityForResult(intentImage, 1);
                                    mimageuri = null;
                                }
                            });

                            updateCategoryDetails.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final String nameOfCategory = updateCatagoryName.getText().toString().trim().toUpperCase();

                                    if (nameOfCategory == null || "".equals(nameOfCategory)
                                            || android.text.TextUtils.isEmpty(nameOfCategory)) {
                                        updateCatagoryName.setError(REQUIRED_MSG);
                                        Toast.makeText(getApplicationContext(), "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                                        return;
                                    } else if (
                                            nameOfCategory.length() < 3) {
                                        updateCatagoryName.setError("Maximum 3 characters");
                                        Toast.makeText(getApplicationContext(), "Maximum 3 characters", Toast.LENGTH_SHORT).show();
                                        return;

                                    } else if (
                                            TextUtils.isNumeric(nameOfCategory) == true) {
                                        updateCatagoryName.setError("Enter valid Category Name");
                                        Toast.makeText(getApplicationContext(), "Enter valid Category Name", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else if (!android.text.TextUtils.isEmpty(nameOfCategory)
                                            && !TextUtils.validateNames_catagoryItems(nameOfCategory)) {
                                        updateCatagoryName.setError(INVALID_CATEGORY_NAME);
                                        Toast.makeText(getApplicationContext(), "" + INVALID_CATEGORY_NAME, Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    Query query = categoryRef.orderByChild(CATEGORY_NAME).equalTo(nameOfCategory);

                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {


                                            if (!"".equals(nameOfCategory)) {

                                                updateCategoryDetails.setVisibility(View.INVISIBLE);
                                                updateCatagoryName.setTextIsSelectable(BOOLEAN_FALSE);
                                                updateCatagoryName.setFocusable(BOOLEAN_FALSE);

                                                if (mimageuriUpdate != null) {
                                                    StorageReference imageFileStorageRef = category_storage.child(CATEGORY_IMAGE_STORAGE
                                                            + System.currentTimeMillis() + "." + getExtenstion(mimageuriUpdate));
                                                    Bitmap bmp = null;
                                                    try {
                                                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mimageuriUpdate);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                    bmp.compress(Bitmap.CompressFormat.PNG, 25, baos);
                                                    byte[] data = baos.toByteArray();


                                                    mItemStorageTask = imageFileStorageRef.putBytes(data).addOnSuccessListener(
                                                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                    Handler handler = new Handler();
                                                                    handler.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            progressBarUpdate.setProgress(0);
                                                                        }
                                                                    }, 5000);
                                                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                                    while (!urlTask.isSuccessful())
                                                                        ;

                                                                    Uri downloadUrl_update = urlTask.getResult();

                                                                    uploadImageAndInsertData(updateCatagoryName, updateCategoryDetails, b, downloadUrl_update);
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(CategoryActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                            progressBarUpdate.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                                                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                            progressBarUpdate.setProgress((int) progress);
                                                        }
                                                    });
                                                } else {
                                                    final Uri downloadUrl = null;
                                                    mimageuriUpdate = null;

                                                    uploadImageAndInsertData(updateCatagoryName, updateCategoryDetails, b, downloadUrl);

                                                }

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }

                                    });
                                }


                                public void uploadImageAndInsertData(final EditText updateCatagory,
                                                                     final Button updateCategoryDetails,
                                                                     final AlertDialog b, final Uri downloadUrl_update) {

                                    newCategoryName = updateCatagory.getText().toString().toUpperCase().trim();


                                    final Query updatequery = categoryRef.orderByChild(CATEGORY_NAME).equalTo(newCategoryName);
                                    previousCategoryName = categoryDetails.getCategoryName();
                                    final String previousImageUri = categoryDetails.getCategoryImage();

                                    updatequery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                                            if (dataSnapshot1.getChildrenCount() > 0) {

                                                if (!previousCategoryName.equalsIgnoreCase(newCategoryName) && TextUtils.isNumeric(newCategoryName) == false) {

                                                    updateCategoryDetails.setVisibility(View.VISIBLE);
                                                    updateCatagory.setTextIsSelectable(BOOLEAN_TRUE);
                                                    updateCatagory.setTextIsSelectable(BOOLEAN_TRUE);
                                                    updateCatagory.setFocusable(BOOLEAN_TRUE);

                                                    Toast.makeText(CategoryActivity.this, CATAGORY_EXIST, Toast.LENGTH_SHORT).show();

                                                } else if (previousCategoryName.equalsIgnoreCase(newCategoryName)) {

                                                    CategoryDetails updateDetails = new CategoryDetails();
                                                    updateDetails.setCategoryid(categoryDetails.getCategoryid());
                                                    updateDetails.setCategoryName(newCategoryName);


                                                    if (downloadUrl_update != null) {
                                                        deletePreviousCategoryImage(previousImageUri);
                                                        updateDetails.setCategoryImage(downloadUrl_update.toString());
                                                    } else {
                                                        updateDetails.setCategoryImage(categoryDetails.getCategoryImage());
                                                    }
                                                    updateDetails.setCategoryCreatedDate(categoryDetails.getCategoryCreatedDate());

                                                    categoryRef.child(String.valueOf(categoryDetails.getCategoryid())).setValue(updateDetails);
                                                    Toast.makeText(CategoryActivity.this, "Category Updated Successfully", Toast.LENGTH_LONG).show();

                                                    // Code to delete the previous image from Storage
                                                    //   deletePreviousCategoryImage(previousImageUri);

                                                    mimageuriUpdate = null;
                                                    b.dismiss();
                                                }
                                            } else {


                                                CategoryDetails updateDetails = new CategoryDetails();
                                                updateDetails.setCategoryid(categoryDetails.getCategoryid());
                                                if (TextUtils.isNumeric(newCategoryName) == false) {
                                                    updateDetails.setCategoryName(newCategoryName);

                                                } else {
                                                    updateCatagory.setText("Enter valid Category Name");
                                                }


                                                updateDetails.setCategoryPriority(categoryDetails.getCategoryPriority());


                                                if (downloadUrl_update != null) {
                                                    deletePreviousCategoryImage(previousImageUri);

                                                    updateDetails.setCategoryImage(downloadUrl_update.toString());
                                                } else {
                                                    updateDetails.setCategoryImage(categoryDetails.getCategoryImage());
                                                }
                                                updateDetails.setCategoryCreatedDate(categoryDetails.getCategoryCreatedDate());
                                                categoryRef.child(String.valueOf(categoryDetails.getCategoryid())).setValue(updateDetails);


                                                mimageuriUpdate = null;
                                                b.dismiss();

                                                DatabaseReference productRef = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_TABLE);
                                                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot productSnap : dataSnapshot.getChildren()) {
                                                            ItemDetails itemDetails = productSnap.getValue(ItemDetails.class);
                                                            System.out.println("LLFKFKF  " + itemDetails.getCategoryDetailsArrayList());


                                                            updateCategoryName(itemDetails);

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });


                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }

                                    });
                                }

                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        add_Categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CategoryActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.add_catagory_dialog, null);
                dialogBuilder.setView(dialogView);

                //get the spinner from the xml.

                categoryName_edittext = dialogView.findViewById(R.id.catagoryname_dialog);
                categoryImage_imageview = dialogView.findViewById(R.id.catagoryImage);

                Button PickImageCatagory_button = dialogView.findViewById(R.id.catgory_image_button_dialog);
                final Button addCatagory_button = dialogView.findViewById(R.id.addCatagory_dialog);
                ImageView cancel = dialogView.findViewById(R.id.Cancel);
                final ProgressBar progressBar = dialogView.findViewById(R.id.progressbar);

                final AlertDialog b = dialogBuilder.create();
                b.show();


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        b.dismiss();
                    }
                });

                PickImageCatagory_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openFileChooser();
                        startActivityForResult(intentImage, 0);
                    }
                });

                addCatagory_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        catagoryName = categoryName_edittext.getText().toString().trim().toUpperCase();

                        if (catagoryName == null || "".equals(catagoryName) || android.text.TextUtils.isEmpty(catagoryName)) {
                            categoryName_edittext.setError(REQUIRED_MSG);
                            Toast.makeText(getApplicationContext(), "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();

                            return;
                        } else if (catagoryName.length() < 3) {
                            categoryName_edittext.setError("Maximum Length should be 3");
                            Toast.makeText(getApplicationContext(), "Maximum Length should be 3", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (TextUtils.isNumeric(catagoryName) == true) {
                            categoryName_edittext.setError("Enter Valid Category Name");
                            Toast.makeText(getApplicationContext(), "Enter Valid Category Name", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (!android.text.TextUtils.isEmpty(catagoryName) && !TextUtils.validateNames_catagoryItems(catagoryName)) {
                            categoryName_edittext.setError(INVALID_CATAGORY_NAME);
                            Toast.makeText(getApplicationContext(), "" + INVALID_CATAGORY_NAME, Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            addCatagory_button.setVisibility(View.INVISIBLE);

                            if (mimageuri != null) {

                                StorageReference imageFileStorageRef = category_storage.child(CATEGORY_IMAGE_STORAGE
                                        + System.currentTimeMillis() + "." + getExtenstion(mimageuri));
                                Bitmap bmp = null;
                                try {
                                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mimageuri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.PNG, 25, baos);

                                byte[] data = baos.toByteArray();
                                mItemStorageTask = imageFileStorageRef.putBytes(data).addOnSuccessListener(
                                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressBar.setProgress(0);
                                                    }
                                                }, 5000);


                                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                while (!urlTask.isSuccessful()) ;
                                                final Uri downloadUrl = urlTask.getResult();


                                                Query query = categoryRef.orderByChild(CATEGORY_NAME).equalTo(catagoryName);

                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        if (dataSnapshot.getChildrenCount() > 0) {
                                                            addCatagory_button.setVisibility(View.VISIBLE);
                                                            categoryName_edittext.setText("");
                                                            Toast.makeText(CategoryActivity.this, CATAGORY_EXIST, Toast.LENGTH_LONG).show();
                                                        } else {

                                                            addCatagory_button.setVisibility(View.INVISIBLE);
                                                            CategoryDetails categoryDetails = new CategoryDetails();

                                                            categoryDetails.setCategoryid(String.valueOf(maxid + 1));
                                                            categoryDetails.setCategoryName(catagoryName);
                                                            categoryDetails.setCategoryImage(downloadUrl.toString());
                                                            categoryDetails.setCategoryPriority(categoryPrioritySelected);


                                                            categoryRef.child(String.valueOf(maxid + 1)).setValue(categoryDetails);

                                                            Toast.makeText(CategoryActivity.this, "Category Added Successfully", Toast.LENGTH_LONG).show();
                                                            categoryName_edittext.setFocusable(BOOLEAN_FALSE);
                                                            categoryName_edittext.setTextIsSelectable(BOOLEAN_TRUE);

                                                            addCatagory_button.setVisibility(View.INVISIBLE);
                                                            b.dismiss();

                                                            mimageuri = null;
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
                                        Toast.makeText(CategoryActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        progressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                        progressBar.setProgress((int) progress);
                                    }
                                });
                            } else {
                                mimageuri = null;
                                Toast.makeText(CategoryActivity.this, PLEASE_SELECT_IMAGE, Toast.LENGTH_LONG).show();
                                addCatagory_button.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                });

            }

        });
    }

    private void updateCategoryName(ItemDetails itemDetails) {
        Iterator itemIterator = itemDetails.getCategoryDetailsArrayList().iterator();
        ArrayList<CategoryDetails> categoryDetailsList = new ArrayList<>();
        while (itemIterator.hasNext()) {

            CategoryDetails categoryDetails = (CategoryDetails) itemIterator.next();

            if (categoryDetails.getCategoryName().equalsIgnoreCase(previousCategoryName)) {
                CategoryDetails item = new CategoryDetails();
                item.setCategoryName(newCategoryName);
                item.setItemId(String.valueOf(itemDetails.getItemId()));
                item.setCategoryid(selectedCategoryId);
                categoryDetailsList.add(item);

            } else {
                CategoryDetails item = new CategoryDetails();
                item.setCategoryName(categoryDetails.getCategoryName());
                item.setItemId(String.valueOf(itemDetails.getItemId()));
                item.setCategoryid(categoryDetails.getCategoryid());
                categoryDetailsList.add(item);

            }


            itemDetails.setCategoryId(itemDetails.getCategoryId());
            itemDetails.setCategoryName(itemDetails.getCategoryName());
            itemDetails.setItemName(itemDetails.getItemName());
            itemDetails.setItemPrice(itemDetails.getItemPrice());

            itemDetails.setCategoryDetailsArrayList(categoryDetailsList);
            itemDetails.setItemId(itemDetails.getItemId());
            itemDetails.setMRP_Price(itemDetails.getMRP_Price());

            itemDetails.setItemDescription(itemDetails.getItemDescription());
            itemDetails.setBasePrice(itemDetails.getBasePrice());
            itemDetails.setCreateDate(itemDetails.getCreateDate());
            itemDetails.setHSNCode(itemDetails.getHSNCode());
            itemDetails.setItemAvailableQuantity(itemDetails.getItemAvailableQuantity());
            itemDetails.setItemBrand(itemDetails.getItemBrand());
            itemDetails.setItemBuyQuantity(itemDetails.getItemBuyQuantity());
            itemDetails.setItemDescription(itemDetails.getItemDescription());
            itemDetails.setItemFeatures(itemDetails.getItemFeatures());
            itemDetails.setItemImage(itemDetails.getItemImage());
            itemDetails.setItemMaxLimitation(itemDetails.getItemMaxLimitation());
            itemDetails.setItemQuantity(itemDetails.getItemQuantity());
            itemDetails.setItemStatus(itemDetails.getItemStatus());
            itemDetails.setStoreLogo(itemDetails.getStoreLogo());
            itemDetails.setSubCategoryName(itemDetails.getSubCategoryName());
            itemDetails.setTax(itemDetails.getTax());
            itemDetails.setTaxPrice(itemDetails.getTaxPrice());
            itemDetails.setTotalItemQtyPrice(itemDetails.getTotalItemQtyPrice());
            itemDetails.setWishList(itemDetails.getWishList());


            DatabaseReference databaseReference = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_TABLE);
            databaseReference.child(String.valueOf(itemDetails.getItemId())).setValue(itemDetails);

        }
    }

    public void deletePreviousCategoryImage(String previousImageUri) {
        category_storage.getFile(Uri.parse(previousImageUri));
        category_storage.child(CATEGORY_IMAGE_STORAGE).getStorage()
                .getReferenceFromUrl(previousImageUri).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(CategoryActivity.this, "deleted" +
                        "", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void openFileChooser() {

        intentImage = new Intent();
        intentImage.setType("image/*");
        intentImage.setAction(Intent.ACTION_GET_CONTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                PICK_IMAGE_REQUEST = 0;
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    mimageuri = data.getData();

                    Glide.with(CategoryActivity.this).load(mimageuri).into(categoryImage_imageview);
                }
                break;

            case 1:
                PICK_IMAGE_REQUEST = 1;
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    //requestCode == PICK_IMAGE_REQUEST;
                    mimageuriUpdate = data.getData();
                    // Picasso.get().load(mimageuriUpdate).into(updateCategoryImage);
                    Glide.with(CategoryActivity.this).load(mimageuriUpdate).into(updateCategoryImage);
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
    protected void onStart() {
        super.onStart();

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                maxid = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CategoryActivity.this);
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
                    Intent intent = new Intent(CategoryActivity.this,
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
                    navigationView.setCheckedItem(R.id.category);
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

        return true;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(CategoryActivity.this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }


}*/

