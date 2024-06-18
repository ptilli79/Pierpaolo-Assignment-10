package com.projects.cavany.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projects.cavany.domain.RecipeDetails.RecipeDetails;
import com.projects.cavany.dto.RecipeDetails.RecipeWithIngredientsDTOFromEntity;

@Repository
public interface RecipeDetailsRepositoryNeo4j extends Neo4jRepository<RecipeDetails, UUID>, CustomRecipeDetailsRepository {
	@Query("MATCH (r:RecipeDetails) RETURN max(r.Id)")
	Optional<Long> findMaxId();
	
	@Query("MATCH (recipe:RecipeDetails) RETURN recipe.Id")
	Set<Long> findAllIds();
/*	
	@Query("""
			// Step 1: Fetch Recipe IDs based on diets
			MATCH (recipe:RecipeDetails)
			WHERE ALL(d IN $diets WHERE d IN recipe.diets)
			WITH DISTINCT recipe.Id AS recipeId, recipe

			// Step 2: Fetch Ingredients
			OPTIONAL MATCH (recipe)-[:HAS_INGREDIENT]->(ingredient:ExtendedIngredient)
			WITH recipe, recipeId, COLLECT(DISTINCT toLower(ingredient.name)) AS rawIngredients, COLLECT(DISTINCT toLower(ingredient.nameClean)) AS cleanIngredients

			// Step 3: Fetch Nutrition Ingredients
			OPTIONAL MATCH (recipe)-[:HAS_INGREDIENT_NUTRITION]->(nutritionIngredient:IngredientNutrition)
			WITH recipe, recipeId, rawIngredients, cleanIngredients, COLLECT(DISTINCT toLower(nutritionIngredient.name)) AS nutritionRawIngredients, COLLECT(DISTINCT toLower(nutritionIngredient.nameClean)) AS nutritionCleanIngredients

			// Step 4: Fetch Step Ingredients
			OPTIONAL MATCH (recipe)-[:HAS_ANALYZED_INSTRUCTIONS]->(instruction:AnalyzedInstruction)-[:HAS_STEPS]->(:Step)-[:HAS_INGREDIENTS]->(stepIngredient:Ingredient)
			WITH recipe, recipeId, rawIngredients, cleanIngredients, nutritionRawIngredients, nutritionCleanIngredients, COLLECT(DISTINCT toLower(stepIngredient.name)) AS stepRawIngredients, COLLECT(DISTINCT toLower(stepIngredient.nameClean)) AS stepCleanIngredients

			// Combine all ingredients and filter excluded ingredients
			WITH recipe, recipeId, rawIngredients + cleanIngredients + nutritionRawIngredients + nutritionCleanIngredients + stepRawIngredients + stepCleanIngredients AS allIngredientsList
			UNWIND allIngredientsList AS ingredientName
			WITH recipe, recipeId, recipe.title AS recipeTitle, recipe.diets AS r-ecipeDiets, recipe.dishTypes AS recipeDishTypes, COLLECT(DISTINCT ingredientName) AS distinctIngredients
			WHERE NONE(ing IN distinctIngredients WHERE ing IN $excludedIngredients)
			RETURN DISTINCT recipeId
			ORDER BY recipeId ASC
			""")
		List<Long> findFilteredRecipes(List<String> diets, boolean glutenFree, boolean dairyFree, List<String> excludedIngredients);
*/	
	@Query("""
		    MATCH (recipe:RecipeDetails)
		    OPTIONAL MATCH (recipe)-[:HAS_INGREDIENTS_COLLECTION]->(ingredientCollection:ExtendedIngredientsCollection)-[:HAS_INGREDIENT]->(ingredient:ExtendedIngredient)
		    WITH recipe, recipe.Id AS recipeId, recipe.title AS recipeTitle, recipe.diets AS recipeDiets, recipe.dishTypes AS recipeDishTypes, 
		         COLLECT(DISTINCT toLower(ingredient.name)) + COLLECT(DISTINCT toLower(ingredient.nameClean)) AS allIngredientsList
		    WITH recipe, recipeId, recipeTitle, recipeDiets, recipeDishTypes, 
		         REDUCE(s = [], i IN allIngredientsList | CASE WHEN i IS NOT NULL AND NOT i IN s THEN s + i ELSE s END) AS distinctIngredients
		    WHERE ALL(d IN $diets WHERE d IN recipeDiets) AND
		          NONE(ing IN distinctIngredients WHERE ing IN $excludedIngredients) AND
		          (CASE WHEN $glutenFree THEN recipe.glutenFree ELSE true END) AND
		          (CASE WHEN $dairyFree THEN recipe.dairyFree ELSE true END)
		    RETURN DISTINCT recipeId
		    ORDER BY recipeId ASC
		    """)
		List<Long> findFilteredRecipes(List<String> diets, boolean glutenFree, boolean dairyFree, List<String> excludedIngredients);




    @Query("MATCH (recipe:RecipeDetails) WHERE recipe.Id IN $recipeIds " +
            "RETURN recipe ")
	List<RecipeDetails> findLimitedRecipesByIds(List<Long> recipeIds);
    
    @Query("MATCH (recipe:RecipeDetails {Id: $id}) RETURN recipe")
    Optional<RecipeDetails> findRecipeById(Long id);
}
