package com.example.tryatutor.Tutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Script;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tryatutor.Database.ConnectionDataHandler;
import com.example.tryatutor.Database.ParentNotificationDataHandler;
import com.example.tryatutor.Database.TutorInvitationHandler;
import com.example.tryatutor.Database.TutorNotificationDataHandler;
import com.example.tryatutor.Parent.ParentHomeActivity;
import com.example.tryatutor.Parent.TutorDetailActivity;
import com.example.tryatutor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class TutorInvitationDetailActivity extends AppCompatActivity {

    private TutorInvitationHandler inviteData;
    private TextView invitedBy,information,subject;
    private Button acceptButton,rejectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_invitation_detail);

        inviteData = (TutorInvitationHandler) getIntent().getSerializableExtra("inviteDetail");

        // bind all elements
        invitedBy = findViewById(R.id.tutorInvitationDetailInvitedBy_id);
        subject = findViewById(R.id.tutorInvitationDetailSubject_id);
        information = findViewById(R.id.tutorInvitationDetailInformation_id);
        acceptButton = findViewById(R.id.tutorInvitationDetailAcceptButton_id);
        rejectButton = findViewById(R.id.tutorInvitationDetailRejectButton_id);

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteInvitation();
                sendNotification("REJECTED");

                AlertDialog.Builder builder = new AlertDialog.Builder(TutorInvitationDetailActivity.this);
                builder.setTitle("Rejected Invitation");
                builder.setMessage("You rejected the invitation")
                        .setCancelable(false)
                        .setNeutralButton("To Dashboard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(TutorInvitationDetailActivity.this, TutorHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });
                builder.show();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteInvitation();
                sendNotification("ACCEPTED");
                createConnection();
                AlertDialog.Builder builder = new AlertDialog.Builder(TutorInvitationDetailActivity.this);
                builder.setTitle("Accepted Invitation");
                builder.setMessage("You accepted the invitation")
                        .setCancelable(false)
                        .setNeutralButton("To Dashboard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(TutorInvitationDetailActivity.this, TutorHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });
                builder.show();
            }
        });
    }

    private void createConnection() {
        String connectionId = inviteData.getParentId() + "-" + inviteData.getTutorId();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.CONNECTION_TABLE));
        ConnectionDataHandler newConnection = new ConnectionDataHandler(connectionId, inviteData.getParentId(), inviteData.getTutorId(),inviteData.getParentName(),inviteData.getTutorName());
        reference.child(connectionId).setValue(newConnection);
    }

    private void sendNotification(String notificationStatus) {
        String notificationId = inviteData.getInvitationId()+"-"+ new Date().getTime();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.PARENT_NOTIFICATION_TABLE));
        ParentNotificationDataHandler newNotification = new ParentNotificationDataHandler(notificationId, inviteData.getInvitationId(), notificationStatus, inviteData.getTutorId(), inviteData.getParentId(), inviteData.getTutorName());
        reference.child(notificationId).setValue(newNotification);
    }

    private void deleteInvitation() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.TUTOR_INVITATION_TABLE)).orderByChild(getResources().getString(R.string.TUTOR_INVITATION_ID)).equalTo(inviteData.getInvitationId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}