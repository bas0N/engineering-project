package org.example.gateway.filter;

import org.example.gateway.entity.Endpoint;
import org.coffeecode.entity.HttpMethod;
import org.coffeecode.entity.Role;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    public Set<Endpoint> openApiEndpoints = new HashSet<>(List.of(
            new Endpoint("/auth/register", HttpMethod.POST, Role.GUEST),
            new Endpoint("/auth/login", HttpMethod.POST, Role.GUEST),
            new Endpoint("/auth/validate", HttpMethod.GET, Role.GUEST),
            new Endpoint("/auth/authorize", HttpMethod.GET, Role.GUEST),
            new Endpoint("/auth/verify", HttpMethod.GET, Role.GUEST)
    )
    );
    private final Set<Endpoint> adminEndpoints = new HashSet<>(List.of(
            new Endpoint("/api/v1/auth/admin/**", HttpMethod.DELETE, Role.ADMIN),
            new Endpoint("/api/v1/auth/admin/**", HttpMethod.PATCH, Role.ADMIN),
            new Endpoint("/api/v1/auth/admin/**", HttpMethod.GET, Role.ADMIN),
            new Endpoint("/api/v1/product/admin/**", HttpMethod.DELETE, Role.ADMIN)
    ));

    public void addEndpoints(List<Endpoint> endpoints) {
        for (Endpoint ep : endpoints) {
            String roleName = ep.getRole().name();
            if (Role.ADMIN.name().equals(roleName)) {
                adminEndpoints.add(ep);
            } else if (Role.GUEST.name().equals(roleName)) {
                openApiEndpoints.add(ep);
            }
        }
    }

    public Predicate<ServerHttpRequest> isAdmin =
            req -> adminEndpoints
                    .stream()
                    .anyMatch(endpoint -> pathMatcher.match(endpoint.getUrl(), req.getURI().getPath())
                            && req.getMethod().name().equals(endpoint.getHttpMethod().name()));

    public Predicate<ServerHttpRequest> isSecure =
            req -> openApiEndpoints
                    .stream()
                    .noneMatch(endpoint -> req.getURI()
                            .getPath()
                            .contains(endpoint.getUrl())
                            && req.getMethod().name().equals(endpoint.getHttpMethod().name()));




}
