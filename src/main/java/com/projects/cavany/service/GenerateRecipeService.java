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
    		    "OPTIONAL MATCH (recipe)-[:HAS_INGREDIENTS_COLLECTION]->(ingredientCollection:ExtendedIngredientsCollection)-[:HAS_INGREDIENT]->(ingredient:ExtendedIngredient) " +
    		    "WITH recipe, " +
    		    "COLLECT(DISTINCT ingredient.name) AS rawIngredients, " +
    		    "COLLECT(DISTINCT ingredient.nameClean) AS cleanIngredients " +
    		    "RETURN recipe.Id AS recipeId, recipe.title AS recipeTitle, " +
    		    "cleanIngredients + rawIngredients AS recipeIngredients " +
    		    "ORDER BY recipe.Id ASC";

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