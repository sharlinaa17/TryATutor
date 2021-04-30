package com.example.tryatutor.Parent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tryatutor.Database.ConnectionDataHandler;
import com.example.tryatutor.Database.InternetConnect;
import com.example.tryatutor.Database.ParentDataHandler;
import com.example.tryatutor.Database.SharedPreferencesManager;
import com.example.tryatutor.Database.TutorDataHandler;
import com.example.tryatutor.Database.TutorInvitationHandler;
import com.example.tryatutor.Database.TutorNotificationDataHandler;
import com.example.tryatutor.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class TutorDetailActivity extends AppCompatActivity {

    private ImageView image;
    private LinearLayout contactLinearLayout, applicationInteractLinearLayout;
    private TextView name, institution, bio, email, phone, address;
    private Button invitationButton;
    private Button acceptButton, rejectButton;
    private TutorDataHandler tutorData;
    private ParentDataHandler parentData;
    private String tutorId;
    private String applicationId;
    private boolean flag = false;
    private boolean isConnected = false;
    private boolean isApplication = false;
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
        setContentView(R.layout.activity_tutor_detail);

        //bind all elements
        image = findViewById(R.id.parentTutorDetailImage_id);
        contactLinearLayout = findViewById(R.id.parentTutorDetailContactLayout_id);
        applicationInteractLinearLayout = findViewById(R.id.applicationInteractLayout_id);
        name = findViewById(R.id.parentTutorDetailName_id);
        institution = findViewById(R.id.parentTutorDetailInstitute_id);
        bio = findViewById(R.id.parentTutorDetailBio_id);
        email = findViewById(R.id.parentTutorDetailEmail_id);
        phone = findViewById(R.id.parentTutorDetailPhone_id);
        address = findViewById(R.id.parentTutorDetailAddress_id);
        invitationButton = findViewById(R.id.sendJobInvitationButton_id);
        acceptButton = findViewById(R.id.acceptApplicationButton_id);
        rejectButton = findViewById(R.id.rejectApplicationButton_id);

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(TutorDetailActivity.this);
        parentData = sharedPreferencesManager.getParentDataSharedPreferences();

        invitationButton.setVisibility(View.GONE);
        contactLinearLayout.setVisibility(View.GONE);
        applicationInteractLinearLayout.setVisibility(View.GONE);

        //get tutor id from prev activity and also get all data
        tutorId = getIntent().getStringExtra("tutorId");
        applicationId = getIntent().getStringExtra("applicationId");
        isApplication = getIntent().getBooleanExtra("isApplication", false);

        if (!InternetConnect.isNetworkAvailable(TutorDetailActivity.this)) {
            InternetConnect.showConnectivityErrorCloseApp(TutorDetailActivity.this);
        }
        fetchTutorData();
        //search if this tutor is already connected
        getTutorConnection();

        invitationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = false;
                if (!prevInvitation())
                    sendInvitationDialogue();
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TutorDetailActivity.this);
                    builder.setTitle("Already Sent");
                    builder.setMessage("You already sent an invitation to this tutor")
                            .setCancelable(false)
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    builder.show();
                }
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteApplication();
                sendNotification("ACCEPTED");
                createConnection();

                AlertDialog.Builder builder = new AlertDialog.Builder(TutorDetailActivity.this);
                builder.setTitle("Accepted Application");
                builder.setMessage("You accepted the application")
                        .setCancelable(false)
                        .setNeutralButton("To Dashboard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(TutorDetailActivity.this, ParentHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });
                builder.show();
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteApplication();
                sendNotification("REJECTED");

                AlertDialog.Builder builder = new AlertDialog.Builder(TutorDetailActivity.this);
                builder.setTitle("Rejected Application");
                builder.setMessage("You rejected the application")
                        .setCancelable(false)
                        .setNeutralButton("To Dashboard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(TutorDetailActivity.this, ParentHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });
                builder.show();
            }
        });
    }

    private boolean prevInvitation() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.TUTOR_INVITATION_TABLE)).orderByChild(getResources().getString(R.string.TUTOR_INVITATION_TUTOR_ID)).equalTo(tutorId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (TutorDetailActivity.isActive) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TutorInvitationHandler temp_data = new TutorInvitationHandler(snapshot.child(getResources().getString(R.string.TUTOR_INVITATION_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_INVITATION_PARENT_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_INVITATION_TUTOR_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_INVITATION_INFORMATION)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_INVITATION_PARENT_NAME)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_INVITATION_TUTOR_NAME)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_INVITATION_SUBJECT)).getValue(String.class));
                        if (temp_data.getParentId().equals(parentData.getuId()))
                            flag = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return flag;
    }

    private void createConnection() {
        String connectionId = parentData.getuId() + "-" + tutorId;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.CONNECTION_TABLE));
        ConnectionDataHandler newConnection = new ConnectionDataHandler(connectionId, parentData.getuId(), tutorId,parentData.getName(),tutorData.getName());
        reference.child(connectionId).setValue(newConnection);
    }

    private void sendNotification(String notificationStatus) {
        String notificationId = applicationId + "-" + new Date().getTime();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.TUTOR_NOTIFICATION_TABLE));
        TutorNotificationDataHandler newNotification = new TutorNotificationDataHandler(notificationId, applicationId, notificationStatus, tutorId, parentData.getuId(), parentData.getName());
        reference.child(notificationId).setValue(newNotification);
    }

    private void deleteApplication() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.JOB_APPLICATION_TABLE)).orderByChild(getResources().getString(R.string.JOB_APPLICATION_ID)).equalTo(applicationId);
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

    private void getTutorConnection() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.CONNECTION_TABLE)).orderByChild(getResources().getString(R.string.CONNECTION_PARENT_ID)).equalTo(parentData.getuId());
        Log.d("debug", "connection test");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isActive) {
                    Log.d("debug", "connection test2");
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            ConnectionDataHandler temp_data = new ConnectionDataHandler(snapshot.child(getResources().getString(R.string.CONNECTION_ID)).getValue(String.class),
                                    snapshot.child(getResources().getString(R.string.CONNECTION_PARENT_ID)).getValue(String.class),
                                    snapshot.child(getResources().getString(R.string.CONNECTION_TUTOR_ID)).getValue(String.class),
                                    snapshot.child(getResources().getString(R.string.CONNECTION_PARENT_NAME)).getValue(String.class),
                                    snapshot.child(getResources().getString(R.string.CONNECTION_TUTOR_NAME)).getValue(String.class));
                            if (temp_data.getTutorId().equals(tutorId)) {
                                isConnected = true;
                            }
                        }
                        if (isConnected) {
                            invitationButton.setVisibility(View.GONE);
                            contactLinearLayout.setVisibility(View.VISIBLE);
                            applicationInteractLinearLayout.setVisibility(View.GONE);
                            Log.d("debug", "test 1");
                        } else {
                            if (isApplication) {
                                applicationInteractLinearLayout.setVisibility(View.VISIBLE);
                                contactLinearLayout.setVisibility(View.GONE);
                                invitationButton.setVisibility(View.GONE);
                                Log.d("debug", "test 2");
                            } else {
                                invitationButton.setVisibility(View.VISIBLE);
                                contactLinearLayout.setVisibility(View.GONE);
                                applicationInteractLinearLayout.setVisibility(View.GONE);
                                Log.d("debug", "test 3");
                            }
                        }
                    }
                    else
                    {
                        if (isApplication) {
                            applicationInteractLinearLayout.setVisibility(View.VISIBLE);
                            contactLinearLayout.setVisibility(View.GONE);
                            invitationButton.setVisibility(View.GONE);
                            Log.d("debug", "test 4");
                        } else {
                            invitationButton.setVisibility(View.VISIBLE);
                            contactLinearLayout.setVisibility(View.GONE);
                            applicationInteractLinearLayout.setVisibility(View.GONE);
                            Log.d("debug", "test 5");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendInvitationDialogue() {

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogue_send_invitation, null);

        final TextInputEditText informationEditText = dialogView.findViewById(R.id.sendInvitationDialogueInformation_id);
        final TextInputLayout informationTextInputLayout = dialogView.findViewById(R.id.sendInvitationDialogueInformationLayout_id);

        final TextInputEditText subjectEditText = dialogView.findViewById(R.id.sendInvitationDialogueSubject_id);
        final TextInputLayout subjectTextInputLayout = dialogView.findViewById(R.id.sendInvitationDialogueSubjectLayout_id);
        Button cancelBtn = dialogView.findViewById(R.id.sendInvitationDialogueCancelBtn_id);
        Button sendBtn = dialogView.findViewById(R.id.sendInvitationDialogueSendBtn_id);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = true;
                if (informationEditText.getText().toString().trim().isEmpty()) {
                    informationTextInputLayout.setError(null);
                    informationTextInputLayout.setErrorEnabled(false);
                    informationTextInputLayout.setError("Field can not be empty");
                    flag = false;
                } else {
                    informationTextInputLayout.setError(null);
                    informationTextInputLayout.setErrorEnabled(false);
                }
                if (subjectEditText.getText().toString().trim().isEmpty()) {
                    subjectTextInputLayout.setError(null);
                    subjectTextInputLayout.setErrorEnabled(false);
                    subjectTextInputLayout.setError("Field can not be empty");
                    flag = false;
                } else {
                    subjectTextInputLayout.setError(null);
                    subjectTextInputLayout.setErrorEnabled(false);
                }
                if (flag) {
                    sendInvitation(informationEditText.getText().toString(), subjectEditText.getText().toString());
                    Toast.makeText(TutorDetailActivity.this, "Your invitation was sent", Toast.LENGTH_SHORT).show();
                    dialogBuilder.dismiss();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        dialogBuilder.show();
    }

    private void sendInvitation(String invitationInformation, String subject) {
        String inviteId = parentData.getuId() + "-" + tutorData.getuId() + "-" + new Date().getTime();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.TUTOR_INVITATION_TABLE));
        TutorInvitationHandler newInvitation = new TutorInvitationHandler(inviteId, parentData.getuId(), tutorData.getuId(), invitationInformation, parentData.getName(), tutorData.getName(), subject);
        reference.child(inviteId).setValue(newInvitation);
    }

    private void fetchTutorData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.TUTOR_TABLE)).orderByChild(getResources().getString(R.string.TUTOR_UID)).equalTo(tutorId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (TutorDetailActivity.isActive) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        TutorDataHandler temp_data = new TutorDataHandler(snapshot.child(getResources().getString(R.string.TUTOR_UID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_NAME)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_EMAIL)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_PHONE)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_PASSWORD)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_ADDRESS)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_ADDRESS_LATITUDE)).getValue(Double.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_ADDRESS_LONGITUDE)).getValue(Double.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_CURRENT_INSTITUTION)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_BIO)).getValue(String.class));
                        tutorData = temp_data;
                        name.setText(tutorData.getName());
                        institution.setText(tutorData.getCurrentInstitution());
                        bio.setText(tutorData.getBio());
                        email.setText(tutorData.getEmail());
                        phone.setText(tutorData.getPhoneNo());
                        address.setText(tutorData.getAddress());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}