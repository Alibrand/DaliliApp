package com.ksacp2022.dalili;

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
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminAddArticleActivity extends AppCompatActivity {

    EditText article_title, article_content;
    AppCompatButton save;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_article);
        article_title = findViewById(R.id.article_title);
        article_content = findViewById(R.id.article_content);
        save = findViewById(R.id.save);

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);



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



                HistoryArticle article=new HistoryArticle();
                article.setTitle(str_article_title);
                article.setContent(str_article_content);


                progressDialog.setMessage("Saving Article");
                progressDialog.show();


                firestore.collection("history")
                        .add(article)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                progressDialog.dismiss();
                                Toast.makeText(AdminAddArticleActivity.this,"Article added successfully" , Toast.LENGTH_LONG).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(AdminAddArticleActivity.this,"Failed to add Article" , Toast.LENGTH_LONG).show();

                            }
                        });





            }
        });


    }
}