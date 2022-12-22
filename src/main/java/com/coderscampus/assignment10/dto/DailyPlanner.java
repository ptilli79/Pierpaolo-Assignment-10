package com.coderscampus.assignment10.dto;

import java.util.List;

public class DailyPlanner {
	
	private List<Meal> meals;
	private Nutrient nutrients;
	
	public List<Meal> getMeals() {
		return meals;
	}
	public void setMeals(List<Meal> meals) {
		this.meals = meals;
	}
	public Nutrient getNutrients() {
		return nutrients;
	}
	public void setNutrients(Nutrient nutrients) {
		this.nutrients = nutrients;
	}
	@Override
	public String toString() {
		return "DailyPlanner [meals=" + meals + ", nutrients=" + nutrients + "]\n";
	}
	

}
