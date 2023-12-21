package com.smiligence.etmtechAdmin;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.smiligence.etmtechAdmin.Adapter.DescriptionAdapter;
import com.smiligence.etmtechAdmin.Adapter.ImageListAdapter;
import com.smiligence.etmtechAdmin.bean.CategoryDetails;
import com.smiligence.etmtechAdmin.bean.Ingredients;
import com.smiligence.etmtechAdmin.bean.ItemDetails;
import com.smiligence.etmtechAdmin.bean.ItemReviewAndRatings;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.MultiSelectionSpinnerCategory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligence.etmtechAdmin.ViewOrderActivity.textViewEmail;
import static com.smiligence.etmtechAdmin.common.Constant.CATEGORY_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.ITEM_RATING_REVIEW_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.ORDER_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.SELLER_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.TITLE_DESCRIPTOB;
import static com.smiligence.etmtechAdmin.common.MessageConstant.ITEMDETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.MessageConstant.PRODUCT_DETAILS_TABLE;


public class Add_Description_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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
    DescriptionAdapter itemAdapter;
    List<CategoryDetails> subCategoryList = new ArrayList<>();
    private final int PICK_PDF_CODE = 2342;
    ImageView image_Item, multipleSelectionImages;
    Uri updatedownloadUrl;
    public static Uri updatedImagedownloadUrl;
    List<Bitmap> bitmapList = new ArrayList<>();
    ProgressBar updateProgressBarMutiImage;
    ImageView multipleimagesview;
    EditText taxEditText;
    private Uri filepath;
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
    FirebaseStorage storage;
    StorageReference storageReference;
    Button backArrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_description_activity);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(TITLE_DESCRIPTOB);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(Add_Description_Activity.this);
        drawer.setDrawerListener(toggle);
        navigationView.setCheckedItem(R.id.add_items);
        mHeaderView = navigationView.getHeaderView(0);
        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);
        textViewUsername.setText(DashBoardActivity.roleName);
        textViewEmail.setText(DashBoardActivity.userName);

        reviewCount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.reviewApproval));
        reviewCount.setGravity(Gravity.CENTER_VERTICAL);
        reviewCount.setTypeface(null, Typeface.BOLD);
        reviewCount.setTextColor(getResources().getColor(R.color.redColor));

        categoryRef = CommonMethods.fetchFirebaseDatabaseReference(CATEGORY_DETAILS_TABLE);
        sellerDetailDataRef = CommonMethods.fetchFirebaseDatabaseReference(SELLER_DETAILS_TABLE);
        itemDetailsRef = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_TABLE);
        orderdetailRef = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_TABLE);
        itemStorageRef = CommonMethods.fetchFirebaseStorageReference(ITEMDETAILS_TABLE);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //  databaseReference= FirebaseDatabase.getInstance("https://kidsdevelopmentnew-default-rtdb.firebaseio.com/").getReference("Test");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Add_Description_Activity.this));
        if (imageListAdapter != null && !tempImageArray.isEmpty()) {

            imageListAdapter.notifyDataSetChanged();
        }


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
                        itemAdapter = new DescriptionAdapter(Add_Description_Activity.this, itemDetailList);
                        recyclerView.setAdapter(itemAdapter);
                        itemAdapter.notifyDataSetChanged();
                    }

                    if (itemAdapter != null) {
                        itemAdapter.setOnItemclickListener(new DescriptionAdapter.OnItemClicklistener() {
                            @Override
                            public void Onitemclick(int Position) {
                                existingitemDetails = itemDetailList.get(Position);

                                CharSequence[] items = {"Add Description", "View Description"};
                                AlertDialog.Builder dialog = new AlertDialog.Builder(Add_Description_Activity.this);
                                dialog.setTitle("Choose");
                                dialog.setItems(items, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, final int item) {

                                        if (item == 0) {
                                            chooseDoc();
                                        } else if (item == 1) {
                                            if (existingitemDetails.getDescriptionUrl() != null && !existingitemDetails.getDescriptionUrl().equals("")) {
                                                Intent viewWebIntent = new Intent(getApplicationContext(), WebPageActivity.class);
                                                viewWebIntent.putExtra("intentHtmlUrl", existingitemDetails.getDescriptionUrl());
                                                startActivity(viewWebIntent);
                                            } else {
                                                Toast.makeText(Add_Description_Activity.this, "No File Uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });

                                dialog.show();
                                measureHeight();

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


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (data.getData() != null) {
                filepath = data.getData();
                UploadFile();
            } else {
                Toast.makeText(this, "NO FILE CHOSEN", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void UploadFile() {
        if (filepath != null) {

            Date dateObject = new Date(System.currentTimeMillis());
            String formattedDate = formatDate(dateObject);
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference sref = storageReference.child("HTMLFILES" + formattedDate + ".html");

            sref.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl_update = urlTask.getResult();
                            Log.d("url", downloadUrl_update.toString());
                            itemDetailsRef.child(String.valueOf(existingitemDetails.getItemId())).child("descriptionUrl").setValue(downloadUrl_update.toString());
                            Toast.makeText(Add_Description_Activity.this, "Uploaded Succesfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Add_Description_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot
                                    .getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
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

            final SweetAlertDialog pDialog = new SweetAlertDialog(Add_Description_Activity.this, SweetAlertDialog.PROGRESS_TYPE);
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
        else if (id == R.id.category) {
            Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.Add_Advertisment) {

            Intent intent = new Intent(getApplicationContext(), AddAdvertisementActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.add_items) {
            Intent intent = new Intent(getApplicationContext(), Add_Description_Activity.class);
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
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Add_Description_Activity.this);
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
                    Intent intent = new Intent(Add_Description_Activity.this,
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

    private void chooseDoc() {
        Intent intent = new Intent();
        intent.setType("text/html");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Document"), PICK_PDF_CODE);


    }


    private String formatDate(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}