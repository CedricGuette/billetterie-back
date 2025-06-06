package com.jeuxolympiques.billetterie.configuration;

import com.jeuxolympiques.billetterie.filter.JwtFilter;
import com.jeuxolympiques.billetterie.services.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
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

    private final CustomUserDetailService customUserDetailService;
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
                        auth.requestMatchers("api/auth/*").permitAll()
                                .requestMatchers("/uploads/*","tickets/pdf/*").permitAll()
                                .requestMatchers("api/stripe/checkout/*").permitAll()
                                .requestMatchers("api/stripe/checkout/validation/**").permitAll()
                                .requestMatchers("api/customers/*").hasRole("USER")
                                .requestMatchers("api/moderators/*").hasRole("MODERATOR")
                                .requestMatchers("api/admin/*").hasRole("ADMIN")
                                .requestMatchers("api/security/*").hasRole("SECURITY")
                                .requestMatchers("api/tickets/*").permitAll()//a changer en prod
                                .anyRequest().authenticated())
                .cors(Customizer.withDefaults())
                .addFilterBefore(new JwtFilter(customUserDetailService, jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
