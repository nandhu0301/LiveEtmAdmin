package com.smiligence.etmtechAdmin.bean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smiligence.etmtechAdmin.R;

import java.util.List;

public class SellerAdapter extends BaseAdapter {
    public Context mcontext ;
    public  List<UserDetails> itemList ;


    public SellerAdapter(Context context , List<UserDetails> itemListŇew){

        mcontext = context;
        itemList = itemListŇew ;

    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int i) {
        return itemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("CheckResult")
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View row = convertView;
        SellerAdapter.ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
            row = inflater.inflate ( R.layout.item_details_layout, viewGroup, false );
            holder.t_storename = row.findViewById ( R.id.itemName );
            holder.t_store_address = row.findViewById ( R.id.item_qty );

            holder.images = row.findViewById ( R.id.itemImage );
            row.setTag ( holder );
        } else {

            holder = (SellerAdapter.ViewHolder) row.getTag ();
        }

        UserDetails itemDetailsObj = itemList.get ( position );

        holder.t_storename.setText ( itemDetailsObj.getStoreName () );
        holder.t_store_address.setText ( itemDetailsObj.getAddress ());

        RequestOptions requestOptions = new RequestOptions ();
        requestOptions.placeholder ( R.mipmap.ic_launcher_new );
        requestOptions.error ( R.mipmap.ic_launcher_new );
        if (!((Activity) mcontext).isFinishing())
        {
            Glide.with ( mcontext )
                    .setDefaultRequestOptions ( requestOptions )
                    .load ( itemDetailsObj.getStoreLogo () ).fitCenter ().into ( holder.images );
        }
        return row;
    }

    private static class ViewHolder{
        ImageView images;
        TextView t_storename, t_store_address;

    }
}
