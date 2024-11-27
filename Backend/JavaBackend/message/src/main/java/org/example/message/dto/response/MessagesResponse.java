package org.example.message.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessagesResponse {
    private List<MessageResponse> messages;
    private UserDetailsResponse sender;
    private UserDetailsResponse receiver;
}
