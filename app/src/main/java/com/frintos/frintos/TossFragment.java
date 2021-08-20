package com.frintos.frintos;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.frintos.frintos.Model.TossModel;
import com.frintos.frintos.RecyclerAdapter.TossAdaptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TossFragment extends Fragment {
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    List<TossModel> tossModels=new ArrayList<>();
    TossAdaptor tossAdaptor;
    DatabaseReference databaseReference;
    ImageButton imageButton;
    TextView textView;
    ProgressBar progressBar;
    public TossFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.fragment_toss, container, false);
        imageButton=view.findViewById(R.id.imageButton3);
        linearLayoutManager=new LinearLayoutManager(view.getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView=view.findViewById(R.id.recyclerView5);
        textView=view.findViewById(R.id.textView18);
        progressBar=view.findViewById(R.id.progressBar10);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        tossAdaptor=new TossAdaptor(view.getContext(),tossModels,uid);
        recyclerView.setAdapter(tossAdaptor);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("toss").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot ds: snapshot.getChildren())
                    {
                        TossModel tossModel=ds.getValue(TossModel.class);
                        tossModels.add(tossModel);
                        tossAdaptor.notifyItemInserted(tossModels.size()-1);
                        recyclerView.scrollToPosition(tossModels.size()-1);
                    }
                    textView.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
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
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),TossWritingActivity.class);
            startActivity(intent);
        });
        return view;
    }
}