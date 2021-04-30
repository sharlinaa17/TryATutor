package com.example.tryatutor.Database;

public class TutorNotificationDataHandler {

    private String notificationId,applicationId,notificationStatus,tutorId,parentId,parentName;
    //status 1 - accepted job request. Go to parent profile
    //status 2 - rejected job request. Go to parent profile

    public TutorNotificationDataHandler(String notificationId, String applicationId, String notificationStatus, String tutorId, String parentId, String parentName) {
        this.notificationId = notificationId;
        this.applicationId = applicationId;
        this.notificationStatus = notificationStatus;
        this.tutorId = tutorId;
        this.parentId = parentId;
        this.parentName = parentName;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
