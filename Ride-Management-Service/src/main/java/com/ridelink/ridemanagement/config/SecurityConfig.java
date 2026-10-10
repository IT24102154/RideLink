package com.ridelink.ridemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // Swagger / OpenAPI
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Create ride - Passenger or Admin
                        .requestMatchers(HttpMethod.POST, "/api/rides")
                        .hasAnyRole("PASSENGER", "ADMIN")

                        // Update ride status - Driver or Admin
                        .requestMatchers(HttpMethod.PUT, "/api/rides/*/status")
                        .hasAnyRole("DRIVER", "ADMIN")

                        // Retrieve rides - Any authenticated user
                        .requestMatchers(HttpMethod.GET, "/api/rides/**")
                        .authenticated()

                        // Everything else
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

        UserDetails passenger = User.builder()
                .username("passenger")
                .password(passwordEncoder.encode("Passenger@123"))
                .roles("PASSENGER")
                .build();

        UserDetails driver = User.builder()
                .username("driver")
                .password(passwordEncoder.encode("Driver@123"))
                .roles("DRIVER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("Admin@123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(
                passenger,
                driver,
                admin
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}