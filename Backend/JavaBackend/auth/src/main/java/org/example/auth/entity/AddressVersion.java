package org.example.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    @JoinColumn(name = "user_version_id", nullable = false)
    private UserVersion userVersion;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String country;

    @Column(name = "version_timestamp", nullable = false)
    private String versionTimestamp;

    public AddressVersion() {
    }

    // Konstruktor do tworzenia nowej wersji adresu
    public AddressVersion(Address address, UserVersion userVersion) {
        this.userVersion = userVersion;
        this.street = address.getStreet();
        this.city = address.getCity();
        this.state = address.getState();
        this.postalCode = address.getPostalCode();
        this.country = address.getCountry();
        this.versionTimestamp = String.valueOf(System.currentTimeMillis());
    }
}
