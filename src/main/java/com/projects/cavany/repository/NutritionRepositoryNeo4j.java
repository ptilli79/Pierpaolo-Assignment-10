package com.projects.cavany.repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import com.projects.cavany.domain.RecipeDetails.WinePairing;
import com.projects.cavany.domain.RecipeDetails.Nutrition.Nutrition;

public interface NutritionRepositoryNeo4j extends Neo4jRepository<Nutrition, Long> {
    @Query("MERGE (n:Nutrition) " +
            "RETURN n")
     Nutrition mergeNutrition(@Param("nutrition") Nutrition nutrition);
    
    @Query("MATCH (r:RecipeDetails {Id: $recipeId})-[rel:HAS_NUTRITION]->(n:Nutrition) DETACH DELETE n")
    void deleteNutritionByRecipeId(Long recipeId);

    @Query("MATCH (n:Nutrition) WHERE n.id = $id RETURN n")
    Optional<Nutrition> findNutritionById(Long id);
    
    @Query("MATCH path = (n:Nutrition)-[r*1..3]->(related) WHERE n.id = $id RETURN n, relationships(path) AS rels, nodes(path) AS nodes")
    Map<String, Object> findNutritionWithRelationsById(Long id);
}
