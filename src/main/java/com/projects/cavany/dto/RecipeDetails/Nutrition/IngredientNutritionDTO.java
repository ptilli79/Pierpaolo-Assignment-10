package com.projects.cavany.dto.RecipeDetails.Nutrition;

import java.util.List;

public class IngredientNutritionDTO {
    private Long id;
    private String name;
    private double amount;
    private String unit;
    private List<NutrientDTO> nutrients;
    
    // Getters and setters for all fields
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public List<NutrientDTO> getNutrients() {
		return nutrients;
	}
	public void setNutrients(List<NutrientDTO> nutrients) {
		this.nutrients = nutrients;
	}
}
