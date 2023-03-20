package com.ksacp2022.dalili;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PlacesToVisitActivity extends AppCompatActivity {


    RecyclerView places;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    List<SightPlace> sightPlaceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_to_visit);
        places = findViewById(R.id.places);

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        load_places();



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
                        PlacesListAdapter adapter=new PlacesListAdapter(sightPlaceList,PlacesToVisitActivity.this);
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