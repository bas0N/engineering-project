package org.example.message.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/v3/api-docs/**",         // Swagger API documentation
                                "/swagger-ui/**",          // Swagger UI resources
                                "/swagger-ui.html"         // Swagger UI page
                        ).permitAll()                    // Allow access to Swagger endpoints
                        .anyRequest().permitAll()        // Allow all other requests
                )
                .csrf(AbstractHttpConfigurer::disable);

        SecurityFilterChain chain = http.build();

        chain.getFilters().forEach(filter -> System.out.println(filter.getClass()));

        return chain;
    }
}
