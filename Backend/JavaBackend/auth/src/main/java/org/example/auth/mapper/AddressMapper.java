package org.example.auth.mapper;

import org.example.auth.dto.request.AddressRequest;
import org.example.auth.dto.response.AddressAdminResponse;
import org.example.auth.dto.response.AddressResponse;
import org.example.auth.entity.Address;
import org.example.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "user", target = "user")
    @Mapping(target = "uuid", ignore = true)
    Address toAddress(AddressRequest addressRequest, User user);

    AddressRequest toAddressRequest(Address address);

    AddressResponse toAddressResponse(Address address);

    List<AddressResponse> toAddressResponseList(List<Address> addresses);

    AddressAdminResponse toAddressAdminResponse(Address address);

    List<AddressAdminResponse> toAddressAdminResponseList(List<Address> addresses);
}
