package com.projects.cavany.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import com.projects.cavany.domain.RecipeDetails.ExtendedIngredientsCollection;

public interface ExtendedIngredientsCollectionRepositoryNeo4j extends Neo4jRepository<ExtendedIngredientsCollection, Long> {
    @Query("MATCH (e:ExtendedIngredientsCollection) RETURN max(e.id)")
    Optional<Long> findMaxId();
}