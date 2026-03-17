package com.interiordesignplanner.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.interiordesignplanner.authentication.User;

/**
 * User Details (Core Information) for Spring security.
 * 
 * <p>
 * Including user's username, password and roles
 * </p>
 */
public class ApplicationUserDetails implements UserDetails {

    private User user;

    public ApplicationUserDetails(User user) {
        this.user = user;
    }

    /**
     * Implemented pre-authorization for privileges with Granted Authority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Maps the user's roles established in the database
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRoles().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
