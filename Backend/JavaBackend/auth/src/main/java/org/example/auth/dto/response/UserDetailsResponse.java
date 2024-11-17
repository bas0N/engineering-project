package org.example.auth.dto.response;

import lombok.Getter;
import org.example.auth.dto.request.AddressRequest;
import org.example.auth.entity.User;
import org.example.auth.mapper.AddressMapper;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserDetailsResponse {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;
    private final List<AddressResponse> addresses;
    private static final AddressMapper addressMapper = AddressMapper.INSTANCE;

    public UserDetailsResponse(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.addresses = user.getAddresses().stream()
                .map(addressMapper::toAddressResponse)
                .collect(Collectors.toList());
    }
}
