package com.example.tryatutor.Tutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tryatutor.Database.ConnectionDataHandler;
import com.example.tryatutor.Database.JobApplicationDataHandler;
import com.example.tryatutor.Database.JobBoardDataHandler;
import com.example.tryatutor.Database.ParentDataHandler;
import com.example.tryatutor.Database.SharedPreferencesManager;
import com.example.tryatutor.Database.TutorDataHandler;
import com.example.tryatutor.Parent.ParentHomeActivity;
import com.example.tryatutor.Parent.TutorDetailActivity;
import com.example.tryatutor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TutorJobDetailActivity extends AppCompatActivity {

    private JobBoardDataHandler jobData;
    private Button button;
    private TextView postedBy, subject, address, information, creationDate, expirationDate, alreadyConnectedText;
    //ArrayList<JobApplicationDataHandler> data = new ArrayList<JobApplicationDataHandler>();
    private TutorDataHandler tutorData;
    private boolean alreadySentApplication = false;
    private boolean isConnected = false;
    static boolean isActive = false;

    @Override
    protected void onStart() {
        super.onStart();
        TutorJobDetailActivity.isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        TutorJobDetailActivity.isActive = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_job_detail);

        jobData = (JobBoardDataHandler) getIntent().getSerializableExtra("jobData");

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(TutorJobDetailActivity.this);
        tutorData = sharedPreferencesManager.getTutorDataSharedPreferences();

        //bind all elements
        button = findViewById(R.id.tutorSendApplicationButton_id);
        postedBy = findViewById(R.id.tutorJobDetailPostedBy_id);
        subject = findViewById(R.id.tutorJobDetailSubject_id);
        address = findViewById(R.id.tutorJobDetailAddress_id);
        information = findViewById(R.id.tutorJobDetailInformation_id);
        creationDate = findViewById(R.id.tutorJobDetailCreationDate_id);
        expirationDate = findViewById(R.id.tutorJobDetailExpirationDate_id);
        alreadyConnectedText = findViewById(R.id.alreadyConnected_id);

        alreadyConnectedText.setVisibility(View.GONE);
        button.setVisibility(View.GONE);

        getTutorConnection();

        postedBy.setText(jobData.getParentName());
        subject.setText(jobData.getSubject());
        address.setText(jobData.getJobAddress());
        information.setText(jobData.getJobInformation());
        creationDate.setText(jobData.getJobCreationDate());
        expirationDate.setText(jobData.getJobExpirationDate());


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fetchUserSentApplications()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TutorJobDetailActivity.this);
                    builder.setTitle("Application already Sent");
                    builder.setMessage("You already sent an application for this job")
                            .setCancelable(false)
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TutorJobDetailActivity.this);
                    builder.setTitle("Confirmation");
                    builder.setMessage("Confirm sending application?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    sendJobApplication();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    private void getTutorConnection() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.CONNECTION_TABLE)).orderByChild(getResources().getString(R.string.CONNECTION_TUTOR_ID)).equalTo(tutorData.getuId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isActive) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        ConnectionDataHandler temp_data = new ConnectionDataHandler(snapshot.child(getResources().getString(R.string.CONNECTION_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.CONNECTION_PARENT_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.CONNECTION_TUTOR_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.CONNECTION_PARENT_NAME)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.CONNECTION_TUTOR_NAME)).getValue(String.class));

                        if (temp_data.getParentId().equals(jobData.getParentId())) {
                            isConnected = true;
                        }
                    }
                    if (isConnected) {
                        alreadyConnectedText.setVisibility(View.VISIBLE);
                        button.setVisibility(View.GONE);
                    }
                    else
                    {
                        button.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendJobApplication() {
        String applicationId = jobData.getParentId() + "-" + tutorData.getuId() + "-" + jobData.getJobId();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.JOB_APPLICATION_TABLE));
        JobApplicationDataHandler newApplication = new JobApplicationDataHandler(applicationId, jobData.getJobId(), jobData.getParentId(), tutorData.getuId(), tutorData.getName(), jobData.getJobInformation(),
                jobData.getJobCreationDate(), jobData.getParentName(), jobData.getJobExpirationDate());
        reference.child(applicationId).setValue(newApplication);

        AlertDialog.Builder builder = new AlertDialog.Builder(TutorJobDetailActivity.this);
        builder.setTitle("Application Sent");
        builder.setMessage("Application was successfully sent")
                .setCancelable(false)
                .setNeutralButton("To Dashboard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(TutorJobDetailActivity.this, TutorHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
        builder.show();
    }

    boolean fetchUserSentApplications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.JOB_APPLICATION_TABLE)).orderByChild(getResources().getString(R.string.JOB_APPLICATION_TUTOR_ID)).equalTo(tutorData.getuId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (TutorJobDetailActivity.isActive) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        JobApplicationDataHandler temp_data = new JobApplicationDataHandler(snapshot.child(getResources().getString(R.string.JOB_APPLICATION_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_APPLICATION_JOB_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_APPLICATION_PARENT_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_APPLICATION_TUTOR_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_APPLICATION_TUTOR_NAME)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_APPLICATION_DESCRIPTION)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_APPLICATION_POST_DATE)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_APPLICATION_PARENT_NAME)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_APPLICATION_EXPIRE_DATE)).getValue(String.class));
                        if (temp_data.getJobId().equals(jobData.getJobId())) {
                            alreadySentApplication = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return alreadySentApplication;

    }

}