package com.projects.cavany.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projects.cavany.domain.RecipeDetails.ExtendedIngredient;

@Repository
public interface ExtendedIngredientRepositoryNeo4j extends Neo4jRepository<ExtendedIngredient, Long> {

	@Query("MATCH (ingredient:ExtendedIngredient) WHERE id(ingredient) = $id RETURN ingredient")
	Optional<ExtendedIngredient> findById(@Param("id") Long id);

}
