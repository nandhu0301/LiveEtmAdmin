package com.smiligence.etmtechAdmin;

import static com.smiligence.etmtechAdmin.common.Constant.ADMIN_FARE;
import static com.smiligence.etmtechAdmin.common.Constant.SELLER_PAYMENTS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmtechAdmin.bean.SellerPaymentDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.NetworkStateReceiver;

public class MaintainDeliveryFairActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , NetworkStateReceiver.NetworkStateReceiverListener {


    Button addSellerPayments;
    TextView startingPayment,weeklyPayment;
    Spinner paymentSpinner;
    DatabaseReference sellerPaymentsDataRef;
    SellerPaymentDetails sellerPaymentDetails=new SellerPaymentDetails();
    CardView basicCard,silverCard,goldenCard;
    NavigationView navigationView;
    public static TextView textViewUsername;
    public static TextView textViewEmail;
    public static ImageView imageView;
    public static Menu menuNav;
    public static View mHeaderView;
    private NetworkStateReceiver networkStateReceiver;
    androidx.appcompat.app.AlertDialog alertDialog;
    ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_delivery_fair);


        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(ADMIN_FARE);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MaintainDeliveryFairActivity.this);
        navigationView.setCheckedItem(R.id.adminfare);


        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);


        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);




        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

        addSellerPayments=findViewById(R.id.addSellerPayments);
        weeklyPayment=findViewById(R.id.weeklypayment);
        startingPayment=findViewById(R.id.startingPayment);
        paymentSpinner=findViewById(R.id.typeOfSettlment);
        basicCard=findViewById(R.id.basicCard);
        silverCard=findViewById(R.id.silverCard);
        goldenCard=findViewById(R.id.goldenCard);

        sellerPaymentsDataRef= CommonMethods.fetchFirebaseDatabaseReference(SELLER_PAYMENTS);


        String payments[] = {"Select Payment Type","Basic","Silver","Gold"};


        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        payments); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        paymentSpinner.setAdapter(spinnerArrayAdapter);



        basicCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MaintainDeliveryFairActivity.this);
