package com.example.messenger.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.R;
import com.example.messenger.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, lastMessage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.fullName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }

    private final List<User> usersList;

    public UserAdapter(List<User> contactList) {
        this.usersList = contactList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_layout, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User contact = usersList.get(position);
        holder.fullName.setText(contact.getUser_name());
        holder.lastMessage.setText(contact.getUser_password());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}

