package com.frintos.frintos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.frintos.frintos.Model.usersData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class DisplayThisUser extends AppCompatActivity {
    ImageView imageView,imageView1,imageViewVerified;
    TextView textView,textView1,textView2,textView3;
    ProgressBar progressBar,progressBar1,progressBar2;
    Button button,button1;
    String name,status,picture,upvotes,uid,currState,curUid,online;
    boolean isVerified;
    usersData userd;
    DatabaseReference databaseReference, friendRequestRefrence, friendRefrence;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_display_this_user);
        uid=getIntent().getStringExtra("uid");
        isVerified=getIntent().getBooleanExtra("verified", false);
        imageView=findViewById(R.id.imageView7);
        imageView1=findViewById(R.id.imageView4);
        textView2=findViewById(R.id.textView15);
        textView3=findViewById(R.id.textView17);
        textView=findViewById(R.id.textView9);
        button1=findViewById(R.id.button8);
        progressBar2=findViewById(R.id.progressBar7);
        textView1=findViewById(R.id.textView10);
        progressBar=findViewById(R.id.progressBar5);
        progressBar1=findViewById(R.id.progressBar6);
        imageViewVerified=findViewById(R.id.imageView19);
        progressBar1.setVisibility(View.INVISIBLE);
        progressBar2.setVisibility(View.INVISIBLE);
        if(isVerified) {
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    imageViewVerified.setImageResource(R.drawable.verified_night);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    imageViewVerified.setImageResource(R.drawable.verified);
                    break;
            }
            imageViewVerified.setVisibility(View.VISIBLE);
        } else {
            imageViewVerified.setVisibility(View.INVISIBLE);
        }
        currState="notFriends";
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        curUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        button=findViewById(R.id.button7);
        button1.setEnabled(false);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        friendRequestRefrence=FirebaseDatabase.getInstance().getReference().child("friend_req");
        friendRefrence=FirebaseDatabase.getInstance().getReference().child("friends");
        databaseReference.keepSynced(true);
        friendRefrence.child(curUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(uid).exists())
                {
                    currState="Friends";
                    button.setText(R.string.remove_friend);
                }
                else{
                    currState="notFriends";
                    button.setText(R.string.send_request);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        friendRequestRefrence.child(curUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type="";
                if(snapshot.child(uid).child("type").getValue() != null) {
                    type=snapshot.child(uid).child("type").getValue().toString();
                }
                if(type.equals("received")){
                    button1.setEnabled(true);
                    button1.setVisibility(View.VISIBLE);
                    currState="received";
                    button.setText(R.string.accept_request);
                }
                else if(type.equals("sent")){
                    currState="requested";
                    button.setText(R.string.cancel_req);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        friendRefrence.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    long frintCount=snapshot.getChildrenCount();
                    textView2.setText(String.valueOf(frintCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userd=snapshot.getValue(usersData.class);
                if (userd != null) {
                    picture = userd.getPicture();
                    online=userd.getOnline().toString();
                    upvotes = userd.getUpvotes();
                    name = userd.getName();
                    status = userd.getStatus();
                    textView.setText(name);
                    textView1.setText(status);
                    textView3.setText(upvotes);
                    if (!picture.equals("default")) {
                        RequestOptions options = new RequestOptions()
                                .circleCrop()
                                .placeholder(R.drawable.ic_account_circle_black_24dp)
                                .error(R.drawable.ic_account_circle_black_24dp);
                        RequestOptions options1 = new RequestOptions()
                                .placeholder(R.drawable.ic_account_circle_black_24dp)
                                .error(R.drawable.ic_account_circle_black_24dp);
                        Glide.with(getApplicationContext()).load(userd.getPicture()).apply(options).into(imageView);
                        Glide.with(getApplicationContext()).load(userd.getPicture()).apply(options1).into(imageView1);
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void sentReq(View view){
        button.setEnabled(false);
        progressBar1.setVisibility(View.VISIBLE);
        switch (currState) {
            case "requested":
                friendRequestRefrence.child(curUid).child(uid).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        friendRequestRefrence.child(uid).child(curUid).removeValue().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                button.setText(R.string.send_request);
                                button.setEnabled(true);
                                progressBar1.setVisibility(View.INVISIBLE);
                                currState = "notFriends";
                                Toast.makeText(DisplayThisUser.this, "Request Cancelled", Toast.LENGTH_SHORT).show();
                            } else {
                                progressBar1.setVisibility(View.INVISIBLE);
                                button.setEnabled(true);
                                Toast.makeText(DisplayThisUser.this, "Failed to cancel Request", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        progressBar1.setVisibility(View.INVISIBLE);
                        button.setEnabled(true);
                        Toast.makeText(DisplayThisUser.this, "Failed to cancel Request", Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            case "received":
                final String curdate = DateFormat.getDateTimeInstance().format(new Date());
                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("joined", curdate);
                userMap.put("last", "");
                userMap.put("timestamp", ServerValue.TIMESTAMP);
                userMap.put("seen", false);
                friendRefrence.child(curUid).child(uid).setValue(userMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        friendRefrence.child(uid).child(curUid).setValue(userMap).addOnCompleteListener(task12 -> {
                            if (task12.isSuccessful()) {
                                friendRequestRefrence.child(curUid).child(uid).removeValue().addOnCompleteListener(task121 -> {
                                    if (task121.isSuccessful()) {
                                        friendRequestRefrence.child(uid).child(curUid).removeValue().addOnCompleteListener(task1211 -> {
                                            if (task1211.isSuccessful()) {
                                                button.setText(R.string.remove_friend);
                                                button.setEnabled(true);
                                                progressBar1.setVisibility(View.INVISIBLE);
                                                currState = "Friends";
                                                button1.setEnabled(false);
                                                button1.setVisibility(View.INVISIBLE);
                                                Toast.makeText(DisplayThisUser.this, "You are now Frints", Toast.LENGTH_SHORT).show();
                                            } else {
                                                button.setEnabled(true);
                                                progressBar1.setVisibility(View.INVISIBLE);
                                                Toast.makeText(DisplayThisUser.this, "Failed to accept request", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        button.setEnabled(true);
                                        progressBar1.setVisibility(View.INVISIBLE);
                                        Toast.makeText(DisplayThisUser.this, "Failed to accept request", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                button.setEnabled(true);
                                progressBar1.setVisibility(View.INVISIBLE);
                                Toast.makeText(DisplayThisUser.this, "Failed to accept request", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        button.setEnabled(true);
                        progressBar1.setVisibility(View.INVISIBLE);
                        Toast.makeText(DisplayThisUser.this, "Failed to accept request", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case "notFriends":
                friendRequestRefrence.child(curUid).child(uid).child("type").setValue("sent").addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        friendRequestRefrence.child(uid).child(curUid).child("type").setValue("received").addOnSuccessListener(aVoid -> {
                            Toast.makeText(DisplayThisUser.this, "Request Sent", Toast.LENGTH_SHORT).show();
                            progressBar1.setVisibility(View.INVISIBLE);
                            currState = "requested";
                            button.setText(R.string.cancel_req);
                            button.setEnabled(true);
                        }).addOnFailureListener(e -> {
                            progressBar1.setVisibility(View.INVISIBLE);
                            button.setEnabled(true);
                            Toast.makeText(DisplayThisUser.this, "Failed to sent request", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        Toast.makeText(DisplayThisUser.this, "Failed to sent request", Toast.LENGTH_SHORT).show();
                        progressBar1.setVisibility(View.INVISIBLE);
                        button.setEnabled(true);
                    }
                });
                break;
            case "Friends":
                friendRefrence.child(uid).child(curUid).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        friendRefrence.child(curUid).child(uid).removeValue().addOnCompleteListener(task13 -> {
                            if (task13.isSuccessful()) {
                                currState = "notFriends";
                                button.setText(R.string.send_request);
                                button.setEnabled(true);
                                progressBar1.setVisibility(View.INVISIBLE);
                            } else {
                                button.setEnabled(true);
                                progressBar1.setVisibility(View.INVISIBLE);
                                Toast.makeText(DisplayThisUser.this, "Failed to remove friend", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        button.setEnabled(true);
                        progressBar1.setVisibility(View.INVISIBLE);
                        Toast.makeText(DisplayThisUser.this, "Failed to remove friend", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
    public void decReq(View view)
    {
        button1.setEnabled(false);
        progressBar2.setVisibility(View.VISIBLE);
        friendRequestRefrence.child(uid).child(curUid).removeValue().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                friendRequestRefrence.child(curUid).child(uid).removeValue().addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful())
                    {
                        button1.setVisibility(View.INVISIBLE);
                        button.setText(R.string.send_request);
                        currState="notFriends";
                        progressBar2.setVisibility(View.INVISIBLE);
                        Toast.makeText(DisplayThisUser.this, "Removed Request", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        button1.setEnabled(true);
                        progressBar2.setVisibility(View.INVISIBLE);
                        Toast.makeText(DisplayThisUser.this, "Failed to remove request", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                button1.setEnabled(true);
                progressBar2.setVisibility(View.INVISIBLE);
                Toast.makeText(DisplayThisUser.this, "Failed to remove request", Toast.LENGTH_SHORT).show();
            }
        });
    }
}