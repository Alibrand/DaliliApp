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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MessagesListAdapter extends  RecyclerView.Adapter<MessagetCard> {
    List<ChatMessage> chatMessageList;
    Context context;


    public MessagesListAdapter(List<ChatMessage> chatMessageList, Context context) {
        this.chatMessageList = chatMessageList;
        this.context = context;

    }

    @NonNull
    @Override
    public MessagetCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_bot_message_card,parent,false);

        return new MessagetCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagetCard holder, int position) {
        int pos=position;
        ChatMessage chatMessage= chatMessageList.get(pos);

         if(chatMessage.getFrom().equals("me"))
         {
             holder.bot_card.setVisibility(View.GONE);
             holder.sender_text.setText(chatMessage.getText());
         }
         else{
             holder.sender_card.setVisibility(View.GONE);
             holder.bot_text.setText(chatMessage.getText());
         }

    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }
}

class MessagetCard extends RecyclerView.ViewHolder{


    TextView sender_text,bot_text;
    CardView sender_card,bot_card;

    public MessagetCard(@NonNull View itemView) {
        super(itemView);


        sender_text =itemView.findViewById(R.id.sender_text);
        bot_text=itemView.findViewById(R.id.bot_text);
        sender_card=itemView.findViewById(R.id.sender_card);
        bot_card=itemView.findViewById(R.id.bot_card);

    }
}
