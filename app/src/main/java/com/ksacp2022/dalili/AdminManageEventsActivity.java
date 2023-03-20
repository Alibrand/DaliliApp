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

public class AdminManageEventsActivity extends AppCompatActivity {


    AppCompatButton add_event;
    RecyclerView events;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_managr_events);
        add_event = findViewById(R.id.add_event);
        events = findViewById(R.id.events);

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        load_events();

        add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminManageEventsActivity.this,AdminAddEventActivity. class);
                startActivity(intent);
            }
        });




    }

    private void load_events() {
        progressDialog.setMessage("Loading");
        progressDialog.show();

        firestore.collection("events")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        eventList=new ArrayList<Event>();
                        progressDialog.dismiss();
                        for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()
                        ) {
                            Map<String,Object> data=doc.getData();
                            Event event=new Event();
                            event.setTitle(data.get("title").toString());
                            event.setDescription(data.get("description").toString());
                            event.setStatus(data.get("status").toString());
                            event.setPlace(data.get("place").toString());
                            event.setId(doc.getId());

                            eventList.add(event);
                        }
                        //sort from last place to old one
                        Collections.reverse(eventList);
                        AdminEventsListAdapter adapter=new AdminEventsListAdapter(eventList,AdminManageEventsActivity.this);
                        events.setAdapter(adapter);

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
        load_events();
    }
}