package com.example.tryatutor.LoginAndRegistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tryatutor.Database.InternetConnect;
import com.example.tryatutor.Database.ParentDataHandler;
import com.example.tryatutor.Database.SharedPreferencesManager;
import com.example.tryatutor.Database.TutorDataHandler;
import com.example.tryatutor.Parent.ParentHomeActivity;
import com.example.tryatutor.R;
import com.example.tryatutor.Tutor.TutorHomeActivity;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signUpButton, logInButton;
    private RelativeLayout progressBar;
    private TextInputEditText logInPhone, logInPassword;
    private TextInputLayout logInPhoneLayout, logInPasswordLayout;
    private EditText pseudo_logInPhone;
    private CountryCodePicker ccp;
    private boolean phone_focus_state = false;
    private RadioGroup radioGrp;
    private TextView radioSelectionTv;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Bind and set progressbar as invisible
        progressBar = findViewById(R.id.progressBar_id);
        progressBar.setVisibility(View.INVISIBLE);

        // Bind all button
        signUpButton = findViewById(R.id.signUpButton_id);
        logInButton = findViewById(R.id.loginButton_id);

        // bind all editText
        logInPhone = findViewById(R.id.logInPhone_id);
        logInPassword = findViewById(R.id.logInPassword_id);
        pseudo_logInPhone = findViewById(R.id.pseudo_logInPhone_id);

        // bind all TextInputLayouts
        logInPhoneLayout = findViewById(R.id.logInPhoneLayout_id);
        logInPasswordLayout = findViewById(R.id.logInPasswordLayout_id);

        // bind RadioGroup
        radioGrp = findViewById(R.id.loginRadioGroup_id);
        radioSelectionTv = findViewById(R.id.radioSelectionTextView_id);

        // set on click listeners for all buttons
        signUpButton.setOnClickListener(LoginActivity.this);
        logInButton.setOnClickListener(LoginActivity.this);

        // country code picker binding with phone EditText
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(pseudo_logInPhone);

        // set phone number prefix
        // https://stackoverflow.com/questions/14195207/put-constant-text-inside-edittext-which-should-be-non-editable-android

        logInPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                phone_focus_state = b;
                if (!phone_focus_state) {
                    if (logInPhone.getText().toString().equals("+880 - ")) {
                        logInPhone.setText("");
                    }
                }
            }
        });
        logInPhone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (logInPhone.getText().toString().trim().isEmpty())
                    logInPhone.setText("+880 - ");
                Selection.setSelection(logInPhone.getText(), logInPhone.getText().length());
                logInPhone.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (!phone_focus_state) {
                            return;
                        }
                        if (!editable.toString().startsWith("+880 - ")) {
                            logInPhone.setText("+880 - ");
                            Selection.setSelection(logInPhone.getText(), logInPhone.getText().length());
                        }
                    }
                });
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signUpButton_id) {
            startActivity(new Intent(LoginActivity.this, SignUpUserSelectionActivity.class));

        } else if (view.getId() == R.id.loginButton_id) {
            if(!InternetConnect.isNetworkAvailable(this))
            {
                InternetConnect.showConnectivityError(this);
                return;
            }
            String phone = logInPhoneLayout.getEditText().getText().toString().trim();
            String password = logInPasswordLayout.getEditText().getText().toString();

            // take the substring starting after the country code
            if (phone.startsWith("+880 - "))
                phone = phone.substring("+880 - ".length());
            if (phone.startsWith("+880 -"))
                phone = phone.substring("+880 -".length());
            pseudo_logInPhone.setText(phone);

            if (!validateData(phone, password))
                return;

            checkDataAndLogin(phone, password);
        }
    }

    private void checkDataAndLogin(String phone, final String password) {
        progressBar.setVisibility(View.VISIBLE);
        Query checkUser = null;
        if (radioGrp.getCheckedRadioButtonId() == R.id.parentRadioButton_id) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.PARENT_TABLE));
            checkUser = reference.child(phone);
        } else if (radioGrp.getCheckedRadioButtonId() == R.id.tutorRadioButton_id) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.TUTOR_TABLE));
            checkUser = reference.child(phone);
        }

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child(getResources().getString(R.string.PARENT_PASSWORD)).getValue(String.class).equals(password)) {
                        logInPhoneLayout.setError(null);
                        logInPhoneLayout.setErrorEnabled(false);

                        if (radioGrp.getCheckedRadioButtonId() == R.id.parentRadioButton_id) {

                            //create shared preferences
                            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(LoginActivity.this);
                            sharedPreferencesManager.createParentLoginSharedPreferences(dataSnapshot.child(getResources().getString(R.string.PARENT_UID)).getValue(String.class),
                                    dataSnapshot.child(getResources().getString(R.string.PARENT_NAME)).getValue(String.class),
                                    dataSnapshot.child(getResources().getString(R.string.PARENT_EMAIL)).getValue(String.class),
                                    dataSnapshot.child(getResources().getString(R.string.PARENT_PHONE)).getValue(String.class),
                                    dataSnapshot.child(getResources().getString(R.string.PARENT_PASSWORD)).getValue(String.class),
                                    dataSnapshot.child(getResources().getString(R.string.PARENT_ADDRESS)).getValue(String.class),
                                    dataSnapshot.child(getResources().getString(R.string.PARENT_ADDRESS_LATITUDE)).getValue(Double.class),
                                    dataSnapshot.child(getResources().getString(R.string.PARENT_ADDRESS_LONGITUDE)).getValue(Double.class));

                            Intent intent = new Intent(LoginActivity.this, ParentHomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            progressBar.setVisibility(View.INVISIBLE);
                            startActivity(intent);
                        } else if (radioGrp.getCheckedRadioButtonId() == R.id.tutorRadioButton_id) {

                            //create shared preferences
                            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(LoginActivity.this);
                            sharedPreferencesManager.createTutorLoginSharedPreferences(dataSnapshot.child(getResources().getString(R.string.TUTOR_UID)).getValue(String.class),
                                    dataSnapshot.child(getResources().getString(R.string.TUTOR_NAME)).getValue(String.class),
                                    dataSnapshot.child(getResources().getString(R.string.TUTOR_EMAIL)).getValue(String.class),
                                    dataSnapshot.child(getResources().getString(R.string.TUTOR_PHONE)).getValue(String.class),
                                    dataSnapshot.child(getResources().getString(R.string.TUTOR_PASSWORD)).getValue(String.class),
                                    dataSnapshot.child(getResources().getString(R.string.TUTOR_ADDRESS)).getValue(String.class),
                                    dataSnapshot.child(getResources().getString(R.string.TUTOR_ADDRESS_LATITUDE)).getValue(Double.class),
                                    dataSnapshot.child(getResources().getString(R.string.TUTOR_ADDRESS_LONGITUDE)).getValue(Double.class),
                                    dataSnapshot.child(getResources().getString(R.string.TUTOR_CURRENT_INSTITUTION)).getValue(String.class),
                                    dataSnapshot.child(getResources().getString(R.string.TUTOR_BIO)).getValue(String.class));
                            Intent intent = new Intent(LoginActivity.this, TutorHomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            progressBar.setVisibility(View.INVISIBLE);
                            startActivity(intent);
                        }
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        logInPhoneLayout.setError("Phone/Password does not match");
                    }
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    logInPhoneLayout.setError("No account found by this number");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Debug", "Log In Activity : Firebase phone check error : " + databaseError.toString());
            }
        });
    }

    @SuppressLint("ResourceType")
    private boolean validateData(String phone, String password) {
        boolean flag = true;

        // phone validation
        if (phone.isEmpty()) {
            logInPhoneLayout.setError("Field can not be empty");
            flag = false;
        } else if (!ccp.isValidFullNumber()) {
            logInPhoneLayout.setError("Enter a valid phone number");
            flag = false;
        } else {
            logInPhoneLayout.setError(null);
            logInPhoneLayout.setErrorEnabled(false);
        }

        // password validation

        if (password.isEmpty()) {
            logInPasswordLayout.setError("Field can not be empty");
            flag = false;
        } else {
            logInPasswordLayout.setError(null);
            logInPasswordLayout.setErrorEnabled(false);
        }

        // radio button validation
        if (radioGrp.getCheckedRadioButtonId() <= 0) {
            radioSelectionTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.alert_icon, 0, 0, 0);
            flag = false;
        } else {
            radioSelectionTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        return flag;
    }
}