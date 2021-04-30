package com.example.tryatutor.Parent;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.example.tryatutor.Adapters.TutorListAdapter;
import com.example.tryatutor.Database.AddressData;
import com.example.tryatutor.Database.InternetConnect;
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


public class ParentFindTutorFragment extends Fragment {

    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private TextInputEditText parentFindTutorAddress, parentFindTutorRange;
    private TextInputLayout parentFindTutorAddressLayout, parentFindTutorRangeLayout;
    private Button parentFindTutorButton;

    private AddressData addressData;
    private TutorListAdapter adapter;
    private ArrayList<TutorDataHandler> data = new ArrayList<TutorDataHandler>();
    private ArrayList<TutorDataHandler> allData = new ArrayList<TutorDataHandler>();

    boolean isCurrentlyRunning = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parent_find_tutor, container, false);

        // bind all elements
        linearLayout = view.findViewById(R.id.parentFindTutorRecyclerViewLayout_id);
        recyclerView = view.findViewById(R.id.parentFindTutorRecyclerView_id);
        parentFindTutorAddress = view.findViewById(R.id.parentFindTutorAddress_id);
        parentFindTutorRange = view.findViewById(R.id.parentFindTutorRange_id);
        parentFindTutorButton = view.findViewById(R.id.parentFindTutorButton_id);
        parentFindTutorAddressLayout = view.findViewById(R.id.parentFindTutorAddressLayout_id);
        parentFindTutorRangeLayout = view.findViewById(R.id.parentFindTutorRangeLayout_id);

        isCurrentlyRunning = true;


        parentFindTutorAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), GoogleMapActivity.class);
                intent.putExtra("senderType", "findTutor");
                startActivityForResult(intent, 1);
            }
        });

        if (!InternetConnect.isNetworkAvailable(getActivity())) {
            InternetConnect.showConnectivityErrorCloseApp(getActivity());
        }
        fetchTutorData();

        parentFindTutorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateData()) {
                    return;
                }
                getRequiredTutor();
            }
        });

        adapter = new TutorListAdapter(getActivity(), data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        isCurrentlyRunning = false;
    }


    private boolean validateData() {
        boolean flag = true;

        if (parentFindTutorRange.getText().toString().trim().isEmpty()) {
            parentFindTutorRangeLayout.setError(null);
            parentFindTutorRangeLayout.setErrorEnabled(false);
            parentFindTutorRangeLayout.setError("Field cannot be empty");
            flag = false;

        } else if (Integer.parseInt(parentFindTutorRange.getText().toString().trim()) > 10) {
            parentFindTutorRangeLayout.setError(null);
            parentFindTutorRangeLayout.setErrorEnabled(false);
            parentFindTutorRangeLayout.setError("Range is max 10 KM");
            flag = false;
        } else {
            parentFindTutorRangeLayout.setError(null);
            parentFindTutorRangeLayout.setErrorEnabled(false);
        }

        if (parentFindTutorAddress.getText().toString().trim().isEmpty()) {
            parentFindTutorAddressLayout.setError(null);
            parentFindTutorAddressLayout.setErrorEnabled(false);
            parentFindTutorAddressLayout.setError("Field cannot be empty");
            flag = false;
        } else {
            parentFindTutorAddressLayout.setError(null);
            parentFindTutorAddressLayout.setErrorEnabled(false);
        }
        return flag;
    }

    private void fetchTutorData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.TUTOR_TABLE)).orderByChild(getResources().getString(R.string.TUTOR_UID));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isCurrentlyRunning) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        TutorDataHandler temp_data = new TutorDataHandler(snapshot.child(getResources().getString(R.string.TUTOR_UID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_NAME)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_EMAIL)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_PHONE)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_PASSWORD)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_ADDRESS)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_ADDRESS_LATITUDE)).getValue(Double.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_ADDRESS_LONGITUDE)).getValue(Double.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_CURRENT_INSTITUTION)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_BIO)).getValue(String.class));
                        allData.add(temp_data);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getRequiredTutor() {
        data.clear();
        Double x = addressData.getLatitude();
        Double y = addressData.getLongitude();
        Double r = Double.parseDouble(parentFindTutorRange.getText().toString().trim());
        for (TutorDataHandler tutor : allData) {
            Double val = (x - tutor.getAddressLatitude()) * (x - tutor.getAddressLatitude()) + (y - tutor.getAddressLongitude()) * (y - tutor.getAddressLongitude());
            if (val <= r * r)
                data.add(tutor);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            addressData = (AddressData) data.getSerializableExtra("addressData");
            parentFindTutorAddress.setText(addressData.getAddress());
        }
    }
}