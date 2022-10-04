package com.frintos.frintos;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.LifecycleObserver;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class frintosapp extends Application implements LifecycleObserver {
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
            //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            databaseReference.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
            databaseReference.child("online").setValue("true");
        }
    }



}
