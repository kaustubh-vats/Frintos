package com.frintos.frintos.Utility;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VerifiedUsers {
    List<String> verifiedUsers;

    public VerifiedUsers(Context context){
        verifiedUsers = new ArrayList<String>();
        DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference().child("verified_users");
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    onFetched(true);
                }
                for(DataSnapshot ds:snapshot.getChildren()){
                    try{
                        if(ds.getValue()!=null) {
                            String uid = ds.getKey();
                            boolean isVerified = (boolean)ds.getValue();
                            if(isVerified){
                                verifiedUsers.add(uid);
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
                onFetched(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onFetched(false);
            }
        });
    }

    protected void onFetched(boolean b) {
        // do nothing
    }

    public List<String> getVerifiedUsers(){
        return this.verifiedUsers;
    }

    public boolean isVerified(String uid) {
        return verifiedUsers.contains(uid);
    }
}
