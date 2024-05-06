package com.projects.cavany.service;


import com.projects.cavany.domain.Security.Authorities;
import com.projects.cavany.domain.Security.RoleUser;
import com.projects.cavany.domain.Security.WebUser;
import com.projects.cavany.repository.WebUserRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserDataInitializer {
    private final WebUserRepository webUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDataInitializer(WebUserRepository webUserRepository, PasswordEncoder passwordEncoder) {
        this.webUserRepository = webUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initializeUsers() {
        if (webUserRepository.count() == 0) {
            WebUser adminUser = createUser("admin", "admin@example.com", "admin123", RoleUser.ROLE_ADMIN, Arrays.asList("read", "write", "update", "delete"));
            WebUser regularUser = createUser("user", "user@example.com", "user123", RoleUser.ROLE_USER, Arrays.asList("read"));

            webUserRepository.save(adminUser);
            webUserRepository.save(regularUser);
        }
    }

    private WebUser createUser(String username, String email, String password, RoleUser role, List<String> permissions) {
        WebUser user = new WebUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setUsuarioRole(role);

        Set<Authorities> authorities = new HashSet<>();
        for (String permission : permissions) {
            Authorities authority = new Authorities();
            authority.setAuthority(permission);
            authority.setUser(user);
            authorities.add(authority);
        }

        user.setAuthorities(authorities);
        return user;
    }
}
