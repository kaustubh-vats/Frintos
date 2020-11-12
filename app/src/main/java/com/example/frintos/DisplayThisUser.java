package com.example.frintos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.frintos.Model.usersData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Map;

public class DisplayThisUser extends AppCompatActivity {
    ImageView imageView,imageView1;
    TextView textView,textView1,textView2,textView3;
    ProgressBar progressBar,progressBar1,progressBar2;
    Button button,button1;
    String name,status,picture,upvotes,uid,currState,curUid,online;
    usersData userd;
    DatabaseReference databaseReference, friendRequestRefrence, friendRefrence;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_display_this_user);
        uid=getIntent().getStringExtra("uid");
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
        progressBar1.setVisibility(View.INVISIBLE);
        progressBar2.setVisibility(View.INVISIBLE);
        currState="notFriends";
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        curUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        button=findViewById(R.id.button7);
        button1.setEnabled(false);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        friendRequestRefrence=FirebaseDatabase.getInstance().getReference().child("friend_req");
        friendRefrence=FirebaseDatabase.getInstance().getReference().child("friends");
        databaseReference.keepSynced(true);
        friendRequestRefrence.child(curUid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                Toast.makeText(DisplayThisUser.this, "Database error please try again", Toast.LENGTH_SHORT).show();
            }
        });
        friendRefrence.child(curUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(uid).exists())
                {
                    currState="Friends";
                    button.setText(R.string.remove_friend);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        friendRefrence.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    long frintcount=snapshot.getChildrenCount();
                    textView2.setText(String.valueOf(frintcount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userd=snapshot.getValue(usersData.class);
                picture=userd.getPicture();
                online=userd.getOnline();
                upvotes=userd.getUpvotes();
                name=userd.getName();
                status=userd.getStatus();
                textView.setText(name);
                textView1.setText(status);
                textView3.setText(upvotes);
                if(!picture.equals("default"))
                {
                    RequestOptions options = new RequestOptions()
                            .circleCrop()
                            .placeholder(R.drawable.ic_account_circle_black_24dp)
                            .error(R.drawable.ic_account_circle_black_24dp);
                    RequestOptions options1 = new RequestOptions()
                            .placeholder(R.drawable.ic_account_circle_black_24dp)
                            .error(R.drawable.ic_account_circle_black_24dp);
                    Glide.with(DisplayThisUser.this).load(userd.getPicture()).apply(options).into(imageView);
                    Glide.with(DisplayThisUser.this).load(userd.getPicture()).apply(options1).into(imageView1);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DisplayThisUser.this, "Data base error occured", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void sentReq(View view){
        button.setEnabled(false);
        progressBar1.setVisibility(View.VISIBLE);
        if(currState.equals("notFriends"))
        {
            friendRequestRefrence.child(curUid).child(uid).child("type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        friendRequestRefrence.child(uid).child(curUid).child("type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DisplayThisUser.this, "Request Sent", Toast.LENGTH_SHORT).show();
                                progressBar1.setVisibility(View.INVISIBLE);
                                currState="requested";
                                button.setText(R.string.cancel_req);
                                button.setEnabled(true);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar1.setVisibility(View.INVISIBLE);
                                button.setEnabled(true);
                                Toast.makeText(DisplayThisUser.this, "Failed to sent request", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(DisplayThisUser.this, "Failed to sent request", Toast.LENGTH_SHORT).show();
                        progressBar1.setVisibility(View.INVISIBLE);
                        button.setEnabled(true);
                    }
                }
            });
        }
        else if(currState.equals("requested"))
        {
            friendRequestRefrence.child(curUid).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        friendRequestRefrence.child(uid).child(curUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    button.setText(R.string.send_request);
                                    button.setEnabled(true);
                                    progressBar1.setVisibility(View.INVISIBLE);
                                    currState="notFriends";
                                    Toast.makeText(DisplayThisUser.this, "Request Cancelled", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    progressBar1.setVisibility(View.INVISIBLE);
                                    button.setEnabled(true);
                                    Toast.makeText(DisplayThisUser.this, "Failed to cancel Request", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        progressBar1.setVisibility(View.INVISIBLE);
                        button.setEnabled(true);
                        Toast.makeText(DisplayThisUser.this, "Failed to cancel Request", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else if(currState.equals("received")){
            final String curdate = DateFormat.getDateTimeInstance().format(new Date());
            final Map userMap = new HashMap();
            userMap.put("joined",curdate);
            userMap.put("last","");
            userMap.put("timestamp", ServerValue.TIMESTAMP);
            userMap.put("seen",false);
            friendRefrence.child(curUid).child(uid).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        friendRefrence.child(uid).child(curUid).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    friendRequestRefrence.child(curUid).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                friendRequestRefrence.child(uid).child(curUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            button.setText(R.string.remove_friend);
                                                            button.setEnabled(true);
                                                            progressBar1.setVisibility(View.INVISIBLE);
                                                            currState="Friends";
                                                            button1.setEnabled(false);
                                                            button1.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(DisplayThisUser.this, "You are now Frints", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else{
                                                            button.setEnabled(true);
                                                            progressBar1.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(DisplayThisUser.this, "Failed to accept request", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                            else{
                                                button.setEnabled(true);
                                                progressBar1.setVisibility(View.INVISIBLE);
                                                Toast.makeText(DisplayThisUser.this, "Failed to accept request", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    button.setEnabled(true);
                                    progressBar1.setVisibility(View.INVISIBLE);
                                    Toast.makeText(DisplayThisUser.this, "Failed to accept request", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        button.setEnabled(true);
                        progressBar1.setVisibility(View.INVISIBLE);
                        Toast.makeText(DisplayThisUser.this, "Failed to accept request", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else if(currState.equals("Friends"))
        {
            friendRefrence.child(uid).child(curUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        friendRefrence.child(curUid).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    currState="notFriends";
                                    button.setText(R.string.send_request);
                                    button.setEnabled(true);
                                    progressBar1.setVisibility(View.INVISIBLE);
                                }
                                else
                                {
                                    button.setEnabled(true);
                                    progressBar1.setVisibility(View.INVISIBLE);
                                    Toast.makeText(DisplayThisUser.this, "Failed to remove friend", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else
                    {
                        button.setEnabled(true);
                        progressBar1.setVisibility(View.INVISIBLE);
                        Toast.makeText(DisplayThisUser.this, "Failed to remove friend", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    public void decReq(View view)
    {
        button1.setEnabled(false);
        progressBar2.setVisibility(View.VISIBLE);
        friendRequestRefrence.child(uid).child(curUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    friendRequestRefrence.child(curUid).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
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
                        }
                    });
                }
                else
                {
                    button1.setEnabled(true);
                    progressBar2.setVisibility(View.INVISIBLE);
                    Toast.makeText(DisplayThisUser.this, "Failed to remove request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}