package org.example.auth.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.auth.entity.Operation;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    private String uuid;

    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "\\d{5}", message = "Postal code must be a 5-digit number")
    private String postalCode;

    @NotBlank(message = "Country is required")
    private String country;

    @NotNull(message = "Operation is required and must be one of: UPDATE, DELETE, CREATE")
    private Operation operation;
}
