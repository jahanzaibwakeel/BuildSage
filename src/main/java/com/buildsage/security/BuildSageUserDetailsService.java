package com.buildsage.security;

import com.buildsage.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BuildSageUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public BuildSageUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository
                .findByEmail(username)
                .map(user -> new CurrentUser(
                        user.getId(), user.getEmail(), user.getPasswordHash(), user.getRole(), user.isEnabled()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
