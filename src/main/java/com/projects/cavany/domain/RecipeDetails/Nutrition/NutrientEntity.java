package com.projects.cavany.domain.RecipeDetails.Nutrition;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public class NutrientEntity {
    @Id @GeneratedValue private Long id;
    private String name;
    private double amount;
    private String unit;
    private double percentOfDailyNeeds;
    
    // Getters and setters
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
	public double getPercentOfDailyNeeds() {
		return percentOfDailyNeeds;
	}
	public void setPercentOfDailyNeeds(double percentOfDailyNeeds) {
		this.percentOfDailyNeeds = percentOfDailyNeeds;
	}
    
}
