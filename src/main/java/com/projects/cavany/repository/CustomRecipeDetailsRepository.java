package com.projects.cavany.repository;

import java.util.List;

import com.projects.cavany.domain.RecipeDetails.RecipeDetails;

public interface CustomRecipeDetailsRepository {
	void saveRecipeDetailsInBatch(List<RecipeDetails> recipeDetailsList);

}


