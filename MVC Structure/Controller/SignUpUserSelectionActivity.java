package com.example.tryatutor.LoginAndRegistration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tryatutor.Database.User;
import com.example.tryatutor.Database.UserFactory;
import com.example.tryatutor.R;

public class SignUpUserSelectionActivity extends AppCompatActivity {

    private Button tutorSelectionBtutton;
    private Button parentSelectionBtutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_user_selection);

        tutorSelectionBtutton = findViewById(R.id.tutorSelectionButton_id);
        parentSelectionBtutton = findViewById(R.id.parentSelectionButton_id);

        tutorSelectionBtutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpUserSelectionActivity.this, SignUpActivity.class);
                intent.putExtra("accountType", getResources().getString(R.string.TUTOR_TYPE_USER));
                startActivity(intent);
            }
        });

        parentSelectionBtutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpUserSelectionActivity.this, SignUpActivity.class);
                intent.putExtra("accountType", getResources().getString(R.string.PARENT_TYPE_USER));
                startActivity(intent);
            }
        });
    }
}