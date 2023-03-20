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

public class AdminManagePlacesActivity extends AppCompatActivity {

    AppCompatButton add_place;
    RecyclerView places;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    List<SightPlace> sightPlaceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_places);
        add_place = findViewById(R.id.add_place);
        places = findViewById(R.id.places);

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        load_places();

        add_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminManagePlacesActivity.this,AdminAddPlaceActivity. class);
                startActivity(intent);
            }
        });




    }

    private void load_places() {
        progressDialog.setMessage("Loading");
        progressDialog.show();

        firestore.collection("places")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        sightPlaceList =new ArrayList<SightPlace>();
                        progressDialog.dismiss();
                        for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()
                        ) {
                            Map<String,Object> data=doc.getData();
                            SightPlace sightPlace =new SightPlace();
                            sightPlace.setName(data.get("name").toString());
                            sightPlace.setDescription(data.get("description").toString());
                            sightPlace.setImage_url(data.get("image_url").toString());
                            sightPlace.setId(doc.getId());

                            sightPlaceList.add(sightPlace);
                        }
                        //sort from last place to old one
                        Collections.reverse(sightPlaceList);
                        AdminPlaceListAdapter adapter=new AdminPlaceListAdapter(sightPlaceList,AdminManagePlacesActivity.this);
                        places.setAdapter(adapter);

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
        load_places();
    }
}