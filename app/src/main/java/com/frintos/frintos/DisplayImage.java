package com.frintos.frintos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DisplayImage extends AppCompatActivity {
    Button button,button1;
    ProgressBar progressBar;
    ImageView imageView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference mStorageRef;
    String uid,name;
    int RequestCode=28;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        Window w=getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Intent intent=getIntent();
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        final String image=intent.getStringExtra("image");
        final String thumb=intent.getStringExtra("thumb");
        final String flag=intent.getStringExtra("flg");
        name=intent.getStringExtra("name");
        uid=intent.getStringExtra("uid");
        button=findViewById(R.id.button6);
        button1=findViewById(R.id.button5);
        imageView=findViewById(R.id.imageView3);
        progressBar=findViewById(R.id.progressBar4);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp);

        Glide.with(getApplicationContext()).load(image).apply(options).into(imageView);
        progressBar.setVisibility(View.INVISIBLE);
        if(flag.equals("false"))
        {
            button.setEnabled(false);
            button.setVisibility(View.INVISIBLE);
        }
        if(image.equals("default"))
        {
            button.setVisibility(View.INVISIBLE);
            button1.setVisibility(View.INVISIBLE);
        }
        else
        {
            mStorageRef=firebaseStorage.getReferenceFromUrl(image);
        }
        button.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            final StorageReference thumb_refrence1 = firebaseStorage.getReferenceFromUrl(thumb);
            final StorageReference storageReference1 = firebaseStorage.getReferenceFromUrl(image);
            thumb_refrence1.delete().addOnSuccessListener(aVoid -> storageReference1.delete().addOnSuccessListener(aVoid1 -> {
                    databaseReference.child("picture").setValue("default");
                    databaseReference.child("thumb").setValue("default");
                    progressBar.setVisibility(View.INVISIBLE);
                    finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(DisplayImage.this, "Error while deleting "+e.toString(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            })).addOnFailureListener(e -> {
                Toast.makeText(DisplayImage.this, "Error while deleting thumb "+e.toString(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            });
        });
        button1.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},RequestCode);
                }
                else
                {
                    download();
                }
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==28){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                download();
            } else {
                Toast.makeText(DisplayImage.this, "Sorry you denied the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void download()
    {
        DateFormat df = new SimpleDateFormat("yyyyMMddhhmmssS", Locale.getDefault());
        final String filename = name+df.format(new Date()) + ".jpg";
        mStorageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String url=uri.toString();
            DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));
            request.setDescription("Downloading profile picture");
            request.setTitle(filename);
            request.setMimeType("image/jpg");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,"Frintos/FrintosProfilePictures/"+filename);
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);
            Toast.makeText(DisplayImage.this, "Started Downloading", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(exception -> Toast.makeText(DisplayImage.this, "Error occurred", Toast.LENGTH_SHORT).show());
    }
}