package com.example.tryatutor.Parent;

import android.content.Intent;
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

import com.example.tryatutor.Adapters.ParentJobBoardAdapter;
import com.example.tryatutor.Database.InternetConnect;
import com.example.tryatutor.Database.JobBoardDataHandler;
import com.example.tryatutor.Database.ParentDataHandler;
import com.example.tryatutor.Database.ParentNotificationDataHandler;
import com.example.tryatutor.Database.SharedPreferencesManager;
import com.example.tryatutor.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ParentJobBoardFragment extends Fragment {

    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ParentJobBoardAdapter adapter;
    private ArrayList<JobBoardDataHandler> data = new ArrayList<JobBoardDataHandler>();
    private ParentDataHandler parentData;
    boolean isCurrentlyRunning = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parent_job_board, container, false);

        // bind all elements
        linearLayout = view.findViewById(R.id.parentJobBoardRecyclerViewLayout_id);
        recyclerView = view.findViewById(R.id.parentJobBoardRecyclerView_id);
        fab = view.findViewById(R.id.parentJobBoardFab_id);

        isCurrentlyRunning = true;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        parentData = sharedPreferencesManager.getParentDataSharedPreferences();

        if (!InternetConnect.isNetworkAvailable(getActivity())) {
            InternetConnect.showConnectivityErrorCloseApp(getActivity());
        }
        fetchData();

        adapter = new ParentJobBoardAdapter(getActivity(), data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), PostNewJobActivity.class);
                startActivity(intent);
            }
        });
        swipeToDelete();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isCurrentlyRunning = false;
    }


    private void fetchData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        Query query = reference.child(getString(R.string.JOB_BOARD_TABLE)).orderByChild(getResources().getString(R.string.JOB_PARENT_ID)).equalTo(parentData.getuId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isCurrentlyRunning) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        JobBoardDataHandler jobBoardDataHandler = new JobBoardDataHandler(snapshot.child(getResources().getString(R.string.JOB_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_PARENT_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_INFORMATION)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_ADDRESS)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_CREATION_DATE)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_EXPIRATION_DATE)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_PARENT_NAME)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_SUBJECT)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.JOB_ADDRESS_LATITUDE)).getValue(Double.class),
                                snapshot.child(getResources().getString(R.string.JOB_ADDRESS_LONGITUDE)).getValue(Double.class));
                        data.add(jobBoardDataHandler);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void deleteData(String jobId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.JOB_BOARD_TABLE)).orderByChild(getResources().getString(R.string.JOB_ID)).equalTo(jobId);
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
                final JobBoardDataHandler item = data.get(position);
                data.remove(position);
                adapter.notifyItemRemoved(position);
                Snackbar snackbar = Snackbar.make(linearLayout, "Item was removed from the list.", Snackbar.LENGTH_SHORT);
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
                            deleteData(item.getJobId());
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
