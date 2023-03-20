package com.ksacp2022.dalili;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminPlaceGalleryActivity extends AppCompatActivity {

    RecyclerView images;
    AppCompatButton add_image;

    FirebaseStorage storage;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    String place_id;

    List<String> gallery_images;

    Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_place_gallery);
        images = findViewById(R.id.images);
        add_image = findViewById(R.id.add_image);




        storage=FirebaseStorage.getInstance();
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        place_id=getIntent().getStringExtra("place_id");


        load_gallery();



        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 110);
            }
        });








    }

    private void load_gallery(){
        progressDialog.setMessage("Loading");
        progressDialog.show();

        firestore.collection("places")
                .document(place_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        gallery_images=new ArrayList<>();
                        Map<String,Object> data=documentSnapshot.getData();
                        if(data.get("gallery_images")!=null)
                         gallery_images= (List<String>) data.get("gallery_images");


                        AdminPlaceGalleryListAdapter adapter=new AdminPlaceGalleryListAdapter(gallery_images,AdminPlaceGalleryActivity.this,place_id);
                        images.setAdapter(adapter);







                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminPlaceGalleryActivity.this,"Failed to load images" , Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == 110) {
                // Get the url of the image from data
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                   //upload image to firebase
                    progressDialog.setMessage("Uploading");
                    progressDialog.show();

                    //define unique image name usinf UUID generator
                    String imageName = UUID.randomUUID().toString() + ".jpg";

                    // Defining the child of storageReference
                    StorageReference storageReference = storage.getReference();
                    StorageReference ref = storageReference.child("places_images/" + imageName);


                    // adding listeners on upload
                    // or failure of image

                    ref.putFile(selectedImageUri)
                            .addOnSuccessListener(new  OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    //add new image to list
                                    gallery_images.add(imageName);

                                    //update the image  list in firestore
                                    firestore.collection("places")
                                            .document(place_id)
                                            .update("gallery_images",gallery_images)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    makeText(AdminPlaceGalleryActivity.this,"Image uploaded successfully" , LENGTH_LONG).show();
                                                    load_gallery();
                                                    progressDialog.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    makeText(AdminPlaceGalleryActivity.this,"Failed to save image" , LENGTH_LONG).show();
                                                    progressDialog.dismiss();
                                                }
                                            });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    makeText(AdminPlaceGalleryActivity.this,"Failed to save image" , LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });
                }
            }
        }
    }


}