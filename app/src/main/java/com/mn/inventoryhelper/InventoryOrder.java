package com.mn.inventoryhelper;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class InventoryOrder {
    private int id;
    private Date dateOrdered;
    private boolean completed;

    public InventoryOrder() {
        this.id = 0;
        this.dateOrdered = new Date();
        this.completed = false;
    }

    public InventoryOrder(int id, Date dateOrdered, boolean completed) {
        this.id = id;
        this.dateOrdered = dateOrdered;
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateOrdered() {
        return dateOrdered;
    }

    public void setDateOrdered(Date dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public static InventoryOrder parseJSON(JSONObject jsonObject) throws JSONException{
        return new InventoryOrder(jsonObject.getInt("id"), new Date(jsonObject.getString("date_ordered")),
                jsonObject.getBoolean("completed"));
    }

    @Nullable
    public static ArrayList<InventoryOrder> getInventoryOrders(String server, String token){
        try {
            URL url = new URL(server + "api/order/");
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
                    ArrayList<InventoryOrder> inventoryOrders = new ArrayList<>();

                    JSONArray jsonArray = new JSONArray(jsonResponse);

                    for(int i = 0; i < jsonArray.length(); ++i){
                        inventoryOrders.add(InventoryOrder.parseJSON(jsonArray.getJSONObject(i)));
                    }

                    return inventoryOrders;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<Room> getRooms(String server, String token, int id){
        try {
            URL url = new URL(server + "api/order/" + id + "/rooms/");
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
                    ArrayList<Room> rooms = new ArrayList<>();

                    JSONArray jsonArray = new JSONArray(jsonResponse);

                    for(int i = 0; i < jsonArray.length(); ++i){
                        rooms.add(Room.parseJSON(jsonArray.getJSONObject(i)));
                    }

                    return rooms;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
