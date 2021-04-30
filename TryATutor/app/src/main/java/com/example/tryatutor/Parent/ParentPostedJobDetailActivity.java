package com.example.tryatutor.Parent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.tryatutor.Database.JobBoardDataHandler;
import com.example.tryatutor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ParentPostedJobDetailActivity extends AppCompatActivity {

    private TextView postedBy, subject, address, information, creationDate, expirationDate;
    private String jobId;
    private JobBoardDataHandler jobData;
    static boolean isActive = false;

    @Override
    protected void onStart() {
        super.onStart();
        TutorDetailActivity.isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        TutorDetailActivity.isActive = false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_posted_job_detail);

        // bind all elements
        postedBy = findViewById(R.id.parentJobDetailPostedBy_id);
        subject = findViewById(R.id.parentJobDetailSubject_id);
        address = findViewById(R.id.parentJobDetailAddress_id);
        information = findViewById(R.id.parentJobDetailInformation_id);
        creationDate = findViewById(R.id.parentJobDetailCreationDate_id);
        expirationDate = findViewById(R.id.parentJobDetailExpirationDate_id);

        // get Job id
        jobId = getIntent().getStringExtra("jobId");
        loadData();
    }

    private void loadData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.JOB_BOARD_TABLE)).orderByChild(getResources().getString(R.string.JOB_ID)).equalTo(jobId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (ParentPostedJobDetailActivity.isActive) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("datatest", snapshot.toString());
                        jobData = new JobBoardDataHandler(snapshot.child(getResources().getString(R.string.JOB_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_PARENT_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_INFORMATION)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_ADDRESS)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_CREATION_DATE)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_EXPIRATION_DATE)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_PARENT_NAME)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_SUBJECT)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_ADDRESS_LATITUDE)).getValue(Double.class),
                                snapshot.child(getResources().getString(R.string.JOB_ADDRESS_LONGITUDE)).getValue(Double.class));
                    }
                    postedBy.setText(jobData.getParentName());
                    subject.setText(jobData.getSubject());
                    address.setText(jobData.getJobAddress());
                    information.setText(jobData.getJobInformation());
                    creationDate.setText(jobData.getJobCreationDate());
                    expirationDate.setText(jobData.getJobExpirationDate());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}