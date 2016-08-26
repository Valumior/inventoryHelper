package com.mn.inventoryhelper;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class InventoryOrderAdapter extends ArrayAdapter<InventoryOrder> {
    public InventoryOrderAdapter(Context context, ArrayList<InventoryOrder> inventoryOrders){
        super(context, 0, inventoryOrders);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InventoryOrder inventoryOrder = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_inventory_order, parent, false);
        }

        TextView orderItemDate = (TextView) convertView.findViewById(R.id.orderItemDate);

        orderItemDate.setText("Zlecenie " + inventoryOrder.getDateOrdered().toString("dd/MM/yyyy"));
        orderItemDate.setTextColor(Color.BLACK);

        return convertView;
    }
}
