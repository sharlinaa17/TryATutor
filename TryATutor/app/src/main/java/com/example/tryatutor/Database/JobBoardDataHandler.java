package com.example.tryatutor.Database;

import java.io.Serializable;

public class JobBoardDataHandler implements Serializable {
    private String jobId, parentId, jobInformation, jobAddress, jobCreationDate, jobExpirationDate, parentName,subject;
    private double jobAddressLatitude, jobAddressLongitude;

    public JobBoardDataHandler(String jobId, String parentId, String jobInformation, String jobAddress, String jobCreationDate, String jobExpirationDate, String parentName, String subject, double jobAddressLatitude, double jobAddressLongitude) {
        this.jobId = jobId;
        this.parentId = parentId;
        this.jobInformation = jobInformation;
        this.jobAddress = jobAddress;
        this.jobCreationDate = jobCreationDate;
        this.jobExpirationDate = jobExpirationDate;
        this.parentName = parentName;
        this.subject = subject;
        this.jobAddressLatitude = jobAddressLatitude;
        this.jobAddressLongitude = jobAddressLongitude;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getJobInformation() {
        return jobInformation;
    }

    public void setJobInformation(String jobInformation) {
        this.jobInformation = jobInformation;
    }

    public String getJobAddress() {
        return jobAddress;
    }

    public void setJobAddress(String jobAddress) {
        this.jobAddress = jobAddress;
    }

    public String getJobCreationDate() {
        return jobCreationDate;
    }

    public void setJobCreationDate(String jobCreationDate) {
        this.jobCreationDate = jobCreationDate;
    }

    public String getJobExpirationDate() {
        return jobExpirationDate;
    }

    public void setJobExpirationDate(String jobExpirationDate) {
        this.jobExpirationDate = jobExpirationDate;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public double getJobAddressLatitude() {
        return jobAddressLatitude;
    }

    public void setJobAddressLatitude(double jobAddressLatitude) {
        this.jobAddressLatitude = jobAddressLatitude;
    }

    public double getJobAddressLongitude() {
        return jobAddressLongitude;
    }

    public void setJobAddressLongitude(double jobAddressLongitude) {
        this.jobAddressLongitude = jobAddressLongitude;
    }
}
