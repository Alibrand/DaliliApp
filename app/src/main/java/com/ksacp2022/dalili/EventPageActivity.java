package com.ksacp2022.dalili;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class EventPageActivity extends AppCompatActivity {

    TextView event_title,event_description,event_start_date,
    event_place,event_status,event_duration;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        event_title = findViewById(R.id.event_title);
        event_description = findViewById(R.id.event_description);
        event_start_date = findViewById(R.id.event_start_date);
        event_place = findViewById(R.id.event_place);
        event_status = findViewById(R.id.event_status);
        event_duration = findViewById(R.id.event_duration);

        String event_id=getIntent().getStringExtra("event_id");

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Loading");
        progressDialog.show();

        firestore.collection("events")
                .document(event_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        progressDialog.dismiss();

                        Map<String,Object> data=documentSnapshot.getData();

                        event_title.setText(data.get("title").toString());
                        event_description.setText(data.get("description").toString());
                        event_place.setText(data.get("place").toString());
                        event_start_date.setText(data.get("start_date").toString());
                        event_duration.setText(data.get("duration").toString());
                        event_status.setText(data.get("status").toString());


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EventPageActivity.this,"Failed to load Info" , Toast.LENGTH_LONG).show();
                        finish();
                    }
                });






    }
}