package org.example.auth.configuration;

import jakarta.annotation.PostConstruct;
import org.coffeecode.ApiGatewayEndpointConfiguration;
import org.coffeecode.entity.Endpoint;
import org.coffeecode.entity.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.coffeecode.entity.Role;
import org.coffeecode.entity.Response;


public class ApiGatewayEndpointConfigurationImpl implements ApiGatewayEndpointConfiguration {
    @Value("${api-gateway.url}")
    private String GATEWAY_URL;

    @PostConstruct
    public void startOperation() {
        initMap();
        register();
    }

    @Override
    public void initMap() {
        endpointList.add(new Endpoint("/api/v1/auth/admin/**", HttpMethod.DELETE, Role.ADMIN));
        endpointList.add(new Endpoint("/api/v1/auth/admin/**", HttpMethod.PATCH, Role.ADMIN));
        endpointList.add(new Endpoint("/api/v1/auth/admin/**", HttpMethod.GET, Role.ADMIN));
    }

    @Override
    public void register() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Response> response = restTemplate.postForEntity(GATEWAY_URL, endpointList, Response.class);
        if (response.getStatusCode().isError()) throw new RuntimeException();
    }
}
