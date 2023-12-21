package com.smiligence.etmtechAdmin.common;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;

import com.smiligence.etmtechAdmin.bean.CategoryDetails;

import java.util.ArrayList;
import java.util.Arrays;

public class MultiSelectionSpinnerCategory extends AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener {

    ArrayList<CategoryDetails> items = null;
    boolean[] selection = null;
    ArrayAdapter adapter;
    TextView textView;

    public MultiSelectionSpinnerCategory(Context context) {
        super ( context );

        adapter = new ArrayAdapter( context,
                android.R.layout.simple_spinner_item );
        super.setAdapter ( adapter );
    }

    public MultiSelectionSpinnerCategory(Context context, AttributeSet attrs) {
        super ( context, attrs );

        adapter = new ArrayAdapter( context,
                android.R.layout.simple_spinner_item );
        super.setAdapter ( adapter );
    }

    @Override
    public void onClick(DialogInterface dialog, int idx, boolean isChecked)
    {
        if (selection != null && idx < selection.length)
        {
            selection[idx] = isChecked;

            adapter.clear ();
            adapter.add ( buildSelectedItemString () );
        } else
            {
            throw new IllegalArgumentException(
                    "'idx' is out of bounds." );
        }
    }

    @Override
    public boolean performClick() {
        final AlertDialog.Builder builder = new AlertDialog.Builder ( getContext () );
        String[] itemNames = new String[0];
        if(items!=null){
             itemNames = new String[items.size ()];

            for ( int i = 0; i < items.size (); i++ ) {
                itemNames[i] = items.get ( i ).getCategoryName ();
            }

        }
      

        builder.setMultiChoiceItems ( itemNames, selection, this );

        builder.setPositiveButton ( "OK", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                textView.setText("");
            }
        } );

        builder.show ();

        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner." );
    }

    public void setItems(ArrayList<CategoryDetails> items, TextView textView) {
        this.items = items;
        this.textView=textView;
        selection = new boolean[this.items.size ()];
        adapter.clear ();
        adapter.add ( "" );
        Arrays.fill ( selection, false );
    }

    public void setSelection(ArrayList<CategoryDetails> selection) {
        for ( int i = 0; i < this.selection.length; i++ ) {
            this.selection[i] = false;
        }

        for ( CategoryDetails sel : selection ) {
            for ( int j = 0; j < items.size (); ++j ) {
                if (items.get ( j ).getCategoryName ().equals ( sel.getCategoryName () )) {
                    this.selection[j] = true;
                }
            }
        }

        adapter.clear ();
        adapter.add ( buildSelectedItemString () );
    }

    public ArrayList<CategoryDetails> getSelectedItems() {
        ArrayList<CategoryDetails> selectedItems = new ArrayList<>();

        if (items.size()>0 && items!=null) {

            for (int i = 0; i < items.size(); ++i) {
                if (selection[i]) {
                    selectedItems.add(items.get(i));
                }
            }
        }
        return selectedItems;
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for ( int i = 0; i < items.size (); ++i ) {
            if (selection[i]) {
                if (foundOne) {
                    sb.append ( ", " );
                }

                foundOne = true;

                sb.append ( items.get ( i ).getCategoryName () );
            }
        }

        return sb.toString ();
    }
}