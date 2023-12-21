package com.smiligence.etmtechAdmin.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smiligence.etmtechAdmin.R;
import com.smiligence.etmtechAdmin.bean.PincodeDetails;

import java.util.ArrayList;

import static com.smiligence.etmtechAdmin.common.Constant.INACTIVE_STATUS;

public class PincodeDetailsAdapter extends ArrayAdapter<PincodeDetails> {
    public Activity context;

    public ArrayList<PincodeDetails> billreportsList;

    private TextView catagoryName;
    ImageView itemImages;
    LinearLayout card;

    public PincodeDetailsAdapter(Activity context, ArrayList<PincodeDetails> billreportsList) {
        super(context, R.layout.pincode_list, billreportsList);

        this.context = context;
        this.billreportsList = billreportsList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listview = inflater.inflate(R.layout.pincode_list, null, true);

        catagoryName = listview.findViewById(R.id.pincode);
        card=listview.findViewById(R.id.card);

        PincodeDetails pincodeDetails = billreportsList.get(position);


        catagoryName.setText(pincodeDetails.getPinCode());
        if (INACTIVE_STATUS.equalsIgnoreCase(pincodeDetails.getPincodeStatus())) {
            //holder.card.setBackgroundColor(R.color.grey);
            card.setBackgroundColor(Color.parseColor("#B8B5B5"));
        }

        return listview;
    }
}
