package com.projects.cavany.repository;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import com.projects.cavany.dto.MealPlanner.WeeklyPlannerResponse;
import com.projects.cavany.dto.RecipeDetails.RecipeDetailsDTO;


@Repository
public class RecipeDetailsRepository {
    private Map<Long, RecipeDetailsDTO> recipeDetails = new HashMap<>();
    private Long index = 0L;

    public RecipeDetailsDTO save(RecipeDetailsDTO genericRecipeDetails) {
        Long recipeId = genericRecipeDetails.getId();

        if (recipeDetails.containsKey(recipeId)) {
            // Recipe with the same ID already exists, update it
            recipeDetails.put(recipeId, genericRecipeDetails);
        } else {
            // Recipe with the given ID doesn't exist, add it
            recipeDetails.put(recipeId, genericRecipeDetails);
            index++;
        }
        
        return recipeDetails.get(recipeId);
    }


    public RecipeDetailsDTO getRecipeById(Long recipeId) {
        return recipeDetails.get(recipeId);
    }

    public Map<Long, RecipeDetailsDTO> getAll() {
        return recipeDetails;
    }

    public List<RecipeDetailsDTO> saveAll(List<RecipeDetailsDTO> recipeList) {
        List<RecipeDetailsDTO> savedRecipes = new ArrayList<>();

        for (RecipeDetailsDTO genericRecipeDetails : recipeList) {
            Long recipeId = genericRecipeDetails.getId();
            boolean recipeExists = recipeDetails.containsKey(recipeId);

            if (recipeExists) {
                // Recipe with the same ID already exists, update it
                recipeDetails.put(recipeId, genericRecipeDetails);
                savedRecipes.add(genericRecipeDetails);
            } else {
                // Recipe with the given ID doesn't exist, add it
                recipeDetails.put(recipeId, genericRecipeDetails);
                savedRecipes.add(genericRecipeDetails);
                index++;
            }
        }

        return savedRecipes;
    }

    @Override
    public String toString() {
        return "RecipeDetailsRepository [recipeDetails=" + recipeDetails + "]";
    }
}
