package com.mn.inventoryhelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class InventoryReport {
    private int orderId;
    private Room room;
    private ArrayList<Entry> entries;

    public InventoryReport() {
    }

    public InventoryReport(int orderId, Room room, ArrayList<Entry> entries) {
        this.orderId = orderId;
        this.room = room;
        this.entries = entries;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<Entry> entries) {
        this.entries = entries;
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();

        JSONArray array = new JSONArray();
        for (Entry entry : this.entries) {
            array.put(entry.toReportJSON());
        }

        try {
            json.put("order", this.orderId);
            json.put("room", this.room.getId());
            json.put("entries", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public boolean sendInventoryReport(String server, String token){
        try {
            URL loginUrl = new URL(server + "api/report/");
            String json = this.toJSON().toString();

            HttpURLConnection connection = (HttpsURLConnection) loginUrl.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Token " + token);
            connection.connect();

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(json);
            writer.flush();
            writer.close();

            if (connection.getResponseCode() == 201) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
