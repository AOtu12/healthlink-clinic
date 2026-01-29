package com.healthlink.clinicsystem.config;

import com.healthlink.clinicsystem.security.JwtAuthFilter;
import com.healthlink.clinicsystem.security.JwtTokenProvider;
import com.healthlink.clinicsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    // ===============================
    // LOAD USER FROM DATABASE
    // ===============================
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .map(u -> org.springframework.security.core.userdetails.User
                        .builder()
                        .username(u.getUsername())
                        .password(u.getPassword())
                        .roles(u.getRole().name())
                        .disabled(Boolean.FALSE.equals(u.getIsActive()))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    // Password hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(userDetailsService(), tokenProvider);
    }

    // ===============================
    // MAIN SECURITY CONFIGURATION
    // ===============================
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // ============================================================
        // DISABLE CSRF FOR APIs + DELETE ROUTES (Fixes 404 Delete Bug)
        // ============================================================
        http.csrf(csrf -> csrf.ignoringRequestMatchers(
                "/api/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",

                // 🔥 DELETE FIX — allow POST delete requests
                "/appointments/*/delete",
                "/patients/*/delete",
                "/doctors/*/delete"
        ));

        http.authenticationProvider(authenticationProvider());

        http.authorizeHttpRequests(auth -> auth

                // ============================================
                // PUBLIC ROUTES
                // ============================================
                .requestMatchers(
                        "/",
                        "/login",
                        "/error",
                        "/forgot-password",   // public
                        "/reset-password",    // public
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                ).permitAll()

                // API routes
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/**").authenticated()

                // Role-based
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/doctor/**").hasRole("DOCTOR")

                // Receptionist access
                .requestMatchers(
                        "/appointments/**",
                        "/patients/**",
                        "/doctors/**",
                        "/reception/**"
                ).hasRole("RECEPTIONIST")

                .anyRequest().authenticated()
        );

        // ===============================
        // LOGIN CONFIG
        // ===============================
        http.formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)
                .failureUrl("/login?error=true")
                .permitAll()
        );

        // ===============================
        // LOGOUT CONFIG
        // ===============================
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );

        // ===============================
        // JWT FILTER BEFORE SPRING LOGIN
        // ===============================
        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
