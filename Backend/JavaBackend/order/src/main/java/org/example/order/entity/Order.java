package org.example.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.order.enums.Status;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(generator = "orders_id_seq",strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "orders_id_seq",sequenceName = "orders_id_seq", allocationSize = 1)
    private long id;
    private String uuid;
    private String orders;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String basketId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<OrderItems> orderItems;

    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    private String phone;
    private String email;
    private String client;

    private String city;
    private String street;
    private String state;
    private String country;
    @Column(name = "postcode")
    private String postCode;


    @ManyToOne
    @JoinColumn(name = "deliver")
    private Deliver deliver;
}
