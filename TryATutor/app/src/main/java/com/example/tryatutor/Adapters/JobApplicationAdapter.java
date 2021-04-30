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

import com.example.tryatutor.Database.JobApplicationDataHandler;
import com.example.tryatutor.Parent.TutorDetailActivity;
import com.example.tryatutor.R;

import java.util.ArrayList;

public class JobApplicationAdapter extends RecyclerView.Adapter<JobApplicationAdapter.MyViewHolder> {

    ArrayList<JobApplicationDataHandler> data;
    Context context;

    public JobApplicationAdapter(Context context, ArrayList<JobApplicationDataHandler> data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_job_application, parent, false);
        return new JobApplicationAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.tutorName.setText(data.get(position).getTutorName());
        holder.jobPostDate.setText(data.get(position).getJobPostDate());
        holder.jobDesc.setText(data.get(position).getJobDescription());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TutorDetailActivity.class);
                intent.putExtra("tutorId",data.get(position).getTutorId());
                intent.putExtra("applicationId",data.get(position).getApplicationId());
                intent.putExtra("isApplication",true);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tutorName,jobPostDate,jobDesc,subject;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tutorName = itemView.findViewById(R.id.rowJobApplicationTutorName_id);
            jobPostDate = itemView.findViewById(R.id.rowJobApplicationJobDate_id);
            jobDesc = itemView.findViewById(R.id.rowJobApplicationJobDesc_id);
            linearLayout = itemView.findViewById(R.id.rowJobApplicationLayout_id);
            subject = itemView.findViewById(R.id.rowJobApplicationSubject_id);
        }
    }
}
