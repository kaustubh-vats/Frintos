package com.example.frintos;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class frintosapp extends Application {
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseuser;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseuser = firebaseAuth.getCurrentUser();
        if(firebaseuser!=null)
        {
            databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            databaseReference.child("online").onDisconnect().setValue(dateFormat.format(new Date()));
            databaseReference.child("online").setValue("true");
        }
    }
}
