package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    RecyclerView recyclerView;
    List<PostModel> postovi;
    AdapterPost adapterPost;

    private ActionBar actionBar;

    private TextView usernameTextView;
    private TextView emailTextView;
    //private ImageView userImageImageView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        recyclerView = findViewById(R.id.postsByUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        initComponents();
    }

    private void initComponents() {

        usernameTextView = findViewById(R.id.username);
        emailTextView = findViewById(R.id.email);

        recyclerView = findViewById(R.id.postsByUser);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        recyclerView.setLayoutManager(lm);


        postovi = new ArrayList<>();


        //userImageImageView = findViewById(R.id.userImage)
        //
        usernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUsernameDialog();
            }
        });

        //Upit da nadjemo informacije o useru preko emaila
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    String username = ""+ds.child("username").getValue();
                    String email = ""+ds.child("email").getValue();

                    usernameTextView.setText(username);
                    emailTextView.setText(email);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String uid = user.getUid();

        ucitajSvePostoveUsera();
    }

    private void ucitajSvePostoveUsera() {
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
                    String time =ds.child("time").getValue(String.class);

                    PostModel post = new PostModel(title,description,username,email,time);

                    if(Objects.equals(email, user.getEmail()))
                    {
                        postovi.add(post);
                    }


                }
                adapterPost = new AdapterPost(getApplicationContext(),postovi);
                recyclerView.setAdapter(adapterPost);
                //Toast.makeText(ProfileActivity.this,"Tu si sad", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this,"Greska", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) //ubacujemo dugme za logout u meni gore
    {
        getMenuInflater().inflate(R.menu.menu_navigation, menu);

        menu.findItem(R.id.addPostButton).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.logoutButtonMenu:
                mAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                break;
            case R.id.nav_profile:
                Toast.makeText(ProfileActivity.this, "You are currently on page", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_feed:
                //Dodaj FeedActivity klasu
                startActivity(new Intent(ProfileActivity.this, FeedActivity.class));
                break;
            case R.id.addPostButton:
                startActivity(new Intent(ProfileActivity.this,AddPostActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateUsernameDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update username");
        EditText editText = new EditText(this);

        builder.setView(editText);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String usernameValue = editText.getText().toString().trim();
                HashMap<String,Object> result = new HashMap<>();
                result.put("username",usernameValue);

                databaseReference.child(user.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ProfileActivity.this,"Success",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this,"Success",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();



    }
}