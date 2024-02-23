package com.frintos.frintos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.frintos.frintos.Model.MyUserData;
import com.frintos.frintos.Model.usersData;
import com.frintos.frintos.RecyclerAdapter.AdapterClass;
import com.frintos.frintos.Utility.VerifiedUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class allUsers extends AppCompatActivity {
    ProgressBar progressBar;
    DatabaseReference databaseReference;
    ImageButton imageButton;
    EditText editText;
    RecyclerView recyclerView;
    ArrayList<MyUserData> usersDataList;
    VerifiedUsers verifiedUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        imageButton=findViewById(R.id.imageButton);
        imageButton.setEnabled(false);
        final String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        editText=findViewById(R.id.editText2);
        progressBar=findViewById(R.id.progressbar5);
        recyclerView=findViewById(R.id.recyclerView);
        progressBar.setVisibility(View.VISIBLE);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(allUsers.this,DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(allUsers.this, R.drawable.recycler_decor)));
        recyclerView.addItemDecoration(dividerItemDecoration);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    usersDataList=new ArrayList<>();
                    for(DataSnapshot ds: snapshot.getChildren())
                    {
                        if(!Objects.equals(ds.getKey(), uid))
                        {
                            usersData ud=ds.getValue(usersData.class);
                            MyUserData myUserData=new MyUserData();
                            if (ud != null) {
                                myUserData.setName(ud.getName());
                                myUserData.setOnline(ud.getOnline().toString());
                                myUserData.setPicture(ud.getPicture());
                                myUserData.setThumb(ud.getThumb());
                                myUserData.setStatus(ud.getStatus());
                                myUserData.setToken(ud.getToken());
                                myUserData.setUid(ds.getKey());
                                myUserData.setUpvotes(ud.getUpvotes());
                                usersDataList.add(myUserData);
                            }
                        }
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    imageButton.setEnabled(true);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        imageButton.setOnClickListener(v -> SearchFrint());
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                imageButton.performClick();
            }
            return false;
        });
    }

    private void SearchFrint() {
        progressBar.setVisibility(View.VISIBLE);
        imageButton.setEnabled(false);
        ArrayList<MyUserData> mUsersDataList=new ArrayList<>();
        String item=editText.getText().toString();
        if(!item.equals("")) {
            for (MyUserData ud : usersDataList) {
                if (ud.getName().toLowerCase().contains(item.toLowerCase())) {
                    mUsersDataList.add(ud);
                }
            }
        }
        progressBar.setVisibility(View.INVISIBLE);
        imageButton.setEnabled(true);
        verifiedUsers = new VerifiedUsers(allUsers.this){
            @Override
            protected void onFetched(boolean success){
                AdapterClass adapterClass = new AdapterClass(mUsersDataList,allUsers.this, verifiedUsers);
                recyclerView.setAdapter(adapterClass);
            }
        };
    }
}