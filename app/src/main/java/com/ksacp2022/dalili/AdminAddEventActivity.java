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

public class AdminAddEventActivity extends AppCompatActivity {
    EditText event_title,event_description,event_place,event_start_date,event_status,event_duration;
    AppCompatButton save;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_event);
        event_title = findViewById(R.id.event_title);
        event_description = findViewById(R.id.event_description);
        event_place = findViewById(R.id.event_place);
        event_start_date = findViewById(R.id.event_start_date);
        event_status = findViewById(R.id.event_status);
        event_duration = findViewById(R.id.event_duration);
        save = findViewById(R.id.save);

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_event_title =event_title.getText().toString();
                String str_event_description =event_description.getText().toString();
                String str_event_place =event_place.getText().toString();
                String str_event_start_date =event_start_date.getText().toString();
                String str_event_status =event_status.getText().toString();
                String str_event_duration =event_duration.getText().toString();

                 if(str_event_title.isEmpty())
                 {
                     event_title.setError("Empty field");
                     return;
                 }
                if(str_event_description.isEmpty())
                {
                    event_description.setError("Empty field");
                    return;
                }
                if(str_event_place.isEmpty())
                {
                    event_place.setError("Empty field");
                    return;
                }
                if(str_event_start_date.isEmpty())
                {
                    event_start_date.setError("Empty field");
                    return;
                }
                if(str_event_status.isEmpty())
                {
                    event_status.setError("Empty field");
                    return;
                }
                if(str_event_duration.isEmpty())
                {
                    event_duration.setError("Empty field");
                    return;
                }


                Event event=new Event();
                event.setDescription(str_event_description);
                event.setTitle(str_event_title);
                event.setDuration(str_event_duration);
                event.setPlace(str_event_place);
                event.setStart_date(str_event_start_date);
                event.setStatus(str_event_status);
                long timestamp=System.currentTimeMillis();

                progressDialog.setMessage("Saving Event");
                progressDialog.show();


                firestore.collection("events")
                        .document(String.valueOf(timestamp))
                        .set(event)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Toast.makeText(AdminAddEventActivity.this,"Event added successfully" , Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(AdminAddEventActivity.this,"Failed to add event" , Toast.LENGTH_LONG).show();

                            }
                        });





            }
        });


    }
}