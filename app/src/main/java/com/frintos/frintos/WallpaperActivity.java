package com.frintos.frintos;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WallpaperActivity extends AppCompatActivity {
    ImageView imageView;
    Bitmap bmp;
    String uid;
    int image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        imageView = findViewById(R.id.imageView15);
        uid = "all";
        uid=getIntent().getStringExtra("uid");
        bmp = null;
        image = 0;
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags)
        {
            case Configuration.UI_MODE_NIGHT_YES:
                imageView.setImageResource(R.drawable.chat_bg_dark_default);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
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
                imageView.setImageBitmap(BitmapFactory.decodeStream(fileInputStream));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(WallpaperActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }

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
            Uri resultUri = data.getData();
            try {
                bmp = getBitmapFromUri(resultUri);
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
            Toast.makeText(WallpaperActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(WallpaperActivity.this, "Error while Saving", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveForAllChanges(View view) {
        if(bmp==null) {
            Toast.makeText(WallpaperActivity.this, "Choose a file first", Toast.LENGTH_SHORT).show();
            return;
        }
        if(saveToInternalStorage("all",bmp)){
            Toast.makeText(WallpaperActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(WallpaperActivity.this, "Error while Saving", Toast.LENGTH_SHORT).show();
        }
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
        if(image == 0) {
            Toast.makeText(WallpaperActivity.this, "Already the default wallpaper", Toast.LENGTH_SHORT).show();
        } else if(image == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Do you really want to delete this wallpaper");
            builder.setMessage("This wallpaper is set as default for all users. Do you want to proceed");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("Wallpaper", Context.MODE_PRIVATE);
                File file = new File(directory,"all.jpg");
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
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Do you really want to delete this wallpaper");
            builder.setMessage("This wallpaper is set as wallpaper for this chat only. Do you want to proceed");
            builder.setPositiveButton("Yes", (dialog, which) -> {
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
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}