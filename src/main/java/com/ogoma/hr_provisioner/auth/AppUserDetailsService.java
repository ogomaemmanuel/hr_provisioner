package com.ogoma.hr_provisioner.auth;

import com.ogoma.hr_provisioner.auth.entities.UserEntity;
import com.ogoma.hr_provisioner.auth.repositories.UserEntityRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AppUserDetailsService implements UserDetailsService {
    private final UserEntityRepository userRepository;

    public AppUserDetailsService(UserEntityRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = this.userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));
        return user;
    }
}
