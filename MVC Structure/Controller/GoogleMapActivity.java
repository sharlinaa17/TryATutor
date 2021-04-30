package com.example.tryatutor.Shared;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.tryatutor.Database.AddressData;
import com.example.tryatutor.Parent.ParentEditProfileActivity;
import com.example.tryatutor.Parent.ParentFindTutorFragment;
import com.example.tryatutor.Parent.PostNewJobActivity;
import com.example.tryatutor.R;
import com.example.tryatutor.Tutor.TutorEditProfileActivity;
import com.example.tryatutor.Tutor.TutorJobBoardFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LatLng current_location;
    private String current_address;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private Button selectButton, cancelButton;
    private String senderActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        selectButton = findViewById(R.id.googleMapSelectButton_id);
        cancelButton = findViewById(R.id.googleMapCancelButton_id);

        senderActivity = getIntent().getStringExtra("senderType");


        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddressData addressData = new AddressData(current_address,current_location.latitude,current_location.longitude);
                sendData(addressData);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddressData addressData = new AddressData(null,0,0);
                sendData(addressData);
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(GoogleMapActivity.this);
        createMapWithLastLocation();
    }

    void sendData(AddressData addressData)
    {
        Intent intent = null;
        if(senderActivity.equals("postJob"))
        {
            intent = new Intent(GoogleMapActivity.this, PostNewJobActivity.class);
        }
        else if(senderActivity.equals("findTutor"))
        {
            intent = new Intent(GoogleMapActivity.this, ParentFindTutorFragment.class);
        }
        else if(senderActivity.equals("findJob"))
        {
            intent = new Intent(GoogleMapActivity.this, TutorJobBoardFragment.class);
        }
        else if(senderActivity.equals("parentEdit"))
        {
            intent = new Intent(GoogleMapActivity.this, ParentEditProfileActivity.class);
        }
        else if(senderActivity.equals("tutorEdit"))
        {
            intent = new Intent(GoogleMapActivity.this, TutorEditProfileActivity.class);
        }
        intent.putExtra("addressData",addressData);
        setResult(1,intent);
        finish();
    }

    public void createMapWithLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    current_location = new LatLng(location.getLatitude(),location.getLongitude());
                    getAddress();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(GoogleMapActivity.this);
                }
            }
        });
    }



    @Override
    public void onMapReady(final GoogleMap googleMap) {

        //LatLng latLng = new LatLng(current_location.latitude, current_location.longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(current_location).title(current_address);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current_location, 15));
        googleMap.addMarker(markerOptions);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        googleMap.setMyLocationEnabled(true);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                current_location = latLng;
                getAddress();
                MarkerOptions _markerOptions = new MarkerOptions().position(current_location).title(current_address);
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current_location, 15));
                googleMap.addMarker(_markerOptions);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (REQUEST_CODE)
        {
            case REQUEST_CODE: if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                createMapWithLastLocation();
            }break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void getAddress()
    {
        Geocoder geocoder = new Geocoder(GoogleMapActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(current_location.latitude,current_location.longitude,1);
            Address obj = addresses.get(0);
            current_address = obj.getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}