package com.smiligence.etmtechAdmin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.smiligence.etmtechAdmin.Adapter.ImageListAdapter;
import com.smiligence.etmtechAdmin.Adapter.ImageSelectedAdapter;
import com.smiligence.etmtechAdmin.Adapter.ItemAdapter;
import com.smiligence.etmtechAdmin.bean.CategoryDetails;
import com.smiligence.etmtechAdmin.bean.CustomerDetails;
import com.smiligence.etmtechAdmin.bean.Ingredients;
import com.smiligence.etmtechAdmin.bean.ItemDetails;
import com.smiligence.etmtechAdmin.bean.ItemReviewAndRatings;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.Constant;
import com.smiligence.etmtechAdmin.common.DateUtils;
import com.smiligence.etmtechAdmin.common.MultiSelectionSpinnerCategory;
import com.smiligence.etmtechAdmin.common.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligence.etmtechAdmin.ViewOrderActivity.textViewEmail;
import static com.smiligence.etmtechAdmin.common.Constant.ACTIVE_STATUS;
import static com.smiligence.etmtechAdmin.common.Constant.BOOLEAN_FALSE;
import static com.smiligence.etmtechAdmin.common.Constant.CATEGORY_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.INACTIVE_STATUS;
import static com.smiligence.etmtechAdmin.common.Constant.ITEM_RATING_REVIEW_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.ORDER_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.SELECT_SUB_CATEGORY;
import static com.smiligence.etmtechAdmin.common.Constant.SELLER_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.TITLE_ITEMS;
import static com.smiligence.etmtechAdmin.common.MessageConstant.EXCLUSIVE_OF_TAXES;
import static com.smiligence.etmtechAdmin.common.MessageConstant.INCLUSIVE_OF_TAXES;
import static com.smiligence.etmtechAdmin.common.MessageConstant.ITEMDETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.MessageConstant.ITEM_IMAGE_STORAGE;
import static com.smiligence.etmtechAdmin.common.MessageConstant.PRODUCT_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.MessageConstant.REQUIRED_MSG;


public class AddItemActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    FloatingActionButton fab;
    String itemName, itemPrice, quantityString, itemDescription, itemMRPprice, itemFeatures;
    ProgressBar progressBar;
    MultiSelectionSpinnerCategory spinnerCategory, spinnerCategoryUpdate;
    ImageView image;
    TextView textViewUsername;
    int clipDataCount;
    int total = 0;
    DatabaseReference categoryRef, itemDetailsRef, sellerDetailDataRef;
    StorageReference itemStorageRef;
    DatabaseReference orderdetailRef;
    ArrayList<CategoryDetails> categoryList = new ArrayList<CategoryDetails>();
    ArrayList<String> categoryNamestring = new ArrayList<>();
    String selectedSubCategoryString;
    String selectedItemQuantityunits;
    EditText categoryNameEditText, subcategoryNameEditText, quantityAddItem, skuVariants, brandName, modelName;
    String itemQuantityUnitsString, itemLimitationString;
    EditText name, fixedprice, description, mrpPrice, features, rating, review;
    private Uri mimageuri, mimageuriUpdate, updateImageUri;
    ItemDetails itemDetails;
    private StorageTask mItemStorageTask;
    StorageTask mItemStorageTaskUpdate;
    int maxid = 0;
    EditText updateItemPrice;
    Button updateItemDetailsButton;
    Button b_upload, b_choosefile, b_multipleimages;
    EditText itemLimitation;
    public static String sellerLogo, sellerName;
    public static String sellerPinCode, sellerAddress, sellerId;
    RecyclerView recyclerView;
    ArrayList<ItemDetails> itemDetailList = new ArrayList<ItemDetails>();
    ItemAdapter itemAdapter;
    List<CategoryDetails> subCategoryList = new ArrayList<>();
    ImageView image_Item, multipleSelectionImages;
    Uri updatedownloadUrl;
    public static Uri updatedImagedownloadUrl;
    List<Bitmap> bitmapList = new ArrayList<>();
    ProgressBar updateProgressBarMutiImage;
    ImageView multipleimagesview;
    EditText taxEditText;
    Intent intentImage = new Intent();
    private static int PICK_IMAGE_REQUEST;
    View mHeaderView;
    String price_updateEdit;
    ItemDetails existingitemDetails;
    ItemDetails updateitemDetails;
    int price;
    int previousPrice;
    int categoryPosition;
    NavigationView navigationView;
    Uri imageUri;
    ArrayList<Uri> imageList = new ArrayList<>();
    ArrayList<String> imageStringList = new ArrayList<>();
    int uploadCount = 0;
    int counter = 0;
    public static int itemID;
    int exisitingItemId;
    AlertDialog b;
    String taxString;
    CheckBox inclusiveTax;
    EditText HSNCodeEditText;
    String HSNCodeString;
    ArrayList<Ingredients> ingredientsArrayList = new ArrayList<>();
    TextView selectedSpinnerData;
    ArrayList<String> imageUriList = new ArrayList<>();
    public static ArrayList<String> tempImageArray = new ArrayList<>();
    ListView imageListView;
    ImageListAdapter imageListAdapter;
    public static int staticCounter = 0;
    ImageView updatedImage;
    int countIndicator = 0;
    TextView selectedCategoryTextView;
    EditText itemNameUpdate, updateQty, fixedPriceUpdate, mrpUpdate, itemLimitationUpdate, HSNCodeupdate, taxPercentUpdate, descriptionUpdate,
            featureUpdate, brandNameUpdate, modelNameUpdate, skuVariantUpdate;
    CheckBox inclusiveTaxUpdate;
    int listSize;
    TextView reviewCount;

    RecyclerView selectedImageRecyclerView, selectedimagesUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(TITLE_ITEMS);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(AddItemActivity.this);
        drawer.setDrawerListener(toggle);
        navigationView.setCheckedItem(R.id.add_items);
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
        sellerDetailDataRef = CommonMethods.fetchFirebaseDatabaseReference(SELLER_DETAILS_TABLE);
        itemDetailsRef = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_TABLE);
        orderdetailRef = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_TABLE);
        itemStorageRef = CommonMethods.fetchFirebaseStorageReference(ITEMDETAILS_TABLE);

        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recycler_view);



        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddItemActivity.this));
        if (imageListAdapter != null && !tempImageArray.isEmpty()) {
            imageListAdapter.notifyDataSetChanged();
        }


        DatabaseReference viewCartRef = CommonMethods.fetchFirebaseDatabaseReference("ViewCart");

        viewCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    // Toast.makeText(AddItemActivity.this, "" + snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                    Log.v("Order1", snapshot.getChildren().iterator().next().getKey());

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        //Log.v("Order1", snapshot.getChildren().iterator().next().getKey());
                        String key = dataSnapshot.getChildren().iterator().next().getKey();
                       // String key = snapshot.getChildren().iterator().next().getKey();

//                        viewCartRef.child(key)
//                                .child("itemName")
//                                .setValue("Dell");

                        Log.v("Order", key);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

