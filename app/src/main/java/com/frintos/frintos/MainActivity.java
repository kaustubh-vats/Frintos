package com.frintos.frintos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.frintos.frintos.FragmentAdaptor.SectionsPagerAdaptor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ViewPager2 viewPager2;
    SectionsPagerAdaptor sectionsPagerAdaptor;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
            if(firebaseAuth.getCurrentUser()==null){
                Intent log_intent=new Intent(MainActivity.this, LoginActivity.class);
                log_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(log_intent);
                finish();
            }
        };
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(authStateListener);
        bottomNavigationView = findViewById(R.id.bottomnav);
        FirebaseUser currentUser = mAuth.getCurrentUser();
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
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.requests) {
                viewPager2.setCurrentItem(0, true);
            } else if (itemId == R.id.chats) {
                viewPager2.setCurrentItem(1, true);
            } else if (itemId == R.id.toss) {
                viewPager2.setCurrentItem(2, true);
            }
            return true;
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
        int currItemId = item.getItemId();
        if(R.id.account_settings == currItemId) {
                Intent intent = new Intent(MainActivity.this, DisplayUser.class);
                startActivity(intent);
        } else if(R.id.logout == currItemId) {
            logoutandclose();
        } else if(R.id.all_users == currItemId) {
            Intent intent1=new Intent(MainActivity.this,allUsers.class);
            startActivity(intent1);
        } else if(R.id.about_dev == currItemId) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
            startActivity(browserIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutandclose() {
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        databaseReference.child("online").setValue(ServerValue.TIMESTAMP).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                mAuth.signOut();
            }
            else
            {
                Toast.makeText(MainActivity.this, "Failed to logout", Toast.LENGTH_SHORT).show();
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
