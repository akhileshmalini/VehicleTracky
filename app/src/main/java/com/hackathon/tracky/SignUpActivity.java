package com.hackathon.tracky;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText e3_name, e4_mail, e5_password, e6_confirmpassword;
    FirebaseAuth auth;
    ProgressDialog dialog;
    Button signupbutton;
    DatabaseReference databaseReference;
    int count = 0;
    TextView login;
    ImageView img;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_form);
        e3_name = findViewById(R.id.editName);
        e4_mail = findViewById(R.id.editEmail);
        e5_password = findViewById(R.id.editPassword);
        e6_confirmpassword = findViewById(R.id.editConfirmPassword);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        signupbutton=findViewById(R.id.button);
        img=findViewById(R.id.imageView5);

        login=findViewById(R.id.textView14);
        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupuser();
            }
        });
        Glide.with(getApplicationContext())
                .load(R.drawable.wheels)
                .apply(new RequestOptions()

                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                ).into(img);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }


    public void signupuser() {
        dialog.show();
        count++;


        String name = e3_name.getText().toString();
        String email = e4_mail.getText().toString();
        String password = e5_password.getText().toString();
        String cpassword = e6_confirmpassword.getText().toString();



        if (name.equals(" ") || email.equals(" ") || password.equals(" ")) {
            Toast.makeText(getApplicationContext(), "Fields Cannot Be Left Empty", Toast.LENGTH_SHORT).show();
        } else if(!password.equals(cpassword)){
            Toast.makeText(getApplicationContext(), "Passwords Don't Match", Toast.LENGTH_SHORT).show();
        }else {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    sendEmailVerification();
                    if (task.isSuccessful()) {
                        dialog.hide();
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        Users users = new Users(e3_name.getText().toString(), e4_mail.getText().toString());
                        databaseReference.child(firebaseUser.getUid()).setValue(users)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Check Email To Verify Account", Toast.LENGTH_SHORT).show();

                                            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                                            startActivity(i);

                                        } else {
                                            Toast.makeText(getApplicationContext(), "Data Could Not be Saved", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });


                        Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.hide();
                        Toast.makeText(getApplicationContext(), "Email Already Exists", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }


    public void sendEmailVerification() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)

                        {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Verification Mail Sent", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error Sending Verification", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }








}

