package com.smiligence.etmtechAdmin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmtechAdmin.Adapter.Advertisementadapter;
import com.smiligence.etmtechAdmin.bean.AdvertisementDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.smiligence.etmtechAdmin.common.Constant.ADVERTISEMT_DETAILS_FIREBASE_TABLE;

public class ViewAdvertisementActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference advertisementRef;
    ArrayList<AdvertisementDetails> advertisementDetailsList=new ArrayList<>();
    Advertisementadapter advertisementAdapter;
    AdvertisementDetails advertisementDetails;
    ImageView backButton;
    TextView reviewCount;
    String pattern = "hh:mm aa";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    String currentTime;
    String currentDateAndTime;
    Date currentLocalTime;
    DateFormat date;
    public static String DATE_FORMAT = "MMMM dd, YYYY";
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_advertisement);

        backButton=findViewById(R.id.redirecttohome);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(ViewAdvertisementActivity.this, 1));
        recyclerView.setHasFixedSize(true);

        //fetch current date
        Calendar cal = Calendar.getInstance();
        currentLocalTime = cal.getTime();
        date = new SimpleDateFormat("HH:mm aa");
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        currentDateAndTime = dateFormat.format(new Date());
        currentTime = date.format(currentLocalTime);



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewAdvertisementActivity.this, AddAdvertisementActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        advertisementRef= CommonMethods.fetchFirebaseDatabaseReference(ADVERTISEMT_DETAILS_FIREBASE_TABLE);

        advertisementRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    advertisementDetailsList.clear();
                    for(DataSnapshot advertisementSnap:dataSnapshot.getChildren()){

                         advertisementDetails=advertisementSnap.getValue(AdvertisementDetails.class);
                         advertisementDetailsList.add(advertisementDetails);
                    }

                    if (advertisementDetailsList!=null) {
                        advertisementAdapter = new Advertisementadapter(ViewAdvertisementActivity.this, advertisementDetailsList);
                        advertisementAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(advertisementAdapter);
                    }
                    if (advertisementAdapter != null) {
                        advertisementAdapter.setOnItemclickListener(new Advertisementadapter.OnItemClicklistener() {
                            @Override
                            public void Onitemclick(int Position) {
                                AdvertisementDetails advertisementDetails=advertisementDetailsList.get(Position);

                                AlertDialog.Builder mBuilder = new AlertDialog.Builder ( ViewAdvertisementActivity.this );
                                View mView = getLayoutInflater ().inflate ( R.layout.advertisement_dialog, null );
                                TextView advPriority = mView.findViewById ( R.id.adv_priority );
                                TextView advAgents = mView.findViewById ( R.id.adv_agent );
                                TextView advDuration = mView.findViewById ( R.id.adv_duration );
                                TextView advPlacing = mView.findViewById ( R.id.adv_placing );
                                TextView advScheduledDate = mView.findViewById ( R.id.scheduleddate );
                                TextView advScheduledTime = mView.findViewById ( R.id.scheduledtime );
                                TextView adv_enddate = mView.findViewById ( R.id.adv_enddate );
                                ImageView Cancel=mView.findViewById(R.id.Canceldialog);


                                mBuilder.setView ( mView );
                                dialog = mBuilder.create ();
                                dialog.show ();
                                dialog.setCancelable ( true );

/*
                                activeInactiveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                        if (b) {
                                            activeInactiveSwitch.setChecked(true);
                                            DatabaseReference databaseReference = CommonMethods.fetchFirebaseDatabaseReference("Advertisements").child(advertisementDetails.getId());
                                            databaseReference.child("advertisementstatus").setValue("Active");
                                        } else {
                                            activeInactiveSwitch.setChecked(false);
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                    .getReference("Advertisements").child(advertisementDetails.getId());

                                            databaseReference.child("advertisementstatus").setValue("InActive");
                                        }
                                    }
                                });
*/

                                Cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                advPriority.setText(advertisementDetails.getAdvertisementpriority());
                                advAgents.setText(advertisementDetails.getAdvertisingAgent());
                                advDuration.setText(advertisementDetails.getAdvertisingDuration());
                                advPlacing.setText(advertisementDetails.getAdvertisementPlacing());
                                advScheduledDate.setText(advertisementDetails.getScheduledDate());
                                advScheduledTime.setText(advertisementDetails.getScheduledTime());
                                adv_enddate.setText(advertisementDetails.getAdvertisementEndingDurationDate());

                            }
                        });
                    }

                    /*if (advertisementAdapter != null) {
                        advertisementAdapter.setOnItemclickListener(new Advertisementadapter.OnItemClicklistener() {
                            @Override
                            public void Onitemclick(int Position) {
                                advertisementDetails=advertisementDetailsList.get(Position);


                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ViewAdvertisementActivity.this);
                                builder.setTitle("Confrimation Status")
                                        .setMessage("Are sure, want to Inactive the Banner ?")
                                        .setCancelable(false)
                                        .setPositiveButton("Inactive", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                advertisementRef.child(String.valueOf(advertisementDetails.getId())).child("advertisementstatus").setValue(INACTIVE_STATUS);
                                                Intent intent = new Intent(ViewAdvertisementActivity.this, ViewAdvertisementActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                startActivity(intent);
                                            }
                                        })
                                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).setNegativeButton("Active", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        advertisementRef.child(String.valueOf(advertisementDetails.getId())).child("advertisementstatus").setValue(ACTIVE_STATUS);
                                        Intent intent = new Intent(ViewAdvertisementActivity.this, ViewAdvertisementActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                    }
                                });
                                android.app.AlertDialog dialogBuiler = builder.create();
                                dialogBuiler.show();


                            }
                        });

                    }*/

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ViewAdvertisementActivity.this, DashBoardActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}