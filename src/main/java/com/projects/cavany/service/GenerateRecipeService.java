package com.projects.cavany.service;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.neo4j.driver.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Service;

import com.projects.cavany.dto.ComplexSearch.ComplexSearchResultItemDTO;
import com.projects.cavany.dto.ComplexSearch.ComplexSearchResultsDTO;
import com.projects.cavany.dto.RecipeDetails.ExtendedIngredientDTO;
import com.projects.cavany.dto.RecipeDetails.RecipeDetailsDTO;
import com.projects.cavany.dto.RecipeDetails.RecipeWithIngredientsDTOFromEntity;
import com.projects.cavany.repository.RecipeDetailsRepository;

@Service
public class GenerateRecipeService {
	
	@Autowired
	RecipeDetailsRepository recipeDetailsRepository;
	
	
    @Autowired
    private Neo4jClient neo4jClient;

    // Constructor injection of Neo4jClient
    public GenerateRecipeService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public List<RecipeWithIngredientsDTOFromEntity> findLimitedRecipesWithIngredients(List<Long> recipeIds, List<String> dishTypes) {
    	String cypherQuery = 
    		    "MATCH (recipe:RecipeDetails) " +
    		    "WHERE recipe.Id IN $recipeIds AND ANY(dishType IN $dishTypes WHERE dishType IN recipe.dishTypes) " +
    		    "WITH DISTINCT recipe.Id AS recipeId, recipe " +

    		    // Step 2: Fetch Ingredients
    		    "OPTIONAL MATCH (recipe)-[:HAS_INGREDIENT]->(ingredient:ExtendedIngredient) " +
    		    "WITH recipe, recipeId, COLLECT(DISTINCT toLower(ingredient.name)) AS rawIngredients, COLLECT(DISTINCT toLower(ingredient.nameClean)) AS cleanIngredients " +

    		    // Step 3: Fetch Nutrition Ingredients
    		    "OPTIONAL MATCH (recipe)-[:HAS_INGREDIENT_NUTRITION]->(nutritionIngredient:IngredientNutrition) " +
    		    "WITH recipe, recipeId, rawIngredients, cleanIngredients, COLLECT(DISTINCT toLower(nutritionIngredient.name)) AS nutritionRawIngredients, COLLECT(DISTINCT toLower(nutritionIngredient.nameClean)) AS nutritionCleanIngredients " +

    		    // Step 4: Fetch Step Ingredients
    		    "OPTIONAL MATCH (recipe)-[:HAS_ANALYZED_INSTRUCTIONS]->(instruction:AnalyzedInstruction)-[:HAS_STEPS]->(:Step)-[:HAS_INGREDIENTS]->(stepIngredient:Ingredient) " +
    		    "WITH recipe, recipeId, rawIngredients, cleanIngredients, nutritionRawIngredients, nutritionCleanIngredients, COLLECT(DISTINCT toLower(stepIngredient.name)) AS stepRawIngredients, COLLECT(DISTINCT toLower(stepIngredient.nameClean)) AS stepCleanIngredients " +

    		    // Combine all ingredients
    		    "WITH recipe, recipeId, rawIngredients + cleanIngredients + nutritionRawIngredients + nutritionCleanIngredients + stepRawIngredients + stepCleanIngredients AS allIngredientsList " +
    		    "UNWIND allIngredientsList AS ingredientName " +
    		    "WITH recipe, recipeId, recipe.title AS recipeTitle, COLLECT(DISTINCT ingredientName) AS distinctIngredients " +
    		    "RETURN DISTINCT recipeId, recipe.title AS recipeTitle, distinctIngredients AS recipeIngredients " +
    		    "ORDER BY recipeId ASC";


        // Assuming neo4jClient is correctly set up to interact with your Neo4j database
        return neo4jClient.query(cypherQuery)
                .bindAll(Map.of("recipeIds", recipeIds, "dishTypes", dishTypes)) // Bind recipeIds and dishTypes to the query
                .fetchAs(RecipeWithIngredientsDTOFromEntity.class)
                .mappedBy((typeSystem, record) -> {
                    Long recipeId = record.get("recipeId").asLong();
                    String recipeTitle = record.get("recipeTitle").asString();
                    List<String> ingredients = record.get("recipeIngredients").asList(value -> value.asString()); // Extracting ingredients as a list of strings
                    return new RecipeWithIngredientsDTOFromEntity(recipeId, recipeTitle, ingredients);
                })
                .all()
                .stream()
                .collect(Collectors.toList());
    }





	
    public void generateRecipeCSV(Map<Long, RecipeDetailsDTO> recipeDetailsMap, String outputPath) throws IOException {
        if (recipeDetailsMap != null && !recipeDetailsMap.isEmpty()) {
        	try (FileWriter writer = new FileWriter(outputPath)) {
        	    // Write header row to the CSV file with semicolon as the separator
        	    writer.append("Recipe ID,Title,Ingredients\n");

        	    // Iterate through the entries in the recipeDetailsMap
        	    for (Map.Entry<Long, RecipeDetailsDTO> entry : recipeDetailsMap.entrySet()) {
        	        RecipeDetailsDTO recipeDetails = entry.getValue();

        	        if (recipeDetails != null) {
        	            Long recipeID = recipeDetails.getId();
        	            String title = recipeDetails.getTitle();
        	            List<ExtendedIngredientDTO> extendedIngredients = recipeDetails.getExtendedIngredients();

        	            List<String> ingredientsList = new ArrayList<>();

        	            if (extendedIngredients != null) {
        	                for (ExtendedIngredientDTO ingredient : extendedIngredients) {
        	                    String ingredientName = ingredient.getNameClean();
        	                    // If the ingredient contains a comma, enclose it in double quotes
        	                    if (ingredientName != null && ingredientName.contains(",")) {
        	                        ingredientName = "\"" + ingredientName + "\"";
        	                    }
        	                    ingredientsList.add(ingredientName);
        	                }
        	            }

        	            String ingredients = String.join(" | ", ingredientsList);

        	            // If the title or ingredients contain a comma, enclose them in double quotes
        	            if (title.contains(",") || ingredients.contains(",")) {
        	                title = "\"" + title + "\"";
        	                ingredients = "\"" + ingredients + "\"";
        	            }

        	            // Write the row to the CSV file with comma as the column separator
        	            writer.append(recipeID.toString()).append(",").append(title).append(",").append(ingredients).append("\n");
        	        }
        	   }
        	}



 catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error while writing the CSV file: " + e.getMessage());
            }
        }
    }

	

	//This function retrieves the ingredients for a specific recipe ID
	private List<String> getIngredientsForRecipe(String recipeID) {
		// Assuming you have a method or API to retrieve ingredients for a recipe
		// Replace this with your actual implementation
		List<String> ingredients = new ArrayList<>();
		// Logic to fetch ingredients for the recipe by its ID
		// Add ingredients to the 'ingredients' list
		return ingredients;
	}
}