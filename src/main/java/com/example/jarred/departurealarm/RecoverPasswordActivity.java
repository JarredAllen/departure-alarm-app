package com.example.jarred.departurealarm;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecoverPasswordActivity extends AppCompatActivity {

    private EditText email;

    @Override
    /**
     * Run when this activity is first created.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        email=(EditText)findViewById(R.id.email_field);

        Button recover=(Button)findViewById(R.id.recover_password_button);
        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverPassword();
            }
        });
    }

    /**
     * Run when the user clicks the button to recover their password
     *
     * Sends the e-mail to recover the password, unless it is an invalid e-mail address, in which case it notifies the user.
     */
    private void recoverPassword() {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    TextView barf=(TextView)findViewById(R.id.bottom_text);
                    barf.setText("An e-mail has been sent to your e-mail address to reset your password.");
                }
                else {
                    email.setError("This e-mail address is not associated with any account.");
                    email.requestFocus();
                }
            }
        });
    }
}
