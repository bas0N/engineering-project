package org.example.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRegisterDto {
    private String login;
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;
    private String role;
}
