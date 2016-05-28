package com.mn.inventoryhelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class InventorySessionActivity extends AppCompatActivity {
    TextView inventoryRoomLabel;
    ListView inventoryEntryListView;
    Button inventoryScanButton, inventoryReportButton;
    int roomId, inventoryOrderId;
    String server, token;
    ArrayList<String> readCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_session);

        inventoryRoomLabel = (TextView)findViewById(R.id.inventoryRoomLabel);

        inventoryEntryListView = (ListView)findViewById(R.id.inventoryEntryListView);

        inventoryScanButton = (Button)findViewById(R.id.inventoryScanButton);
        inventoryReportButton = (Button)findViewById(R.id.inventoryReportButton);

        InventoryHelperApplication application = (InventoryHelperApplication)getApplicationContext();
        token = application.getToken();

        SharedPreferences sharedPreferences = getSharedPreferences(InventoryHelperApplication.getPREFERENCES(),MODE_PRIVATE);
        server = sharedPreferences.getString("server", "");

        readCodes = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            roomId = extras.getInt("roomId");
            inventoryOrderId = extras.getInt("orderId");
            RoomItemAsyncDownloader downloader = new RoomItemAsyncDownloader(this);
            downloader.execute(server, token);
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            readCodes = data.getStringArrayListExtra("readCodes");
        }
    }

    private class EntryAsyncDownloader extends AsyncTask<String, Void, ArrayList<Entry>> {
        private ProgressDialog progressDialog;
        private Room room;

        public EntryAsyncDownloader(ProgressDialog progressDialog, Room room){
            this.progressDialog = progressDialog;
            this.room = room;
        }

        @Override
        protected ArrayList<Entry> doInBackground(String... params) {
            return room.getEntries(params[0], params[1]);
        }

        @Override
        protected void onPreExecute() {
            if(this.progressDialog.isShowing()){
                this.progressDialog.setMessage("Fetching data.");
                this.progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(final ArrayList<Entry> entries) {
            if(this.progressDialog.isShowing())
                this.progressDialog.dismiss();
            if(entries != null){

                inventoryScanButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), QRReaderMassActivity.class);
                        intent.putStringArrayListExtra("readCodes", readCodes);
                        startActivityForResult(intent, 1);
                    }
                });

            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "No entries found. Check your connection or pick another room.", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        }
    }

    private class RoomItemAsyncDownloader extends AsyncTask<String, Void, Room>{
        private ProgressDialog progressDialog;
        private String server;
        private String token;

        public RoomItemAsyncDownloader(InventorySessionActivity activity){
            this.progressDialog = new ProgressDialog(activity);
            this.server = "";
            this.server = "";
        }

        @Override
        protected Room doInBackground(String... params) {
            this.server = params[0];
            this.token = params[1];

            return Room.getRoom(this.server, this.token, params[2]);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.setMessage("Fetching data.");
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(Room room) {
            if(room != null) {
                EntryAsyncDownloader downloader = new EntryAsyncDownloader(this.progressDialog, room);
                downloader.execute(this.server, this.token);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Connection error. Check your connection and try again.", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        }
    }
}
