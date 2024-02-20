package com.frintos.frintos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.frintos.frintos.Model.usersData;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class DisplayUser extends AppCompatActivity {
    TextView textView;
    EditText editText;
    Button button,buttton1;
    ImageView imageView;
    FirebaseUser firebaseUser;
    String uid,urlThumb;
    usersData userd;
    ProgressBar progressBar,image_progressbar;
    DatabaseReference databaseReference;
    StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user);
        image_progressbar=findViewById(R.id.progressBar3);
        textView=findViewById(R.id.textView7);
        editText=findViewById(R.id.textView8);
        button=findViewById(R.id.button3);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        progressBar=findViewById(R.id.progressBar2);
        buttton1=findViewById(R.id.button4);
        imageView=findViewById(R.id.imageView2);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        uid=firebaseUser.getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userd=dataSnapshot.getValue(usersData.class);
                if (userd != null) {
                    textView.setText(userd.getName());
                    editText.setText(userd.getStatus());
                    progressBar.setVisibility(View.INVISIBLE);
                    if (!userd.getPicture().equals("default")) {
                        RequestOptions options = new RequestOptions()
                                .circleCrop()
                                .placeholder(R.drawable.ic_account_circle_black_24dp)
                                .error(R.drawable.ic_account_circle_black_24dp);

                        Glide.with(getApplicationContext()).load(userd.getPicture()).apply(options).into(imageView);
                    }
                }
                image_progressbar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        imageView.setOnClickListener(v -> {
            Intent intent=new Intent(DisplayUser.this,DisplayImage.class);
            intent.putExtra("image",userd.getPicture());
            intent.putExtra("thumb",userd.getThumb());
            intent.putExtra("name",userd.getName());
            intent.putExtra("uid",uid);
            intent.putExtra("flg","true");
            startActivity(intent);
        });
    }
    public void updatePicture(View view){
        image_progressbar.setVisibility(View.VISIBLE);

        ImagePicker.with(this)
                .cropSquare()
                .maxResultSize(1080, 1080)
                .start();
    }

    public void updateStatus(View view) {
        progressBar.setVisibility(View.VISIBLE);
        usersData usersData=new usersData();
        usersData.setName(userd.getName());
        usersData.setOnline(userd.getOnline());
        usersData.setPicture(userd.getPicture());
        usersData.setThumb(userd.getThumb());
        usersData.setToken(userd.getToken());
        usersData.setUpvotes(userd.getUpvotes());
        String status=editText.getText().toString();
        usersData.setStatus(status);
        databaseReference.setValue(usersData).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                editText.setText(getString(R.string.error));
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        image_progressbar.setVisibility(View.INVISIBLE);

        if (resultCode == Activity.RESULT_OK) {
            final Uri resultUri = data.getData();
            File thumb_file=new File(resultUri.getPath());
            Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(thumb_file.getPath()), 200, 200);
            ByteArrayOutputStream baos= new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG,70,baos);
            byte[] thumb_byte=baos.toByteArray();
            final StorageReference thumb_refrence=mStorageRef.child("ProfilePictures").child("thumbs").child(uid+".jpg");
            final StorageReference storageReference=mStorageRef.child("ProfilePictures").child(uid+".jpg");
            final UploadTask uploadTask = storageReference.putFile(resultUri);
            UploadTask uploadTask1=thumb_refrence.putBytes(thumb_byte);
            Task<Uri> urlTask1 = uploadTask1.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(DisplayUser.this, "Task Failed", Toast.LENGTH_SHORT).show();
                }
                return thumb_refrence.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    urlThumb = downloadUri.toString();

                    Task<Uri> urlTask = uploadTask.continueWithTask(task1 -> {
                        if (!task1.isSuccessful()) {
                            Toast.makeText(DisplayUser.this, "Task Failed", Toast.LENGTH_SHORT).show();
                        }
                        return storageReference.getDownloadUrl();
                    }).addOnCompleteListener(task12 -> {
                        if (task12.isSuccessful()) {
                            Uri downloadUri1 = task12.getResult();
                            String url = downloadUri1.toString();
                            usersData usersData = new usersData();
                            usersData.setName(userd.getName());
                            usersData.setOnline(userd.getOnline());
                            usersData.setPicture(url);
                            usersData.setThumb(urlThumb);
                            usersData.setStatus(userd.getStatus());
                            usersData.setToken(userd.getToken());
                            usersData.setUpvotes(userd.getUpvotes());
                            databaseReference.setValue(usersData);
                            RequestOptions options = new RequestOptions()
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_account_circle_black_24dp)
                                    .error(R.drawable.ic_account_circle_black_24dp);

                            Glide.with(getApplicationContext()).load(url).apply(options).into(imageView);
                            Toast.makeText(DisplayUser.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                            image_progressbar.setVisibility(View.INVISIBLE);
                        } else {
                            Toast.makeText(DisplayUser.this, "Got Some error", Toast.LENGTH_SHORT).show();
                            image_progressbar.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    Toast.makeText(DisplayUser.this, "Got Some error while generating thumbnail\nPlease upload again", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void dark_theme(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("dark mode", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        editor.putString("dark mode enabled", "yes");
        editor.apply();
    }

    public void lignt_theme(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("dark mode", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        editor.putString("dark mode enabled", "no");
        editor.apply();
    }
}