package org.example.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.auth.token.Token;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users" , uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String userId;

    @Column(name = "email" , nullable=false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getGrantedAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
