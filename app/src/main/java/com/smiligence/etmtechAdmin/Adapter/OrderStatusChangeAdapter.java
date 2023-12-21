package com.smiligence.etmtechAdmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smiligence.etmtechAdmin.R;
import com.smiligence.etmtechAdmin.bean.OrderDetails;

import java.util.List;

public class OrderStatusChangeAdapter extends BaseAdapter {

    private Context mcontext;
    private List<OrderDetails> itemList;
    LayoutInflater inflater;

    public OrderStatusChangeAdapter(Context context, List<OrderDetails> itemListŇew) {
        mcontext = context;
        itemList = itemListŇew;
        inflater = (LayoutInflater.from ( context ));
    }

    @Override
    public int getCount() {
        return itemList.size ();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get ( position );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView images;
        TextView cusName, cusPhoneNumber, cusPurchaseAmount,date,orderNum,orderstatustext,cusaddress;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        OrderStatusChangeAdapter.ViewHolder holder = new OrderStatusChangeAdapter.ViewHolder();
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
            row = inflater.inflate ( R.layout.order_status_change_layout, parent, false );
            holder.cusName = row.findViewById ( R.id.cusnametext );
            holder.cusPhoneNumber = row.findViewById ( R.id.cusphonenumber );
            holder.cusPurchaseAmount = row.findViewById ( R.id.purchasedamount );
            holder.date = row.findViewById ( R.id.ordereddatetext );
            holder.orderNum = row.findViewById ( R.id.ordernumber );
            holder.orderstatustext=row.findViewById(R.id.statustext);
            holder.cusaddress=row.findViewById(R.id.customerAddresstext);


            row.setTag ( holder );
        } else {

            holder = (OrderStatusChangeAdapter.ViewHolder) row.getTag ();
        }

        if(itemList != null && !itemList.isEmpty()) {
            OrderDetails orderDetails = itemList.get(position);

            holder.cusPhoneNumber.setText((orderDetails.getCustomerPhoneNumber()));
            holder.cusPurchaseAmount.setText("₹" + String.valueOf(orderDetails.getTotalAmount()));
            holder.date.setText(orderDetails.getPaymentDate());
            holder.orderNum.setText("#" + String.valueOf(orderDetails.getOrderId()));
            holder.cusName.setText(orderDetails.getCustomerName());
            holder.orderstatustext.setText(orderDetails.getOrderStatus());
            holder.cusaddress.setText(orderDetails.getShippingaddress());

        }
        return row;


    }
}
