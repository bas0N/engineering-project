package org.example.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChangePasswordData {
    private String password;
    private String uid;
}
