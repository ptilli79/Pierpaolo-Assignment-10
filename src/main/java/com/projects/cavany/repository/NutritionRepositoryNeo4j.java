package com.projects.cavany.repository;

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
}
