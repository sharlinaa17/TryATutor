package com.example.tryatutor.Tutor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tryatutor.Database.AddressData;
import com.example.tryatutor.Database.SharedPreferencesManager;
import com.example.tryatutor.Database.TutorDataHandler;
import com.example.tryatutor.Parent.ParentEditProfileActivity;
import com.example.tryatutor.R;
import com.example.tryatutor.Shared.GoogleMapActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TutorEditProfileActivity extends AppCompatActivity {

    private TextInputLayout nameLayout, emailLayout, addressLayout, passwordLayout, confirmPasswordLayout, prevPasswordLayout, instituteLayout, bioLayout;
    private TextInputEditText nameEdit, emailEdit, addressEdit, passwordEdit, confirmPasswordEdit, prevPasswordEdit, instituteEdit, bioEdit;
    private Button saveChanges;
    private AddressData addressData;
    private TutorDataHandler tutorData;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_edit_profile);

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(TutorEditProfileActivity.this);
        tutorData = sharedPreferencesManager.getTutorDataSharedPreferences();
        if (tutorData.getAddress().equals("NONE"))
            tutorData.setAddress("");
        if (tutorData.getBio().equals("NONE"))
            tutorData.setBio("");
        if (tutorData.getCurrentInstitution().equals("NONE"))
            tutorData.setCurrentInstitution("");

        // bind all elements
        nameEdit = findViewById(R.id.tutorEditName_id);
        emailEdit = findViewById(R.id.tutorEditEmail_id);
        addressEdit = findViewById(R.id.tutorEditAddress_id);
        passwordEdit = findViewById(R.id.tutorEditPassword_id);
        confirmPasswordEdit = findViewById(R.id.tutorEditConfirmPassword_id);
        prevPasswordEdit = findViewById(R.id.tutorEditPrevPassword_id);
        instituteEdit = findViewById(R.id.tutorEditInstitute_id);
        bioEdit = findViewById(R.id.tutorEditBio_id);


        nameLayout = findViewById(R.id.tutorEditNameLayout_id);
        emailLayout = findViewById(R.id.tutorEditEmailLayout_id);
        addressLayout = findViewById(R.id.tutorEditAddressLayout_id);
        passwordLayout = findViewById(R.id.tutorEditPasswordLayout_id);
        confirmPasswordLayout = findViewById(R.id.tutorEditConfirmPasswordLayout_id);
        prevPasswordLayout = findViewById(R.id.tutorEditPrevPasswordLayout_id);
        instituteLayout = findViewById(R.id.tutorEditInstituteLayout_id);
        bioLayout = findViewById(R.id.tutorEditBioLayout_id);
        saveChanges = findViewById(R.id.saveChangesButton_id);

        nameEdit.setText(tutorData.getName());
        emailEdit.setText(tutorData.getEmail());
        addressEdit.setText(tutorData.getAddress());
        bioEdit.setText(tutorData.getBio());
        instituteEdit.setText(tutorData.getCurrentInstitution());

        addressEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TutorEditProfileActivity.this, GoogleMapActivity.class);
                intent.putExtra("senderType", "tutorEdit");
                startActivityForResult(intent, 1);
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChanged() == false) {
                    return;
                }

                if (!prevPasswordEdit.getText().toString().equals(tutorData.getPassword())) {
                    prevPasswordLayout.setError("Password did not match");
                    return;
                } else {
                    prevPasswordLayout.setError(null);
                    prevPasswordLayout.setErrorEnabled(false);
                }
                if (!validateData()) {
                    return;
                }

                getNewTutorData();

                //insert into database
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.TUTOR_TABLE));
                reference.child(tutorData.getuId()).setValue(tutorData);


                //create shared preferences
                SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(TutorEditProfileActivity.this);
                sharedPreferencesManager.logOutSharedPreferences();
                sharedPreferencesManager.createTutorLoginSharedPreferences(tutorData.getPhoneNo(),tutorData.getName(),tutorData.getEmail(),tutorData.getPhoneNo(),tutorData.getPassword(),tutorData.getAddress(),tutorData.getAddressLatitude(),tutorData.getAddressLongitude(),tutorData.getCurrentInstitution(),tutorData.getBio());

                Toast.makeText(TutorEditProfileActivity.this, "Your information has been changed successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getNewTutorData() {

        if (!nameEdit.getText().toString().trim().equals(tutorData.getName()))
            tutorData.setName(nameEdit.getText().toString().trim());
        if (!emailEdit.getText().toString().trim().equals(tutorData.getEmail()))
            tutorData.setEmail(emailEdit.getText().toString().trim());
        if (!passwordEdit.getText().toString().isEmpty())
            tutorData.setPassword(passwordEdit.getText().toString());
        if (!addressEdit.getText().toString().trim().equals(tutorData.getAddress())) {
            tutorData.setAddress(addressEdit.getText().toString().trim());
            tutorData.setAddressLatitude(addressData.getLatitude());
            tutorData.setAddressLongitude(addressData.getLongitude());
        }
        if(tutorData.getAddress().isEmpty())
            tutorData.setAddress("NONE");

        if (!instituteEdit.getText().toString().trim().equals(tutorData.getCurrentInstitution())) {
            tutorData.setCurrentInstitution(instituteEdit.getText().toString().trim());
        }
        if(tutorData.getCurrentInstitution().isEmpty())
            tutorData.setCurrentInstitution("NONE");

        if (!bioEdit.getText().toString().trim().equals(tutorData.getBio())) {
            tutorData.setBio(bioEdit.getText().toString().trim());
        }
        if(tutorData.getBio().isEmpty())
            tutorData.setBio("NONE");
    }

    private boolean validateData() {
        boolean flag = true;
        String CheckEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (nameEdit.getText().toString().trim().isEmpty()) {
            nameLayout.setError("Field can not be empty");
            flag = false;
        } else {
            nameLayout.setError(null);
            nameLayout.setErrorEnabled(false);
        }

        if (emailEdit.getText().toString().trim().isEmpty()) {
            emailLayout.setError("Field can not be empty");
            flag = false;
        } else if (!emailEdit.getText().toString().trim().matches(CheckEmail)) {
            emailLayout.setError("Invalid email");
            flag = false;
        } else {
            emailLayout.setError(null);
            emailLayout.setErrorEnabled(false);
        }

        if (prevPasswordEdit.getText().toString().trim().isEmpty()) {
            prevPasswordLayout.setError("Field can not be empty");
            flag = false;
        } else if (!prevPasswordEdit.getText().toString().equals(tutorData.getPassword())) {
            prevPasswordLayout.setError("Password did not match");
            flag = false;
        } else {
            prevPasswordLayout.setError(null);
            prevPasswordLayout.setErrorEnabled(false);
        }

        if (!passwordEdit.getText().toString().isEmpty() && !passwordEdit.getText().toString().equals(confirmPasswordEdit.getText().toString())) {
            flag = false;
            confirmPasswordLayout.setError("Both fields must match");
        } else {
            confirmPasswordLayout.setError(null);
            confirmPasswordLayout.setErrorEnabled(false);
        }

        return flag;
    }

    private boolean isChanged() {

        if (!nameEdit.getText().toString().trim().equals(tutorData.getName()) ||
                !emailEdit.getText().toString().trim().equals(tutorData.getEmail()) ||
                !addressEdit.getText().toString().trim().equals(tutorData.getAddress()) ||
                !instituteEdit.getText().toString().trim().equals(tutorData.getCurrentInstitution()) ||
                !bioEdit.getText().toString().trim().equals(tutorData.getBio()) ||
                !passwordEdit.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 1) {
            addressData = (AddressData) data.getSerializableExtra("addressData");
            addressEdit.setText(addressData.getAddress());
        }
    }
}