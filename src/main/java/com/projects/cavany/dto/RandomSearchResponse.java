package com.projects.cavany.dto;

import java.util.List;

public class RandomSearchResponse {
    private List<RecipeDetailsDTO> recipes;

    public List<RecipeDetailsDTO> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<RecipeDetailsDTO> recipes) {
        this.recipes = recipes;
    }
}
