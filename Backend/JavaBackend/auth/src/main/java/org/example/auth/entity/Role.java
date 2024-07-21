package org.example.auth.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Permission;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Role {
    USER(Collections.emptySet()),
    ADMIN(
            Set.of(
            Permission.ADMIN_READ,
            Permission.ADMIN_CREATE,
            Permission.ADMIN_UPDATE,
            Permission.ADMIN_DELETE
            )
    );

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getGrantedAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
