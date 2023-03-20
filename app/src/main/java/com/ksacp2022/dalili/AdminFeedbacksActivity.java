package com.ksacp2022.dalili;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminFeedbacksActivity extends AppCompatActivity {

    RecyclerView recycler_feedbacks;
    FirebaseFirestore firestore;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_feedbacks);
        recycler_feedbacks = findViewById(R.id.feedbacks);

        firestore=FirebaseFirestore.getInstance();
        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Loading");
        dialog.show();

        firestore.collection("feedback")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        dialog.dismiss();
                        List<String> feedbacks=new ArrayList<>();
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()
                             ) {
                            Map<String,Object> data=doc.getData();
                            feedbacks.add(data.get("message").toString());

                        }
                        AdminFeedbacksListAdapter adapter=new AdminFeedbacksListAdapter(feedbacks);
                         recycler_feedbacks.setAdapter(adapter);
                         }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminFeedbacksActivity.this,"Failed to load feedbacks" , Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });

    }
}