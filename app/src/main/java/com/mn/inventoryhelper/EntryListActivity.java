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

public class EntryListActivity extends AppCompatActivity {

    ListView entryListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_list);

        entryListView = (ListView) findViewById(R.id.entryListView);

        SharedPreferences sharedPreferences = getSharedPreferences(InventoryHelperApplication.getPREFERENCES(), MODE_PRIVATE);
        InventoryHelperApplication application = (InventoryHelperApplication)getApplicationContext();

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            int id = extras.getInt("room");
            RoomItemAsyncDownloader downloader = new RoomItemAsyncDownloader(this);
            downloader.execute(sharedPreferences.getString("server",""), application.getToken(), Integer.toString(id));
        } else {
            finish();
        }
    }

    private class EntryAsyncDownloader extends AsyncTask<String, Void, ArrayList<Entry>>{
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
                EntryAdapter adapter = new EntryAdapter(EntryListActivity.this, entries);
                entryListView.setAdapter(adapter);

                entryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Entry entry = (Entry) parent.getAdapter().getItem(position);

                        Intent intent = new Intent(getApplicationContext(), EntryDetailsActivity.class);
                        intent.putExtra("idNumber", entry.getSigning());
                        startActivity(intent);
                        finish();
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

        public RoomItemAsyncDownloader(EntryListActivity activity){
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
