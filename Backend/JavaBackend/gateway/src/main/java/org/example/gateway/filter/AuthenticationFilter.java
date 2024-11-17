package org.example.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.example.gateway.config.Carousel;
import org.example.gateway.filter.RouteValidator;
import org.example.gateway.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.util.List;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private final RouteValidator validator;
    private final RestTemplate template;
    private final JwtUtil jwtUtil;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private Carousel carousel;

    public AuthenticationFilter(JwtUtil jwtUtil, RestTemplate restTemplate, RouteValidator validator, Carousel carousel) {
        super(Config.class);
        this.carousel = carousel;
        this.jwtUtil = jwtUtil;
        this.template = restTemplate;
        this.validator = validator;
    }

//    @Override
//    public GatewayFilter apply(Config config) {
//        return ((exchange, chain) -> {
//            log.info("Processing request for path: {}", exchange.getRequest().getURI());
//            log.info("--START GatewayFilter");
//            if (validator.isSecure.test(exchange.getRequest())) {
//                if (!exchange.getRequest().getCookies().containsKey(HttpHeaders.AUTHORIZATION) && !exchange.getRequest().getCookies().containsKey("refresh")) {
//                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
//                    StringBuilder stringBuilder = new StringBuilder("{\n")
//                            .append("\"timestamp\": \"")
//                            .append(new Timestamp(System.currentTimeMillis()))
//                            .append("\",\n")
//                            .append("\"message\": \"This token is empty or invalid\",\n")
//                            .append("\"code\": \"A3\"\n")
//                            .append("}");
//
//                    return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
//                            .bufferFactory()
//                            .wrap((stringBuilder.toString()).getBytes())));
//                }
//
//                HttpCookie authCookie = exchange.getRequest().getCookies().get(HttpHeaders.AUTHORIZATION).get(0);
//                HttpCookie refreshCookie = exchange.getRequest().getCookies().get("refresh").get(0);
//                log.info("--START validate Token");
//                try {
//                    if (activeProfile.equals("test")) {
//                        log.debug("Init self auth methods (only for tests)");
//                        jwtUtil.validateToken(authCookie.getValue());
//                    } else {
//                        String cookies = new StringBuilder()
//                                .append(authCookie.getName())
//                                .append("=")
//                                .append(authCookie.getValue())
//                                .append(";")
//                                .append(refreshCookie.getName())
//                                .append("=")
//                                .append(refreshCookie.getValue()).toString();
//                        HttpHeaders httpHeaders = new HttpHeaders();
//                        httpHeaders.add("Cookie", cookies);
//                        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
//                        ResponseEntity<String> response;
//                        if (validator.isAdmin.test(exchange.getRequest())) {
//                            response = template.exchange("http://" + carousel.getUriAuth() + "/api/v1/auth/authorize", HttpMethod.GET, entity, String.class);
//                        } else {
//                            response = template.exchange("http://" + carousel.getUriAuth() + "/api/v1/auth/validate", HttpMethod.GET, entity, String.class);
//                        }
//                        if (response.getStatusCode() == HttpStatus.OK) {
//                            List<String> cookiesList = response.getHeaders().get(HttpHeaders.SET_COOKIE);
//                            if (cookiesList != null) {
//                                List<java.net.HttpCookie> httpCookie = java.net.HttpCookie.parse(cookiesList.get(0));
//                                for (java.net.HttpCookie cookie : httpCookie) {
//                                    exchange.getResponse().getCookies().add(cookie.getName(),
//                                            ResponseCookie.from(cookie.getName(), cookie.getValue())
//                                                    .domain(cookie.getDomain())
//                                                    .path(cookie.getPath())
//                                                    .maxAge(cookie.getMaxAge())
//                                                    .secure(cookie.getSecure())
//                                                    .httpOnly(cookie.isHttpOnly())
//                                                    .build());
//                                }
//                            }
//                            log.info("Successful login");
//                        }
//                    }
//                } catch (HttpClientErrorException e) {
//                    log.warn("Can't login bad token");
//                    String message = e.getMessage().substring(7);
//                    message = message.substring(0, message.length() - 1);
//                    ServerHttpResponse response = exchange.getResponse();
//                    HttpHeaders headers = response.getHeaders();
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
//                    return exchange.getResponse().writeWith(Flux.just(new DefaultDataBufferFactory().wrap(message.getBytes())));
//                }
//            }
//            log.info("--STOP validate Token");
//            log.info("--STOP GatewayFilter");
//            log.info("Forwarding request to downstream service: {}", exchange.getRequest().getURI());
//            return chain.filter(exchange).doOnSuccess(aVoid -> {
//                log.info("Przekierowanie zakończone sukcesem");
//            });
//        });
//    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("Processing request for path: {}", exchange.getRequest().getURI());
            log.info("--START GatewayFilter");
            if (validator.isSecure.test(exchange.getRequest())) {
                String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                String refreshHeader = exchange.getRequest().getHeaders().getFirst("Refresh-Token");
                if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                    log.warn("Authorization header is missing or invalid");
                    return buildErrorResponse(exchange, "Authorization token is missing or invalid", "A3", HttpStatus.UNAUTHORIZED);
                }

                String accessToken = authorizationHeader.substring(7); // Extract the Bearer token
                String refreshToken = (refreshHeader != null && refreshHeader.startsWith("Bearer "))
                        ? refreshHeader.substring(7)
                        : null;
                log.info("--START validate Access Token");
                try {
                    if (activeProfile.equals("test")) {
                        log.debug("Init self auth methods (only for tests)");
                        jwtUtil.validateToken(accessToken);
                    } else {
                        // Validate the access token via the auth service
                        HttpHeaders httpHeaders = new HttpHeaders();
                        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
                        if (refreshToken != null) {
                            httpHeaders.add("Refresh-Token", "Bearer " + refreshToken);
                        }

                        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
                        ResponseEntity<String> response;

                        // Call appropriate auth endpoint based on request type (admin or user)
                        String authUrl = validator.isAdmin.test(exchange.getRequest())
                                ? "http://" + carousel.getUriAuth() + "/api/v1/auth/authorize"
                                : "http://" + carousel.getUriAuth() + "/api/v1/auth/validate";

                        response = template.exchange(authUrl, HttpMethod.GET, entity, String.class);

                        if (response.getStatusCode() == HttpStatus.OK) {
                            log.info("Successful token validation");

                            // If auth service returns new tokens in headers, forward them
                            List<String> newAccessToken = response.getHeaders().get(HttpHeaders.AUTHORIZATION);
                            List<String> newRefreshToken = response.getHeaders().get("Refresh-Token");

                            if (newAccessToken != null && !newAccessToken.isEmpty()) {
                                exchange.getResponse().getHeaders().set(HttpHeaders.AUTHORIZATION, newAccessToken.get(0));
                            }
                            if (newRefreshToken != null && !newRefreshToken.isEmpty()) {
                                exchange.getResponse().getHeaders().set("Refresh-Token", newRefreshToken.get(0));
                            }
                        }
                    }
                } catch (HttpClientErrorException e) {
                    log.warn("Token validation failed: {}", e.getMessage());
                    return buildErrorResponse(exchange, "Invalid or expired token", "TOKEN_INVALID", HttpStatus.UNAUTHORIZED);
                }
            }

            log.info("--STOP validate Token");
            log.info("--STOP GatewayFilter");
            log.info("Forwarding request to downstream service: {}", exchange.getRequest().getURI());
            return chain.filter(exchange).doOnSuccess(aVoid -> {
                log.info("Przekierowanie zakończone sukcesem");
            });
        };
    }

    private Mono<Void> buildErrorResponse(ServerWebExchange exchange, String message, String code, HttpStatus status) {
        log.warn("Error: {} - {}", code, message);

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        StringBuilder responseBody = new StringBuilder("{\n")
                .append("\"timestamp\": \"")
                .append(new Timestamp(System.currentTimeMillis()))
                .append("\",\n")
                .append("\"message\": \"")
                .append(message)
                .append("\",\n")
                .append("\"code\": \"")
                .append(code)
                .append("\"\n")
                .append("}");

        return exchange.getResponse().writeWith(Mono.just(
                exchange.getResponse().bufferFactory().wrap(responseBody.toString().getBytes())));
    }


    public static class Config {

    }


}
