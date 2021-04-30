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
import com.example.tryatutor.Parent.ParentPostedJobDetailActivity;
import com.example.tryatutor.R;

import java.util.ArrayList;

public class ParentJobBoardAdapter extends RecyclerView.Adapter<ParentJobBoardAdapter.MyViewHolder> {

    ArrayList<JobBoardDataHandler> data;
    Context context;

    public ParentJobBoardAdapter(Context context, ArrayList<JobBoardDataHandler> data) {
        this.context = context;
        this.data = data;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_job_parent, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.creationDate.setText(data.get(position).getJobCreationDate());
        holder.expirationDate.setText(data.get(position).getJobExpirationDate());
        holder.subject.setText(data.get(position).getJobInformation());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ParentPostedJobDetailActivity.class);
                intent.putExtra("jobId",data.get(position).getJobId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView creationDate, expirationDate, subject;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            creationDate = itemView.findViewById(R.id.jobCreationDate_id);
            expirationDate = itemView.findViewById(R.id.jobExpirationDate_id);
            subject = itemView.findViewById(R.id.jobSubject_id);
            linearLayout = itemView.findViewById(R.id.rowJobParentLayout_id);
        }
    }
}
