package com.example.tryatutor.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tryatutor.Database.TutorDataHandler;
import com.example.tryatutor.Parent.TutorDetailActivity;
import com.example.tryatutor.R;

import java.util.ArrayList;

public class TutorListAdapter extends RecyclerView.Adapter<TutorListAdapter.MyViewHolder> {

    Context context;
    ArrayList<TutorDataHandler> data;

    public TutorListAdapter(Context context, ArrayList<TutorDataHandler> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_parent_tutor_list, parent, false);
        return new TutorListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.teacherName.setText(data.get(position).getName());
        holder.teacherInstitution.setText(data.get(position).getCurrentInstitution());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TutorDetailActivity.class);
                intent.putExtra("tutorId",data.get(position).getuId());
                intent.putExtra("isApplication",false);
                intent.putExtra("applicationId","NONE");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView teacherName,teacherInstitution;
        ImageView teacherImage;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            teacherName = itemView.findViewById(R.id.tutorName_id);
            teacherInstitution = itemView.findViewById(R.id.tutorInstitution_id);
            teacherImage = itemView.findViewById(R.id.tutorImage_id);
            linearLayout = itemView.findViewById(R.id.rowParentTutorListLayout_id);
        }
    }
}
