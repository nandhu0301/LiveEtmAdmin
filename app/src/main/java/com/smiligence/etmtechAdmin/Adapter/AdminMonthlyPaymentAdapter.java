package com.smiligence.etmtechAdmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smiligence.etmtechAdmin.R;
import com.smiligence.etmtechAdmin.bean.AdminRevenueDetails;

import java.util.List;

public class AdminMonthlyPaymentAdapter   extends RecyclerView.Adapter<AdminMonthlyPaymentAdapter.PaymentViewHolder>{
    private Context mcontext;
    private List<AdminRevenueDetails> itemDetailsList;

    public AdminMonthlyPaymentAdapter(Context context, List<AdminRevenueDetails> itemListŇew){
       this.itemDetailsList = itemListŇew;
       this.mcontext = context;
    }


    @NonNull
    @Override
    public AdminMonthlyPaymentAdapter.PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout

        View photoView = inflater.inflate(R.layout.admin_details_card, parent, false);

        PaymentViewHolder viewHolder = new PaymentViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminMonthlyPaymentAdapter.PaymentViewHolder holder, int position) {
        AdminRevenueDetails itemDetails = itemDetailsList.get ( position );
        holder.startDate.setText(itemDetails.getStartDate());
        holder.endDate.setText(itemDetails.getEndDate());
        holder.revenueFromNewStore.setText(String.valueOf(itemDetails.getRevenueFromNewStore()));
        holder.revenueFromAdvertisemnetbanners.setText(String.valueOf(itemDetails.getRevenueFromAdvertingBanners()));
        holder.revenueFromProductDelivery.setText(String.valueOf(itemDetails.getRevenueFromProductDelivery()));
        holder.totalRevenueText.setText(String.valueOf(itemDetails.getTotalRevenueAmount()));
    }

    @Override
    public int getItemCount() {
        return itemDetailsList.size();
    }

    public class PaymentViewHolder extends RecyclerView.ViewHolder{
        TextView startDate, endDate, revenueFromProductDelivery,revenueFromNewStore,revenueFromAdvertisemnetbanners,totalRevenueText;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);

            startDate = itemView.findViewById(R.id.startDate);
            endDate = itemView.findViewById(R.id.endDate);
            revenueFromProductDelivery = itemView.findViewById(R.id.revenuefromproductdelivery);
            revenueFromNewStore = itemView.findViewById(R.id.revenueFromNewStore);
            totalRevenueText = itemView.findViewById(R.id.totalRevenueText);
            revenueFromAdvertisemnetbanners=itemView.findViewById(R.id.revenuefromadvertisingbanners);


        }
    }
}
