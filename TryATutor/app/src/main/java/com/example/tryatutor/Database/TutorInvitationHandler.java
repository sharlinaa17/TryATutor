package com.example.tryatutor.Database;

import java.io.Serializable;

public class TutorInvitationHandler implements Serializable {
    private String invitationId,parentId,tutorId,invitationInformation,parentName,tutorName,subject;

    public TutorInvitationHandler(String invitationId, String parentId, String tutorId, String invitationInformation, String parentName, String tutorName, String subject) {
        this.invitationId = invitationId;
        this.parentId = parentId;
        this.tutorId = tutorId;
        this.invitationInformation = invitationInformation;
        this.parentName = parentName;
        this.tutorName = tutorName;
        this.subject = subject;
    }

    public String getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getInvitationInformation() {
        return invitationInformation;
    }

    public void setInvitationInformation(String invitationInformation) {
        this.invitationInformation = invitationInformation;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
