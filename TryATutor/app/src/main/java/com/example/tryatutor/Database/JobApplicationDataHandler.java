package com.example.tryatutor.Database;

public class JobApplicationDataHandler {
    private String applicationId, jobId, parentId, tutorId, tutorName, jobDescription, jobPostDate,parentName,jobExpireDate;


    public JobApplicationDataHandler(String applicationId, String jobId, String parentId, String tutorId, String tutorName, String jobDescription, String jobPostDate, String parentName, String jobExpireDate) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.parentId = parentId;
        this.tutorId = tutorId;
        this.tutorName = tutorName;
        this.jobDescription = jobDescription;
        this.jobPostDate = jobPostDate;
        this.parentName = parentName;
        this.jobExpireDate = jobExpireDate;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
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

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobPostDate() {
        return jobPostDate;
    }

    public void setJobPostDate(String jobPostDate) {
        this.jobPostDate = jobPostDate;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getJobExpireDate() {
        return jobExpireDate;
    }

    public void setJobExpireDate(String jobExpireDate) {
        this.jobExpireDate = jobExpireDate;
    }
}
