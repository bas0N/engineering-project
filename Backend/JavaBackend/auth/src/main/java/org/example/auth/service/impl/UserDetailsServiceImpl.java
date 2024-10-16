package org.example.auth.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.AddressRequest;
import org.example.auth.dto.UserDetailsRequest;
import org.example.auth.dto.UserDetailsResponse;
import org.example.auth.entity.Address;
import org.example.auth.entity.AddressVersion;
import org.example.auth.entity.User;
import org.example.auth.entity.UserVersion;
import org.example.auth.repository.UserRepository;
import org.example.auth.repository.UserVersionRepository;
import org.example.auth.service.JwtService;
import org.example.auth.service.UserDetailsService;
import org.example.exception.exceptions.ApiRequestException;
import org.example.exception.exceptions.ResourceNotFoundException;
import org.example.exception.exceptions.UnauthorizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserVersionRepository userVersionRepository;
    private final JwtService jwtService;


    @Override
    public ResponseEntity<?> fillUserDetails(UserDetailsRequest userDetailsRequest, HttpServletRequest request) {
        try {
            String token = jwtService.getTokenFromRequest(request);
            String userId = jwtService.getUuidFromToken(token);

            User user = userRepository.findByUuid(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userId));

            user.setFirstName(userDetailsRequest.getFirstName());
            user.setLastName(userDetailsRequest.getLastName());

            List<Address> newAddresses = getAddressList(userDetailsRequest, user);
            user.setAddresses(newAddresses);

            userRepository.save(user);

            UserVersion userVersion = getUserVersion(userDetailsRequest, user, newAddresses);
            userVersionRepository.save(userVersion);

            return ResponseEntity.ok("User details updated successfully");
        } catch (ResourceNotFoundException e) {
            log.error("Error in filling user details: User not found", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating user details", e);
            throw new ApiRequestException("An error occurred while updating user details", e, "UPDATE_USER_DETAILS_ERROR");
        }
    }

    private static List<Address> getAddressList(UserDetailsRequest userDetailsRequest, User user) {
        List<Address> newAddresses = new ArrayList<>();
        for (AddressRequest addressRequest : userDetailsRequest.getAddresses()) {
            Address address = new Address(
                    user,
                    addressRequest.getStreet(),
                    addressRequest.getCity(),
                    addressRequest.getState(),
                    addressRequest.getPostalCode(),
                    addressRequest.getCountry()
            );
            newAddresses.add(address);
        }
        newAddresses.forEach(address -> address.setUser(user)); // Powiąż adresy z użytkownikiem
        return newAddresses;
    }

    @Override
    public Object getUserDetails(HttpServletRequest request) {
        try {
            String token = jwtService.getTokenFromRequest(request);
            String userId = jwtService.getUuidFromToken(token);

            User user = userRepository.findByUuid(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userId));
            return new UserDetailsResponse(user);
        } catch (UnauthorizedException e) {
            log.error("Unauthorized access while getting user details", e);
            throw e;
        } catch (ResourceNotFoundException e) {
            log.error("User not found", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving user details", e);
            throw new ApiRequestException("An error occurred while retrieving user details", e, "GET_USER_DETAILS_ERROR");
        }
    }

    private static UserVersion getUserVersion(UserDetailsRequest userDetailsRequest, User user, List<Address> newAddresses) {
        UserVersion userVersion = new UserVersion(user);
        userVersion.setFirstName(userDetailsRequest.getFirstName());
        userVersion.setLastName(userDetailsRequest.getLastName());

        List<AddressVersion> addressVersions = new ArrayList<>();
        for (Address address : newAddresses) {
            AddressVersion addressVersion = new AddressVersion(address, userVersion);
            addressVersions.add(addressVersion);
        }
        userVersion.setAddressVersions(addressVersions);
        return userVersion;
    }
}
