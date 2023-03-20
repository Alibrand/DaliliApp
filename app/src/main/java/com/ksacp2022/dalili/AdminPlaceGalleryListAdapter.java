package com.ksacp2022.dalili;

import android.content.Context;
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

public class AdminPlaceGalleryListAdapter extends RecyclerView.Adapter<AdminPlaceGalleryCard> {
    List<String> gallery_images;
    Context context;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    String place_id;

    public AdminPlaceGalleryListAdapter(List<String> images, Context context, String place_id) {
        this.gallery_images = images;
        this.context = context;
        this.place_id = place_id;
        storage=FirebaseStorage.getInstance();
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public AdminPlaceGalleryCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_place_gallery_card,parent,false);

        return new AdminPlaceGalleryCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminPlaceGalleryCard holder, int position) {
        int pos=position;
        String image_url= gallery_images.get(pos);

        StorageReference ref=storage.getReference();
        StorageReference image_path=ref.child("places_images/"+image_url);


        GlideApp.with(context)
                .load(image_path)
                .transform(new CenterCrop(),new RoundedCorners(25))
                .into(holder.image);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallery_images.remove(image_url);
                firestore.collection("places")
                        .document(place_id)
                        .update("gallery_images", gallery_images)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                notifyItemRemoved(pos);
                                notifyItemRangeChanged(pos, gallery_images.size());
                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return gallery_images.size();
    }
}

class AdminPlaceGalleryCard extends  RecyclerView.ViewHolder{

    ImageView image;
    ImageButton delete;

    public AdminPlaceGalleryCard(@NonNull View itemView) {
        super(itemView);
        image=itemView.findViewById(R.id.image);
        delete=itemView.findViewById(R.id.delete);

    }
}