package com.jeuxolympiques.billetterie.configuration;

import com.jeuxolympiques.billetterie.filter.JwtFilter;
import com.jeuxolympiques.billetterie.services.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    CustomUserDetailService customUserDetailService;

    private final JwtUtils jwtUtils;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    // On crée un Bean pour gérer les autorisations lors des requêtes
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/*", "/api/event", "/uploads/event/*", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .requestMatchers("/api/stripe/checkout/*", "/api/stripe/checkout/validation/**").hasRole("USER")
                                .requestMatchers("/api/customers/*", "/tickets/pdf/*").hasRole("USER")
                                .requestMatchers("/uploads/verification/*").hasRole("MODERATOR")
                                .requestMatchers("/api/moderators/*").hasRole("MODERATOR")
                                .requestMatchers("/api/security/*").hasRole("SECURITY")
                                .requestMatchers("/api/admin/*","/api/event/post", "/api/event/delete/*", "/api/event/update/*").hasRole("ADMIN")
                                .anyRequest().authenticated())
                .cors(Customizer.withDefaults())
                .addFilterBefore(new JwtFilter(customUserDetailService, jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
