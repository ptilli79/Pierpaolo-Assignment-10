package com.projects.cavany.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.projects.cavany.domain.Security.WebUser;
import com.projects.cavany.repository.WebUserRepositoryNeo4j;
import com.projects.cavany.security.CustomSecurityUser;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final WebUserRepositoryNeo4j webUserRepositoryNeo4j;

    public CustomUserDetailsService(WebUserRepositoryNeo4j webUserRepositoryNeo4j) {
        this.webUserRepositoryNeo4j = webUserRepositoryNeo4j;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        WebUser webUser = webUserRepositoryNeo4j.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        Set<GrantedAuthority> grantedAuthorities = webUser.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toSet());
        return new org.springframework.security.core.userdetails.User(
                webUser.getUsername(), webUser.getPassword(), grantedAuthorities);
    }
}
