package com.ksacp2022.dalili;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminEditArticleActivity extends AppCompatActivity {

    EditText article_title, article_content;
    AppCompatButton save;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_article);
        article_title = findViewById(R.id.article_title);
        article_content = findViewById(R.id.article_content);
        save = findViewById(R.id.save);

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        String article_id=getIntent().getStringExtra("article_id");

        progressDialog.setMessage("Loading");
        progressDialog.show();


        firestore.collection("history")
                        .document(article_id)
                                .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                progressDialog.dismiss();
                                                Map<String,Object> data=documentSnapshot.getData();
                                                article_title.setText(data.get("title").toString());
                                                article_content.setText(data.get("content").toString());

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(AdminEditArticleActivity.this,"Failed to load info" , LENGTH_LONG).show();
                        finish();

                    }
                });







        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_article_title = article_title.getText().toString();
                String str_article_content = article_content.getText().toString();


                if(str_article_title.isEmpty())
                {
                    article_title.setError("Empty field");
                    return;
                }
                if(str_article_content.isEmpty())
                {
                    article_content.setError("Empty field");
                    return;
                }



               Map<String,Object> new_data=new HashMap<>();
                new_data.put("title",str_article_title);
                new_data.put("content",str_article_content);


                progressDialog.setMessage("Saving Article");
                progressDialog.show();


                firestore.collection("history")
                        .document(article_id)
                        .update(new_data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                makeText(AdminEditArticleActivity.this,"Article saved successfully" , LENGTH_LONG).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(AdminEditArticleActivity.this,"Failed to save Article" , LENGTH_LONG).show();

                            }
                        });





            }
        });


    }
}