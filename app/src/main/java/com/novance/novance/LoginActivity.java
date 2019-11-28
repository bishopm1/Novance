package com.novance.novance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class LoginActivity extends AppCompatActivity {

    //declare instance of FirebaseAuth
    private FirebaseAuth mAuth;

    //declare instance of database
    private DatabaseReference mDatabase;

    private StorageReference storageReference;

    //boolean to see if user just signed in
    boolean justSignedIn = false;

    //creates global user variable
    User u;

    //Login Activity tag
    private static final String TAG = "LoginActivity";

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    protected void updateUI(FirebaseUser u) {
        if (u != null) {
            loadUser();
            //creates intent for main activity and starts
            Intent i = new Intent(LoginActivity.this, MainNavActivity.class);
            //passes user to next activity
            i.putExtra("user", this.u);
            startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //creates login button and assigns it a listener
        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creates and reads in email to string
                EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
                String email = emailEditText.getText().toString();

                //creates and reads in password to string
                EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
                String password = passwordEditText.getText().toString();
                signIn(email, password);
            }
        });

        //creates create account text view and assigns it a listener
        TextView createAccountTextView = (TextView) findViewById(R.id.createAccountTextView);
        createAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creates and reads in email to string
                EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
                String email = emailEditText.getText().toString();

                //creates and reads in password to string
                EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
                String password = passwordEditText.getText().toString();
                //creates intent for create account activity
                Intent i = new Intent(LoginActivity.this, CreateAccountActivity.class);
                //bundles information in text field to carry over to create account activity
                Bundle b = new Bundle();
                b.putString("email", email);
                b.putString("password", password);
                i.putExtras(b);
                startActivity(i);
            }
        });
    }

    protected void signIn(final String email, final String password) {
        if (email.equals("") || email == null || password.equals("") || password == null) {
            // If fields are empty, display incorrect error
            Log.w(TAG, "signInWithEmail:failure - email and/or password was empty");
            //creates incorrect email/password text view
            TextView incorrectTextView = (TextView) findViewById(R.id.incorrectTextView);
            incorrectTextView.setVisibility(View.VISIBLE);
            updateUI(null);
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                justSignedIn = true;
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                final String id = user.getUid();
                                //sets mDatabase to reference to users data
                                mDatabase = FirebaseDatabase.getInstance().getReference("users").child(id);
                                mDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        //reads in user info (other than email and password) into u
                                        //TODO read in profile picture and any other info
                                        User temp = dataSnapshot.getValue(User.class);
                                        u = new User(email, password);
                                        u.setId(id);
                                        u.setUsername(temp.getUsername());
                                        u.setFullName(temp.getFullName());
                                        u.setProfileImageUri(Uri.parse(dataSnapshot.child("imageURL").getValue().toString()));
                                        saveUser(u);
                                        Log.d(TAG, "User data read in");
                                        Intent i = new Intent(LoginActivity.this, MainNavActivity.class);
                                        //passes user to next activity
                                        i.putExtra("user", u);
                                        startActivity(i);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d(TAG + ": Failed to read in User data", databaseError.toString());
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                //creates incorrect email/password text view
                                TextView incorrectTextView = (TextView) findViewById(R.id.incorrectTextView);
                                incorrectTextView.setVisibility(View.VISIBLE);
                                updateUI(null);
                            }
                        }

                        // ...
                    });
        }

    }

    public void saveUser(User u) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(u);
        editor.putString("user", json);
        editor.apply();
        Log.d(TAG, "User data saved internally");
    }

    public void loadUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user", null);
        Type type = new TypeToken<User>() {
        }.getType();
        u = gson.fromJson(json, type);

        if (u == null) {
            u = new User();
            Log.d(TAG, "User data failed to load");
        } else {
            Log.d(TAG, "User data loaded from internal storage");
        }
    }

}
