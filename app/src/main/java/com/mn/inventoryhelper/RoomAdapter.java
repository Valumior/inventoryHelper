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
public class RoomAdapter extends ArrayAdapter<Room> {
    public RoomAdapter(Context context, ArrayList<Room> rooms){
        super(context, 0, rooms);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Room room = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_room, parent, false);
        }

        TextView roomItemNumber = (TextView) convertView.findViewById(R.id.roomItemNumber);
        TextView roomItemAddress = (TextView) convertView.findViewById(R.id.roomItemAddress);

        roomItemNumber.setText(room.getRoomId());
        roomItemNumber.setTextColor(Color.BLACK);
        roomItemAddress.setText(room.getAddress().toString());
        roomItemAddress.setTextColor(Color.BLACK);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return this.getView(position, convertView, parent);
    }
}
