package com.smiligence.etmtechAdmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmtechAdmin.bean.ItemReviewAndRatings;
import com.smiligence.etmtechAdmin.common.CommonMethods;

import java.util.ArrayList;

import static com.smiligence.etmtechAdmin.ViewOrderActivity.textViewEmail;
import static com.smiligence.etmtechAdmin.ViewOrderActivity.textViewUsername;
import static com.smiligence.etmtechAdmin.common.Constant.ITEM_RATING_REVIEW_TABLE;
import static com.smiligence.etmtechAdmin.common.Constant.LEGAL_DETAILS;
import static com.smiligence.etmtechAdmin.common.Constant.REQUIRED;
import static com.smiligence.etmtechAdmin.common.Constant.TITLE_LEGAL_DETAILS;

public class LegalDetailsUploadActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;
    static Menu menuNav;
    static View mHeaderView;
    EditText TandCText, privacypolicy, desclaimer,aboutUs;
    Button TermsConditionsButton, buttonPrivacyPolicy, b_Desclaimer,b_aboutUs;
    DatabaseReference legalRef;
TextView reviewCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_details_upload);
        TandCText = findViewById(R.id.TandCText);
        TermsConditionsButton = findViewById(R.id.b_TandC);
        privacypolicy = findViewById(R.id.privacypolicy);
        buttonPrivacyPolicy = findViewById(R.id.buttonPrivacyPolicy);
        desclaimer = findViewById(R.id.desclaimer);
        b_Desclaimer = findViewById(R.id.b_Desclaimer);
        aboutUs=findViewById(R.id.aboutus);
        b_aboutUs=findViewById(R.id.b_aboutus);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(TITLE_LEGAL_DETAILS);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(LegalDetailsUploadActivity.this);
        navigationView.setCheckedItem(R.id.legal);


        LinearLayout linearLayout = (LinearLayout) navigationView.getHeaderView(0);
        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);
        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);
        textViewUsername.setText(DashBoardActivity.roleName);
        textViewEmail.setText(DashBoardActivity.userName);

        reviewCount=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.reviewApproval));
        reviewCount.setGravity(Gravity.CENTER_VERTICAL);
        reviewCount.setTypeface(null, Typeface.BOLD);
        reviewCount.setTextColor(getResources().getColor(R.color.redColor));

        legalRef = CommonMethods.fetchFirebaseDatabaseReference(LEGAL_DETAILS);


        legalRef.child("Disclaimer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String data = (String) dataSnapshot.getValue();
                    desclaimer.setText(data);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        legalRef.child("TermsAndConditions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //  Legal legal=dataSnapshot.getChildren(Legal.class);
                    String data = (String) dataSnapshot.getValue();
                    TandCText.setText(data);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        legalRef.child("PrivacyPolicy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String data = (String) dataSnapshot.getValue();
                    privacypolicy.setText(data);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        legalRef.child("AboutUs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //  Legal legal=dataSnapshot.getChildren(Legal.class);
                    String data = (String) dataSnapshot.getValue();
                    aboutUs.setText(data);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        TermsConditionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String disclaimerString = TandCText.getText().toString().trim();

                if(TermsConditionsButton.getText().toString().equals("Clear")){
                    legalRef.child("TermsAndConditions").setValue("");
                    TermsConditionsButton.setText("Save");
                }else if(TermsConditionsButton.getText().toString().equals("Save")){
                    if (disclaimerString == null || "".equalsIgnoreCase(disclaimerString)) {
                        privacypolicy.setError(REQUIRED);
                        Toast.makeText(LegalDetailsUploadActivity.this, ""+REQUIRED, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (disclaimerString != null) {
                        legalRef.child("TermsAndConditions").setValue(disclaimerString);
                        TermsConditionsButton.setText("Clear");
                    }

                }
            }
        });

        buttonPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String disclaimerString = privacypolicy.getText().toString().trim();

                if(buttonPrivacyPolicy.getText().toString().equals("Clear")){
                    legalRef.child("PrivacyPolicy").setValue("");
                    buttonPrivacyPolicy.setText("Save");
                }else if(buttonPrivacyPolicy.getText().toString().equals("Save")){
                    if (disclaimerString == null || "".equalsIgnoreCase(disclaimerString)) {
                        privacypolicy.setError(REQUIRED);
                        Toast.makeText(LegalDetailsUploadActivity.this, ""+REQUIRED, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (disclaimerString != null) {
                        legalRef.child("PrivacyPolicy").setValue(disclaimerString);
                        buttonPrivacyPolicy.setText("Clear");
                    }

                }

            }
        });
        b_Desclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String disclaimerString = desclaimer.getText().toString().trim();

                if(b_Desclaimer.getText().toString().equals("Clear")){
                    legalRef.child("Disclaimer").setValue("");
                    b_Desclaimer.setText("Save");
                }else if(b_Desclaimer.getText().toString().equals("Save")){
                    if (disclaimerString == null || "".equalsIgnoreCase(disclaimerString)) {
                        desclaimer.setError(REQUIRED);
                        Toast.makeText(LegalDetailsUploadActivity.this, ""+REQUIRED, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (disclaimerString != null) {
                        legalRef.child("Disclaimer").setValue(disclaimerString);
                        b_Desclaimer.setText("Clear");
                    }

                }
            }
        });


        b_aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(b_aboutUs.getText().toString().equals("Clear")){
                    legalRef.child("AboutUs").setValue("");
                    b_aboutUs.setText("Save");
                }else if(b_aboutUs.getText().toString().equals("Save")){
                    if (aboutUs.getText().toString() == null || "".equalsIgnoreCase(aboutUs.getText().toString())) {
                        desclaimer.setError(REQUIRED);
                        Toast.makeText(LegalDetailsUploadActivity.this, ""+REQUIRED, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (aboutUs.getText().toString() != null) {
                        legalRef.child("AboutUs").setValue(aboutUs.getText().toString().trim());
                        b_aboutUs.setText("Clear");
                    }

                }

            }
        });
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
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
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(LegalDetailsUploadActivity.this);
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
                    Intent intent = new Intent(LegalDetailsUploadActivity.this,
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
                    navigationView.setCheckedItem(R.id.legal);
                }
            });
        } else if (id == R.id.contactdetails) {

            Intent intent = new Intent(getApplicationContext(), DeliverySettings.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }  else if (id == R.id.CustomerDetails) {
            Intent intent = new Intent(getApplicationContext(), CustomerDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.legal) {
            Intent intent = new Intent(getApplicationContext(), LegalDetailsUploadActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else  if(id==R.id.reviewApproval){
            Intent intent = new Intent(getApplicationContext(), ItemRatingReviewApprovalActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else  if(id==R.id.dashboard){
            Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

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