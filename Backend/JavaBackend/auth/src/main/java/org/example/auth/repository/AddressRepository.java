package org.example.auth.repository;

import org.example.auth.entity.Address;
import org.example.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a WHERE a.uuid = :uuid")
    Optional<Address> findByUuid(String uuid);

    @Transactional
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

    @Query("SELECT COUNT(a) > 0 FROM Address a WHERE a.uuid = :uuid")
    boolean existsByUuid(String uuid);

    @Query("SELECT a FROM Address a WHERE a.user = :user")
    List<Address> findAllByUser(User user);

    @Query("SELECT a FROM Address a WHERE a.user.id = :id")
    List<Address> findAllByUserId(Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM Address a WHERE a.user.id = :id")
    void deleteAddressesByUserId(Long id);
}
