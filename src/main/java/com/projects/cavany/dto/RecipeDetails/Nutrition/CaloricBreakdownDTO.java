package com.projects.cavany.dto.RecipeDetails.Nutrition;

public class CaloricBreakdownDTO {
    private double percentProtein;
    private double percentFat;
    private double percentCarbs;
    
    // Getters and setters
	public double getPercentProtein() {
		return percentProtein;
	}
	public void setPercentProtein(double percentProtein) {
		this.percentProtein = percentProtein;
	}
	public double getPercentFat() {
		return percentFat;
	}
	public void setPercentFat(double percentFat) {
		this.percentFat = percentFat;
	}
	public double getPercentCarbs() {
		return percentCarbs;
	}
	public void setPercentCarbs(double percentCarbs) {
		this.percentCarbs = percentCarbs;
	}
    
}
