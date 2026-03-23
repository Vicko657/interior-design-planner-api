package com.interiordesignplanner.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.authentication.UserRepository;

/**
 * Spring security - User Login and Authentication
 * 
 */
@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    public ApplicationUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    // Retrieves the user signed in
    @Override
    public ApplicationUserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new ApplicationUserDetails(user);
    }

}
