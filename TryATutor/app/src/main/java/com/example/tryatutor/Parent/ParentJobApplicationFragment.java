package com.example.tryatutor.Parent;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tryatutor.Adapters.JobApplicationAdapter;
import com.example.tryatutor.Database.InternetConnect;
import com.example.tryatutor.Database.JobApplicationDataHandler;
import com.example.tryatutor.Database.ParentDataHandler;
import com.example.tryatutor.Database.SharedPreferencesManager;
import com.example.tryatutor.Database.TutorDataHandler;
import com.example.tryatutor.Database.TutorNotificationDataHandler;
import com.example.tryatutor.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;


public class ParentJobApplicationFragment extends Fragment {

    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private JobApplicationAdapter adapter;
    private ArrayList<JobApplicationDataHandler> data = new ArrayList<JobApplicationDataHandler>();
    private ParentDataHandler parentData;
    boolean isCurrentlyRunning = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parent_job_application, container, false);

        // bind all elements
        linearLayout = view.findViewById(R.id.parentJobApplicationRecyclerViewLayout_id);
        recyclerView = view.findViewById(R.id.parentJobApplicationRecyclerView_id);

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        parentData = sharedPreferencesManager.getParentDataSharedPreferences();

        isCurrentlyRunning = true;
        if (!InternetConnect.isNetworkAvailable(getActivity())) {
            InternetConnect.showConnectivityErrorCloseApp(getActivity());
        }
        adapter = new JobApplicationAdapter(getActivity(), data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        fetchApplicationData();
        swipeToDelete();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isCurrentlyRunning = false;
    }

    private void fetchApplicationData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.JOB_APPLICATION_TABLE)).orderByChild(getResources().getString(R.string.JOB_APPLICATION_PARENT_ID)).equalTo(parentData.getuId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isCurrentlyRunning) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("datatest", snapshot.toString());

                        JobApplicationDataHandler temp_data = new JobApplicationDataHandler(snapshot.child(getResources().getString(R.string.JOB_APPLICATION_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_APPLICATION_JOB_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_APPLICATION_PARENT_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_APPLICATION_TUTOR_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_APPLICATION_TUTOR_NAME)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_APPLICATION_DESCRIPTION)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_APPLICATION_POST_DATE)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_APPLICATION_PARENT_NAME)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_APPLICATION_EXPIRE_DATE)).getValue(String.class));
                        data.add(temp_data);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void deleteData(String applicationId,String tutorId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.JOB_APPLICATION_TABLE)).orderByChild(getResources().getString(R.string.JOB_APPLICATION_ID)).equalTo(applicationId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // sending notification
        String notificationId = applicationId + new Date().getTime();
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.TUTOR_NOTIFICATION_TABLE));
        TutorNotificationDataHandler newNotification = new TutorNotificationDataHandler(notificationId, applicationId, "REJECTED", tutorId, parentData.getuId(), parentData.getName());
        reference2.child(notificationId).setValue(newNotification);
    }

    private void swipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition();
                final JobApplicationDataHandler item = data.get(position);
                data.remove(position);
                adapter.notifyItemRemoved(position);

                Snackbar snackbar = Snackbar
                        .make(linearLayout, "Item was removed from the list.", Snackbar.LENGTH_SHORT);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        data.add(position, item);
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(position);
                    }
                });

                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            deleteData(item.getApplicationId(),item.getTutorId());
                        }
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}