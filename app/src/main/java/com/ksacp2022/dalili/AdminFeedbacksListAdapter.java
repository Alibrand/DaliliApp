package com.ksacp2022.dalili;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminFeedbacksListAdapter extends  RecyclerView.Adapter<AdminFeedbackCard> {
    List<String> feedbacks;


    public AdminFeedbacksListAdapter(List<String> feedbacks) {
        this.feedbacks = feedbacks;

    }

    @NonNull
    @Override
    public AdminFeedbackCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feedback_card,parent,false);

        return new AdminFeedbackCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminFeedbackCard holder, int position) {
        String feedback= feedbacks.get(position);
         holder.text.setText(feedback);

    }

    @Override
    public int getItemCount() {
        return feedbacks.size();
    }
}

class AdminFeedbackCard extends RecyclerView.ViewHolder{


    TextView text;



    public AdminFeedbackCard(@NonNull View itemView) {
        super(itemView);


        text =itemView.findViewById(R.id.text);

    }
}
