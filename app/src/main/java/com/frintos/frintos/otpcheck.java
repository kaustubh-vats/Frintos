package com.frintos.frintos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class otpcheck extends AppCompatActivity {
    EditText editText;
    String name,mAuthCredentials;
    ProgressBar progressBar;
    String deviceToken;
    Button button;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpcheck);
        Intent intent=getIntent();
        name=intent.getStringExtra("name");
        mAuthCredentials=intent.getStringExtra("credentials");
        progressBar=findViewById(R.id.progressBar1);
        button=findViewById(R.id.button2);
        editText=findViewById(R.id.editText5);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                button.performClick();
            }
            return false;
        });
        button.setOnClickListener(v -> {
            String otp=editText.getText().toString();
            if(TextUtils.isEmpty(otp))
                editText.setError(name+" I told you I don't trust anyone... Please Enter OTP");
            else{
                button.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                PhoneAuthCredential phoneAuthCredential= PhoneAuthProvider.getCredential(mAuthCredentials,otp);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }
        });
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    String uid=user.getUid();
                    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                    mDatabase=firebaseDatabase.getReference().child("users").child(uid);
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final String dataname=dataSnapshot.child("name").getValue().toString();
                                final String dataimage=dataSnapshot.child("picture").getValue().toString();
                                final String datathumb=dataSnapshot.child("thumb").getValue().toString();
                                final String datastatus=dataSnapshot.child("status").getValue().toString();
                                final String dataupvotes=dataSnapshot.child("upvotes").getValue().toString();
                                FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
                                    deviceToken=s;
                                    updateToken(dataname,dataimage,datathumb,datastatus,deviceToken,dataupvotes);
                                }).addOnFailureListener(e -> Toast.makeText(otpcheck.this, "Error while generating Token", Toast.LENGTH_SHORT).show());
                            } else {
                                FirebaseMessaging.getInstance().getToken().addOnSuccessListener(instanceIdResult -> {
                                    deviceToken=instanceIdResult;
                                    HashMap<String, String> userdatamap = new HashMap<>();
                                    userdatamap.put("name", name);
                                    userdatamap.put("online","true");
                                    userdatamap.put("status", "Proud Frintoser");
                                    userdatamap.put("picture", "default");
                                    userdatamap.put("thumb", "default");
                                    userdatamap.put("token",deviceToken);
                                    userdatamap.put("upvotes","0");
                                    SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                    editor.putString("tokenId", deviceToken);
                                    editor.apply();
                                    mDatabase.setValue(userdatamap).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Intent intent = new Intent(otpcheck.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(otpcheck.this, "Unknown Error Occurred", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }).addOnFailureListener(e -> Toast.makeText(otpcheck.this, "Error while generating Token", Toast.LENGTH_SHORT).show());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("datacancel","Data base error occurred");
                        }
                    });


                } else {
                        // Sign in failed, display a message and update the UI
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(otpcheck.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                            editText.setError("Be Careful "+name+" it is heartbreaking that you are lying with invalid OTP");
                        }
                        button.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

    }
    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser!=null)
        {
            Intent log_intent=new Intent(otpcheck.this, MainActivity.class);
            log_intent.putExtra("name",name);
            log_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(log_intent);
            finish();
        }
    }
    public void updateToken(String namedata,String imagedata, String thumbdata, String statusdata, String tokendata, String upvotes)
    {
        HashMap<String, String> userdatamap = new HashMap<>();
        userdatamap.put("name", namedata);
        userdatamap.put("online","true");
        userdatamap.put("status", statusdata);
        userdatamap.put("picture", imagedata);
        userdatamap.put("thumb", thumbdata);
        userdatamap.put("token", tokendata);
        userdatamap.put("upvotes",upvotes);
        SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        editor.putString("tokenId", tokendata);
        editor.apply();
        mDatabase.setValue(userdatamap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(otpcheck.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(otpcheck.this, "Unknown Error Occurred", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                button.setEnabled(true);
            }
        });
    }
}
