package com.mn.inventoryhelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RoomListActivity extends AppCompatActivity {

    ListView roomListView;
    Boolean inventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        roomListView = (ListView) findViewById(R.id.roomListView);

        SharedPreferences sharedPreferences = getSharedPreferences(InventoryHelperApplication.getPREFERENCES(), MODE_PRIVATE);
        InventoryHelperApplication application = (InventoryHelperApplication)getApplicationContext();

        String server = sharedPreferences.getString("server","");
        String token = application.getToken();

        Bundle extras = getIntent().getExtras();
        inventory = extras.getBoolean("inventory", false);
        int id = extras.getInt("address", 0);

        if(id != 0){
            AddressItemAsyncDownloader downloader = new AddressItemAsyncDownloader(this);
            downloader.execute(server, token, Integer.toString(id));
        } else {
            RoomAsyncDownloader downloader = new RoomAsyncDownloader(this);
            downloader.execute(server, token);
        }
    }

    private class RoomAsyncDownloader extends AsyncTask<String, Void, ArrayList<Room>>{

        ProgressDialog progressDialog;
        Address address;

        public RoomAsyncDownloader(RoomListActivity activity){
            this.progressDialog = new ProgressDialog(activity);
            this.address = null;
        }

        public RoomAsyncDownloader(ProgressDialog progressDialog, Address address){
            this.progressDialog = progressDialog;
            this.address = address;
        }

        @Override
        protected ArrayList<Room> doInBackground(String... params) {
            String server = params[0];
            String token = params[1];

            if(address != null){
                return address.getRooms(server, token);
            } else {
                return Room.getRooms(server, token);
            }
        }

        @Override
        protected void onPreExecute() {
            if(!this.progressDialog.isShowing()){
                progressDialog.setMessage("Fetching data.");
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Room> rooms) {
            if(this.progressDialog.isShowing())
               this.progressDialog.dismiss();
            if(rooms != null){
                RoomAdapter adapter = new RoomAdapter(RoomListActivity.this, rooms);
                roomListView.setAdapter(adapter);

                if(inventory){
                    roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Room room = (Room)parent.getAdapter().getItem(position);

                            Intent intent = new Intent(getApplicationContext(), InventorySessionActivity.class);
                            intent.putExtra("room", room.getId());
                            startActivity(intent);
                        }
                    });
                } else {
                    roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Room room = (Room)parent.getAdapter().getItem(position);

                            Intent intent = new Intent(getApplicationContext(), EntryListActivity.class);
                            intent.putExtra("room", room.getId());
                            startActivity(intent);
                        }
                    });
                }
            } else {
                if(this.address != null){
                    Toast toast = Toast.makeText(getApplicationContext(), "No addresses found. Check your connection or try another room.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Connection error. Check your connection and try again.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                finish();
            }
        }
    }

    private class AddressItemAsyncDownloader extends AsyncTask<String, Void, Address>{
        private String server;
        private String token;
        private ProgressDialog progressDialog;

        public AddressItemAsyncDownloader(RoomListActivity activity){
            this.progressDialog = new ProgressDialog(activity);
            this.server = "";
            this.token = "";
        }

        @Override
        protected Address doInBackground(String... params) {
            this.server = params[0];
            this.token = params[1];

            return Address.getAddress(this.server, this.token, params[2]);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.setMessage("Fetching data.");
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(Address address) {
            if (address != null) {
                RoomAsyncDownloader downloader = new RoomAsyncDownloader(progressDialog, address);
                downloader.execute(this.server, this.token);
            } else {
                if(this.progressDialog.isShowing())
                    this.progressDialog.dismiss();

                Toast toast = Toast.makeText(getApplicationContext(), "Connection error. Check your connection and try again.", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        }
    }
}
