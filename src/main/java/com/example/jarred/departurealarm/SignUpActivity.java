package com.example.jarred.departurealarm;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

/**
 * Class to handle creating new user accounts
 *
 * @author Jarred
 * @version 10/31/2016
 */
public class SignUpActivity extends AppCompatActivity {

    private String packageName="com.example.jarred.departurealarm";

    /**
     * User input fields on the screen
     */
    private EditText email, password1, password2;

    /**
     * Classes for authentication
     */
    private FirebaseAuth authentication;
    private FirebaseAuth.AuthStateListener myListener;

    @Override
    /**
     * Builds the activity to display to the user
     *
     * @param savedInstanceState The information about this activity if it was cleared from the RAM to make space
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email=(EditText)findViewById(R.id.email_field);
        password1=(EditText)findViewById(R.id.password_field1);
        password2=(EditText)findViewById(R.id.password_field2);

        email.setText(getIntent().getStringExtra(packageName+".username"));

        Button signUp=(Button)findViewById(R.id.sign_up_button);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        authentication=FirebaseAuth.getInstance();
        myListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                onSuccessfulLogin();
            }
        };
    }

    @Override
    /**
     * Run when this activity is first made and when it goes from stopped to the primary focus
     */
    protected void onStart() {
        super.onStart();
        authentication.addAuthStateListener(myListener);
    }

    @Override
    /**
     * Run when this activity is frozen and hidden from the user.
     */
    protected void onStop() {
        super.onStop();
        authentication.removeAuthStateListener(myListener);
    }

    /**
     * Create the user account. It also tests to see if the passwords and usernames are valid
     */
    private void signUp() {
        String e = email.getText().toString();
        String pass = password1.getText().toString();
        if (!pass.equals(password2.getText().toString())) {
            password2.setError("The passwords are different");
            password2.requestFocus();
            return;
        }
        authentication.createUserWithEmailAndPassword(e, pass).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {onAccountCreationFailure(e);}
        });
    }

    private void onAccountCreationFailure(Exception e) {
        if(e instanceof FirebaseAuthInvalidCredentialsException || e instanceof FirebaseAuthUserCollisionException) {
            email.setError(e.getMessage());
            email.requestFocus();
        }
        if(e instanceof FirebaseAuthWeakPasswordException) {
            password1.setError(e.getMessage());
            password1.requestFocus();
        }
    }

    /**
     * Once the user has logged in, they can go and see their events
     */
    private void onSuccessfulLogin() {
        if(authentication.getCurrentUser()!=null) {
            DatabaseRetriever.doOnCreateUser();
            Intent intent = new Intent(this, EventViewActivity.class);
            startActivity(intent);
        }
    }
}
