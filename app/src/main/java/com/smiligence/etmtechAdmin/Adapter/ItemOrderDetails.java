package com.smiligence.etmtechAdmin.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smiligence.etmtechAdmin.R;
import com.smiligence.etmtechAdmin.bean.ItemDetails;

import java.util.List;


public class ItemOrderDetails extends RecyclerView.Adapter<ItemOrderDetails.ImageViewHolder>  {
    private Context mcontext;
    private List<ItemDetails> itemList;
    String orderid;
    private OnItemClicklistener mlistener;


    public interface OnItemClicklistener {
        void Onitemclick(int Position ,String orderid);
    }

    public void setOnItemclickListener(OnItemClicklistener listener) {
        mlistener = listener;
    }

    public ItemOrderDetails(Context context, List<ItemDetails> itemListŇew ,String orderId) {
        mcontext = context;
        itemList = itemListŇew;
        orderid = orderId;

    }

    @NonNull
    @Override
    public ItemOrderDetails.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mcontext).inflate(R.layout.item_details_layout, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(v, mlistener);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemOrderDetails.ImageViewHolder holder, int position) {

        ItemDetails itemDetailsObj = itemList.get(position);
        holder.t_name.setTypeface(null, Typeface.BOLD);
        holder.t_name.setText((itemDetailsObj.getItemName().toLowerCase()));
        holder.t_price_percent.setText(itemDetailsObj.getItemPrice() + " * " + itemDetailsObj.getItemBuyQuantity());
        holder.t_total_amount.setText("₹" + String.valueOf(itemDetailsObj.getTotalItemQtyPrice()));
        holder.store_name.setText(itemDetailsObj.getStoreName());
        holder.orderstatus.setText(itemDetailsObj.getOrderStatus());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher_new);
        requestOptions.error(R.mipmap.ic_launcher_new);

        try {
            Glide.with(mcontext).setDefaultRequestOptions(requestOptions).load(itemDetailsObj.getItemImage()).fitCenter().into(holder.images);

        }catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


public class ImageViewHolder extends RecyclerView.ViewHolder{
    TextView t_name, t_price_percent, t_total_amount ,store_name , orderstatus;
    ImageView images;

    public ImageViewHolder(@NonNull View itemView, final OnItemClicklistener itemClicklistener) {
        super(itemView);

        store_name = itemView.findViewById(R.id.store_name);
        images = (ImageView) itemView.findViewById(R.id.itemImage);
        t_name = itemView.findViewById(R.id.itemName);
        t_price_percent = itemView.findViewById(R.id.item_qty);
        t_total_amount = itemView.findViewById(R.id.itemTotal);
        orderstatus = itemView.findViewById(R.id.orderstatusview);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClicklistener != null) {
                    int Position = getAdapterPosition();
                    if (Position != RecyclerView.NO_POSITION) {
                        itemClicklistener.Onitemclick(Position ,orderid);

                    }
                }
            }
        });

    }
}
}

