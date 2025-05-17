package org.nextweb.converstions.security;

import org.nextweb.converstions.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class SecurityUser implements UserDetails {

    private final User user;

    public SecurityUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // In this example, we don't have roles, so we return an empty collection
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return null; // We don't have a password, we're using the X-Authenticated-User header
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Account is always valid
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // Account is always valid
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Credentials are always valid
    }

    @Override
    public boolean isEnabled() {
        return true;  // Account is always enabled
    }

    public User getUser() {
        return this.user;
    }
}
