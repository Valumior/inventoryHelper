package com.mn.inventoryhelper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
    Room room;
    String server, token;
    ArrayList<String> readCodes, roomCodes;
    ArrayList<Entry> report, anomaly;

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
        roomCodes = new ArrayList<>();
        report = new ArrayList<>();
        anomaly = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            roomId = extras.getInt("room");
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

    private void prepareReport(){
        for (int i = 0; i < report.size(); ++i){
            if(readCodes.contains(report.get(i).getIdNumber())) {
                report.get(i).setInventoryStatus(Entry.InventoryStatus.PRESENT);
            }
        }

        ArrayList<String> extra = new ArrayList<>(readCodes);
        extra.removeAll(roomCodes);
        EntryScanCheckDownloader downloader = new EntryScanCheckDownloader(extra, this);
        downloader.execute(server, token);
    }

    private void fillList(){
        ArrayList<Entry> allEntries = new ArrayList<>();
        allEntries.addAll(report);
        allEntries.addAll(anomaly);
        EntryAdapter adapter = new EntryAdapter(InventorySessionActivity.this, allEntries);
        inventoryEntryListView.setAdapter(adapter);
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
                report = entries;
                for(int i = 0; i < report.size(); ++i){
                    report.get(i).setInventoryStatus(Entry.InventoryStatus.MISSING);
                    roomCodes.add(report.get(i).getIdNumber());
                }
                prepareReport();

                inventoryRoomLabel.setText(room.toString());

                inventoryEntryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                        final Entry entry = (Entry) parent.getAdapter().getItem(position);

                        AlertDialog.Builder builder = new AlertDialog.Builder(InventorySessionActivity.this);

                        switch (entry.getInventoryStatus()){
                            case MISSING:
                                builder.setTitle("Brakujący wpis.");
                                builder.setMessage("Nie potwierdzono obecności wpisu. Korekcja ręczna?");

                                builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        report.get(report.indexOf(entry)).setInventoryStatus(Entry.InventoryStatus.PRESENT);
                                        fillList();

                                        dialog.dismiss();
                                    }
                                });

                                builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                break;
                            case EXTRA:
                                builder.setTitle("Nadplanowy wpis.");
                                builder.setMessage("Wykryto nadplanowy wpis. Zignorować?");

                                builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        anomaly.remove(anomaly.indexOf(entry));
                                        readCodes.remove(entry.getIdNumber());

                                        fillList();
                                        dialog.dismiss();
                                    }
                                });

                                builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                break;
                        }
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
                InventorySessionActivity.this.room = room;
                EntryAsyncDownloader downloader = new EntryAsyncDownloader(this.progressDialog, room);
                downloader.execute(this.server, this.token);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Connection error. Check your connection and try again.", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        }
    }

    private class EntryScanCheckDownloader extends AsyncTask<String, Void, ArrayList<Entry>>{
        ProgressDialog progressDialog;
        ArrayList<String> scans;
        ArrayList<String> invalidScans;

        public EntryScanCheckDownloader(ArrayList<String> scans, InventorySessionActivity activity){
            this.scans = scans;
            this.invalidScans = new ArrayList<>();
            this.progressDialog = new ProgressDialog(activity);
        }

        @Override
        protected ArrayList<Entry> doInBackground(String... params) {
            ArrayList<Entry> entries = new ArrayList<>();

            for(int i = 0; i < scans.size(); ++i){
                Entry entry = Entry.getEntry(params[0], params[1], scans.get(i));
                if(entry != null){
                    entries.add(entry);
                } else {
                    invalidScans.add(scans.get(i));
                }
            }

            return entries;
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.setMessage("Analyzing scans.");
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> entries) {
            readCodes.removeAll(this.invalidScans);

            for(int i = 0; i < entries.size(); ++i){
                Entry entry = entries.get(i);
                Boolean exists = false;
                for(int j = 0; j < anomaly.size(); ++j){
                    if(anomaly.get(i).getIdNumber().equals(entry.getIdNumber())){
                        exists = true;
                        break;
                    }
                }
                if(!exists){
                    entry.setInventoryStatus(Entry.InventoryStatus.EXTRA);
                    anomaly.add(entry);
                }
            }

            fillList();
            if(this.progressDialog.isShowing()){
                this.progressDialog.dismiss();
            }
        }
    }
}
