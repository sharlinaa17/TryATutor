package com.example.tryatutor.Parent;

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

import com.example.tryatutor.Database.ParentDataHandler;
import com.example.tryatutor.Database.SharedPreferencesManager;
import com.example.tryatutor.LoginAndRegistration.LoginActivity;
import com.example.tryatutor.R;
import com.example.tryatutor.Shared.AboutUsActivity;
import com.example.tryatutor.Shared.ContactUsActivity;
import com.google.android.material.navigation.NavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class ParentHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView headerText,headerEmail;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ChipNavigationBar chipNavigationBar;
    private ParentDataHandler parentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home);

        // get parent data on enter
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(ParentHomeActivity.this);
        parentData = sharedPreferencesManager.getParentDataSharedPreferences();


        // binding the elements
        drawerLayout = findViewById(R.id.parentDrawerLayout_id);
        navigationView = findViewById(R.id.parentNavView_id);
        toolbar = findViewById(R.id.parentToolbar_id);
        chipNavigationBar = findViewById(R.id.parentBottomNavBar_id);

        //for header layout
        View headerLayout = navigationView.getHeaderView(0);
        headerText = headerLayout.findViewById(R.id.headerAccountName);
        headerEmail = headerLayout.findViewById(R.id.headerAccountEmail);
        headerText.setText(parentData.getName());
        headerEmail.setText(parentData.getEmail());

        // for toolbar
        toolbar.setTitleTextColor(getResources().getColor(R.color.app_grey));
        toolbar.setTitle("Try A Tutor");
        setSupportActionBar(toolbar);

        //for navigation drawer and listener
        Menu menu = navigationView.getMenu();
        parentDrawerListener();

        //for bottom nav bar
        parentBottomNavBar();

    }

    private void parentBottomNavBar() {

        getSupportFragmentManager().beginTransaction().replace(R.id.parentFragmentContainer_id, new ParentNotificationFragment()).commit();
        chipNavigationBar.setItemSelected(R.id.parentBottomNavNotification_id, true);

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {

                    case R.id.parentBottomNavNotification_id:
                        fragment = new ParentNotificationFragment();
                        break;
                    case R.id.parentBottomNavJobBoard_id:
                        fragment = new ParentJobBoardFragment();
                        break;
                    case R.id.parentBottomNavJobApplication_id:
                        fragment = new ParentJobApplicationFragment();
                        break;
                    case R.id.parentBottomNavFindTutor_id:
                        fragment = new ParentFindTutorFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.parentFragmentContainer_id, fragment).commit();
            }
        });

    }

    private void parentDrawerListener() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(ParentHomeActivity.this, drawerLayout, toolbar, R.string.parent_nav_drawer_open, R.string.parent_nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.parentNavDrawerEditProfile_id:
                        startActivity(new Intent(ParentHomeActivity.this, ParentEditProfileActivity.class));
                        break;
                    case R.id.parentNavDrawerConnectedTutor_id:
                        startActivity(new Intent(ParentHomeActivity.this, ConnectedTutor.class));
                        break;
                    case R.id.parentNavDrawerContactUs_id:
                        startActivity(new Intent(ParentHomeActivity.this, ContactUsActivity.class));
                        break;
                    case R.id.parentNavDrawerAboutUs_id:
                        startActivity(new Intent(ParentHomeActivity.this, AboutUsActivity.class));
                        break;
                    case R.id.parentNavDrawerLogOut_id:
                        logOut();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ParentHomeActivity.this);
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
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(ParentHomeActivity.this);
                        sharedPreferencesManager.logOutSharedPreferences();
                        Intent intent = new Intent(ParentHomeActivity.this, LoginActivity.class);
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