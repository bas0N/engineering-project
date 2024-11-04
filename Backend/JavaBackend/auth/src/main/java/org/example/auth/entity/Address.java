package org.example.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Table(name = "addresses")
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_id_seq")
    @SequenceGenerator(name = "address_id_seq", sequenceName = "address_id_seq", allocationSize = 1)
    private long id;

    @Getter
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    public Address() {
        generateUuid();
    }

    public Address(User user, String street, String city, String state, String postalCode, String country) {
        this.user = user;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        generateUuid();
    }

    private void generateUuid() {
        if (uuid == null || uuid.isEmpty()) {
            setUuid(UUID.randomUUID().toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return uuid != null && uuid.equals(address.uuid);
    }
}
