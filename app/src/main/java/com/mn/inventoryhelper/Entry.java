package com.mn.inventoryhelper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class Entry {

    public enum InventoryStatus{
        UNLISTED,
        MISSING,
        PRESENT,
        EXTRA,
    }

    private String signing;
    private String name;
    private String description;
    private Room room;
    private InventoryStatus inventoryStatus;

    public Entry() {
        this.signing = "";
        this.name = "";
        this.description = "";
        this.room = new Room();
        this.inventoryStatus = InventoryStatus.UNLISTED;
    }

    public Entry(String signing, String name, String description, Room room) {
        this.signing = signing;
        this.name = name;
        this.description = description;
        this.room = room;
        this.inventoryStatus = InventoryStatus.UNLISTED;
    }

    public String getSigning() {
        return signing;
    }

    public void setSigning(String signing) {
        this.signing = signing;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public InventoryStatus getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(InventoryStatus inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public String getInventoryStatusString(){
        switch (this.inventoryStatus){
            case MISSING:
                return "M";
            case PRESENT:
                return "P";
            case EXTRA:
                return "E";
            default:
                return "";
        }
    }

    public static Entry parseJSON(JSONObject jsonObject) throws JSONException{
        return new Entry(jsonObject.getString("signing"), jsonObject.getString("name"),
                jsonObject.getString("description"), Room.parseJSON(jsonObject.getJSONObject("room")));
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();

        try {
            json.put("signing", this.signing);
            json.put("room", this.room.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public JSONObject toReportJSON(){
        JSONObject json = new JSONObject();

        try {
            json.put("entry", this.signing);
            json.put("status", this.getInventoryStatusString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @NonNull
    public static String URLifySigning(String signing){
        return signing.replace(" ", "_").replace("/", "__");
    }

    @NonNull
    public static String DeURLifySigning(String signing){
        return signing.replace("__", "/").replace("_", " ");
    }

    @Nullable
    public static ArrayList<Entry> getEntries(String server, String token){
        try {
            URL url = new URL(server + "api/entry/");
            String jsonResponse = "";

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Token " + token);
            connection.connect();

            if(connection.getResponseCode() == 200){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                String line;
                while ((line = reader.readLine()) != null){
                    jsonResponse += line;
                }

                reader.close();

                try {
                    ArrayList<Entry> entries = new ArrayList<>();

                    JSONArray jsonArray = new JSONArray(jsonResponse);

                    for(int i = 0; i < jsonArray.length(); ++i){
                        entries.add(Entry.parseJSON(jsonArray.getJSONObject(i)));
                    }

                    return entries;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static  Entry getEntry(String server, String token, String signing){
        try {
            URL url = new URL(server + "api/entry/" + URLifySigning(signing) + "/");
            String jsonResponse = "";

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Token " + token);
            connection.connect();

            if(connection.getResponseCode() == 200){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                String line;
                while ((line = reader.readLine()) != null){
                    jsonResponse += line;
                }

                reader.close();

                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    return Entry.parseJSON(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean editEntry(String server, String token){
        try {
            URL loginUrl = new URL(server + "api/entry/" + URLifySigning(this.signing) + "/");
            String json = this.toJSON().toString();

            HttpURLConnection connection = (HttpsURLConnection) loginUrl.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Token " + token);
            connection.connect();

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(json);
            writer.flush();
            writer.close();

            if (connection.getResponseCode() == 200) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
