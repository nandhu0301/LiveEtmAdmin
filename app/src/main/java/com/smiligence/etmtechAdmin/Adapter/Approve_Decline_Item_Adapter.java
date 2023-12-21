package com.smiligence.etmtechAdmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.smiligence.etmtechAdmin.R;
import com.smiligence.etmtechAdmin.bean.ItemDetails;

import java.util.ArrayList;
import java.util.List;

public class Approve_Decline_Item_Adapter extends RecyclerView.Adapter<Approve_Decline_Item_Adapter.ImageViewHolder> {
    private Context mcontext;
    private List<ItemDetails> sellerDetailsList;
    ArrayList<String> arrayListUser = new ArrayList<>();
    ArrayList<String> arrayListUser1 = new ArrayList<>();
    private OnItemClicklistener mlistener;
    Boolean flag;
    private int lastPosition = -1;
    DatabaseReference itemDataRef;
    boolean check = false;

    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }


    public Approve_Decline_Item_Adapter(Context context, List<ItemDetails> userDetails, Boolean flag) {
        mcontext = context;
        sellerDetailsList = userDetails;
        this.flag = flag;
        notifyDataSetChanged ();
    }

    @NonNull

    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from ( mcontext ).inflate ( R.layout.approve_or_decline_layout, parent, false );
        ImageViewHolder imageViewHolder = new ImageViewHolder ( v, mlistener );
        return imageViewHolder;
    }

    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        final ItemDetails sellerUserDetails = sellerDetailsList.get ( position );


      /*  holder.itemNameAd.setText ( sellerUserDetails.getItemName () );
        holder.categoryNameAd.setText ( sellerUserDetails.getCategoryName () );
        holder.sellerNameAd.setText ( sellerUserDetails.getStoreName () );
        itemDataRef = FirebaseDatabase.getInstance ().getReference ().child ( "ProductDetails" );
        Query itemApprovalStatusQuery = itemDataRef.orderByChild ( "itemApprovalStatus" ).equalTo ( "Waiting for approval" );

        itemApprovalStatusQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()<=0)
                {
                    sellerDetailsList.clear();
                    Intent intent=new Intent(mcontext, ItemRatingReviewApprovalActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mcontext.startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.approveButton.setOnClickListener (v -> {
            if (!((Activity) mcontext).isFinishing ()) {


                new SweetAlertDialog( mcontext, SweetAlertDialog.SUCCESS_TYPE )
                        .setTitleText ( "Item Approved" )
                        .setConfirmText ( "Ok" )
                        .setConfirmClickListener (sDialog -> {
                            DatabaseReference approvedDbref = FirebaseDatabase.getInstance().getReference().child("ProductDetails").child(String.valueOf(sellerDetailsList.get(position).getItemId()));
                            approvedDbref.child("itemApprovalStatus").setValue("Approved");
                            sDialog.dismiss();
                            sellerDetailsList.remove(position);
                            if (position==0)
                            {
                                sellerDetailsList.clear();
                            }
                            notifyItemRemoved(position);

                        })
                        .show ();
            }
        });


        holder.declineButton.setOnClickListener (v -> new SweetAlertDialog( mcontext, SweetAlertDialog.SUCCESS_TYPE )
                .setTitleText ( "Item Declined" )
                .setConfirmText ( "Ok" )
                .setConfirmClickListener (sDialog -> {
                    DatabaseReference approvedDbref = FirebaseDatabase.getInstance().getReference().child("ProductDetails").child(String.valueOf(sellerDetailsList.get(position).getItemId()));
                    approvedDbref.child("itemApprovalStatus").setValue("Declined");
                    sDialog.dismiss();
                    setAnimation(holder.itemView, position);
                    sellerDetailsList.remove(position);
                    notifyItemRemoved(position);
                })
                .show ());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder ( R.mipmap.ic_launcher );
        requestOptions.error ( R.mipmap.ic_launcher );
        Glide.with ( mcontext )
                .setDefaultRequestOptions ( requestOptions )
                .load ( sellerUserDetails.getStoreLogo () ).fitCenter ().into ( holder.imageViewAd );*/


    }

    public int getItemCount() {
        return sellerDetailsList.size ();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameAd, categoryNameAd, sellerNameAd;
        ImageView imageViewAd;
        Button approveButton, declineButton;
        RatingBar ratingBar;



        public ImageViewHolder(@NonNull View itemView, final OnItemClicklistener itemClicklistener) {
            super ( itemView );
            itemNameAd = itemView.findViewById ( R.id.approveItemname );
            categoryNameAd = itemView.findViewById ( R.id.approveCategoryName );
            sellerNameAd = itemView.findViewById ( R.id.sellerName );
            imageViewAd = itemView.findViewById ( R.id.approveImage );
            approveButton = itemView.findViewById ( R.id.Approve );
            declineButton = itemView.findViewById ( R.id.Decline );
            ratingBar=itemView.findViewById(R.id.rating);


            itemView.setOnClickListener (v -> {
                if (itemClicklistener != null) {
                    int Position = getAdapterPosition ();
                    if (Position != RecyclerView.NO_POSITION) {
                        itemClicklistener.Onitemclick ( Position );
                    }
                }
            });


        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation ( mcontext, android.R.anim.slide_in_left );
            viewToAnimate.startAnimation ( animation );
            lastPosition = position;
        }
    }
}
