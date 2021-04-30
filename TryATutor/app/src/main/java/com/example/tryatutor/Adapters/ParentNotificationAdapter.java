package com.example.tryatutor.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tryatutor.Database.ParentNotificationDataHandler;
import com.example.tryatutor.Parent.TutorDetailActivity;
import com.example.tryatutor.R;

import java.util.ArrayList;

public class ParentNotificationAdapter extends RecyclerView.Adapter<ParentNotificationAdapter.MyViewHolder> {

    ArrayList<ParentNotificationDataHandler> data;
    Context context;

    public ParentNotificationAdapter(Context context, ArrayList<ParentNotificationDataHandler> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_notification_parent, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        if(data.get(position).getNotificationStatus().equals("ACCEPTED"))
        {
            holder.notification.setText(data.get(position ).getTutorName()+ " accepted your job invitation");
        }
        else if(data.get(position).getNotificationStatus().equals("REJECTED"))
        {
            holder.notification.setText(data.get(position).getTutorName() + " rejected your job invitation");
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TutorDetailActivity.class);
                intent.putExtra("tutorId",data.get(position).getTutorId());
                intent.putExtra("applicationId","NONE");
                intent.putExtra("isApplication",false);
                context.startActivity(intent);
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

            notification = itemView.findViewById(R.id.parentNotificationText_id);
            linearLayout = itemView.findViewById(R.id.rowNotificationParentLayout_id);
        }
    }

}
