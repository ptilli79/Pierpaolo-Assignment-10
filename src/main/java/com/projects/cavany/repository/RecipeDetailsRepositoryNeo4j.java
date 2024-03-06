package com.projects.cavany.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projects.cavany.domain.RecipeDetails;

@Repository
public interface RecipeDetailsRepositoryNeo4j extends Neo4jRepository<RecipeDetails, Long> {
	@Query("MATCH (r:RecipeDetails) RETURN max(r.Id)")
	Optional<Long> findMaxId();
	
	@Query("MATCH (recipe:RecipeDetails) RETURN recipe.Id")
	Set<Long> findAllIds();
	
	@Query("MATCH (recipe:RecipeDetails)-[:HAS_INGREDIENT]->(ingredient:ExtendedIngredient) " +
		       "WHERE ANY(d IN $diets WHERE d IN recipe.diets) AND recipe.glutenFree = $glutenFree " +
		       "WITH recipe, COLLECT(DISTINCT toLower(ingredient.nameClean)) AS ingredients " +
		       "OPTIONAL MATCH (recipe)-[:HAS_PREPARATION_INSTRUCTIONS]->(:AnalyzedInstruction)-[:HAS_STEPS]->(:Step)-[:HAS_INGREDIENTS]->(stepIngredient:Ingredient) " +
		       "WITH recipe, ingredients, COLLECT(DISTINCT toLower(stepIngredient.name)) AS stepIngredients " +
		       "WITH recipe, ingredients + stepIngredients AS allIngredientsList " +
		       "UNWIND allIngredientsList AS ingredientNames " +
		       "WITH recipe, COLLECT(DISTINCT ingredientNames) AS distinctIngredients " +
		       "WHERE NONE(ing IN distinctIngredients WHERE ing IN $excludedIngredients ) " +
		       "RETURN DISTINCT recipe.Id AS id")
	List<Long> findFilteredRecipes(List<String> diets, boolean glutenFree, List<String> excludedIngredients);

    @Query("MATCH (recipe:RecipeDetails) WHERE recipe.Id IN $recipeIds " +
            "RETURN recipe ")
	List<RecipeDetails> findLimitedRecipesByIds(List<Long> recipeIds);
    

}