package com.smiligence.etmtechAdmin;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smiligence.etmtechAdmin.bean.CustomerDetails;

import java.util.List;


public class CustomerAdapterNew extends BaseAdapter {
    private Activity context;
    List<CustomerDetails> customerList;
    private static LayoutInflater inflater = null;

    public CustomerAdapterNew(Activity context, List<CustomerDetails> list) {
        //super ( context, R.layout.viewcustomer, list );
        this.context = context;
        this.customerList = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return customerList.size();
    }


    @Override
    public Object getItem(int position) {
        return customerList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listViewItem =  convertView;
        listViewItem =(listViewItem== null) ? inflater.inflate ( R.layout.customer_list, null ) : listViewItem;
        TextView textViewName = (TextView) listViewItem.findViewById
                ( R.id.textViewName );
        TextView textviewnumber = (TextView) listViewItem.findViewById
                ( R.id.textviewnumber );
        CustomerDetails customer = customerList.get ( position );
        textViewName.setText ( customer.getFullName() );
        textviewnumber.setText ( customer.getCustomerPhoneNumber());

        return listViewItem;
    }
}
