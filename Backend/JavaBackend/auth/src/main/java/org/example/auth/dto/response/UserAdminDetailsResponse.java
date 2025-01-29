package org.example.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.auth.entity.Role;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminDetailsResponse {
    private long id;
    private String uuid;
    private String email;
    private List<AddressAdminResponse> addresses;
    private String imageUrl;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String password;
    private Role role;
    private boolean lock;
    private boolean enabled;
    private boolean isActive;

}
