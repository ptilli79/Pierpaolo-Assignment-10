package com.projects.cavany.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.projects.cavany.domain.Security.WebUser;

public interface WebUserRepository extends Neo4jRepository<WebUser, Long> {
    Optional<WebUser> findByUsername(String username);
}