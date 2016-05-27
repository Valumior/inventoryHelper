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
public class Address {
    private int id;
    private String city;
    private String street;
    private String streetNumber;

    public Address() {
        this.id = 0;
        this.city = "";
        this.street = "";
        this.streetNumber = "";
    }

    public Address(int id, String city, String street, String streetNumber) {
        this.id = id;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public static Address parseJSON(JSONObject jsonObject) throws JSONException {
        return new Address(jsonObject.getInt("id"),jsonObject.getString("city"),jsonObject.getString("street"),jsonObject.getString("street_number"));
    }

    @Nullable
    public static ArrayList<Address> getAddresses(String server, String token){
        try {
            URL url = new URL(server + "api/address/");
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
                    ArrayList<Address> addresses = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(jsonResponse);

                    for(int i = 0; i < jsonArray.length(); ++i){
                        addresses.add(Address.parseJSON(jsonArray.getJSONObject(i)));
                    }
                    return addresses;
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
    public static Address getAddress(String server, String token, String id){
        try {
            URL url = new URL(server + "api/address/" + id + "/");
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

                    return Address.parseJSON(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Room> getRooms(String server, String token){
        try {
            URL url = new URL(server + "api/address/" + this.id + "/rooms/");
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

    @Override
    public String toString() {
        return this.city + " " + this.street + " " + this.streetNumber;
    }
}
