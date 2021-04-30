package com.example.tryatutor.Parent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tryatutor.Database.AddressData;
import com.example.tryatutor.Database.ParentDataHandler;
import com.example.tryatutor.Database.SharedPreferencesManager;
import com.example.tryatutor.LoginAndRegistration.SignUpActivity;
import com.example.tryatutor.R;
import com.example.tryatutor.Shared.GoogleMapActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ParentEditProfileActivity extends AppCompatActivity {

    private TextInputLayout nameLayout, emailLayout, addressLayout, passwordLayout, confirmPasswordLayout, prevPasswordLayout;
    private TextInputEditText nameEdit, emailEdit, addressEdit, passwordEdit, confirmPasswordEdit, prevPasswordEdit;
    private Button saveChanges;
    private ParentDataHandler parentData;
    private AddressData addressData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_edit_profile);

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(ParentEditProfileActivity.this);
        parentData = sharedPreferencesManager.getParentDataSharedPreferences();
        if (parentData.getAddress().equals("NONE"))
            parentData.setAddress("");

        // bind all elements
        nameEdit = findViewById(R.id.parentEditName_id);
        emailEdit = findViewById(R.id.parentEditEmail_id);
        addressEdit = findViewById(R.id.parentEditAddress_id);
        passwordEdit = findViewById(R.id.parentEditPassword_id);
        confirmPasswordEdit = findViewById(R.id.parentEditConfirmPassword_id);
        prevPasswordEdit = findViewById(R.id.parentEditPrevPassword_id);

        nameLayout = findViewById(R.id.parentEditNameLayout_id);
        emailLayout = findViewById(R.id.parentEditEmailLayout_id);
        addressLayout = findViewById(R.id.parentEditAddressLayout_id);
        passwordLayout = findViewById(R.id.parentEditPasswordLayout_id);
        confirmPasswordLayout = findViewById(R.id.parentEditConfirmPasswordLayout_id);
        prevPasswordLayout = findViewById(R.id.parentEditPrevPasswordLayout_id);

        nameEdit.setText(parentData.getName());
        emailEdit.setText(parentData.getEmail());
        addressEdit.setText(parentData.getAddress());


        saveChanges = findViewById(R.id.saveChangesButton_id);

        addressEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ParentEditProfileActivity.this, GoogleMapActivity.class);
                intent.putExtra("senderType", "parentEdit");
                startActivityForResult(intent, 1);
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isChanged()) {
                    return;
                }
                if (!prevPasswordEdit.getText().toString().equals(parentData.getPassword())) {
                    prevPasswordLayout.setError("Password did not match");
                    return;
                } else {
                    prevPasswordLayout.setError(null);
                    prevPasswordLayout.setErrorEnabled(false);
                }
                if (!validateData()) {
                    return;
                }
                getNewParentData();

                //insert into database
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.PARENT_TABLE));
                reference.child(parentData.getuId()).setValue(parentData);

                //create shared preferences
                SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(ParentEditProfileActivity.this);
                sharedPreferencesManager.logOutSharedPreferences();
                sharedPreferencesManager.createParentLoginSharedPreferences(parentData.getPhoneNo(), parentData.getName(), parentData.getEmail(), parentData.getPhoneNo(), parentData.getPassword(), parentData.getAddress(), parentData.getAddressLatitude(), parentData.getAddressLongitude());

                Toast.makeText(ParentEditProfileActivity.this, "Your information has been changed successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getNewParentData() {

        if (!nameEdit.getText().toString().trim().equals(parentData.getName()))
            parentData.setName(nameEdit.getText().toString().trim());
        if (!emailEdit.getText().toString().trim().equals(parentData.getEmail()))
            parentData.setEmail(emailEdit.getText().toString().trim());
        if (!passwordEdit.getText().toString().isEmpty())
            parentData.setPassword(passwordEdit.getText().toString());
        if (!addressEdit.getText().toString().trim().equals(parentData.getAddress())) {
            parentData.setAddress(addressEdit.getText().toString().trim());
            parentData.setAddressLatitude(addressData.getLatitude());
            parentData.setAddressLongitude(addressData.getLongitude());
        }
        if(parentData.getAddress().isEmpty())
            parentData.setAddress("NONE");
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
        } else if (!prevPasswordEdit.getText().toString().equals(parentData.getPassword())) {
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

        if (!nameEdit.getText().toString().trim().equals(parentData.getName()) ||
                !emailEdit.getText().toString().trim().equals(parentData.getEmail()) ||
                !addressEdit.getText().toString().trim().equals(parentData.getAddress()) ||
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