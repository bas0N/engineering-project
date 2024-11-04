package org.example.auth.repository;

import org.example.auth.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    @Modifying
    @Query("DELETE FROM Address a WHERE a.uuid IN :uuids")
    void deleteAddressesByUuids(@Param("uuids") List<String> uuids);

    @Query("SELECT a FROM Address a WHERE a.uuid = :uuid")
    Optional<Address> findByUuid(String uuid);

    @Modifying
    @Query("UPDATE Address a SET " +
            "a.street = :street, " +
            "a.city = :city, " +
            "a.state = :state, " +
            "a.postalCode = :postalCode, " +
            "a.country = :country WHERE a.uuid = :uuid")
    void updateAddress(
            @Param("uuid") String uuid,
            @Param("street") String street,
            @Param("city") String city,
            @Param("state") String state,
            @Param("postalCode") String postalCode,
            @Param("country") String country
    );

}
