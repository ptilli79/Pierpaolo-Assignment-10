package com.projects.cavany.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projects.cavany.domain.RecipeDetails.ExtendedIngredient;

@Repository
public interface ExtendedIngredientRepositoryNeo4j extends Neo4jRepository<ExtendedIngredient, UUID> {

	@Query("MATCH (ingredient:ExtendedIngredient) WHERE id(ingredient) = $id RETURN ingredient")
	Optional<ExtendedIngredient> findById(@Param("id") Long id);
	
	@Query("MERGE (ei:ExtendedIngredient {uuid: $uuid}) " +
	           "ON CREATE SET ei.id = $id, ei.aisle = $aisle, ei.image = $image, " +
	           "ei.consistency = $consistency, ei.name = $name, ei.nameClean = $nameClean, " +
	           "ei.original = $original, ei.originalName = $originalName, ei.amount = $amount, " +
	           "ei.unit = $unit, ei.meta = $meta " +
	           "ON MATCH SET ei.aisle = $aisle, ei.image = $image, ei.consistency = $consistency, " +
	           "ei.name = $name, ei.nameClean = $nameClean, ei.original = $original, " +
	           "ei.originalName = $originalName, ei.amount = $amount, ei.unit = $unit, ei.meta = $meta " +
	           "RETURN ei")
	ExtendedIngredient merge(UUID uuid, Long id, String aisle, String image, String consistency, String name,
	                        String nameClean, String original, String originalName, Double amount, String unit, List<String> meta);
	
    @Query("MATCH (r:RecipeDetails {Id: $recipeId})-[rel:HAS_INGREDIENT]->(i:ExtendedIngredient) DETACH DELETE i")
    void deleteIngredientsByRecipeId(Long recipeId);
	}

