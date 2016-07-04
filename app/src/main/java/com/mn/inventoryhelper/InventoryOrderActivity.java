package com.mn.inventoryhelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class InventoryOrderActivity extends AppCompatActivity {

    ListView inventoryOrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_order);

        inventoryOrderList = (ListView) findViewById(R.id.inventoryOrderList);
    }

    class InventoryOrderDownloader extends AsyncTask<String, Void, ArrayList<InventoryOrder>>{
        private ProgressDialog progressDialog;

        public InventoryOrderDownloader(InventoryOrderActivity activity){
            this.progressDialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.setMessage("Pobieranie danych");
            this.progressDialog.show();
        }

        @Override
        protected ArrayList<InventoryOrder> doInBackground(String... params) {
            return InventoryOrder.getInventoryOrders(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(ArrayList<InventoryOrder> inventoryOrders) {
            if(this.progressDialog.isShowing())
                this.progressDialog.dismiss();
            if(inventoryOrders != null){
                InventoryOrderAdapter adapter = new InventoryOrderAdapter(InventoryOrderActivity.this, inventoryOrders);
                inventoryOrderList.setAdapter(adapter);

                inventoryOrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        InventoryOrder order = (InventoryOrder) parent.getAdapter().getItem(position);

                        Intent intent = new Intent(getApplicationContext(), RoomListActivity.class);
                        intent.putExtra("inventory", true);
                        intent.putExtra("orderId", order.getId());
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
