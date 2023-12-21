package com.smiligence.etmtechAdmin;

import static com.smiligence.etmtechAdmin.common.MessageConstant.PRODUCT_DETAILS_TABLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmtechAdmin.Adapter.StoreItemDetailsAdapter;
import com.smiligence.etmtechAdmin.bean.ItemDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;

import java.util.ArrayList;

public class ViewStoreBasedItems extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<ItemDetails> itemDetailList = new ArrayList<ItemDetails> ();
    DatabaseReference itemDetailsRef;
    StoreItemDetailsAdapter storeListingAdapter;
    ImageView backIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_store_based_items);


        recyclerView = findViewById ( R.id.recycler_view );
        recyclerView.setHasFixedSize ( true );
        recyclerView.setLayoutManager ( new LinearLayoutManager( ViewStoreBasedItems.this ) );
        backIcon=findViewById(R.id.back);

        String getStoreID = getIntent ().getStringExtra ( "StoreId" );

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToHomeintent = new Intent ( ViewStoreBasedItems.this, StoreHistory.class );
                startActivity ( backToHomeintent );
            }
        });

        itemDetailsRef = CommonMethods.fetchFirebaseDatabaseReference ( PRODUCT_DETAILS_TABLE );
        Query fetchItem = itemDetailsRef.orderByChild ( "sellerId" ).equalTo ( getStoreID );
        fetchItem.addValueEventListener ( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount () >0)
                {
                    itemDetailList.clear ();

                    for ( DataSnapshot itemSnapshot : dataSnapshot.getChildren () )
                    {
                        ItemDetails itemDetails = itemSnapshot.getValue ( ItemDetails.class );
                        itemDetailList.add ( itemDetails );
                    }
                    if (itemDetailList.size () > 0)
                    {
                        storeListingAdapter = new StoreItemDetailsAdapter ( ViewStoreBasedItems.this, itemDetailList );
                        recyclerView.setAdapter ( storeListingAdapter );
                        storeListingAdapter.notifyDataSetChanged ();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,StoreHistory.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}