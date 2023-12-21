package com.smiligence.etmtechAdmin;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmtechAdmin.Adapter.BillingAdapter;
import com.smiligence.etmtechAdmin.Adapter.PaymentAdapter;
import com.smiligence.etmtechAdmin.bean.CustomerDetails;
import com.smiligence.etmtechAdmin.bean.ItemDetails;
import com.smiligence.etmtechAdmin.bean.OrderDetails;
import com.smiligence.etmtechAdmin.bean.ShippingAddress;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.Constant;
import com.smiligence.etmtechAdmin.common.DateUtils;
import com.smiligence.etmtechAdmin.common.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.graphics.Color.WHITE;
import static com.smiligence.etmtechAdmin.common.Constant.CUSTOMER_DETAILS_FIREBASE_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.DATE_FORMAT;
import static com.smiligence.etmtechAdmin.common.Constant.DATE_FORMAT_YYYYMD;
import static com.smiligence.etmtechAdmin.common.Constant.DATE_TIME_FORMAT;
import static com.smiligence.etmtechAdmin.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;
import static com.smiligence.etmtechAdmin.common.MessageConstant.PRODUCT_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.MessageConstant.REQUIRED_MSG;
import static com.smiligence.etmtechAdmin.common.NetworkDate.getTrueTime;
import static com.smiligence.etmtechAdmin.common.NetworkDate.initTrueTime;

public class BillingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {


    // TODO FREE OFFER need to be implemented and Tested

    String saved_username;
    TextView reviewCount;
    Button setDate, setTime, setDateoneweek;
    ListView itemListView;
    NavigationView navigationView;
    public static TextView textViewUsername;
    public static TextView textViewEmail;
    public static ImageView imageView;
    public static Menu menuNav;
    public static View mHeaderView;

    TextView dateText, hourText, datetextoneWeek;
    RecyclerView recycler_view;
    DatabaseReference itemDetailsRef;
    ArrayList<ItemDetails> itemDetailList = new ArrayList<ItemDetails>();
    BillingAdapter billingAdapter;
    SearchView salesSearchView;
    List<ItemDetails> search_items = new ArrayList<ItemDetails>();
    ItemDetails itemDetails = new ItemDetails();
    ArrayList<ItemDetails> purchasedItemList = new ArrayList<>();

    int counter = 0, selectedItemPosition;
    boolean isItemPresent = false;
    int discountAmount = 0, discountAmountIntent = 0, maximimDiscountAmount = 0;
    String discountName = "", discountType = "";
    String intentFrom;

    TextView ticketsTextView;
    BottomSheetDialog bottomSheetDialog;
    PaymentAdapter viewCartAdapter;

    TextView applyOffers;
    TextView discountNameTxt;
    TextView overAllAmount;
    TextView customerNameText;
    TextView itemtotalvalue;
    TextView discountAmount1;
    int finalTotalAmount = 0, discountedAmount = 0;

    RelativeLayout ticketsLayout;
    androidx.appcompat.app.AlertDialog addCustomerView;
    OrderDetails orderDetails = new OrderDetails();
    DatabaseReference orderDetailsRef;
    EditText customerNameEdt, customerPhoneNumberEdt, customerAddressEdt, customerPincodeEdt;
    boolean isCustomerDetailsPresent = false;
    String financialYear;
    CustomerDetails customerDetails;
    DatabaseReference customerLoginDetailsRef;
    long customerMaxId = 0;
    RelativeLayout chargeLayout;
    ListView customersListView;
    List<CustomerDetails> search_Users = new ArrayList<>();
    List<CustomerDetails> customerlist = new ArrayList<>();
    CustomerAdapterNew customerAdapter;
    long customercount = 0;
    DatabaseReference shippingAddressDataRef;
    Integer increamentId;
    String cashcardvalue = "";

    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();

