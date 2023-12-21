package com.smiligence.etmtechAdmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smiligence.etmtechAdmin.R;

import java.util.ArrayList;
import java.util.List;

public class ImageListAdapter extends BaseAdapter {

    private Context mcontext;
    private List<String> discountList;
    LayoutInflater inflater;

    public ImageListAdapter(Context context, List<String> listDiscount) {
        mcontext = context;
       this.discountList = listDiscount;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return discountList.size();
    }

    @Override
    public Object getItem(int position) {
        return discountList.get(position);
    }

    @Override

    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView images, bin,edit;


    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void updateData(ArrayList<String> viewModels) {
        discountList.clear();
        discountList.addAll(viewModels);
        notifyDataSetChanged();
    }  public void removeItem(int position) {
        discountList.remove(position);
        notifyDataSetChanged();
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.image_list, parent, false);

            holder.images = (ImageView) row.findViewById(R.id.itemimageview);
         /*   holder.bin = row.findViewById(R.id.bin);
            holder.edit=row.findViewById(R.id.edit);*/
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }



        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher_new);
        requestOptions.error(R.mipmap.ic_launcher_new);
        Glide.with(mcontext)
                .setDefaultRequestOptions(requestOptions)
                .load(discountList.get(position)).fitCenter().into(holder.images);


        return row;
    }


    @Override
    public int getViewTypeCount() {
        if (getCount() > 0) {
            return getCount();
        } else {
            return super.getViewTypeCount();
        }
    }
}