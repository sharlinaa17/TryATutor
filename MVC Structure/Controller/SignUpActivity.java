package com.example.tryatutor.LoginAndRegistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tryatutor.Database.InternetConnect;
import com.example.tryatutor.Database.ParentDataHandler;
import com.example.tryatutor.Database.SharedPreferencesManager;
import com.example.tryatutor.Database.TutorDataHandler;
import com.example.tryatutor.Database.User;
import com.example.tryatutor.Database.UserFactory;
import com.example.tryatutor.Parent.ParentHomeActivity;
import com.example.tryatutor.R;
import com.example.tryatutor.Tutor.TutorHomeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class SignUpActivity extends AppCompatActivity {

    private RelativeLayout progressBar;
    private TextInputEditText signUpName, signUpEmail, signUpPhone, signUpPassword, signUpConfirmPassword;
    private TextInputLayout signUpNameLayout, signUpEmailLayout, signUpPhoneLayout, signUpPasswordLayout, signUpConfirmPasswordLayout;
    private EditText pseudo_signUpPhone;
    private Button signUpButton,signInBackButton;
    private TextView signUpHead;
    private String accountType;
    private CountryCodePicker ccp;
    private boolean phone_focus_state = false;

    //private FirebaseDatabase rootNode;
    //private DatabaseReference reference;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // init progressBar
        progressBar = findViewById(R.id.progressBar_id);
        progressBar.setVisibility(View.INVISIBLE);

        // all EditText binding
        signUpName = findViewById(R.id.signUpName_id);
        signUpEmail = findViewById(R.id.signUpEmail_id);
        signUpPhone = findViewById(R.id.signUpPhone_id);
        signUpPassword = findViewById(R.id.signUpPassword_id);
        signUpConfirmPassword = findViewById(R.id.signUpConfirmPassword_id);
        pseudo_signUpPhone = findViewById(R.id.pseudo_signUpPhone_id);

        // all InputLayout Binding
        signUpNameLayout = findViewById(R.id.signUpNameLayout_id);
        signUpEmailLayout = findViewById(R.id.signUpEmailLayout_id);
        signUpPhoneLayout = findViewById(R.id.signUpPhoneLayout_id);
        signUpPasswordLayout = findViewById(R.id.signUpPasswordLayout_id);
        signUpConfirmPasswordLayout = findViewById(R.id.signUpConfirmPasswordLayout_id);

        // country code picker binding with phone EditText
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(pseudo_signUpPhone);

        // all Button Binding
        signUpButton = findViewById(R.id.signUpButton_id);
        signInBackButton = findViewById(R.id.signInBackButton_id);

        // all TextView binding
        signUpHead = findViewById(R.id.signUpHead_id);

        // get the account type from previous Intent selection ans set the SignUp Head
        accountType = getIntent().getStringExtra("accountType");
        signUpHead.setText(accountType + " Sign Up");

        // set phone number prefix
        // https://stackoverflow.com/questions/14195207/put-constant-text-inside-edittext-which-should-be-non-editable-android

        signUpPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                phone_focus_state = b;
                if (!phone_focus_state) {
                    if (signUpPhone.getText().toString().equals("+880 - ")) {
                        signUpPhone.setText("");
                    }
                }
            }
        });
        signUpPhone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (signUpPhone.getText().toString().trim().isEmpty())
                    signUpPhone.setText("+880 - ");
                Selection.setSelection(signUpPhone.getText(), signUpPhone.getText().length());
                signUpPhone.addTextChangedListener(new TextWatcher() {

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
                            signUpPhone.setText("+880 - ");
                            Selection.setSelection(signUpPhone.getText(), signUpPhone.getText().length());
                        }
                    }
                });
                return false;
            }
        });

        // set signInBack Button listener
        signInBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( SignUpActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // set signUp Button Listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new InternetConnect().isNetworkAvailable(SignUpActivity.this)) {
                    String name = signUpNameLayout.getEditText().getText().toString().trim();
                    String email = signUpEmailLayout.getEditText().getText().toString().trim();
                    String phone = signUpPhoneLayout.getEditText().getText().toString().trim();
                    String password = signUpPasswordLayout.getEditText().getText().toString();
                    String confirmPassword = signUpConfirmPasswordLayout.getEditText().getText().toString();

                    if (phone.startsWith("+880 - "))
                        phone = phone.substring("+880 - ".length());
                    if (phone.startsWith("+880 -"))
                        phone = phone.substring("+880 -".length());
                    pseudo_signUpPhone.setText(phone);

                    if (!isDataValid(name, email, phone, password, confirmPassword)) {
                        return;
                    }
                    checkPhoneNoAndSignUp(name,email,phone,password);

                } else {
                    new InternetConnect().showConnectivityError(SignUpActivity.this);
                }
            }
        });
    }

    // check all the fiels if they meet the requirements
    private boolean isDataValid(String name, String email, String phone, String password, String confirmPassword) {
        String CheckSpaces = "\\A\\w{1,20}\\z";
        String CheckEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String CheckPhone = "[0-9]+";
        boolean flag = true;

        // name validation
        if (name.isEmpty()) {
            signUpNameLayout.setError("Field can not be empty");
            flag = false;
        } else {
            signUpNameLayout.setError(null);
            signUpNameLayout.setErrorEnabled(false);
        }

        // email validation

        if (email.isEmpty()) {
            signUpEmailLayout.setError("Field can not be empty");
            flag = false;
        } else if (!email.matches(CheckEmail)) {
            signUpEmailLayout.setError("Invalid email");
            flag = false;
        } else {
            signUpEmailLayout.setError(null);
            signUpEmailLayout.setErrorEnabled(false);
        }

        // phone validation

        if (phone.isEmpty()) {
            signUpPhoneLayout.setError("Field can not be empty");
            flag = false;
        } else if (!ccp.isValidFullNumber()) {
            signUpPhoneLayout.setError("Enter a valid phone number");
            flag = false;
        } else {
            signUpPhoneLayout.setError(null);
            signUpPhoneLayout.setErrorEnabled(false);
        }

        //password validation

        if (password.isEmpty()) {
            signUpPasswordLayout.setError("Field can not be empty");
            flag = false;
        } else if (password.length() < 8) {
            signUpPasswordLayout.setError("Password should contain at least 8 characters");
            flag = false;
        } else {
            signUpPasswordLayout.setError(null);
            signUpPasswordLayout.setErrorEnabled(false);
        }

        //confirm password validation
        if (!confirmPassword.equals(password)) {
            signUpConfirmPasswordLayout.setError("Value doesn't match");
            flag = false;
        } else {
            signUpConfirmPasswordLayout.setError(null);
            signUpConfirmPasswordLayout.setErrorEnabled(false);
        }
        return flag;
    }


    // check if the phone number already exists in the database
    private void checkPhoneNoAndSignUp(final String name,final String email,final String phone,final String password) {

        progressBar.setVisibility(View.VISIBLE);
        Query checkUser = null;

        if (accountType.equals(getResources().getString(R.string.PARENT_TYPE_USER))) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.PARENT_TABLE));
            checkUser = reference.child(phone);
        } else if(accountType.equals(getResources().getString(R.string.TUTOR_TYPE_USER))){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.TUTOR_TABLE));
            checkUser = reference.child(phone);
        }
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    signUpPhoneLayout.setError("Account with this number already exists");
                } else {
                    signUpPhoneLayout.setError(null);
                    signUpPhoneLayout.setErrorEnabled(false);
                    if (accountType.equals(getResources().getString(R.string.PARENT_TYPE_USER))) {

                        //insert into database
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.PARENT_TABLE));
                        ParentDataHandler newParent = new ParentDataHandler(phone,name,email,phone,password,"NONE",-1.0,-1.0);
                        reference.child(phone).setValue(newParent);

                        //create shared preferences
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(SignUpActivity.this);
                        sharedPreferencesManager.createParentLoginSharedPreferences(phone,name,email,phone,password,"NONE",-1.0,-1.0);

                        // launch activity
                        Intent intent = new Intent(SignUpActivity.this, ParentHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        progressBar.setVisibility(View.INVISIBLE);
                        startActivity(intent);

                    } else if(accountType.equals(getResources().getString(R.string.TUTOR_TYPE_USER))){
                        //insert into database
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.TUTOR_TABLE));
                        TutorDataHandler newTutor = new TutorDataHandler(phone,name,email,phone,password,"NONE",-1.0,-1.0,"NONE","NONE");
                        reference.child(phone).setValue(newTutor);

                        //create shared preferences
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(SignUpActivity.this);
                        sharedPreferencesManager.createTutorLoginSharedPreferences(phone,name,email,phone,password,"NONE",-1.0,-1.0,"NONE","NONE");

                        // launch activity
                        Intent intent = new Intent(SignUpActivity.this, TutorHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        progressBar.setVisibility(View.INVISIBLE);
                        startActivity(intent);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Debug","Sign Up Activity : Firebase phone check error : "+ databaseError.toString());
            }
        });
    }
}
