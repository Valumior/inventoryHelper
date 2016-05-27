package com.mn.inventoryhelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ServerSettingActivity extends AppCompatActivity {

    EditText serverAddressEdit;
    Button serverSettingCancel, serverSettingSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_setting);

        serverAddressEdit = (EditText)findViewById(R.id.serverAddressEdit);

        serverSettingCancel = (Button)findViewById(R.id.serverSettingCancel);
        serverSettingSave = (Button)findViewById(R.id.serverSettingSave);

        final SharedPreferences sharedPreferences = getSharedPreferences(InventoryHelperApplication.getPREFERENCES(), Context.MODE_PRIVATE);
        serverAddressEdit.setText(sharedPreferences.getString("server",""));

        serverSettingCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        serverSettingSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String server = serverAddressEdit.getText().toString();

                if(!server.endsWith("/"))
                    server = server + "/";

                if(!server.startsWith("https://")){
                    if(server.startsWith("http://"))
                        server = server.replace("http://", "https://");
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("server", server);
                editor.apply();

                finish();
            }
        });
    }
}
