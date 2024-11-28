package com.example.messenger.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class Contact {
    private String avatarId;
    private String name;
    private String lastMessage;
    private String lastSmsTime;
    private int unreadMessagesCount;
}
