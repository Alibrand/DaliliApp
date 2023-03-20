package com.ksacp2022.dalili;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageViewerActivity extends AppCompatActivity {

    ImageView image_viewer;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        image_viewer = findViewById(R.id.image_viewer);


        String image_url=getIntent().getStringExtra("image_url");

        storage=FirebaseStorage.getInstance();
        StorageReference ref= storage.getReference();
        StorageReference image=ref.child("places_images/"+image_url);

        GlideApp.with(this)
                .load(image)
                .into(image_viewer);
    }
}