package com.smiligence.etmtechAdmin.Adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smiligence.etmtechAdmin.R;
import com.smiligence.etmtechAdmin.bean.ItemDetails;

import java.util.List;

public class StoreItemDetailsAdapter extends RecyclerView.Adapter<StoreItemDetailsAdapter.ImageViewHolder> {

    private Context mcontext;
    private List<ItemDetails> itemDetailsList;
    private OnItemClicklistener mlistener;


    public StoreItemDetailsAdapter(Context context, List<ItemDetails> itemDetails) {
        mcontext = context;
        itemDetailsList = itemDetails;

    }

    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(OnItemClicklistener listener) {
        mlistener = listener;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from ( mcontext ).inflate ( R.layout.item_view, parent, false );
        ImageViewHolder imageViewHolder = new ImageViewHolder ( v, mlistener );
        return imageViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {
        ItemDetails itemDetails = itemDetailsList.get ( position );


        holder.item_catagory.setText ( itemDetails.getCategoryName () );
        holder.itemName.setText ( itemDetails.getItemName ().toLowerCase () );
        holder.itemPrice.setText ( String.valueOf ( "₹" + itemDetails.getItemPrice () ) );
        RequestOptions requestOptions = new RequestOptions ();
        requestOptions.placeholder ( R.mipmap.ic_launcher_new );
        requestOptions.error ( R.mipmap.ic_launcher_new );
        Glide.with ( mcontext )
                .setDefaultRequestOptions ( requestOptions )
                .load ( itemDetails.getItemImage () ).fitCenter ().into ( holder.itemImages );
        /*if (itemDetails.getItemApprovalStatus ().equals ( "Waiting for approval" )) {
            holder.item_status.setTextColor ( mcontext.getColor ( R.color.colorAccent ) );
        } else if (itemDetails.getItemApprovalStatus ().equals ( "Approved" )) {
            holder.item_status.setTextColor ( mcontext.getColor ( R.color.main_green_color ) );
        } else if (itemDetails.getItemApprovalStatus ().equals ( "Declined" )) {
            holder.item_status.setTextColor ( mcontext.getColor ( R.color.red_btn_bg_color ) );
        }
        holder.item_status.setText ( itemDetails.getItemApprovalStatus () );*/
    }

    @Override
    public int getItemCount() {

        if (itemDetailsList == null) {
            return 0;
        } else {
            return itemDetailsList.size ();
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName, itemPrice, item_status, item_catagory;
        ImageView itemImages;
        // RelativeLayout relativeLayout;

        public ImageViewHolder(@NonNull View itemView, final OnItemClicklistener itemClicklistener) {
            super ( itemView );

            item_catagory = itemView.findViewById ( R.id.text_itemcatagory );
            itemName = itemView.findViewById ( R.id.text_itemname );
            itemImages = itemView.findViewById ( R.id.Image_upload );
            itemPrice = itemView.findViewById ( R.id.text_itemPrice );



            itemName.setSelected ( true );

            itemView.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    if (itemClicklistener != null) {
                        int Position = getAdapterPosition ();
                        if (Position != RecyclerView.NO_POSITION) {
                            itemClicklistener.Onitemclick ( Position );

                        }
                    }
                }
            } );
        }
    }
}
