package com.projects.cavany.dto;

import java.util.List;

public class RecipeWithIngredientsDTOFromEntity {
	
    private Long recipeId;
    private String recipeTitle;
    private List<String> recipeIngredients;
    
    
    // Constructor
    public RecipeWithIngredientsDTOFromEntity(Long recipeId, String recipeTitle, List<String> recipeIngredients) {
        this.recipeId = recipeId;
        this.recipeTitle = recipeTitle;
        this.recipeIngredients = recipeIngredients;
    }
	public Long getRecipeId() {
		return recipeId;
	}
	public void setRecipeId(Long recipeId) {
		this.recipeId = recipeId;
	}
	public String getRecipeTitle() {
		return recipeTitle;
	}
	public void setRecipeTitle(String recipeTitle) {
		this.recipeTitle = recipeTitle;
	}
	public List<String> getRecipeIngredients() {
		return recipeIngredients;
	}
	public void setRecipeIngredients(List<String> recipeIngredients) {
		this.recipeIngredients = recipeIngredients;
	}
    
    

}
