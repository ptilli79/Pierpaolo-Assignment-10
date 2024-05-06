package com.projects.cavany.domain.RecipeDetails.Nutrition;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public class CaloricBreakdown {
    @Id @GeneratedValue private Long id;
    private double percentProtein;
    private double percentFat;
    private double percentCarbs;
    
    // Getters and setters
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
