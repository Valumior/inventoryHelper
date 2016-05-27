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

/**
 * Created by Valu on 2016-05-15.
 */
public class Room {
    private int id;
    private String roomId;
    private Address address;

    public Room() {
        this.id = 0;
        this.roomId = "";
        this.address = new Address();
    }

    public Room(int id, String roomId, Address address) {
        this.id = id;
        this.roomId = roomId;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public static Room parseJSON(JSONObject jsonObject) throws JSONException{
        return new Room(jsonObject.getInt("id"),jsonObject.getString("room_id"),Address.parseJSON(jsonObject.getJSONObject("address")));
    }

    @Nullable
    public static ArrayList<Room> getRooms(String server, String token){
        try {
            URL url = new URL(server + "api/room/");
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

    @Nullable
    public static Room getRoom(String server, String token, String id){
        try {
            URL url = new URL(server + "api/room/" + id + "/");
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
                    return Room.parseJSON(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Entry> getEntries(String server, String token){
        try {
            URL url = new URL(server + "api/room/" + this.id + "/entries/");
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

    @Override
    public String toString() {
        return this.roomId + ", " + this.address.toString();
    }
}
