package com.projects.cavany.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import com.projects.cavany.domain.Security.WebUser;

@Repository
public interface WebUserRepositoryNeo4j extends Neo4jRepository<WebUser, Long> {
    Optional<WebUser> findByUsername(String username);
}