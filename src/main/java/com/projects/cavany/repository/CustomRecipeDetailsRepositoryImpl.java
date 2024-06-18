package com.projects.cavany.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Repository;

import com.projects.cavany.domain.RecipeDetails.ExtendedIngredient;
import com.projects.cavany.domain.RecipeDetails.MetricSystem;
import com.projects.cavany.domain.RecipeDetails.RecipeDetails;

import jakarta.transaction.Transactional;

@Repository
public class CustomRecipeDetailsRepositoryImpl implements CustomRecipeDetailsRepository {

    @Autowired
    private Neo4jTemplate neo4jTemplate;

    @Transactional
    @Override
    public void saveRecipeDetailsInBatch(List<RecipeDetails> recipeDetailsList) {
        neo4jTemplate.saveAll(recipeDetailsList);
    }


/*
    @Repository
    public class CustomRecipeDetailsRepositoryImpl implements CustomRecipeDetailsRepository {

        private final Neo4jClient neo4jClient;

        @Autowired
        public CustomRecipeDetailsRepositoryImpl(Neo4jClient neo4jClient) {
            this.neo4jClient = neo4jClient;
        }

        @Override
        @Transactional
        public void saveRecipeDetailsInBatch(List<RecipeDetails> recipeDetailsList) {
            for (RecipeDetails recipe : recipeDetailsList) {
                // Convert recipe to map
                Map<String, Object> recipeMap = convertRecipeToMap(recipe);

                // Convert ingredients to maps
                List<Map<String, Object>> ingredientsAsMaps = recipe.getExtendedIngredients().stream()
                        .map(this::convertIngredientToMap)
                        .distinct()
                        .collect(Collectors.toList());

                // Save RecipeDetails node
                String recipeQuery = "MERGE (r:RecipeDetails {Id: $recipeId}) " +
                        "SET r += $recipeProperties";

                Map<String, Object> recipeParams = new HashMap<>();
                recipeParams.put("recipeId", recipe.getId());
                recipeParams.put("recipeProperties", recipeMap);

                neo4jClient.query(recipeQuery).bindAll(recipeParams).run();

                // Save ExtendedIngredient nodes and create relationships
                for (Map<String, Object> ingredientMap : ingredientsAsMaps) {
                    String ingredientQuery = "MATCH (r:RecipeDetails {Id: $recipeId}) " +
                            "MERGE (i:ExtendedIngredient {id: $ingredientId}) " +
                            "SET i.aisle = $aisle, " +
                            "    i.image = $image, " +
                            "    i.consistency = $consistency, " +
                            "    i.name = $name, " +
                            "    i.nameClean = $nameClean, " +
                            "    i.original = $original, " +
                            "    i.originalName = $originalName, " +
                            "    i.amount = $amount, " +
                            "    i.unit = $unit, " +
                            "    i.meta = $meta " +
                            "MERGE (r)-[:HAS_INGREDIENT]->(i)";

                    Map<String, Object> ingredientParams = new HashMap<>();
                    ingredientParams.put("recipeId", recipe.getId());
                    ingredientParams.put("ingredientId", ingredientMap.get("id"));
                    ingredientParams.put("aisle", ingredientMap.get("aisle"));
                    ingredientParams.put("image", ingredientMap.get("image"));
                    ingredientParams.put("consistency", ingredientMap.get("consistency"));
                    ingredientParams.put("name", ingredientMap.get("name"));
                    ingredientParams.put("nameClean", ingredientMap.get("nameClean"));
                    ingredientParams.put("original", ingredientMap.get("original"));
                    ingredientParams.put("originalName", ingredientMap.get("originalName"));
                    ingredientParams.put("amount", ingredientMap.get("amount"));
                    ingredientParams.put("unit", ingredientMap.get("unit"));
                    ingredientParams.put("meta", ingredientMap.get("meta"));

                    neo4jClient.query(ingredientQuery).bindAll(ingredientParams).run();
                }
            }

            System.out.println("Completed batch save operation.");
    }
  */      
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
    
    private Map<String, Object> flattenIngredientMap(Map<String, Object> ingredientMap) {
        Map<String, Object> flattenedMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : ingredientMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof List) {
                flattenedMap.put(key, ((List<?>) value).toArray());
            } else {
                flattenedMap.put(key, value);
            }
        }
        return flattenedMap;
    }
    
}

