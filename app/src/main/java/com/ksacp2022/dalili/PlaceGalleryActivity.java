package com.ksacp2022.dalili;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class PlaceGalleryActivity extends AppCompatActivity {

    RecyclerView images;
    ProgressDialog progressDialog;
    String place_id;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_gallery);
        images = findViewById(R.id.images);
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        firestore=FirebaseFirestore.getInstance();

        place_id=getIntent().getStringExtra("place_id");


        progressDialog.setMessage("Loading Images");
        progressDialog.show();

        firestore.collection("places")
                .document(place_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        Map<String,Object> data=documentSnapshot.getData();

                        List<String> gallery_images= (List<String>) data.get("gallery_images");

                        PlaceGalleryListAdapter adapter=new PlaceGalleryListAdapter(gallery_images,PlaceGalleryActivity.this);
                        images.setAdapter(adapter);



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(PlaceGalleryActivity.this,"Failed to load images" , Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

    }
}