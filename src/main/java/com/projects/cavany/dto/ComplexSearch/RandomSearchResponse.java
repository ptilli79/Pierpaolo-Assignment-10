package com.projects.cavany.dto.ComplexSearch;

import java.util.List;

import com.projects.cavany.dto.RecipeDetails.RecipeDetailsDTO;

public class RandomSearchResponse {
    private List<RecipeDetailsDTO> recipes;

    public List<RecipeDetailsDTO> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<RecipeDetailsDTO> recipes) {
        this.recipes = recipes;
    }
}
