package com.projects.cavany.domain.RecipeDetails.Nutrition;

import java.util.List;
import java.util.UUID;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.projects.cavany.domain.RecipeDetails.RecipeDetails;

@Node
public class Nutrition {
    @Id 
    private Long id;
    @Relationship(type = "HAS_NUTRIENT", direction = Relationship.Direction.OUTGOING)
    private List<NutrientEntity> nutrients;
    @Relationship(type = "HAS_PROPERTY", direction = Relationship.Direction.OUTGOING)
    private List<NutritionProperty> properties;
    @Relationship(type = "HAS_INGREDIENT_NUTRITION", direction = Relationship.Direction.OUTGOING)
    private List<IngredientNutrition> ingredients;
    @Relationship(type = "HAS_CALORIC_BREAKDOWN", direction = Relationship.Direction.OUTGOING)
    private CaloricBreakdown caloricBreakdown;
    @Relationship(type = "HAS_WEIGHT_PER_SERVING", direction = Relationship.Direction.OUTGOING)
    private WeightPerServing weightPerServing;
    
    
    // Constructor that takes a RecipeDetails object and assigns its id to Nutrition's recipeId
    public Nutrition(Long id) {
        this.id = id;  // Assuming RecipeDetails has an getId() method returning Long
    }
    
    // Getters and setters
//	public UUID getUuid() {
//		return uuid;
//	}
//	public void setUuid(UUID uuid) {
//		this.uuid = uuid;
//	}
    
	public Long getId() {
		return id;
	}
	//public void setId(Long id) {
	//	this.id = id;
	//}
	public List<NutrientEntity> getNutrients() {
		return nutrients;
	}
	public void setNutrients(List<NutrientEntity> nutrients) {
		this.nutrients = nutrients;
	}
	public List<NutritionProperty> getProperties() {
		return properties;
	}
	public void setProperties(List<NutritionProperty> properties) {
		this.properties = properties;
	}
	public List<IngredientNutrition> getIngredients() {
		return ingredients;
	}
	public void setIngredients(List<IngredientNutrition> ingredients) {
		this.ingredients = ingredients;
	}
	public CaloricBreakdown getCaloricBreakdown() {
		return caloricBreakdown;
	}
	public void setCaloricBreakdown(CaloricBreakdown caloricBreakdown) {
		this.caloricBreakdown = caloricBreakdown;
	}
	public WeightPerServing getWeightPerServing() {
		return weightPerServing;
	}
	public void setWeightPerServing(WeightPerServing weightPerServing) {
		this.weightPerServing = weightPerServing;
	}
}
