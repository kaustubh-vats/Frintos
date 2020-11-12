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
import com.example.frintos.RecyclerAdapter.MyRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ChatsFragment extends Fragment {
    RecyclerView recyclerView;
    DatabaseReference friendReference, databaseReference;
    TextView textView;
    ArrayList<MyUserData> usersDataList;
    ProgressBar progressBar;
    View view;
    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView=view.findViewById(R.id.recyclerView3);
        textView=view.findViewById(R.id.textView5);
        progressBar=view.findViewById(R.id.progressBar9);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(view.getContext(), R.drawable.recycler_decor)));
        recyclerView.addItemDecoration(dividerItemDecoration);
        final String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        friendReference= FirebaseDatabase.getInstance().getReference().child("friends").child(uid);
        databaseReference=FirebaseDatabase.getInstance().getReference().child("users");
        friendReference.keepSynced(true);
        databaseReference.keepSynced(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        friendReference.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    usersDataList=new ArrayList<>();
                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                        final String user=ds.getKey();
                        final String lastmsg = ds.child("last").getValue().toString();
                        if (user != null) {
                            databaseReference.child(user).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.exists())
                                    {
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                    else {
                                        final usersData ud = snapshot.getValue(usersData.class);
                                        MyUserData myUserData = new MyUserData();
                                        myUserData.setName(ud.getName());
                                        myUserData.setOnline(ud.getOnline());
                                        myUserData.setPicture(ud.getPicture());
                                        myUserData.setThumb(ud.getThumb());
                                        if(lastmsg.equals(""))
                                            myUserData.setStatus(ud.getOnline());
                                        else
                                            myUserData.setStatus(lastmsg);
                                        myUserData.setToken(ud.getToken());
                                        myUserData.setUpvotes(ud.getUpvotes());
                                        myUserData.setUid(user);
                                        boolean f = true;
                                        for (int i = 0; i < usersDataList.size(); i++) {
                                            if (usersDataList.get(i).getUid().equals(user)) {
                                                usersDataList.set(i, myUserData);
                                                setView(usersDataList, view);
                                                f = false;
                                            }
                                        }
                                        if (f) {
                                            usersDataList.add(myUserData);
                                            setView(usersDataList, view);
                                        }
                                        progressBar.setVisibility(View.INVISIBLE);

                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
                else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setView(ArrayList<MyUserData> usersDataList, View view) {
        progressBar.setVisibility(View.INVISIBLE);
        MyRecyclerAdapter myRecyclerAdapter = new MyRecyclerAdapter(usersDataList,view.getContext());
        recyclerView.setAdapter(myRecyclerAdapter);
        textView.setVisibility(View.INVISIBLE);
    }
}