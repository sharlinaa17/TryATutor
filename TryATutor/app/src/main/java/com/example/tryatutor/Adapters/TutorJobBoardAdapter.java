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

import com.example.tryatutor.Database.JobBoardDataHandler;
import com.example.tryatutor.R;
import com.example.tryatutor.Tutor.TutorJobDetailActivity;

import java.net.Inet4Address;
import java.util.ArrayList;

public class TutorJobBoardAdapter  extends RecyclerView.Adapter<TutorJobBoardAdapter.MyViewHolder> {

    ArrayList<JobBoardDataHandler> data;
    Context context;

    public TutorJobBoardAdapter( Context context,ArrayList<JobBoardDataHandler> data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_job_tutor, parent, false);
        return new TutorJobBoardAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.jobPostedBy.setText(data.get(position).getParentName());
        holder.expirationDate.setText(data.get(position).getJobExpirationDate());
        holder.jobSubject.setText(data.get(position).getSubject());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TutorJobDetailActivity.class);
                intent.putExtra("jobData",data.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView jobPostedBy, expirationDate, jobSubject;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            jobPostedBy = itemView.findViewById(R.id.jobPostedBy_id);
            expirationDate = itemView.findViewById(R.id.jobExpirationDate_id);
            jobSubject = itemView.findViewById(R.id.jobSubject_id);
            linearLayout = itemView.findViewById(R.id.rowJobTutorLayout_id);

        }
    }
}
