package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email;
    private EditText password;
    private TextView haveAccountLogin;
    private Button signupButtonSignup;
    public FirebaseAuth mAuth;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        actionBar = getSupportActionBar();
        actionBar.hide();

        mAuth = FirebaseAuth.getInstance();

        initComponents();

    }

    private void initComponents()
    {
        email = findViewById(R.id.editTextEmailSignup);
        password = findViewById(R.id.editTextPasswordSignup);
        signupButtonSignup = findViewById(R.id.signupButtonSignup);

        haveAccountLogin = findViewById(R.id.haveAccountLogin);
        haveAccountLogin.setOnClickListener(this);
        signupButtonSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.haveAccountLogin:
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                break;
            case R.id.signupButtonSignup:
                signup();
                break;
        }
    }

    public void signup() {
        String unetEmail = email.getText().toString().trim(); //trim izbacuje praznine sa pocetka i kraja
        String unetPass = password.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(unetEmail).matches()) // regex za mail
        {
            email.setError("Ne valja email");
            email.setFocusable(true);
        }
        else if(unetPass.length() < 6)
        {
            password.setError("Sifra mora biti duza od 6 karaktera");
            password.setFocusable(true);
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(unetEmail, unetPass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();

                                String email = user.getEmail();
                                String userID = user.getUid();

                                HashMap<Object,String> hashMap = new HashMap<>();
                                hashMap.put("email",email);
                                hashMap.put("userID",userID);
                                hashMap.put("username","");
                                //hashMap.put("image",""); // Ovo sad pravimo prazno pa posle dodajemo te informacije

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(userID).setValue(hashMap);


                                Toast.makeText(SignupActivity.this, "Registrovao se "+user.getEmail(),Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, FeedActivity.class));
                                finish(); // Activity.finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignupActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignupActivity.this,"Greska",Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }
}