package com.novance.novance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccountActivity extends AppCompatActivity {

    //declare instance of FirebaseAuth
    private FirebaseAuth mAuth;

    //declare instance of database
    private DatabaseReference mDatabase;

    //creates global user variable
    User u;

    //Create Account Activity tag
    private static final String TAG = "CreateAccountActivity";

    protected void updateUI(FirebaseUser u) {
        if (u != null) {
            //TODO read in user info
            //creates intent for main activity and starts
            Intent i = new Intent(CreateAccountActivity.this, MainNavActivity.class);
            //passes user to next activity
            i.putExtra("user", this.u);
            startActivity(i);
            //TODO adjust based on loginactivity to save and load user data internally AND pass user on to next activity
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO toolbar instead of action bar?
        //TODO save username and full name of user
        //TODO allow user to edit profile image
        //TODO allow user option to apply as investor
        //TODO allow user to edit bio

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //gets email and password previously inputted (if previously inputted)
        Bundle b = getIntent().getExtras();
        String email = b.getString("email");
        String password = b.getString("password");

        //sets text fields to previously inputted info
        EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        emailEditText.setText(email);

        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordEditText.setText(password);

        //creates button and sets listener
        Button createAccountBtn = (Button) findViewById(R.id.createAccountBtn);
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creates all three create account text fields
                EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
                EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
                EditText confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
                //reads create account text fields in to create account method
                createAccount(emailEditText.getText().toString(), passwordEditText.getText().toString(), confirmPasswordEditText.getText().toString());
            }
        });
    }

    protected void createAccount(final String email, final String password, String password2) {
        //gets username and full name
        EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
        final String name = nameEditText.getText().toString();
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        final String username = usernameEditText.getText().toString();

        //error text view
        final TextView errorTextView = (TextView) findViewById(R.id.errorTextView);

        boolean missingInfo = email.equals("") || password.equals("") || password2.equals("");
        boolean shortPassword = password.length() < 6;
        boolean errorFound = missingInfo || shortPassword;
        //checks that all field have data
        if (errorFound) {
            if (missingInfo) {
                errorTextView.setText("*All fields are required");
                errorTextView.setVisibility(View.VISIBLE);
            } else if (shortPassword) {
                errorTextView.setText("*Password must be at least 6 characters");
                errorTextView.setVisibility(View.VISIBLE);
            }
        } else {
            //TODO make sure password fits requirements????
            //true if passwords match
            boolean passwordsMatch = (password.equals(password2));

            //format for valid email address
            String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
            boolean validEmail = email.matches(regex);

            //if email is valid and passwords match
            if (validEmail && passwordsMatch) {
                u = new User(email, password);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    //creates user object and saves data to database
                                    u.setFullName(name);
                                    u.setUsername(username);
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String id = user.getUid();
                                    u.setId(id);
                                    mDatabase = FirebaseDatabase.getInstance().getReference("users");
                                    mDatabase.child(u.getId()).setValue(u);
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    //displays error if account under that email already exists
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        errorTextView.setText("*Email already in use");
                                        errorTextView.setVisibility(View.VISIBLE);
                                    }
                                    updateUI(null);
                                }
                            }
                        });
            } else {
                //handles if email or password inputs were incorrect
                if (!validEmail && !passwordsMatch) {
                    errorTextView.setText("*Email is invalid & passwords do not match");
                    errorTextView.setVisibility(View.VISIBLE);
                } else if (!validEmail) {
                    errorTextView.setText("*Email is invalid");
                    errorTextView.setVisibility(View.VISIBLE);
                } else if (!passwordsMatch) {
                    errorTextView.setText("*Passwords do not match");
                    errorTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}

//TODO make buttons look better
//TODO create account button is difficult to hit (on log in page)
//TODO make return button from create account screen to return to log in
