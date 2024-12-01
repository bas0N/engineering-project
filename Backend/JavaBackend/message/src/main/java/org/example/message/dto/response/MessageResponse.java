package org.example.message.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String uuid;
    private String content;
    private String senderId;
    private String receiverId;
    private String dateAdded;
    private boolean isRead;
}
