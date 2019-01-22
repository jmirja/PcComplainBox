package com.example.jmirza.firebaseauth.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jmirza.firebaseauth.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    TextInputEditText emailEt, passEt;
    Button loginButton;
    TextView registerTextView;
    ProgressBar progressBar;
    private FirebaseAuth uAuth;
    private DatabaseReference myRef;
    String uId;
    FirebaseUser user;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Sign In");
        initialization();
        onClick();


    }

    public void initialization() {
        uAuth = FirebaseAuth.getInstance();
        user = uAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();
        emailEt = findViewById(R.id.emailEtId);
        passEt = findViewById(R.id.passwordEtId);
        loginButton = findViewById(R.id.loginBtnId);
        registerTextView = findViewById(R.id.signUpTvId);
        progressBar = findViewById(R.id.progressBarId);
        String text = "Don't have an account? <font color='red'>Register</font>";
        registerTextView.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);

    }

    public void onClick() {
        loginButton.setOnClickListener(this);
        registerTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.loginBtnId) {
            userLogin();

        } else if (view.getId() == R.id.signUpTvId) {
            startActivity(new Intent(this, SignUpActivity.class));
        }

    }

    // this method is made for the view of a logged in user
    @Override
    protected void onStart() {
        super.onStart();

        if (uAuth.getCurrentUser() != null) {


            if (uAuth.getCurrentUser().isEmailVerified()) {
                finish();
                startActivity(new Intent(this, ProfileActivity.class));
            }

        }

    }

    public boolean checkValidity() {

        boolean cancel = false;
        String email = emailEt.getText().toString();
        String password = passEt.getText().toString();

        if (TextUtils.isEmpty(email)) {
            cancel = true;
            emailEt.setError("Enter a email");
            emailEt.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            cancel = true;
            passEt.setError("Enter a password");
            passEt.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            cancel = true;
            emailEt.setError("Enter a valid email");
            emailEt.requestFocus();
        }
        return cancel;
    }

    private void userLogin() {

        String email = emailEt.getText().toString().trim();
        String pass = passEt.getText().toString().trim();

        if (checkValidity()) {

        } else {
            progressBar.setVisibility(View.VISIBLE);

            uAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        if (uAuth.getCurrentUser() != null) {

                            uId = uAuth.getCurrentUser().getUid();
                            final String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (Objects.equals(dataSnapshot.child("student").child(uId).child("occupation").getValue(), "Student")) {

                                        if (uAuth.getCurrentUser().isEmailVerified()) {
                                            finish();
                                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);

                                        } else {
                                            Toast.makeText(LoginActivity.this, "verify your email to login..", Toast.LENGTH_LONG).show();

                                        }

                                    } else if (Objects.equals(dataSnapshot.child("personnel").child(uId).child("occupation").getValue(), "Personnel")) {

                                        if (uAuth.getCurrentUser().isEmailVerified()) {
                                            myRef.child("personnel").child(uId).child("messagingToken").setValue(deviceToken);
                                            finish();
                                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);

                                        } else {
                                            Toast.makeText(LoginActivity.this, "verify your email to login..", Toast.LENGTH_LONG).show();

                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }

    }


}
