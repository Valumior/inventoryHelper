package com.mn.inventoryhelper;

import android.app.Application;

/**
 * Created by Valu on 2016-05-14.
 */
public class InventoryHelperApplication extends Application {

    private String token;
    private UserPermissions userPermissions;
    private static final String PREFERENCES = "InventoryPreferences";

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserPermissions getUserPermissions() {
        return userPermissions;
    }

    public void setUserPermissions(UserPermissions userPermissions) {
        this.userPermissions = userPermissions;
    }

    public static String getPREFERENCES() {
        return PREFERENCES;
    }
}
