package com.ksacp2022.dalili;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PlacesListAdapter extends RecyclerView.Adapter<PlaceCard> {
    List<SightPlace> sightPlaceList;
    Context context;
    FirebaseStorage storage;
    FirebaseFirestore firestore;

    public PlacesListAdapter(List<SightPlace> sightPlaceList, Context context) {
        this.sightPlaceList = sightPlaceList;
        this.context = context;
        storage=FirebaseStorage.getInstance();
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public PlaceCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_card,parent,false);

        return new PlaceCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceCard holder, int position) {
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
                Intent intent = new Intent(context,PlacePageActivity. class);
                intent.putExtra("place_id", sightPlace.getId());
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return sightPlaceList.size();
    }
}

class PlaceCard extends RecyclerView.ViewHolder{
    ImageView image;
    TextView name;
    ImageButton delete;

    public PlaceCard(@NonNull View itemView) {
        super(itemView);
        image=itemView.findViewById(R.id.image);
        name=itemView.findViewById(R.id.name);
        delete=itemView.findViewById(R.id.delete);

    }
}
