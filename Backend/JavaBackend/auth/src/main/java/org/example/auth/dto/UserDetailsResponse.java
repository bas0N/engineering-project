package org.example.auth.dto;

import lombok.Getter;
import org.example.auth.entity.User;

import java.util.List;

@Getter
public class UserDetailsResponse {
    private String firstName;
    private String lastName;
    private String email;
    private List<AddressRequest> addresses;

    public UserDetailsResponse(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.addresses = null;
    }
}
