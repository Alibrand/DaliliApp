package com.ksacp2022.dalili;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ReviewsActivity extends AppCompatActivity {

    AppCompatButton add_review;
    RecyclerView reviews;
    List<Review> reviewList;


    FirebaseFirestore firestore;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        add_review = findViewById(R.id.add_review);
        reviews = findViewById(R.id.reviews);


        firestore=FirebaseFirestore.getInstance();
        dialog=new ProgressDialog(this);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Loading Reviews");


        get_review_list();




        add_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReviewsActivity.this,AddReviewActivity. class);
                startActivity(intent);
            }
        });

    }

    private void get_review_list() {
        dialog.show();

        firestore.collection("reviews")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        reviewList=new ArrayList<Review>();
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
                        reviews.setAdapter(adapter);
                        dialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReviewsActivity.this,"Failed to get reviews" , Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
    }

    @Override
    protected void onResume() {
        get_review_list();
        super.onResume();

    }
}