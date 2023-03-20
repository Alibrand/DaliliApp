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
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminAddQuestionActivity extends AppCompatActivity {
    EditText question, answer, key_words;
    AppCompatButton save;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_question);
        question = findViewById(R.id.question);
        answer = findViewById(R.id.answer);
        key_words = findViewById(R.id.key_words);

        save = findViewById(R.id.save);

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_question = question.getText().toString();
                String str_answer = answer.getText().toString();
                String str_key_words = key_words.getText().toString();


                if(str_question.isEmpty())
                {
                    question.setError("Empty field");
                    return;
                }
                if(str_answer.isEmpty())
                {
                    answer.setError("Empty field");
                    return;
                }
                if(str_key_words.isEmpty())
                {
                    key_words.setError("Empty field");
                    return;
                }



                Question question=new Question();
                question.setQuestion(str_question);
                question.setAnswer(str_answer);
                question.setKeywords(str_key_words);

                long timestamp=System.currentTimeMillis();


                progressDialog.setMessage("Saving Question");
                progressDialog.show();


                firestore.collection("questions")
                        .document(String.valueOf(timestamp))
                        .set(question)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Toast.makeText(AdminAddQuestionActivity.this,"Question added successfully" , Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(AdminAddQuestionActivity.this,"Failed to add event" , Toast.LENGTH_LONG).show();

                            }
                        });





            }
        });


    }
}