package com.example.tryatutor.Tutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tryatutor.Database.SharedPreferencesManager;
import com.example.tryatutor.Database.TutorDataHandler;
import com.example.tryatutor.LoginAndRegistration.LoginActivity;
import com.example.tryatutor.Parent.ParentEditProfileActivity;
import com.example.tryatutor.Parent.ParentFindTutorFragment;
import com.example.tryatutor.Parent.ParentHomeActivity;
import com.example.tryatutor.Parent.ParentJobApplicationFragment;
import com.example.tryatutor.Parent.ParentJobBoardFragment;
import com.example.tryatutor.Parent.ParentNotificationFragment;
import com.example.tryatutor.R;
import com.example.tryatutor.Shared.AboutUsActivity;
import com.example.tryatutor.Shared.ContactUsActivity;
import com.google.android.material.navigation.NavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class TutorHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView headerText,headerEmail;
    private Toolbar toolbar;
    private ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_home);

        // get tutor data on enter
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(TutorHomeActivity.this);
        TutorDataHandler tutorData = sharedPreferencesManager.getTutorDataSharedPreferences();

        // binding the elements
        drawerLayout = findViewById(R.id.tutorDrawerLayout_id);
        navigationView = findViewById(R.id.tutorNavView_id);
        toolbar = findViewById(R.id.tutorToolbar_id);
        chipNavigationBar = findViewById(R.id.tutorBottomNavBar_id);

        //for header layout
        View headerLayout = navigationView.getHeaderView(0);
        headerText = headerLayout.findViewById(R.id.headerAccountName);
        headerEmail = headerLayout.findViewById(R.id.headerAccountEmail);
        headerText.setText(tutorData.getName());
        headerEmail.setText(tutorData.getEmail());

        // for toolbar
        setSupportActionBar(toolbar);

        //for navigation drawer and listener
        Menu menu = navigationView.getMenu();
        tutorDrawerListener();

        //for bottom nav bar
        tutorBottomNavBar();

    }

    private void tutorBottomNavBar() {

        getSupportFragmentManager().beginTransaction().replace(R.id.tutorFragmentContainer_id, new TutorNotificationFragment()).commit();
        chipNavigationBar.setItemSelected(R.id.tutorBottomNavNotification_id, true);

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {

                    case R.id.tutorBottomNavNotification_id:
                        fragment = new TutorNotificationFragment();
                        break;
                    case R.id.tutorBottomNavJobBoard_id:
                        fragment = new TutorJobBoardFragment();
                        break;
                    case R.id.tutorBottomNavJobInvitation_id:
                        fragment = new TutorJobInvitationFragment();
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.tutorFragmentContainer_id, fragment).commit();
            }
        });

    }

    private void tutorDrawerListener() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(TutorHomeActivity.this, drawerLayout, toolbar, R.string.tutor_nav_drawer_open, R.string.tutor_nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tutorNavDrawerEditProfile_id:
                        startActivity(new Intent(TutorHomeActivity.this, TutorEditProfileActivity.class));
                        break;
                    case R.id.tutorNavDrawerConnectedParent_id:
                        startActivity(new Intent(TutorHomeActivity.this, ConnectedParent.class));
                        break;
                    case R.id.tutorNavDrawerContactUs_id:
                        startActivity(new Intent(TutorHomeActivity.this, ContactUsActivity.class));
                        break;
                    case R.id.tutorNavDrawerAboutUs_id:
                        startActivity(new Intent(TutorHomeActivity.this, AboutUsActivity.class));
                        break;
                    case R.id.tutorNavDrawerLogOut_id:
                        logOut();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TutorHomeActivity.this);
        builder.setTitle("Log Out");
        builder.setMessage("Do you want to log out?")
                .setCancelable(false)
                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setNegativeButton("Log Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(TutorHomeActivity.this);
                        sharedPreferencesManager.logOutSharedPreferences();
                        Intent intent = new Intent(TutorHomeActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}