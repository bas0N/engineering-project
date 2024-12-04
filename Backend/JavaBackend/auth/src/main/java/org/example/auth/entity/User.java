package org.example.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Setter
@Table(name = "users")
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(generator = "users_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
    @Getter
    private long id;

    @Getter
    private String uuid;

    @Getter
    private String email;

    @Getter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Address> addresses;

    @Getter
    private String imageUrl;

    @Getter
    private String phoneNumber;

    @Getter
    private String firstName;

    @Getter
    private String lastName;

    private String password;

    @Getter
    @Enumerated(EnumType.STRING)
    private Role role;

    @Setter
    @Column(name = "islock")
    private boolean lock;

    @Column(name = "isenabled")
    private boolean enabled;

    @Column(name = "isActive")
    @Getter
    @Setter
    private boolean isActive;

    public User(long id, String uuid, String email, String password, Role role, boolean isLock, boolean isEnabled, boolean isActive) {
        this.id = id;
        this.uuid = uuid;
        this.email = email;
        this.password = password;
        this.role = role;
        this.lock = isLock;
        this.enabled = isEnabled;
        this.isActive = isActive;
        generateUuid();
    }

    public User() {
        generateUuid();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return uuid;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !lock;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    private void generateUuid() {
        if (uuid == null || uuid.isEmpty()) {
            setUuid(UUID.randomUUID().toString());
        }
    }

    public boolean getIsLock() {
        return lock;
    }
}
