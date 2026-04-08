package dev.retreever.example.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Principal;
import java.util.List;
import java.util.Set;

public record MockAuthenticatedUser(
        String email,
        String deviceId,
        Set<String> authorities
) implements Principal {

    public MockAuthenticatedUser {
        authorities = authorities == null ? Set.of() : Set.copyOf(authorities);
    }

    @Override
    public String getName() {
        return email;
    }

    public List<GrantedAuthority> grantedAuthorities() {
        return authorities.stream()
                .sorted()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }

    public boolean hasAuthority(String authority) {
        return authorities.contains(authority);
    }
}
