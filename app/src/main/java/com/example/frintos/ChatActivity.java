package com.example.frintos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.frintos.Model.ChatModel;
import com.example.frintos.Model.GetchatData;
import com.example.frintos.RecyclerAdapter.ChatAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    ImageView imageView, imageView1,imageView2;
    TextView textView, textView1;
    String name, picture, online, status, uid, thumb;
    DatabaseReference chatRefrence;
    FirebaseUser firebaseUser;
    EditText editText;
    RecyclerView recyclerView;
    ConstraintLayout constraintLayout;
    String myuid;
    ProgressBar progressBar;
    List<ChatModel> messageList = new ArrayList<>();
    ChatAdapter chatAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setStackFromEnd(false);
        name=intent.getStringExtra("name");
        uid=intent.getStringExtra("uid");
        thumb=intent.getStringExtra("thumb");
        picture=intent.getStringExtra("picture");
        status=intent.getStringExtra("status");
        online=intent.getStringExtra("online");
        progressBar=findViewById(R.id.progressBar12);
        imageView=findViewById(R.id.imageView10);
        recyclerView=findViewById(R.id.recyclerView4);
        imageView1=findViewById(R.id.imageView11);
        textView=findViewById(R.id.textView22);
        textView1=findViewById(R.id.textView23);
        imageView2=findViewById(R.id.imageView12);
        editText=findViewById(R.id.editTextTextMultiLine);
        constraintLayout=findViewById(R.id.constraintLayout4);
        textView1.setSelected(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp);
        Glide.with(ChatActivity.this).load(thumb).apply(options).into(imageView);
        textView.setText(name);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        chatRefrence= FirebaseDatabase.getInstance().getReference();
        myuid = firebaseUser.getUid();
        chatAdapter=new ChatAdapter(ChatActivity.this, messageList);
        recyclerView.setAdapter(chatAdapter);
        switch (online) {
            case "true":
                textView1.setText(R.string.active);
                imageView1.setVisibility(View.VISIBLE);
                ObjectAnimator hbeat = ObjectAnimator.ofPropertyValuesHolder(imageView1, PropertyValuesHolder.ofFloat("scaleX", 1.2f), PropertyValuesHolder.ofFloat("scaleY", 1.2f));
                hbeat.setDuration(500);
                hbeat.setRepeatCount(ObjectAnimator.INFINITE);
                hbeat.setRepeatMode(ObjectAnimator.REVERSE);
                hbeat.start();
                break;
            case "hidden":
                textView1.setText(status);
                break;
            case "false":
                textView1.setText(String.format("%s need to to update the app", name));
                break;
            default:
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                try {
                    Date date1 = dateFormat.parse(online);
                    long diff = date.getTime() - date1.getTime();
                    String dayName = (String) android.text.format.DateFormat.format("EEEE", date1);
                    float numOfDayPass = diff / 86400000.0f;
                    String lastsn;
                    if (numOfDayPass < 1) {
                        long sec = diff / 1000;
                        long min = sec / 60;
                        long hour = min / 60;
                        sec = sec % 60;
                        min = min % 60;
                        if (hour == 0) {
                            if (min == 0) {
                                lastsn = sec + " seconds ago";
                            } else {
                                lastsn = min + " minutes ago";
                            }
                        } else {
                            lastsn = hour + " hours ago";
                        }
                    } else if ((numOfDayPass >= 1) && (numOfDayPass < 7)) {
                        lastsn = "was on " + dayName;
                    } else if ((numOfDayPass >= 7) && (numOfDayPass < 30)) {
                        int weeks = (int) numOfDayPass / 7;
                        if (weeks > 1) {
                            lastsn = weeks + " weeks ago";
                        } else {
                            lastsn = weeks + " week ago";
                        }
                    } else if ((numOfDayPass >= 30) && (numOfDayPass < 90)) {
                        int months = (int) numOfDayPass / 30;
                        if (months > 1) {
                            lastsn = months + " months ago";
                        } else {
                            lastsn = months + " month ago";
                        }
                    } else if ((numOfDayPass >= 360) && (numOfDayPass < 1080)) {
                        int years = (int) numOfDayPass / 360;

                        if (years > 1) {
                            lastsn = years + " years ago";
                        } else {
                            lastsn = years + " year ago";
                        }
                    } else {
                        lastsn = online;
                    }
                    textView1.setText(String.format("Last seen %s", lastsn));
                } catch (ParseException e) {
                    textView1.setText(String.format("Last seen at: %s", online));
                    e.printStackTrace();
                }
                break;
        }
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ChatActivity.this,DisplayThisUser.class);
                intent1.putExtra("uid",uid);
                startActivity(intent1);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ChatActivity.this,DisplayImage.class);
                intent1.putExtra("image",picture);
                intent1.putExtra("thumb",thumb);
                intent1.putExtra("flg","false");
                intent1.putExtra("uid",uid);
                intent1.putExtra("name",name);
                startActivity(intent1);
            }
        });
        chatRefrence.child("chats").child(myuid).child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists())
                {
                    GetchatData getchatData = snapshot.getValue(GetchatData.class);
                    ChatModel chatModel = new ChatModel();
                    chatModel.setMessage(getchatData.getMessage());
                    chatModel.setFrom(getchatData.getFrom());
                    chatModel.setSeen(getchatData.isSeen());
                    chatModel.setMyuid(myuid);
                    chatModel.setType(getchatData.getType());
                    chatModel.setTimestamp(getchatData.getTimestamp());
                    chatModel.setThumb(thumb);
                    chatModel.setMessageId(snapshot.getKey());
                    messageList.add(chatModel);
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size()-1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(bottom<oldBottom)
                {
                    int pos= Objects.requireNonNull(recyclerView.getAdapter()).getItemCount()-1;
                    if(pos>=0)
                        recyclerView.smoothScrollToPosition(pos);
                }
            }
        });
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final String messageId = messageList.get(position).getMessageId();
                String fromId = messageList.get(position).getFrom();
                if(myuid.equals(fromId))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setMessage("This message will be deleted permanently and can't be retrieved back. Do you wish to continue");
                    builder.setTitle("Do you want to delete this mesaage");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressBar.setVisibility(View.VISIBLE);
                            imageView2.setEnabled(false);
                            Toast.makeText(ChatActivity.this, "Deleting Please wait a moment", Toast.LENGTH_SHORT).show();
                            chatRefrence.child("chats").child(myuid).child(uid).child(messageId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        chatRefrence.child("chats").child(uid).child(myuid).child(messageId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    long lastTime=messageList.get(position).getTimestamp();
                                                    messageList.remove(position);
                                                    if(messageList.isEmpty())
                                                    {
                                                        final Map<String, Object> lastMap = new HashMap<String, Object>();
                                                        lastMap.put("last","");
                                                        lastMap.put("timestamp",lastTime);
                                                        chatRefrence.child("friends").child(uid).child(myuid).updateChildren(lastMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    chatRefrence.child("friends").child(myuid).child(uid).updateChildren(lastMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful())
                                                                            {
                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                imageView2.setEnabled(true);
                                                                                Toast.makeText(ChatActivity.this, "Message Deleted", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                            else
                                                                            {
                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                imageView2.setEnabled(true);
                                                                                Toast.makeText(ChatActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                                else
                                                                {
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    imageView2.setEnabled(true);
                                                                    Toast.makeText(ChatActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                    else
                                                    {
                                                        lastTime=messageList.get(messageList.size()-1).getTimestamp();
                                                        final String Lastmsg=messageList.get(messageList.size()-1).getMessage();

                                                        final Map<String, Object> lastMap = new HashMap<String, Object>();
                                                        lastMap.put("last",Lastmsg);
                                                        lastMap.put("timestamp",lastTime);
                                                        chatRefrence.child("friends").child(uid).child(myuid).updateChildren(lastMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    chatRefrence.child("friends").child(myuid).child(uid).updateChildren(lastMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful())
                                                                            {
                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                imageView2.setEnabled(true);
                                                                                Toast.makeText(ChatActivity.this, "Message Deleted", Toast.LENGTH_SHORT).show();
                                                                            }else
                                                                            {
                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                imageView2.setEnabled(true);
                                                                                Toast.makeText(ChatActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                                else
                                                                {
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    imageView2.setEnabled(true);
                                                                    Toast.makeText(ChatActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                    chatAdapter.notifyDataSetChanged();
                                                }
                                                else
                                                {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    imageView2.setEnabled(true);
                                                    chatAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                                    Toast.makeText(ChatActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    else
                                    {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        imageView2.setEnabled(true);
                                        chatAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                        Toast.makeText(ChatActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            chatAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            dialog.cancel();
                        }
                    });
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            chatAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    imageView2.setEnabled(true);
                    chatAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void sendText(View view) {
        String message=editText.getText().toString().trim();
        if(message.isEmpty())
        {
            editText.setError("You cannot send the empty text or spaces");
            return;
        }
        final View v=view;
        final float deg = view.getRotation();
        final Animation animation1 = AnimationUtils.loadAnimation(ChatActivity.this,R.anim.arrow);
        animation1.setDuration(1000);
        animation1.setRepeatCount(0);
        v.animate().rotation(deg-90f).setDuration(500);
        imageView2.startAnimation(animation1);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                imageView2.setVisibility(View.VISIBLE);
                sendMessage();
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                imageView2.setVisibility(View.INVISIBLE);
                float p = v.getRotation();
                v.setRotation(deg);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void sendMessage() {
        String currUserrefPath = "chats/"+myuid+"/"+uid;
        String chatUserRefPath = "chats/"+uid+"/"+myuid;
        String message=editText.getText().toString().trim();
        DatabaseReference databaseReference = chatRefrence.child("chats").child(uid).push();
        String pushId=databaseReference.getKey();
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message",message);
        messageMap.put("seen",false);
        messageMap.put("type","text");
        messageMap.put("timestamp", ServerValue.TIMESTAMP);
        messageMap.put("from",myuid);

        Map<String, Object> messageUserMap = new HashMap<String, Object>();
        messageUserMap.put(currUserrefPath+"/"+pushId,messageMap);
        messageUserMap.put(chatUserRefPath+"/"+pushId,messageMap);
        messageUserMap.put("friends/"+myuid+"/"+uid+"/last",message);
        messageUserMap.put("friends/"+uid+"/"+myuid+"/last",message);
        messageUserMap.put("friends/"+myuid+"/"+uid+"/timestamp",ServerValue.TIMESTAMP);
        messageUserMap.put("friends/"+uid+"/"+myuid+"/timestamp",ServerValue.TIMESTAMP);
        chatRefrence.updateChildren(messageUserMap, new DatabaseReference.CompletionListener(){

            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if(error!=null)
                {
                    Toast.makeText(ChatActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        editText.setText("");
    }
}