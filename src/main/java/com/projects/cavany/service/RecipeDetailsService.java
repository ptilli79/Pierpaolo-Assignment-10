package com.projects.cavany.service;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


import org.neo4j.driver.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projects.cavany.domain.RecipeDetails.ExtendedIngredient;
import com.projects.cavany.domain.RecipeDetails.RecipeDetails;
import com.projects.cavany.domain.RecipeDetails.WinePairing;
import com.projects.cavany.domain.RecipeDetails.AnalyzedInstruction.AnalyzedInstruction;
import com.projects.cavany.domain.RecipeDetails.Nutrition.CaloricBreakdown;
import com.projects.cavany.domain.RecipeDetails.Nutrition.IngredientNutrition;
import com.projects.cavany.domain.RecipeDetails.Nutrition.NutrientEntity;
import com.projects.cavany.domain.RecipeDetails.Nutrition.Nutrition;
import com.projects.cavany.domain.RecipeDetails.Nutrition.NutritionProperty;
import com.projects.cavany.domain.RecipeDetails.Nutrition.WeightPerServing;
import com.projects.cavany.dto.ComplexSearch.ComplexSearchResultItemDTO;
import com.projects.cavany.dto.ComplexSearch.ComplexSearchResultsDTO;
import com.projects.cavany.dto.RecipeDetails.ExtendedIngredientDTO;
import com.projects.cavany.dto.RecipeDetails.RecipeDetailsDTO;
import com.projects.cavany.dto.RecipeDetails.RecipeWithIngredientsDTOFromEntity;
import com.projects.cavany.repository.AnalyzedInstructionRepositoryNeo4j;
import com.projects.cavany.repository.ExtendedIngredientRepositoryNeo4j;
import com.projects.cavany.repository.NutritionRepositoryNeo4j;
import com.projects.cavany.repository.RecipeDetailsRepository;
import com.projects.cavany.repository.RecipeDetailsRepositoryNeo4j;
import com.projects.cavany.repository.WinePairingRepositoryNeo4j;

@Service
public class RecipeDetailsService {
	
	@Autowired
	RecipeDetailsRepositoryNeo4j recipeDetailsRepository;
    @Autowired
    private ExtendedIngredientRepositoryNeo4j extendedIngredientRepository;
    @Autowired
    private WinePairingRepositoryNeo4j winePairingRepository;
    @Autowired
    private AnalyzedInstructionRepositoryNeo4j analyzedInstructionRepository;
    @Autowired
    private NutritionRepositoryNeo4j nutritionRepository;
    

    @Autowired
    private Neo4jClient neo4jClient;

