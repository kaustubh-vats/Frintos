package com.frintos.frintos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.frintos.frintos.Model.ChatModel;
import com.frintos.frintos.Model.GetchatData;
import com.frintos.frintos.Model.MyUserData;
import com.frintos.frintos.Model.WallpaperModel;
import com.frintos.frintos.Model.usersData;
import com.frintos.frintos.RecyclerAdapter.ChatAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class ChatActivity extends AppCompatActivity {
    ImageView imageView, imageView1,imageView2, bgimage, imageViewVerified;
    TextView textView, textView1;
    String name, picture, online, status, uid, thumb;
    DatabaseReference chatRefrence;
    FirebaseUser firebaseUser;
    EditText editText;
    RecyclerView recyclerView;
    Toolbar toolbar;
    DatabaseReference databaseReference, wallpaperReference, localReference;
    String myuid;
    ProgressBar progressBar;
    List<ChatModel> messageList = new ArrayList<>();
    ChatAdapter chatAdapter;
    boolean verified;
    WallpaperModel wallpaperModel;
    int currModeImage;
    String currUserName, currUserImage;
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
        verified=intent.getBooleanExtra("verified", false);
        currUserName = "Frintos";
        currUserImage = "default";
        bgimage = findViewById(R.id.imageView14);
        progressBar=findViewById(R.id.progressBar12);
        imageView=findViewById(R.id.imageView10);
        recyclerView=findViewById(R.id.recyclerView4);
        imageView1=findViewById(R.id.imageView11);
        textView=findViewById(R.id.textView22);
        textView1=findViewById(R.id.textView23);
        imageView2=findViewById(R.id.imageView12);
        editText=findViewById(R.id.editTextTextMultiLine);
        imageViewVerified=findViewById(R.id.imageView17);
        toolbar=findViewById(R.id.constraintLayout4);
        toolbar.setTitle("");
        currModeImage = R.drawable.chat_bg_light_default;
        setSupportActionBar(toolbar);
        databaseReference=FirebaseDatabase.getInstance().getReference().child("users");
        textView1.setSelected(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp);
        Glide.with(getApplicationContext()).load(thumb).apply(options).into(imageView);
        textView.setText(name);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        chatRefrence= FirebaseDatabase.getInstance().getReference();
        myuid = firebaseUser.getUid();
        wallpaperReference =  FirebaseDatabase.getInstance().getReference().child("wallpapers").child(myuid).child(uid);
        localReference = FirebaseDatabase.getInstance().getReference().child("local_wallpaper").child(myuid).child(uid);
        chatAdapter=new ChatAdapter(ChatActivity.this, messageList);
        recyclerView.setAdapter(chatAdapter);
        if(verified) {
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
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
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
                    } else if ((numOfDayPass >= 30) && (numOfDayPass < 365)) {
                        int months = (int) numOfDayPass / 30;
                        if (months > 1) {
                            lastsn = months + " months ago";
                        } else {
                            lastsn = months + " month ago";
                        }
                    } else if (numOfDayPass >= 365) {
                        int years = (int) numOfDayPass / 365;

                        if (years > 1) {
                            lastsn = "few years ago";
                        } else {
                            lastsn = "1 year ago";
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

        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.INVISIBLE);
                usersData ud=snapshot.getValue(usersData.class);
                MyUserData myUserData=new MyUserData();
                myUserData.setName(ud.getName());
                myUserData.setPicture(ud.getPicture());
                myUserData.setThumb(ud.getThumb());
                myUserData.setOnline(ud.getOnline().toString());
                myUserData.setStatus(ud.getStatus());
                myUserData.setToken(ud.getToken());
                myUserData.setUpvotes(ud.getUpvotes());
                myUserData.setUid(uid);
                currUserName = ud.getName();
                currUserImage = ud.getThumb();
                setView(myUserData);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        toolbar.setOnClickListener(v -> {
            Intent intent1 = new Intent(ChatActivity.this, DisplayThisUser.class);
            intent1.putExtra("uid",uid);
            intent1.putExtra("verified", verified);
            startActivity(intent1);
        });
        imageView.setOnClickListener(v -> {
            Intent intent1 = new Intent(ChatActivity.this,DisplayImage.class);
            intent1.putExtra("image",picture);
            intent1.putExtra("thumb",thumb);
            intent1.putExtra("flg","false");
            intent1.putExtra("uid",uid);
            intent1.putExtra("name",name);
            startActivity(intent1);
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
                    chatAdapter.notifyItemInserted(messageList.size()-1);
                    recyclerView.scrollToPosition(messageList.size()-1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    GetchatData getchatData = snapshot.getValue(GetchatData.class);
                    ChatModel chatModel = new ChatModel();
                    if (getchatData != null) {
                        chatModel.setMessage(getchatData.getMessage());
                        chatModel.setFrom(getchatData.getFrom());
                        chatModel.setSeen(getchatData.isSeen());
                        chatModel.setMyuid(myuid);
                        chatModel.setType(getchatData.getType());
                        chatModel.setTimestamp(getchatData.getTimestamp());
                        chatModel.setThumb(thumb);
                        chatModel.setMessageId(snapshot.getKey());
                        if(!myuid.equals(chatModel.getFrom()))
                        {
                            int pos = messageList.indexOf(chatModel);
                            messageList.remove(chatModel);
                            chatAdapter.notifyItemRemoved(pos);
                            recyclerView.scrollToPosition(messageList.size() - 1);
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if(bottom<oldBottom)
            {
                int pos= Objects.requireNonNull(recyclerView.getAdapter()).getItemCount()-1;
                if(pos>=0)
                    recyclerView.smoothScrollToPosition(pos);
            }
        });
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getBindingAdapterPosition();
                final String messageId = messageList.get(position).getMessageId();
                String fromId = messageList.get(position).getFrom();
                if(myuid.equals(fromId))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setMessage("This message will be deleted permanently and can't be retrieved back. Do you wish to continue");
                    builder.setTitle("Do you want to delete this mesaage");
                    builder.setCancelable(true);
                    builder.setIcon(AppCompatResources.getDrawable(ChatActivity.this, R.drawable.logo));
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        progressBar.setVisibility(View.VISIBLE);
                        imageView2.setEnabled(false);
                        Toast.makeText(ChatActivity.this, "Deleting Please wait a moment", Toast.LENGTH_SHORT).show();
                        chatRefrence.child("chats").child(myuid).child(uid).child(messageId).removeValue().addOnCompleteListener(task -> {
                            if(task.isSuccessful())
                            {
                                chatRefrence.child("chats").child(uid).child(myuid).child(messageId).removeValue().addOnCompleteListener(task14 -> {
                                    if(task14.isSuccessful())
                                    {
                                        long lastTime=messageList.get(position).getTimestamp();
                                        messageList.remove(position);
                                        if(messageList.isEmpty())
                                        {
                                            final Map<String, Object> lastMap = new HashMap<>();
                                            lastMap.put("last","");
                                            lastMap.put("timestamp",lastTime);
                                            chatRefrence.child("friends").child(uid).child(myuid).updateChildren(lastMap).addOnCompleteListener(task13 -> {
                                                if(task13.isSuccessful())
                                                {
                                                    chatRefrence.child("friends").child(myuid).child(uid).updateChildren(lastMap).addOnCompleteListener(task12 -> {
                                                        if(task12.isSuccessful())
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
                                                    });
                                                }
                                                else
                                                {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    imageView2.setEnabled(true);
                                                    Toast.makeText(ChatActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        else
                                        {
                                            lastTime=messageList.get(messageList.size()-1).getTimestamp();
                                            final String Lastmsg=messageList.get(messageList.size()-1).getMessage();

                                            final Map<String, Object> lastMap = new HashMap<>();
                                            lastMap.put("last",Lastmsg);
                                            lastMap.put("timestamp",lastTime);
                                            chatRefrence.child("friends").child(uid).child(myuid).updateChildren(lastMap).addOnCompleteListener(task1 -> {
                                                if(task1.isSuccessful())
                                                {
                                                    chatRefrence.child("friends").child(myuid).child(uid).updateChildren(lastMap).addOnCompleteListener(task11 -> {
                                                        if(task11.isSuccessful())
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
                                                    });
                                                }
                                                else
                                                {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    imageView2.setEnabled(true);
                                                    Toast.makeText(ChatActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        chatAdapter.notifyDataSetChanged();
                                    }
                                    else
                                    {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        imageView2.setEnabled(true);
                                        chatAdapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());
                                        Toast.makeText(ChatActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else
                            {
                                progressBar.setVisibility(View.INVISIBLE);
                                imageView2.setEnabled(true);
                                chatAdapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());
                                Toast.makeText(ChatActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                    builder.setNegativeButton("No", (dialog, which) -> {
                        chatAdapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());
                        dialog.cancel();
                    });
                    builder.setOnCancelListener(dialog -> chatAdapter.notifyItemChanged(viewHolder.getBindingAdapterPosition()));
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    imageView2.setEnabled(true);
                    chatAdapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags)
        {
            case Configuration.UI_MODE_NIGHT_YES:
                currModeImage = R.drawable.chat_bg_dark_default;
                bgimage.setImageResource(R.drawable.chat_bg_dark_default);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                currModeImage = R.drawable.chat_bg_light_default;
                bgimage.setImageResource(R.drawable.chat_bg_light_default);
                break;
        }
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("Wallpaper", Context.MODE_PRIVATE);
        File []files = directory.listFiles();
        File imagefile = null;
        if(files!=null)
        {
            for (File file:files){
                if(file.getName().equals(uid + ".jpg")){
                    imagefile = file;
                    break;
                }
                else if(file.getName().equals("all.jpg")){
                    imagefile = file;
                }
            }
        }
        if(imagefile != null)
        {
            try {
                FileInputStream fileInputStream = new FileInputStream(imagefile);
                Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                Glide.with(getApplicationContext())
                        .load(bitmap)
                        .fitCenter()
                        .centerCrop()
                        .into(bgimage);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ChatActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }

        }
        myuid = firebaseUser.getUid();
        wallpaperReference =  FirebaseDatabase.getInstance().getReference().child("wallpapers").child(myuid).child(uid);
        wallpaperReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    try{
                        wallpaperModel = snapshot.getValue(WallpaperModel.class);
                        if(wallpaperModel != null) {
                            changeImageBgFromUrl(wallpaperModel.getUrl(), wallpaperModel.getTimestamp());
                        } else {
                            Toast.makeText(ChatActivity.this, "Image not found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e){
                        Toast.makeText(ChatActivity.this, "Failed to fetch data from server", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeImageBgFromUrl(String url, Long timestamp) {
        localReference = FirebaseDatabase.getInstance().getReference().child("local_wallpaper").child(myuid).child(uid);
        localReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    try {
                        if(snapshot.hasChild("timestamp")) {
                            Long ts = snapshot.child("timestamp").getValue(Long.class);
                            if (ts==null || timestamp > ts) {
                                changeWallpaperFromUrl(url);
                            }
                        }
                    } catch (Exception e){
                        changeWallpaperFromUrl(url);
                    }
                } else {
                    changeWallpaperFromUrl(url);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Do nothing
            }
        });
    }

    private void changeWallpaperFromUrl(String url) {
        if(!url.equals("localImage")) {
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .centerCrop()
                    .placeholder(currModeImage)
                    .error(currModeImage);
            Glide.with(this.getApplicationContext()).load(url).apply(options).into(bgimage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.chat_wallpaper)
        {
            Intent wallpaperIntent = new Intent(ChatActivity.this,WallpaperActivity.class);
            wallpaperIntent.putExtra("uid",uid);
            wallpaperIntent.putExtra("myUid", myuid);
            wallpaperIntent.putExtra("profileImage", currUserImage);
            wallpaperIntent.putExtra("username", currUserName);
            startActivity(wallpaperIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setView(MyUserData myUserData) {
        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp);
        Glide.with(getApplicationContext()).load(myUserData.getThumb()).apply(options).into(imageView);
        textView.setText(myUserData.getName());
        online = myUserData.getOnline();
        imageView1.setVisibility(View.INVISIBLE);
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
                //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",Locale.getDefault());
                Date date = new Date();
                try {
                    Date date1 = new Date(Long.parseLong(online));
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
                    } else if ((numOfDayPass >= 30) && (numOfDayPass < 365)) {
                        int months = (int) numOfDayPass / 30;
                        if (months > 1) {
                            lastsn = months + " months ago";
                        } else {
                            lastsn = months + " month ago";
                        }
                    } else if (numOfDayPass >= 365) {
                        int years = (int) numOfDayPass / 365;

                        if (years > 1) {
                            lastsn = "few years ago";
                        } else {
                            lastsn = "1 year ago";
                        }
                    } else {
                        lastsn = online;
                    }
                    textView1.setText(String.format("Last seen %s", lastsn));
                } catch (Exception e) {
                    textView1.setText(String.format("Last seen at: %s", online));
                }
                break;
        }
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

        Map<String, Object> messageUserMap = new HashMap<>();
        messageUserMap.put(currUserrefPath+"/"+pushId,messageMap);
        messageUserMap.put(chatUserRefPath+"/"+pushId,messageMap);
        messageUserMap.put("friends/"+myuid+"/"+uid+"/last",message);
        messageUserMap.put("friends/"+uid+"/"+myuid+"/last",message);
        messageUserMap.put("friends/"+myuid+"/"+uid+"/timestamp",ServerValue.TIMESTAMP);
        messageUserMap.put("friends/"+uid+"/"+myuid+"/timestamp",ServerValue.TIMESTAMP);
        chatRefrence.updateChildren(messageUserMap, (error, ref) -> {
            if(error!=null)
            {
                Toast.makeText(ChatActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        editText.setText("");
    }
}