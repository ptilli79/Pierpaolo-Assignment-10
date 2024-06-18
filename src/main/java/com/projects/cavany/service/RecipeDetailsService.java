package com.projects.cavany.service;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import com.projects.cavany.domain.RecipeDetails.ExtendedIngredientsCollection;
import com.projects.cavany.domain.RecipeDetails.MetricSystem;
import com.projects.cavany.domain.RecipeDetails.ProductMatch;
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
import com.projects.cavany.repository.ExtendedIngredientsCollectionRepositoryNeo4j;
import com.projects.cavany.repository.NutritionRepositoryNeo4j;
import com.projects.cavany.repository.RecipeDetailsRepository;
import com.projects.cavany.repository.RecipeDetailsRepositoryNeo4j;
import com.projects.cavany.repository.WinePairingRepositoryNeo4j;

@Service
public class RecipeDetailsService {
	
	@Autowired
	RecipeDetailsRepositoryNeo4j recipeDetailsRepository;
    @Autowired
    private ExtendedIngredientsCollectionRepositoryNeo4j extendedIngredientsCollectionRepository;
    @Autowired
    private WinePairingRepositoryNeo4j winePairingRepository;
    @Autowired
    private AnalyzedInstructionRepositoryNeo4j analyzedInstructionRepository;
    @Autowired
    private NutritionRepositoryNeo4j nutritionRepository;
    

    
    @Autowired
    private Neo4jTemplate neo4jTemplate;
    @Autowired
    private Neo4jClient neo4jClient;

    // Constructor injection of Neo4jClient
    public RecipeDetailsService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }
      

    @Autowired
    public RecipeDetailsService(Neo4jTemplate neo4jTemplate, RecipeDetailsRepositoryNeo4j recipeDetailsRepository,
                                ExtendedIngredientsCollectionRepositoryNeo4j extendedIngredientsCollectionRepository, WinePairingRepositoryNeo4j winePairingRepository,
                                AnalyzedInstructionRepositoryNeo4j analyzedInstructionRepository, NutritionRepositoryNeo4j nutritionRepository) {
        this.neo4jTemplate = neo4jTemplate;
        this.recipeDetailsRepository = recipeDetailsRepository;
        this.extendedIngredientsCollectionRepository = extendedIngredientsCollectionRepository;
        this.winePairingRepository = winePairingRepository;
        this.analyzedInstructionRepository = analyzedInstructionRepository;
        this.nutritionRepository = nutritionRepository;
    }

 /*   
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
        
    @Transactional
    private void updateExistingRecipe(RecipeDetails existingNeo4j, RecipeDetails newFetch) {
    	// Check and update ingredients
    	//saveWithIngredients(existingNeo4j,newFetch.getExtendedIngredients());
        //updateWinePairing(existingNeo4j, newFetch.getWinePairing());
        //updateAnalyzedInstructions(existingNeo4j, newFetch.getAnalyzedInstructions());
        //updateNutrition(existingNeo4j, newFetch.getNutrition());

        // Save the updated recipe details
        recipeDetailsRepository.save(existingNeo4j);
    }

*/
    
    @Transactional
	public void saveOrUpdateNutrition(Nutrition nutrition) {
        Optional<Nutrition> existingNutrition = nutritionRepository.findNutritionById(nutrition.getId());
        if (existingNutrition.isPresent()) {
            updateNutrition(existingNutrition.get().getId(), nutrition);
        } else {
            // Save new recipe
            nutritionRepository.save(nutrition);
        }
		
	}

    public Optional<Long> findMaxId() {
        return extendedIngredientsCollectionRepository.findMaxId();
    }

    @Transactional
    public void saveOrUpdateExtendedIngredientsCollection(ExtendedIngredientsCollection extendedIngredientsCollection) {
        Optional<ExtendedIngredientsCollection> existingCollection = extendedIngredientsCollectionRepository.findById(extendedIngredientsCollection.getId());
        if (existingCollection.isPresent()) {
            // Update logic if needed
        } else {
        	extendedIngredientsCollectionRepository.save(extendedIngredientsCollection);
        }
    }

    @Transactional
    public void saveRecipeDetailsInBatch(List<RecipeDetails> recipeDetailsList) {
        recipeDetailsRepository.saveRecipeDetailsInBatch(recipeDetailsList);
    }
    
//    private void updateWinePairing(RecipeDetails recipe, WinePairing newWinePairing) {
//        if (newWinePairing != null && recipe.getId() != null) {
//            winePairingRepository.deleteWinePairingByRecipeId(recipe.getId());
//            // Assuming the merge method ensures correct properties setting
//            recipe.setWinePairing(winePairingRepository.save(newWinePairing));
//        }
//    }


    private Nutrition updateNutrition(Long recipeId, Nutrition newNutrition) {
        if (newNutrition != null && recipeId != null) {
            nutritionRepository.deleteNutritionByRecipeId(recipeId);
            return(nutritionRepository.save(newNutrition));
        }
        else {
        	return null;
        }
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