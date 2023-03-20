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

public class EventsListAdapter extends  RecyclerView.Adapter<EventCard> {
    List<Event> eventList;
    Context context;
    FirebaseFirestore firestore;

    public EventsListAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public EventCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_card,parent,false);

        return new EventCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventCard holder, int position) {
        int pos=position;
        Event event= eventList.get(pos);

        holder.title.setText(event.getTitle());
        holder.status.setText(event.getStatus());
        holder.place.setText(event.getPlace());

        holder.event_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,EventPageActivity. class);
                intent.putExtra("event_id",event.getId());
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}

class EventCard extends RecyclerView.ViewHolder{


    TextView title,place,status;
    LinearLayoutCompat event_card;


    public EventCard(@NonNull View itemView) {
        super(itemView);


        title =itemView.findViewById(R.id.title);
        place=itemView.findViewById(R.id.place);
        status=itemView.findViewById(R.id.status);
        event_card=itemView.findViewById(R.id.event_card);
    }
}
