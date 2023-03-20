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

public class AdminEventsListAdapter extends  RecyclerView.Adapter<AdminEventCard> {
    List<Event> eventList;
    Context context;
    FirebaseFirestore firestore;

    public AdminEventsListAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public AdminEventCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_event_card,parent,false);

        return new AdminEventCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminEventCard holder, int position) {
        int pos=position;
        Event event= eventList.get(pos);

        holder.title.setText(event.getTitle());
        holder.status.setText(event.getStatus());
        holder.place.setText(event.getPlace());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("events")
                        .document(event.getId())
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                eventList.remove(event);
                                notifyItemRemoved(pos);
                                notifyItemRangeChanged(pos,eventList.size());

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context,"Failed to remove item" , Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        holder.event_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,AdminEventEditActivity. class);
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

class AdminEventCard extends RecyclerView.ViewHolder{


    TextView title,place,status;
    ImageButton delete;
    LinearLayoutCompat event_card;

    public AdminEventCard(@NonNull View itemView) {
        super(itemView);


        title =itemView.findViewById(R.id.title);
        place=itemView.findViewById(R.id.place);
        status=itemView.findViewById(R.id.status);
        delete=itemView.findViewById(R.id.delete);
        event_card=itemView.findViewById(R.id.event_card);
    }
}
