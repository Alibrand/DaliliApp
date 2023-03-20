package com.ksacp2022.dalili;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {

    EditText name, email, phone, city, password, confirm_password;
    AppCompatButton create, login;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        city = findViewById(R.id.city);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        create = findViewById(R.id.create);
        login = findViewById(R.id.login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateAccountActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strname = name.getText().toString();
                String stremail = email.getText().toString();
                String strphone = phone.getText().toString();
                String strcity = city.getText().toString();
                String strpassword = password.getText().toString();
                String strconfirm_password = confirm_password.getText().toString();

                if (strname.isEmpty()) {
                    name.setError("This field is required");
                    name.requestFocus();
                    return;
                }
                if (stremail.isEmpty()) {
                    email.setError("This field is required");
                    email.requestFocus();
                    return;
                }

                if (!isValidEmail(stremail)) {
                    email.setError("Email is in a bad Format.It should look like name@example.com");
                    email.requestFocus();
                    return;
                }

                if (strphone.isEmpty()) {
                    phone.setError("This field is required");
                    phone.requestFocus();
                    return;
                }

                if (strcity.isEmpty()) {
                    city.setError("This field is required");
                    city.requestFocus();
                    return;
                }

                if (strpassword.isEmpty()) {
                    password.setError("This field is required");
                    password.requestFocus();
                    return;
                }

                if (!isValidPassword(strpassword)) {
                    password.setError("Weak password!.Make sure to include capitals and smalls with numbers");
                    password.requestFocus();
                    return;
                }

                if (strconfirm_password.isEmpty()) {
                    confirm_password.setError("This field is required");
                    confirm_password.requestFocus();
                    return;
                }

                if (!strconfirm_password.equals(strpassword)) {
                    confirm_password.setError("Passwords don't match");
                    confirm_password.requestFocus();
                    return;
                }


                UserAccount new_account=new UserAccount();

                new_account.setName(strname);
                new_account.setPhone(strphone);
                new_account.setCity(strcity);

                progressDialog.setTitle("Creating User");
                progressDialog.setMessage("Please Wait");
                progressDialog.show();

                firebaseAuth.createUserWithEmailAndPassword(stremail,strpassword)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                    @Override
                                                    public void onSuccess(AuthResult authResult) {
                                                        String new_user_id=firebaseAuth.getUid();
                                                        firebaseFirestore.collection("user_accounts")
                                                                .document(new_user_id)
                                                                .set(new_account)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        progressDialog.dismiss();
                                                                        makeText(CreateAccountActivity.this,"Account created successfully" , LENGTH_LONG).show();
                                                                        Intent intent = new Intent(CreateAccountActivity.this,HomeActivity.
                                                                        class);
                                                                        startActivity(intent);
                                                                        finish();


                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        progressDialog.dismiss();
                                                                        makeText(CreateAccountActivity.this,"an error occured :"+e.getMessage().toString() , LENGTH_LONG).show();
                                                                    }
                                                                });


                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();

                                                        makeText(CreateAccountActivity.this,"An Error Occured : "+e.getMessage().toString() , LENGTH_LONG).show();

                                                    }
                                                });


            }
        });


    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public boolean isValidEmail(final String email) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,6}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();

    }
}