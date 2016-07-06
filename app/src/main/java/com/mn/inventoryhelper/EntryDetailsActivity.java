package com.mn.inventoryhelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EntryDetailsActivity extends AppCompatActivity {

    TextView entryDetailsTitle, entryDetailsId, entryDetailsRoom, entryDetailsDescription;
    Button entryDetailsEditButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_details);

        entryDetailsTitle = (TextView) findViewById(R.id.entryDetailsTitle);
        entryDetailsId = (TextView) findViewById(R.id.entryDetailsId);
        entryDetailsRoom = (TextView) findViewById(R.id.entryDetailsRoom);
        entryDetailsDescription = (TextView) findViewById(R.id.entryDetailsDescription);

        entryDetailsEditButton = (Button) findViewById(R.id.entryDetailsEditButton);

        SharedPreferences sharedPreferences = getSharedPreferences(InventoryHelperApplication.getPREFERENCES(), MODE_PRIVATE);
        InventoryHelperApplication application = (InventoryHelperApplication)getApplicationContext();

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            String id = extras.getString("idNumber");
            EntryItemAsyncDownloader downloader = new EntryItemAsyncDownloader(this);
            downloader.execute(sharedPreferences.getString("server",""), application.getToken(), id);
        } else {
            finish();
        }
    }

    private class EntryItemAsyncDownloader extends AsyncTask<String, Void, Entry>{
        private ProgressDialog progressDialog;

        public  EntryItemAsyncDownloader(EntryDetailsActivity activity){
            this.progressDialog = new ProgressDialog(activity);
        }

        @Override
        protected Entry doInBackground(String... params) {
            return Entry.getEntry(params[0], params[1], params[2]);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.setMessage("Fetching data");
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(final Entry entry) {
            if(this.progressDialog.isShowing())
                this.progressDialog.dismiss();
            if(entry != null){
                entryDetailsTitle.setText(entry.getName());
                entryDetailsId.setText(entry.getSigning());
                entryDetailsRoom.setText(entry.getRoom().toString());
                entryDetailsDescription.setText(entry.getDescription());

                InventoryHelperApplication application = (InventoryHelperApplication) getApplicationContext();

                if(application.getUserPermissions().isAdmin() || application.getUserPermissions().isEditAllowed()) {
                    entryDetailsEditButton.setVisibility(View.VISIBLE);
                    entryDetailsEditButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), EntryEditActivity.class);
                            intent.putExtra("idNumber", entry.getSigning());
                            intent.putExtra("room", entry.getRoom().toString());
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Connection error. Check your connection and try again.", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        }
    }
}
