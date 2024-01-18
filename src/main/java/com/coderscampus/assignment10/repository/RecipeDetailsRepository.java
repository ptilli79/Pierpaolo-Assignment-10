package com.coderscampus.assignment10.repository;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

import com.coderscampus.assignment10.dto.RecipeDetailsDTO;
import com.coderscampus.assignment10.dto.WeeklyPlannerResponse;


@Repository
public class RecipeDetailsRepository {
	private Map<Long, RecipeDetailsDTO> recipeDetails = new HashMap<Long, RecipeDetailsDTO>();
	private Long index = 0L;
	
	public RecipeDetailsDTO save(RecipeDetailsDTO genericRecipeDetails) {
	    for (Map.Entry<Long, RecipeDetailsDTO> entry : recipeDetails.entrySet()) {
	        if (entry.getValue().getId() == genericRecipeDetails.getId()) {
	            // Recipe with the same ID already exists, update it
	            entry.setValue(genericRecipeDetails);
	            return entry.getValue();
	        }
	    }

	    // Recipe with the given ID doesn't exist, add it
	    recipeDetails.put(index, genericRecipeDetails);
	    index++;
	    return getRecipeById(index - 1);
	}
	
	public RecipeDetailsDTO getRecipeById (Long weeklyMealPlanId) {
		return recipeDetails.get(weeklyMealPlanId);
	}
	
	public Map<Long, RecipeDetailsDTO> getAll () {
		return recipeDetails;
	}

	public List<RecipeDetailsDTO> saveAll(List<RecipeDetailsDTO> recipeList) {
	    List<RecipeDetailsDTO> savedRecipes = new ArrayList<>();

	    for (RecipeDetailsDTO genericRecipeDetails : recipeList) {
	        boolean recipeExists = false;
	        for (Map.Entry<Long, RecipeDetailsDTO> entry : recipeDetails.entrySet()) {
	            if (entry.getValue().getId() == genericRecipeDetails.getId()) {
	                // Recipe with the same ID already exists, update it
	                entry.setValue(genericRecipeDetails);
	                savedRecipes.add(entry.getValue());
	                recipeExists = true;
	                break;
	            }
	        }

	        if (!recipeExists) {
	            // Recipe with the given ID doesn't exist, add it
	            recipeDetails.put(index, genericRecipeDetails);
	            savedRecipes.add(genericRecipeDetails);
	            index++;
	        }
	    }

	    return savedRecipes;
	}

	
	@Override
	public String toString() {
		return "RecipeInformationRepository [recipeDetails=" + recipeDetails + "]";
	}
	
	
}
