package com.example.messenger.activities;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.R;
import com.example.messenger.adapters.MessageAdapter;
import com.example.messenger.models.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private RecyclerView chatViewer;
    private ImageButton sendButton;
    private TextView messageInput;
    private TextView friendsNameTextView;
    private List<Message> chatHistory;
    private MessageAdapter messageAdapter;
    private String currentUserName;
    private String friendsName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        currentUserName = getIntent().getStringExtra("currentUser");
        friendsName = getIntent().getStringExtra("friendsName");
        friendsNameTextView = findViewById(R.id.friends_name);
        friendsNameTextView.setText(friendsName);
        messageInput = findViewById(R.id.messageTextInput);
        messageInput.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                String messageText = messageInput.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    sendMessage(currentUserName, messageText);
                    messageInput.setText("");
                } else {
                    Toast.makeText(ChatActivity.this, "Enter a message", Toast.LENGTH_SHORT).show();
                }
                return true; // Consume the event
            }
            return false; // Pass the event to the next handler
        });
        sendButton = findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(currentUserName, messageText);
                messageInput.setText("");
            } else {
                Toast.makeText(ChatActivity.this, "Enter a message", Toast.LENGTH_SHORT).show();
            }
        });
        chatHistory = new ArrayList<>();
        chatViewer = findViewById(R.id.chatRecycleViewer);
        chatViewer.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatViewer.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(chatHistory, currentUserName);
        chatViewer.setAdapter(messageAdapter);
        chatViewer.scrollToPosition(chatHistory.size()-1);
        database = FirebaseDatabase.getInstance("https://messenger-13b4d-default-rtdb.europe-west1.firebasedatabase.app/");
        fetchChatHistory();
    }
    private void fetchChatHistory() {
        DatabaseReference dbRef = database.getReference("allChats").child(currentUserName).child(friendsName);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatHistory.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message sms = snapshot.getValue(Message.class);
                    if (sms != null) {
                        chatHistory.add(sms);
                    }
                }
                messageAdapter.notifyDataSetChanged();
                chatViewer.scrollToPosition(chatHistory.size()-1);
                chatViewer.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (!chatViewer.canScrollVertically(1)) {
                            markMessagesAsRead();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void markMessagesAsRead(){
        DatabaseReference dbRef = database.getReference("allChats").child(currentUserName).child(friendsName);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().child("isSeen").setValue(true);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void sendMessage(String author, String messageText) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        Message newMessage = new Message(author, messageText, timeStamp, false);
        database.getReference("allChats").child(currentUserName).child(friendsName).child(timeStamp).setValue(newMessage).addOnSuccessListener(aVoid -> {
            database.getReference("allChats").child(friendsName).child(currentUserName).child(timeStamp).setValue(newMessage);
        }).addOnFailureListener(e ->
                Toast.makeText(ChatActivity.this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_LONG).show()
        );
    }

}