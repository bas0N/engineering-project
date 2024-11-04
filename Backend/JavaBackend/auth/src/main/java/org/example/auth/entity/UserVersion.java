package org.example.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.auth.mapper.AddressMapper;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Table(name = "user_versions")
@Entity
public class UserVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_versions_id_seq")
    @SequenceGenerator(name = "user_versions_id_seq", sequenceName = "user_versions_id_seq", allocationSize = 1)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String email;
    private String password;
    private String firstName;
    private String lastName;

    @OneToMany(mappedBy = "userVersion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AddressVersion> AddressVersions;

    @Getter
    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isLock;
    private boolean isEnabled;

    @Column(name = "version_timestamp")
    private LocalDateTime versionTimestamp;

    public UserVersion(User user) {
        this.user = user;
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.AddressVersions = user.getAddresses().stream()
                .map(AddressMapper.INSTANCE::toAddressVersion)
                .toList();
        this.role = user.getRole();
        this.isLock = user.getIsLock();
        this.isEnabled = user.isEnabled();
        this.versionTimestamp = LocalDateTime.now();
    }

    public UserVersion() {
    }
}
