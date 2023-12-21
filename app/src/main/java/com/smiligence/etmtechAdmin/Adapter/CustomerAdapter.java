package com.smiligence.etmtechAdmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmtechAdmin.R;
import com.smiligence.etmtechAdmin.bean.CustomerDetails;
import com.smiligence.etmtechAdmin.bean.OrderDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ImageViewHolder> {
    private Context mcontext;
    private List<CustomerDetails> categoryDetailsList;
    private OnItemClicklistener mlistener;
    ImageViewHolder imageViewHolder;
    private int indicator;

    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(OnItemClicklistener listener) {
        mlistener = listener;
    }

    public CustomerAdapter(Context context, List<CustomerDetails> catagories) {
        mcontext = context;
        categoryDetailsList = catagories;
        this.indicator = indicator;
    }

    @NonNull

    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.customer_layout, parent, false);
        imageViewHolder = new ImageViewHolder(v, mlistener);
        return imageViewHolder;
    }


    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        holder.setIsRecyclable(false);
        CustomerDetails categoryDetails = categoryDetailsList.get(position);


            holder.catagoryName.setText(categoryDetails.getFullName());
            holder.customerPhonenumber.setText(categoryDetails.getCustomerPhoneNumber());
            holder.customerAddress.setText(categoryDetails.getEmailId());

            if(categoryDetailsList.get(position).getFullName()==null||categoryDetailsList.get(position).getFullName().isEmpty()){
                DatabaseReference databaseReference= CommonMethods.fetchFirebaseDatabaseReference("OrderDetails");
                databaseReference.orderByChild("customerId").equalTo(String.valueOf(categoryDetailsList.get(position).getCustomerId())).
                        addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                                OrderDetails orderDetails=dataSnapshot1.getValue(OrderDetails.class);
                                holder.catagoryName.setText(orderDetails.getCustomerName());
                            }

                           // holder.customerAddress.setText(orderDetails.getShippingaddress());
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

    }


    public int getItemCount() {
        return categoryDetailsList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView catagoryName,customerPhonenumber,customerAddress;
        ImageView itemImages;

        public ImageViewHolder(@NonNull View itemView, final OnItemClicklistener itemClicklistener) {
            super(itemView);
            catagoryName = itemView.findViewById(R.id.customer_name);
            customerPhonenumber=itemView.findViewById(R.id.customer_phonenumber);
            customerAddress=itemView.findViewById(R.id.customeraddress);

            catagoryName.setSelected(true);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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