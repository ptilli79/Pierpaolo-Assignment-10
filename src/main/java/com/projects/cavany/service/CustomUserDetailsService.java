package com.projects.cavany.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.projects.cavany.domain.WebUser;
import com.projects.cavany.repository.WebUserRepository;

import security.CustomSecurityUser;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final WebUserRepository webUserRepository;

    public CustomUserDetailsService(WebUserRepository webUserRepository) {
        this.webUserRepository = webUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        WebUser webUser = webUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new CustomSecurityUser(webUser);
    }
}
