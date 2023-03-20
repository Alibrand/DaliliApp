package com.ksacp2022.dalili;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    EditText email,password;
    AppCompatButton login,create_account;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        create_account = findViewById(R.id.create_account);

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);


        if(firebaseAuth.getCurrentUser()!=null)
        {

            if(firebaseAuth.getCurrentUser().getEmail().equals("admin@dalili.com"))
            {
                Intent intent = new Intent(LogInActivity.this,AdminHomeActivity.
                        class);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(LogInActivity.this,HomeActivity.
                        class);
                startActivity(intent);
            }
            finish();
        }





        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this,CreateAccountActivity. class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stremail =email.getText().toString();
                String strpassword =password.getText().toString();

                if (stremail.isEmpty()) {
                    email.setError("This field is required");
                    email.requestFocus();
                    return;
                }

                if (strpassword.isEmpty()) {
                    password.setError("This field is required");
                    password.requestFocus();
                    return;
                }

                progressDialog.setTitle("Logging in");
                progressDialog.setMessage("Please Wait");
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(stremail,strpassword)
                        . addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                    @Override
                                                    public void onSuccess(AuthResult authResult) {
                                                        if(firebaseAuth.getCurrentUser().getEmail().equals("admin@dalili.com"))
                                                        {
                                                            Intent intent = new Intent(LogInActivity.this,AdminHomeActivity.
                                                                    class);
                                                            startActivity(intent);
                                                        }
                                                        else{
                                                            Intent intent = new Intent(LogInActivity.this,HomeActivity.
                                                                    class);
                                                            startActivity(intent);
                                                        }
                                                        finish();
                                                        progressDialog.dismiss();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(LogInActivity.this,"Failed to log in :"+e.getMessage().toString() , Toast.LENGTH_LONG).show();
                                                    }
                                                });




            }
        });






    }
}