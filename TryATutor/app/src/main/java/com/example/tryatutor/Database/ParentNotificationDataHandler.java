package com.example.tryatutor.Database;

public class ParentNotificationDataHandler {

    private String notificationId,invitationId,notificationStatus,tutorId,parentId,tutorName;
    //status 1 - accepted job request. Go to tutor profile
    //status 2 - rejected job request. Go to tutor profile


    public ParentNotificationDataHandler(String notificationId, String invitationId, String notificationStatus, String tutorId, String parentId, String tutorName) {
        this.notificationId = notificationId;
        this.invitationId = invitationId;
        this.notificationStatus = notificationStatus;
        this.tutorId = tutorId;
        this.parentId = parentId;
        this.tutorName = tutorName;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
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

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }
}
