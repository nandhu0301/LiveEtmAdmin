package com.smiligence.etmtechAdmin.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smiligence.etmtechAdmin.R;
import com.smiligence.etmtechAdmin.bean.UserDetails;

import java.util.HashMap;
import java.util.List;

public class StoreListingAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<UserDetails>> expandableBillDetail;
    String cus_name, billNum, finalAmount, timeStamp, paymentMode, orderStatusText,categoryTypeId;

    public StoreListingAdapter(Context context, List<String> expandableListTitle,
                               HashMap<String, List<UserDetails>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableBillDetail = expandableListDetail;
    }

    @Override
    public int getGroupCount() {
    return this.expandableListTitle.size ();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.expandableBillDetail.get ( this.expandableListTitle.get ( i ) )
                .size ();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get ( listPosition );
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {

        return this.expandableBillDetail.get ( this.expandableListTitle.get ( listPosition ))
                .get ( expandedListPosition );
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String listTitle = (String) getGroup ( listPosition );
        final String[] day = {(String) getGroup ( listPosition )};


        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
            convertView = layoutInflater.inflate ( R.layout.list_group, null );
        }
        TextView listTitleTextView = convertView
                .findViewById ( R.id.lblListHeader );

        listTitleTextView.setText ( listTitle );
        return convertView;
    }

    @Override
    public View getChildView(int listPosition, int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
            convertView = layoutInflater.inflate ( R.layout.list_of_item_store, null );
        }

        TextView storeName = convertView
                .findViewById ( R.id.storeNameList );
        TextView storeAddress = convertView
                .findViewById ( R.id.storeAddressList );
        ImageView storeImageView = convertView
                .findViewById ( R.id.storeImageList );


        UserDetails billDetails = this.expandableBillDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);

        storeName.setText(billDetails.getStoreName());
        storeAddress.setText(billDetails.getAddress());

        RequestOptions requestOptions = new RequestOptions ();
        requestOptions.placeholder ( R.mipmap.ic_launcher_new );
        requestOptions.error ( R.mipmap.ic_launcher_new );
        if (!((Activity) context).isFinishing())
        {
            Glide.with ( context ).setDefaultRequestOptions ( requestOptions ).load ( billDetails.getStoreLogo() ).fitCenter ().into ( storeImageView );
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
