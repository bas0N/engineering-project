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
            new Endpoint("/auth/authorize", HttpMethod.GET, Role.GUEST)
    )
    );
    private final Set<Endpoint> adminEndpoints = new HashSet<>(List.of(
            new Endpoint("/api/v1/auth/admin/**", HttpMethod.DELETE, Role.ADMIN),
            new Endpoint("/api/v1/auth/admin/**", HttpMethod.PATCH, Role.ADMIN),
            new Endpoint("/api/v1/auth/admin/**", HttpMethod.GET, Role.ADMIN),
            new Endpoint("/api/v1/product/admin/**", HttpMethod.DELETE, Role.ADMIN)
    ));

    public void addEndpoints(List<Endpoint> endpointList) {
        for (Endpoint endpoint : endpointList) {
            if (endpoint.getRole().name().equals(Role.ADMIN.name())) {
                adminEndpoints.add(endpoint);
            }
            if (endpoint.getRole().name().equals(Role.GUEST.name())) {
                openApiEndpoints.add(endpoint);
            }
        }
    }

    public Predicate<org.springframework.http.server.reactive.ServerHttpRequest> isAdmin =
            request -> adminEndpoints
                    .stream()
                    .anyMatch(value -> pathMatcher.match(value.getUrl(), request.getURI().getPath())
                            && request.getMethod().name().equals(value.getHttpMethod().name()));

    public Predicate<ServerHttpRequest> isSecure =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(value -> request.getURI()
                            .getPath()
                            .contains(value.getUrl())
                            && request.getMethod().name().equals(value.getHttpMethod().name()));


}
