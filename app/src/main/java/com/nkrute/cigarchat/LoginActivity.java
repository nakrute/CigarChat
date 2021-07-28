package com.nkrute.cigarchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // views
    EditText mEmailEt, mPasswordEt;
    TextView notHaveAccntTv;
    Button mLoginBtn;

    //decale firebase auth
    private FirebaseAuth mAuth;

    // progress dialog
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");

        // enable back
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //initialize firebase
        mAuth = FirebaseAuth.getInstance();

        // init
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        notHaveAccntTv = findViewById(R.id.nothave_accountTv);
        mLoginBtn = findViewById(R.id.loginBtn);

        //login button clicked
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input data
                String email = mEmailEt.getText().toString();
                String passw = mPasswordEt.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    // invalid email pattern
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);
                } else {
                    // valid email
                    loginUser(email, passw);

                }
            }
        });
        //no account text view
        notHaveAccntTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // init progress dialog
        pd = new ProgressDialog(this);
        pd.setMessage("Logging In...");
    }

    private void loginUser(String email, String passw) {
        // show progress
        pd.show();
        mAuth.signInWithEmailAndPassword(email, passw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // dismiss progress dialog
                    pd.dismiss();
                    FirebaseUser user = mAuth.getCurrentUser();
                    startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                    finish();
                } else {
                    // dismiss progress dialog
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go previous
        return super.onSupportNavigateUp();
    }
}