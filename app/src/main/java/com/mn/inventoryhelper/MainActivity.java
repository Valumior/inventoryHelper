package com.mn.inventoryhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

public class MainActivity extends AppCompatActivity {

    Button readQRButton, getAllRoomsButton, getAllAddressesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readQRButton = (Button) findViewById(R.id.readQRButton);
        getAllRoomsButton = (Button) findViewById(R.id.getAllRoomsButton);
        getAllAddressesButton = (Button) findViewById(R.id.getAllAddressesButton);

        readQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QRReaderActivity.class);
                startActivity(intent);
            }
        });

        getAllRoomsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RoomListActivity.class);
                startActivity(intent);
            }
        });

        getAllAddressesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddressListActivity.class);
                startActivity(intent);
            }
        });
    }
}
