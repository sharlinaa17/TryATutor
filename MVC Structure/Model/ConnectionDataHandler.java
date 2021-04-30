package com.example.tryatutor.Database;

public class ConnectionDataHandler {

    private String connectionId,parentId,tutorId,parentName,tutorName;

    public ConnectionDataHandler(String connectionId, String parentId, String tutorId, String parentName, String tutorName) {
        this.connectionId = connectionId;
        this.parentId = parentId;
        this.tutorId = tutorId;
        this.parentName = parentName;
        this.tutorName = tutorName;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
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
}
