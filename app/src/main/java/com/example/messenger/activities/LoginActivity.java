package com.example.messenger.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.messenger.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private DatabaseReference database;
    private EditText user_name;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user_name = findViewById(R.id.editEmailAddress);
        password = findViewById(R.id.editPassword);
        database = FirebaseDatabase.getInstance("https://messenger-13b4d-default-rtdb.europe-west1.firebasedatabase.app/").getReference("allUsers");
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        if(currentUser != null){
//            Intent friends = new Intent(LoginActivity.this, UsersActivity.class);
//            startActivity(friends);
//            finish();
//        }
//    }

    public void getRegistrationForm(View view) {
        Intent registration = new Intent(this, RegistrationActivity.class);
        startActivity(registration);
        finish();
    }

    public void logIn(View view) {
        if (credentialsVerification()) {
            database.child(user_name.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String storedPassword = dataSnapshot.getValue(String.class);
                        if (storedPassword != null && storedPassword.equals(password.getText().toString())) {
                            Toast.makeText(LoginActivity.this, "Welcome " + user_name.getText(), Toast.LENGTH_LONG).show();
                            Intent friends = new Intent(LoginActivity.this, UsersActivity.class);
                            friends.putExtra("loggedInUser", user_name.getText().toString());
                            startActivity(friends);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Incorrect Password. Try again!", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(LoginActivity.this, "Incorrect Login. Try again!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(LoginActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean credentialsVerification() {
        if (user_name != null && !user_name.getText().toString().isEmpty()) {
            if (password != null && !password.getText().toString().isEmpty()) {
                return true;
            } else {
                Toast.makeText(LoginActivity.this, "Password cannot be empty", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "Username cannot be empty", Toast.LENGTH_LONG).show();
        }
        return false;
    }
}