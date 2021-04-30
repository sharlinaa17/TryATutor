package com.example.tryatutor.Database;

import android.content.Context;

import com.example.tryatutor.R;


public class UserFactory {

    public User createUser(String accountType, String name, String email, String phoneNo, String password, String address, Double addressLatitude, Double addressLongitude, Context context) {
        User user = null;
        if (accountType.equals(context.getResources().getString(R.string.PARENT_TYPE_USER))) {
            user = new ParentDataHandler(phoneNo,name, email, phoneNo, password,address,addressLatitude,addressLongitude);
        } else if (accountType.equals(context.getResources().getString(R.string.TUTOR_TYPE_USER))) {
            user = new TutorDataHandler(phoneNo,name, email, phoneNo, password,address,addressLatitude,addressLongitude,"NONE","NONE");
        }
        return user;
    }
}
