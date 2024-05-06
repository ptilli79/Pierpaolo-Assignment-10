package com.projects.cavany.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projects.cavany.domain.RecipeDetails.RecipeDetails;
import com.projects.cavany.dto.RecipeDetails.RecipeWithIngredientsDTOFromEntity;

@Repository
public interface RecipeDetailsRepositoryNeo4j extends Neo4jRepository<RecipeDetails, Long> {
	@Query("MATCH (r:RecipeDetails) RETURN max(r.Id)")
	Optional<Long> findMaxId();
	
	@Query("MATCH (recipe:RecipeDetails) RETURN recipe.Id")
	Set<Long> findAllIds();
	
	@Query("MATCH (recipe:RecipeDetails)-[:HAS_INGREDIENT]->(ingredient:ExtendedIngredient) " +
		       "WHERE ALL(d IN $diets WHERE d IN recipe.diets)" +
		       "WITH recipe, COLLECT(DISTINCT toLower(ingredient.name)) AS rawIngredients, " +
		       "COLLECT(DISTINCT toLower(ingredient.nameClean)) AS cleanIngredients " +
		       "OPTIONAL MATCH (recipe)-[:HAS_PREPARATION_INSTRUCTIONS]->(:AnalyzedInstruction)-[:HAS_STEPS]->(:Step)-[:HAS_INGREDIENTS]->(stepIngredient:Ingredient) " +
		       "WITH recipe, rawIngredients, cleanIngredients, COLLECT(DISTINCT toLower(stepIngredient.name)) AS stepRawIngredients, " +
		       "COLLECT(DISTINCT toLower(stepIngredient.nameClean)) AS stepCleanIngredients " +
		       "WITH recipe, rawIngredients + cleanIngredients + stepRawIngredients + stepCleanIngredients AS allIngredientsList " +
		       "UNWIND allIngredientsList AS ingredientNames " +
		       "WITH recipe, COLLECT(DISTINCT ingredientNames) AS distinctIngredients " +
		       "WHERE NONE(ing IN distinctIngredients WHERE ing IN $excludedIngredients) " +
		       "RETURN DISTINCT recipe.Id")
		List<Long> findFilteredRecipes(List<String> diets, boolean glutenFree, boolean dairyFree, List<String> excludedIngredients);


    @Query("MATCH (recipe:RecipeDetails) WHERE recipe.Id IN $recipeIds " +
            "RETURN recipe ")
	List<RecipeDetails> findLimitedRecipesByIds(List<Long> recipeIds);
    






    

}
