package com.mn.inventoryhelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button loginButton, serverSettingsButton;
    TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button)findViewById(R.id.loginButton);
        serverSettingsButton = (Button)findViewById(R.id.serverSettingsButton);

        username = (EditText)findViewById(R.id.usernameEdit);
        password = (EditText)findViewById(R.id.passwordEdit);

        errorText = (TextView)findViewById(R.id.errorText);

        SharedPreferences sharedPreferences = getSharedPreferences(InventoryHelperApplication.getPREFERENCES(), Context.MODE_PRIVATE);
        username.setText(sharedPreferences.getString("username",""));

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                LoginAsync loginAsync = new LoginAsync(LoginActivity.this);
                loginAsync.execute(username.getText().toString(), password.getText().toString());
            }
        });

        serverSettingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ServerSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private class LoginAsync extends AsyncTask<String, String, Boolean>{

        private ProgressDialog progressDialog;

        public LoginAsync(LoginActivity activity){
            progressDialog = new ProgressDialog(activity);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            User user = new User(params[0], params[1]);

            SharedPreferences sharedPreferences = getSharedPreferences(InventoryHelperApplication.getPREFERENCES(), Context.MODE_PRIVATE);

            String server = sharedPreferences.getString("server", "");

            if(!server.isEmpty()){
                if(user.login(server)) {
                    final InventoryHelperApplication application = (InventoryHelperApplication) getApplicationContext();
                    application.setToken(user.getToken());

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username",user.getUsername());
                    editor.apply();

                    return true;
                }
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Connecting.");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else {
                errorText.setText("Login failed. Check server address and login credentials.");
                errorText.setTextColor(Color.RED);
                errorText.setVisibility(View.VISIBLE);
            }
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }
}
