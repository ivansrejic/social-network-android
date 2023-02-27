package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public ActionBar actionBar;

    RecyclerView recyclerView;
    List<PostModel> postovi;
    AdapterPost adapterPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        mAuth = FirebaseAuth.getInstance();

        actionBar = getSupportActionBar();
        actionBar.setTitle("Feed");

        recyclerView = findViewById(R.id.postsRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        initComponents();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) //ubacujemo dugme za logout u meni gore
    {
        getMenuInflater().inflate(R.menu.menu_navigation,menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.logoutButtonMenu:
                mAuth.signOut();
                startActivity(new Intent(FeedActivity.this,MainActivity.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(FeedActivity.this,ProfileActivity.class));
                break;
            case R.id.nav_feed:
                //Dodaj FeedActivity klasu
                Toast.makeText(FeedActivity.this,"You are currently on page",Toast.LENGTH_SHORT).show();
                break;
            case R.id.addPostButton:
                startActivity(new Intent(FeedActivity.this,AddPostActivity.class));
                break;



        }
//        if(id == R.id.logoutButtonMenu)
//        {
//            mAuth.signOut();
//            startActivity(new Intent(FeedActivity.this,MainActivity.class));
//        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {

        recyclerView = findViewById(R.id.postsRV);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        recyclerView.setLayoutManager(lm);


        postovi = new ArrayList<>();

        ucitajSvePostove();


    }

    private void ucitajSvePostove()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postovi.clear();
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String email = ds.child("email").getValue(String.class);
                    String description = ds.child("description").getValue(String.class);
                    String title = ds.child("title").getValue(String.class);
                    String username = ds.child("username").getValue(String.class);
                    String time = ds.child("time").getValue(String.class);

                    PostModel post = new PostModel(title,description,username,email,time);
                    postovi.add(post);

                }
                adapterPost = new AdapterPost(getApplicationContext(),postovi);
                recyclerView.setAdapter(adapterPost);
                //Toast.makeText(FeedActivity.this,"Tu si sad", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FeedActivity.this,"Greska", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void pretraziPostove(final String searchInput)
//    {
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                postovi.clear();
//                for(DataSnapshot ds : snapshot.getChildren())
//                {
//                    String email = ds.child("email").getValue(String.class);
//                    String description = ds.child("description").getValue(String.class);
//                    String title = ds.child("title").getValue(String.class);
//                    String username = ds.child("username").getValue(String.class);
//
//                    PostModel post = new PostModel(title,description,username,email);
//
//                    if(title.toLowerCase() == searchInput.toLowerCase())
//                    {
//                        postovi.add(post);
//                    }
//
//
//                }
//                adapterPost = new AdapterPost(getApplicationContext(),postovi);
//                recyclerView.setAdapter(adapterPost);
//                Toast.makeText(FeedActivity.this,"Tu si", Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(FeedActivity.this,"Greska", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}