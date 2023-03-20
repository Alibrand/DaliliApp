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
import com.google.firebase.firestore.FirebaseFirestore;

public class FeedBackActivity extends AppCompatActivity {

    AppCompatButton back,send;
    FirebaseFirestore firestore;
    EditText feed_back;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        back = findViewById(R.id.back);
        send = findViewById(R.id.send);
        feed_back = findViewById(R.id.feed_back);


        firestore=FirebaseFirestore.getInstance();
        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=feed_back.getText().toString();

                if(message.isEmpty())
                {
                    feed_back.setError("Empty field");
                    return;
                }

                dialog.setMessage("Sending Feedback");
                dialog.show();

                FeedBack feedBack=new FeedBack();
                long timestamp=System.currentTimeMillis();
                feedBack.setMessage(message);
                feedBack.setTimestamp(timestamp);
                String docId=String.valueOf(timestamp);


                firestore.collection("feedback")
                        .document(docId)
                        .set(feedBack)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                makeText(FeedBackActivity.this,"Your feedback has been sent successfully" , LENGTH_LONG).show();
                           dialog.dismiss();
                           finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                makeText(FeedBackActivity.this,"Failed to send feedback" , LENGTH_LONG).show();
                            }
                        });


            }
        });







    }
}