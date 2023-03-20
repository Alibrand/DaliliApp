package com.ksacp2022.dalili;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PlacePageActivity extends AppCompatActivity {


    ImageView place_image,button_like;
    TextView place_description,likes_count,place_name,see_all,see_all_reviews;
    RecyclerView recycler_gallery,recycler_reviews;
    ImageButton location;

    String place_id;

    List<String> likes;

    FirebaseFirestore firestore;
    FirebaseStorage storage;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_page);
        place_image = findViewById(R.id.place_image);
        button_like = findViewById(R.id.button_like);
        place_description = findViewById(R.id.place_description);
        likes_count = findViewById(R.id.likes_count);
        place_name = findViewById(R.id.place_name);
        recycler_gallery = findViewById(R.id.recycler_gallery);
        see_all = findViewById(R.id.see_all);
        location = findViewById(R.id.location);
        recycler_reviews = findViewById(R.id.recycler_reviews);
        see_all_reviews = findViewById(R.id.see_all_reviews);







        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);



        place_id=getIntent().getStringExtra("place_id");



        progressDialog.setMessage("Loading");
        progressDialog.show();

        firestore.collection("places")
                .document(place_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();

                        Map<String,Object> data=documentSnapshot.getData();

                        String image_url=data.get("image_url").toString();
                        StorageReference ref=storage.getReference();
                        StorageReference image_path=ref.child("places_images/"+image_url);

                        GlideApp.with(PlacePageActivity.this)
                                .load(image_path)
                                .apply(new RequestOptions().centerCrop())
                                .into(place_image);

                        place_name.setText(data.get("name").toString());
                        place_description.setText(data.get("description").toString());
                        likes= (List<String>) data.get("likes");
                        likes_count.setText(String.valueOf(likes.size()));
                        //get current user id
                        String uid= firebaseAuth.getUid();
                        //check if the user likes this place before?
                        //if the list of likes doesn't contain the uid
                        if(likes.indexOf(uid)==-1)
                        {
                            //uid found so the button should be outlined
                            button_like.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        }
                        else{
                            button_like.setImageResource(R.drawable.ic_baseline_favorite_24);

                        }

                        //on click
                        button_like.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //if the list of likes doesn't contain the uid
                                if(likes.indexOf(uid)==-1)
                                {
                                    //add uid to likes
                                    likes.add(uid);
                                    button_like.setImageResource(R.drawable.ic_baseline_favorite_24);
                                }
                                else{
                                    //remove uid from likes
                                    likes.remove(uid);
                                    button_like.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                }

                                //update likes for this place
                                firestore.collection("places")
                                        .document(place_id)
                                        .update("likes",likes);
                                //set likes count
                                likes_count.setText(String.valueOf(likes.size()));
                            }
                        });

                        //get gallery images
                        List<String> gallery_images= (List<String>) data.get("gallery_images");
                        if(gallery_images.size()>4)
                            gallery_images=gallery_images.subList(0,4);
                        PlaceGalleryListAdapter adapter=new PlaceGalleryListAdapter(gallery_images,PlacePageActivity.this);
                        recycler_gallery.setAdapter(adapter);

                        see_all.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(PlacePageActivity.this,PlaceGalleryActivity. class);
                                intent.putExtra("place_id",place_id);
                                startActivity(intent);
                            }
                        });

                        see_all_reviews.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(PlacePageActivity.this,PlaceReviewsActivity. class);
                                intent.putExtra("place_id",place_id);
                                startActivity(intent);
                            }
                        });

                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                GeoPoint location= (GeoPoint) data.get("location");
                                Intent intent = new Intent(PlacePageActivity.this,MapActivity. class);
                                intent.putExtra("lat",location.getLatitude());
                                intent.putExtra("lng",location.getLongitude());
                                startActivity(intent);
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeText(PlacePageActivity.this,"Failed to load info" , LENGTH_LONG).show();
                        finish();

                    }
                });


        firestore.collection("places")
                .document(place_id)
                .collection("reviews")
                .limit(4)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Review>  reviewList=new ArrayList<Review>();
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()

                        ) {
                            Map<String,Object> data=doc.getData();
                            Review review=new Review();
                            review.setAuthor(data.get("author").toString());
                            review.setReview(data.get("review").toString());
                            review.setRating(data.get("rating").toString());

                            reviewList.add(review);

                        }
                        Collections.reverse(reviewList);
                        ReviewsAdapter adapter=new ReviewsAdapter(reviewList);
                        recycler_reviews.setAdapter(adapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeText(PlacePageActivity.this,"Unable to fetch reviews" , LENGTH_LONG).show();

                    }
                });




    }
}