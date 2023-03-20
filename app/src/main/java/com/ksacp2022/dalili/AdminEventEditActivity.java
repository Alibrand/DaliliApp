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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminEventEditActivity extends AppCompatActivity {

    EditText event_title,event_description,event_place,event_start_date,event_status,event_duration;
    AppCompatButton save;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_edit);

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

        String event_id=getIntent().getStringExtra("event_id");

        progressDialog.setMessage("Loading info");
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
                                                event_duration.setText(data.get("duration").toString());
                                                event_start_date.setText(data.get("start_date").toString());
                                                event_status.setText(data.get("status").toString());




                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeText(AdminEventEditActivity.this,"Failed to load info" , LENGTH_LONG).show();
                        finish();
                    }
                });


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

                Map<String,Object> new_data=new HashMap<>();
                new_data.put("title",str_event_title);
                new_data.put("description",str_event_description);
                new_data.put("place",str_event_place);
                new_data.put("duration",str_event_duration);
                new_data.put("start_date",str_event_start_date);
                new_data.put("status",str_event_status);

                progressDialog.setMessage("Saving Event");
                progressDialog.show();


                firestore.collection("events")
                        .document(event_id)
                        .update(new_data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                makeText(AdminEventEditActivity.this,"Changes saved successfully" , LENGTH_LONG).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(AdminEventEditActivity.this,"Failed to save event" , LENGTH_LONG).show();

                            }
                        });





            }
        });
    }
}