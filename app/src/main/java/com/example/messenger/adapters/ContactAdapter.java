package com.example.messenger.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.messenger.R;
import com.example.messenger.models.Contact;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, lastMessage, lastMessageTime, unreadMessages;
        ImageView avatarPicture;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.fullName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            lastMessageTime = itemView.findViewById(R.id.lastMessageTime);
            unreadMessages = itemView.findViewById(R.id.unreadMessagesAmount);
            avatarPicture = itemView.findViewById(R.id.avatarPic);
        }
    }

    private final List<Contact> contactsList;
    private OnItemClickListener onContactClickListener;
    public interface OnItemClickListener {
        void onItemClick(String contactName);
    }

    public ContactAdapter(List<Contact> contactList, OnItemClickListener listener) {
        this.contactsList = contactList;
        this.onContactClickListener = listener;
    }

    @NonNull
    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_layout, parent, false);
        return new ContactAdapter.ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ContactViewHolder holder, int position) {
        Contact contact = contactsList.get(position);
        holder.fullName.setText(contact.getName());
        holder.lastMessage.setText(contact.getLastMessage());
        holder.lastMessageTime.setText(contact.getLastSmsTime());
        holder.unreadMessages.setText(String.valueOf(contact.getUnreadMessagesCount()));
        // holder.avatarPicture.setImageResource(contact.getAvatarId());

        holder.itemView.setOnClickListener(v -> {
            if (onContactClickListener != null) {
                onContactClickListener.onItemClick(contact.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }
}
