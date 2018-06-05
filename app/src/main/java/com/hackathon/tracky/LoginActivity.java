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
import android.widget.ImageButton;
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

public class LoginActivity extends AppCompatActivity {

    EditText e1_email, e2_password;
    FirebaseAuth auth;
    ProgressDialog dialog;
    ImageView img;
    Button logInButton;
    TextView signup, forgot;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form);
        logInButton=findViewById(R.id.button);
        img=findViewById(R.id.imageView5);
        signup =findViewById(R.id.textView14);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        Glide.with(getApplicationContext())
                .load(R.drawable.wheels)
                .apply(new RequestOptions()

                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                ).into(img);


        e1_email = findViewById(R.id.editEmail);
        e2_password = findViewById(R.id.editPassword);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("Signing In");
                dialog.show();

                if (e1_email.getText().toString().equals("") || e2_password.getText().toString().equals((""))) {
                    Toast.makeText(getApplicationContext(), "Field Cannot Be Empty!", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(e1_email.getText().toString(), e2_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                dialog.hide();
                                if(checkIfEmailVerified()){
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }else{

                                }

                            } else {
                                dialog.hide();
                                Toast.makeText(getApplicationContext(), "Not Successfully Signed In", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });



    }


    private boolean checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
                return true;
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Email not Verified", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            return false;

        }

    }

}
