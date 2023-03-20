package com.ksacp2022.dalili;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AddPlaceReviewActivity extends AppCompatActivity {
    AppCompatButton back,send;
    EditText review;
    RatingBar rating;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place_review);
        back = findViewById(R.id.back);
        send = findViewById(R.id.send);
        review = findViewById(R.id.review);
        rating = findViewById(R.id.rating);

        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        String place_id=getIntent().getStringExtra("place_id");


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String review_message=review.getText().toString();


                if(review_message.isEmpty())
                {
                    review.setError("We hope you give us a word");
                    return;
                }

                float rate= rating.getRating();


                Review new_review=new Review();
                new_review.setRating(String.valueOf(rate));
                new_review.setReview(review_message);

                String uid=firebaseAuth.getUid();
                dialog.setMessage("Sending Review");
                dialog.show();
                firestore.collection("user_accounts")
                        .document(uid)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Map<String,Object> data=documentSnapshot.getData();
                                String name=data.get("name").toString();
                                float rate= rating.getRating();


                                Review new_review=new Review();
                                new_review.setRating(String.valueOf(rate));
                                new_review.setReview(review_message);
                                new_review.setAuthor(name);

                                long timestamp=System.currentTimeMillis();
                                new_review.setTimestamp(timestamp);
                                String docId=String.valueOf(timestamp);

                                firestore.collection("places")
                                        .document(place_id)
                                        .collection("reviews")
                                        .document( docId)
                                        .set(new_review)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                                Toast.makeText(AddPlaceReviewActivity.this,"Thanks for adding your review" , Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.dismiss();
                                                Toast.makeText(AddPlaceReviewActivity.this,"Fail to save review" , Toast.LENGTH_LONG).show();

                                            }
                                        });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(AddPlaceReviewActivity.this,"Fail to save review" , Toast.LENGTH_LONG).show();

                            }
                        });





            }
        });










    }
}