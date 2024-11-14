package org.example.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.example.gateway.entity.Endpoint;
import org.example.gateway.filter.RouteValidator;
import org.coffeecode.entity.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/gateway")
@RequiredArgsConstructor
public class RegistrationController {
    private final RouteValidator routeValidator;
    @PostMapping
    public ResponseEntity<Response> register(@RequestBody List<Endpoint> endpoints){
        routeValidator.addEndpoints(endpoints);
        return ResponseEntity.ok(new Response("Successfully register new endpoints"));
    }
}
