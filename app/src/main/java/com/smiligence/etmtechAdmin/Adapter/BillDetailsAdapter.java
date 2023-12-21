package com.smiligence.etmtechAdmin.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smiligence.etmtechAdmin.R;
import com.smiligence.etmtechAdmin.bean.OrderDetails;

import java.util.List;

public class BillDetailsAdapter extends BaseAdapter {
    private Activity context;
    List<OrderDetails> billDetailsList;
    private static LayoutInflater inflater = null;
    String type;


    public BillDetailsAdapter(Activity context, List<OrderDetails> list,String type1) {
        this.context = context;
        this.billDetailsList = list;
        this.type=type1;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return billDetailsList.size();
    }

    public Object getItem(int position) {
        return billDetailsList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listViewItem = convertView;
        listViewItem = (listViewItem == null) ? inflater.inflate(R.layout.bill_info, null) : listViewItem;
        TextView bill_Number = (TextView) listViewItem.findViewById
                (R.id.bill_Number_text_view);
        TextView bill_amount = (TextView) listViewItem.findViewById
                (R.id.bill_amount_text);

        OrderDetails billDetails = billDetailsList.get(position);
        bill_Number.setText("# "+billDetails.getOrderId());
        bill_amount.setText(""+(billDetails.getPaymentamount()));

        /*if (type.equals("Seller"))
        {
            OrderDetails billDetails = billDetailsList.get(position);
            bill_Number.setText("# "+billDetails.getOrderId());
            bill_amount.setText(""+(billDetails.getPaymentamount()-billDetails.getTipAmount()));
        }
        else
        {
            OrderDetails billDetails = billDetailsList.get(position);
            bill_Number.setText("# "+billDetails.getOrderId());
           // bill_amount.setText(""+(billDetails.getTotalFeeForDeliveryBoy()+billDetails.getTipAmount()));
        }*/
        return listViewItem;
    }
}
