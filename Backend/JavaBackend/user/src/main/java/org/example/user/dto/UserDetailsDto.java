package org.example.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDetailsDto {
    private String id;
    private String login;
    private String email;
}
