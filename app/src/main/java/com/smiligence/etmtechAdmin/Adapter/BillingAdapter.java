package com.smiligence.etmtechAdmin.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smiligence.etmtechAdmin.R;
import com.smiligence.etmtechAdmin.bean.ItemDetails;

import java.util.List;

import static com.smiligence.etmtechAdmin.common.Constant.BOOLEAN_TRUE;


public class BillingAdapter extends RecyclerView.Adapter<BillingAdapter.ImageViewHolder> {

    private Context mcontext;
    private List<ItemDetails> itemDetailsList;
    private OnItemClicklistener mlistener;

    public BillingAdapter(Context context, List<ItemDetails> itemDetails) {
        mcontext = context;
        itemDetailsList = itemDetails;

    }

    public void setOnItemclickListener(OnItemClicklistener listener) {
        mlistener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.item_grid_sales, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(v, mlistener);
        return imageViewHolder;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        //TODO Need to test
        // holder.setIsRecyclable(false)


        ItemDetails itemList = itemDetailsList.get(position);

         holder.itemName.setText(itemList.getItemName());
            holder.itemPrice.setText("₹" + String.valueOf(itemList.getItemPrice()));
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.ic_launcher_new);
            requestOptions.error(R.mipmap.ic_launcher_new);
            Glide.with(mcontext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(itemList.getItemImage()).into(holder.itemImages);





    }

    @Override
    public int getItemCount() {
        return itemDetailsList.size();
    }

    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImages;
        private TextView itemName, itemPrice, itemId;

        public ImageViewHolder(@NonNull View itemView, final OnItemClicklistener itemClicklistener) {
            super(itemView);

            itemName = itemView.findViewById(R.id.text_view_name);
            itemImages = itemView.findViewById(R.id.image_view_upload);
            itemPrice = itemView.findViewById(R.id.tprice);
            itemName.setSelected(BOOLEAN_TRUE);
            itemPrice.setSelected(BOOLEAN_TRUE);


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
