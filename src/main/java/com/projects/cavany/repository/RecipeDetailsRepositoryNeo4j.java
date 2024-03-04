package com.projects.cavany.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import com.projects.cavany.domain.RecipeDetails;

@Repository
public interface RecipeDetailsRepositoryNeo4j extends Neo4jRepository<RecipeDetails, Long> {
	@Query("MATCH (r:RecipeDetails) RETURN max(r.Id)")
	Optional<Long> findMaxId();
	
	@Query("MATCH (recipe:RecipeDetails) RETURN recipe.Id")
	Set<Long> findAllIds();
}
