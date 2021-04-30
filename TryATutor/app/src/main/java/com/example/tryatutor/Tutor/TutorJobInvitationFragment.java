package com.example.tryatutor.Tutor;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.tryatutor.Adapters.TutorJobInvitationAdapter;
import com.example.tryatutor.Database.SharedPreferencesManager;
import com.example.tryatutor.Database.TutorDataHandler;
import com.example.tryatutor.Database.TutorInvitationHandler;
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

public class TutorJobInvitationFragment extends Fragment {

    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private TutorJobInvitationAdapter adapter;
    private TutorDataHandler tutorData;
    private ArrayList<TutorInvitationHandler> data = new ArrayList<TutorInvitationHandler>();
    boolean isCurrentlyRunning = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_tutor_job_invitation, container, false);

        // bind all elements
        linearLayout = view.findViewById(R.id.tutorJobInvitationRecyclerViewLayout_id);
        recyclerView = view.findViewById(R.id.tutorJobInvitationRecyclerView_id);
        SharedPreferencesManager sharedPreferencesManager=new SharedPreferencesManager(getActivity());
        tutorData = sharedPreferencesManager.getTutorDataSharedPreferences();

        isCurrentlyRunning = true;
        fetchData();

        adapter = new TutorJobInvitationAdapter(getActivity(),data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //implement swipe to delete

        return view;
    }

    private void fetchData() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.TUTOR_INVITATION_TABLE)).orderByChild(getResources().getString(R.string.TUTOR_INVITATION_TUTOR_ID)).equalTo(tutorData.getuId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isCurrentlyRunning) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        TutorInvitationHandler temp_data = new TutorInvitationHandler(snapshot.child(getResources().getString(R.string.TUTOR_INVITATION_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_INVITATION_PARENT_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_INVITATION_TUTOR_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_INVITATION_INFORMATION)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_INVITATION_PARENT_NAME)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_INVITATION_TUTOR_NAME)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.TUTOR_INVITATION_SUBJECT)).getValue(String.class));
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

    @Override
    public void onDetach() {
        super.onDetach();
        isCurrentlyRunning = false;
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
                final TutorInvitationHandler item = data.get(position);
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
                            deleteInvitation(item.getInvitationId());
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

    private void deleteInvitation(String invitationId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.TUTOR_INVITATION_TABLE)).orderByChild(getResources().getString(R.string.TUTOR_INVITATION_ID)).equalTo(invitationId);
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
}