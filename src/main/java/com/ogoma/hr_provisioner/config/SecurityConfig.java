package com.ogoma.hr_provisioner.config;

import com.ogoma.hr_provisioner.auth.AppUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final AppUserDetailsService userDetailsService;

    public SecurityConfig(AppUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security
                .authorizeHttpRequests(httpRequest -> httpRequest.
                        requestMatchers("/login").permitAll()
                        .anyRequest().authenticated())
                .userDetailsService(userDetailsService)
                .formLogin(formLogin->
                        formLogin.loginPage("/login")
                )
                .build();
    }
}
