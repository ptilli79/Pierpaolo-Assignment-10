package com.projects.cavany.domain.RecipeDetails.AnalyzedInstruction;

import java.util.List;
import java.util.UUID;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class AnalyzedInstruction {
	@Id
    private Long id;
	
	@Property
    private String name;
	
	@Relationship(type = "HAS_STEPS", direction = Relationship.Direction.OUTGOING)
    private List<Step> steps;
	
    // Constructor that takes a AnalyzedInstruction object and assigns its id to Nutrition's id
	public AnalyzedInstruction(Long id) {
		this.id=id;
	}
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
	public List<Step> getSteps() {
		return steps;
	}
	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

}
