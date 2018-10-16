package com.example.thai.dotify;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * the UserPermission object checks for permissions the user allowed the app to have
 */
public class UserPermission {

    public enum Permission{
        READ_PERMISSION
    }

    /**
     * Checks the permissions that the user allowed the app to have.
     * @param context Context
     * @param permission The permission to check
     * @return A boolean based on if permission has been granted.
     */
    public static boolean checkUserPermission(Context context, Permission permission) {
        switch(permission) {
            case READ_PERMISSION:{
                if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED){
                    return true;
                }
            }
            break;
        }
        return false;
    }
}
