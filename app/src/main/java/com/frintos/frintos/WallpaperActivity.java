package com.frintos.frintos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.frintos.frintos.Model.WallpaperModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WallpaperActivity extends AppCompatActivity {
    ImageView imageView, imageView1, imageView2;
    TextView textView;
    Bitmap bmp;
    String uid, myUid;
    int image;
    DatabaseReference databaseReference, databaseReference1, localReference;
    ConstraintLayout constraintLayout, constraintLayout1;
    WallpaperModel wallpaperModel;
    int currModeImage;
    ProgressBar progressBar;
    Button button1, button2, button3, button4, button5;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        imageView = findViewById(R.id.imageView15);
        progressBar = findViewById(R.id.progressBar13);
        button1 = findViewById(R.id.delete_image);
        button2 = findViewById(R.id.save_for_this_chat);
        button3 = findViewById(R.id.save_for_all_changes);
        button4 = findViewById(R.id.change_for_both);
        button5 = findViewById(R.id.change_wallpaper);
        imageView1 = findViewById(R.id.head_image);
        textView = findViewById(R.id.head_title);
        constraintLayout = findViewById(R.id.received_message);
        constraintLayout1 = findViewById(R.id.sent_message);
        TextView time1 = constraintLayout.findViewById(R.id.textView24);
        TextView time2 = constraintLayout1.findViewById(R.id.textView21);
        imageView2 = constraintLayout.findViewById(R.id.imageView9);
        time1.setVisibility(View.INVISIBLE);
        time2.setVisibility(View.INVISIBLE);
        uid = "all";
        uid = getIntent().getStringExtra("uid");
        myUid = getIntent().getStringExtra("myUid");
        String username = getIntent().getStringExtra("username");
        String profileImage = getIntent().getStringExtra("profileImage");
        if(username!=null && !username.equals("Frintos")){
            String myUsername = username.split(" ")[0];
            button4.setText("Apply to "+myUsername+ " as well");
        }
        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp);
        Glide.with(getApplicationContext()).load(profileImage).apply(options).into(imageView1);
        Glide.with(getApplicationContext()).load(profileImage).apply(options).into(imageView2);
        textView.setText(username);
        bmp = null;
        image = 0;
        currModeImage = R.drawable.chat_bg_light_default;
        databaseReference =  FirebaseDatabase.getInstance().getReference().child("wallpapers").child(myUid).child(uid);
        localReference = FirebaseDatabase.getInstance().getReference().child("local_wallpaper").child(myUid).child(uid);
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("wallpapers").child(uid).child(myUid);
        databaseReference.keepSynced(true);
        databaseReference1.keepSynced(true);
        localReference.keepSynced(true);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags)
        {
            case Configuration.UI_MODE_NIGHT_YES:
                currModeImage = R.drawable.chat_bg_dark_default;
                imageView.setImageResource(R.drawable.chat_bg_dark_default);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                currModeImage = R.drawable.chat_bg_light_default;
                imageView.setImageResource(R.drawable.chat_bg_light_default);
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
                    image = 2;
                    break;
                }
                else if(file.getName().equals("all.jpg")){
                    imagefile = file;
                    image = 1;
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
                        .into(imageView);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(WallpaperActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    try{
                        wallpaperModel = snapshot.getValue(WallpaperModel.class);
                        if(wallpaperModel != null) {
                            changeImageBgFromUrl(wallpaperModel.getUrl(), wallpaperModel.getTimestamp());
                        } else {
                            Toast.makeText(WallpaperActivity.this, "Image not found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e){
                        Toast.makeText(WallpaperActivity.this, "Failed to fetch data from server", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeImageBgFromUrl(String url, Long timestamp) {
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
            image = 3;
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .centerCrop()
                    .placeholder(currModeImage)
                    .error(currModeImage);
            Glide.with(this.getApplicationContext()).load(url).apply(options).into(imageView);
        }
    }

    public void changeWallpaper(View view) {
        ImagePicker.with(this)
                .crop()
                .start();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data!=null) {
            uri = data.getData();
            try {
                bmp = getBitmapFromUri(uri);
                Glide.with(getApplicationContext())
                        .load(bmp)
                        .fitCenter()
                        .centerCrop()
                        .into(imageView);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(WallpaperActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void saveForThisChat(View view) {
        if(bmp==null) {
            Toast.makeText(WallpaperActivity.this, "Choose a file first", Toast.LENGTH_SHORT).show();
            return;
        }
        if(saveToInternalStorage(uid,bmp)){
            localReference.child("timestamp").setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(WallpaperActivity.this, "Image Saved Success fully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WallpaperActivity.this, "Error while Saving", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(WallpaperActivity.this, "Error while Saving", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveForAllChanges(View view) {
        if(bmp==null) {
            Toast.makeText(WallpaperActivity.this, "Choose a file first", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to apply this wallpaper to all the chats");
        builder.setMessage("This wallpaper will be applied to all the chats excluding the chats which you have customized wallpaper");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            if(saveToInternalStorage("all",bmp)){
                Toast.makeText(WallpaperActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(WallpaperActivity.this, "Error while Saving", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean saveToInternalStorage(String filename, Bitmap bitmap)
    {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("Wallpaper", Context.MODE_PRIVATE);
        File file = new File(directory, filename + ".jpg");
        if (!file.exists()) {
            System.out.println("path "+file.toString());
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                fos.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public void deleteImage(View view) {
        if (image == 0) {
            Toast.makeText(WallpaperActivity.this, "Already the default wallpaper", Toast.LENGTH_SHORT).show();
        } else if (image == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Do you really want to delete this wallpaper");
            builder.setMessage("This wallpaper is set as default for all users. Do you want to proceed");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("Wallpaper", Context.MODE_PRIVATE);
                File file = new File(directory, "all.jpg");
                boolean deleted = file.delete();
                if (deleted) {
                    image = 0;
                    int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                    switch (nightModeFlags) {
                        case Configuration.UI_MODE_NIGHT_YES:
                            imageView.setImageResource(R.drawable.chat_bg_dark_default);
                            break;
                        case Configuration.UI_MODE_NIGHT_NO:
                            imageView.setImageResource(R.drawable.chat_bg_light_default);
                            break;
                    }
                } else {
                    Toast.makeText(WallpaperActivity.this, "Failed To Delete", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if (image == 2){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Do you really want to delete this wallpaper");
            builder.setMessage("This wallpaper is set as wallpaper for this chat only. Do you want to proceed");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                localReference.removeValue().addOnCompleteListener(task2 -> {
                    if(task2.isSuccessful()){
                        ContextWrapper cw = new ContextWrapper(getApplicationContext());
                        File directory = cw.getDir("Wallpaper", Context.MODE_PRIVATE);
                        File file = new File(directory,uid+".jpg");
                        boolean deleted = file.delete();
                        if(deleted) {
                            image = 0;
                            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                            switch (nightModeFlags) {
                                case Configuration.UI_MODE_NIGHT_YES:
                                    imageView.setImageResource(R.drawable.chat_bg_dark_default);
                                    break;
                                case Configuration.UI_MODE_NIGHT_NO:
                                    imageView.setImageResource(R.drawable.chat_bg_light_default);
                                    break;
                            }
                        } else {
                            Toast.makeText(WallpaperActivity.this, "Failed To Delete", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(WallpaperActivity.this, "Failed to delete image", Toast.LENGTH_SHORT).show();
                    }
                    enableButtonsAndHideProgress();
                });
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Do you really want to delete this wallpaper");
            builder.setMessage("This wallpaper is set as wallpaper for both users.\nRemoving this background will lead to removal from both users.");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                disableButtonsAndShowProgress();
                databaseReference.removeValue().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        databaseReference1.removeValue().addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){
                                image = 0;
                                int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                                switch (nightModeFlags) {
                                    case Configuration.UI_MODE_NIGHT_YES:
                                        imageView.setImageResource(R.drawable.chat_bg_dark_default);
                                        break;
                                    case Configuration.UI_MODE_NIGHT_NO:
                                        imageView.setImageResource(R.drawable.chat_bg_light_default);
                                        break;
                                }
                                Toast.makeText(WallpaperActivity.this, "Image deleted", Toast.LENGTH_SHORT).show();
                                enableButtonsAndHideProgress();
                            } else {
                                enableButtonsAndHideProgress();
                                Toast.makeText(WallpaperActivity.this, "Failed to delete image", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        enableButtonsAndHideProgress();
                        Toast.makeText(WallpaperActivity.this, "Failed To delete Image", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void applyForBoth(View view) {
        if(uri!=null) {
            disableButtonsAndShowProgress();
            if(image == 2){
                localReference.removeValue().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        ContextWrapper cw = new ContextWrapper(getApplicationContext());
                        File directory = cw.getDir("Wallpaper", Context.MODE_PRIVATE);
                        File file = new File(directory, uid + ".jpg");
                        boolean deleted = file.delete();
                        if (deleted) {
                            image = 0;
                            uplaodImageToServer();
                        } else {
                            enableButtonsAndHideProgress();
                            Toast.makeText(WallpaperActivity.this, "Failed To delete existing", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        enableButtonsAndHideProgress();
                        Toast.makeText(WallpaperActivity.this, "Failed to delete existing image", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                uplaodImageToServer();
            }
        } else {
            Toast.makeText(this, "Please choose a file first", Toast.LENGTH_SHORT).show();
        }
    }

    private void uplaodImageToServer() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("wallpapers").child(myUid).child(uid + ".jpg");
        storageReference.putFile(uri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String url = downloadUri.toString();
                updateDatabase(url);
            } else {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                enableButtonsAndHideProgress();
            }
        });
    }

    private void updateDatabase(String url) {
        if(url != null && !url.trim().isEmpty()) {
            Map<String, Object> mp = new HashMap<String, Object>();
            mp.put("url", url);
            mp.put("setFrom", myUid);
            mp.put("timestamp", ServerValue.TIMESTAMP);
            databaseReference.setValue(mp).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        databaseReference1.setValue(mp).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                enableButtonsAndHideProgress();
                                if(task.isSuccessful()){
                                    image = 3;
                                    Toast.makeText(WallpaperActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(WallpaperActivity.this, "Failed to update data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        enableButtonsAndHideProgress();
                        Toast.makeText(WallpaperActivity.this, "Failed to update data", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void disableButtonsAndShowProgress(){
        button1.setEnabled(false);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);
        button5.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void enableButtonsAndHideProgress(){
        button1.setEnabled(true);
        button2.setEnabled(true);
        button3.setEnabled(true);
        button4.setEnabled(true);
        button5.setEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
