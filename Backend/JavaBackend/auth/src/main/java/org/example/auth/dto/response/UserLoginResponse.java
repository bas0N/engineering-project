package org.example.auth.dto.response;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.auth.entity.Role;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
public class UserLoginResponse {

    private String login;

    private String email;

    private Role role;
}
