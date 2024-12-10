package org.example.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressAdminResponse {
    private long id;

    private String uuid;

    private String street;

    private String city;

    private String state;

    private String postalCode;

    private String country;
}
