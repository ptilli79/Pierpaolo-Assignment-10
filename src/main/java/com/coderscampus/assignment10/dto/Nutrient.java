package com.coderscampus.assignment10.dto;

public class Nutrient {
	
    private Long carbohydrates;
    private Long protein;
    private Long fat;
    private Long calories;
    
	@Override
	public String toString() {
		return " Nutrient [carbohydrates=" + carbohydrates + ", protein=" + protein + ", fat=" + fat + ", calories="
				+ calories + "]";
	}
	public Long getCarbohydrates() {
		return carbohydrates;
	}
	public void setCarbohydrates(Long carbohydrates) {
		this.carbohydrates = carbohydrates;
	}
	public Long getProtein() {
		return protein;
	}
	public void setProtein(Long protein) {
		this.protein = protein;
	}
	public Long getFat() {
		return fat;
	}
	public void setFat(Long fat) {
		this.fat = fat;
	}
	public Long getCalories() {
		return calories;
	}
	public void setCalories(Long calories) {
		this.calories = calories;
	}

}
