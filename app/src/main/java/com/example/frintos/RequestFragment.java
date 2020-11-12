package com.example.frintos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.frintos.Model.MyUserData;
import com.example.frintos.Model.usersData;
import com.example.frintos.RecyclerAdapter.AdapterClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class RequestFragment extends Fragment {
    DatabaseReference friendrequestdatabase, databaseReference;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView textView;
    View view;
    ArrayList<MyUserData> usersDataList;

    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_request, container, false);
        final String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressBar=view.findViewById(R.id.progressBar8);
        recyclerView=view.findViewById(R.id.recyclerView2);
        textView=view.findViewById(R.id.textView11);
        progressBar.setVisibility(View.VISIBLE);
        friendrequestdatabase = FirebaseDatabase.getInstance().getReference().child("friend_req");
        databaseReference=FirebaseDatabase.getInstance().getReference().child("users");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(view.getContext(), R.drawable.recycler_decor)));
        recyclerView.addItemDecoration(dividerItemDecoration);
        friendrequestdatabase.keepSynced(true);
        databaseReference.keepSynced(true);
        usersDataList=new ArrayList<>();
        friendrequestdatabase.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    usersDataList=new ArrayList<>();
                    for(DataSnapshot ds: snapshot.getChildren())
                    {
                        if(ds.child("type").getValue().toString().equals("received"))
                        {
                            final String userId=ds.getKey();
                            if (userId != null) {
                                databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(!snapshot.exists())
                                        {
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                        usersData ud=snapshot.getValue(usersData.class);
                                        MyUserData myUserData=new MyUserData();
                                        myUserData.setName(ud.getName());
                                        myUserData.setPicture(ud.getPicture());
                                        myUserData.setThumb(ud.getThumb());
                                        myUserData.setOnline(ud.getOnline());
                                        myUserData.setStatus(ud.getStatus());
                                        myUserData.setToken(ud.getToken());
                                        myUserData.setUpvotes(ud.getUpvotes());
                                        myUserData.setUid(userId);
                                        usersDataList.add(myUserData);
                                        setView(usersDataList,view);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }
                }
                else
                {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private void setView(ArrayList<MyUserData> usersDataList, View view) {
        progressBar.setVisibility(View.INVISIBLE);
        AdapterClass adapterClass = new AdapterClass(usersDataList,view.getContext());
        recyclerView.setAdapter(adapterClass);
        textView.setVisibility(View.INVISIBLE);
    }
}