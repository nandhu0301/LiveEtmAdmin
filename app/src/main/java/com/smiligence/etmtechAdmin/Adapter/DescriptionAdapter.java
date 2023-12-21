package com.smiligence.etmtechAdmin.Adapter;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.smiligence.etmtechAdmin.common.Constant.BOOLEAN_FALSE;
import static com.smiligence.etmtechAdmin.common.Constant.BOOLEAN_TRUE;

public class DescriptionAdapter extends RecyclerView.Adapter<DescriptionAdapter.ImageViewHolder> {

    private Context mcontext;
    private List<ItemDetails> itemDetailsList;
    private DescriptionAdapter.OnItemClicklistener mlistener;


    public DescriptionAdapter(Context context, List<ItemDetails> itemDetails) {
        mcontext = context;
        itemDetailsList = itemDetails;
    }

    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(DescriptionAdapter.OnItemClicklistener listener) {
        mlistener = listener;
    }


    @NonNull
    @Override
    public DescriptionAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.description_view, parent, BOOLEAN_FALSE);
        DescriptionAdapter.ImageViewHolder imageViewHolder = new DescriptionAdapter.ImageViewHolder(v, mlistener);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final DescriptionAdapter.ImageViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        ItemDetails itemDetails = itemDetailsList.get(position);

        /*for (int i = 0; i < itemDetails.getCategoryDetailsArrayList().size(); i++) {
            if (itemDetails.getCategoryDetailsArrayList().size() == 1) {
                holder.item_catagory.append(itemDetails.getCategoryDetailsArrayList().get(i).getCategoryName() + ".");
            } else if (itemDetails.getCategoryDetailsArrayList().size() > 1) {
                holder.item_catagory.append(itemDetails.getCategoryDetailsArrayList().get(i).getCategoryName() + ",");
            }
        }*/

        holder.itemName.setText(itemDetails.getItemName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher_new);
        requestOptions.error(R.mipmap.ic_launcher_new);
        Glide.with(mcontext)
                .setDefaultRequestOptions(requestOptions)
                .load(itemDetails.getItemImage()).fitCenter().into(holder.itemImages);


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
        private TextView itemName, itemId, item_catagory;
        ImageView itemImages;

        public ImageViewHolder(@NonNull View itemView, final DescriptionAdapter.OnItemClicklistener itemClicklistener) {
            super(itemView);

            item_catagory = itemView.findViewById(R.id.text_itemcatagory);
            itemName = itemView.findViewById(R.id.text_itemname);
            itemImages = itemView.findViewById(R.id.Image_upload);



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


    private String formatDate(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
}