package com.projects.cavany.domain.Security;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Node
public class WebUser {
    @Id
    @GeneratedValue
    private Long id;
    private String nombre;
    private String username;
    private String email;
    private String password;
    private RoleUser userRole;

    @Relationship(type = "HAS_AUTHORITY", direction = Relationship.Direction.OUTGOING)
    private Set<Authorities> authorities = new HashSet<>();

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleUser getUsuarioRole() {
        return userRole;
    }

    public void setUsuarioRole(RoleUser usuarioRole) {
        this.userRole = usuarioRole;
    }

    public Set<Authorities> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authorities> authorities) {
        this.authorities = authorities;
    }

    // Corrected getAuthorities() for GrantedAuthority:
    public List<SimpleGrantedAuthority> getAuthoritiesForGrantedAuthority() {
        return authorities.stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
            .collect(Collectors.toList());
    }
}