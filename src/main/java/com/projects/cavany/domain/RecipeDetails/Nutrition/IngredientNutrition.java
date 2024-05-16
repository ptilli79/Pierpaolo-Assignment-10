package com.projects.cavany.domain.RecipeDetails.Nutrition;

import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class IngredientNutrition {
    @Id @GeneratedValue private Long id;
    private Long ingredientId;
    private String name;
    private double amount;
    private String unit;
    
    //@Relationship(type = "HAS_NUTRIENT", direction = Relationship.Direction.OUTGOING)
    //private List<NutrientEntity> nutrients;
    
    // Getters and setters
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getIngredientId() {
		return ingredientId;
	}
	public void setIngredientId(Long ingredientId) {
		this.ingredientId = ingredientId;
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
	//public List<NutrientEntity> getNutrients() {
	//	return nutrients;
	//}
	//public void setNutrients(List<NutrientEntity> nutrients) {
	//	this.nutrients = nutrients;
	//}  
}
