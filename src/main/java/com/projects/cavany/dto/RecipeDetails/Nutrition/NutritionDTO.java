package com.projects.cavany.dto.RecipeDetails.Nutrition;

import java.util.List;

public class NutritionDTO {
    private List<NutrientDTO> nutrients;
    private List<NutritionPropertyDTO> properties;
    //private List<FlavonoidDTO> flavonoids;
    private List<IngredientNutritionDTO> ingredients;
    private CaloricBreakdownDTO caloricBreakdown;
    private WeightPerServingDTO weightPerServing;
    //Getters and Setters
	public List<NutrientDTO> getNutrients() {
		return nutrients;
	}
	public void setNutrients(List<NutrientDTO> nutrients) {
		this.nutrients = nutrients;
	}
	public List<NutritionPropertyDTO> getProperties() {
		return properties;
	}
	public void setProperties(List<NutritionPropertyDTO> properties) {
		this.properties = properties;
	}
	public List<IngredientNutritionDTO> getIngredients() {
		return ingredients;
	}
	public void setIngredients(List<IngredientNutritionDTO> ingredients) {
		this.ingredients = ingredients;
	}
	public CaloricBreakdownDTO getCaloricBreakdown() {
		return caloricBreakdown;
	}
	public void setCaloricBreakdown(CaloricBreakdownDTO caloricBreakdown) {
		this.caloricBreakdown = caloricBreakdown;
	}
	public WeightPerServingDTO getWeightPerServing() {
		return weightPerServing;
	}
	public void setWeightPerServing(WeightPerServingDTO weightPerServing) {
		this.weightPerServing = weightPerServing;
	}
    

}