    // Constructor injection of Neo4jClient
    public RecipeDetailsService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }
      

    @Autowired
    public RecipeDetailsService(RecipeDetailsRepositoryNeo4j recipeDetailsRepository, ExtendedIngredientRepositoryNeo4j extendedIngredientRepository,
                         WinePairingRepositoryNeo4j winePairingRepository, AnalyzedInstructionRepositoryNeo4j analyzedInstructionRepository,
                         NutritionRepositoryNeo4j nutritionRepository) {
        this.recipeDetailsRepository = recipeDetailsRepository;
        this.extendedIngredientRepository = extendedIngredientRepository;
        this.winePairingRepository = winePairingRepository;
        this.analyzedInstructionRepository = analyzedInstructionRepository;
        this.nutritionRepository = nutritionRepository;
    }

    
    @Transactional
    public void saveOrUpdateRecipeDetails(RecipeDetails recipeDetails) {
        Optional<RecipeDetails> existingRecipe = recipeDetailsRepository.findRecipeById(recipeDetails.getId());
        if (existingRecipe.isPresent()) {
            updateExistingRecipe(existingRecipe.get(), recipeDetails);
        } else {
            // Save new recipe
            recipeDetailsRepository.save(recipeDetails);
        }
    }

    private void updateExistingRecipe(RecipeDetails existingNeo4j, RecipeDetails newFetch) {
    	// Check and update ingredients
    	saveWithIngredients(existingNeo4j,newFetch.getExtendedIngredients());
        updateWinePairing(existingNeo4j, newFetch.getWinePairing());
        updateAnalyzedInstructions(existingNeo4j, newFetch.getAnalyzedInstructions());
        updateNutrition(existingNeo4j, newFetch.getNutrition());

        // Save the updated recipe details
        recipeDetailsRepository.save(existingNeo4j);
    }

    @Transactional
    public RecipeDetails saveWithIngredients(RecipeDetails existingRecipe, List<ExtendedIngredient> newIngredients) {
        if (newIngredients != null && existingRecipe.getId() != null) {
            // Delete existing ingredients
            extendedIngredientRepository.deleteIngredientsByRecipeId(existingRecipe.getId());

            // Set new ingredients with UUIDs
            existingRecipe.setExtendedIngredients(newIngredients.stream().map(ingredient -> {
                if (ingredient.getUuid() == null) {
                    ingredient.setUuid(UUID.randomUUID());
                }
                return ingredient;
            }).collect(Collectors.toList()));

            // Save the recipe with new ingredients
            return recipeDetailsRepository.save(existingRecipe);
        }
        return existingRecipe;
    }

    private void updateWinePairing(RecipeDetails recipe, WinePairing newWinePairing) {
        if (newWinePairing != null && recipe.getId() != null) {
            winePairingRepository.deleteWinePairingByRecipeId(recipe.getId());
            // Assuming the merge method ensures correct properties setting
            recipe.setWinePairing(winePairingRepository.save(newWinePairing));
        }
    }

    private void updateAnalyzedInstructions(RecipeDetails recipe, List<AnalyzedInstruction> newInstructions) {
        if (newInstructions != null && recipe.getId() != null) {
            analyzedInstructionRepository.deleteInstructionsByRecipeId(recipe.getId());
            recipe.setAnalyzedInstructions(newInstructions.stream()
                .map(analyzedInstructionRepository::save)
                .collect(Collectors.toList()));
        }
    }

    private void updateNutrition(RecipeDetails recipe, Nutrition newNutrition) {
        if (newNutrition != null && recipe.getId() != null) {
            nutritionRepository.deleteNutritionByRecipeId(recipe.getId());
            recipe.setNutrition(nutritionRepository.save(newNutrition));
        }
    }
    
    public List<RecipeWithIngredientsDTOFromEntity> findLimitedRecipesWithIngredients(List<Long> recipeIds, List<String> dishTypes) {
        String cypherQuery = 
            "MATCH (recipe:RecipeDetails)-[:HAS_INGREDIENT]->(ingredient:ExtendedIngredient) " +
            "WHERE recipe.Id IN $recipeIds AND any(dishType IN $dishTypes WHERE dishType IN recipe.dishTypes) " +
            "OPTIONAL MATCH (recipe)-[:HAS_PREPARATION_INSTRUCTIONS]->(:AnalyzedInstruction)-[:HAS_STEPS]->(:Step)-[:HAS_INGREDIENTS]->(stepIngredient:Ingredient) " +
            "WITH recipe, " +
            "COLLECT(DISTINCT ingredient.name) AS rawIngredients, " +
            "COLLECT(DISTINCT ingredient.nameClean) AS cleanIngredients, " +
            "COLLECT(DISTINCT stepIngredient.name) AS stepRawIngredients, " +
            "COLLECT(DISTINCT stepIngredient.nameClean) AS stepCleanIngredients " +
            "RETURN recipe.Id AS recipeId, recipe.title AS recipeTitle, " +
            "cleanIngredients + rawIngredients + stepRawIngredients + stepCleanIngredients AS recipeIngredients " +
            "ORDER BY recipe.Id ASC";

        // Assuming neo4jClient is correctly set up to interact with your Neo4j database
        return neo4jClient.query(cypherQuery)
                .bindAll(Map.of("recipeIds", recipeIds, "dishTypes", dishTypes)) // Bind both recipeIds and dishTypes to the query
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