package com.example.messenger.activities;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.messenger.R;
import com.example.messenger.adapters.ContactAdapter;
import com.example.messenger.models.Contact;
import com.example.messenger.models.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private List<Contact> contactsList;
    private AutoCompleteTextView userSearch;
    private RecyclerView recyclerView;
    private ContactAdapter contactsAdapter;
    private String currentUser;
    private List<String> allUserNames;
    private ArrayAdapter<String> adaptUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        currentUser = intent.getStringExtra("loggedInUser");

        database = FirebaseDatabase.getInstance("https://messenger-13b4d-default-rtdb.europe-west1.firebasedatabase.app/");
        contactsList = new ArrayList<>();
        allUserNames = new ArrayList<>();

        userSearch = findViewById(R.id.autoCompleteTextView);
        recyclerView = findViewById(R.id.contacts_recycle_viewer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactsAdapter = new ContactAdapter(contactsList, itemClicked -> {
            Intent intention = new Intent(this, ChatActivity.class);
            intention.putExtra("currentUser", currentUser);
            intention.putExtra("friendsName", itemClicked);
            startActivity(intention);
        });
        recyclerView.setAdapter(contactsAdapter);

        fetchChats();
        fetchUsers();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatapp_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.about_page) {
            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setMessage("Version: 1.0\nCreator: Vitalik Polianskiy")
                    .setPositiveButton("OK", null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchUsers() {
        database.getReference("allUsers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    String username = snapshot.getKey();
                    allUserNames.add(username);
                }
                adaptUsers = new ArrayAdapter<>(UsersActivity.this, android.R.layout.simple_dropdown_item_1line, allUserNames);
                userSearch.setAdapter(adaptUsers);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UsersActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        userSearch.setOnItemClickListener((adapterView, view, position, id) -> {
            String selectedUserName = adaptUsers.getItem(position);

            Intent intention = new Intent(UsersActivity.this, ChatActivity.class);
            intention.putExtra("currentUser", currentUser);
            intention.putExtra("friendsName", selectedUserName);
            startActivity(intention);
        });
    }

    private void fetchChats() {
        database.getReference("allChats").child(currentUser).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactsList.clear();
                for (DataSnapshot userChatSnapshot : dataSnapshot.getChildren()) {
                    String username = userChatSnapshot.getKey(); // Friend's username
                    Message msg = new Message();
                    if (username != null) {
                        msg.setAuthor(username);
                        DataSnapshot lastMessageSnapshot = null;
                        int unreadMessagesAmount = 0;
                        for (DataSnapshot messageSnapshot : userChatSnapshot.getChildren()) {
                            if(Boolean.FALSE.equals(messageSnapshot.child("isSeen").getValue(Boolean.class))){
                                unreadMessagesAmount++;
                            }
                            lastMessageSnapshot = messageSnapshot; // Gets the last child
                        }

                        if (lastMessageSnapshot != null) {
                            String lastSmsTime = lastMessageSnapshot.getKey();
                            String lastSms = lastMessageSnapshot.child("text").getValue(String.class);
                            String author = lastMessageSnapshot.child("author").getValue(String.class);
                            msg.setText(lastSms);
                            msg.setTime(lastSmsTime);
                            if (lastSmsTime == null) lastSmsTime = "";
                            if (lastSms == null) lastSms = "";
                            if(!currentUser.equals(author) && unreadMessagesAmount !=0){
                                showNotification(msg);
                            }
                            Contact contact = new Contact("", username, lastSms, lastSmsTime, unreadMessagesAmount);
                            contactsList.add(contact);
                        }
                    }
                }

                contactsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UsersActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("NotificationPermission")
    private void showNotification(Message msg){
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "channel_id_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My_Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            nm.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(msg.getAuthor())
                .setContentText(msg.getText());

        Intent notificationIntent = new Intent(this, UsersActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        nm.notify(4, notification);
    }
}