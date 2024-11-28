package com.example.messenger.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.messenger.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegistrationActivity extends AppCompatActivity {
    private DatabaseReference database;
    private EditText user_name;
    private EditText password;
    private EditText repeatedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        database = FirebaseDatabase.getInstance("https://messenger-13b4d-default-rtdb.europe-west1.firebasedatabase.app/").getReference("allUsers");
        user_name = findViewById(R.id.editEmailAddress);
        password = findViewById(R.id.editPassword);
        repeatedPassword = findViewById(R.id.editPasswordRepeat);
    }

    public void signUpClicked(View v) {
        if (credentialsVerification()) {
            database.child(user_name.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(RegistrationActivity.this, "The user with this name already exists.", Toast.LENGTH_LONG).show();
                    } else {
                        database.child(user_name.getText().toString()).setValue(password.getText().toString())
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegistrationActivity.this, "Registration Successful.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RegistrationActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(RegistrationActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean credentialsVerification() {
        if (user_name != null && !user_name.getText().toString().isEmpty()) {
            if (password != null && !password.getText().toString().isEmpty()) {
                if (password.getText().toString().equals(repeatedPassword.getText().toString())) {
                    return true;
                } else {
                    Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(RegistrationActivity.this, "Password cannot be empty", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(RegistrationActivity.this, "Username cannot be empty", Toast.LENGTH_LONG).show();
        }
        return false;
    }

}