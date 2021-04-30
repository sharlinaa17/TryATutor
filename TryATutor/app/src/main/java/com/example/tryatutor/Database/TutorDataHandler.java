package com.example.tryatutor.Database;


public class TutorDataHandler extends User {

    private String currentInstitution,bio;


    public TutorDataHandler(String uId, String name, String email, String phoneNo, String password, String address, Double addressLatitude, Double addressLongitude,String currentInstitution,String bio) {
        super(uId, name, email, phoneNo, password, address, addressLatitude, addressLongitude);
        this.currentInstitution = currentInstitution;
        this.bio = bio;
    }


    public String getCurrentInstitution() {
        return currentInstitution;
    }

    public void setCurrentInstitution(String currentInstitution) {
        this.currentInstitution = currentInstitution;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
