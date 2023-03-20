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
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdminPlaceEditActivity extends AppCompatActivity {

    ImageButton take_pic;
    ImageView place_image;
    EditText place_name, place_description;
    AppCompatButton save,manage_gallery,update_location;

    FirebaseFirestore firestore;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    GeoPoint location;

    Uri selectedImageUri;
    String place_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_place_edit);
        take_pic = findViewById(R.id.take_pic);
        place_image = findViewById(R.id.place_image);
        place_name = findViewById(R.id.place_name);
        place_description = findViewById(R.id.place_description);
        save = findViewById(R.id.save);
        manage_gallery = findViewById(R.id.manage_gallery);
        update_location = findViewById(R.id.update_location);



        place_id=getIntent().getStringExtra("place_id");



        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        //load place info
        get_place_info();





        update_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminPlaceEditActivity.this,LocationChooseActivity. class);
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

        manage_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminPlaceEditActivity.this,AdminPlaceGalleryActivity. class);
                intent.putExtra("place_id",place_id);
                startActivity(intent);
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


                progressDialog.setMessage("Saving Changes");
                progressDialog.show();


                //prepare data
                Map<String,Object> new_data=new HashMap<>();
                new_data.put("name",name);
                new_data.put("description",description);
                new_data.put("location",location);

                // if admin did not change the image
                //so update the data only
                if(selectedImageUri==null)
                {
                    update_place_info(new_data);
                    return;
                }





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
                                new_data.put("image_url",imageName);
                                update_place_info(new_data);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                makeText(AdminPlaceEditActivity.this,"Failed to save place" , LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });


            }
        });


    }

    private void update_place_info(Map<String,Object> new_data)
    {
        firestore.collection("places")
                .document(place_id)
                .update(new_data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        makeText(AdminPlaceEditActivity.this,"changes saved successfully" , LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeText(AdminPlaceEditActivity.this,"Failed to save changes" , LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void get_place_info(){
        progressDialog.setMessage("Loading Place Info");
        progressDialog.show();

        firestore.collection("places")
                .document(place_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        Map<String,Object> data=documentSnapshot.getData();

                        place_name.setText(data.get("name").toString());
                        place_description.setText(data.get("description").toString());

                        location= (GeoPoint) data.get("location");

                        String image_url=data.get("image_url").toString();
                        StorageReference ref=storage.getReference();
                        StorageReference img=ref.child("places_images/"+image_url);

                        GlideApp.with(AdminPlaceEditActivity.this)
                                .load(img)
                                .fitCenter()
                                .apply(new RequestOptions().centerCrop())
                                .into(place_image);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminPlaceEditActivity.this,"Faied to load place info" , Toast.LENGTH_LONG).show();
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
                makeText(AdminPlaceEditActivity.this,"Location set successfully", LENGTH_LONG).show();
                location=new GeoPoint(latitude,longitude);
            }
        }
    }
}