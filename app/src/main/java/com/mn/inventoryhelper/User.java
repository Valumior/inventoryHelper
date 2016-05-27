package com.mn.inventoryhelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Valu on 2016-05-14.
 */
public class User {
    private String username;
    private String password;
    private String token;

    public User() {
        this.username = "";
        this.password = "";
        this.token = "";
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.token = "";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();

        try {
            json.put("username", this.username);
            json.put("password", this.password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public boolean login(String url){
        try {
            URL loginUrl = new URL(url + "api/login/");
            String json = this.toJSON().toString();
            String jsonResponse = "";

            HttpURLConnection connection = (HttpsURLConnection) loginUrl.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.addRequestProperty("Content-Type", "application/json");
            connection.connect();

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(json);
            writer.flush();
            writer.close();

            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                String line;
                while ((line = reader.readLine()) != null){
                    jsonResponse += line;
                }

                reader.close();
            }

            try {
                JSONObject response = new JSONObject(jsonResponse);
                if(response.has("token")){
                    this.token = response.getString("token");
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