// ...Irrelevant code for customizing the buttons and title
                LayoutInflater inflater = MaintainDeliveryFairActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.payment_layout, null);
                dialogBuilder.setView(dialogView);

                RelativeLayout border=dialogView.findViewById(R.id.backGroundLayout);
                border.setBackgroundColor(getResources().getColor(R.color.face));
                TextView header=dialogView.findViewById(R.id.typeText);
                header.setText("Basic");
                EditText payPrice=dialogView.findViewById(R.id.startingPayment);
                EditText percentPrice=dialogView.findViewById(R.id.PercentagePayment);

                sellerPaymentsDataRef.child("Basic").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount()>0)
                        {
                            SellerPaymentDetails sellerPaymentDetailsConstant=dataSnapshot.getValue(SellerPaymentDetails.class);
                            payPrice.setText(String.valueOf(sellerPaymentDetailsConstant.getPayment()));
                            percentPrice.setText(String.valueOf(sellerPaymentDetailsConstant.getPercentage()));
                        }else
                        {
                            payPrice.setText(String.valueOf(0));
                            percentPrice.setText(String.valueOf(0));

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });
        silverCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MaintainDeliveryFairActivity.this);
                LayoutInflater inflater = MaintainDeliveryFairActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.payment_layout, null);
                dialogBuilder.setView(dialogView);

                RelativeLayout border=dialogView.findViewById(R.id.backGroundLayout);
                border.setBackgroundColor(getResources().getColor(R.color.silver));
                TextView header=dialogView.findViewById(R.id.typeText);
                header.setText("Silver");
                EditText payPrice=dialogView.findViewById(R.id.startingPayment);
                EditText percentPrice=dialogView.findViewById(R.id.PercentagePayment);

                sellerPaymentsDataRef.child("Silver").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount()>0)
                        {
                            SellerPaymentDetails sellerPaymentDetailsConstant=dataSnapshot.getValue(SellerPaymentDetails.class);
                            payPrice.setText(String.valueOf(sellerPaymentDetailsConstant.getPayment()));
                            percentPrice.setText(String.valueOf(sellerPaymentDetailsConstant.getPercentage()));
                        }else
                        {
                            payPrice.setText(String.valueOf(0));
                            percentPrice.setText(String.valueOf(0));

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });
        goldenCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MaintainDeliveryFairActivity.this);

                LayoutInflater inflater = MaintainDeliveryFairActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.payment_layout, null);
                dialogBuilder.setView(dialogView);

                RelativeLayout border=dialogView.findViewById(R.id.backGroundLayout);
                border.setBackgroundColor(getResources().getColor(R.color.gold));
                TextView header=dialogView.findViewById(R.id.typeText);
                header.setText("Gold");
                EditText payPrice=dialogView.findViewById(R.id.startingPayment);
                EditText percentPrice=dialogView.findViewById(R.id.PercentagePayment);
                sellerPaymentsDataRef.child("Gold").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount()>0)
                        {
                            SellerPaymentDetails sellerPaymentDetailsConstant=dataSnapshot.getValue(SellerPaymentDetails.class);
                            payPrice.setText(String.valueOf(sellerPaymentDetailsConstant.getPayment()));
                            percentPrice.setText(String.valueOf(sellerPaymentDetailsConstant.getPercentage()));
                        }else
                        {
                            payPrice.setText(String.valueOf(0));
                            percentPrice.setText(String.valueOf(0));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });


        addSellerPayments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int price;

                if (!"".equals(weeklyPayment.getText().toString()) && weeklyPayment.getText().toString() != null) {
                    price =Integer.parseInt(weeklyPayment.getText().toString());
                } else {
                    price = 0;
                }

                if (paymentSpinner.getSelectedItemPosition()==0)
                {
                    Toast.makeText(MaintainDeliveryFairActivity.this, "Please select payment type", Toast.LENGTH_SHORT).show();
                }else if (startingPayment.getText().toString().equals(""))
                {
                    startingPayment.setError("Required");
                }

                else if (startingPayment.getText().toString().startsWith("0"))
                {
                    startingPayment.setError("Price should not starts with (0)");
                    if (startingPayment.getText().toString().length() > 0)
                    {
                        startingPayment.setText(startingPayment.getText().toString().substring(1));
                        return;
                    }
                    else
                    {
                        startingPayment.setText("");
                        return;
                    }

                }



                else if (weeklyPayment.getText().toString().equals(""))
                {
                    weeklyPayment.setError("Required");
                }else if (price>100 || price==0)
                {
                    weeklyPayment.setError("Please enter valid percentage");
                }

                else if (weeklyPayment.getText().toString().startsWith("0"))
                {
                    weeklyPayment.setError("Price should not starts with (0)");
                    if (weeklyPayment.getText().toString().length() > 0)
                    {
                        weeklyPayment.setText(weeklyPayment.getText().toString().substring(1));
                        return;
                    }
                    else
                    {
                        weeklyPayment.setText("");
                        return;
                    }

                }
                else
                {

                    sellerPaymentDetails.setShareType(String.valueOf(paymentSpinner.getSelectedItem()));
                    sellerPaymentDetails.setPayment(Integer.parseInt(startingPayment.getText().toString()));
                    sellerPaymentDetails.setPercentage(Integer.parseInt(weeklyPayment.getText().toString()));
                    sellerPaymentsDataRef.child(String.valueOf(paymentSpinner.getSelectedItem())).setValue(sellerPaymentDetails);

                    paymentSpinner.setSelection(0);
                    startingPayment.setText("");
                    weeklyPayment.setText("");
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MaintainDeliveryFairActivity.this,DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
        else if (id == R.id.adminfare) {
            Intent intent = new Intent(getApplicationContext(), MaintainDeliveryFairActivity.class);
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


        else if (id == R.id.logoutscreen) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MaintainDeliveryFairActivity.this);
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
                    Intent intent = new Intent(MaintainDeliveryFairActivity.this,
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
        } else if (id == R.id.adminMonthlyPayments) {
            Intent intent = new Intent(getApplicationContext(), MonthlyPaymentSettlements.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        else if (id == R.id.ordertracking) {
            Intent intent = new Intent(getApplicationContext(), OrderTrackingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        else if (id == R.id.dashboard) {
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
    public void networkAvailable() {

    }

    @Override
    public void networkUnavailable() {

    }
}