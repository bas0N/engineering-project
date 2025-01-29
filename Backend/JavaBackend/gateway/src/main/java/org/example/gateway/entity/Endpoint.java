package org.example.gateway.entity;

import lombok.Getter;
import org.coffeecode.entity.HttpMethod;
import org.coffeecode.entity.Role;

import java.util.Objects;

@Getter
public class Endpoint {
    private final String url;
    private final HttpMethod httpMethod;
    private final Role role;

    public Endpoint(String url, HttpMethod httpMethod, Role role) {
        this.url = url;
        this.httpMethod = httpMethod;
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !this.getClass().equals(obj.getClass())) {
            return false;
        }
        Endpoint otherEndpoint = (Endpoint) obj;
        return Objects.equals(this.url, otherEndpoint.url) && this.httpMethod == otherEndpoint.httpMethod && Objects.equals(this.role, otherEndpoint.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, httpMethod, role);
    }
}
