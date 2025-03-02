package org.example.commondto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailInfoEvent {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private boolean isActive;
}
