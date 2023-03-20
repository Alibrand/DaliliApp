package com.ksacp2022.dalili;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PlaceGalleryListAdapter extends RecyclerView.Adapter<PlaceGalleryCard> {
    List<String> gallery_images;
    Context context;
    FirebaseStorage storage;
    FirebaseFirestore firestore;

    public PlaceGalleryListAdapter(List<String> images, Context context) {
        this.gallery_images = images;
        this.context = context;
        storage=FirebaseStorage.getInstance();
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public PlaceGalleryCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_gallery_card,parent,false);

        return new PlaceGalleryCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceGalleryCard holder, int position) {
        int pos=position;
        String image_url= gallery_images.get(pos);

        StorageReference ref=storage.getReference();
        StorageReference image_path=ref.child("places_images/"+image_url);


        GlideApp.with(context)
                .load(image_path)
                .transform(new CenterCrop(),new RoundedCorners(25))
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ImageViewerActivity. class);
                intent.putExtra("image_url",image_url);
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return gallery_images.size();
    }
}

class PlaceGalleryCard extends  RecyclerView.ViewHolder{

    ImageView image;


    public PlaceGalleryCard(@NonNull View itemView) {
        super(itemView);
        image=itemView.findViewById(R.id.image);


    }
}