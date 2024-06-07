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
    private Neo4jTemplate neo4jTemplate;
    @Autowired
    private Neo4jClient neo4jClient;

    // Constructor injection of Neo4jClient
    public RecipeDetailsService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }
      

    @Autowired
    public RecipeDetailsService(Neo4jTemplate neo4jTemplate, RecipeDetailsRepositoryNeo4j recipeDetailsRepository,
                                ExtendedIngredientRepositoryNeo4j extendedIngredientRepository, WinePairingRepositoryNeo4j winePairingRepository,
                                AnalyzedInstructionRepositoryNeo4j analyzedInstructionRepository, NutritionRepositoryNeo4j nutritionRepository) {
        this.neo4jTemplate = neo4jTemplate;
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
    private void updateExistingRecipe(RecipeDetails existingNeo4j, RecipeDetails newFetch) {
    	// Check and update ingredients
    	saveWithIngredients(existingNeo4j,newFetch.getExtendedIngredients());
        updateWinePairing(existingNeo4j, newFetch.getWinePairing());
        //updateAnalyzedInstructions(existingNeo4j, newFetch.getAnalyzedInstructions());
        //updateNutrition(existingNeo4j, newFetch.getNutrition());

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


    @Transactional
    public void saveRecipeDetailsInBatch(List<RecipeDetails> recipeDetailsList) {
        // Save RecipeDetails nodes
        List<Map<String, Object>> recipesAsMaps = recipeDetailsList.stream()
            .map(this::convertRecipeToMap)
            .collect(Collectors.toList());

        String recipeQuery = "UNWIND $recipeDetails AS recipe " +
                             "MERGE (r:RecipeDetails {Id: recipe.Id}) " +
                             "SET r += recipe";

        Map<String, Object> recipeParams = new HashMap<>();
        recipeParams.put("recipeDetails", recipesAsMaps);

        neo4jClient.query(recipeQuery).bindAll(recipeParams).run();

        // Save ExtendedIngredient nodes
        List<Map<String, Object>> ingredientsAsMaps = recipeDetailsList.stream()
            .flatMap(recipe -> recipe.getExtendedIngredients().stream())
            .map(this::convertIngredientToMap)
            .collect(Collectors.toList());

        String ingredientQuery = "UNWIND $ingredients AS ingredient " +
                                 "MERGE (i:ExtendedIngredient {id: ingredient.id}) " +
                                 "SET i += ingredient";

        Map<String, Object> ingredientParams = new HashMap<>();
        ingredientParams.put("ingredients", ingredientsAsMaps);

        neo4jClient.query(ingredientQuery).bindAll(ingredientParams).run();

        // Save WinePairing nodes
        List<Map<String, Object>> winePairingsAsMaps = recipeDetailsList.stream()
            .map(RecipeDetails::getWinePairing)
            .filter(Objects::nonNull)
            .map(this::convertWinePairingToMap)
            .filter(map -> map.get("Id") != null)  // Ensure WinePairing Id is not null
            .collect(Collectors.toList());

        String winePairingQuery = "UNWIND $winePairings AS winePairing " +
                                  "MERGE (wp:WinePairing {Id: winePairing.Id}) " +
                                  "SET wp += winePairing";

        Map<String, Object> winePairingParams = new HashMap<>();
        winePairingParams.put("winePairings", winePairingsAsMaps);

        neo4jClient.query(winePairingQuery).bindAll(winePairingParams).run();

        // Save ProductMatch nodes
        List<Map<String, Object>> productMatchesAsMaps = recipeDetailsList.stream()
            .map(RecipeDetails::getWinePairing)
            .filter(Objects::nonNull)
            .flatMap(winePairing -> winePairing.getProductMatches().stream())
            .map(this::convertProductMatchToMap)
            .filter(map -> map.get("id") != null)  // Ensure ProductMatch id is not null
            .collect(Collectors.toList());

        String productMatchQuery = "UNWIND $productMatches AS productMatch " +
                                   "MERGE (pm:ProductMatch {id: productMatch.id}) " +
                                   "ON CREATE SET pm.uuid = productMatch.uuid " +
                                   "SET pm += productMatch";

        Map<String, Object> productMatchParams = new HashMap<>();
        productMatchParams.put("productMatches", productMatchesAsMaps);

        neo4jClient.query(productMatchQuery).bindAll(productMatchParams).run();

        // Create relationships between WinePairing and ProductMatch
        String relationshipQuery = "UNWIND $winePairings AS winePairing " +
                                   "MATCH (wp:WinePairing {Id: winePairing.Id}) " +
                                   "UNWIND winePairing.productMatches AS productMatch " +
                                   "MATCH (pm:ProductMatch {id: productMatch.id}) " +
                                   "MERGE (wp)-[:HAS_PRODUCT_MATCH]->(pm)";

        Map<String, Object> relationshipParams = new HashMap<>();
        relationshipParams.put("winePairings", winePairingsAsMaps);

        neo4jClient.query(relationshipQuery).bindAll(relationshipParams).run();

        // Save Measures nodes
        List<Map<String, Object>> measuresAsMaps = recipeDetailsList.stream()
            .flatMap(recipe -> recipe.getExtendedIngredients().stream())
            .filter(ingredient -> ingredient.getUs() != null || ingredient.getMetric() != null)
            .flatMap(ingredient -> {
                List<MetricSystem> measures = new ArrayList<>();
                if (ingredient.getUs() != null) {
                    measures.add(ingredient.getUs());
                }
                if (ingredient.getMetric() != null) {
                    measures.add(ingredient.getMetric());
                }
                return measures.stream();
            })
            .map(this::convertMetricSystemToMap)
            .collect(Collectors.toList());

        String measuresQuery = "UNWIND $measures AS measure " +
                               "MERGE (m:Measures {uuid: measure.uuid}) " +
                               "SET m += measure";

        Map<String, Object> measuresParams = new HashMap<>();
        measuresParams.put("measures", measuresAsMaps);

        neo4jClient.query(measuresQuery).bindAll(measuresParams).run();

        // Create relationships between ExtendedIngredient and Measures
        String ingredientMeasuresRelationshipQuery = "UNWIND $measures AS measure " +
                                                     "MATCH (i:ExtendedIngredient {id: measure.ingredientId}) " +
                                                     "MATCH (m:Measures {uuid: measure.uuid}) " +
                                                     "MERGE (i)-[:HAS_MEASURES]->(m)";

        neo4jClient.query(ingredientMeasuresRelationshipQuery).bindAll(measuresParams).run();
    }

    private Map<String, Object> convertRecipeToMap(RecipeDetails recipeDetails) {
        Map<String, Object> map = new HashMap<>();
        map.put("Id", recipeDetails.getId());
        map.put("title", recipeDetails.getTitle());
        map.put("vegetarian", recipeDetails.isVegetarian());
        map.put("vegan", recipeDetails.isVegan());
        map.put("glutenFree", recipeDetails.isGlutenFree());
        map.put("dairyFree", recipeDetails.isDairyFree());
        map.put("veryHealthy", recipeDetails.isVeryHealthy());
        map.put("cheap", recipeDetails.isCheap());
        map.put("veryPopular", recipeDetails.isVeryPopular());
        map.put("sustainable", recipeDetails.isSustainable());
        map.put("lowFodmap", recipeDetails.isLowFodmap());
        map.put("weightWatcherSmartPoints", recipeDetails.getWeightWatcherSmartPoints());
        map.put("gaps", recipeDetails.getGaps());
        map.put("preparationMinutes", recipeDetails.getPreparationMinutes());
        map.put("cookingMinutes", recipeDetails.getCookingMinutes());
        map.put("aggregateLikes", recipeDetails.getAggregateLikes());
        map.put("healthScore", recipeDetails.getHealthScore());
        map.put("creditsText", recipeDetails.getCreditsText());
        map.put("sourceName", recipeDetails.getSourceName());
        map.put("pricePerServing", recipeDetails.getPricePerServing());
        map.put("readyInMinutes", recipeDetails.getReadyInMinutes());
        map.put("servings", recipeDetails.getServings());
        map.put("sourceUrl", recipeDetails.getSourceUrl());
        map.put("image", recipeDetails.getImage());
        map.put("imageType", recipeDetails.getImageType());
        map.put("summary", recipeDetails.getSummary());
        map.put("cuisines", recipeDetails.getCuisines().toArray(new String[0]));
        map.put("dishTypes", recipeDetails.getDishTypes().toArray(new String[0]));
        map.put("diets", recipeDetails.getDiets().toArray(new String[0]));
        map.put("occasions", recipeDetails.getOccasions().toArray(new String[0]));
        map.put("instructions", recipeDetails.getInstructions());
        map.put("originalId", recipeDetails.getOriginalId());
        map.put("spoonacularScore", recipeDetails.getSpoonacularScore());
        map.put("spoonacularSourceUrl", recipeDetails.getSpoonacularSourceUrl());
        return map;
    }

    private Map<String, Object> convertIngredientToMap(ExtendedIngredient ingredient) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", ingredient.getId());
        map.put("aisle", ingredient.getAisle());
        map.put("image", ingredient.getImage());
        map.put("consistency", ingredient.getConsistency());
        map.put("name", ingredient.getName());
        map.put("nameClean", ingredient.getNameClean());
        map.put("original", ingredient.getOriginal());
        map.put("originalName", ingredient.getOriginalName());
        map.put("amount", ingredient.getAmount());
        map.put("unit", ingredient.getUnit());
        map.put("meta", ingredient.getMeta().toArray(new String[0]));
        map.put("usUuid", ingredient.getUs() != null ? ingredient.getUs().getUuid().toString() : null);
        map.put("metricUuid", ingredient.getMetric() != null ? ingredient.getMetric().getUuid().toString() : null);
        return map;
    }

    private Map<String, Object> convertWinePairingToMap(WinePairing winePairing) {
        Map<String, Object> map = new HashMap<>();
        map.put("Id", winePairing.getId() != null ? winePairing.getId() : UUID.randomUUID().toString());
        map.put("pairedWines", winePairing.getPairedWines() != null ? winePairing.getPairedWines().toArray(new String[0]) : new String[0]);
        map.put("pairingText", winePairing.getPairingText());
        return map;
    }

    private Map<String, Object> convertProductMatchToMap(ProductMatch productMatch) {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", productMatch.getUuid() != null ? productMatch.getUuid().toString() : UUID.randomUUID().toString());
        map.put("id", productMatch.getId());
        map.put("title", productMatch.getTitle());
        map.put("description", productMatch.getDescription());
        map.put("price", productMatch.getPrice());
        map.put("imageUrl", productMatch.getImageUrl());
        map.put("averageRating", productMatch.getAverageRating());
        map.put("ratingCount", productMatch.getRatingCount());
        map.put("score", productMatch.getScore());
        map.put("link", productMatch.getLink());
        return map;
    }

    private Map<String, Object> convertMetricSystemToMap(MetricSystem metricSystem) {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", metricSystem.getUuid() != null ? metricSystem.getUuid().toString() : UUID.randomUUID().toString());
        map.put("amount", metricSystem.getAmount());
        map.put("unitShort", metricSystem.getUnitShort());
        map.put("unitLong", metricSystem.getUnitLong());
        map.put("ingredientId", metricSystem.getId()); // Assuming the MetricSystem entity has a reference to the ingredient's ID
        return map;
    }








    
    private void updateWinePairing(RecipeDetails recipe, WinePairing newWinePairing) {
        if (newWinePairing != null && recipe.getId() != null) {
            winePairingRepository.deleteWinePairingByRecipeId(recipe.getId());
            // Assuming the merge method ensures correct properties setting
            recipe.setWinePairing(winePairingRepository.save(newWinePairing));
        }
    }


    private Nutrition updateNutrition(Long recipeId, Nutrition newNutrition) {
        if (newNutrition != null && recipeId != null) {
            nutritionRepository.deleteNutritionByRecipeId(recipeId);
            return(nutritionRepository.save(newNutrition));
        }
        else {
        	return null;
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