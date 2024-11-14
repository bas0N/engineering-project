package org.example.auth.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {
    private String uuid;

    private String street;

    private String city;

    private String state;

    private String postalCode;

    private String country;
}
