package com.example.tryatutor.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tryatutor.Database.TutorInvitationHandler;
import com.example.tryatutor.R;
import com.example.tryatutor.Tutor.TutorInvitationDetailActivity;

import java.util.ArrayList;

public class TutorJobInvitationAdapter extends RecyclerView.Adapter<TutorJobInvitationAdapter.MyViewHolder>{

    ArrayList<TutorInvitationHandler> data;
    Context context;

    public TutorJobInvitationAdapter(Context context,ArrayList<TutorInvitationHandler> data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_job_invitation, parent, false);
        return new TutorJobInvitationAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.jobInvitedBy.setText(data.get(position).getParentName());
        holder.jobSubject.setText(data.get(position).getSubject());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TutorInvitationDetailActivity.class);
                intent.putExtra("inviteDetail",data.get(position));
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView jobInvitedBy,jobSubject;
        LinearLayout linearLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            jobInvitedBy = itemView.findViewById(R.id.jobInvitedBy_id);
            jobSubject = itemView.findViewById(R.id.jobSubject_id);
            linearLayout = itemView.findViewById(R.id.rowJobInvitationLayout_id);

        }
    }
}
