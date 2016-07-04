package com.mn.inventoryhelper;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

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
        LinearLayout entryItemBackground = (LinearLayout) convertView.findViewById(R.id.entryItemBackground);

        entryItemId.setText(entry.getSigning());
        entryItemId.setTextColor(Color.BLACK);
        entryItemName.setText(entry.getName());
        entryItemName.setTextColor(Color.BLACK);

        switch (entry.getInventoryStatus()){
            case MISSING:
                entryItemBackground.setBackgroundColor(Color.RED);
                break;
            case PRESENT:
                entryItemBackground.setBackgroundColor(Color.GREEN);
                break;
            case EXTRA:
                entryItemBackground.setBackgroundColor(Color.YELLOW);
                break;
        }

        return convertView;
    }
}
