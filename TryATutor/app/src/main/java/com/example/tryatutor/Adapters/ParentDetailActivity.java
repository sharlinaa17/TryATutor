package com.example.tryatutor.Adapters;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tryatutor.Database.ParentDataHandler;
import com.example.tryatutor.Database.TutorDataHandler;
import com.example.tryatutor.Parent.TutorDetailActivity;
import com.example.tryatutor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ParentDetailActivity extends AppCompatActivity {

    private ImageView image;
    private TextView name,email,phone;
    private String parentId;
    static boolean isActive = false;
    private ParentDataHandler parentData;

    @Override
    protected void onStart() {
        super.onStart();
        ParentDetailActivity.isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        ParentDetailActivity.isActive = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_detail);

        //bind all elements
        image =  findViewById(R.id.tutorParentDetailImage_id);
        name =  findViewById(R.id.tutorParentDetailName_id);
        email =  findViewById(R.id.tutorParentDetailEmail_id);
        phone =  findViewById(R.id.tutorParentDetailPhone_id);
        parentId = getIntent().getStringExtra("parentId");

        fetchParentData();
    }

    private void fetchParentData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.PARENT_TABLE)).orderByChild(getResources().getString(R.string.PARENT_UID)).equalTo(parentId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (ParentDetailActivity.isActive) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ParentDataHandler temp_data = new ParentDataHandler( snapshot.child(getResources().getString(R.string.PARENT_UID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.PARENT_NAME)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.PARENT_EMAIL)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.PARENT_PHONE)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.PARENT_PASSWORD)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.PARENT_ADDRESS)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.PARENT_ADDRESS_LATITUDE)).getValue(Double.class),
                                snapshot.child(getResources().getString(R.string.PARENT_ADDRESS_LONGITUDE)).getValue(Double.class));
                        parentData = temp_data;
                        name.setText(parentData.getName());
                        email.setText(parentData.getEmail());
                        phone.setText(parentData.getPhoneNo());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}