package com.example.tryatutor.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tryatutor.Database.ParentNotificationDataHandler;
import com.example.tryatutor.Database.TutorNotificationDataHandler;
import com.example.tryatutor.Parent.TutorDetailActivity;
import com.example.tryatutor.R;

import java.util.ArrayList;

public class TutorNotificationAdapter extends RecyclerView.Adapter<TutorNotificationAdapter.MyViewHolder> {

    ArrayList<TutorNotificationDataHandler> data;
    Context context;

    public TutorNotificationAdapter( Context context,ArrayList<TutorNotificationDataHandler> data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_notification_tutor, parent, false);
        return new TutorNotificationAdapter.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        if(data.get(position).getNotificationStatus().equals("ACCEPTED"))
        {
            holder.notification.setText(data.get(position ).getParentName()+ " accepted your job request");
        }
        else if(data.get(position).getNotificationStatus().equals("REJECTED"))
        {
            holder.notification.setText(data.get(position).getParentName() + " rejected your job request");
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data.get(position).getNotificationStatus().equals("ACCEPTED"))
                {
                    Intent intent = new Intent(context, ParentDetailActivity.class);
                    intent.putExtra("parentId",data.get(position).getParentId());
                    context.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView notification;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            notification = itemView.findViewById(R.id.tutorNotificationText_id);
            linearLayout = itemView.findViewById(R.id.rowNotificationTutorLayout_id);
        }
    }
}
