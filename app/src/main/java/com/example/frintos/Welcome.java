package com.example.frintos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Welcome extends AppCompatActivity {
    TextView textView;
    ImageView imageView;
    LinearLayout linearLayout;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uid= firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        textView=findViewById(R.id.textView4);
        imageView=findViewById(R.id.imageView);
        linearLayout=findViewById(R.id.linearLayout);
        Intent intent = getIntent();
        String name= intent.getStringExtra("name");
        textView.setText("Welcome, "+name+" My Friend");
        Animation slideRight = AnimationUtils.loadAnimation(this,R.anim.slideleft);
        slideRight.setDuration(1000);
        linearLayout.startAnimation(slideRight);
        textView.startAnimation(slideRight);
        Animation popup=AnimationUtils.loadAnimation(this,R.anim.popup);
        popup.setDuration(1000);
        imageView.startAnimation(popup);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String dataname=dataSnapshot.child("name").getValue().toString();
                textView.setText("Welcome, "+dataname+" My Friend");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("read error","Error while reading database");
            }
        });
        int TIME_OUT = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(Welcome.this,MainActivity.class);
                intent.putExtra("flag",true);
                startActivity(intent);
                finish();
            }
        }, TIME_OUT);
    }
}
