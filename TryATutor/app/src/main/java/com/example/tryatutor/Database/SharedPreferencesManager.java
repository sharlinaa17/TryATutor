package com.example.tryatutor.Database;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.tryatutor.R;

public class SharedPreferencesManager {

    SharedPreferences userSP;
    SharedPreferences.Editor editor;
    Context context;

    private static final String LOGIN_STATE = "Login_State";
    private static final String ACCOUNT_TYPE = "Account_Type";


    public SharedPreferencesManager(Context context) {
        this.context = context;
        userSP = context.getSharedPreferences("userLoginSession", Context.MODE_PRIVATE);
        editor = userSP.edit();
    }

    public void createParentLoginSharedPreferences(String uId,String name, String email, String phoneNo, String password, String address, Double addressLatitude, Double addressLongitude) {
        editor.putBoolean(LOGIN_STATE, true);
        editor.putString(ACCOUNT_TYPE, context.getResources().getString(R.string.PARENT_TYPE_USER));

        editor.putString(context.getResources().getString(R.string.PARENT_NAME), name);
        editor.putString(context.getResources().getString(R.string.PARENT_EMAIL), email);
        editor.putString(context.getResources().getString(R.string.PARENT_PASSWORD), password);
        editor.putString(context.getResources().getString(R.string.PARENT_PHONE), phoneNo);
        editor.putString(context.getResources().getString(R.string.PARENT_ADDRESS), address);
        editor.putString(context.getResources().getString(R.string.PARENT_ADDRESS_LATITUDE), addressLatitude.toString());
        editor.putString(context.getResources().getString(R.string.PARENT_ADDRESS_LONGITUDE), addressLongitude.toString());
        editor.putString(context.getResources().getString(R.string.PARENT_UID), uId);

        editor.commit();
    }

    public void createTutorLoginSharedPreferences(String uId,String name, String email, String phoneNo, String password, String address, Double addressLatitude, Double addressLongitude, String currentInstitution, String bio) {
        editor.putBoolean(LOGIN_STATE, true);
        editor.putString(ACCOUNT_TYPE, context.getResources().getString(R.string.TUTOR_TYPE_USER));

        editor.putString(context.getResources().getString(R.string.TUTOR_NAME), name);
        editor.putString(context.getResources().getString(R.string.TUTOR_EMAIL), email);
        editor.putString(context.getResources().getString(R.string.TUTOR_PHONE), phoneNo);
        editor.putString(context.getResources().getString(R.string.TUTOR_PASSWORD), password);
        editor.putString(context.getResources().getString(R.string.TUTOR_ADDRESS), address);
        editor.putString(context.getResources().getString(R.string.TUTOR_ADDRESS_LATITUDE), addressLatitude.toString());
        editor.putString(context.getResources().getString(R.string.TUTOR_ADDRESS_LONGITUDE), addressLongitude.toString());
        editor.putString(context.getResources().getString(R.string.TUTOR_CURRENT_INSTITUTION), currentInstitution);
        editor.putString(context.getResources().getString(R.string.TUTOR_BIO), bio);
        editor.putString(context.getResources().getString(R.string.TUTOR_UID), uId);

        editor.commit();
    }

    public ParentDataHandler getParentDataSharedPreferences() {
        ParentDataHandler parent = new ParentDataHandler(userSP.getString(context.getResources().getString(R.string.PARENT_UID), null),
                userSP.getString(context.getResources().getString(R.string.PARENT_NAME), null),
                userSP.getString(context.getResources().getString(R.string.PARENT_EMAIL), null),
                userSP.getString(context.getResources().getString(R.string.PARENT_PHONE), null),
                userSP.getString(context.getResources().getString(R.string.PARENT_PASSWORD), null),
                userSP.getString(context.getResources().getString(R.string.PARENT_ADDRESS), null),
                Double.parseDouble(userSP.getString(context.getResources().getString(R.string.PARENT_ADDRESS_LATITUDE), "-1.0")),
                Double.parseDouble(userSP.getString(context.getResources().getString(R.string.PARENT_ADDRESS_LONGITUDE), "-1.0")));
        return parent;
    }

    public TutorDataHandler getTutorDataSharedPreferences() {
        TutorDataHandler tutor = new TutorDataHandler(userSP.getString(context.getResources().getString(R.string.TUTOR_UID), null),
                userSP.getString(context.getResources().getString(R.string.TUTOR_NAME), null),
                userSP.getString(context.getResources().getString(R.string.TUTOR_EMAIL), null),
                userSP.getString(context.getResources().getString(R.string.TUTOR_PHONE), null),
                userSP.getString(context.getResources().getString(R.string.TUTOR_PASSWORD), null),
                userSP.getString(context.getResources().getString(R.string.TUTOR_ADDRESS), null),
                Double.parseDouble(userSP.getString(context.getResources().getString(R.string.TUTOR_ADDRESS_LATITUDE), "-1.0")),
                Double.parseDouble(userSP.getString(context.getResources().getString(R.string.TUTOR_ADDRESS_LONGITUDE), "-1.0")),
                userSP.getString(context.getResources().getString(R.string.TUTOR_CURRENT_INSTITUTION), null),
                userSP.getString(context.getResources().getString(R.string.TUTOR_BIO), null));
        return tutor;
    }

    public boolean checkLogin() {
        if (userSP.getBoolean(LOGIN_STATE, false)) {
            return true;
        } else {
            return false;
        }
    }

    public String getLoginAccount() {
        return userSP.getString(ACCOUNT_TYPE, null);
    }

    public void logOutSharedPreferences() {
        editor.clear();
        editor.commit();
    }
}
