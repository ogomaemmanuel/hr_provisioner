package com.ogoma.hr_provisioner.config;

import com.ogoma.hr_provisioner.auth.AppUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ApplicationSecurityConfig {

    private final AppUserDetailsService userDetailsService;

    public ApplicationSecurityConfig(AppUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/plans/**").permitAll()
                        .requestMatchers("/payments/**").permitAll()
                        .requestMatchers("/subscriptions/**").permitAll()
                        .anyRequest().permitAll()
                )
                .userDetailsService(this.userDetailsService)
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

}


