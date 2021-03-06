package com.example.frintos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.frintos.FragmentAdaptor.SectionsPagerAdaptor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ViewPager2 viewPager2;
    SectionsPagerAdaptor sectionsPagerAdaptor;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.bottomnav);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            logoutUser();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        viewPager2 = findViewById(R.id.viewPager);
        sectionsPagerAdaptor = new SectionsPagerAdaptor(this);
        viewPager2.setAdapter(sectionsPagerAdaptor);
        this.setSupportActionBar(toolbar);
        Intent intent = getIntent();
        boolean flag = intent.getBooleanExtra("flag", false);
        if (currentUser != null && !flag) {
            Intent intent1 = new Intent(MainActivity.this, Welcome.class);
            intent1.putExtra("name", "offline user");
            startActivity(intent1);
            finish();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.requests:
                        viewPager2.setCurrentItem(0, true);
                        break;
                    case R.id.chats:
                        viewPager2.setCurrentItem(1, true);
                        break;
                    case R.id.toss:
                        viewPager2.setCurrentItem(2, true);
                        break;
                }
                return true;
            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch(position)
                {
                    case 0: bottomNavigationView.setSelectedItemId(R.id.requests);
                            break;
                    case 1: bottomNavigationView.setSelectedItemId(R.id.chats);
                            break;
                    case 2: bottomNavigationView.setSelectedItemId(R.id.toss);
                            break;
                }
                super.onPageSelected(position);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            logoutUser();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.account_settings: Intent intent = new Intent(MainActivity.this,DisplayUser.class);
                                        startActivity(intent);
                                        break;
            case R.id.logout: logoutandclose();
                              break;
            case R.id.all_users: Intent intent1=new Intent(MainActivity.this,allUsers.class);
                                 startActivity(intent1);
                                 break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutandclose() {
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        databaseReference.child("online").setValue(dateFormat.format(date)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    mAuth.signOut();
                    Intent log_intent=new Intent(MainActivity.this, LoginActivity.class);
                    log_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(log_intent);
                    finish();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Failed to logout", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void logoutUser(){
        Intent log_intent=new Intent(MainActivity.this, LoginActivity.class);
        log_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(log_intent);
        finish();
    }
}
