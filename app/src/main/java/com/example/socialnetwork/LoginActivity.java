package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity  {

    private TextView signupPageLink;
    private EditText editTextEmailLogin;
    private EditText editTextPasswordLogin;
    private Button loginButtonLogin;

    private FirebaseAuth mAuth;

    private final static String SHARED_PREF_PREFIX = "LoginActivitySharedPrefPrefix"; //Pref je za preference
    private final static String SHARED_PREF_KEY_EMAIL = "ime";

    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        actionBar = getSupportActionBar();
        actionBar.hide();

        mAuth = FirebaseAuth.getInstance();

        initComponents();
    }
    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveData();
        //Ako izadjemo iz aplikacije, on ce da sacuva podatke ipak
    }

    private void initComponents() {
        signupPageLink = findViewById(R.id.dontHaveAccountSignup);
        editTextEmailLogin = findViewById(R.id.editTextEmailLogin);
        editTextPasswordLogin = findViewById(R.id.editTextPasswordLogin);
        loginButtonLogin = findViewById(R.id.loginButtonLogin);

        loginButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String unetEmail = editTextEmailLogin.getText().toString().trim();
                String unetPass = editTextPasswordLogin.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(unetEmail).matches())
                {
                    editTextEmailLogin.setError("Unesite ponovo Emial");
                    editTextEmailLogin.setFocusable(true);
                }
                else
                {
                    mAuth.signInWithEmailAndPassword(unetEmail, unetPass)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Uspesno
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        //String email = user.getEmail();
                                        //String userID = user.getUid();

                                        //HashMap<Object,String> hashMap = new HashMap<>();
                                        //hashMap.put("email",email);
                                        //hashMap.put("userID",userID);
                                        //hashMap.put("username","");
                                        //hashMap.put("image",""); // Ovo sad pravimo prazno pa posle dodajemo te informacije

                                        //FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        //DatabaseReference reference = database.getReference("Users");
                                        //reference.child(userID).setValue(hashMap);

                                        Toast.makeText(LoginActivity.this,"Uspesno je ulogovan korsinik sa emailom: "+user.getEmail(),Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, FeedActivity.class));
                                    } else {
                                        // Nije uspesno
                                        Toast.makeText(LoginActivity.this, "Neuspesno.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this,"Neuspesno",Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });


        signupPageLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }
    private void saveData()
    {
        String email = editTextEmailLogin.getText().toString().trim();
        String password = editTextEmailLogin.getText().toString().trim();

        SharedPreferences sp = getSharedPreferences(SHARED_PREF_PREFIX,0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SHARED_PREF_KEY_EMAIL,email);

        editor.commit();
    }
    private void loadData()
    {
        SharedPreferences sp = getSharedPreferences(SHARED_PREF_PREFIX,0);
        String email = sp.getString(SHARED_PREF_KEY_EMAIL,"");
        editTextEmailLogin.setText(email);

    }
}