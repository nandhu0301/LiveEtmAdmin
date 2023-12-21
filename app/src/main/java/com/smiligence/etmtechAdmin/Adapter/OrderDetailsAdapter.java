package com.smiligence.etmtechAdmin.Adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smiligence.etmtechAdmin.R;
import com.smiligence.etmtechAdmin.bean.OrderDetails;

import java.util.HashMap;
import java.util.List;

public class OrderDetailsAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<OrderDetails>> expandableBillDetail;


    String cus_name, billNum, finalAmount, timeStamp, paymentMode, orderStatusText, categoryTypeId, orderTypeText;

    public OrderDetailsAdapter(Context context, List<String> expandableListTitle,
                               HashMap<String, List<OrderDetails>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableBillDetail = expandableListDetail;


    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {


        return this.expandableBillDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);

    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.listofitem, null);
        }
        TextView expandedListTextView = convertView
                .findViewById(R.id.listItem);
        TextView billNumText = convertView.findViewById(R.id.billnumber);
        TextView timeStampText = convertView.findViewById(R.id.timeList);
        TextView finalBillAmountText = convertView.findViewById(R.id.finalbilllist);
        ImageView cash = convertView.findViewById(R.id.money_icon);
        ImageView card = convertView.findViewById(R.id.card_icon);
        TextView orderStatus = convertView.findViewById(R.id.orderStatus);



        OrderDetails billDetails = this.expandableBillDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);


        cus_name = billDetails.getCustomerName();
        billNum = billDetails.getOrderId();
        timeStamp = billDetails.getOrderTime();
        finalAmount = String.valueOf(billDetails.getPaymentamount());
        paymentMode = billDetails.getPaymentType();
        orderStatusText = billDetails.getOrderStatus();
        categoryTypeId = billDetails.getCategoryTypeId();

        if (billDetails.getOrderType() != null) {
            orderTypeText = billDetails.getOrderType();
        }

        expandedListTextView.setText(cus_name);
        billNumText.setText("#" + billNum);
        timeStampText.setText(timeStamp);
        finalBillAmountText.setText("₹" + String.valueOf(finalAmount));
        orderStatus.setText(billDetails.getOrderStatus());

        System.out.println("sdFV" + billDetails.getOrderId() + billDetails.getOrderType());

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableBillDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        final String[] day = {(String) getGroup(listPosition)};


        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = convertView
                .findViewById(R.id.lblListHeader);

        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}