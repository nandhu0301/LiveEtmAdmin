package com.smiligence.etmtechAdmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.smiligence.etmtechAdmin.R;
import com.smiligence.etmtechAdmin.bean.MenuModel;
import com.smiligence.etmtechAdmin.common.Constant;

import java.util.HashMap;
import java.util.List;


public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<MenuModel> listDataHeader;
    private HashMap<MenuModel, List<MenuModel>> listDataChild;

    public ExpandableListAdapter(Context context, List<MenuModel> listDataHeader,
                                 HashMap<MenuModel, List<MenuModel>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public MenuModel getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = getChild(groupPosition, childPosition).menuName;


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_child, null);
        }
        TextView txtListChild = convertView.findViewById(R.id.lblListItem);


        if (childText.equals(Constant.TITLE_CATEGORY)) {
            txtListChild.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_categories_svg, 0, 0, 0 );
        }else if (childText.equals(Constant.DELIVERY_DETAILS))
        {
            txtListChild.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_contact, 0, 0, 0 );
        }

        txtListChild.setText(childText);
        return convertView;



    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if (this.listDataChild.get(this.listDataHeader.get(groupPosition)) == null)

            return 0;
        else
            return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                    .size();
    }

    @Override
    public MenuModel getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {


        String headerTitle = getGroup(groupPosition).menuName;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_header, null);
        }



        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        TextView lblListHeader1 = convertView.findViewById(R.id.lblListHeader1);
        if (headerTitle.equals(Constant.TITLE_DASHBOARD )  ||headerTitle.equals("Logout" ))
        {
            if (headerTitle.equals(Constant.TITLE_DASHBOARD))
            {
                lblListHeader1.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_dashboardicon_01, 0, 0, 0 );

            }
            else if ( headerTitle.equals(Constant.TITLE_STOREHISTORY ) )
            {
                lblListHeader1.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_storehistory_01, 0, 0, 0 );
            }

            else if ( headerTitle.equals(Constant.TITLE_LOGOUT) )
            {
                lblListHeader1.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_logout_svg, 0, 0, 0 );
            }
            lblListHeader1.setText(headerTitle);
            lblListHeader.setVisibility(View.INVISIBLE);
            lblListHeader1.setVisibility(View.VISIBLE);
        }else {
            if (headerTitle.equals(Constant.TITLE_MAINTAININGINPUTS))
            {
                lblListHeader.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_maintain_inputs_icon, 0, R.drawable.ic_outline_keyboard_arrow_down_24, 0 );
            }
            lblListHeader.setText(headerTitle);
            lblListHeader.setVisibility(View.VISIBLE);
            lblListHeader1.setVisibility(View.INVISIBLE);
        }


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }
}









/*public class ExpandableListAdapter  extends BaseExpandableListAdapter  {

    private Context context;
    private List<MenuModel> listDataHeader;
    private HashMap<MenuModel, List<MenuModel>> listDataChild;

    public ExpandableListAdapter(Context context, List<MenuModel> listDataHeader,
                                 HashMap<MenuModel, List<MenuModel>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }


    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (this.listDataChild.get(this.listDataHeader.get(groupPosition)) == null)

            return 0;
        else
            return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                    .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int i, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

       String headerTitle = getGroup(groupPosition).menuName;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_header, null);
        }



        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        TextView lblListHeader1 = convertView.findViewById(R.id.lblListHeader1);
        if (headerTitle.equals(Constant.TITLE_DASHBOARD )  ||headerTitle.equals("Logout" ))
        {
            if (headerTitle.equals(Constant.TITLE_DASHBOARD))
            {
                lblListHeader1.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_dashboardicon_01, 0, 0, 0 );

            }
            else if ( headerTitle.equals(Constant.TITLE_STOREHISTORY ) )
            {
                lblListHeader1.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_storehistory_01, 0, 0, 0 );
            }

            else if ( headerTitle.equals(Constant.TITLE_LOGOUT) )
            {
                lblListHeader1.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_logout_svg, 0, 0, 0 );
            }
            lblListHeader1.setText(headerTitle);
            lblListHeader.setVisibility(View.INVISIBLE);
            lblListHeader1.setVisibility(View.VISIBLE);
        }else {
           if (headerTitle.equals(Constant.TITLE_MAINTAININGINPUTS))
            {
                lblListHeader.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_maintain_inputs_icon, 0, R.drawable.ic_outline_keyboard_arrow_down_24, 0 );
            }
            lblListHeader.setText(headerTitle);
            lblListHeader.setVisibility(View.VISIBLE);
            lblListHeader1.setVisibility(View.INVISIBLE);
        }


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild , View convertView, ViewGroup parent) {


        final String childText = getChild(groupPosition, childPosition).menuName;



        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_child, null);
        }
        TextView txtListChild = convertView.findViewById(R.id.lblListItem);


        if (childText.equals(Constant.TITLE_CATEGORY)) {
            txtListChild.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_categories_svg, 0, 0, 0 );
        }else if (childText.equals(Constant.DELIVERY_DETAILS))
        {
            txtListChild.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_contact, 0, 0, 0 );
        }

        txtListChild.setText(childText);
        return convertView;


        return null;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}*/
