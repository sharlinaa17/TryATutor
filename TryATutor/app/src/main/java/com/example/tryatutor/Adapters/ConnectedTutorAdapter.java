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

import com.example.tryatutor.Database.ConnectionDataHandler;
import com.example.tryatutor.Database.ParentNotificationDataHandler;
import com.example.tryatutor.Parent.TutorDetailActivity;
import com.example.tryatutor.R;

import java.util.ArrayList;

public class ConnectedTutorAdapter extends RecyclerView.Adapter<ConnectedTutorAdapter.MyViewHolder> {

    ArrayList<ConnectionDataHandler> data;
    Context context;
    public ConnectedTutorAdapter( Context context,ArrayList<ConnectionDataHandler> data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_connection, parent, false);
        return new ConnectedTutorAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {


        holder.textView.setText(data.get(position).getTutorName());
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

        LinearLayout linearLayout;
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.connectionRowLayout_id);
            textView = itemView.findViewById(R.id.connectionRow_id);
        }
    }


}
