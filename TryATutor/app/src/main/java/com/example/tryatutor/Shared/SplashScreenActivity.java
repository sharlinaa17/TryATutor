package com.example.tryatutor.Shared;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tryatutor.*;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tryatutor.Database.SharedPreferencesManager;
import com.example.tryatutor.Database.Variables;
import com.example.tryatutor.LoginAndRegistration.LoginActivity;
import com.example.tryatutor.Parent.ParentHomeActivity;
import com.example.tryatutor.Parent.PostNewJobActivity;
import com.example.tryatutor.Tutor.TutorHomeActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private Animation topAnim, bottomAnim;
    private ImageView image;
    private TextView appName, slogan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        // using animation in the splash screen
        topAnim = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.bottom_animation);
        image = findViewById(R.id.splashScreenImage_id);
        appName = findViewById(R.id.splashScreenTitle_id);
        slogan = findViewById(R.id.splashScreenSlogan_id);

        image.setAnimation(topAnim);
        appName.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(SplashScreenActivity.this);
                if (sharedPreferencesManager.checkLogin()) {
                    if (sharedPreferencesManager.getLoginAccount().equals(getResources().getString(R.string.PARENT_TYPE_USER))) {
                        Intent intent = new Intent(SplashScreenActivity.this, ParentHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else if (sharedPreferencesManager.getLoginAccount().equals(getResources().getString(R.string.TUTOR_TYPE_USER))) {
                        Intent intent = new Intent(SplashScreenActivity.this, TutorHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                finish();
            }
        }, Variables.SPLASH_SCREEN_TIME);
    }
}