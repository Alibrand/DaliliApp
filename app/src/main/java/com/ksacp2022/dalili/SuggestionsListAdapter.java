package com.ksacp2022.dalili;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SuggestionsListAdapter extends  RecyclerView.Adapter<SuggestiontCard> {
    List<Question> suggestionsList;
    Context context;


    public SuggestionsListAdapter(List<Question> suggestionsList, Context context) {
        this.suggestionsList = suggestionsList;
        this.context = context;

    }

    @NonNull
    @Override
    public SuggestiontCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggestion_card,parent,false);

        return new SuggestiontCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestiontCard holder, int position) {
        int pos=position;
        Question question = suggestionsList.get(pos);

        holder.suggestion.setText(question.getQuestion());

        holder.suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ChatBotActivity)context).ask_bot(question);
            }
        });

    }

    @Override
    public int getItemCount() {
        return suggestionsList.size();
    }
}

class SuggestiontCard extends RecyclerView.ViewHolder{


    TextView suggestion;


    public SuggestiontCard(@NonNull View itemView) {
        super(itemView);


        suggestion =itemView.findViewById(R.id.suggestion);


    }
}
