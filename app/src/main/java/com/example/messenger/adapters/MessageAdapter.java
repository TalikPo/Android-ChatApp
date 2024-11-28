package com.example.messenger.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.messenger.R;
import com.example.messenger.models.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ChatViewHolder> {
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView friendsSmsText, friendsSmsTime, mySmsText, mySmsTime;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            friendsSmsText = itemView.findViewById(R.id.friendsMessageText);
            friendsSmsTime = itemView.findViewById(R.id.friendsMessageTime);
            mySmsText = itemView.findViewById(R.id.myMessageText);
            mySmsTime = itemView.findViewById(R.id.myMessageTime);
        }
    }

    private final List<Message> chatList;
    private final String currentUsername;

    public MessageAdapter(List<Message> chats, String currentUserName) {
        this.chatList = chats;
        this.currentUsername = currentUserName;
    }

    @NonNull
    @Override
    public MessageAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout, parent, false);
        return new MessageAdapter.ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ChatViewHolder holder, int position) {
        Message chat = chatList.get(position);
        String formattedTime = formatTimestamp(chat.getTime());
        holder.mySmsText.setVisibility(View.GONE);
        holder.mySmsTime.setVisibility(View.GONE);
        holder.friendsSmsText.setVisibility(View.GONE);
        holder.friendsSmsTime.setVisibility(View.GONE);
        if (chat.getAuthor().equals(currentUsername)){
            holder.mySmsText.setText(chat.getText());
            holder.mySmsTime.setText(formattedTime);
            holder.mySmsText.setVisibility(View.VISIBLE);
            holder.mySmsTime.setVisibility(View.VISIBLE);
        }else {
            holder.friendsSmsText.setText(chat.getText());
            holder.friendsSmsTime.setText(formattedTime);
            holder.friendsSmsText.setVisibility(View.VISIBLE);
            holder.friendsSmsTime.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    private String formatTimestamp(String timestamp) {
        try {
            long timeInMillis = Long.parseLong(timestamp);
            Date date = new Date(timeInMillis);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()); // Format for hours and minutes
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(date);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "";
        }
    }
}
