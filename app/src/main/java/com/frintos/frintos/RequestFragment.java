package com.frintos.frintos;

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

public class RequestFragment extends Fragment {
    DatabaseReference friendrequestdatabase, databaseReference;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView textView;
    View view;
    ArrayList<MyUserData> usersDataList;
    VerifiedUsers verifiedUsers;

    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_request, container, false);
        final String uid= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser(),"No Current User").getUid();
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
        databaseReference.keepSynced(true);
        usersDataList=new ArrayList<>();
        friendrequestdatabase.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersDataList=new ArrayList<>();
                if(snapshot.exists())
                {
                    System.out.println("snapshot exist");
                    for(DataSnapshot ds: snapshot.getChildren())
                    {
                        if(Objects.requireNonNull(ds.child("type").getValue(),"No Value Found as name type").toString().equals("received"))
                        {
                            final String userId=ds.getKey();
                            if (userId != null) {
                                databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        usersData ud=snapshot.getValue(usersData.class);
                                        MyUserData myUserData=new MyUserData();
                                        if (ud != null) {
                                            myUserData.setName(ud.getName());
                                            myUserData.setPicture(ud.getPicture());
                                            myUserData.setThumb(ud.getThumb());
                                            myUserData.setOnline(ud.getOnline().toString());
                                            myUserData.setStatus(ud.getStatus());
                                            myUserData.setToken(ud.getToken());
                                            myUserData.setUpvotes(ud.getUpvotes());
                                            myUserData.setUid(userId);
                                            usersDataList.add(myUserData);
                                            setView(usersDataList, view);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }
                    if(usersDataList.isEmpty()){
                        progressBar.setVisibility(View.INVISIBLE);
                        setView(usersDataList,view);
                        textView.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    System.out.println("No snapshot exist");
                    progressBar.setVisibility(View.INVISIBLE);
                    setView(usersDataList,view);
                    textView.setVisibility(View.VISIBLE);
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
        verifiedUsers = new VerifiedUsers(view.getContext()){
            @Override
            protected void onFetched(boolean success){
                if(!usersDataList.isEmpty())
                    textView.setVisibility(View.INVISIBLE);
                AdapterClass adapterClass = new AdapterClass(usersDataList,view.getContext(),verifiedUsers);
                recyclerView.setAdapter(adapterClass);
            }
        };
    }
}