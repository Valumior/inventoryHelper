package com.mn.inventoryhelper;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Valu on 2016-05-15.
 */
public class AddressAdapter extends ArrayAdapter<Address> {

    public AddressAdapter(Context context, ArrayList<Address> addresses){
        super(context, 0, addresses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Address address = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_address, parent, false);
        }

        TextView addressItemText = (TextView) convertView.findViewById(R.id.addressItemText);

        addressItemText.setText(address.toString());
        addressItemText.setTextColor(Color.BLACK);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return this.getView(position, convertView, parent);
    }
}
