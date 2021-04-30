package com.example.tryatutor.Tutor;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.tryatutor.Adapters.TutorJobBoardAdapter;
import com.example.tryatutor.Database.AddressData;
import com.example.tryatutor.Database.JobBoardDataHandler;
import com.example.tryatutor.Database.TutorDataHandler;
import com.example.tryatutor.R;
import com.example.tryatutor.Shared.GoogleMapActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class TutorJobBoardFragment extends Fragment {


    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private TextInputEditText tutorJobBoardAddress, tutorJobBoardRange;
    private TextInputLayout tutorJobBoardAddressLayout, tutorJobBoardRangeLayout;
    private Button tutorJobBoardFindButton;
    private AddressData addressData;


    private TutorJobBoardAdapter adapter;
    private ArrayList<JobBoardDataHandler> data = new ArrayList<JobBoardDataHandler>();
    private ArrayList<JobBoardDataHandler> allData = new ArrayList<JobBoardDataHandler>();
    boolean isCurrentlyRunning = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tutor_job_board, container, false);

        // bind all elements
        linearLayout = view.findViewById(R.id.linearLayout_id);
        recyclerView = view.findViewById(R.id.tutorJobBoardRecyclerView_id);
        tutorJobBoardAddress = view.findViewById(R.id.tutorJobBoardAddress_id);
        tutorJobBoardRange = view.findViewById(R.id.tutorJobBoardRange_id);
        tutorJobBoardAddressLayout = view.findViewById(R.id.tutorJobBoardAddressLayout_id);
        tutorJobBoardRangeLayout = view.findViewById(R.id.tutorJobBoardRangeLayout_id);
        tutorJobBoardFindButton = view.findViewById(R.id.tutorJobBoardFindJobButton_id);

        isCurrentlyRunning = true;
        fetchJobData();


        tutorJobBoardAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), GoogleMapActivity.class);
                intent.putExtra("senderType", "findJob");
                startActivityForResult(intent, 1);
            }
        });

        tutorJobBoardFindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateData()) {
                    return;
                }
                getRequiredJob();

            }
        });
        adapter = new TutorJobBoardAdapter(getActivity(),data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    private void getRequiredJob() {
        data.clear();
        Double x = addressData.getLatitude();
        Double y = addressData.getLongitude();
        Double r = Double.parseDouble(tutorJobBoardRange.getText().toString().trim());

        for (JobBoardDataHandler job : allData) {
            Double val = (x - job.getJobAddressLatitude()) * (x - job.getJobAddressLatitude()) + (y - job.getJobAddressLongitude()) * (y - job.getJobAddressLongitude());
            if (val <= r * r)
                data.add(job);
        }
        adapter.notifyDataSetChanged();
    }

    boolean validateData()
    {
        boolean flag = true;

        if (tutorJobBoardRange.getText().toString().trim().isEmpty()) {
            tutorJobBoardRangeLayout.setError(null);
            tutorJobBoardRangeLayout.setErrorEnabled(false);
            tutorJobBoardRangeLayout.setError("Field cannot be empty");
            flag = false;

        } else if (Integer.parseInt(tutorJobBoardRange.getText().toString().trim()) > 10) {
            tutorJobBoardRangeLayout.setError(null);
            tutorJobBoardRangeLayout.setErrorEnabled(false);
            tutorJobBoardRangeLayout.setError("Range is max 10 KM");
            flag = false;
        } else {
            tutorJobBoardRangeLayout.setError(null);
            tutorJobBoardRangeLayout.setErrorEnabled(false);
        }

        if ( tutorJobBoardAddress.getText().toString().trim().isEmpty()) {
            tutorJobBoardAddressLayout.setError(null);
            tutorJobBoardAddressLayout.setErrorEnabled(false);
            tutorJobBoardAddressLayout.setError("Field cannot be empty");
            flag = false;
        } else {
            tutorJobBoardAddressLayout.setError(null);
            tutorJobBoardAddressLayout.setErrorEnabled(false);
        }
        return flag;
    }

    private void fetchJobData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.JOB_BOARD_TABLE)).orderByChild(getResources().getString(R.string.JOB_ID));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isCurrentlyRunning) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        JobBoardDataHandler temp_data = new JobBoardDataHandler(snapshot.child(getResources().getString(R.string.JOB_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_PARENT_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_INFORMATION)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_ADDRESS)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_CREATION_DATE)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_EXPIRATION_DATE)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_PARENT_NAME)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_SUBJECT)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_ADDRESS_LATITUDE)).getValue(Double.class),
                                snapshot.child(getResources().getString(R.string.JOB_ADDRESS_LONGITUDE)).getValue(Double.class));
                        allData.add(temp_data);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isCurrentlyRunning = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            addressData = (AddressData) data.getSerializableExtra("addressData");
            tutorJobBoardAddress.setText(addressData.getAddress());
        }
    }

}