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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AdminQuestionsActivity extends AppCompatActivity {

    AppCompatButton add_question;
    RecyclerView questions;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    List<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_questions);
        add_question = findViewById(R.id.add_question);
        questions = findViewById(R.id.questions);

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        load_questions();

        add_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminQuestionsActivity.this,AdminAddQuestionActivity. class);
                startActivity(intent);
            }
        });




    }

    private void load_questions() {
        progressDialog.setMessage("Loading");
        progressDialog.show();

        firestore.collection("questions")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        questionList =new ArrayList<Question>();
                        progressDialog.dismiss();
                        for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()
                        ) {
                            Map<String,Object> data=doc.getData();
                            Question question =new Question();
                            question.setQuestion(data.get("question").toString());
                            question.setId(doc.getId());

                            questionList.add(question);
                        }
                        //sort from last place to old one
                        Collections.reverse(questionList);
                        AdminQuestionsListAdapter adapter=new AdminQuestionsListAdapter(questionList,AdminQuestionsActivity.this);
                        questions.setAdapter(adapter);

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
        load_questions();
    }
}