package com.smiligence.etmtechAdmin.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smiligence.etmtechAdmin.R;
import com.smiligence.etmtechAdmin.bean.ItemDetails;

import java.util.List;

import static com.smiligence.etmtechAdmin.common.Constant.BOOLEAN_FALSE;
import static com.smiligence.etmtechAdmin.common.Constant.BOOLEAN_TRUE;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ImageViewHolder> {

    private Context mcontext;
    private List<ItemDetails> itemDetailsList;
    private OnItemClicklistener mlistener;


    public ItemAdapter(Context context, List<ItemDetails> itemDetails) {
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
        View v = LayoutInflater.from(mcontext).inflate(R.layout.itemview,parent, BOOLEAN_FALSE);
        ImageViewHolder imageViewHolder = new ImageViewHolder(v, mlistener);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        ItemDetails itemDetails = itemDetailsList.get(position);

        for (int i = 0; i < itemDetails.getCategoryDetailsArrayList().size(); i++) {

            if (itemDetails.getCategoryDetailsArrayList().size() == 1) {
                holder.item_catagory.append(itemDetails.getCategoryDetailsArrayList().get(i).getCategoryName() + ".");
            } else if(itemDetails.getCategoryDetailsArrayList().size() > 1 ){
                holder.item_catagory.append(itemDetails.getCategoryDetailsArrayList().get(i).getCategoryName() + ",");
            }
        }



        holder.itemPrice.setText(String.valueOf("Fixed Price"+" ₹" + itemDetails.getItemPrice()));
        holder.MRP_Price.setText(String.valueOf("₹" + itemDetails.getMRP_Price()));
        holder.itemName.setText(itemDetails.getItemName());
       holder.MRP_Price.setPaintFlags(holder.MRP_Price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher_new);
        requestOptions.error(R.mipmap.ic_launcher_new);
        Glide.with(mcontext)
                .setDefaultRequestOptions(requestOptions)
                .load(itemDetails.getItemImage()).fitCenter().into(holder.itemImages);

        if (!itemDetails.getItemStatus().equals("Available")) {
            holder.relativeLayout.setBackgroundColor(Color.GRAY);
        }





    }



    @Override
    public int getItemCount() {

        if (itemDetailsList == null) {
            return 0;
        } else {
            return itemDetailsList.size();
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName, itemPrice, itemId, item_catagory,MRP_Price;
        ImageView itemImages;
        RelativeLayout relativeLayout;


        public ImageViewHolder(@NonNull View itemView, final OnItemClicklistener itemClicklistener) {
            super(itemView);

            item_catagory = itemView.findViewById(R.id.text_itemcatagory);
            itemName = itemView.findViewById(R.id.text_itemname);
            itemImages = itemView.findViewById(R.id.Image_upload);
            itemPrice = itemView.findViewById(R.id.text_itemPrice);
            MRP_Price=itemView.findViewById(R.id.mrpPrice);
            relativeLayout=itemView.findViewById(R.id.ItemViewLayout);


            itemName.setSelected(BOOLEAN_TRUE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClicklistener != null) {
                        int Position = getAdapterPosition();
                        if (Position != RecyclerView.NO_POSITION) {
                            itemClicklistener.Onitemclick(Position);

                        }
                    }
                }
            });
        }
    }
}
