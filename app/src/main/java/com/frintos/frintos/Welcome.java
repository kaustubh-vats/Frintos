package com.frintos.frintos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
        SharedPreferences sharedPreferences = getSharedPreferences("dark mode", MODE_PRIVATE);
        String nightMode = sharedPreferences.getString("dark mode enabled", "undefined");
        if(nightMode.equals("yes"))
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else if(nightMode.equals("no"))
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uid= firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_welcome);
        textView=findViewById(R.id.textView4);
        imageView=findViewById(R.id.imageView);
        linearLayout=findViewById(R.id.linearLayout);
        Intent intent = getIntent();
        String name= intent.getStringExtra("name");
        if(name.length() > 12){
            name = name.substring(0,12);
            name+="...";
        }
        String welcomeStr = "Welcome, "+name+" My Friend";
        textView.setText(welcomeStr);
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
                if(dataname.length() > 12){
                    dataname = dataname.substring(0,12);
                    dataname+="...";
                }
                String newWelcomeStr = "Welcome, "+dataname+" My Friend";
                textView.setText(newWelcomeStr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("read error","Error while reading database");
            }
        });
        int TIME_OUT = 2000;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent1 =new Intent(Welcome.this,MainActivity.class);
            intent1.putExtra("flag",true);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            finish();
        }, TIME_OUT);
    }
}