        initTrueTime(this);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.billingScreen);


        navigationView.setNavigationItemSelectedListener(BillingActivity.this);

        LinearLayout linearLayout = (LinearLayout) navigationView.getHeaderView(0);

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
        dateText = findViewById(R.id.setdatetext);

        hourText = findViewById(R.id.hourText);
        chargeLayout = findViewById(R.id.chargeLayout);

        ticketsTextView = findViewById(R.id.Tickets);
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new GridLayoutManager(BillingActivity.this, 3));

        salesSearchView = findViewById(R.id.searchView);
        ticketsLayout = findViewById(R.id.ticketsLayout);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        salesSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        salesSearchView.setIconifiedByDefault(false);
        salesSearchView.setFocusable(false);
        salesSearchView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);
        salesSearchView.setOnCloseListener((SearchView.OnCloseListener) this);

        customerLoginDetailsRef = CommonMethods.fetchFirebaseDatabaseReference(CUSTOMER_DETAILS_FIREBASE_TABLE);
        shippingAddressDataRef = CommonMethods.fetchFirebaseDatabaseReference("ShippingAddress");


        salesSearchView.setBackgroundColor(WHITE);

        if (getIntent().getStringExtra("From") != null && !getIntent().getStringExtra("From").equals("")) {
            if (getIntent().getStringExtra("From").equals("ApplyOffers")) {
                purchasedItemList = (ArrayList<ItemDetails>) getIntent().getSerializableExtra("itemDetailsArrayList");
                discountAmountIntent = getIntent().getIntExtra("discountAmount", 0);
                discountName = getIntent().getStringExtra("discountName");
                maximimDiscountAmount = getIntent().getIntExtra("maximumDiscountAmount", 0);


                SharedPreferences loginSharedPreferences = getSharedPreferences("Address", MODE_PRIVATE);
                String customerName = loginSharedPreferences.getString("CustomerName", "");
                discountType = getIntent().getStringExtra("discountType");
                ticketsTextView.setText("Items Added (" + purchasedItemList.size() + ")");
                intentFrom = getIntent().getStringExtra("From");
                isCustomerDetailsPresent = getIntent().getExtras().getBoolean("isCustomerDetailsPresent");

                System.out.println("DiscountAmounth" + discountAmountIntent + "disocuntName" + discountName +
                        "maximumDiscountAmount" + maximimDiscountAmount + "discountType" + discountType);
                bottomDialogFunction(customerName);
            }
        } else {
            SharedPreferences myPrefs = getSharedPreferences("Address",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.clear();
            editor.commit();
        }


        SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        SharedPreferences.Editor loginEditor = loginSharedPreferences.edit();
        saved_username = loginSharedPreferences.getString("userNameStr", "");

        itemDetailsRef = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_TABLE);
        orderDetailsRef = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);

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
                        billingAdapter = new BillingAdapter(BillingActivity.this, itemDetailList);
                        billingAdapter.notifyDataSetChanged();
                        recycler_view.setAdapter(billingAdapter);

                    }

                    billingAdapter.setOnItemclickListener(
                            new BillingAdapter.OnItemClicklistener() {
                                @Override
                                public void Onitemclick(int Position) {

                                    onItemClickFunction(Position, 1);
                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ticketsLayout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                SharedPreferences loginSharedPreferences = getSharedPreferences("Address", MODE_PRIVATE);
                String customerName = loginSharedPreferences.getString("CustomerName", "");
                bottomDialogFunction(customerName);
            }
        });

        customerLoginDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customerMaxId = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        chargeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences loginSharedPreferences = getSharedPreferences("Address", MODE_PRIVATE);
                String customerName = loginSharedPreferences.getString("CustomerName", "");
                bottomDialogFunction(customerName);
                chargeFunction();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items_toolbar, menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.searchmenu) {
            salesSearchView = (SearchView) item.getActionView();
            salesSearchView.setIconified(false);
            salesSearchView.onActionViewExpanded();
            search_items.clear();
            salesSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        salesSearchView.setIconified(true);
                    }
                }
            });


            salesSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterData(String.valueOf(salesSearchView.getQuery()));

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    filterData(String.valueOf(salesSearchView.getQuery()));
                    return true;
                }
            });
            for (ItemDetails itemDetails : itemDetailList) {
                if (itemDetails.getItemName().toLowerCase().contains(String.valueOf(salesSearchView.getQuery()))) {
                    search_items.add(itemDetails);
                }
            }
            if (search_items.size() > 0) {
                billingAdapter = new BillingAdapter(BillingActivity.this, search_items);
                recycler_view.setAdapter(billingAdapter);
                billingAdapter.setOnItemclickListener(new BillingAdapter.OnItemClicklistener() {
                    @Override
                    public void Onitemclick(int Position) {
                        // onItemClickFunction(Position, 2);
                    }
                });
            }
        } else if (id == R.id.clearTicket) {
            purchasedItemList.clear();
            discountAmount = 0;
            discountedAmount = 0;
            discountAmountIntent = 0;
            finalTotalAmount = 0;
            discountName = "";
            maximimDiscountAmount = 0;
            bottomDialogFunction("");
            overAllAmount.setText("₹" + discountedAmount);
            discountAmount1.setText("₹ -" + discountAmount);
            ticketsTextView.setText("Items Added (" + purchasedItemList.size() + ")");

            customerNameText.setText("");
        } else if (id == R.id.customerIcon) {


            androidx.appcompat.app.AlertDialog.Builder dialogBuilderAddCustomer = new androidx.appcompat.app.AlertDialog.Builder(BillingActivity.this);
            LayoutInflater inflaterdialog = getLayoutInflater();
            final View dialogViewDialog = inflaterdialog.inflate(R.layout.add_search_customer, null);
            dialogBuilderAddCustomer.setView(dialogViewDialog);
            addCustomerView = dialogBuilderAddCustomer.create();
            addCustomerView.show();

            customersListView = dialogViewDialog.findViewById(R.id.customerslistview);
            ImageView cancelDialog = dialogViewDialog.findViewById(R.id.cancel_dialog);
            final SearchView search_customers = dialogViewDialog.findViewById(R.id.customersearch);
            TextView addNewCustomer = dialogViewDialog.findViewById(R.id.addNewCustomer);


            addNewCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomerDetails customerDetails = new CustomerDetails();

                    CallUpdateAndDeleteDialog(customerDetails, "NewUser", addCustomerView);

                    //CallUpdateAndDeleteDialog(customer);
                }
            });

            SearchManager searchManagerCustomers = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            search_customers.setSearchableInfo(searchManagerCustomers.getSearchableInfo(getComponentName()));
            search_customers.setIconifiedByDefault(false);

            search_customers.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    filterDataCustomers(String.valueOf(search_customers.getQuery()));
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {

                    filterDataCustomers(String.valueOf(search_customers.getQuery()));

                    return true;
                }

                public void filterDataCustomers(String query) {
                    query = query.toLowerCase();
                    search_Users.clear();

                    if (query.isEmpty()) {
                        search_Users.addAll(customerlist);
                    } else {
                        for (CustomerDetails Customer : customerlist) {
                            if (Customer.getCustomerPhoneNumber().contains(query)) {
                                search_Users.add(Customer);
                            }
                        }
                        if (search_Users.size() > 0) {
                            customerAdapter = new CustomerAdapterNew(BillingActivity.this, search_Users);
                            customersListView.setAdapter(customerAdapter);
                        }
                    }

                    if (customerAdapter != null) {
                        customerAdapter.notifyDataSetChanged();
                    }
                }
            });


            cancelDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addCustomerView.dismiss();
                }
            });


            customerLoginDetailsRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    customerlist.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        CustomerDetails User = postSnapshot.getValue(CustomerDetails.class);
                        customerlist.add(User);
                    }

                    //Collections.sort(customerlist);
                    CustomerAdapterNew UserAdapter = new CustomerAdapterNew(BillingActivity.this, customerlist);
                    customersListView.setAdapter(UserAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });


            customersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    CustomerDetails customer = (CustomerDetails) ((CustomerAdapterNew) adapterView.getAdapter()).getItem(i);
                    CallUpdateAndDeleteDialog(customer, "OldUser", addCustomerView);
                }
            });


        }
        return super.onOptionsItemSelected(item);

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
        }/*else if(id==R.id.deliveryfare){
            Intent intent = new Intent(getApplicationContext(), AddDeliveryFareActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }*/ else if (id == R.id.logoutscreen) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(BillingActivity.this);
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
                    Intent intent = new Intent(BillingActivity.this,
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
                    navigationView.setCheckedItem(R.id.billingScreen);
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
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        filterData(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        filterData(query);
        return false;
    }

    public void filterData(String query) {
        query = query.toLowerCase();
        search_items.clear();

        if (query.isEmpty()) {
            search_items.addAll(itemDetailList);
        } else {
            for (ItemDetails itemDetails : itemDetailList) {
                if (itemDetails.getItemName().toLowerCase().contains(query)) {
                    search_items.add(itemDetails);
                }
            }
        }

        if (billingAdapter != null) {
            billingAdapter.notifyDataSetChanged();
        }

    }

    private void CallUpdateAndDeleteDialog(final CustomerDetails customer, String userType, Dialog addCustomerView) {


        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.customer_details_layout, null);
        dialogBuilder.setView(dialogView);
        EditText fullName = dialogView.findViewById(R.id.shippingName);
        EditText phoneNumber = dialogView.findViewById(R.id.shippingMobileNumber);
        EditText houseNumber = dialogView.findViewById(R.id.shippingHousenumber);
        EditText roadName = dialogView.findViewById(R.id.ShippingArea);
        EditText city = dialogView.findViewById(R.id.ShippingCity);
        EditText state = dialogView.findViewById(R.id.ShippingState);
        EditText pincode = dialogView.findViewById(R.id.shippingpincode);
        Button addShippingAddress = dialogView.findViewById(R.id.addShippingAddress);
        ImageView iv_cancel = dialogView.findViewById(R.id.iv_cancel);


        final AlertDialog b = dialogBuilder.create();
        b.show();

        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.dismiss();

            }
        });

        if (userType.equals("OldUser")) {
            shippingAddressDataRef.child(customer.getCustomerId()).orderByChild("shippingId")
                    .equalTo(String.valueOf("1")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            ShippingAddress shippingAddress = dataSnapshot1.getValue(ShippingAddress.class);

                            if (!"".equals(shippingAddress.getFullName()) && shippingAddress.getFullName() != null) {
                                fullName.setText(shippingAddress.getFullName());
                            }
                            if (!"".equals(shippingAddress.getPhoneNumber()) && shippingAddress.getPhoneNumber() != null) {
                                phoneNumber.setText(shippingAddress.getPhoneNumber());
                            }

                            if (!"".equals(shippingAddress.getHouseAddress()) && shippingAddress.getHouseAddress() != null) {
                                houseNumber.setText(shippingAddress.getHouseAddress());
                            }

                            if (!"".equals(shippingAddress.getAreaAddress()) && shippingAddress.getAreaAddress() != null) {
                                roadName.setText(shippingAddress.getAreaAddress());
                            }

                            if (!"".equals(shippingAddress.getCity()) && shippingAddress.getCity() != null) {
                                city.setText(shippingAddress.getCity());
                            }

                            if (!"".equals(shippingAddress.getState()) && shippingAddress.getState() != null) {
                                state.setText(shippingAddress.getState());
                            }

                            if (!"".equals(shippingAddress.getPincode()) && shippingAddress.getPincode() != null) {
                                pincode.setText(shippingAddress.getPincode());
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            addShippingAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("".equals(fullName.getText().toString())) {
                        fullName.setError(REQUIRED_MSG);
                        Toast.makeText(getApplicationContext(), "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isNumeric(fullName.getText().toString()) == true) {
                        fullName.setError("Enter Valid Name");
                        Toast.makeText(getApplicationContext(), "Enter Valid Item Name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (phoneNumber.getText().toString().equals("")) {
                        phoneNumber.setError("Required");
                        return;
                    } else if (phoneNumber.getText().toString().trim() != null && !TextUtils.validatePhoneNumber(phoneNumber.getText().toString().trim())) {
                        phoneNumber.setError("Enter Valid Phone Number");
                        return;
                    } else {

                        ShippingAddress shippingAddressOne = new ShippingAddress();
                        shippingAddressOne.setFullName(fullName.getText().toString());
                        shippingAddressOne.setPhoneNumber(phoneNumber.getText().toString());
                        shippingAddressOne.setAlternatePhoneNumber(houseNumber.getText().toString());
                        shippingAddressOne.setHouseAddress(houseNumber.getText().toString());
                        shippingAddressOne.setAreaAddress(roadName.getText().toString());
                        shippingAddressOne.setCity(city.getText().toString());
                        shippingAddressOne.setState(state.getText().toString());
                        shippingAddressOne.setPincode(pincode.getText().toString());
                        shippingAddressOne.setLandMark("");
                        shippingAddressOne.setAddressType("Home Address");
                        shippingAddressOne.setFullAddress("");
                        shippingAddressOne.setShippingId("1");
                        shippingAddressDataRef.child(customer.getCustomerId()).child("1").setValue(shippingAddressOne);
                        b.dismiss();
                        addCustomerView.dismiss();
                        isCustomerDetailsPresent = true;
                        SharedPreferences sharedPreferences = getSharedPreferences("Address", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("CustomerId", customer.getCustomerId());
                        editor.putString("CustomerName", fullName.getText().toString());
                        editor.commit();
                        discountAmountIntent = 0;
                        discountName = "";
                        maximimDiscountAmount = 0;
                        bottomDialogFunction(fullName.getText().toString());
                    }

                }
            });

        } else {

            addShippingAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("".equals(fullName.getText().toString())) {
                        fullName.setError(REQUIRED_MSG);
                        Toast.makeText(getApplicationContext(), "" + REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isNumeric(fullName.getText().toString()) == true) {
                        fullName.setError("Enter Valid Name");
                        Toast.makeText(getApplicationContext(), "Enter Valid Item Name", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (phoneNumber.getText().toString().equals("")) {
                        phoneNumber.setError("Required");
                        return;
                    } else if (phoneNumber.getText().toString().trim() != null && !TextUtils.validatePhoneNumber(phoneNumber.getText().toString().trim())) {
                        phoneNumber.setError("Enter Valid Phone Number");
                        return;
                    } else {
                        Query query = customerLoginDetailsRef.orderByChild("customerPhoneNumber").equalTo(phoneNumber.getText().toString());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() > 0) {

                                    Toast.makeText(BillingActivity.this, "Customer Already exists. Any change updated", Toast.LENGTH_SHORT).show();

                                    for (DataSnapshot detailsSnap : dataSnapshot.getChildren()) {
                                        customerDetails = detailsSnap.getValue(CustomerDetails.class);
                                    }

                                    ShippingAddress shippingAddressOne = new ShippingAddress();
                                    shippingAddressOne.setFullName(fullName.getText().toString());
                                    shippingAddressOne.setPhoneNumber(phoneNumber.getText().toString());
                                    shippingAddressOne.setAlternatePhoneNumber(houseNumber.getText().toString());
                                    shippingAddressOne.setHouseAddress(houseNumber.getText().toString());
                                    shippingAddressOne.setAreaAddress(roadName.getText().toString());
                                    shippingAddressOne.setCity(city.getText().toString());
                                    shippingAddressOne.setState(state.getText().toString());
                                    shippingAddressOne.setPincode(pincode.getText().toString());
                                    shippingAddressOne.setLandMark("");
                                    shippingAddressOne.setAddressType("Home Address");
                                    shippingAddressOne.setFullAddress("");
                                    shippingAddressOne.setShippingId("1");
                                    shippingAddressDataRef.child(customerDetails.getCustomerId()).child("1").setValue(shippingAddressOne);
                                    b.dismiss();
                                    addCustomerView.dismiss();
                                    isCustomerDetailsPresent = true;
                                    SharedPreferences sharedPreferences = getSharedPreferences("Address", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("CustomerId", customerDetails.getCustomerId());
                                    editor.putString("CustomerName", fullName.getText().toString());
                                    editor.commit();


                                    discountAmountIntent = 0;
                                    discountName = "";
                                    maximimDiscountAmount = 0;
                                    bottomDialogFunction(fullName.getText().toString());
                                } else {
                                    CustomerDetails sellerLoginDetails = new CustomerDetails();
                                    String createdDate = DateUtils.fetchCurrentDateAndTime();
                                    sellerLoginDetails.setCustomerId(String.valueOf(customerMaxId + 1));
                                    sellerLoginDetails.setCustomerPhoneNumber(phoneNumber.getText().toString());
                                    sellerLoginDetails.setFullName(fullName.getText().toString());
                                    sellerLoginDetails.setCreationDate(createdDate);
                                    sellerLoginDetails.setSignIn(true);
                                    customerLoginDetailsRef.child(String.valueOf(customerMaxId + 1)).setValue(sellerLoginDetails);
                                    ShippingAddress shippingAddressOne = new ShippingAddress();
                                    shippingAddressOne.setFullName(fullName.getText().toString());
                                    shippingAddressOne.setPhoneNumber(phoneNumber.getText().toString());
                                    shippingAddressOne.setAlternatePhoneNumber(houseNumber.getText().toString());
                                    shippingAddressOne.setHouseAddress(houseNumber.getText().toString());
                                    shippingAddressOne.setAreaAddress(roadName.getText().toString());
                                    shippingAddressOne.setCity(city.getText().toString());
                                    shippingAddressOne.setState(state.getText().toString());
                                    shippingAddressOne.setPincode(pincode.getText().toString());
                                    shippingAddressOne.setLandMark("");
                                    shippingAddressOne.setAddressType("Home Address");
                                    shippingAddressOne.setFullAddress("");
                                    shippingAddressOne.setShippingId("1");
                                    shippingAddressDataRef.child(String.valueOf(customerMaxId + 1)).child("1").setValue(shippingAddressOne);
                                    b.dismiss();
                                    addCustomerView.dismiss();
                                    isCustomerDetailsPresent = true;
                                    SharedPreferences sharedPreferences = getSharedPreferences("Address", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("CustomerId", String.valueOf(customerMaxId + 1));
                                    editor.putString("CustomerName", fullName.getText().toString());
                                    editor.commit();
                                    discountAmountIntent = 0;
                                    discountName = "";
                                    maximimDiscountAmount = 0;
                                    bottomDialogFunction(fullName.getText().toString());
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


    }

    public void onItemClickFunction(int Position, int ref) {

        if (ref == 1) {
            itemDetails = itemDetailList.get(Position);
        } else {
            itemDetails = search_items.get(Position);
        }


        androidx.appcompat.app.AlertDialog.Builder dialogBuilder =
                new androidx.appcompat.app.AlertDialog.Builder(BillingActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.qty_dialog_layout, null);
        dialogBuilder.setView(dialogView);
        final ImageView increaseQty = (ImageView) dialogView.findViewById(R.id.increase_qty);
        final ImageView decreaseQty = (ImageView) dialogView.findViewById(R.id.decrease_qty);
        final TextView qtyText = dialogView.findViewById(R.id.qty_text);
        TextView okButton = dialogView.findViewById(R.id.okbutton);


        final androidx.appcompat.app.AlertDialog b = dialogBuilder.create();
        b.show();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(b.getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 0.49f);
        int dialogWindowHeight = (int) (displayHeight * 0.15f);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        b.getWindow().setAttributes(layoutParams);


        if (!purchasedItemList.isEmpty()) {
            for (int j = 0; j < purchasedItemList.size(); j++) {
                if (itemDetails.getItemName().equalsIgnoreCase(String.valueOf(purchasedItemList.get(j).getItemName()))) {
                    counter = purchasedItemList.get(j).getItemBuyQuantity();
                    qtyText.setText(String.valueOf(counter));
                    selectedItemPosition = j;
                    isItemPresent = true;
                    break;
                } else {
                    counter = 1;
                    qtyText.setText(String.valueOf(counter));
                    isItemPresent = false;
                }
            }
        }

        if (purchasedItemList.isEmpty()) {
            counter = 1;
            qtyText.setText(String.valueOf(counter));
        }

        increaseQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemDetailList != null && !itemDetailList.isEmpty()) {
                    Iterator itemIterator = itemDetailList.iterator();
                    int totalAmount = 0;
                    while (itemIterator.hasNext()) {
                        ItemDetails item = (ItemDetails) itemIterator.next();
                        totalAmount = totalAmount + item.getTotalItemQtyPrice();
                    }
                }

                counter = counter + 1;
                qtyText.setText(String.valueOf(counter));


                if (counter == 0) {
                    counter = 0;
                    b.dismiss();
                }
            }
        });


        decreaseQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (purchasedItemList != null && !purchasedItemList.isEmpty()) {


                    Iterator itemIterator = purchasedItemList.iterator();
                    int totalAmount = 0;
                    while (itemIterator.hasNext()) {

                        ItemDetails item = (ItemDetails) itemIterator.next();
                        totalAmount = totalAmount + item.getTotalItemQtyPrice();

                    }
                }

                counter = counter - 1;
                qtyText.setText(String.valueOf(counter));


                if (counter == 0 || counter < 0) {
                    counter = 0;
                    b.dismiss();
                }
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentFrom = "";
                discountAmount = 0;
                discountAmountIntent = 0;
                maximimDiscountAmount = 0;
                discountName = "";

                if (purchasedItemList.size() > 0 && purchasedItemList != null) {
                    if (isItemPresent) {

                        purchasedItemList.remove(selectedItemPosition);
                    }
                }


                ItemDetails purchaseDetails = new ItemDetails();

                purchaseDetails.setBasePrice(itemDetails.getBasePrice());
                // purchaseDetails.setCategoryDetailsArrayList(itemDetails.getCategoryDetailsArrayList());
                purchaseDetails.setCategoryId(itemDetails.getCategoryId());
                purchaseDetails.setCategoryName(itemDetails.getCategoryName());
                purchaseDetails.setCreateDate(itemDetails.getCreateDate());

                purchaseDetails.setInclusiveOfTax(itemDetails.getInclusiveOfTax());

                purchaseDetails.setItemAvailableQuantity(itemDetails.getItemAvailableQuantity());
                purchaseDetails.setItemBuyQuantity(counter);
                purchaseDetails.setItemCounter(0);
                purchaseDetails.setItemDescription(itemDetails.getItemDescription());
                purchaseDetails.setItemFeatures(itemDetails.getItemFeatures());
                purchaseDetails.setItemId(itemDetails.getItemId());
                purchaseDetails.setItemImage(itemDetails.getItemImage());
                purchaseDetails.setItemMaxLimitation(itemDetails.getItemMaxLimitation());
                purchaseDetails.setItemMinLimitation(itemDetails.getItemMinLimitation());
                purchaseDetails.setItemName(itemDetails.getItemName());
                purchaseDetails.setItemPrice(itemDetails.getItemPrice());
                purchaseDetails.setItemQuantity(itemDetails.getItemQuantity());
                purchaseDetails.setItemStatus(itemDetails.getItemStatus());
                purchaseDetails.setMRP_Price(itemDetails.getMRP_Price());
                purchaseDetails.setStoreLogo(itemDetails.getStoreLogo());
                purchaseDetails.setSubCategoryName(itemDetails.getSubCategoryName());
                purchaseDetails.setTax(itemDetails.getTaxPrice());
                purchaseDetails.setTaxPrice((itemDetails.getTaxPrice() * counter));
                //  purchaseDetails.setHSNCode(itemDetails.getHSNCode());
                purchaseDetails.setImageUriList(itemDetails.getImageUriList());
                purchaseDetails.setTotalItemQtyPrice((counter * itemDetails.getItemPrice()));
                purchaseDetails.setTotalTaxPrice((itemDetails.getTaxPrice() * counter));

                purchaseDetails.setWishList(itemDetails.getWishList());
                //purchaseDetails.setIngredientsList(itemDetails.getIngredientsList());


                purchasedItemList.add(purchaseDetails);
                b.dismiss();
                ticketsTextView.setText("Items Added (" + purchasedItemList.size() + ")");

            }
        });
    }


    public void bottomDialogFunction(String customerName) {


        bottomSheetDialog = new BottomSheetDialog(BillingActivity.this);
        bottomSheetDialog.setContentView(R.layout.tickets_layout);
        itemListView = bottomSheetDialog.findViewById(R.id.purchaselist);
        //itemListView.setNestedScrollingEnabled(true);

        itemtotalvalue = bottomSheetDialog.findViewById(R.id.itemtotalvalue);
        applyOffers = bottomSheetDialog.findViewById(R.id.viewOffers);
        discountNameTxt = bottomSheetDialog.findViewById(R.id.discountTextview);
        overAllAmount = bottomSheetDialog.findViewById(R.id.topayvalue);
        customerNameText = bottomSheetDialog.findViewById(R.id.customerName);

        if (!(customerName == null)) {
            customerNameText.setText(Html.fromHtml("<font color='#0141DF'><center><B>Customer Name: </B></center></font>") + customerName);
        } else {
            customerNameText.setText(Html.fromHtml(""));
        }

        discountAmount1 = bottomSheetDialog.findViewById(R.id.deductionAmount);

        RelativeLayout charge = bottomSheetDialog.findViewById(R.id.chargeBottomLayout);


        charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chargeFunction();
            }
        });

        discountNameTxt.setText(discountName);

        final int[] totalAmount = {0};
        // bottomSheetDialog.getEdgeToEdgeEnabled();
        bottomSheetDialog.show();

        viewCartAdapter = new PaymentAdapter(BillingActivity.this, purchasedItemList);
        itemListView.setAdapter(viewCartAdapter);
        viewCartAdapter.notifyDataSetChanged();
        ticketsTextView.setText("Items Added (" + purchasedItemList.size() + ")");

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {

                final ItemDetails itemDetails = purchasedItemList.get(position);


                final int reduceitemTotalPrice = itemDetails.getTotalItemQtyPrice();
                final String delete_itemName = itemDetails.getItemName();


                androidx.appcompat.app.AlertDialog.Builder dialogBuilder =
                        new androidx.appcompat.app.AlertDialog.Builder(BillingActivity.this);

                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.deletecartitem_dialog, null);
                dialogBuilder.setView(dialogView);

                final TextView itemname = dialogView.findViewById(R.id.updateTextname);
                final TextView Price = dialogView.findViewById(R.id.updateTextemail);

                String upperString = delete_itemName;
                String delete_itemNameString = upperString.substring(0, 1).toUpperCase() + upperString.substring(1).toLowerCase();

                itemname.setText("Item name : " + delete_itemNameString);
                Price.setText("Item price  : ₹ " + reduceitemTotalPrice);

                final Button buttonDelete = dialogView.findViewById(R.id.buttonDeleteUser);
                final ImageView buttonCancel = dialogView.findViewById(R.id.cancelButton);


                final androidx.appcompat.app.AlertDialog b = dialogBuilder.create();
                b.show();

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        b.dismiss();
                    }
                });

                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //reset values
                        discountName = "";
                        discountAmount = 0;
                        discountAmountIntent = 0;
                        maximimDiscountAmount = 0;
                        discountNameTxt.setText("");
                        discountAmount1.setText("");

                        purchasedItemList.remove(itemDetails);
                        viewCartAdapter.notifyDataSetChanged();
                        itemListView.setAdapter(viewCartAdapter);

                        Iterator itemListIterator = purchasedItemList.iterator();
                        int amountAfterItemDelete = 0;

                        while (itemListIterator.hasNext()) {
                            ItemDetails item = (ItemDetails) itemListIterator.next();
                            amountAfterItemDelete = amountAfterItemDelete + item.getTotalItemQtyPrice();
                        }
                        itemtotalvalue.setText("₹ " + amountAfterItemDelete);
                        overAllAmount.setText("₹ " + amountAfterItemDelete);
                        discountAmount1.setText("₹ -" + 0);
                        finalTotalAmount = amountAfterItemDelete;
                        discountedAmount = amountAfterItemDelete;
                        if (viewCartAdapter != null) {
                            int totalHeight = 0;

                            for (int i = 0; i < viewCartAdapter.getCount(); i++) {
                                totalAmount[0] = totalAmount[0] + purchasedItemList.get(i).getTotalItemQtyPrice();
                                View listItem = viewCartAdapter.getView(i, null, itemListView);
                                listItem.measure(0, 0);
                                totalHeight += listItem.getMeasuredHeight();
                            }
                            ViewGroup.LayoutParams params = itemListView.getLayoutParams();
                            params.height = totalHeight + (itemListView.getDividerHeight() * (viewCartAdapter.getCount() - 1));
                            itemListView.setLayoutParams(params);
                            itemListView.requestLayout();
                            itemListView.setAdapter(viewCartAdapter);
                            viewCartAdapter.notifyDataSetChanged();
                        }
                        b.dismiss();
                        ticketsTextView.setText("Items Added (" + purchasedItemList.size() + ")");
                    }
                });
            }
        });

        if (viewCartAdapter != null) {
            int totalHeight = 0;

            for (int i = 0; i < viewCartAdapter.getCount(); i++) {
                totalAmount[0] = totalAmount[0] + purchasedItemList.get(i).getTotalItemQtyPrice();
                View listItem = viewCartAdapter.getView(i, null, itemListView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            itemtotalvalue.setText("₹" + String.valueOf(totalAmount[0]));
            ViewGroup.LayoutParams params = itemListView.getLayoutParams();
            params.height = totalHeight + (itemListView.getDividerHeight() * (viewCartAdapter.getCount() - 1));
            itemListView.setLayoutParams(params);
            itemListView.requestLayout();
            itemListView.setAdapter(viewCartAdapter);
            viewCartAdapter.notifyDataSetChanged();

            //todo

            System.out.println("DISOCUT" + discountType + discountAmountIntent);
            if (discountType != null && !discountType.equals("")) {

                if (discountType.equals("Price")) {
                    discountedAmount = totalAmount[0] - discountAmountIntent;
                    discountAmount = discountAmountIntent;
                } else if (discountType.equals("Percent")) {
                    int calculatePercentValue = (totalAmount[0] * discountAmountIntent / 100);
                    if (calculatePercentValue < maximimDiscountAmount) {
                        discountAmount = calculatePercentValue;
                    } else {
                        discountAmount = maximimDiscountAmount;
                    }
                    discountedAmount = totalAmount[0] - discountAmount;
                }
            } else {
                discountedAmount = totalAmount[0];
            }


            overAllAmount.setText("₹" + discountedAmount);
            discountAmount1.setText("₹ -" + discountAmount);
            finalTotalAmount = totalAmount[0];


        }
        applyOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("purchasedItemList" + purchasedItemList.size());
                if (isCustomerDetailsPresent) {
                    Intent intent = new Intent(BillingActivity.this, OffersActivity.class);
                    intent.putExtra("BillAmount", String.valueOf(finalTotalAmount));

                    // Bundle bundle = new Bundle();
                    // bundle.putSerializable("itemDetailsArrayList", purchasedItemList);
                    //  intent.putExtras(bundle);

                    intent.putExtra("itemDetailsArrayList", purchasedItemList);
                    intent.putExtra("discountName", discountName);
                    intent.putExtra("discountType", discountType);
                    intent.putExtra("discountAmount", discountAmountIntent);
                    intent.putExtra("maximumDiscountAmount", maximimDiscountAmount);

                    if (isCustomerDetailsPresent) {
                        intent.putExtra("isCustomerDetailsPresent", true);
                    } else {
                        intent.putExtra("isCustomerDetailsPresent", false);
                    }


                    startActivity(intent);
                } else {
                    Toast.makeText(BillingActivity.this, "Please select customer to view exclusive offers", Toast.LENGTH_SHORT).show();
                }


            }


        });

    }

    public void chargeFunction() {
        if (purchasedItemList.size() == 0) {

            Toast.makeText(BillingActivity.this, "Please select the items", Toast.LENGTH_SHORT).show();

        }else {
            if (!isCustomerDetailsPresent) {
                Toast.makeText(this, "Please Add Customer", Toast.LENGTH_SHORT).show();
                return;
            } else {

                androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(BillingActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.chargebill, null);
                dialogBuilder.setView(dialogView);


                final TextView totrec = (TextView) dialogView.findViewById(R.id.textView35);
                RadioGroup cash_card = dialogView.findViewById(R.id.cash_card);
                totrec.setText("FINAL BILL AMOUNT  ₹ " + discountedAmount);


                Button ok = (Button) dialogView.findViewById(R.id.button9);
                Button cancel = (Button) dialogView.findViewById(R.id.button10);

                androidx.appcompat.app.AlertDialog bottomSheetDialog = dialogBuilder.create();
                bottomSheetDialog.show();

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        cashcardvalue = "";
                    }
                });


                cash_card.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        checkedId = cash_card.getCheckedRadioButtonId();

                        RadioButton radioButton = (RadioButton) dialogView.findViewById(checkedId);
                        cashcardvalue = radioButton.getText().toString();
                    }
                });


                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if ((cashcardvalue == null || "".equalsIgnoreCase(cashcardvalue))) {

                            Toast.makeText(BillingActivity.this, "Please select the payment mode ", Toast.LENGTH_SHORT).show();
                            return;

                        } else {
                            uploadOrderFucntion(cashcardvalue);
                        }
                    }
                });

                //uploadOrderFucntion();
            }
        }


    }

    public void uploadOrderFucntion(String cashcardvalue) {

        pDialog = new SweetAlertDialog(BillingActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#1898ED"));
        pDialog.setTitleText("Loading.....");
        pDialog.setCancelable(false);
        pDialog.show();
        SharedPreferences addressSharedPreferences = getSharedPreferences("Address", MODE_PRIVATE);
        String customerID = addressSharedPreferences.getString("CustomerId", "");

        shippingAddressDataRef.child(customerID).orderByChild("shippingId")
                .equalTo(String.valueOf("1")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        ShippingAddress shippingAddress = dataSnapshot1.getValue(ShippingAddress.class);

                        String address = (shippingAddress.getHouseAddress() + "," + shippingAddress.getAreaAddress() + "," +
                                shippingAddress.getCity() + "," + shippingAddress.getState() + "," + shippingAddress.getPincode());


                        SharedPreferences loginSharedPreferences = getSharedPreferences("CustomerID", MODE_PRIVATE);
                        String customerId = loginSharedPreferences.getString("ID", "");

                        orderDetails.setAssignedTo("");
                        orderDetails.setCategoryTypeId("1");
                        orderDetails.setNotificationStatus("true");
                        orderDetails.setNotificationStatusForCustomer("true");
                        orderDetails.setNotificationStatusForSeller("true");
                        orderDetails.setTipAmount(0);
                        orderDetails.setTotalDistanceTraveled(0);
                        orderDetails.setTotalItem(0);
                        orderDetails.setOrderStatus("Shipped");
                        orderDetails.setOrderType("Retail");
                        orderDetails.setDeliveryFee(0);
                        //need to get server time
                        Date d = new Date(getTrueTime().toString());

                        SimpleDateFormat formattedBillDate = new SimpleDateFormat(DATE_FORMAT_YYYYMD);
                        SimpleDateFormat orderTime = new SimpleDateFormat("HH:mm:ss");
                        SimpleDateFormat orderCreatedDate = new SimpleDateFormat(DATE_TIME_FORMAT);
                        SimpleDateFormat paymentDate = new SimpleDateFormat(DATE_FORMAT);
                        SimpleDateFormat yearSD = new SimpleDateFormat("yyyy");
                        SimpleDateFormat monthSD = new SimpleDateFormat("M");

                        int year = Integer.parseInt(yearSD.format(d));
                        int month = Integer.parseInt(monthSD.format(d));

                        if (month <= 3) {
                            financialYear = ((year - 1) + "-" + year);
                        } else {
                            financialYear = (year + "-" + (year + 1));
                        }

                        orderDetails.setOrderNumberForFinancialYearCalculation(Constant.Order_format_start + financialYear + Constant.Order_format_end);
                        orderDetails.setFormattedDate(formattedBillDate.format(d));
                        orderDetails.setOrderTime(orderTime.format(d));
                        orderDetails.setOrderCreateDate(orderCreatedDate.format(d));
                        orderDetails.setPaymentDate(paymentDate.format(d));
                        orderDetails.setOrderIdfromPaymentGateway("");
                        orderDetails.setPaymentId("");
                        orderDetails.setPaymentType(cashcardvalue);


                        //set and get
                        orderDetails.setCustomerId(customerId);
                        orderDetails.setCustomerName(shippingAddress.getFullName());
                        orderDetails.setCustomerPhoneNumber(shippingAddress.getPhoneNumber());
                        orderDetails.setDiscountAmount(discountAmount);
                        orderDetails.setDiscountName(discountName);
                        orderDetails.setFullName(shippingAddress.getFullName());
                        orderDetails.setItemDetailList(purchasedItemList);
                        orderDetails.setPaymentamount(discountedAmount);
                        orderDetails.setShippingPincode(shippingAddress.getPincode());
                        orderDetails.setShippingaddress(address);
                        orderDetails.setTotalAmount(finalTotalAmount);
                        onTransaction(orderDetailsRef);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void onTransaction(DatabaseReference postRef) {

        postRef.runTransaction(new Transaction.Handler() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                increamentId = Math.toIntExact(mutableData.getChildrenCount());
                if (increamentId == null) {
                    orderDetails.setOrderId(String.valueOf(1));
                    mutableData.child(String.valueOf(1)).setValue(orderDetails);
                    return Transaction.success(mutableData);
                } else {
                    increamentId = increamentId + 1;
                    orderDetails.setOrderId(String.valueOf(increamentId));
                    mutableData.child(String.valueOf(increamentId)).setValue(orderDetails);
                    return Transaction.success(mutableData);
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                SweetAlertDialog errorDialog1 = new SweetAlertDialog(BillingActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                errorDialog1.setCancelable(false);

                if (!(BillingActivity.this).isFinishing()) {
                    pDialog.dismiss();
                    errorDialog1.setContentText("Order Confirmed").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            errorDialog1.dismiss();
                            SharedPreferences myPrefs = getSharedPreferences("Address",
                                    MODE_PRIVATE);
                            SharedPreferences.Editor editor = myPrefs.edit();
                            editor.clear();
                            editor.commit();
                            Intent intent = new Intent(BillingActivity.this, BillingActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }).show();
                }
            }
        });
    }
}