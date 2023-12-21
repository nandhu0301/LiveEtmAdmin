package com.smiligence.etmtechAdmin.Adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;


import com.airbnb.lottie.LottieAnimationView;
import com.smiligence.etmtechAdmin.R;

import java.util.HashMap;
import java.util.List;

public class ReceiptExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> uniqueDateList;
    private List<String> uniqueDayList;
    private HashMap<String, List<String>> expandableListDetail;
    private HashMap<String, List<String>> expandableBillList;
    private HashMap<String, List<String>> timeStampList;
    private HashMap<String, List<Integer>> finalBillList;
    private HashMap<String, List<String>> paymentModeList;
    private HashMap<String, List<String>> orderStatusList;



    String cus_name, billNum, finalAmount, timeStamp, paymentMode,orderStatusText;

    public ReceiptExpandableAdapter(Context context, List<String> expandableListTitle,
                                    HashMap<String, List<String>> expandableListDetail,
                                    HashMap<String, List<String>> expandableBillList,
                                    HashMap<String, List<String>> timeStampList,
                                    HashMap<String, List<Integer>> finalBillList,
                                    HashMap<String, List<String>> paymentModeList,
                                    HashMap<String, List<String>> orderStatusList){
        this.context = context;
        this.uniqueDateList = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.expandableBillList = expandableBillList;
        this.timeStampList = timeStampList;
        this.finalBillList = finalBillList;
        this.paymentModeList = paymentModeList;
        this.orderStatusList=orderStatusList;

    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {


        return this.expandableListDetail.get(this.uniqueDateList.get(listPosition))
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
            convertView = layoutInflater.inflate(R.layout.newlistofitem, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.listItem);
        TextView billNumText = convertView.findViewById(R.id.billnumber);
        TextView timeStampText = convertView.findViewById(R.id.timeList);
        TextView finalBillAmountText = convertView.findViewById(R.id.finalbilllist);
        LottieAnimationView orderplaced = convertView.findViewById(R.id.orderplaced);
        LottieAnimationView readyforpickup = convertView.findViewById(R.id.readyforpickup);
        LottieAnimationView deliveryontheway = convertView.findViewById(R.id.deliveryontheway);
        LottieAnimationView delivered= convertView.findViewById(R.id.delivered);
        TextView orderStatus=convertView.findViewById(R.id.orderStatus_view);

        cus_name = this.expandableListDetail.get(this.uniqueDateList.get(listPosition))
                .get(expandedListPosition);
        billNum = this.expandableBillList.get(this.uniqueDateList.get(listPosition)).
                get(expandedListPosition);
        timeStamp = this.timeStampList.get(this.uniqueDateList.get(listPosition)).
                get(expandedListPosition);
        finalAmount = String.valueOf(this.finalBillList.get(this.uniqueDateList.get(listPosition)).get(expandedListPosition));
        paymentMode = this.paymentModeList.get(this.uniqueDateList.get(listPosition)).
                get(expandedListPosition);
        orderStatusText = this.orderStatusList.get(this.uniqueDateList.get(listPosition))
                .get(expandedListPosition);

        expandedListTextView.setText(cus_name);
        billNumText.setText("#" + billNum);
        timeStampText.setText(timeStamp);
        finalBillAmountText.setText("₹" + String.valueOf(finalAmount));
        orderStatus.setText(orderStatusText);
        if (orderStatusText.equalsIgnoreCase ( "Your Order is Shipped" ))
        {
            readyforpickup.setVisibility(View.VISIBLE);
            orderplaced.setVisibility(View.INVISIBLE);
            deliveryontheway.setVisibility(View.INVISIBLE);
            delivered.setVisibility(View.INVISIBLE);
        }
        else if (orderStatusText.equalsIgnoreCase ( "Order Placed" ))
        {
            orderplaced.setVisibility(View.VISIBLE);
            deliveryontheway.setVisibility(View.INVISIBLE);
            readyforpickup.setVisibility(View.INVISIBLE);
            delivered.setVisibility(View.INVISIBLE);
        }


        else  if(orderStatusText.equalsIgnoreCase("Completed")){
            deliveryontheway.setVisibility(View.INVISIBLE);
            orderplaced.setVisibility(View.INVISIBLE);
            readyforpickup.setVisibility(View.INVISIBLE);
            delivered.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.uniqueDateList.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.uniqueDateList.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.uniqueDateList.size();
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
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.lblListHeader);

        listTitleTextView.setTypeface(null, Typeface.BOLD);
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


