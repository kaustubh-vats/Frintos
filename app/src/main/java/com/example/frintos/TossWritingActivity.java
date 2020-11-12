package com.example.frintos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.frintos.Model.MyUserData;
import com.example.frintos.Model.usersData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TossWritingActivity extends AppCompatActivity {
    EditText editText;
    ProgressBar progressBar;
    Button button;
    List<MyUserData> userKeys;
    DatabaseReference tossReference,userRefrence;
    String myUid;
    usersData udata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toss_writing);
        tossReference = FirebaseDatabase.getInstance().getReference().child("toss");
        userKeys = new ArrayList<>();
        myUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        editText=findViewById(R.id.editTextTextMultiLine2);
        progressBar=findViewById(R.id.progressBar11);
        button = findViewById(R.id.button9);
        button.setEnabled(false);
        userRefrence=FirebaseDatabase.getInstance().getReference();
        userRefrence.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    for(DataSnapshot ds: snapshot.getChildren())
                    {
                        if (!ds.getKey().equals(myUid))
                        {
                            usersData ud = ds.getValue(usersData.class);
                            MyUserData myUserData=new MyUserData();
                            myUserData.setStatus(ud.getStatus());
                            myUserData.setOnline(ud.getOnline());
                            myUserData.setUid(ds.getKey());
                            myUserData.setUpvotes(ud.getUpvotes());
                            myUserData.setToken(ud.getToken());
                            myUserData.setThumb(ud.getThumb());
                            myUserData.setPicture(ud.getPicture());
                            myUserData.setName(ud.getName());
                            //Toast.makeText(TossWritingActivity.this, ""+myUserData.getName(), Toast.LENGTH_SHORT).show();
                            userKeys.add(myUserData);
                        }
                        if(ds.getKey().equals(myUid))
                        {
                            udata = ds.getValue(usersData.class);
                        }
                    }

                }
                progressBar.setVisibility(View.INVISIBLE);
                button.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendToss(View view) {
        button.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        String message=editText.getText().toString().trim();
        editText.setText("");
        if(message.equals(""))
        {
            editText.setError("No text detected");
            button.setEnabled(true);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        Random random=new Random();
      //  Toast.makeText(this, ""+userKeys.size(), Toast.LENGTH_SHORT).show();
        int r = random.nextInt(userKeys.size());
        final MyUserData myUserData = userKeys.get(r);
        Map tossMap = new HashMap();
        tossMap.put("toss_mes", message);
        tossMap.put("sender", udata.getName());
        tossMap.put("picture", udata.getPicture());
        tossMap.put("thumb", udata.getThumb());
        tossMap.put("sender_id", myUid);
        tossMap.put("upvoted", false);
        tossMap.put("reported", false);
        tossReference.child(myUserData.getUid()).child(myUid).setValue(tossMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(TossWritingActivity.this, "Toast has been send\n Receiver: " + myUserData.getName() + "\nLast seen on: " + myUserData.getOnline(), Toast.LENGTH_LONG).show();
                    button.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(TossWritingActivity.this, "Failed to send toast", Toast.LENGTH_SHORT).show();
                    button.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}