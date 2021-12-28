package com.frintos.frintos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;

public class ErrorActivity extends AppCompatActivity {
    TextView textView;
    Button button;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        String error = getIntent().getStringExtra("error");
        mAuth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
            if(firebaseAuth.getCurrentUser()==null){
                Intent log_intent=new Intent(ErrorActivity.this, LoginActivity.class);
                log_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(log_intent);
                finish();
            }
        };
        mAuth.addAuthStateListener(authStateListener);
        textView = findViewById(R.id.textView30);
        button = findViewById(R.id.button10);
        textView.setText(error);
        button.setOnClickListener(v -> mAuth.signOut());
    }

}