package org.nextweb.converstions.security;

import org.nextweb.converstions.repository.UserRepository;
import org.nextweb.converstions.util.SystemLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                SystemLogger.log("username security", username);
                return userRepository.findByName(username)
                        .map(SecurityUser::new)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        // No password encoder, we are trusting the X-Authenticated-User header
        authProvider.setPasswordEncoder(org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
        return authProvider;
    }

    @Bean
    public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider() {
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(userDetailsService()));
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Arrays.asList(preAuthenticatedAuthenticationProvider(), authenticationProvider())); // Changed to a list
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity in this example
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/users/**", "/conferences/**", "/conversations/**").authenticated() // Secure all endpoints
                        .anyRequest().permitAll() // Allow all other requests (e.g., static resources, login page if any)
                )
                .addFilterAt(requestHeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // Custom filter to handle the X-Authenticated-User header

        return http.build();
    }

    @Bean
    public RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter() {
        RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
        filter.setPrincipalRequestHeader("X-Authenticated-User");
        try {
            filter.setAuthenticationManager(authenticationManager());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        filter.setExceptionIfHeaderMissing(false); // Important: Don't throw an exception if the header is missing
        return filter;
    }

}
