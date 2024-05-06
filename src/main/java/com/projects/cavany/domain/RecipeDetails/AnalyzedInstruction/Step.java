package com.projects.cavany.domain.RecipeDetails.AnalyzedInstruction;

import java.util.List;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class Step {
	@Id @GeneratedValue
	private Long Id;
	@Property
	private int number;
	@Property
    private String step;
	@Property
    private Length length;
	
	@Relationship(type = "HAS_INGREDIENTS", direction = Relationship.Direction.OUTGOING)
    private List<Ingredient> ingredients;
	
	@Relationship(type = "HAS_EQUIPMENTS", direction = Relationship.Direction.OUTGOING)
    private List<Equipment> equipment;
	
    // Getters and setters for all fields
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public List<Ingredient> getIngredients() {
		return ingredients;
	}
	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}
	public List<Equipment> getEquipment() {
		return equipment;
	}
	public void setEquipment(List<Equipment> equipment) {
		this.equipment = equipment;
	}
	public Length getLength() {
		return length;
	}
	public void setLength(Length length) {
		this.length = length;
	}
}
