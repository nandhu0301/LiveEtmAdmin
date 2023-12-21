package com.smiligence.etmtechAdmin.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.smiligence.etmtechAdmin.R;
import com.smiligence.etmtechAdmin.bean.ItemDetails;

import java.util.ArrayList;
import java.util.List;

public class PaymentAdapter extends BaseAdapter {
    private Activity context;
    List<ItemDetails> itemDetailsList;
    ItemDetails itemDetails;
    private static LayoutInflater inflater = null;

    String saved_id;

    public PaymentAdapter(Activity context, ArrayList<ItemDetails> list) {
        this.context = context;
        this.itemDetailsList = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return itemDetailsList.size();
    }


    @Override
    public Object getItem(int position) {
        return itemDetailsList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    private class ViewHolder {
        ImageView itemImage;
        TextView itemName, itemqtyPrice, itemPrice;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listViewItem = convertView;

        final ViewHolder holder = new ViewHolder();

        listViewItem = (listViewItem == null) ? inflater.inflate(R.layout.item_details_layout, null) : listViewItem;
        holder.itemName = (TextView) listViewItem.findViewById
                (R.id.itemName);
        holder.itemqtyPrice = (TextView) listViewItem.findViewById
                (R.id.item_qty);
        holder.itemPrice = (TextView) listViewItem.findViewById
                (R.id.itemTotal);
        holder.itemImage = listViewItem.findViewById(R.id.itemImage);


        itemDetails = itemDetailsList.get(position);
        holder.itemName.setText(itemDetails.getItemName());
        holder.itemqtyPrice.setText(" ₹" + String.valueOf(itemDetails.getItemPrice()) + " * " + itemDetails.getItemBuyQuantity());
        holder.itemPrice.setText(" ₹" + String.valueOf(itemDetails.getTotalItemQtyPrice()));

        if (!((Activity) context).isFinishing()) {
            Glide.with(context).load(itemDetails.getItemImage()).into(holder.itemImage);
        }

        return listViewItem;
    }


}
