package com.ksacp2022.dalili;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {
    AppCompatButton history_info,places,events,logout,feedbacks,chatbot;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        history_info=findViewById(R.id.history_info);
        places=findViewById(R.id.places);
        events=findViewById(R.id.events);
        logout=findViewById(R.id.logout);
        feedbacks = findViewById(R.id.feedbacks);
        chatbot = findViewById(R.id.chatbot);



        firebaseAuth=FirebaseAuth.getInstance();

        history_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(AdminHomeActivity.this,AdminManageHistoryActivity.class);
                startActivity(intent);

            }
        });
        places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(AdminHomeActivity.this,AdminManagePlacesActivity.class);
                startActivity(intent);

            }
        });
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminHomeActivity.this, AdminManageEventsActivity.class);
                startActivity(intent);

            }
        });
        feedbacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminHomeActivity.this, AdminFeedbacksActivity.class);
                startActivity(intent);

            }
        });
        chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this,AdminQuestionsActivity. class);
                startActivity(intent);
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent=new Intent(AdminHomeActivity.this,LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}