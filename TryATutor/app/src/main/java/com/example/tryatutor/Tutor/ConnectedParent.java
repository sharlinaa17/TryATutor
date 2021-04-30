package com.example.tryatutor.Tutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.tryatutor.Adapters.ConnectedParentAdapter;
import com.example.tryatutor.Adapters.ConnectedTutorAdapter;
import com.example.tryatutor.Database.ConnectionDataHandler;
import com.example.tryatutor.Database.InternetConnect;
import com.example.tryatutor.Database.ParentDataHandler;
import com.example.tryatutor.Database.SharedPreferencesManager;
import com.example.tryatutor.Database.TutorDataHandler;
import com.example.tryatutor.Parent.TutorDetailActivity;
import com.example.tryatutor.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConnectedParent extends AppCompatActivity {

    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private ConnectedParentAdapter adapter;
    private ArrayList<ConnectionDataHandler> data = new ArrayList<ConnectionDataHandler>();
    private TutorDataHandler tutorData;
    static boolean isActive = false;

    @Override
    protected void onStart() {
        super.onStart();
        ConnectedParent.isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        ConnectedParent.isActive = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_parent);

        linearLayout = findViewById(R.id.connectedParentLayout_id);
        recyclerView = findViewById(R.id.connectedParentRecyclerView_id);

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        tutorData = sharedPreferencesManager.getTutorDataSharedPreferences();

        adapter = new ConnectedParentAdapter(this, data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (!InternetConnect.isNetworkAvailable(this)) {
            InternetConnect.showConnectivityErrorCloseApp(this);
        }
        fetchData();
        swipeToDelete();

    }

    void deleteData(String connectionId)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.CONNECTION_TABLE)).orderByChild(getResources().getString(R.string.CONNECTION_ID)).equalTo(connectionId);
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
                final ConnectionDataHandler item = data.get(position);
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
                            deleteData(item.getConnectionId());
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

    private void fetchData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.CONNECTION_TABLE)).orderByChild(getResources().getString(R.string.CONNECTION_TUTOR_ID)).equalTo(tutorData.getuId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isActive) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        ConnectionDataHandler temp_data = new ConnectionDataHandler(snapshot.child(getResources().getString(R.string.CONNECTION_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.CONNECTION_PARENT_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.CONNECTION_TUTOR_ID)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.CONNECTION_PARENT_NAME)).getValue(String.class),
                                snapshot.child(getResources().getString(R.string.CONNECTION_TUTOR_NAME)).getValue(String.class));
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
}