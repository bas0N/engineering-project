package org.example.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Table(name = "address_versions")
@Entity
public class AddressVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_versions_id_seq")
    @SequenceGenerator(name = "address_versions_id_seq", sequenceName = "address_versions_id_seq", allocationSize = 1)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    @Column(name = "version_timestamp")
    private LocalDateTime versionTimestamp;

    public AddressVersion(Address address) {
        this.user = address.getUser();
        this.street = address.getStreet();
        this.city = address.getCity();
        this.state = address.getState();
        this.postalCode = address.getPostalCode();
        this.country = address.getCountry();
        this.versionTimestamp = LocalDateTime.now();
    }

    public AddressVersion() {
    }
}
