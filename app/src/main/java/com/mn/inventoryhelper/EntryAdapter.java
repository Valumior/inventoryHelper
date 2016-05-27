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
public class EntryAdapter extends ArrayAdapter<Entry> {
    public EntryAdapter(Context context, ArrayList<Entry> entries){
        super(context, 0, entries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Entry entry = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_entry, parent, false);
        }

        TextView entryItemId = (TextView) convertView.findViewById(R.id.entryItemId);
        TextView entryItemName = (TextView) convertView.findViewById(R.id.entryItemName);

        entryItemId.setText(entry.getIdNumber());
        entryItemId.setTextColor(Color.BLACK);
        entryItemName.setText(entry.getName());
        entryItemName.setTextColor(Color.BLACK);

        return convertView;
    }
}
