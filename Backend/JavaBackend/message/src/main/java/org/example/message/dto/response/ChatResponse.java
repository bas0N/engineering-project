package org.example.message.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    String username;
    String lastMessage;
    String lastMessageTime;
    boolean isRead;
    Integer unreadCount;
}
