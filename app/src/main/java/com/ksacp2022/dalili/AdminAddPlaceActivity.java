package com.ksacp2022.dalili;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AdminAddPlaceActivity extends AppCompatActivity {

    ImageButton take_pic;
    ImageView place_image;
    EditText place_name, place_description;
    AppCompatButton save,set_location;

    FirebaseFirestore firestore;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    GeoPoint location;

    Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_place);
        take_pic = findViewById(R.id.take_pic);
        place_image = findViewById(R.id.place_image);
        place_name = findViewById(R.id.place_name);
        place_description = findViewById(R.id.place_description);
        save = findViewById(R.id.save);
        set_location = findViewById(R.id.set_location);



        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        set_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminAddPlaceActivity.this,LocationChooseActivity. class);
                startActivityForResult(intent,100);
            }
        });

        take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 110);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = place_name.getText().toString();
                String description = place_description.getText().toString();


                if (name.isEmpty()) {
                    place_name.setError("Empty field");
                    return;
                }
                if (description.isEmpty()) {
                    place_description.setError("Empty field");
                    return;
                }
                if(selectedImageUri==null)
                {
                    makeText(AdminAddPlaceActivity.this,"You should choose an image" , LENGTH_LONG).show();
                return;
                }

                if(location==null)
                {
                    makeText(AdminAddPlaceActivity.this,"You should set new place location on the map" , LENGTH_LONG).show();
                    return;
                }


                progressDialog.setMessage("Saving Place");
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
                                                              SightPlace sightPlace =new SightPlace();
                                                              sightPlace.setName(name);
                                                              sightPlace.setDescription(description);
                                                              sightPlace.setImage_url(imageName);
                                                              sightPlace.setLocation(location);
                                                              long timestamp=System.currentTimeMillis();

                                                              firestore.collection("places")
                                                                      .document(String.valueOf(timestamp))
                                                                      .set(sightPlace)
                                                                      .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                          @Override
                                                                          public void onSuccess(Void unused) {
                                                                              makeText(AdminAddPlaceActivity.this,"Place added successfully" , LENGTH_LONG).show();
                                                                              finish();
                                                                          }
                                                                      }).addOnFailureListener(new OnFailureListener() {
                                                                          @Override
                                                                          public void onFailure(@NonNull Exception e) {
                                                                              makeText(AdminAddPlaceActivity.this,"Failed to save place" , LENGTH_LONG).show();
                                                                              progressDialog.dismiss();
                                                                          }
                                                                      });

                                                          }
                                                      }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                makeText(AdminAddPlaceActivity.this,"Failed to save place" , LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });


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
                    // update the preview image in the layout
                    //selected_image = (Bitmap)data.getExtras().get("data");
                    GlideApp.with(this)
                            .load(selectedImageUri)
                            .apply(new RequestOptions().centerCrop())
                            .into(place_image);
                }
            }
            else if(requestCode==100)
            {
                double latitude= (double) data.getSerializableExtra("lat");
                double longitude= (double) data.getSerializableExtra("long");
                makeText(AdminAddPlaceActivity.this,"Location set successfully", LENGTH_LONG).show();
                location=new GeoPoint(latitude,longitude);
            }
        }
    }


}