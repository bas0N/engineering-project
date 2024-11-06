package org.example.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String content;
    private UserResponse receiver;
    private UserResponse senderId;
    private Date dateAdded;
    private String uuid;
}
