package org.example.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.auth.dto.request.AddressRequest;
import org.example.auth.entity.User;
import org.example.auth.mapper.AddressMapper;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private List<AddressResponse> addresses;
    private String imageUrl;

//    public UserDetailsResponse(User user) {
//        this.id = user.getUuid();
//        this.firstName = user.getFirstName();
//        this.lastName = user.getLastName();
//        this.email = user.getEmail();
//        this.phoneNumber = user.getPhoneNumber();
//        this.imageUrl = user.getImageUrl();
//        this.addresses = addressMapper.toAddressResponseList(user.getAddresses());
//    }
}