//        viewCartRef.orderByChild("itemName").equalTo("HP lap").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull  DataSnapshot snapshot) {
//
//
//                   // Toast.makeText(AddItemActivity.this, ""+snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        itemDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    itemDetailList.clear();

                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                        ItemDetails itemDetails = itemSnapshot.getValue(ItemDetails.class);
                        itemDetailList.add(itemDetails);
                    }
                    if (itemDetailList.size() > 0) {
                        itemAdapter = new ItemAdapter(AddItemActivity.this, itemDetailList);
                        recyclerView.setAdapter(itemAdapter);
                        itemAdapter.notifyDataSetChanged();
                    }

                    if (itemAdapter != null) {
                        itemAdapter.setOnItemclickListener(new ItemAdapter.OnItemClicklistener() {
                            @Override
                            public void Onitemclick(int Position) {
                                existingitemDetails = itemDetailList.get(Position);
                                CharSequence[] items = {"Update the Status of an item", "Update Item"};
                                androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(AddItemActivity.this);
                                dialog.setTitle("Choose an action");

                                dialog.setItems(items, new DialogInterface.OnClickListener() {


                                    public void onClick(DialogInterface dialog, final int item) {

                                        if (item == 0) {
                                            final String Item_id = String.valueOf(existingitemDetails.getItemId());
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(AddItemActivity.this);
                                            builder.setMessage("Select the item status for " + existingitemDetails.getItemName());
                                            builder.setCancelable(true);
                                            builder.setNegativeButton("UnAvailable", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                        itemDetailsRef.child(Item_id).child("itemStatus").setValue(INACTIVE_STATUS);
                                                        DatabaseReference customerDetailsRef = CommonMethods.fetchFirebaseDatabaseReference("CustomerLoginDetails");

                                                        customerDetailsRef.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                for (DataSnapshot customerSnap : dataSnapshot.getChildren()) {
                                                                    CustomerDetails customerDetails = customerSnap.getValue(CustomerDetails.class);


                                                                    DatabaseReference viewCartRef = CommonMethods.fetchFirebaseDatabaseReference("ViewCart").child(customerDetails.getCustomerId()).child(Item_id);

                                                                    viewCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            if (snapshot.getChildrenCount() > 0) {
                                                                                // Change view cart value while update item valyes.
                                                                                if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                                                    viewCartRef.child("itemStatus").setValue(INACTIVE_STATUS);
                                                                                }
                                                                            }
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
                                                        Toast.makeText(AddItemActivity.this, "Item Status Updated as InActive", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                            builder.setPositiveButton("Available", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                        itemDetailsRef.child(Item_id).child("itemStatus").setValue(Constant.ACTIVE_STATUS);

                                                        DatabaseReference customerDetailsRef = CommonMethods.fetchFirebaseDatabaseReference("CustomerLoginDetails");

                                                        customerDetailsRef.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                for (DataSnapshot customerSnap : dataSnapshot.getChildren()) {
                                                                    CustomerDetails customerDetails = customerSnap.getValue(CustomerDetails.class);


                                                                    DatabaseReference viewCartRef = CommonMethods.fetchFirebaseDatabaseReference("ViewCart").child(customerDetails.getCustomerId()).child(Item_id);

                                                                    viewCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            if (snapshot.getChildrenCount() > 0) {
                                                                                // Change view cart value while update item valyes.
                                                                                if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                                                    viewCartRef.child("itemStatus").setValue(ACTIVE_STATUS);
                                                                                }
                                                                            }
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
                                                        Toast.makeText(AddItemActivity.this, "Item Status Updated as Active", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            AlertDialog alert = builder.create();
                                            alert.show();
                                        }
                                        else if (item == 1)
                                        {
                                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddItemActivity.this);
                                            LayoutInflater inflater = getLayoutInflater();
                                            final View dialogView = inflater.inflate(R.layout.item_update, null);
                                            dialogBuilder.setView(dialogView);

                                            selectedCategoryTextView = dialogView.findViewById(R.id.selectedCategoryTextView);
                                            itemNameUpdate = dialogView.findViewById(R.id.itemnameUpdate);
                                            updateQty = dialogView.findViewById(R.id.updateQty);
                                            fixedPriceUpdate = dialogView.findViewById(R.id.fixedPriceUpdate);
                                            mrpUpdate = dialogView.findViewById(R.id.mrpUpdate);
                                            itemLimitationUpdate = dialogView.findViewById(R.id.itemLimitationUpdate);
                                            HSNCodeupdate = dialogView.findViewById(R.id.HSNCodeupdate);
                                            taxPercentUpdate = dialogView.findViewById(R.id.taxPercentUpdate);
                                            descriptionUpdate = dialogView.findViewById(R.id.descriptionUpdate);
                                            featureUpdate = dialogView.findViewById(R.id.featureUpdate);
                                            brandNameUpdate = dialogView.findViewById(R.id.brandNameUpdate);
                                            modelNameUpdate = dialogView.findViewById(R.id.modelNamUpdate);
                                            skuVariantUpdate = dialogView.findViewById(R.id.skuNumberUpdate);

                                            inclusiveTaxUpdate = dialogView.findViewById(R.id.inclusiveTaxUpdate);
                                            spinnerCategoryUpdate = dialogView.findViewById(R.id.e_catagory);

                                            updateItemPrice = dialogView.findViewById(R.id.updateprice);
                                            updateItemDetailsButton = dialogView.findViewById(R.id.updatedetailsDialog);
                                            final Button buttonUpdate = dialogView.findViewById(R.id.update_item);
                                            Button multiselectionImagesButton = dialogView.findViewById(R.id.addmultipleimageUpdatebutton);
                                            image_Item = dialogView.findViewById(R.id.updateitemimage);
                                            multipleSelectionImages = dialogView.findViewById(R.id.imageselectedUpdate);
                                            selectedimagesUpdate = dialogView.findViewById(R.id.selectedimagesUpdate);
                                            final Button pickImageForUpdate = dialogView.findViewById(R.id.pickimageforupdate);
                                            ImageView cancel = dialogView.findViewById(R.id.Cancel_subcategory);
                                            updateProgressBarMutiImage = dialogView.findViewById(R.id.item_update_progressBarMutiImage);
                                            imageListView = dialogView.findViewById(R.id.imageList);
                                            categoryRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    categoryList.clear();
                                                    categoryNamestring.clear();
                                                    categoryNamestring.add("Select Category");


                                                    for (DataSnapshot categorySnap : dataSnapshot.getChildren()) {

                                                        CategoryDetails categoryDetails = categorySnap.getValue(CategoryDetails.class);
                                                        categoryList.add(categoryDetails);
                                                        categoryNamestring.add(categoryDetails.getCategoryName());
                                                        spinnerCategoryUpdate.setItems(categoryList, selectedCategoryTextView);
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                            for (int i = 0; i < existingitemDetails.getCategoryDetailsArrayList().size(); i++) {

                                                if (existingitemDetails.getCategoryDetailsArrayList().size() == 1) {
                                                    selectedCategoryTextView.append(existingitemDetails.getCategoryDetailsArrayList().get(i).getCategoryName() + ".");
                                                } else {
                                                    selectedCategoryTextView.append(existingitemDetails.getCategoryDetailsArrayList().get(i).getCategoryName() + ",");
                                                }
                                            }
                                            final AlertDialog b = dialogBuilder.create();
                                            if (!isFinishing()) {
                                                b.show();
                                            }

                                            b.setCancelable(false);

                                            pickImageForUpdate.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    openFileChooser();
                                                    startActivityForResult(intentImage, 1);
                                                }
                                            });
                                            multiselectionImagesButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    bitmapList = new ArrayList<>();
                                                    imageStringList = new ArrayList<>();
                                                    imageList = new ArrayList<>();
                                                    openFileChooser();
                                                    countIndicator = 2;
                                                    startActivityForResult(intentImage, 100);
                                                }
                                            });

                                            price = (existingitemDetails.getItemPrice());
                                            updateItemPrice.setText(String.valueOf(price));
                                            itemNameUpdate.setText(existingitemDetails.getItemName());
                                            updateQty.setText(existingitemDetails.getItemQuantity());
                                            fixedPriceUpdate.setText(String.valueOf(existingitemDetails.getItemPrice()));
                                            mrpUpdate.setText(String.valueOf(existingitemDetails.getMRP_Price()));
                                            itemLimitationUpdate.setText(String.valueOf(existingitemDetails.getItemMaxLimitation()));
                                            HSNCodeupdate.setText(String.valueOf(existingitemDetails.getHSNCode()));
                                            taxPercentUpdate.setText(String.valueOf(existingitemDetails.getTax()));
                                            brandNameUpdate.setText(existingitemDetails.getBrandName());
                                            modelNameUpdate.setText(existingitemDetails.getModelName());
                                            skuVariantUpdate.setText(existingitemDetails.getSkuVariant());

                                            descriptionUpdate.setText(existingitemDetails.getItemDescription());
                                            featureUpdate.setText(existingitemDetails.getItemFeatures());


                                            if (INCLUSIVE_OF_TAXES.equalsIgnoreCase(existingitemDetails.getInclusiveOfTax())) {
                                                inclusiveTaxUpdate.setChecked(true);
                                            }


                                            imageUriList = (ArrayList<String>) existingitemDetails.getImageUriList();
                                            listSize = imageUriList.size();
                                            tempImageArray = imageUriList;


                                            if (imageUriList != null || !imageUriList.isEmpty()) {
                                                imageListAdapter = new ImageListAdapter(AddItemActivity.this, tempImageArray);
                                                imageListView.setAdapter(imageListAdapter);
                                                imageListAdapter.notifyDataSetChanged();
                                            }
                                            measureHeight();

                                            if (imageListAdapter != null) {
                                                imageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                                        String imageUri = tempImageArray.get(i);

                                                        CharSequence[] items = {"Delete", "Edit"};
                                                        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(AddItemActivity.this);
                                                        dialog.setTitle("Choose an action");
                                                        dialog.setItems(items, new DialogInterface.OnClickListener() {


                                                            public void onClick(DialogInterface dialog, final int item) {

                                                                if (item == 0) {

                                                                    androidx.appcompat.app.AlertDialog.Builder dialogBuilder =
                                                                            new androidx.appcompat.app.AlertDialog.Builder(AddItemActivity.this);
                                                                    LayoutInflater inflater = getLayoutInflater();
                                                                    final View dialogView = inflater.inflate(R.layout.deletecart_dialog, null);
                                                                    dialogBuilder.setView(dialogView);

                                                                    final Button buttonDelete = dialogView.findViewById(R.id.buttonDeleteUser);
                                                                    final ImageView buttonCancel = dialogView.findViewById(R.id.cancelButton);


                                                                    final androidx.appcompat.app.AlertDialog b = dialogBuilder.create();
                                                                    if (!isFinishing()) {
                                                                        b.show();
                                                                    }
                                                                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            b.dismiss();
                                                                        }
                                                                    });
                                                                    buttonDelete.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {

                                                                            final SweetAlertDialog pDialog = new SweetAlertDialog(AddItemActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                                                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#1898ED"));
                                                                            pDialog.setTitleText("Deleting Image.....");
                                                                            pDialog.setCancelable(false);


                                                                            ProgressDialog dialog1 = new ProgressDialog(AddItemActivity.this);
                                                                            dialog1.setMessage("Deleting Image");
                                                                            dialog1.setCancelable(false);
                                                                            if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                                                dialog1.show();
                                                                            }
                                                                            tempImageArray.remove(imageUri);
                                                                            imageListAdapter.notifyDataSetChanged();
                                                                            imageListView.setAdapter(imageListAdapter);

                                                                            measureHeight();


                                                                            itemStorageRef.getFile(Uri.parse(imageUri));

                                                                            itemStorageRef.child(ITEM_IMAGE_STORAGE).getStorage()
                                                                                    .getReferenceFromUrl(imageUri).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    // File deleted successfully
                                                                                    Toast.makeText(AddItemActivity.this, "Deleted Succesfully", Toast.LENGTH_SHORT).show();
                                                                                    itemDetailsRef.child(String.valueOf(existingitemDetails.getItemId())).child("imageUriList").setValue(tempImageArray);

                                                                                    if (dialog1.isShowing()) {
                                                                                        dialog1.dismiss();
                                                                                    }
                                                                                    countIndicator = 3;

                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception exception) {


                                                                                }
                                                                            });


                                                                            b.dismiss();

                                                                        }
                                                                    });

                                                                }


                                                                if (item == 1) {

                                                                    androidx.appcompat.app.AlertDialog.Builder dialogBuilder =
                                                                            new androidx.appcompat.app.AlertDialog.Builder(AddItemActivity.this);
                                                                    LayoutInflater inflater = getLayoutInflater();
                                                                    final View dialogView = inflater.inflate(R.layout.edit_image_dialog, null);
                                                                    dialogBuilder.setView(dialogView);
                                                                    dialogBuilder.setCancelable(BOOLEAN_FALSE);
                                                                    Button pickImage = dialogView.findViewById(R.id.selectimage);
                                                                    updatedImage = dialogView.findViewById(R.id.imageviewupdate);
                                                                    ImageView cancelButton = dialogView.findViewById(R.id.cancelButton);
                                                                    Button updatedImageButton = dialogView.findViewById(R.id.updateImageDialog);
                                                                    final androidx.appcompat.app.AlertDialog b = dialogBuilder.create();
                                                                    b.show();
                                                                    pickImage.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            openFileChooser();
                                                                            startActivityForResult(intentImage, 2);
                                                                        }
                                                                    });
                                                                    cancelButton.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            b.dismiss();
                                                                        }
                                                                    });

                                                                    Glide.with(AddItemActivity.this).load(imageUri).into(updatedImage);

                                                                    updatedImageButton.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            if (updateImageUri != null) {


                                                                                final SweetAlertDialog pDialog = new SweetAlertDialog(AddItemActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                                                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#1898ED"));
                                                                                pDialog.setTitleText("Updating Image.....");
                                                                                pDialog.setCancelable(false);
                                                                                pDialog.show();
                                                                                if (!imageUri.equalsIgnoreCase(String.valueOf(updateImageUri))) {

                                                                                    tempImageArray.remove(imageUri);
                                                                                    imageListAdapter.notifyDataSetChanged();
                                                                                    imageListView.setAdapter(imageListAdapter);

                                                                                    measureHeight();
                                                                                    itemStorageRef.getFile(Uri.parse(imageUri));

                                                                                    itemStorageRef.child(ITEM_IMAGE_STORAGE).getStorage()
                                                                                            .getReferenceFromUrl(imageUri).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                            // File deleted successfully


                                                                                            StorageReference imageFileStorageRef = itemStorageRef.child(ITEM_IMAGE_STORAGE
                                                                                                    + System.currentTimeMillis() + "." + getExtenstion(updateImageUri));

                                                                                            Bitmap bmp = null;
                                                                                            try {
                                                                                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mimageuri);
                                                                                                //  Toast.makeText(AddItemActivity.this, "he", Toast.LENGTH_SHORT).show();
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


                                                                                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                                                                            while (!urlTask.isSuccessful())
                                                                                                                ;
                                                                                                            updatedImagedownloadUrl = urlTask.getResult();
                                                                                                            tempImageArray.add(String.valueOf(updatedImagedownloadUrl));
                                                                                                            if (updatedImagedownloadUrl != null) {
                                                                                                                if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                                                                                    pDialog.dismiss();
                                                                                                                    b.dismiss();


                                                                                                                }
                                                                                                            }


                                                                                                            if (tempImageArray != null || !tempImageArray.isEmpty()) {
                                                                                                                imageListAdapter = new ImageListAdapter(AddItemActivity.this, tempImageArray);
                                                                                                                imageListView.setAdapter(imageListAdapter);
                                                                                                                imageListAdapter.notifyDataSetChanged();
                                                                                                            }

                                                                                                            measureHeight();


                                                                                                        }

                                                                                                    });


                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception exception) {


                                                                                        }
                                                                                    });

                                                                                } else {

                                                                                }


                                                                            } else {

                                                                                b.dismiss();
                                                                            }


                                                                        }
                                                                    });


                                                                }


                                                            }


                                                        });


                                                        dialog.show();

                                                    }
                                                });
                                            }


                                            previousPrice = existingitemDetails.getItemPrice();
                                            final int itemId = existingitemDetails.getItemId();
                                            Glide.with(AddItemActivity.this).load(existingitemDetails.getItemImage()).into(image_Item);

                                            cancel.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    b.dismiss();
                                                }
                                            });

                                            updateItemDetailsButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {


                                                    updateItemDetails();
                                                }

                                                private void updateItemDetails() {


                                                    price_updateEdit = updateItemPrice.getText().toString();


                                                    if (mimageuriUpdate != null) {


                                                        final SweetAlertDialog pDialog = new SweetAlertDialog(AddItemActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#1898ED"));
                                                        pDialog.setTitleText("Uploading Image.....");
                                                        pDialog.setCancelable(false);
                                                        if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                            pDialog.show();
                                                        }

                                                        StorageReference imageFileStorageRef = itemStorageRef.child(ITEM_IMAGE_STORAGE
                                                                + System.currentTimeMillis() + "." + getExtenstion(mimageuriUpdate));


                                                        mItemStorageTask = imageFileStorageRef.putFile(mimageuriUpdate).addOnSuccessListener(
                                                                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                                                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                                        while (!urlTask.isSuccessful())
                                                                            ;
                                                                        updatedownloadUrl = urlTask.getResult();


                                                                        uploadImageAndInsertData(updatedownloadUrl);

                                                                        pDialog.dismiss();


                                                                    }


                                                                });
                                                    } else if (mimageuriUpdate == null) {

                                                        final Uri downloadUrl = null;
                                                        uploadImageAndInsertData(Uri.parse(existingitemDetails.getItemImage()));

                                                    }
                                                }

                                                private void uploadImageAndInsertData(Uri downloadUrl) {
                                                    // Toast.makeText(AddItemActivity.this, ""+selectedCategoryTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                                                    String previousImage = existingitemDetails.getItemImage();
                                                    previousPrice = existingitemDetails.getItemPrice();

                                                    String itemPriceUpdate = updateItemPrice.getText().toString().trim();
                                                    String itemNameUpdateString = itemNameUpdate.getText().toString().trim();
                                                    String updateQtyString = updateQty.getText().toString().trim();
                                                    String fixedPriceUpdateString = fixedPriceUpdate.getText().toString().trim();
                                                    String mrpUpdateString = mrpUpdate.getText().toString().trim();
                                                    String itemLimitationUpdateString = itemLimitationUpdate.getText().toString().trim();
                                                    String HSNCodeupdateString = HSNCodeupdate.getText().toString().trim();
                                                    String descriptionUpdateString = descriptionUpdate.getText().toString().trim();
                                                    String featureUpdateString = featureUpdate.getText().toString().trim();
                                                    String inclusiveOfTaxes;
                                                    String taxPercentUpdateString = taxPercentUpdate.getText().toString().trim();
                                                    String existingCategoryNames = selectedCategoryTextView.getText().toString().trim();

                                                    if (selectedCategoryTextView == null && spinnerCategoryUpdate.getSelectedItems().size() == 0) {
                                                        Toast.makeText(AddItemActivity.this, "Select Category NAS", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }

                                                    if ("".equals(itemNameUpdateString)) {
                                                        itemNameUpdate.setError(REQUIRED_MSG);
                                                        Toast.makeText(getApplicationContext(), "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if (TextUtils.isNumeric(itemNameUpdateString) == true) {
                                                        itemNameUpdate.setError("Enter Valid Item Name");
                                                        Toast.makeText(getApplicationContext(), "Enter Valid Item Name", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if ((itemNameUpdateString).length() < 3) {
                                                        itemNameUpdate.setError("Minimum 3 charcaters");
                                                        Toast.makeText(getApplicationContext(), "Minimum 3 charcaters", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if (TextUtils.isNumeric(updateQtyString) == true) {
                                                        updateQty.setError("Enter Valid Item Quantity");
                                                        Toast.makeText(getApplicationContext(), "Enter Valid Item Quantity", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if ("".equals(updateQtyString)) {
                                                        updateQty.setError(REQUIRED_MSG);
                                                        Toast.makeText(getApplicationContext(), "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if ("".equals(fixedPriceUpdateString)) {
                                                        fixedPriceUpdate.setError(REQUIRED_MSG);
                                                        Toast.makeText(getApplicationContext(), "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if ("".equals(mrpUpdateString)) {
                                                        mrpUpdate.setError(REQUIRED_MSG);
                                                        Toast.makeText(getApplicationContext(), "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if (Integer.parseInt(fixedPriceUpdate.getText().toString()) == 0 || !TextUtils.isValidPrice(fixedPriceUpdate.getText().toString())) {
                                                        fixedPriceUpdate.setError("Enter valid Price");
                                                        Toast.makeText(getApplicationContext(), "Enter valid Price", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if (Integer.parseInt(mrpUpdate.getText().toString()) == 0 || !TextUtils.isValidPrice(mrpUpdate.getText().toString())) {
                                                        mrpUpdate.setError("Enter valid Price");
                                                        Toast.makeText(getApplicationContext(), "Enter valid Price", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if (Integer.parseInt(fixedPriceUpdateString) > Integer.parseInt(mrpUpdateString)) {
                                                        Toast.makeText(AddItemActivity.this, "MRP Should be Greater than Fixed Price and should not be equal", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if ("".equals(itemLimitationUpdateString)) {
                                                        itemLimitationUpdate.setError(REQUIRED_MSG);
                                                        Toast.makeText(getApplicationContext(), "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if (Integer.parseInt(itemLimitationUpdate.getText().toString()) == 0 ||
                                                            !TextUtils.isValidItemLimitation(itemLimitationUpdate.getText().toString())) {
                                                        itemLimitationUpdate.setError("Enter valid Item Limitation");
                                                        Toast.makeText(getApplicationContext(), "Enter valid Item Limitation", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if ("".equalsIgnoreCase(HSNCodeupdateString)) {
                                                        HSNCodeupdate.setError(REQUIRED_MSG);
                                                        Toast.makeText(getApplicationContext(), "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if (Integer.parseInt(HSNCodeupdateString) == 0 || !TextUtils.isValidPrice(HSNCodeupdateString)) {
                                                        HSNCodeupdate.setError("Enter valid HSN Code");
                                                        Toast.makeText(getApplicationContext(), "Enter valid HSN Code", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if ("".equalsIgnoreCase(taxPercentUpdateString)) {
                                                        taxPercentUpdate.setError(REQUIRED_MSG);
                                                        Toast.makeText(getApplicationContext(), "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if (Integer.parseInt(taxPercentUpdateString) == 0 || !TextUtils.isValidPrice(taxPercentUpdateString)) {
                                                        taxPercentUpdate.setError("Enter valid Percentage");
                                                        Toast.makeText(getApplicationContext(), "Enter valid Percentage", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if ("".equalsIgnoreCase(descriptionUpdateString) || descriptionUpdateString == null) {
                                                        descriptionUpdate.setError("Enter Item Description");
                                                        Toast.makeText(getApplicationContext(), "Enter Item Description", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else {
                                                        if (inclusiveTaxUpdate.isChecked()) {
                                                            inclusiveOfTaxes = INCLUSIVE_OF_TAXES;
                                                        } else {
                                                            inclusiveOfTaxes = "";
                                                        }

                                                        if (price_updateEdit == null || "".equals(price_updateEdit)) {
                                                            updateItemPrice.setError(REQUIRED_MSG);
                                                            Toast.makeText(getApplicationContext(), "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                                                            return;
                                                        } else if (Integer.parseInt(price_updateEdit) == 0 || !TextUtils.isValidPrice(price_updateEdit)) {
                                                            updateItemPrice.setError("Enter valid price");
                                                            Toast.makeText(getApplicationContext(), "Enter valid price", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        } else {
                                                            updateitemDetails = new ItemDetails();
                                                            if (tempImageArray != null && !tempImageArray.isEmpty() && tempImageArray.size() != 0) {
                                                                updateitemDetails.setImageUriList(tempImageArray);
                                                            }
                                                            SweetAlertDialog sDialog = new SweetAlertDialog(AddItemActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                                            sDialog.getProgressHelper().setBarColor(Color.parseColor("#1898ED"));
                                                            sDialog.setTitleText("Uploading Image.....");
                                                            sDialog.setCancelable(false);
                                                            if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                                sDialog.show();
                                                            }


                                                            exisitingItemId = existingitemDetails.getItemId();
                                                            updateitemDetails.setItemId(existingitemDetails.getItemId());
                                                            if (existingitemDetails.getMergeId() != null) {
                                                                updateitemDetails.setMergeId(existingitemDetails.getMergeId());
                                                            }
                                                            updateitemDetails.setItemPrice(Integer.parseInt(fixedPriceUpdateString));
                                                            updateitemDetails.setSubCategoryName(existingitemDetails.getSubCategoryName());
                                                            updateitemDetails.setItemName(itemNameUpdateString);
                                                            updateitemDetails.setCategoryId(existingitemDetails.getCategoryId());
                                                            if (spinnerCategoryUpdate.getSelectedItems().size() != 0) {
                                                                for (int i = 0; i < spinnerCategoryUpdate.getSelectedItems().size(); i++) {
                                                                    spinnerCategoryUpdate.getSelectedItems().get(i).setItemId(String.valueOf(existingitemDetails.getItemId()));
                                                                }
                                                            }
                                                            if (spinnerCategoryUpdate.getSelectedItems().size() == 0) {
                                                                updateitemDetails.setCategoryDetailsArrayList(existingitemDetails.getCategoryDetailsArrayList());
                                                            } else {
                                                                updateitemDetails.setCategoryDetailsArrayList(spinnerCategoryUpdate.getSelectedItems());
                                                            }
                                                            updateitemDetails.setMRP_Price(Integer.parseInt(mrpUpdateString));
                                                            updateitemDetails.setItemMaxLimitation(Integer.parseInt(itemLimitationUpdateString));
                                                            updateitemDetails.setItemMinLimitation(existingitemDetails.getItemMinLimitation());
                                                            updateitemDetails.setQuantityUnits(existingitemDetails.getQuantityUnits());
                                                            updateitemDetails.setItemQuantity(Integer.parseInt(updateQtyString));
                                                            updateitemDetails.setItemDescription(descriptionUpdateString);
                                                            updateitemDetails.setItemFeatures(featureUpdateString);
                                                            updateitemDetails.setStoreName(existingitemDetails.getStoreName());
                                                            updateitemDetails.setStoreAdress(existingitemDetails.getStoreAdress());
                                                            updateitemDetails.setStorePincode(existingitemDetails.getStorePincode());
                                                            updateitemDetails.setTotalItemQtyPrice(existingitemDetails.getTotalItemQtyPrice());
                                                            updateitemDetails.setWishList(existingitemDetails.getWishList());
                                                            updateitemDetails.setTax(Integer.parseInt(taxPercentUpdateString));
                                                            updateitemDetails.setHSNCode(Integer.parseInt(HSNCodeupdateString));
                                                            updateitemDetails.setBrandName(brandNameUpdate.getText().toString());
                                                            updateitemDetails.setSkuVariant(skuVariantUpdate.getText().toString());
                                                            updateitemDetails.setModelName(modelNameUpdate.getText().toString());

                                                            updateitemDetails.setItemBuyQuantity(1);
                                                            updateitemDetails.setItemCounter(1);
                                                            updateitemDetails.setInclusiveOfTax(inclusiveOfTaxes);
                                                            updateitemDetails.setItemImage(downloadUrl.toString());

                                                            int basePrice = 0;
                                                            basePrice = (100 * Integer.parseInt(fixedPriceUpdateString)) / (100 + Integer.parseInt(taxPercentUpdateString));

                                                            updateitemDetails.setBasePrice(basePrice);
                                                            int taxPriceAmount;
                                                            taxPriceAmount = Integer.parseInt(fixedPriceUpdateString) - basePrice;
                                                            updateitemDetails.setTaxPrice(taxPriceAmount);


                                                            updateitemDetails.setCreateDate(DateUtils.fetchCurrentDateAndTime());
                                                            updateitemDetails.setItemStatus(ACTIVE_STATUS);


                                                            if (clipDataCount >= 1) {

                                                                SweetAlertDialog alert = new SweetAlertDialog(AddItemActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                                                alert.getProgressHelper().setBarColor(Color.parseColor("#1898ED"));
                                                                alert.setTitleText("Uploading Image.....");
                                                                alert.setCancelable(false);
                                                                if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                                    alert.show();
                                                                }


                                                                for (uploadCount = 0; uploadCount < imageList.size(); uploadCount++) {

                                                                    Uri individualImage = imageList.get(uploadCount);
                                                                    StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ItemImages");
                                                                    final StorageReference imageName = ImageFolder.child("Image" + individualImage.getLastPathSegment());

                                                                    imageName.putFile(individualImage).addOnSuccessListener
                                                                            (new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                    String url = String.valueOf(taskSnapshot);
                                                                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                                                    while (!urlTask.isSuccessful())
                                                                                        ;
                                                                                    Uri url1 = urlTask.getResult();

                                                                                    tempImageArray = imageUriList;
                                                                                    ArrayList<String> newArray = new ArrayList<>();
                                                                                    tempImageArray.add(url1.toString());
                                                                                    newArray.add(url1.toString());

                                                                                    itemDetailsRef.child(String.valueOf(existingitemDetails.getItemId())).child("imageUriList").setValue(tempImageArray);
                                                                                    total = listSize + clipDataCount;

                                                                                    itemDetailsRef.child(String.valueOf(existingitemDetails.getItemId())).addValueEventListener(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                            if (dataSnapshot.exists()) {
                                                                                                ItemDetails itemDetails = dataSnapshot.getValue(ItemDetails.class);

                                                                                                if (total == itemDetails.getImageUriList().size()) {

                                                                                                    if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                                                                        alert.dismiss();
                                                                                                        b.dismiss();
                                                                                                        Intent intent = new Intent(AddItemActivity.this, AddItemActivity.class);
                                                                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                                                        startActivity(intent);
                                                                                                    }


                                                                                                } else {
                                                                                                    if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                                                                        alert.show();
                                                                                                    }
                                                                                                }

                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                }

                                                                            });

                                                                }
                                                            }


                                                            itemStorageRef.getFile(Uri.parse(previousImage));

                                                            sDialog.dismiss();

                                                            //delete

//                                                            itemStorageRef.child(ITEM_IMAGE_STORAGE).getStorage()
//                                                                    .getReferenceFromUrl(previousImage).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                @Override
//                                                                public void onSuccess(Void aVoid) {
//                                                                    if (!((Activity) AddItemActivity.this).isFinishing()) {
//                                                                        sDialog.dismiss();
//
//                                                                    }
//                                                                    // File deleted successfully
//
//                                                                }
//                                                            }).addOnFailureListener(new OnFailureListener() {
//                                                                @Override
//                                                                public void onFailure(@NonNull Exception exception) {
//
//
//                                                                }
//                                                            });

                                                            if (updatedownloadUrl != null) {
                                                                updateitemDetails.setItemImage(updatedownloadUrl.toString());
                                                            } else {
                                                                updateitemDetails.setItemImage(existingitemDetails.getItemImage());
                                                            }

                                                            itemDetailsRef.child(String.valueOf(existingitemDetails.getItemId())).setValue(updateitemDetails);


                                                            DatabaseReference customerDetailsRef = CommonMethods.fetchFirebaseDatabaseReference("CustomerLoginDetails");

                                                            customerDetailsRef.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                    for (DataSnapshot customerSnap : dataSnapshot.getChildren()) {
                                                                        CustomerDetails customerDetails = customerSnap.getValue(CustomerDetails.class);


                                                                        DatabaseReference viewCartRef = CommonMethods.fetchFirebaseDatabaseReference("ViewCart").child(customerDetails.getCustomerId()).child(String.valueOf(existingitemDetails.getItemId()));

                                                                        viewCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if (snapshot.getChildrenCount() > 0) {

                                                                                    ItemDetails itemDetails = snapshot.getValue(ItemDetails.class);

                                                                                    // Change view cart value while update item valyes.
                                                                                    if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                                                        viewCartRef.child("itemName").setValue(itemNameUpdateString);
                                                                                        viewCartRef.child("itemPrice").setValue(Integer.parseInt(fixedPriceUpdateString));
                                                                                        viewCartRef.child("mrp_Price").setValue(Integer.parseInt(mrpUpdateString));
                                                                                        viewCartRef.child("totalTaxPrice").setValue(taxPriceAmount * itemDetails.getItemBuyQuantity());
                                                                                        viewCartRef.child("totalItemQtyPrice").setValue((Integer.parseInt(fixedPriceUpdateString)) * itemDetails.getItemBuyQuantity());
                                                                                    }

                                                                                }

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

                                                            Toast.makeText(AddItemActivity.this, "Updated Sucessfully", Toast.LENGTH_SHORT).show();

                                                            mimageuri = null;
                                                            bitmapList = new ArrayList<>();
                                                            imageStringList = new ArrayList<>();
                                                            imageList = new ArrayList<>();


                                                            b.dismiss();


                                                            sDialog.dismiss();


                                                        }
                                                    }


                                                }
                                            });
                                        }
                                    }
                                });

                                dialog.show();

                            }

                            private void measureHeight() {
                                if (imageListAdapter != null) {
                                    int totalHeight = 0;

                                    for (int i = 0; i < imageListAdapter.getCount(); i++) {
                                        View listItem = imageListAdapter.getView(i, null, imageListView);
                                        listItem.measure(0, 0);
                                        totalHeight += listItem.getMeasuredHeight();
                                    }

                                    ViewGroup.LayoutParams params = imageListView.getLayoutParams();
                                    params.height = totalHeight + (imageListView.getDividerHeight() * (imageListAdapter.getCount() - 1));
                                    imageListView.setLayoutParams(params);
                                    imageListView.requestLayout();
                                    imageListView.setAdapter(imageListAdapter);
                                    imageListAdapter.notifyDataSetChanged();

                                }
                            }
                        });

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddItemActivity.this);
                final LayoutInflater inflater = getLayoutInflater();
                final View alertdialogView = inflater.inflate(R.layout.add_item_dialog, null);
                alertDialogBuilder.setView(alertdialogView);

                spinnerCategory = alertdialogView.findViewById(R.id.e_catagory);
                selectedSpinnerData = alertdialogView.findViewById(R.id.selectedSpinnerData);
                b_choosefile = alertdialogView.findViewById(R.id.choose_image);
                b_multipleimages = alertdialogView.findViewById(R.id.choosemultipleimage);
                b_upload = alertdialogView.findViewById(R.id.b_Upload);

                image = alertdialogView.findViewById(R.id.image);
                progressBar = alertdialogView.findViewById(R.id.progressBar);
                name = alertdialogView.findViewById(R.id.file_name);
                fixedprice = alertdialogView.findViewById(R.id.item_price);
                categoryNameEditText = alertdialogView.findViewById(R.id.category);
                subcategoryNameEditText = alertdialogView.findViewById(R.id.subcategory);
                //  brand = alertdialogView.findViewById(R.id.itembrand);
                multipleimagesview = alertdialogView.findViewById(R.id.selectedimages);
                selectedImageRecyclerView = alertdialogView.findViewById(R.id.selectedrecycler);
                taxEditText = alertdialogView.findViewById(R.id.tax);
                inclusiveTax = alertdialogView.findViewById(R.id.inclusiveTax);
                description = alertdialogView.findViewById(R.id.itemdescription);
                mrpPrice = alertdialogView.findViewById(R.id.MRPprice);
                features = alertdialogView.findViewById(R.id.itemfeature);
                itemLimitation = alertdialogView.findViewById(R.id.itemLimitation);
                ImageView cancel = alertdialogView.findViewById(R.id.newImage);
                quantityAddItem = alertdialogView.findViewById(R.id.quantityOnitemadd);
                skuVariants = alertdialogView.findViewById(R.id.skuNumber);
                brandName = alertdialogView.findViewById(R.id.brandName);
                modelName = alertdialogView.findViewById(R.id.modelName);

                HSNCodeEditText = alertdialogView.findViewById(R.id.hsnCode);


                b = alertDialogBuilder.create();
                if (!isFinishing()) {
                    b.show();
                }

                inclusiveTax.setChecked(true);


                ArrayAdapter<CharSequence> adapter = ArrayAdapter
                        .createFromResource(AddItemActivity.this, R.array.Quantity_units_spinner,
                                R.layout.support_simple_spinner_dropdown_item);
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);


                b_choosefile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFileChooser();
                        startActivityForResult(intentImage, 0);
                    }
                });
                b_multipleimages.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bitmapList = new ArrayList<>();
                        imageStringList = new ArrayList<>();
                        imageList = new ArrayList<>();
                        openFileChooser();
                        countIndicator = 0;
                        startActivityForResult(intentImage, 100);

                    }
                });

                categoryRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        categoryList.clear();
                        categoryNamestring.clear();
                        categoryNamestring.add("Select Category");


                        for (DataSnapshot categorySnap : dataSnapshot.getChildren()) {

                            CategoryDetails categoryDetails = categorySnap.getValue(CategoryDetails.class);
                            categoryList.add(categoryDetails);
                            categoryNamestring.add(categoryDetails.getCategoryName());
                            spinnerCategory.setItems(categoryList, selectedSpinnerData);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                b_upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inclusiveTax.setChecked(true);
                        itemName = name.getText().toString().trim();

                        itemPrice = fixedprice.getText().toString().trim();
                        //  itemBrand = brand.getText().toString().trim();
                        quantityString = quantityAddItem.getText().toString().trim();
                        itemQuantityUnitsString = selectedItemQuantityunits;
                        itemDescription = description.getText().toString().trim();
                        itemMRPprice = mrpPrice.getText().toString().trim();
                        itemFeatures = features.getText().toString().trim();
                        itemLimitationString = itemLimitation.getText().toString().trim();
                        taxString = taxEditText.getText().toString().trim();
                        HSNCodeString = HSNCodeEditText.getText().toString().trim();
                        UploadFile();

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        b.dismiss();
                    }
                });

                b.setCancelable(false);
            }
        });

    }


    private void openFileChooser() {
        intentImage = new Intent();
        intentImage.setType("image/*");
        intentImage.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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
                    Glide.with(AddItemActivity.this).load(mimageuri).into(image);

                }
                break;


            case 1:
                PICK_IMAGE_REQUEST = 1;
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    mimageuriUpdate = data.getData();
                    Glide.with(AddItemActivity.this).load(mimageuriUpdate).into(image_Item);
                }
                break;
            case 2:
                PICK_IMAGE_REQUEST = 2;
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    updateImageUri = data.getData();
                    Glide.with(AddItemActivity.this).load(updateImageUri).into(updatedImage);
                }
                break;


            case 100:
                PICK_IMAGE_REQUEST = 100;

                if (requestCode == PICK_IMAGE_REQUEST) {
                    if (resultCode == RESULT_OK) {
                        if (data.getData() != null) {


                            clipDataCount = 1;

                            imageUri = data.getData();
                            imageList.add(imageUri);

                            try {
                                InputStream is = getContentResolver().openInputStream(imageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(is);
                                bitmapList.add(bitmap);


                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                        } else if (data.getClipData() != null) {

                            clipDataCount = data.getClipData().getItemCount();

                            int currentImageSelectCount = 0;
                            while (currentImageSelectCount < clipDataCount) {

                                imageUri = data.getClipData().getItemAt(currentImageSelectCount).getUri();
                                imageList.add(imageUri);


                                currentImageSelectCount = currentImageSelectCount + 1;

                            }

                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                imageUri = data.getClipData().getItemAt(i).getUri();
                                try {
                                    InputStream is = getContentResolver().openInputStream(imageUri);
                                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                                    bitmapList.add(bitmap);

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }


                        } else {
                            imageUri = data.getData();
                            try {


                                InputStream is = getContentResolver().openInputStream(imageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(is);
                                bitmapList.add(bitmap);


                            } catch (FileNotFoundException e) {
                                e.printStackTrace();

                            }
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (final Bitmap b : bitmapList) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {


                                            if (countIndicator == 2) {


                                                System.out.println("SIZEEEOFSLIDER  " + bitmapList.size());


                                                ImageSelectedAdapter imageListAdapter = new ImageSelectedAdapter(AddItemActivity.this, bitmapList);
                                                RecyclerView.LayoutManager RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());

                                                selectedimagesUpdate.setLayoutManager(RecyclerViewLayoutManager);
                                                if (bitmapList.size() != 0 && !bitmapList.isEmpty()) {
                                                    LinearLayoutManager HorizontalLayout
                                                            = new LinearLayoutManager(
                                                            AddItemActivity.this,
                                                            LinearLayoutManager.VERTICAL,
                                                            false);
                                                    selectedimagesUpdate.setLayoutManager(HorizontalLayout);
                                                    imageListAdapter.notifyDataSetChanged();
                                                    selectedimagesUpdate.setAdapter(imageListAdapter);

                                                    System.out.
                                                            println("SIZEEEOFSLIDER  " + bitmapList.size());

                                                }
                                                multipleSelectionImages.setImageBitmap(b);

                                            }

                                            if (countIndicator == 0) {


                                                ImageSelectedAdapter imageListAdapter = new ImageSelectedAdapter(AddItemActivity.this, bitmapList);
                                                RecyclerView.LayoutManager RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());

                                                selectedImageRecyclerView.setLayoutManager(RecyclerViewLayoutManager);
                                                if (bitmapList.size() != 0 && !bitmapList.isEmpty()) {
                                                    LinearLayoutManager HorizontalLayout
                                                            = new LinearLayoutManager(
                                                            AddItemActivity.this,
                                                            LinearLayoutManager.HORIZONTAL,
                                                            false);
                                                    selectedImageRecyclerView.setLayoutManager(HorizontalLayout);
                                                    imageListAdapter.notifyDataSetChanged();
                                                    selectedImageRecyclerView.setAdapter(imageListAdapter);

                                                    System.out.
                                                            println("SIZEEEOFSLIDER  " + bitmapList.size());

                                                }

                                                multipleimagesview.setImageBitmap(b);
                                            }


                                        }
                                    });
                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }).start();
                    } else {
                        Toast.makeText(this, "select Image", Toast.LENGTH_SHORT).show();
                    }
                }
                break;


        }


    }


    private void UploadFile() {

        if (spinnerCategory.getSelectedItems().size() == 0) {
            Toast.makeText(this, "Select the category", Toast.LENGTH_SHORT).show();
            return;
        } else if ("".equals(itemName)) {
            name.setError(REQUIRED_MSG);
            return;
        } else if (TextUtils.isNumeric(itemName) == true) {
            name.setError("Enter Valid Item Name");
            return;
        } else if ("".equals(quantityString)) {
            quantityAddItem.setError(REQUIRED_MSG);
            return;
        } else if (TextUtils.isNumeric(quantityString) == true) {
            quantityAddItem.setError("Enter Valid Item Quantity");
            return;
        } else if ("".equals(itemPrice)) {
            fixedprice.setError(REQUIRED_MSG);
            return;
        } else if (itemPrice.startsWith("0")) {
            fixedprice.setError("");
            if (itemPrice.length() > 0) {
                fixedprice.setText(itemPrice.substring(1));
                return;
            } else {
                fixedprice.setText("");
                return;
            }
        } else if ("".equals(itemMRPprice)) {
            mrpPrice.setError(REQUIRED_MSG);
            return;
        } else if (itemMRPprice.startsWith("0")) {
            mrpPrice.setError("Minimum Bill amount should not starts with (0)");
            if (itemMRPprice.length() > 0) {
                mrpPrice.setText(itemMRPprice.substring(1));
                return;
            } else {
                mrpPrice.setText("");
                return;
            }
        } else if (Integer.parseInt(fixedprice.getText().toString()) == 0 || !TextUtils.isValidPrice(fixedprice.getText().toString())) {
            fixedprice.setError("Enter valid Price");
            Toast.makeText(getApplicationContext(), "Enter valid Price", Toast.LENGTH_SHORT).show();
            return;
        } else if (Integer.parseInt(mrpPrice.getText().toString()) == 0 || !TextUtils.isValidPrice(mrpPrice.getText().toString())) {
            mrpPrice.setError("Enter valid Price");
            Toast.makeText(getApplicationContext(), "Enter valid Price", Toast.LENGTH_SHORT).show();
            return;
        } else if (Integer.parseInt(fixedprice.getText().toString().trim()) > Integer.parseInt(mrpPrice.getText().toString().trim())) {
            Toast.makeText(AddItemActivity.this, "MRP Should be Greater than Fixed Price and should not be equal ", Toast.LENGTH_SHORT).show();
            return;
        } else if ("".equals(itemLimitationString)) {
            itemLimitation.setError(REQUIRED_MSG);
            Toast.makeText(getApplicationContext(), "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
            return;
        } else if (Integer.parseInt(itemLimitation.getText().toString()) == 0 || !TextUtils.isValidItemLimitation(itemLimitation.getText().toString())) {
            itemLimitation.setError("Enter valid Item Limitation");
            Toast.makeText(getApplicationContext(), "Enter valid Item Limitation", Toast.LENGTH_SHORT).show();
            return;
        } else if ("".equalsIgnoreCase(HSNCodeString)) {
            HSNCodeEditText.setError(REQUIRED_MSG);
            Toast.makeText(getApplicationContext(), "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
            return;
        } else if (Integer.parseInt(HSNCodeString) == 0 || !TextUtils.isValidPrice(HSNCodeString)) {
            HSNCodeEditText.setError("Enter valid HSN Code");
            Toast.makeText(getApplicationContext(), "Enter valid HSN Code", Toast.LENGTH_SHORT).show();
            return;
        } else if ("".equalsIgnoreCase(taxString)) {
            taxEditText.setError(REQUIRED_MSG);
            Toast.makeText(getApplicationContext(), "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
            return;
        } else if (Integer.parseInt(taxString) == 0 || !TextUtils.isValidPrice(taxString)) {
            taxEditText.setError("Enter valid Percentage");
            Toast.makeText(getApplicationContext(), "Enter valid Percentage", Toast.LENGTH_SHORT).show();
            return;
        } else if ("".equalsIgnoreCase(itemDescription) || itemDescription == null) {
            description.setError("Enter Item Description");
            Toast.makeText(getApplicationContext(), "Enter Item Description", Toast.LENGTH_SHORT).show();
            return;
        } else if (mimageuri != null) {

            if (itemLimitationString == null || "".equals(itemLimitationString)) {
                itemLimitationString = "50";
            }

            if ((SELECT_SUB_CATEGORY).equals(subcategoryNameEditText.getText().toString())) {
                selectedSubCategoryString = "GENERAL CATEGORY";
                subcategoryNameEditText.setText(selectedSubCategoryString);

            }


            itemDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    b_choosefile.setVisibility(View.INVISIBLE);
                    b_multipleimages.setVisibility(View.INVISIBLE);
                    b_upload.setVisibility(View.INVISIBLE);

                    itemDetails = new ItemDetails();
                    itemDetails.setItemId(maxid + 1);
                    itemDetails.setItemName(itemName);

                    if ("".equals(quantityAddItem.getText().toString())) {
                        Toast.makeText(AddItemActivity.this, "Enter valid quantity", Toast.LENGTH_SHORT).show();
                    } else {
                        itemDetails.setItemQuantity(Integer.parseInt((quantityString)));
                    }

                    itemDetails.setItemPrice(Integer.parseInt(itemPrice));
                    //  itemDetails.setItemBrand(itemBrand);
                    itemDetails.setQuantityUnits(itemQuantityUnitsString);
                    itemDetails.setMRP_Price(Integer.parseInt(itemMRPprice));
                    itemDetails.setItemDescription(itemDescription);
                    itemDetails.setItemFeatures(itemFeatures);
                    itemDetails.setCreateDate(DateUtils.fetchCurrentDateAndTime());
                    itemDetails.setTax(Integer.parseInt(taxString));
                    itemDetails.setHSNCode(Integer.parseInt(HSNCodeString));
                    itemDetails.setCategoryId(String.valueOf(categoryPosition));
                    itemDetails.setBrandName(brandName.getText().toString());
                    itemDetails.setModelName(modelName.getText().toString());
                    itemDetails.setSkuVariant(skuVariants.getText().toString());



                    itemDetails.setItemBuyQuantity(1);
                    itemDetails.setItemCounter(1);

                    if (spinnerCategory.getSelectedItems().size() != 0) {
                        for (int i = 0; i < spinnerCategory.getSelectedItems().size(); i++) {
                            spinnerCategory.getSelectedItems().get(i).setItemId(String.valueOf(maxid + 1));
                        }
                    }

                    itemDetails.setCategoryDetailsArrayList(spinnerCategory.getSelectedItems());
                    int basePrice = 0;
                    basePrice = (100 * Integer.parseInt(itemPrice)) / (100 + Integer.parseInt(taxString));

                    itemDetails.setBasePrice(basePrice);
                    int taxPriceAmount;
                    taxPriceAmount = Integer.parseInt(itemPrice) - basePrice;
                    itemDetails.setTaxPrice(taxPriceAmount);
                    Toast.makeText(AddItemActivity.this, ""+taxPriceAmount, Toast.LENGTH_SHORT).show();

                    if (inclusiveTax.isChecked()) {
                        itemDetails.setInclusiveOfTax(INCLUSIVE_OF_TAXES);
                    } else {
                        itemDetails.setInclusiveOfTax(EXCLUSIVE_OF_TAXES);
                    }


                    if ("".equals(itemLimitationString)) {
                        Toast.makeText(AddItemActivity.this, "Enter valid limitation", Toast.LENGTH_SHORT).show();
                    } else {
                        itemDetails.setItemMaxLimitation(Integer.parseInt(itemLimitationString));
                    }

                    itemDetails.setItemStatus(ACTIVE_STATUS);

                    itemDetails.setStoreAdress(sellerAddress);
                    itemDetails.setStoreName(sellerName);
                    itemDetails.setSellerId(sellerId);

                    if (sellerLogo != null) {
                        itemDetails.setStoreLogo(sellerLogo);
                    } else {
                        itemDetails.setStoreLogo("");
                    }
                    itemDetails.setStorePincode(sellerPinCode);


                    itemDetails.setCategoryName(categoryNameEditText.getText().toString());
                    itemDetails.setSubCategoryName(subcategoryNameEditText.getText().toString());


                    StorageReference imageFileStorageRef = itemStorageRef.child(ITEM_IMAGE_STORAGE
                            + System.currentTimeMillis() + "." + getExtenstion(mimageuri));

                    Bitmap bmp = null;
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mimageuri);
                        //  Toast.makeText(AddItemActivity.this, "he", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 25, baos);
                    byte[] data = baos.toByteArray();

                    final SweetAlertDialog pDialog = new SweetAlertDialog(AddItemActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#1898ED"));
                    pDialog.setTitleText("Uploading Image.....");
                    pDialog.setCancelable(false);
                    pDialog.show();


                    mItemStorageTask = imageFileStorageRef.putBytes(data).addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @SuppressLint("ResourceType")
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    final Uri downloadUrl = urlTask.getResult();

                                    RequestOptions requestOptions = new RequestOptions();
                                    requestOptions.placeholder(R.mipmap.ic_launcher_new);
                                    requestOptions.error(R.mipmap.ic_launcher_new);
                                    Glide.with(AddItemActivity.this)
                                            .setDefaultRequestOptions(requestOptions)
                                            .load(itemDetails.getItemImage()).fitCenter().into(image);

                                    if (clipDataCount > 0) {
                                        for (uploadCount = 0; uploadCount < imageList.size(); uploadCount++) {

                                            Bitmap bmp = null;
                                            try {
                                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageList.get(uploadCount));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            bmp.compress(Bitmap.CompressFormat.PNG, 25, baos);
                                            byte[] data = baos.toByteArray();

                                            Uri individualImage = imageList.get(uploadCount);
                                            StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ItemImages");
                                            final StorageReference imageName = ImageFolder.child("Image" + individualImage.getLastPathSegment());

                                            imageName.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    String url = String.valueOf(taskSnapshot);
                                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                    while (!urlTask.isSuccessful()) ;
                                                    Uri url1 = urlTask.getResult();


                                                    imageStringList.add(url1.toString());

                                                    multipleimagesview.setImageResource(R.drawable.b_chooseimage);

                                                    itemDetailsRef.child(String.valueOf(itemID)).child("imageUriList").setValue(imageStringList);
                                                    if (imageStringList != null && !imageStringList.isEmpty()) {


                                                        itemDetailsRef.child(String.valueOf(itemID)).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()) {

                                                                    ItemDetails itemDetails = dataSnapshot.getValue(ItemDetails.class);
                                                                    itemDetails.getImageUriList();
                                                                    if (clipDataCount == itemDetails.getImageUriList().size()) {
                                                                        Toast.makeText(AddItemActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                                                        pDialog.dismiss();
                                                                        b.dismiss();
                                                                        Intent intent = new Intent(AddItemActivity.this, AddItemActivity.class);
                                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                        startActivity(intent);
                                                                    } else {
                                                                        if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                                            pDialog.show();
                                                                        }
                                                                    }

                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }

                                                }

                                            });


                                        }
                                    } else {
                                        pDialog.dismiss();
                                        b.dismiss();
                                        Intent intent = new Intent(AddItemActivity.this, AddItemActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                    }


                                    itemDetails.setItemImage(downloadUrl.toString());
                                    itemDetails.setWishList(BOOLEAN_FALSE);

                                    itemDetailsRef.child(String.valueOf(maxid + 1)).setValue(itemDetails);


                                    itemID = maxid + 1;


                                    b_choosefile.setVisibility(View.VISIBLE);
                                    b_upload.setVisibility(View.VISIBLE);
                                    b_multipleimages.setVisibility(View.VISIBLE);
                                    name.setText("");
                                    fixedprice.setText("");
                                    //brand.setText("");
                                    quantityAddItem.setText((quantityString));
                                    description.setText("");
                                    mrpPrice.setText("");
                                    features.setText("");
                                    HSNCodeEditText.setText("");
                                    taxEditText.setText("");
                                    itemLimitation.setText("");
                                    image.setImageResource(R.drawable.b_chooseimage);
                                    categoryNameEditText.setText("");
                                    ingredientsArrayList.clear();
                                    //  mimageuri = null;
                                    bitmapList = new ArrayList<>();
                                    imageStringList = new ArrayList<>();
                                    imageList = new ArrayList<>();
                                    multipleimagesview.setImageResource(R.drawable.b_chooseimage);

                                    mimageuri = null;
                                    mimageuriUpdate = null;
                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(AddItemActivity.this, "Please Select More Images or Select Images!!", Toast.LENGTH_LONG).show();
            b_choosefile.setVisibility(View.VISIBLE);
            b_upload.setVisibility(View.VISIBLE);

        }
    }


    private String getExtenstion(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void StoreLink(String url) {


        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Img Link", url);

        if (countIndicator == 0) {
            imageStringList.add(url.toString());
            itemDetailsRef.child(String.valueOf(itemID)).child("imageUriList").setValue(imageStringList);
            Toast.makeText(this, "uploaded", Toast.LENGTH_SHORT).show();
        }

        if (countIndicator == 2) {

            final SweetAlertDialog pDialog = new SweetAlertDialog(AddItemActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#1898ED"));
            pDialog.setTitleText("Uploading Image.....");
            pDialog.setCancelable(false);
            pDialog.show();


            tempImageArray.add(url);
            itemDetailsRef.child(String.valueOf(itemID)).child("imageUriList").setValue(tempImageArray);


        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        itemDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                maxid = (int) dataSnapshot.getChildrenCount();

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
        else if (id == R.id.uploadcontactdetails) {
            Intent intent = new Intent(getApplicationContext(), DeliverySettings.class);
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
        }/* else if (id == R.id.deliveryfare) {
            Intent intent = new Intent(getApplicationContext(), AddDeliveryFareActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } */ else if (id == R.id.contactdetails) {

            Intent intent = new Intent(getApplicationContext(), DeliverySettings.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.logoutscreen) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AddItemActivity.this);
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
                    Intent intent = new Intent(AddItemActivity.this,
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
                    navigationView.setCheckedItem(R.id.add_items);
                }
            });
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

        return false;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }


}