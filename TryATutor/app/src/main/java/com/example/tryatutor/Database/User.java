package com.example.tryatutor.Database;

import java.io.Serializable;

public class User implements Serializable {

    private String uId,name,email,phoneNo,password,address;
    private Double addressLatitude,addressLongitude;

    public User(String uId, String name, String email, String phoneNo, String password, String address, Double addressLatitude, Double addressLongitude) {
        this.uId = uId;
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.password = password;
        this.address = address;
        this.addressLatitude = addressLatitude;
        this.addressLongitude = addressLongitude;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getAddressLatitude() {
        return addressLatitude;
    }

    public void setAddressLatitude(Double addressLatitude) {
        this.addressLatitude = addressLatitude;
    }

    public Double getAddressLongitude() {
        return addressLongitude;
    }

    public void setAddressLongitude(Double addressLongitude) {
        this.addressLongitude = addressLongitude;
    }
}