/*public class ItemOrderDetails extends BaseAdapter {

    private Context mcontext;
    private List<ItemDetails> itemList;
    LayoutInflater inflater;
    String orderid;
    Uri imageUri;
    private Activity mActivity;
    ImageView trackImage;
    private OnItemClicklistener mlistener;

    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(OnItemClicklistener listener) {
        mlistener = listener;
    }

    public ItemOrderDetails(Context context, List<ItemDetails> itemListŇew ,String orderId,Activity activity,Uri imageuri) {
        mcontext = context;
        itemList = itemListŇew;
        inflater = (LayoutInflater.from ( context ));
        orderid = orderId;
        mActivity = activity;
        imageUri = imageuri;

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
        TextView t_name, t_price_percent, t_total_amount ,store_name , orderstatus;
        RelativeLayout ItemViewLayout ;
        Button chooseTrackImage;
        Uri mimageuri;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_details_layout, parent, false);
            holder.t_name = row.findViewById(R.id.itemName);
            holder.t_price_percent = row.findViewById(R.id.item_qty);
            holder.t_total_amount = row.findViewById(R.id.itemTotal);
            holder.store_name = row.findViewById(R.id.store_name);
            holder.ItemViewLayout = row.findViewById(R.id.ItemViewLayout);
            holder.orderstatus = row.findViewById(R.id.orderstatusview);

            holder.images = (ImageView) row.findViewById(R.id.itemImage);
            row.setTag(holder);
        } else {

            holder = (ViewHolder) row.getTag();
        }


        ItemDetails itemDetailsObj = itemList.get(position);
        holder.t_name.setTypeface(null, Typeface.BOLD);
        holder.t_name.setText((itemDetailsObj.getItemName().toLowerCase()));
        holder.t_price_percent.setText(itemDetailsObj.getItemPrice() + " * " + itemDetailsObj.getItemBuyQuantity());
        holder.t_total_amount.setText("₹" + String.valueOf(itemDetailsObj.getTotalItemQtyPrice()));
        holder.store_name.setText(itemDetailsObj.getStoreName());
        holder.orderstatus.setText(itemDetailsObj.getOrderStatus());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher);
        requestOptions.error(R.mipmap.ic_launcher);

     try {
         Glide.with(mcontext).setDefaultRequestOptions(requestOptions).load(itemDetailsObj.getItemImage()).fitCenter().into(holder.images);

     }catch (Exception e){

     }


     ViewHolder finalHolder = holder;


     if (itemDetailsObj.getOrderStatus().equals("Order Placed")){


            holder.ItemViewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mcontext);
                    final View customLayout = ((LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.status_change_popup, null);
                    builder.setView(customLayout);

                    ImageView cancel = customLayout.findViewById(R.id.cancelpop);
                    TextView orderidtxt = customLayout.findViewById(R.id.customerorderidtextdialog);
                    TextView storeName = customLayout.findViewById(R.id.cusnametextdialog);
                    TextView amount = customLayout.findViewById(R.id.amounttextdialog);
                    EditText courierPartnerNameEdt = customLayout.findViewById(R.id.courierPartnerName);
                    EditText trackingId = customLayout.findViewById(R.id.trackingId);
                    RelativeLayout trackingidRelativeLayout = customLayout.findViewById(R.id.trackingIdLayout);
                    RelativeLayout courierPartnerRelativeLayout = customLayout.findViewById(R.id.courierPartnerNameLayout);
                    RelativeLayout trackingLayout = customLayout.findViewById(R.id.trackingLayout);
                    Button chooseTrackImage = customLayout.findViewById(R.id.choose_track_Image);
                    trackImage = customLayout.findViewById(R.id.trackImage);
                    Button ok = customLayout.findViewById(R.id.buttonok);
                    final AlertDialog dialog = builder.create();

                    dialog.show();
                    dialog.setCancelable(false);

                    storeName.setText(itemDetailsObj.getStoreName());
                    amount.setText(String.valueOf(itemDetailsObj.getTotalItemQtyPrice()));
                    orderidtxt.setText(String.valueOf(orderid));


    if (imageUri!=null){
    Glide.with(mcontext).load(imageUri).into(trackImage);
}
                    chooseTrackImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((OrderDetailsActivity) mActivity).trackimagefunction();
                        }
                    });


                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if (courierPartnerNameEdt.getText().toString().trim().equals("") || courierPartnerNameEdt.getText().toString().trim().isEmpty()) {
                                courierPartnerNameEdt.setError(Constant.REQUIRED);
                                return;
                            } else if (courierPartnerNameEdt.getText().toString().trim().length() < 3) {
                                courierPartnerNameEdt.setError("Minimum 3 characters required");
                                return;
                            } else if (!(0 <= "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnoqrstuvwxyz".indexOf(courierPartnerNameEdt.getText().toString().trim().charAt(0)))) {
                                courierPartnerNameEdt.setError("Courirer Partner Name Must be starts with Alphabets");
                                return;
                            } else if (trackingId.getText().toString().equals("") || trackingId.getText().toString().isEmpty()) {
                                trackingId.setError(Constant.REQUIRED);
                                return;
                            } else if (trackingId.getText().toString().trim().length() < 3) {
                                trackingId.setError("Minimum 3 characters required");
                                return;
                            } else if (imageUri == null) {
                                Toast.makeText(mcontext, "Please add tracking image", Toast.LENGTH_SHORT).show();
                                return;
                            } else {

                                if ("Order Placed".equalsIgnoreCase(itemDetailsObj.getOrderStatus())) {
                                    statusOrderplaced(itemDetailsObj.getOrderStatus(), courierPartnerNameEdt.getText().toString().trim(), trackingId.getText().toString().trim(), finalHolder.mimageuri, storeName.getText().toString().trim(), orderid,dialog);
                                }

                            }

                        }
                    });


                }
            });
    }
        else  if (itemDetailsObj.getOrderStatus().equals("Your Order is Shipped")){

         holder.ItemViewLayout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mcontext,R.style.CustomAlertDialog);
                 final View customLayout = ((LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.trackingdetail_layout, null);

                 builder.setView(customLayout);

                 final AlertDialog dialog = builder.create();
                 dialog.show();

                 dialog.setCancelable(false);
                 Button cancel = customLayout.findViewById(R.id.cancel_btn);
                 Button okay = customLayout.findViewById(R.id.yes_btn);

                 cancel.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         dialog.dismiss();
                     }
                 });

                 okay.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         if ("Your Order is Shipped".equalsIgnoreCase(itemDetailsObj.getOrderStatus())) {
                             statusPickup(itemDetailsObj.getOrderStatus(),itemDetailsObj.getStoreName(),orderid, dialog);

                         }
                     }
                 });
             }
         });

        } else  if (itemDetailsObj.getOrderStatus().equals("Delivered")) {
            holder.ItemViewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mcontext,R.style.CustomAlertDialog);
                    final View customLayout = ((LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.viewtrackingdetails_layout, null);

                    builder.setView(customLayout);
                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    TextView    deliverystatus = customLayout.findViewById(R.id.delivery_status);
                    TextView    courierstatus = customLayout.findViewById(R.id.courierdelivery_status);
                    TextView    trackingid = customLayout.findViewById(R.id.couriertrackingdelivery_status);


                deliverystatus.setText(itemDetailsObj.getOrderStatus());
                courierstatus.setText(itemDetailsObj.getCourierName());
                trackingid.setText(itemDetailsObj.getTrackingId());


                }
            });

        }

        return row;

    }


  public void statusOrderplaced(String status, String courieredt, String trackingid, Uri imageuri, String storeNameIntent, String orderId, AlertDialog dialog) {

        if (status.equals ( "Order Placed" )) {
            DatabaseReference ref = CommonMethods.fetchFirebaseDatabaseReference ( "OrderDetails" ).child ( String.valueOf ( orderId ) ).child("itemDetailList");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot updatedeliverysnap : snapshot.getChildren()){
                            ItemDetails itemDetails = updatedeliverysnap.getValue(ItemDetails.class);

                            if (storeNameIntent.equals(itemDetails.getStoreName())){
                                String key = updatedeliverysnap.getKey();
                                Log.e("KeySnap",key);


                                DatabaseReference changeref = CommonMethods.fetchFirebaseDatabaseReference ( "OrderDetails" ).child ( String.valueOf ( orderId ) ).child("itemDetailList").child(key);
                                changeref.child ( "orderStatus" ).setValue ( "Your Order is Shipped" );
                                changeref.child ( "courierName" ).setValue ( courieredt );
                                changeref.child ( "trackingId" ).setValue ( trackingid );

                                notifyDataSetChanged();

                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

           dialog.dismiss();

        }

    }

    public void statusPickup(String pickupDtatus, String storeNameIntent, String orderId, AlertDialog dialog) {

        if (pickupDtatus.equals ( "Your Order is Shipped" )) {

            DatabaseReference refdeliverted = CommonMethods.fetchFirebaseDatabaseReference ( "OrderDetails" ).child ( String.valueOf ( orderId ) ).child("itemDetailList");
            refdeliverted.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot updatedeliverysnap : snapshot.getChildren()){
                            ItemDetails itemDetails = updatedeliverysnap.getValue(ItemDetails.class);

                            if (storeNameIntent.equals(itemDetails.getStoreName())){
                                String key = updatedeliverysnap.getKey();
                                Log.e("KeySnap",key);

                                DatabaseReference changeref = CommonMethods.fetchFirebaseDatabaseReference ( "OrderDetails" ).child ( String.valueOf ( orderId ) ).child("itemDetailList").child(key);
                                changeref.child ( "orderStatus" ).setValue ( "Delivered" );


                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            dialog.dismiss();
           Intent intent = mActivity.getIntent();

           mcontext.startActivity ( intent );
            notifyDataSetChanged();
        }
    }



}*/