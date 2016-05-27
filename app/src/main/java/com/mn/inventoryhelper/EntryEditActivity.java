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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EntryEditActivity extends AppCompatActivity {

    TextView entryEditTitle, entryEditRoom, entryEditAddressLabel, entryEditRoomLabel;
    Spinner entryEditAddressSpinner, entryEditRoomSpinner;
    Button entryEditConfirmButton;
    String idNumber, server, token;
    Entry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_edit);

        entryEditTitle = (TextView) findViewById(R.id.entryEditTitle);
        entryEditRoom = (TextView) findViewById(R.id.entryEditRoom);
        entryEditAddressLabel = (TextView) findViewById(R.id.entryEditAddressLabel);
        entryEditRoomLabel = (TextView) findViewById(R.id.entryEditRoomLabel);

        entryEditAddressSpinner = (Spinner) findViewById(R.id.entryEditAddressSpinner);
        entryEditRoomSpinner = (Spinner) findViewById(R.id.entryEditRoomSpinner);

        entryEditConfirmButton = (Button) findViewById(R.id.entryEditConfirmButton);

        SharedPreferences sharedPreferences = getSharedPreferences(InventoryHelperApplication.getPREFERENCES(), MODE_PRIVATE);
        InventoryHelperApplication application = (InventoryHelperApplication)getApplicationContext();

        server = sharedPreferences.getString("server","");
        token = application.getToken();

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            idNumber = extras.getString("idNumber");

            entryEditTitle.setText(idNumber);
            entryEditRoom.setText(extras.getString("room"));

            AddressAsyncDownloader downloader = new AddressAsyncDownloader(this);
            downloader.execute(server, token);
        } else {
            finish();
        }
    }

    private class EntryAsyncUploader extends AsyncTask<String, Void, Boolean>{
        ProgressDialog progressDialog;
        Entry entry;

        public EntryAsyncUploader(EntryEditActivity activity, Entry entry){
            this.progressDialog = new ProgressDialog(activity);
            this.entry = entry;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return entry.editEntry(params[0], params[1]);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.setMessage("Sending data.");
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(this.progressDialog.isShowing())
                this.progressDialog.dismiss();
            if(aBoolean) {
                Toast toast = Toast.makeText(getApplicationContext(), "Edit successful.", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Error sending data. Check your connection and try again.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private class RoomAsyncDownloader extends AsyncTask<String, Void, ArrayList<Room>>{

        ProgressDialog progressDialog;
        Address address;

        public RoomAsyncDownloader(EntryEditActivity activity, Address address){
            this.progressDialog = new ProgressDialog(activity);
            this.address = address;
        }

        @Override
        protected ArrayList<Room> doInBackground(String... params) {
            String server = params[0];
            String token = params[1];

            if(address != null){
                return address.getRooms(server, token);
            } else {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Fetching data.");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<Room> rooms) {
            if(this.progressDialog.isShowing())
                this.progressDialog.dismiss();
            if(rooms != null){
                entryEditRoomLabel.setVisibility(View.VISIBLE);
                entryEditRoomSpinner.setVisibility(View.VISIBLE);

                RoomAdapter adapter = new RoomAdapter(getApplicationContext(), rooms);
                entryEditRoomSpinner.setAdapter(adapter);

                entryEditRoomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Room room = (Room) parent.getAdapter().getItem(position);

                        if(room != null){
                            entryEditConfirmButton.setVisibility(View.VISIBLE);
                            entry = new Entry(idNumber, "", "", room);

                            entryEditConfirmButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    System.out.println(entry.toJSON());

                                    EntryAsyncUploader uploader = new EntryAsyncUploader(EntryEditActivity.this, entry);
                                    uploader.execute(server, token);
                                }
                            });
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "No rooms found. Check connection or try another address.", Toast.LENGTH_SHORT);
                toast.show();
                entryEditRoomLabel.setVisibility(View.INVISIBLE);
                entryEditRoomSpinner.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class AddressAsyncDownloader extends AsyncTask<String ,Void ,ArrayList<Address>> {

        private ProgressDialog progressDialog;

        public AddressAsyncDownloader(EntryEditActivity activity){
            progressDialog = new ProgressDialog(activity);
        }

        @Override
        protected ArrayList<Address> doInBackground(String... params) {
            return Address.getAddresses(params[0], params[1]);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Fetching data.");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<Address> addresses) {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            if(addresses != null){
                final AddressAdapter adapter = new AddressAdapter(getApplicationContext(), addresses);

                entryEditAddressSpinner.setAdapter(adapter);

                entryEditAddressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Address address = (Address) parent.getAdapter().getItem(position);

                        if(address != null){
                            RoomAsyncDownloader downloader = new RoomAsyncDownloader(EntryEditActivity.this, address);
                            downloader.execute(server, token, Integer.toString(address.getId()));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Connection error. Check your connection and try again.", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        }
    }
}
