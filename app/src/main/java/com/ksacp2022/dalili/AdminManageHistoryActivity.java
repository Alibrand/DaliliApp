package com.ksacp2022.dalili;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminManageHistoryActivity extends AppCompatActivity {

    AppCompatButton add_article;
    RecyclerView articles;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    List<HistoryArticle> articleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_history);
        add_article = findViewById(R.id.add_article);
        articles = findViewById(R.id.articles);

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        load_articles();

        add_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminManageHistoryActivity.this,AdminAddArticleActivity. class);
                startActivity(intent);
            }
        });




    }

    private void load_articles() {
        progressDialog.setMessage("Loading");
        progressDialog.show();

        firestore.collection("history")
                .orderBy("create_date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        articleList =new ArrayList<HistoryArticle>();
                        progressDialog.dismiss();
                        for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()
                        ) {
                            Map<String,Object> data=doc.getData();
                            HistoryArticle article =new HistoryArticle();
                            article.setTitle(data.get("title").toString());
                            article.setId(doc.getId());
                            articleList.add(article);
                        }

                        AdminArticlesListAdapter adapter=new AdminArticlesListAdapter(articleList,AdminManageHistoryActivity.this);
                        articles.setAdapter(adapter);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_articles();
    }
}