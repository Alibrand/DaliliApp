package com.ksacp2022.dalili;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdminPlaceListAdapter extends RecyclerView.Adapter<AdminPlaceCard> {
    List<SightPlace> sightPlaceList;
    Context context;
    FirebaseStorage storage;
    FirebaseFirestore firestore;

    public AdminPlaceListAdapter(List<SightPlace> sightPlaceList, Context context) {
        this.sightPlaceList = sightPlaceList;
        this.context = context;
        storage=FirebaseStorage.getInstance();
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public AdminPlaceCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_place_card,parent,false);

        return new AdminPlaceCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminPlaceCard holder, int position) {
        int pos=position;
        SightPlace sightPlace = sightPlaceList.get(pos);
        holder.name.setText(sightPlace.getName());

        StorageReference ref= storage.getReference();
        StorageReference image=ref.child("places_images/"+ sightPlace.getImage_url());

        GlideApp.with(context)
                .load(image)
                .apply(new RequestOptions().circleCrop())
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,AdminPlaceEditActivity. class);
                intent.putExtra("place_id", sightPlace.getId());
                context.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("places")
                        .document(sightPlace.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                sightPlaceList.remove(sightPlace);
                                notifyItemRemoved(pos);
                                notifyItemRangeChanged(pos, sightPlaceList.size());
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
        return sightPlaceList.size();
    }
}

class AdminPlaceCard extends RecyclerView.ViewHolder{
    ImageView image;
    TextView name;
    ImageButton delete;

    public AdminPlaceCard(@NonNull View itemView) {
        super(itemView);
        image=itemView.findViewById(R.id.image);
        name=itemView.findViewById(R.id.name);
        delete=itemView.findViewById(R.id.delete);

    }
}
