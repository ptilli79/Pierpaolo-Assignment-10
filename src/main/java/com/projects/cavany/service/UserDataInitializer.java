package com.projects.cavany.service;


import com.projects.cavany.domain.Security.Authorities;
import com.projects.cavany.domain.Security.RoleUser;
import com.projects.cavany.domain.Security.WebUser;
import com.projects.cavany.repository.WebUserRepositoryNeo4j;

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
    private final WebUserRepositoryNeo4j webUserRepositoryNeo4j;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDataInitializer(WebUserRepositoryNeo4j webUserRepositoryNeo4j, PasswordEncoder passwordEncoder) {
        this.webUserRepositoryNeo4j = webUserRepositoryNeo4j;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initializeUsers() {
        if (webUserRepositoryNeo4j.count() == 0) {
            WebUser adminUser = createUser("admin", "admin@example.com", "admin123", RoleUser.ROLE_ADMIN, Arrays.asList("read", "write", "update", "delete"));
            WebUser regularUser = createUser("user", "user@example.com", "user123", RoleUser.ROLE_USER, Arrays.asList("read"));

            webUserRepositoryNeo4j.save(adminUser);
            webUserRepositoryNeo4j.save(regularUser);
        }
    }

    public WebUser createUser(String username, String email, String password, RoleUser role, List<String> permissions) {
        if (webUserRepositoryNeo4j.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

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
        return webUserRepositoryNeo4j.save(user);
    }
}
