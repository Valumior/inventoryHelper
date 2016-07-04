package com.mn.inventoryhelper;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserPermissions {
    private boolean isAdmin;
    private boolean isSessionController;
    private boolean isEditAllowed;
    private boolean isAddAllowed;
    private boolean isUserManager;
    private boolean isInventory;

    public UserPermissions() {
        this.isAdmin = false;
        this.isSessionController = false;
        this.isEditAllowed = false;
        this.isAddAllowed = false;
        this.isUserManager = false;
        this.isInventory = false;
    }

    public UserPermissions(boolean isAdmin, boolean isSessionController, boolean isEditAllowed, boolean isAddAllowed, boolean isUserManager, boolean isInventory) {
        this.isAdmin = isAdmin;
        this.isSessionController = isSessionController;
        this.isEditAllowed = isEditAllowed;
        this.isAddAllowed = isAddAllowed;
        this.isUserManager = isUserManager;
        this.isInventory = isInventory;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isSessionController() {
        return isSessionController;
    }

    public void setSessionController(boolean sessionController) {
        isSessionController = sessionController;
    }

    public boolean isEditAllowed() {
        return isEditAllowed;
    }

    public void setEditAllowed(boolean editAllowed) {
        isEditAllowed = editAllowed;
    }

    public boolean isAddAllowed() {
        return isAddAllowed;
    }

    public void setAddAllowed(boolean addAllowed) {
        isAddAllowed = addAllowed;
    }

    public boolean isUserManager() {
        return isUserManager;
    }

    public void setUserManager(boolean userManager) {
        isUserManager = userManager;
    }

    public boolean isInventory() {
        return isInventory;
    }

    public void setInventory(boolean inventory) {
        isInventory = inventory;
    }

    public static UserPermissions parseJSON(JSONObject jsonObject) throws JSONException{
        return new UserPermissions(jsonObject.getBoolean("is_admin"), jsonObject.getBoolean("is_session_controller"),
                jsonObject.getBoolean("is_edit_allowed"), jsonObject.getBoolean("is_add_allowed"),
                jsonObject.getBoolean("is_user_manager"), jsonObject.getBoolean("is_inventory"));
    }

    @Nullable
    public static UserPermissions getUserPermissions(String server, String token){
        try {
            URL url = new URL(server + "api/permissions/");
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
                    return UserPermissions.parseJSON(jsonObject);
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
