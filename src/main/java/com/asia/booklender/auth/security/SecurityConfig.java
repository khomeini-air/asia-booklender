package com.asia.booklender.auth.security;

import com.asia.booklender.auth.filter.JwtAuthFilter;
import com.asia.booklender.shared.observability.filter.RequestLoggingFilter;
import com.asia.booklender.shared.security.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter,
            RequestLoggingFilter requestLoggingFilter
    ) throws Exception {
        http
                // CSRF is disabled since the system is stateless hence no csrf attack
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // Admin-only endpoints
                        .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.getRole())

                        // Admin and Member endpoints
                        .requestMatchers("/api/books/**").hasAnyRole(Role.MEMBER.getRole(), Role.ADMIN.getRole())
                        .requestMatchers("/api/members/**").hasAnyRole(Role.MEMBER.getRole(), Role.ADMIN.getRole())
                        .requestMatchers( "/api/loans/**").hasAnyRole(Role.MEMBER.getRole(), Role.ADMIN.getRole())
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthFilter, RequestLoggingFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Admin user - can perform all operations
        UserDetails admin = User.builder()
                .username("admin@booklender.com")
                .password(passwordEncoder().encode("admin123"))
                .roles(Role.ADMIN.getRole())
                .build();

        // Member users - username matches their email in the members table
        UserDetails member1 = User.builder()
                .username("donald.trump@usa.com")
                .password(passwordEncoder().encode("donald123"))
                .roles(Role.MEMBER.getRole())
                .build();

        UserDetails member2 = User.builder()
                .username("barack.obama@usa.com")
                .password(passwordEncoder().encode("barack123"))
                .roles(Role.MEMBER.getRole())
                .build();

        UserDetails member3 = User.builder()
                .username("john.rambo@usa.com")
                .password(passwordEncoder().encode("john123"))
                .roles(Role.MEMBER.getRole())
                .build();

        return new InMemoryUserDetailsManager(admin, member1, member2, member3);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
