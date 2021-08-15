package com.example.frintos;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    EditText editText;
    EditText editText1;
    EditText editText2;
    Button button;
    private ProgressBar progressBar;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    GoogleSignInClient googleSignInClient;
    String deviceToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        editText=findViewById(R.id.editText);
        editText1=findViewById(R.id.editText1);
        editText2=findViewById(R.id.ccp);
        button=findViewById(R.id.button);
        progressBar=findViewById(R.id.progressBar);
        currentUser=mAuth.getCurrentUser();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayName=editText.getText().toString();
                String phoneNumber=editText1.getText().toString();
                if(editText2.getText().toString().isEmpty())
                    editText2.setText(R.string.countrycode);
                if(TextUtils.isEmpty(displayName)||TextUtils.isEmpty(phoneNumber))
                {
                    if(TextUtils.isEmpty(displayName))
                        editText.setError("You Should have a name to proceed");
                    if(TextUtils.isEmpty(phoneNumber))
                    {
                        if(displayName.isEmpty())
                            editText1.setError("Please give me your phone number too");
                        else
                            editText1.setError(displayName+" please give me your phone number");
                    }
                }
                else {
                    String completePhone=editText2.getText().toString()+phoneNumber;
                    progressBar.setVisibility(View.VISIBLE);
                    button.setEnabled(false);
                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(completePhone)
                                    .setTimeout(60L, TimeUnit.SECONDS)
                                    .setActivity(LoginActivity.this)
                                    .setCallbacks(callbacks)
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });
        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(LoginActivity.this, "Something went wrong while verification "+e.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull final String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        Intent intent= new Intent(LoginActivity.this,otpcheck.class);
                        intent.putExtra("credentials",s);
                        intent.putExtra("name",editText.getText().toString());
                        startActivity(intent);
                }, 5000);
            }
        };
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            String uid=user.getUid();
                            FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                            mDatabase=firebaseDatabase.getReference().child("users").child(uid);
                            mDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()) {
                                        final String dataname=dataSnapshot.child("name").getValue().toString();
                                        final String dataimage=dataSnapshot.child("picture").getValue().toString();
                                        final String datathumb=dataSnapshot.child("thumb").getValue().toString();
                                        final String datastatus=dataSnapshot.child("status").getValue().toString();
                                        final String dataupvotes=dataSnapshot.child("upvotes").getValue().toString();
                                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task1 -> {
                                            if (!task1.isSuccessful()) {
                                                Log.w("failedInstance", "getInstanceId failed", task.getException());
                                                return;
                                            }

                                            deviceToken = task1.getResult();
                                            updateToken(dataname,dataimage,datathumb,datastatus,deviceToken,dataupvotes);

                                        });
                                    }
                                    else {

                                        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(
                                                s -> {
                                                    deviceToken=s;
                                                    HashMap<String,String>userdatamap= new HashMap<>();
                                                    userdatamap.put("name",editText.getText().toString());
                                                    userdatamap.put("online","true");
                                                    userdatamap.put("status", "Proud Frintoser");
                                                    userdatamap.put("picture","default");
                                                    userdatamap.put("thumb","default");
                                                    userdatamap.put("token",deviceToken);
                                                    userdatamap.put("upvotes","0");
                                                    mDatabase.setValue(userdatamap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task12) {
                                                            if(task12.isSuccessful())
                                                            {
                                                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Toast.makeText(LoginActivity.this, "Please Wait", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                },3000);
                                                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(LoginActivity.this, "Unknown Error Occurred", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(LoginActivity.this, "Error while generating Token", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(LoginActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                            button.setEnabled(true);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

    }
    public void withgoogle(View view)
    {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        someActivityResultLauncher.launch(signInIntent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) {
                                firebaseAuthWithGoogle(account.getIdToken());
                            }
                        } catch (ApiException e) {
                            Toast.makeText(LoginActivity.this, "Login Failed due to: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    private void firebaseAuthWithGoogle(String idToken){
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this,task->{
            if(task.isSuccessful())
            {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if(firebaseUser != null)
                {
                    String uid = firebaseUser.getUid();
                    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                    mDatabase=firebaseDatabase.getReference().child("users").child(uid);
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                final String dataname=dataSnapshot.child("name").getValue().toString();
                                final String dataimage=dataSnapshot.child("picture").getValue().toString();
                                final String datathumb=dataSnapshot.child("thumb").getValue().toString();
                                final String datastatus=dataSnapshot.child("status").getValue().toString();
                                final String dataupvotes=dataSnapshot.child("upvotes").getValue().toString();
                                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task1 -> {
                                    if (!task1.isSuccessful()) {
                                        Log.w("failedInstance", "getInstanceId failed", task.getException());
                                        return;
                                    }

                                    deviceToken = task1.getResult();
                                    updateToken(dataname,dataimage,datathumb,datastatus,deviceToken,dataupvotes);

                                });
                            }
                            else {

                                FirebaseMessaging.getInstance().getToken().addOnSuccessListener(
                                        s -> {
                                            Uri pic = firebaseUser.getPhotoUrl();
                                            String picture = "default";
                                            if(pic != null){
                                                picture = pic.toString();
                                            }
                                            deviceToken=s;
                                            HashMap<String,String>userdatamap= new HashMap<>();
                                            userdatamap.put("name",firebaseUser.getDisplayName());
                                            userdatamap.put("online","true");
                                            userdatamap.put("status", "Proud Frintoser");
                                            userdatamap.put("picture",picture);
                                            userdatamap.put("thumb",picture);
                                            userdatamap.put("token",deviceToken);
                                            userdatamap.put("upvotes","0");
                                            mDatabase.setValue(userdatamap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task12) {
                                                    if(task12.isSuccessful())
                                                    {
                                                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(LoginActivity.this, "Please Wait", Toast.LENGTH_SHORT).show();
                                                            }
                                                        },3000);
                                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(LoginActivity.this, "Unknown Error Occurred", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, "Error while generating Token", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(LoginActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser!=null)
        {
            Intent log_intent=new Intent(LoginActivity.this, MainActivity.class);
            log_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(log_intent);
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
        userdatamap.put("upvotes", upvotes);
        mDatabase.setValue(userdatamap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Unknown Error Occurred", Toast.LENGTH_SHORT).show();
                    button.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
