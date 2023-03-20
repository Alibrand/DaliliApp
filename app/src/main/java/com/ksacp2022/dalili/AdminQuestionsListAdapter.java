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

public class AdminQuestionsListAdapter extends  RecyclerView.Adapter<AdminQuestionCard> {
    List<Question> questions;
    Context context;
    FirebaseFirestore firestore;

    public AdminQuestionsListAdapter(List<Question> questions, Context context) {
        this.questions = questions;
        this.context = context;
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public AdminQuestionCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_question_card,parent,false);

        return new AdminQuestionCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminQuestionCard holder, int position) {
        int pos=position;
        Question question= questions.get(pos);

        holder.question_text.setText(question.getQuestion());


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("questions")
                        .document(question.getId())
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                questions.remove(question);
                                notifyItemRemoved(pos);
                                notifyItemRangeChanged(pos,questions.size());

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context,"Failed to remove item" , Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });



    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}

class AdminQuestionCard extends RecyclerView.ViewHolder{


    TextView question_text;
    ImageButton delete;


    public AdminQuestionCard(@NonNull View itemView) {
        super(itemView);


        question_text =itemView.findViewById(R.id.question);
        delete=itemView.findViewById(R.id.delete);

    }
}
