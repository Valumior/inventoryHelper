package com.mn.inventoryhelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddressListActivity extends AppCompatActivity {

    ListView addressListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        addressListView = (ListView) findViewById(R.id.addressListView);

        SharedPreferences sharedPreferences = getSharedPreferences(InventoryHelperApplication.getPREFERENCES(), MODE_PRIVATE);
        InventoryHelperApplication application = (InventoryHelperApplication)getApplicationContext();

        AddressAsyncDownloader downloader = new AddressAsyncDownloader(this);
        downloader.execute(sharedPreferences.getString("server",""),application.getToken());

    }

    private class AddressAsyncDownloader extends AsyncTask<String ,Void ,ArrayList<Address>>{

        private ProgressDialog progressDialog;

        public AddressAsyncDownloader(AddressListActivity activity){
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
                AddressAdapter adapter = new AddressAdapter(AddressListActivity.this, addresses);
                addressListView.setAdapter(adapter);

                addressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Address address = (Address)parent.getAdapter().getItem(position);

                        Intent intent = new Intent(getApplicationContext(), RoomListActivity.class);
                        intent.putExtra("address", address.getId());
                        startActivity(intent);
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
